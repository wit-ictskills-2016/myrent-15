package org.wit.myrent.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.wit.myrent.activities.ResidenceListFragment;
import org.wit.myrent.app.MyRentApp;
import org.wit.myrent.models.Residence;

import java.io.IOException;
import java.util.List;

import retrofit.Call;
import retrofit.Response;

public class RefreshService extends IntentService
{
  private String tag = "MyRent";
  MyRentApp app;
  public RefreshService()
  {
    super("RefreshService");
    app = MyRentApp.getApp();
  }

  @Override
  protected void onHandleIntent(Intent intent)
  {
    Intent localIntent = new Intent(ResidenceListFragment.BROADCAST_ACTION);
    Call<List<Residence>> call = (Call<List<Residence>>) app.residenceService.getResidences();
    try
    {
      Response<List<Residence>> response = call.execute();
      app.portfolio.refreshResidences(response.body());
      LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }
    catch (IOException e)
    {

    }
  }

  @Override
  public void onDestroy()
  {
    super.onDestroy();
    Log.i(tag, "RefreshService instance destroyed");
  }

}