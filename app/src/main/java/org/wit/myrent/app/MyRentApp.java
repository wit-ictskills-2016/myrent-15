package org.wit.myrent.app;

import android.app.Application;
import android.util.Log;
import org.wit.myrent.models.Portfolio;


public class MyRentApp extends Application
{
  static final String TAG = "MyRentApp";
  public Portfolio portfolio;

  protected static MyRentApp app;
  @Override
  public void onCreate()
  {
    super.onCreate();
    portfolio = new Portfolio(getApplicationContext());
    Log.d(TAG, "MyRent app launched");
    app = this;
  }

  public static MyRentApp getApp(){
    return app;
  }
}