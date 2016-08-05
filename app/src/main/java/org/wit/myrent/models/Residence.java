package org.wit.myrent.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Random;


public class Residence
{
  public Long id;
  public Long date;

  //a latitude longitude pair
  //example "52.4566,-6.5444"
  public String geolocation;
  public boolean rented;

  private static final String JSON_ID             = "id"            ;
  private static final String JSON_GEOLOCATION    = "geolocation"   ;
  private static final String JSON_DATE           = "date"          ;
  private static final String JSON_RENTED         = "rented"        ;

  public Residence()
  {
    id = new Random().nextLong();
    date = new Date().getTime();
    geolocation = "52.253456,-7.187162";
  }

  public Residence(JSONObject json) throws JSONException
  {
    id            = json.getLong(JSON_ID);
    geolocation   = json.getString(JSON_GEOLOCATION);
    date          = json.getLong(JSON_DATE);
    rented        = json.getBoolean(JSON_RENTED);
  }

  public JSONObject toJSON() throws JSONException
  {
    JSONObject json = new JSONObject();
    json.put(JSON_ID            , Long.toString(id));
    json.put(JSON_GEOLOCATION   , geolocation);
    json.put(JSON_DATE          , date);
    json.put(JSON_RENTED        , rented);
    return json;
  }

  public void setGeolocation(String geolocation)
  {
    this.geolocation = geolocation;
  }

  public String getGeolocation()
  {
    return geolocation;
  }

  public String getDateString() {
    return "Registered:" + dateString();
  }

  private String dateString() {
    String dateFormat = "EEE d MMM yyyy H:mm";
    return android.text.format.DateFormat.format(dateFormat, date).toString();
  }

}