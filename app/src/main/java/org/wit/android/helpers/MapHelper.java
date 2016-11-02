package org.wit.android.helpers;
import android.util.Log;


import com.mapbox.mapboxsdk.geometry.LatLng;

import java.lang.NumberFormatException;


public class MapHelper
{

  /**
   * Parses a string containing latitude and longitude.
   * @param geolocation The string obtained by concatenating comma separated latitude and longitude
   * @return The latitude component
   */
  public static double latitude(String geolocation) {
    String[] g = geolocation.split(",");
    try {
      if (g.length == 2) {
        return Double.parseDouble(g[0]);
      }
    }
    catch (NumberFormatException e) {
      Log.d("MapHelper", "Number format exception: invalid latitude: " + e.getMessage());
    }
    return 0.0;

  }

  /**
   * Parses a string containing latitude and longitude.
   * @param geolocation The string obtained by concatenating comma separated latitude and longitude
   * @return The longitude component
   */
  public static double longitude(String geolocation) {
    String[] g = geolocation.split(",");
    try {
      if (g.length == 2) {
        return Double.parseDouble(g[1]);
      }
    }
    catch (NumberFormatException e) {
      Log.d("MapHelper", "Number format exception: invalid longitude: " + e.getMessage());
    }
    return 0.0;

  }

  /**
   *
   * @param geo A Mapbox LatLng object representing geolocation
   * @return String Returns concatenated latitude and longitude.
   */
  public static String latLng(LatLng geo) {

    return String.format("%.6f", geo.getLatitude()) + ", " + String.format("%.6f", geo.getLongitude());
  }

}