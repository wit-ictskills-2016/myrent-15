package org.wit.myrent.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import org.wit.android.helpers.MapHelper;
import org.wit.myrent.R;
import org.wit.myrent.app.MyRentApp;
import org.wit.myrent.models.Residence;

import static org.wit.android.helpers.IntentHelper.navigateUp;

public class MapBoxActivity extends AppCompatActivity implements
    OnMapReadyCallback,
    MapboxMap.OnMarkerClickListener,
    MapboxMap.OnMapLongClickListener
{

  private MapView mapView;
  private MapboxMap mapboxMap;
  private Marker residenceMarker;
  /*
* We use the current residence when navigating back to parent class - ResidenceFragment as
* this is required in ResidenceFragment onCreate. The navigateUp
*/
  Long resId; // The id of the residence associate with this map pane
  Residence residence; // The residence associated with this map pane
  LatLng residenceLatLng;
  MyRentApp app;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    resId = (Long) getIntent().getSerializableExtra(ResidenceFragment.EXTRA_RESIDENCE_ID);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    app = (MyRentApp) getApplication();
    residence = app.portfolio.getResidence(resId);
    if (residence != null) {
      residenceLatLng = new LatLng(MapHelper.latitude(residence.geolocation),
          MapHelper.longitude(residence.geolocation));
    }
    // Mapbox access token only needs to be configured once in your app
    MapboxAccountManager.start(this, "pk.eyJ1IjoidXNwbGl0dSIsImEiOiI3Mm90ZXR3In0.-XsJBar8tOXqlLjyrXX01Q");

    // This contains the MapView in XML and needs to be called after the account manager
    setContentView(R.layout.activity_mapbox);

    mapView = (MapView) findViewById(R.id.mapView);
    mapView.onCreate(savedInstanceState);
    mapView.getMapAsync(this);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch (item.getItemId())
    {
      case android.R.id.home:
        navigateUp(this, ResidenceFragment.EXTRA_RESIDENCE_ID, resId);
        return true;

      default: return super.onOptionsItemSelected(item);
    }
  }

  // OnMapReadyCallback interface method impl
  @Override
  public void onMapReady(MapboxMap mapboxMap) {
    this.mapboxMap = mapboxMap;
    positionCamera();
    setMarker();
    mapboxMap.getUiSettings().setZoomControlsEnabled(true);
    mapboxMap.getUiSettings().setZoomGesturesEnabled(true);
    mapboxMap.setOnMarkerClickListener(this);
    mapboxMap.setOnMapLongClickListener(this);

  }

  private void setMarker() {

    MarkerViewOptions marker = new MarkerViewOptions().position(residenceLatLng);
    residenceMarker = mapboxMap.addMarker(marker);
  }

  private void positionCamera() {
    CameraPosition position = new CameraPosition.Builder()
        .target(residenceLatLng) // Sets the new camera position
        .zoom(residence.zoom)
        .build(); // Creates a CameraPosition from the builder

    mapboxMap.animateCamera(CameraUpdateFactory
        .newCameraPosition(position));

  }

  // Add the mapView lifecycle to the activity's lifecycle methods
  @Override
  public void onResume() {
    super.onResume();
    mapView.onResume();
  }

  @Override
  public void onPause() {
    super.onPause();
    mapView.onPause();
    residence.zoom = mapboxMap.getCameraPosition().zoom;
    residence.geolocation = MapHelper.latLng(residenceMarker.getPosition());
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
    mapView.onLowMemory();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mapView.onDestroy();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    mapView.onSaveInstanceState(outState);
  }

  // OnMarkerClickListener
  @Override
  public boolean onMarkerClick(@NonNull Marker marker) {
    String snippet = "GPS : " + residence.geolocation;
    marker.setSnippet(snippet);
    return false;
  }


  /**
   * Long click moves marker to clicked position and updates
   * Residence object's geolocation to new marker position.
   * @param point
   */
  // OnMapLonClickListener
  @Override
  public void onMapLongClick(@NonNull LatLng point) {
    residenceMarker.setPosition(point);
  }

}