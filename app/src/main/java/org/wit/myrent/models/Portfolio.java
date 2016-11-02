package org.wit.myrent.models;

import static org.wit.android.helpers.LogHelpers.info;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import org.wit.myrent.sqlite.DbHelper;

public class Portfolio
{
  public ArrayList<Residence> residences;
  public DbHelper dbHelper;

  public Portfolio(Context context) {
    try {
      dbHelper = new DbHelper(context);
      residences = (ArrayList<Residence>) dbHelper.selectResidences();
    }
    catch (Exception e) {
      info(this, "Error loading residences: " + e.getMessage());
      residences = new ArrayList<Residence>();
    }
  }

  /**
   * Obtain the entire database of residences
   *
   * @return All the residences in the database as an ArrayList
   */
  public ArrayList<Residence> selectResidences() {
    return (ArrayList<Residence>) dbHelper.selectResidences();
  }

  /**
   * Add incoming residence to both local and database storage
   *
   * @param residence The residence object to be added to local and database storage.
   */
  public void addResidence(Residence residence) {
    residences.add(residence);
    dbHelper.addResidence(residence);
  }

  /**
   * Obtain specified residence from local list and return.
   *
   * @param id The Long id identifier of the residence sought.
   * @return The specified residence if it exists.
   */
  public Residence getResidence(Long id) {
    Log.i(this.getClass().getSimpleName(), "Long id id: " + id);

    for (Residence res : residences) {
      if (id.equals(res.id)) {
        return res;
      }
    }
    info(this, "failed to find residence. returning first element array to avoid crash");
    return null;
  }

  /**
   * Delete Residence object from local and remote storage
   *
   * @param residence Residence object for deletion.
   */
  public void deleteResidence(Residence residence) {
    dbHelper.deleteResidence(residence);
    residences.remove(residence);
  }

  public void updateResidence(Residence residence) {
    dbHelper.updateResidence(residence);
    updateLocalResidences(residence);

  }

  /**
   * Clear local and sqlite residences and refresh with incoming list.
   * @param residences List residence objects
   */
  public void refreshResidences(List<Residence> residences)
  {
    dbHelper.deleteResidences();
    this.residences.clear();

    dbHelper.addResidences(residences);

    for (int i = 0; i < residences.size(); i += 1) {
      this.residences.add(residences.get(i));
    }
  }

  /**
   * Search the list of residences for argument residence
   * If found replace it with argument residence.
   * If not found just add the argument residence.
   *
   * @param residence The Residence object with which the list of Residences to be updated.
   */
  private void updateLocalResidences(Residence residence) {
    for (int i = 0; i < residences.size(); i += 1) {
      Residence r = residences.get(i);
      if (r.id.equals(residence.id)) {
        residences.remove(i);
        residences.add(residence);
        return;
      }
    }
  }
}