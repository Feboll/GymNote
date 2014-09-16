package com.feboll.gymnote;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;

public class newTrainingEx extends ActionBarActivity {
	ArrayAdapter<String> ExListAdapter;
	ArrayList<String> Ex;
	int groupPoz, childePoz, resume = 0;
	Button dropBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_training_ex);
			resume = getIntent().getIntExtra("resume", 0);

			Ex = new ArrayList<String>();
			ExListAdapter = new ArrayAdapter<String>(this, R.layout.list_view, R.id.label, Ex);
			ListView List = (ListView)findViewById(R.id.listView01);
			DBHelper dbOpenHelper = new DBHelper(this, "gymnote");
			SQLiteDatabase database = dbOpenHelper.getWritableDatabase();

			if (resume==1){
				Cursor cTraining = database.query("training", null, null, null, null, null, null);
				cTraining.moveToLast();
				if (cTraining.getCount()!=0){
					Cursor cEx = database.query("training_exercise", new String[] {"_id", "training_id", "exercise_id", "categoryOfExercise_id"}, "training_id=?",
							new String[] { String.valueOf(cTraining.getString(0))},	null, null, null);
					cEx.moveToFirst();
					if (cEx.getCount()>0) {
						do {
							Cursor cExName = database.query("exercise", new String[] {"_id", "exercise", "categoryOfExercise_id"}, "_id=?",
									new String[] { String.valueOf(cEx.getString(2))},	null, null, null);
							cExName.moveToFirst();
							Ex.add(cExName.getString(1));
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
			dbOpenHelper.close();

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
                        DBHelper dbOpenHelper = new DBHelper(newTrainingEx.this, "gymnote");
                        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
                        Cursor cTraining = database.query("training", null, null, null, null, null, null);
                        cTraining.moveToLast();
                        Cursor cEx = database.query("training_exercise", new String[] {"_id", "training_id", "exercise_id", "categoryOfExercise_id"}, "training_id=?",
                                new String[] { String.valueOf(cTraining.getString(0))},	null, null, null);
                        cEx.moveToPosition(id);
                        Ex.remove(id);
                        ExListAdapter.notifyDataSetChanged();

                        database.delete("training_exercise", "_id=" + cEx.getString(0), null);
                        cEx.close(); cTraining.close();
                        dbOpenHelper.close();
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
				/*case R.id.chat: {
					return true;
				}
				case R.id.settings:{

				}*/
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

				DBHelper dbOpenHelper = new DBHelper(this, "gymnote");
				SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
				Cursor cExChilode = database.query("exercise", new String[] {"_id", "exercise", "categoryOfExercise_id"},
						"categoryOfExercise_id=?", new String[] { String.valueOf(groupPoz)}, null, null, null);
				cExChilode.moveToPosition(childePoz);
				Cursor cTraining = database.query("training", null, null, null, null, null, null);
				cTraining.moveToLast();
				ContentValues cv = new ContentValues();
				if(cTraining.getCount()<=0 || cTraining.getString(2)!=null){
					String currentDateTimeString = (String) DateFormat.format("yyyy-MM-dd kk:mm", new Date());
					cv.put("training_start", currentDateTimeString);
					database.insert("training", null, cv);
				}
				cTraining = database.query("training", null, null, null, null, null, null);
				cTraining.moveToLast();

				cv = new ContentValues();
				cv.put("training_id", cTraining.getString(0));
				cv.put("exercise_id", cExChilode.getString(0));
				cv.put("categoryOfExercise_id", groupPoz);
				database.insert("training_exercise", null, cv);

				Ex.add(cExChilode.getString(1));
				ExListAdapter.notifyDataSetChanged();
				cTraining.close(); cExChilode.close();
				dbOpenHelper.close();
			}
		}
	}

	public void endTraining (View v){
		DBHelper dbOpenHelper = new DBHelper(this, "gymnote");
		SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
		Cursor cTraining = database.query("training", null, null, null, null, null, null);
		cTraining.moveToLast();
		Cursor cEx = database.query("training_exercise", new String[] {"_id", "training_id", "exercise_id", "categoryOfExercise_id"}, "training_id=?",
				new String[] { String.valueOf(cTraining.getString(0))},	null, null, null);
		if (cEx.getCount()==0)	database.delete("training", "_id =" + cTraining.getString(0), null);
		else {
			String DateTimeString = (String) DateFormat.format("yyyy-MM-dd kk:mm",new Date());
			ContentValues cv = new ContentValues();
			cv.put("training_end", DateTimeString);
			database.update("training", cv, "_id=" + cTraining.getString(0), null);
		}
		database.close();
		dbOpenHelper.close();
		finish();
	}
}
