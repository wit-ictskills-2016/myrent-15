package org.wit.myrent.models;

import android.content.Context;

import org.wit.myrent.R;

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
  public String tenant;
  public double  zoom       ;//zoom level of accompanying map
  public String photo;

  public Residence() {
    id = unsignedLong();
    date = new Date().getTime();
    geolocation = "52.253456,-7.187162";
    tenant = "none presently";
    zoom = 16.0f;
    photo = "photo";
  }

  public void setGeolocation(String geolocation) {
    this.geolocation = geolocation;
  }

  public String getGeolocation() {
    return geolocation;
  }

  public String getDateString() {
    return "Registered:" + dateString();
  }

  private String dateString() {
    String dateFormat = "EEE d MMM yyyy H:mm";
    return android.text.format.DateFormat.format(dateFormat, date).toString();
  }

  public String getResidenceReport(Context context) {
    String rentedString = "";
    if (rented) {
      rentedString = context.getString(R.string.residence_report_rented);
    }
    else {
      rentedString = context.getString(R.string.residence_report_not_rented);
    }

    String prospectiveTenant = tenant;
    if (tenant == null) {
      prospectiveTenant = context.getString(R.string.residence_report_nobody_interested);
    }
    else {
      prospectiveTenant = context.getString(R.string.residence_report_prospective_tenant, tenant);
    }
    String report = "Location " + geolocation + " Date: " + dateString() + " " + rentedString + " " + prospectiveTenant;
    return report;

  }

  /**
   * Generate a long greater than zero
   * @return Unsigned Long value greater than zero
   */
  private Long unsignedLong() {
    long rndVal = 0;
    do {
      rndVal = new Random().nextLong();
    } while (rndVal <= 0);
    return rndVal;
  }
}