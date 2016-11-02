package org.wit.myrent.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;

import org.wit.myrent.R;
import org.wit.myrent.app.MyRentApp;
import org.wit.myrent.models.Portfolio;
import org.wit.myrent.models.Residence;

import static org.wit.android.helpers.CameraHelper.showPhoto;

public class ResidenceGalleryActivity extends AppCompatActivity
{

  public static   final String  EXTRA_PHOTO_FILENAME = "org.wit.myrent.photo.filename";
  private ImageView photoView;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.residence_gallery);
    photoView = (ImageView) findViewById(R.id.residenceGalleryImage);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    showPicture();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch (item.getItemId())
    {
      case android.R.id.home  : onBackPressed();
        return true;
      default                 : return super.onOptionsItemSelected(item);
    }
  }

  private void showPicture()
  {
    Long resId = (Long)getIntent().getSerializableExtra(ResidenceFragment.EXTRA_RESIDENCE_ID);
    MyRentApp app = (MyRentApp) getApplication();
    Portfolio portfolio = app.portfolio;
    Residence residence = portfolio.getResidence(resId);
    showPhoto(this, residence,  photoView);
  }
}