package org.wit.myrent.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import org.wit.myrent.R;

import static org.wit.android.helpers.IntentHelper.navigateUp;

import org.wit.android.helpers.MapHelper;
import org.wit.myrent.app.MyRentApp;
import org.wit.myrent.models.Residence;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,
    GoogleMap.OnMarkerDragListener {

  /*
 * We use the current residence when navigating back to parent class - ResidenceFragment as
 * this is required in ResidenceFragment onCreate. The navigateUp
 */
  Long resId;

  Residence residence; // The residence associated with this map pane
  MyRentApp app;
  GoogleMap map;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_map);

    MapFragment mapFragment = (MapFragment) getFragmentManager()
        .findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);

    resId = (Long)getIntent().getSerializableExtra(ResidenceFragment.EXTRA_RESIDENCE_ID);

    app = (MyRentApp)getApplication();
    residence = app.portfolio.getResidence(resId);

  }

  @Override
  public void onMapReady(GoogleMap map) {
    this.map = map;
    LatLng sydney = new LatLng(-33.867, 151.206);

    map.addMarker(new MarkerOptions()
        .title("Sydney")
        .snippet("The most populous city in Australia.")
        .position(sydney));

    map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));

    MarkerOptions options = new MarkerOptions()
        .title("Sydney")
        .snippet("The most populous city in Australia.")
        .draggable(true)
        .position(sydney);

    map.addMarker(options);
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

  @Override
  public void onMarkerDragStart(Marker marker) {

  }

  @Override
  public void onMarkerDrag(Marker marker) {

  }

  /**
   * When marker drag ends, save Residence model geolocation and zoom.
   * @param marker The map marker representing current residence geolocation.
   */
  @Override
  public void onMarkerDragEnd(Marker marker) {
    residence.geolocation = MapHelper.latLng(marker.getPosition());
    residence.zoom = map.getCameraPosition().zoom;
    map.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
  }
}