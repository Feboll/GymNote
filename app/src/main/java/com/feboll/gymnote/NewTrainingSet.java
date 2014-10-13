package com.feboll.gymnote;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.*;


import java.util.ArrayList;

public class NewTrainingSet extends ActionBarActivity {
	ArrayList<String> set = new ArrayList<String>();
	ArrayList<String> weight = new ArrayList<String>();
	ListAdapter boxAdapter;
	EditText edRepetition, edWeight;
	int pos, position, kps = 0, tonaj = 0;
	Button addBtn, dropBtn, editBtn;
	TextView textKps, textTonaj;
	String repeat, weightCount;
	Cursor cExName;
	private AdView adView;

	private SetHelper SetHelper = new SetHelper();

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_training_set);

		// Создание экземпляра adView. Добавление рекламы
		adView = new AdView(this);
		adView.setAdUnitId("ca-app-pub-5748071594809710/2182312782");
		adView.setAdSize(AdSize.BANNER);
		LinearLayout layout = (LinearLayout)findViewById(R.id.mainLayout);
		layout.addView(adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);

			boxAdapter = new ListAdapter(NewTrainingSet.this, set, weight);
			pos = getIntent().getIntExtra("position", 0);
			textKps = (TextView)findViewById(R.id.kps);
			textTonaj = (TextView)findViewById(R.id.tonaj);

			DBHelper dbOpenHelper = new DBHelper(NewTrainingSet.this, "gymnote");
			final SQLiteDatabase database = dbOpenHelper.getWritableDatabase();

			Cursor cTraining = database.query("training", null, null, null, null, null, null);
			cTraining.moveToLast();

			Cursor cEx = database.query("training_exercise", new String[] {"_id", "training_id", "exercise_id", "categoryOfExercise_id"}, "training_id=?",
					new String[] { String.valueOf(cTraining.getString(0))},	null, null, null);
			cEx.moveToPosition(pos);

			cExName = database.query("exercise", new String[] {"_id", "exercise", "categoryOfExercise_id"}, "_id=?",
					new String[] { String.valueOf(cEx.getString(2))},	null, null, null);
			cExName.moveToFirst();

			TextView tv = (TextView)findViewById(R.id.exTitle);
			tv.setText(cExName.getString(1));

			Cursor cSet = database.query("training_set", new String[] {"_id", "weight", "repetition", "training_gymnastic_num", "training_id"},
					"training_gymnastic_num=? AND training_id=?", new String[] { cEx.getString(0), cTraining.getString(0)},	null, null, null);

			if (cSet.getCount()>0) {
				cSet.moveToFirst();
				do {
					kps += cSet.getInt(2);
					tonaj += cSet.getInt(1)*cSet.getInt(2);
					set.add(cSet.getString(2));
					weight.add(cSet.getString(1));
				} while (cSet.moveToNext());
			}
			textKps.setText(String.valueOf(kps));
			textTonaj.setText(String.valueOf(tonaj));
			cTraining.close(); cEx.close();
			dbOpenHelper.close();
			// настраиваем список
			ListView lvMain = (ListView) findViewById(R.id.setList);

		lvMain.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, final int _position, long id) {
				position = _position;
				showDialog(2);
			};
		});
			lvMain.setAdapter(boxAdapter);

			LinearLayout mainLayout = (LinearLayout)findViewById(R.id.mainLayout);
			ConnectivityManager cm =(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
			boolean isConnected = SetHelper.checkConnection(cm);
			if (isConnected) mainLayout.setVisibility(View.GONE);
			else mainLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_training_set, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
			switch (id){
				case R.id.training:{
					Intent intent = new Intent(NewTrainingSet.this, TrainingList.class);
					startActivity(intent);
					return true;
				}
				case R.id.exercise:{
					Intent intent = new Intent(NewTrainingSet.this, ExList.class);
					startActivity(intent);
					return true;
				}
				case R.id.gaging: {
					Intent intent = new Intent(NewTrainingSet.this, Gaging.class);
					startActivity(intent);
					return true;
				}
				case R.id.statistics: {
					Intent intent = new Intent(NewTrainingSet.this, Statistics.class);
					startActivity(intent);
					return true;
				}
				case R.id.profile: {
					Intent intent = new Intent(NewTrainingSet.this, UserProfile.class);
					startActivity(intent);
					return true;
				}
			}
        return super.onOptionsItemSelected(item);
    }

	public void addSet (View v) {
		showDialog(1);
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog dialogDetails = null;
		View dialogview = null;
		LayoutInflater inflater = LayoutInflater.from(this);
		if (id == 1){
			dialogview = inflater.inflate(R.layout.add_set_dialog, null);
		} else if (id == 2){
			dialogview = inflater.inflate(R.layout.edit_set_dialog, null);
		}
		AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(this);
		dialogDetails = dialogbuilder.setView(dialogview).create();
		return dialogDetails;
	}
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		final AlertDialog alertDialog = (AlertDialog) dialog;

		DBHelper dbOpenHelper = new DBHelper(NewTrainingSet.this, "gymnote");
		final SQLiteDatabase database = dbOpenHelper.getWritableDatabase();

		Cursor cTraining = database.query("training", null, null, null, null, null, null); 			cTraining.moveToLast();
		Cursor cEx = database.query("training_exercise", new String[] {"_id", "training_id", "exercise_id", "categoryOfExercise_id"}, "training_id=?",
				new String[] { String.valueOf(cTraining.getString(0))},	null, null, null);
		cEx.moveToPosition(pos);
		final Cursor cSet = database.query("training_set", new String[] {"_id", "weight", "repetition", "training_gymnastic_num", "training_id", "warmUp"},
				"training_gymnastic_num=? AND training_id=?", new String[] { cEx.getString(0), cTraining.getString(0)},	null, null, null);
		cSet.moveToPosition(position);

		edRepetition = (EditText) alertDialog.findViewById(R.id.repetition);
		edWeight = (EditText) alertDialog.findViewById(R.id.weight);
		edRepetition.setText("");
		edWeight.setText("");
		edRepetition.requestFocus();
		if (id == 2){
			editBtn = (Button) alertDialog.findViewById(R.id.editBtn);
			dropBtn = (Button) alertDialog.findViewById(R.id.dropBtn);
			edRepetition.setText(cSet.getString(2));
			edWeight.setText(cSet.getString(1));
			editBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					ContentValues cv = new ContentValues();
					cv.put("weight", edWeight.getText().toString());
					cv.put("repetition", edRepetition.getText().toString());

					database.update("training_set", cv, "_id=" + cSet.getString(0), null);

					tonaj -= Integer.parseInt(weight.get(position))*Integer.parseInt(set.get(position));
					kps -= Integer.parseInt(set.get(position));

					set.remove(position);
					weight.remove(position);
					set.add(position, edRepetition.getText().toString());
					weight.add(position, edWeight.getText().toString());

					kps += Integer.parseInt(set.get(position));
					tonaj += Integer.parseInt(weight.get(position))*Integer.parseInt(set.get(position));

					textKps.setText(String.valueOf(kps));
					textTonaj.setText(String.valueOf(tonaj));

					boxAdapter.notifyDataSetChanged();
					alertDialog.dismiss();
				}
			});
			dropBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
                    AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(NewTrainingSet.this);
                    confirmationDialog.setMessage(R.string.confirmation_message).setPositiveButton(R.string.confirmation_yes, new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int arg1) {
                            database.delete("training_set", "_id=" + cSet.getString(0), null);

                            tonaj -= Integer.parseInt(weight.get(position))*Integer.parseInt(set.get(position));
                            kps -= Integer.parseInt(set.get(position));

                            set.remove(position);
                            weight.remove(position);

                            textKps.setText(String.valueOf(kps));
                            textTonaj.setText(String.valueOf(tonaj));

                            boxAdapter.notifyDataSetChanged();
                            alertDialog.dismiss();
                        }
                    }).setNegativeButton(R.string.confirmation_no, new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int arg1) {
                            alertDialog.dismiss();
                        }
                    }).show();

				}
			});

		} else if (id == 1) {
			addBtn = (Button) alertDialog.findViewById(R.id.addBtn);

			addBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					DBHelper dbOpenHelper = new DBHelper(NewTrainingSet.this, "gymnote");
					SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
					Cursor cTraining = database.query("training", null, null, null, null, null, null);
					cTraining.moveToLast();
					Cursor cEx = database.query("training_exercise", new String[] {"_id", "training_id", "exercise_id", "categoryOfExercise_id"}, "training_id=?",
							new String[] { String.valueOf(cTraining.getString(0))},	null, null, null);
					cEx.moveToPosition(pos);
					ContentValues cv = new ContentValues();

					if (edWeight.getText().toString().equals("")){
						weightCount = "0";
					} else {
						weightCount = edWeight.getText().toString();
					}

					if (edRepetition.getText().toString().equals("")){
						repeat = "0";
					} else {
						repeat = edRepetition.getText().toString();
					}
					cv.put("weight", weightCount);
					cv.put("repetition", repeat);
					cv.put("training_id", cTraining.getString(0));
					cv.put("training_gymnastic_num", cEx.getString(0));
					cv.put("exercise_id", cExName.getString(0));
					database.insert("training_set", null, cv);

					kps += Integer.parseInt(repeat);
					tonaj += Integer.parseInt(weightCount)*Integer.parseInt(repeat);

					set.add(repeat);
					weight.add(weightCount);

					textKps.setText(String.valueOf(kps));
					textTonaj.setText(String.valueOf(tonaj));
					dbOpenHelper.close();
					boxAdapter.notifyDataSetChanged();
					alertDialog.dismiss();
				}
			});
		}
	}

}

