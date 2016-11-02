package org.wit.myrent.activities;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.wit.android.helpers.ContactHelper;
import org.wit.myrent.R;
import org.wit.myrent.app.MyRentApp;
import org.wit.myrent.models.Portfolio;
import org.wit.myrent.models.Residence;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentCompat;
import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.app.DatePickerDialog;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import static org.wit.android.helpers.ContactHelper.sendEmail;
import static org.wit.android.helpers.IntentHelper.navigateUp;
import static org.wit.android.helpers.IntentHelper.startActivityWithData;
import android.support.design.widget.FloatingActionButton;
import android.widget.Toast;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import static org.wit.android.helpers.CameraHelper.showPhoto;
import android.widget.ImageView;

public class ResidenceFragment extends Fragment implements TextWatcher,
    OnCheckedChangeListener,
    OnClickListener,
    DatePickerDialog.OnDateSetListener,
    Callback<Residence>,
    View.OnLongClickListener
{
  static final String TAG = "MyRent";
  public static final String EXTRA_RESIDENCE_ID = "myrent.RESIDENCE_ID";

  private static final int REQUEST_CONTACT = 1;
  private static final int REQUEST_PHOTO = 0;

  private EditText geolocation;
  private CheckBox rented;
  private Button dateButton;
  private Button tenantButton;
  private Button reportButton;

  private Residence residence;
  private Portfolio portfolio;

  private String emailAddress;

  MyRentApp app;

  Intent requestContactIntent;

  private ImageView cameraButton;
  private ImageView photoView;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);

    Long resId = (Long) getActivity().getIntent().getSerializableExtra(EXTRA_RESIDENCE_ID);

    app = MyRentApp.getApp();
    portfolio = app.portfolio;
    residence = portfolio.getResidence(resId);

  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
    super.onCreateView(inflater, parent, savedInstanceState);
    View v = inflater.inflate(R.layout.fragment_residence, parent, false);

    addListeners(v);
    updateControls(residence);

    FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
    fab.setOnClickListener(this);

    return v;
  }

  private void addListeners(View v) {
    geolocation = (EditText) v.findViewById(R.id.geolocation);
    dateButton = (Button) v.findViewById(R.id.registration_date);
    rented = (CheckBox) v.findViewById(R.id.isrented);
    tenantButton = (Button) v.findViewById(R.id.tenant);
    reportButton = (Button) v.findViewById(R.id.residence_reportButton);


    geolocation.addTextChangedListener(this);
    dateButton.setOnClickListener(this);
    rented.setOnCheckedChangeListener(this);
    tenantButton.setOnClickListener(this);
    reportButton.setOnClickListener(this);

    cameraButton  = (ImageView) v.findViewById(R.id.camera_button);
    photoView     = (ImageView) v.findViewById(R.id.myrent_imageView);

  }

  public void updateControls(Residence residence) {
    geolocation.setText(residence.geolocation);
    rented.setChecked(residence.rented);
    dateButton.setText(residence.getDateString());

    cameraButton.setOnClickListener(this);
    photoView.setOnLongClickListener(this);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        navigateUp(getActivity());
        return true;

      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    updateResidence(residence);
    portfolio.updateResidence(residence);
  }


  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode != Activity.RESULT_OK) {
      return;
    }

    switch (requestCode) {
      case REQUEST_CONTACT:
        requestContactIntent = data;
        checkContactsReadPermission();
        break;

      case REQUEST_PHOTO:
        String filename = data.getStringExtra(ResidenceCameraActivity.EXTRA_PHOTO_FILENAME);
        if (filename != null)
        {
          residence.photo = filename;
          showPhoto(getActivity(), residence, photoView );
        }
        break;
    }
  }

  private void readContact() {
    String name = ContactHelper.getContact(getActivity(), requestContactIntent);
    emailAddress = ContactHelper.getEmail(getActivity(), requestContactIntent);
    tenantButton.setText(name + " : " + emailAddress);
    residence.tenant = name;
  }

  @Override
  public void beforeTextChanged(CharSequence s, int start, int count, int after) {
  }

  @Override
  public void onTextChanged(CharSequence s, int start, int before, int count) {
  }

  @Override
  public void afterTextChanged(Editable c) {
    Log.i(this.getClass().getSimpleName(), "geolocation " + c.toString());
    residence.geolocation = c.toString();
  }

  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    residence.rented = isChecked;
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.registration_date:
        Calendar c = Calendar.getInstance();
        DatePickerDialog dpd = new DatePickerDialog(getActivity(), this, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dpd.show();
        break;

      case R.id.tenant:
        Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(i, REQUEST_CONTACT);
        break;

      case R.id.residence_reportButton:
        if (emailAddress == null) {
          emailAddress = ""; // guard against null pointer
        }
        sendEmail(getActivity(), emailAddress, getString(R.string.residence_report_subject), residence.getResidenceReport(getActivity()));
        break;

      case R.id.fab:
        startActivityWithData(getActivity(), MapBoxActivity.class, EXTRA_RESIDENCE_ID, residence.id);
        break;

      case R.id.camera_button:
        Intent ic = new Intent(getActivity(), ResidenceCameraActivity.class);
        startActivityForResult(ic, REQUEST_PHOTO);
    }
  }

  @Override
  public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
    Date date = new GregorianCalendar(year, monthOfYear, dayOfMonth).getTime();
    residence.date = date.getTime();
    dateButton.setText(residence.getDateString());
  }

  /**
   * http://stackoverflow.com/questions/32714787/android-m-permissions-onrequestpermissionsresult-not-being-called
   * This is an override of FragmentCompat.onRequestPermissionsResult
   *
   * @param requestCode Example REQUEST_CONTACT
   * @param permissions String array of permissions requested.
   * @param grantResults int array of results for permissions request.
   */
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    switch (requestCode) {
      case REQUEST_CONTACT: {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

          readContact();
        }
        break;
      }
    }
  }

  /**
   * Bespoke method to check if read contacts permission exists.
   * If it exists then the contact sought is read.
   * Otherwise, the method FragmentCompat.request permissions is invoked and
   * The response is via the callback onRequestPermissionsResult.
   * In onRequestPermissionsResult, on successfully being granted permission then the sought contact is read.
   */
  private void checkContactsReadPermission() {
    if (ContextCompat.checkSelfPermission(getActivity(),
        Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {

      readContact();
    }
    else {
      // Invoke callback to request user-granted permission
      FragmentCompat.requestPermissions(
          this,
          new String[]{Manifest.permission.READ_CONTACTS},
          REQUEST_CONTACT);
    }
  }

  /* ============================ Retrofit =======================================*/
  public void updateResidence(Residence res) {
    Call<Residence> call = app.residenceService.updateResidence(res);
    call.enqueue(this);
  }

  @Override
  public void onResponse(Response<Residence> response, Retrofit retrofit) {
    Residence returnedResidence = response.body();
    try {
      if (returnedResidence != null) {
        Toast.makeText(getActivity(), "Residence updated successfully", Toast.LENGTH_SHORT).show();
      }
      else {
        Toast.makeText(getActivity(), "Update failed. Residence null returned due to incorrectly configured client", Toast.LENGTH_SHORT).show();

      }
    } catch(Exception e) {
      Log.d(TAG, e.getMessage());
    }
  }


  @Override
  public void onFailure(Throwable t) {
    Log.d(TAG,"Failed to update residence due to unknown network issue");

  }

  // Camera feature
  @Override
  public void onStart()
  {
    super.onStart();
    //display thumbnail photo
    showPhoto(getActivity(), residence, photoView);
  }

  /**
   * Long press the thumbnail bitmap image to view photo in single-photo gallery
   */
  @Override
  public boolean onLongClick(View v)
  {
    Intent i = new Intent(getActivity(), ResidenceGalleryActivity.class);
    i.putExtra(EXTRA_RESIDENCE_ID, residence.id);
    startActivity(i);
    return true;
  }
}
