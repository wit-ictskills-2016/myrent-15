package org.wit.myrent;

import java.util.Random;

/**
 * Created by jfitzgerald on 05/08/2016.
 */
public class Residence
{
  private Long id;

  //a latitude longitude pair
  //example "52.4566,-6.5444"
  private String geolocation;

  public Residence()
  {
    id = new Random().nextLong();
  }

  public void setGeolocation(String geolocation)
  {
    this.geolocation = geolocation;
  }

  public String getGeolocation()
  {
    return geolocation;
  }
}