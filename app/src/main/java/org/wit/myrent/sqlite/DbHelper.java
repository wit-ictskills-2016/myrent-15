package org.wit.myrent.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.wit.myrent.models.Residence;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DbHelper extends SQLiteOpenHelper
{
  static final String TAG = "DbHelper";
  static final String DATABASE_NAME = "residences.db";
  static final int DATABASE_VERSION = 1;
  static final String TABLE_RESIDENCES = "tableResidences";

  static final String PRIMARY_KEY = "id";
  static final String GEOLOCATION = "geolocation";
  static final String DATE = "date";
  static final String RENTED = "rented";
  static final String TENANT = "tenant";
  static final String ZOOM = "zoom";
  static final String PHOTO = "photo";

  Context context;

  public DbHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
    this.context = context;
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    String createTable =
        "CREATE TABLE tableResidences " +
            "(id text primary key, " +
            "geolocation text," +
            "date text," +
            "rented text," +
            "tenant text," +
            "zoom text," +
            "photo text)";

    db.execSQL(createTable);
    Log.d(TAG, "DbHelper.onCreated: " + createTable);
  }

  /**
   * @param residence Reference to Residence object to be added to database
   */
  public void addResidence(Residence residence) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(PRIMARY_KEY, residence.id);
    values.put(GEOLOCATION, residence.geolocation);
    values.put(DATE, residence.date);
    values.put(RENTED, residence.rented == true ? "yes" : "no");
    values.put(TENANT, residence.tenant);
    values.put(ZOOM, Double.toString(residence.zoom));
    values.put(PHOTO, residence.photo);

    // Insert record
    db.insert(TABLE_RESIDENCES, null, values);
    db.close();
  }

  /**
   * Persist a list of residences
   *
   * @param residences The list of Residence object to be saved to database.
   */
  public void addResidences(List<Residence> residences) {
    for (Residence residence : residences) {
      addResidence(residence);
    }
  }

  public Residence selectResidence(UUID resId) {
    Residence residence;
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = null;

    try {
      residence = new Residence();

      cursor = db.rawQuery("SELECT * FROM tableResidences WHERE uuid = ?", new String[]{resId.toString() + ""});

      if (cursor.getCount() > 0) {
        int columnIndex = 0;
        cursor.moveToFirst();
        residence.id = cursor.getLong(columnIndex++);
        residence.geolocation = cursor.getString(columnIndex++);
        residence.date = Long.parseLong(cursor.getString(columnIndex++));
        residence.rented = cursor.getString(columnIndex++).equalsIgnoreCase("yes") ? true : false;
        residence.tenant = cursor.getString(columnIndex++);
        residence.zoom = Double.parseDouble(cursor.getString(columnIndex++));
        residence.photo = cursor.getString(columnIndex++);
      }
    }
    finally {
      cursor.close();
    }
    return residence;
  }

  public void deleteResidence(Residence residence) {
    SQLiteDatabase db = this.getWritableDatabase();
    try {
      db.delete("tableResidences", "id" + "=?", new String[]{residence.id + ""});
    }
    catch (Exception e) {
      Log.d(TAG, "delete residence failure: " + e.getMessage());
    }
  }

  /**
   * Query database and select entire tableResidences.
   *
   * @return A list of Residence object records
   */
  public List<Residence> selectResidences() {
    List<Residence> residences = new ArrayList<Residence>();
    String query = "SELECT * FROM " + "tableResidences";
    SQLiteDatabase db = this.getWritableDatabase();
    Cursor cursor = db.rawQuery(query, null);
    if (cursor.moveToFirst()) {
      int columnIndex = 0;
      do {
        Residence residence = new Residence();
        residence.id = cursor.getLong(columnIndex++);
        residence.geolocation = cursor.getString(columnIndex++);
        residence.date = Long.parseLong(cursor.getString(columnIndex++));
        residence.rented = cursor.getString(columnIndex++).equalsIgnoreCase("yes") ? true : false;
        residence.tenant = cursor.getString(columnIndex++);
        residence.zoom = Double.parseDouble(cursor.getString(columnIndex++));
        residence.photo = cursor.getString(columnIndex++);
        columnIndex = 0;

        residences.add(residence);
      } while (cursor.moveToNext());
    }
    cursor.close();
    return residences;
  }

  /**
   * Delete all records
   */
  public void deleteResidences() {
    SQLiteDatabase db = this.getWritableDatabase();
    try {
      db.execSQL("delete from tableResidences");
    }
    catch (Exception e) {
      Log.d(TAG, "delete residences failure: " + e.getMessage());
    }
  }

  /**
   * Queries the database for the number of records.
   *
   * @return The number of records in the dataabase.
   */
  public long getCount() {
    SQLiteDatabase db = this.getReadableDatabase();
    long numberRecords = DatabaseUtils.queryNumEntries(db, TABLE_RESIDENCES);
    db.close();
    return numberRecords;
  }

  /**
   * Update an existing Residence record.
   * All fields except record id updated.
   *
   * @param residence The Residence record being updated.
   */
  public void updateResidence(Residence residence) {
    SQLiteDatabase db = this.getWritableDatabase();
    try {
      ContentValues values = new ContentValues();
      values.put(GEOLOCATION, residence.geolocation);
      values.put(DATE, residence.date);
      values.put(RENTED, residence.rented == true ? "yes" : "no");
      values.put(TENANT, residence.tenant);
      values.put(ZOOM, Double.toString(residence.zoom));
      values.put(PHOTO, residence.photo);
      db.update("tableResidences", values, "id" + "=?", new String[]{residence.id + ""});
    }
    catch (Exception e) {
      Log.d(TAG, "update residences failure: " + e.getMessage());
    }
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("drop table if exists " + TABLE_RESIDENCES);
    Log.d(TAG, "onUpdated");
    onCreate(db);
  }
}