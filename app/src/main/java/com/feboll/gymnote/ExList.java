package com.feboll.gymnote;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class ExList extends ActionBarActivity {
	EditText exName, exDes;
	int cExGroudPos = 0;
	ExpandableListView ExlistView;
	ExpListAdapter adapter;

	ArrayList<String> data, groups, children;
	ArrayList<ArrayList<String>> groupChilde;

	Cursor cExGroud, cExChilode;

	private DBManadger db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ex_list);

			data = new ArrayList<String>();

			ExlistView = (ExpandableListView)findViewById(R.id.expandableListView02);

			groups = new ArrayList<String>();
			groupChilde = new ArrayList<ArrayList<String>>();
			children = new ArrayList<String>();

			db = new DBManadger(this);

			cExGroud = db.getAllCategoryOfExercise();
				cExGroud.moveToFirst();

				do {
					groups.add(cExGroud.getString(1));
					cExChilode = db.getAllGroupExercise(null, cExGroud.getString(0));
					cExChilode.moveToFirst();

					do children.add(cExChilode.getString(2)); while (cExChilode.moveToNext());
					groupChilde.add(children);
					children = new ArrayList<String>();
				} while (cExGroud.moveToNext());

				adapter = new ExpListAdapter(getApplicationContext(), groupChilde, groups);
				ExlistView.setAdapter(adapter);

				cExGroud.moveToFirst();
				do data.add(cExGroud.getString(1)); while (cExGroud.moveToNext());
			cExGroud.close();
			db.close();

			adapter = new ExpListAdapter(getApplicationContext(), groupChilde, groups);
			ExlistView.setAdapter(adapter);

			ExlistView.setOnChildClickListener(new OnChildClickListener() {
				public boolean onChildClick(ExpandableListView parent, View v, final int groupPosition, final int childPosition, long id) {
					Intent intent = new Intent(ExList.this, ExItemDescription.class);
					intent.putExtra("groupPosition", groupPosition);
					intent.putExtra("childPosition", childPosition);
					startActivity(intent);
					return false;
				}
			});
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ex_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
			switch (id){
				case R.id.training:{
					Intent intent = new Intent(ExList.this, TrainingList.class);
					startActivity(intent);
					return true;
				}
				case R.id.exercise:{
					Intent intent = new Intent(ExList.this, ExList.class);
					startActivity(intent);
					return true;
				}
				case R.id.gaging: {
					Intent intent = new Intent(ExList.this, Gaging.class);
					startActivity(intent);
					return true;
				}
				case R.id.statistics: {
					Intent intent = new Intent(ExList.this, Statistics.class);
					startActivity(intent);
					return true;
				}
				case R.id.profile: {
					Intent intent = new Intent(ExList.this, UserProfile.class);
					startActivity(intent);
					return true;
				}
			}
        return super.onOptionsItemSelected(item);
    }
	@Override
	protected void onResume() {
		int groupPos=0, childePos=0;

		cExGroud = db.getAllCategoryOfExercise();
		cExGroud.moveToFirst();

		do {
			cExChilode = db.getAllGroupExercise(null, cExGroud.getString(0));
			cExChilode.moveToFirst();

			groupChilde.get(groupPos).clear();
			do {
				groupChilde.get(groupPos).add(cExChilode.getString(2));
				childePos++;
			} while (cExChilode.moveToNext());

			childePos=0;
			groupPos++;
		} while (cExGroud.moveToNext());

		adapter.notifyDataSetChanged();

		cExGroud.close(); cExChilode.close();

		super.onResume();
	}
	public void addExToList(View v){
		showDialog(1);
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog dialogDetails = null;
		View dialogview = null;
		LayoutInflater inflater = LayoutInflater.from(this);
		dialogview = inflater.inflate(R.layout.add_ex_item, null);
		AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(this);
		dialogDetails = dialogbuilder.setView(dialogview).create();
		return dialogDetails;
	}
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		final AlertDialog alertDialog = (AlertDialog) dialog;
		ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
		adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		Spinner spinner = (Spinner) alertDialog.findViewById(R.id.group);
		spinner.setAdapter(adapterSpinner);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {cExGroudPos = position;}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {  }
		});

		Button addEx = (Button) alertDialog.findViewById(R.id.addBtn);
		addEx.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				exName = (EditText) alertDialog.findViewById(R.id.titleEx);
				exDes = (EditText) alertDialog.findViewById(R.id.titleEx);

				if (exName.getText().length()!=0) {

					groupChilde.get(cExGroudPos).add(exName.getText().toString());
					adapter.notifyDataSetChanged();
					ContentValues cv = new ContentValues();

					cv.put("exercise", exName.getText().toString());
					cv.put("categoryOfExercise_id", cExGroudPos+1);
					cv.put("exDescription", exDes.getText().toString());

					db.insertItem("exercise", cv);
          alertDialog.dismiss();
				} else {
					Toast.makeText(getBaseContext(), "Введите название упражнения",	Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
}
