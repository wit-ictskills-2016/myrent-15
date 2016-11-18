package org.wit.myrent.activities;

import java.util.ArrayList;
import java.util.List;

import org.wit.android.helpers.IntentHelper;
import org.wit.myrent.R;
import org.wit.myrent.app.MyRentApp;
import org.wit.myrent.models.Portfolio;
import org.wit.myrent.models.Residence;
import org.wit.myrent.settings.SettingsActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.view.ActionMode;
import android.widget.AbsListView;
import android.widget.ListView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.CheckBox;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


public class ResidenceListFragment extends ListFragment implements
    OnItemClickListener,
    AbsListView.MultiChoiceModeListener,
    Callback<Residence>
{
  public static final String BROADCAST_ACTION = "org.wit.myrent.activities.ResidenceListFragment";

  private ArrayList<Residence> residences;
  private Portfolio portfolio;
  private ResidenceAdapter adapter;
  private ListView listView;
  MyRentApp app;
  private IntentFilter intentFilter;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
    getActivity().setTitle(R.string.app_name);

    app = MyRentApp.getApp();
    portfolio = app.portfolio;
    residences = portfolio.residences;

    adapter = new ResidenceAdapter(getActivity(), residences);
    setListAdapter(adapter);

  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
    View v = super.onCreateView(inflater, parent, savedInstanceState);

    listView = (ListView) v.findViewById(android.R.id.list);
    listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
    listView.setMultiChoiceModeListener(this);
    registerBroadcastReceiver();

    return v;
  }

  private void registerBroadcastReceiver()
  {
    intentFilter = new IntentFilter(BROADCAST_ACTION);
    ResponseReceiver responseReceiver = new ResponseReceiver();
    // Registers the ResponseReceiver and its intent filters
    LocalBroadcastManager.getInstance(getActivity()).registerReceiver(responseReceiver, intentFilter);
  }

  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
    Residence res = ((ResidenceAdapter) getListAdapter()).getItem(position);
    Intent i = new Intent(getActivity(), ResidencePagerActivity.class);
    i.putExtra(ResidenceFragment.EXTRA_RESIDENCE_ID, res.id);
    startActivityForResult(i, 0);
  }

  @Override
  public void onResume() {
    super.onResume();
    ((ResidenceAdapter) getListAdapter()).notifyDataSetChanged();
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.residencelist, menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_item_new_residence:
        Residence residence = new Residence();
        //portfolio.addResidence(residence);
        createResidence(residence);

//        Intent i = new Intent(getActivity(), ResidencePagerActivity.class);
//        i.putExtra(ResidenceFragment.EXTRA_RESIDENCE_ID, residence.id);
//        startActivityForResult(i, 0);
        return true;

      case R.id.action_refresh:
        retrieveResidences();
        return true;

      case R.id.action_settings:
        startActivity(new Intent(getActivity(), SettingsActivity.class));
        return true;

      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Residence residence = adapter.getItem(position);
    IntentHelper.startActivityWithData(getActivity(), ResidencePagerActivity.class, "RESIDENCE_ID", residence.id);
  }

  /* ************ MultiChoiceModeListener methods (begin) *********** */
  @Override
  public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
    MenuInflater inflater = actionMode.getMenuInflater();
    inflater.inflate(R.menu.residence_list_context, menu);
    return true;
  }

  @Override
  public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
    return false;
  }

  @Override
  public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
    switch (menuItem.getItemId()) {
      case R.id.menu_item_delete_residence:
        deleteResidence(actionMode);
        return true;
      default:
        return false;
    }

  }

  private void deleteResidence(ActionMode actionMode) {
    for (int i = adapter.getCount() - 1; i >= 0; i--) {
      if (listView.isItemChecked(i)) {
        Residence residence = adapter.getItem(i);
        portfolio.deleteResidence(residence);
        deleteResidence(residence.id);
      }
    }
    actionMode.finish();
    adapter.notifyDataSetChanged();
  }

  @Override
  public void onDestroyActionMode(ActionMode actionMode) {
  }

  @Override
  public void onItemCheckedStateChanged(ActionMode actionMode, int position, long id, boolean checked) {
  }

  /* ************ MultiChoiceModeListener methods (end) *********** */

  /* ************ Retrofit: Create Residence ************ */

  public void createResidence(Residence res) {
    Call<Residence> call = app.residenceService.createResidence(res);
    call.enqueue(this);
  }

  @Override
  public void onResponse(Response<Residence> response, Retrofit retrofit) {
    Residence res = response.body();
    if (res != null) {
      Toast.makeText(getActivity(), "Residence created successfully", Toast.LENGTH_SHORT).show();

      portfolio.addResidence(res);
      Intent i = new Intent(getActivity(), ResidencePagerActivity.class);
      i.putExtra(ResidenceFragment.EXTRA_RESIDENCE_ID, res.id);
      startActivityForResult(i, 0);
    }
    else {
      Toast.makeText(getActivity(), "Residence null returned due to incorrectly configured client", Toast.LENGTH_SHORT).show();

    }
  }

  @Override
  public void onFailure(Throwable t) {
    Toast.makeText(getActivity(), "Failed to create residence due to unknown network issue", Toast.LENGTH_SHORT).show();

  }
  /* ************ Retrofit: Delete Residence ************ */

  public void deleteResidence(Long id) {
    DeleteRemoteResidence delResidence = new DeleteRemoteResidence();
    Call<String> call = app.residenceService.deleteResidence(id);
    call.enqueue(delResidence);
  }
  class DeleteRemoteResidence implements Callback<String>
  {

    @Override
    public void onResponse(Response<String> response, Retrofit retrofit) {
      Toast.makeText(getActivity(), "Residence deleted", Toast.LENGTH_SHORT).show();
      adapter.notifyDataSetChanged();
    }

    @Override
    public void onFailure(Throwable t) {
      Toast.makeText(getActivity(), "Failed to delete Residence due to unknown network issue", Toast.LENGTH_SHORT).show();
    }
  }

  /* ************ Retrofit: Refresh Residence list ************ */

  public void retrieveResidences() {
    RetrieveResidences retrieveResidences = new RetrieveResidences();
    Call<List<Residence>> call = app.residenceService.getResidences();
    call.enqueue(retrieveResidences);
  }

  class RetrieveResidences implements Callback<List<Residence>>
  {
    @Override
    public void onResponse(Response<List<Residence>> response, Retrofit retrofit) {
      List<Residence> listRes = response.body();
      Toast.makeText(getActivity(), "Retrieved " + listRes.size() + " residences", Toast.LENGTH_SHORT).show();
      portfolio.refreshResidences(listRes);
      ((ResidenceAdapter) getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onFailure(Throwable t) {
      Toast.makeText(getActivity(), "Failed to retrieve residence list", Toast.LENGTH_SHORT).show();
    }
  }

  class ResidenceAdapter extends ArrayAdapter<Residence>
  {
    private Context context;
    List<Residence> residences;

    public ResidenceAdapter(Context context, ArrayList<Residence> residences) {
      super(context, 0, residences);
      this.context = context;
      this.residences = residences;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      if (convertView == null) {
        convertView = inflater.inflate(R.layout.list_item_residence, null);
      }
      Residence res = getItem(position);

      TextView geolocation = (TextView) convertView.findViewById(R.id.residence_list_item_geolocation);
      geolocation.setText(res.geolocation);

      TextView dateTextView = (TextView) convertView.findViewById(R.id.residence_list_item_dateTextView);
      dateTextView.setText(res.getDateString());

      CheckBox rentedCheckBox = (CheckBox) convertView.findViewById(R.id.residence_list_item_isrented);
      rentedCheckBox.setChecked(res.rented);

      return convertView;
    }
  }

  //Broadcast receiver for receiving status updates from the IntentService
  private class ResponseReceiver extends BroadcastReceiver
  {
    //private void ResponseReceiver() {}
    // Called when the BroadcastReceiver gets an Intent it's registered to receive
    @Override
    public void onReceive(Context context, Intent intent)
    {
      //refreshDonationList();
      adapter.residences = app.portfolio.residences;
      adapter.notifyDataSetChanged();
    }
  }
}