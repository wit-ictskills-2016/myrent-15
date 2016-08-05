package org.wit.myrent.models;

import java.util.Date;
import java.util.Random;

/**
 * Created by jfitzgerald on 05/08/2016.
 */
public class Residence
{
  public Long id;
  public Long date;

  //a latitude longitude pair
  //example "52.4566,-6.5444"
  public String geolocation;
  public boolean rented;

  public Residence()
  {
    id = new Random().nextLong();
    date = new Date().getTime();
    geolocation = "52.253456,-7.187162";
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