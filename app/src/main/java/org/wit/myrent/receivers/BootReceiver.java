package org.wit.myrent.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.wit.myrent.R;
import org.wit.myrent.services.RefreshService;

/**
 * Method 1: If permission set and BootReceiver registered in manifest file then
 * BootReceiver.onReceive method will be invoked by system when device started.
 * Method 2: Create bespoke intent filter action in BootReceiver receiver in manifest and 
 * broadcast an intent from MyApp with this action as parameter. This is approach used
 * in our code for easier debugging into BootReceiver.
 * In this onReceive method we set the interval at which the alarm should trigger.
 * This will be either a default value or a value input by user in preference settings.
 * Note that the system units are milliseconds. Even AlarmManager.INTERVAL_FIFTEEN_MINUTES
 * resolves to milliseconds. The user input frequency units is minutes.
 * Debugging has proved impossible using BOOT_COMPLETED action (see manifest).
 * The approach adopted is to sent an intent from the app and again from the setting if
 * the user changes the refresh frequency.
 */
public class BootReceiver extends BroadcastReceiver
{
  private final int NUMBER_MILLIS_PER_MINUTE = 60000;
  private final int ONE_MINUTE = 60000;
  private static final long DEFAULT_INTERVAL = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
  public static int REQUESTCODE = -1;
  private String tag = "org.wit.myrent";

  @Override
  public void onReceive(Context context, Intent intent)
  {
    Log.d(tag, "In BootReceiver.onReceive");

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    // The settings key
    String key = context.getResources().getString(R.string.refresh_interval_preference_key);
    Log.d(tag, "Settings refresh interval key " + key);
    long interval = DEFAULT_INTERVAL; // 15 minutes but units are millis so this is Constant Value: 900000 (0x00000000000dbba0)
    Log.d(tag,"DEFAULT_INTERVAL " + (interval/NUMBER_MILLIS_PER_MINUTE) + " minute(s)");
    // Use default interval if fail to retrieve valid settings input interval
    String value = prefs.getString(key, Long.toString(DEFAULT_INTERVAL/NUMBER_MILLIS_PER_MINUTE));
    Log.d(tag, "Settings refresh interval value (user-input) " + value + " minute(s)");
    if (NumUtil.isPositiveNumber(value))
    {
      interval = Long.parseLong(value) * NUMBER_MILLIS_PER_MINUTE; // minutes to millis
      Log.d(tag,"Using refresh interval obtained from settings " + interval);
    }
    // Set an arbitrary minimum interval value of a minute to avoid overloading service.
    interval = interval < ONE_MINUTE ? ONE_MINUTE : interval;

    // Prepare an PendingIntent with a view to triggering RefreshService
    PendingIntent operation = PendingIntent.getService(
        context,
        REQUESTCODE,
        new Intent(context, RefreshService.class),
        PendingIntent.FLAG_UPDATE_CURRENT
    );

    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    alarmManager.cancel(operation);//cancel any existing alarms with matching intent
    alarmManager.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis(), interval, operation);

    Log.d(tag, "BootReceiver alarm repeats every: " + (interval/NUMBER_MILLIS_PER_MINUTE) + " minute(s).");
  }
}

/**
 * Class containing single method to validate a string resolves to an integer
 */
class NumUtil
{

  /**
   * Verifies that a string parses to a positive number
   * @param str The string to be verified
   * @return If string comprises digits 0 to 9 only, returns true, else false.
   */
  public static boolean isPositiveNumber(String str)
  {
    // check for empty string
    if (str.compareTo("") == 0)
      return false;

    // if any non-digit char found return false
    for (char c : str.toCharArray())
    {
      if (!Character.isDigit(c))
        return false;
    }
    return true;
  }

}
