package com.feboll.gymnote;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;

public class newTrainingEx extends ActionBarActivity {
	ArrayAdapter<String> ExListAdapter;
	ArrayList<String> Ex;
	int groupPoz, childePoz, resume = 0;
	Button dropBtn;
	private DBManadger db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_training_ex);
			resume = getIntent().getIntExtra("resume", 0);

			Ex = new ArrayList<String>();
			ExListAdapter = new ArrayAdapter<String>(this, R.layout.list_view, R.id.label, Ex);
			ListView List = (ListView)findViewById(R.id.listView01);

			db = new DBManadger(this);

			if (resume==1){
				Cursor cTraining = db.getTraining();
				cTraining.moveToLast();
				if (cTraining.getCount()!=0){
					Cursor cEx = db.getTraining_Exercise(null, cTraining.getString(0), null, null, null, null, null);
					cEx.moveToFirst();
					if (cEx.getCount()>0) {
						do {
							Log.v("my", "_id=" + cEx.getString(0) + " cTraining_id = " + cEx.getString(1));
							Cursor cExName = db.getExercise(cEx.getString(2), null);
							cExName.moveToFirst();
							Log.v("my", "ex_id=" + cExName.getString(0));
							Ex.add(cExName.getString(2));
							cExName.close();
						} while (cEx.moveToNext());
					}
					cTraining.close(); cEx.close();
				}
			}

			List.setAdapter(ExListAdapter);

			List.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
					Intent intent = new Intent(newTrainingEx.this, NewTrainingSet.class);
					intent.putExtra("position", position);
					startActivity(intent);
				}
			});
			List.setOnItemLongClickListener(new OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
					showDialog(position);
					return true;
				}
			});
			db.close();
    }

	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog dialogDetails = null;
		View dialogview = null;
		LayoutInflater inflater = LayoutInflater.from(this);
		dialogview = inflater.inflate(R.layout.popup_drop_btn, null);
		AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(this);
		dialogDetails = dialogbuilder.setView(dialogview).create();
		return dialogDetails;
	}
	@Override
	protected void onPrepareDialog(final int id, Dialog dialog) {
		final AlertDialog alertDialog = (AlertDialog) dialog;
		dropBtn = (Button) alertDialog.findViewById(R.id.dropBtn);
		dropBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(newTrainingEx.this);
                confirmationDialog.setMessage(R.string.confirmation_message).setPositiveButton(R.string.confirmation_yes, new OnClickListener(){
                    public void onClick(DialogInterface dialog, int arg1) {
									Cursor cTraining = db.getTraining();
                  cTraining.moveToLast();
									Cursor cEx = db.getTraining_Exercise(null, cTraining.getString(0), null, null, null, null, null);
									cEx.moveToPosition(id);
									Ex.remove(id);
									ExListAdapter.notifyDataSetChanged();

									db.deleteItem("training_exercise", "_id=" + cEx.getString(0));
									cEx.close(); cTraining.close();
									alertDialog.dismiss();
                    }
                }).setNegativeButton(R.string.confirmation_no, new OnClickListener(){
                    public void onClick(DialogInterface dialog, int arg1) {
                   alertDialog.dismiss();
                    }
                }).show();
			}
		});
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_training_ex, menu);
        return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
			int id = item.getItemId();
		switch (id){
			case R.id.training:{
				Intent intent = new Intent(newTrainingEx.this, TrainingList.class);
				startActivity(intent);
				return true;
			}
			case R.id.exercise:{
				Intent intent = new Intent(newTrainingEx.this, ExList.class);
				startActivity(intent);
				return true;
			}
			case R.id.gaging: {
				Intent intent = new Intent(newTrainingEx.this, Gaging.class);
				startActivity(intent);
				return true;
			}
			case R.id.statistics: {
				Intent intent = new Intent(newTrainingEx.this, Statistics.class);
				startActivity(intent);
				return true;
			}
			case R.id.profile: {
				Intent intent = new Intent(newTrainingEx.this, UserProfile.class);
				startActivity(intent);
				return true;
			}
		}
			return super.onOptionsItemSelected(item);
	}

	static final private int CHOOSE_THIEF = 0;
	public void addEx(View v) {
		Intent intent = new Intent(newTrainingEx.this, ExListForTraining.class);
		startActivityForResult(intent, CHOOSE_THIEF);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == CHOOSE_THIEF) {
			if (resultCode == RESULT_OK) {
				groupPoz = data.getIntExtra(ExListForTraining.THIEF_G, -1) + 1;
				childePoz = data.getIntExtra(ExListForTraining.THIEF_P, -1);

				Cursor cExChilode = db.getExercise(null, String.valueOf(groupPoz));
				cExChilode.moveToPosition(childePoz);

				Cursor cTraining = db.getTraining();
				cTraining.moveToLast();
				ContentValues cv = new ContentValues();
				if(cTraining.getCount()<=0 || cTraining.getString(2)!=null){
					String currentDateTimeString = (String) DateFormat.format("yyyy-MM-dd kk:mm", new Date());
					cv.put("training_start", currentDateTimeString);
					db.insertItem("training", cv);
				}

				cTraining = db.getTraining();
				cTraining.moveToLast();

				cv = new ContentValues();
				cv.put("training_id", cTraining.getString(0));
				cv.put("exercise_id", cExChilode.getString(0));
				cv.put("categoryOfExercise_id", groupPoz);
				db.insertItem("training_exercise", cv);
				Log.v("my", "ex_id=" + cExChilode.getString(0));
				Ex.add(cExChilode.getString(2));

				ExListAdapter.notifyDataSetChanged();
				cTraining.close(); cExChilode.close();
			}
		}
	}

	public void endTraining (View v){
		AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(newTrainingEx.this);
		confirmationDialog.setMessage(R.string.confirmation_message).setPositiveButton(R.string.confirmation_yes, new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int arg1) {
				Cursor cTraining = db.getTraining();
				cTraining.moveToLast();
				Cursor cEx = db.getTraining_Exercise(null, cTraining.getString(0), null, null, null, null, null);
				if (cEx.getCount()==0)	db.deleteItem("training", "_id =" + cTraining.getString(0));
				else {
					String DateTimeString = (String) DateFormat.format("yyyy-MM-dd kk:mm",new Date());
					ContentValues cv = new ContentValues();
					cv.put("training_end", DateTimeString);
					db.updateItem("training", cv, "_id=" + cTraining.getString(0));
				}
				finish();
			}
		}).setNegativeButton(R.string.confirmation_no, new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int arg1) {		}
		}).show();

	}
}
