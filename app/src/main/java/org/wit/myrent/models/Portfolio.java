package org.wit.myrent.models;
import java.util.ArrayList;


import static org.wit.android.helpers.LogHelpers.info;

public class Portfolio
{
  public ArrayList<Residence> residences;

  public Portfolio() {
    residences = new ArrayList<Residence>();
  }

  public void addResidence(Residence residence) {
    residences.add(residence);
  }

  public Residence getResidence(Long id) {
   info(this, "Long parameter id: " + id);

    for (Residence res : residences) {
      if (id.equals(res.id)) {
        return res;
      }
    }
    return null;
  }

}