package org.wit.myrent.app;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import org.wit.myrent.models.Portfolio;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.wit.myrent.retrofit.ResidenceServiceProxy;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

public class MyRentApp extends Application
{
  static final String TAG = "MyRent";
  //public String service_url = "http://10.0.2.2:9000"; // Android Emulator
  //public String service_url = "http://10.0.3.2:9000"; // Genymotion
  public String service_url = "https://myrent-service-2016.herokuapp.com";

  public Portfolio portfolio;
  protected static MyRentApp app;
  public ResidenceServiceProxy residenceService;

  @Override
  public void onCreate() {
    super.onCreate();
    portfolio = new Portfolio(getApplicationContext());
    Log.d(TAG, "MyRent app launched");
    app = this;

    Gson gson = new GsonBuilder().create();
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(service_url)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build();

    residenceService = retrofit.create(ResidenceServiceProxy.class);

    // This broadcast is designed to start the RefreshService with
    // a view to sync the data cache with the remote server.
    sendBroadcast(new Intent("org.wit.myrent.receivers.SEND_BROADCAST"));

  }

  public static MyRentApp getApp() {
    return app;
  }
}