package com.feboll.gymnote;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.feboll.gymnote.R;

import java.util.ArrayList;

public class TrainingExDescription extends ActionBarActivity {
	int childPosition=0, groupPosition=0, kps = 0, tonaj = 0, weightCount, repeat;
	ArrayList<String> set = new ArrayList<String>();
	ArrayList<String> weight = new ArrayList<String>();
	EditText edRepetition, edWeight;
	int position;
	Button dropBtn, editBtn;
	ListAdapter boxAdapter;
	TextView textKps, textTonaj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_ex_description);
			childPosition = getIntent().getIntExtra("childPosition", 0);
			groupPosition = getIntent().getIntExtra("groupPosition", 0);

			ListView lvMain = (ListView) findViewById(R.id.setList);
			TextView noSets = (TextView) findViewById(R.id.noSets);

			DBHelper dbOpenHelper = new DBHelper(this, "gymnote");
			SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
			Cursor cTraining = database.query("training", null, null, null, null, null, null);
			cTraining.moveToPosition(groupPosition);
			Cursor cEx = database.query("training_exercise", new String[] {"_id", "training_id", "exercise_id", "categoryOfExercise_id"}, "training_id=?",
					new String[] { String.valueOf(cTraining.getString(0))},	null, null, null);
			cEx.moveToPosition(childPosition);
			Cursor cExName = database.query("exercise", new String[] {"_id", "exercise", "categoryOfExercise_id"}, "_id=?",
					new String[] { String.valueOf(cEx.getString(2))},	null, null, null);
			cExName.moveToFirst();
			TextView tv = (TextView)findViewById(R.id.exTitle);
			tv.setText(cExName.getString(1));
			Cursor cSet = database.query("training_set", new String[] {"_id", "weight", "repetition", "training_gymnastic_num", "training_id"},
					"training_gymnastic_num=? AND training_id=?", new String[] { cEx.getString(0), cTraining.getString(0)},	null, null, null);
			cSet.moveToFirst();
			if (cSet.getCount()>0){
				do {
					if(cSet.getString(2).equals("")){
						repeat = 0;
					} else {
						repeat = cSet.getInt(2);
					}
					if(cSet.getString(1).equals("")){
						weightCount = 0;
					} else {
						weightCount = cSet.getInt(1);
					}
					kps += repeat;
					tonaj += weightCount*repeat;
					set.add(String.valueOf(repeat));
					weight.add(String.valueOf(weightCount));
				} while (cSet.moveToNext());
				boxAdapter = new ListAdapter(TrainingExDescription.this, set, weight);

				lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view, final int _position, long id) {
						position = _position;
						showDialog(2);
					};
				});

				lvMain.setAdapter(boxAdapter);
				lvMain.setVisibility(View.VISIBLE);
				noSets.setVisibility(View.GONE);
			} else {
				lvMain.setVisibility(View.GONE);
				noSets.setVisibility(View.VISIBLE);
			}

			textKps = (TextView)findViewById(R.id.kps);
			textTonaj = (TextView)findViewById(R.id.tonaj);
			textKps.setText(String.valueOf(kps));
			textTonaj.setText(String.valueOf(tonaj));
			cTraining.close(); cEx.close(); cExName.close();
			dbOpenHelper.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.training_ex_description, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
			switch (id){
				case R.id.training:{
					Intent intent = new Intent(TrainingExDescription.this, TrainingList.class);
					startActivity(intent);
					return true;
				}
				case R.id.exercise:{
					Intent intent = new Intent(TrainingExDescription.this, ExList.class);
					startActivity(intent);
					return true;
				}
				case R.id.gaging: {
					Intent intent = new Intent(TrainingExDescription.this, Gaging.class);
					startActivity(intent);
					return true;
				}
				case R.id.statistics: {
					Intent intent = new Intent(TrainingExDescription.this, Statistics.class);
					startActivity(intent);
					return true;
				}
				case R.id.profile: {
					Intent intent = new Intent(TrainingExDescription.this, UserProfile.class);
					startActivity(intent);
					return true;
				}
			}
        return super.onOptionsItemSelected(item);
    }


	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog dialogDetails = null;
		View dialogview = null;
		LayoutInflater inflater = LayoutInflater.from(this);
		dialogview = inflater.inflate(R.layout.edit_set_dialog, null);
		AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(this);
		dialogDetails = dialogbuilder.setView(dialogview).create();
		return dialogDetails;
	}
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		final AlertDialog alertDialog = (AlertDialog) dialog;

		DBHelper dbOpenHelper = new DBHelper(TrainingExDescription.this, "gymnote");
		final SQLiteDatabase database = dbOpenHelper.getWritableDatabase();

		Cursor cTraining = database.query("training", null, null, null, null, null, null);
		cTraining.moveToPosition(groupPosition);

		Cursor cEx = database.query("training_exercise", new String[] {"_id", "training_id", "exercise_id", "categoryOfExercise_id"}, "training_id=?",
				new String[] { String.valueOf(cTraining.getString(0))},	null, null, null);
		cEx.moveToPosition(childPosition);

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
					AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(TrainingExDescription.this);
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

		}
	}
}
