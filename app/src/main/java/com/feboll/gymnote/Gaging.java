package com.feboll.gymnote;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.feboll.gymnote.R;

import java.util.ArrayList;

public class Gaging extends ActionBarActivity {
	ArrayList<String> profileTitle;
	ArrayAdapter<String> prifileAdapterSpinner, listadapter;
	ArrayList<String> daynames, value, valueTitle;
	Button addProfile;
	Spinner profileList;
	ListView valueList;
	int profilePosition = 0;

	private DBManadger db;

    @Override
    protected void onResume() {

        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gaging);
			addProfile = (Button)findViewById(R.id.add_profile);
			profileList = (Spinner)findViewById(R.id.profile_list);
			valueList = (ListView)findViewById(R.id.value_list);

			String valueT[] = new String[] {"Вес", "Рост", "Бедро", "Бицепс", "Голень", "Грудь", "Талия"};
			valueTitle = new ArrayList<String>();
			profileTitle = new ArrayList<String>();
			value = new ArrayList<String>();
			daynames = new ArrayList<String>();
			for (int i=0; i<valueT.length; i++){
				valueTitle.add(valueT[i]);
				value.add("0");
			}

			db = new DBManadger(this);
			final Cursor cProfile = db.getAllUser_profile();
			if(cProfile.getCount()>0){
				addProfile.setVisibility(View.GONE);
				profileList.setVisibility(View.VISIBLE);
				valueList.setVisibility(View.VISIBLE);

				profileTitle.clear();
				value.clear();
				//Это переделать
					for (int i=3; i<=9; i++){
						if(cProfile.getString(i)!=null){
							value.add(cProfile.getString(i));
						} else {
							value.add("0");
						}
						if(i==3){
							daynames.add(valueTitle.get(i-3) + " - " + value.get(i-3) + " кг.");
						} else {
							daynames.add(valueTitle.get(i-3) + " - " + value.get(i-3) + " см.");
						}
					}
				//
				do {
					profileTitle.add(cProfile.getString(2));
				} while (cProfile.moveToNext());
			} else {
				addProfile.setVisibility(View.VISIBLE);
				profileList.setVisibility(View.GONE);
				valueList.setVisibility(View.GONE);
			}
			cProfile.close();
			db.close();

			prifileAdapterSpinner = new ArrayAdapter<String>(Gaging.this, android.R.layout.simple_spinner_item, profileTitle);
			profileList.setAdapter(prifileAdapterSpinner);

			listadapter = new ArrayAdapter(this, R.layout.list_view, R.id.label, daynames);
			valueList.setAdapter(listadapter);
			valueList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					showDialog(position+1);
				}
			});

			profileList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					profilePosition = position;

					Cursor cProfile = db.getAllUser_profile();
					cProfile.moveToPosition(position);
					//Это переделать
					value.clear();
					daynames.clear();
					for (int i=3; i<=9; i++){
						if(cProfile.getString(i)!=null){
							value.add(cProfile.getString(i));
						} else {
							value.add("0");
						}
						if(i==3){
							daynames.add(valueTitle.get(i-3) + " - " + value.get(i-3) + " кг.");
						} else {
							daynames.add(valueTitle.get(i-3) + " - " + value.get(i-3) + " см.");
						}
					}
					//
					listadapter.notifyDataSetChanged();
					cProfile.close();
				}
				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}
			});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gaging, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
			switch (id){
				case R.id.training:{
					Intent intent = new Intent(Gaging.this, TrainingList.class);
					startActivity(intent);
					return true;
				}
				case R.id.exercise:{
					Intent intent = new Intent(Gaging.this, ExList.class);
					startActivity(intent);
					return true;
				}
				case R.id.gaging: {
					Intent intent = new Intent(Gaging.this, Gaging.class);
					startActivity(intent);
					return true;
				}
				case R.id.statistics: {
					Intent intent = new Intent(Gaging.this, Statistics.class);
					startActivity(intent);
					return true;
				}
				case R.id.profile: {
					Intent intent = new Intent(Gaging.this, UserProfile.class);
					startActivity(intent);
					return true;
				}
			}
        return super.onOptionsItemSelected(item);
    }

	public void addProfile(View v){
		showDialog(0);
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog dialogDetails = null;
		View dialogview = null;
		LayoutInflater inflater = LayoutInflater.from(this);
		dialogview = inflater.inflate(R.layout.add_profile, null);
		AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(this);
		dialogDetails = dialogbuilder.setView(dialogview).create();
		return dialogDetails;
	}
	@Override
	protected void onPrepareDialog(final int id, Dialog dialog) {
		final AlertDialog alertDialog = (AlertDialog) dialog;

		final Button addBtn = (Button)alertDialog.findViewById(R.id.addBtn);
		final EditText profileTitleE = (EditText)alertDialog.findViewById(R.id.profileTitle);
		final TextView titlePopup = (TextView)alertDialog.findViewById(R.id.titlePopup);

		if (id==0){
			addBtn.setText(R.string.add_profile);
			titlePopup.setText(R.string.profile_label_btn);
			profileTitleE.setInputType(InputType.TYPE_CLASS_TEXT);
		} else {
			addBtn.setText(R.string.add_profile_value);
			titlePopup.setText(valueTitle.get(id-1));
			profileTitleE.setInputType(InputType.TYPE_CLASS_NUMBER);
			profileTitleE.setText(value.get(id-1));
		}

		addBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ContentValues cv = new ContentValues();
				if (id==0){
					cv.put("name", profileTitleE.getText().toString());
					profileTitle.add(profileTitleE.getText().toString());
					db.insertItem("user_profile", cv);

					addBtn.setText(R.string.add_profile);
					titlePopup.setText(R.string.profile_label_btn);

				} else {
					addBtn.setText(R.string.add_profile_value);
					titlePopup.setText(valueTitle.get(id-1));
					Cursor cProfile = db.getAllUser_profile();
					cProfile.moveToPosition(profilePosition);
					cv = new ContentValues();
					cv.put(cProfile.getColumnName(id+2), profileTitleE.getText().toString());
					db.updateItem("user_profile", cv, "_id=" + cProfile.getString(0));
					cProfile.close();
				}

				prifileAdapterSpinner.notifyDataSetChanged();
				Cursor cProfile = db.getAllUser_profile();
				if(cProfile.getCount()>0){
					addProfile.setVisibility(View.GONE);
					profileList.setVisibility(View.VISIBLE);
					valueList.setVisibility(View.VISIBLE);
					cProfile.moveToPosition(profilePosition);
					value.clear();
					daynames.clear();
					//Это переделать
						for (int i=3; i<=9; i++){
							if(cProfile.getString(i)!=null){
								value.add(cProfile.getString(i));
							} else {
								value.add("0");
							}
							if(i==3){
								daynames.add(valueTitle.get(i-3) + " - " + value.get(i-3) + " кг.");
							} else {
								daynames.add(valueTitle.get(i-3) + " - " + value.get(i-3) + " см.");
							}
						}
						listadapter.notifyDataSetChanged();
					//
				} else {
					addProfile.setVisibility(View.VISIBLE);
					profileList.setVisibility(View.GONE);
					valueList.setVisibility(View.GONE);
				}
				cProfile.close();
				profileTitleE.setText("");
				alertDialog.dismiss();
			}
		});
	}
}
