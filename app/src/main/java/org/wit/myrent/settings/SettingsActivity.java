package org.wit.myrent.settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import org.wit.myrent.R;
import org.wit.myrent.settings.SettingsFragment;


public class SettingsActivity extends AppCompatActivity
{

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    if (savedInstanceState == null)
    {
      SettingsFragment fragment = new SettingsFragment();
      getFragmentManager().beginTransaction().add(android.R.id.content, fragment, fragment.getClass().getSimpleName())
          .commit();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    getMenuInflater().inflate(R.menu.residencelist, menu);
    return true;
  }
}