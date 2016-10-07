package org.wit.myrent.settings;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import org.wit.myrent.R;

import static org.wit.android.helpers.IntentHelper.navigateUp;
import static org.wit.android.helpers.LogHelpers.info;


public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener
{
  private SharedPreferences prefs;

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.settings);
    setHasOptionsMenu(true);

  }

  @Override
  public void onStart()
  {
    super.onStart();
    prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
    prefs.registerOnSharedPreferenceChangeListener(this);
  }

  @Override
  public void onStop()
  {
    super.onStop();
    prefs.unregisterOnSharedPreferenceChangeListener(this);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch (item.getItemId())
    {
      case android.R.id.home:
        navigateUp(getActivity());
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
  {
    info(getActivity(), "Setting change - key : value = " + key + " : " + sharedPreferences.getString(key, ""));
  }
}