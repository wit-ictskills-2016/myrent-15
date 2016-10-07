package org.wit.android.helpers;

import android.content.Context;
import com.google.android.gms.maps.model.LatLng;

/**
 * Class contains utility methos to:
 * parse a Residence geolocation string and return a GoogleMap LatLng object,
 * parse a GoogleMap LatLng object and return a Residence geolocation string.
 */
public class MapHelper
{
  /**
   *
   * @param context Presently redundant, referenct to the application context
   * @param geolocation The geolocation in Residence format, example: "42.122,-7.456"
   * @return The geolocation expressed as a Google LatLng object
   */
  public static LatLng latLng(Context context, String geolocation)
  {
    String[] g = geolocation.split(",");
    if (g.length == 2)
    {
      return new LatLng(Double.parseDouble(g[0]), Double.parseDouble(g[1]));
    }
    return new LatLng(0, 0);

  }

  /**
   * parse a GoogleMap LatLng object and return a Residence geolocation string.
   * example: "42.122,-7.456"
   * @param geo Google LatLng object representing a latitude, longitude pair
   * @return A latitude longitude pair in a format suitable for use in Residence class
   */
  public static String latLng(LatLng geo)
  {
    return String.format("%.6f", geo.latitude) + ", " + String.format("%.6f", geo.longitude);
  }

}