package com.feboll.gymnote;

import android.app.AlertDialog;
import android.app.Dialog;
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
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.feboll.gymnote.R;

import java.util.ArrayList;

public class TrainingList extends ActionBarActivity {
	ArrayList<String> training = new ArrayList<String>();
	ArrayList<ArrayList<String>> trainingExGroup = new ArrayList<ArrayList<String>>();
	ArrayList<String> trainingEx = new ArrayList<String>();
	ExpListAdapter adapter;
	int groupPosition, childePosition, flag = -1, i=0;
	TextView noTraining;
	ExpandableListView ExlistView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_list);
			noTraining = (TextView)findViewById(R.id.noTrainingItem);
			ExlistView = (ExpandableListView)findViewById(R.id.trainingListView);


			DBHelper dbOpenHelper = new DBHelper(this, "gymnote");
			SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
			Cursor cTraining = database.query("training", null, null, null, null, null, null);
			Cursor cTrainingExChilode;
			cTraining.moveToFirst();
			if (cTraining.getCount()>0){
				do {
					training.add(cTraining.getString(1));
					cTrainingExChilode = database.query("training_exercise",
							new String[] {"_id", "training_id", "exercise_id", "categoryOfExercise_id"},
							"training_id=?", new String[] { cTraining.getString(0)},
							null, null, null);
					cTrainingExChilode.moveToFirst();

					if(cTrainingExChilode.getCount()>0){
						do {
							Cursor cTrainingExName = database.query("exercise",  new String[] {"_id", "categoryOfExercise_id", "exercise"},
									"_id=?", new String[] { cTrainingExChilode.getString(2)}, null, null, null, null);
							cTrainingExName.moveToFirst();
							trainingEx.add(cTrainingExName.getString(2));
							cTrainingExName.close();

						} while (cTrainingExChilode.moveToNext());
					} else {
						database.delete("training", "_id=" + cTraining.getString(0), null);
						training.remove(cTraining.getPosition());
					}
					trainingExGroup.add(trainingEx);
					trainingEx = new ArrayList<String>();
				} while (cTraining.moveToNext());
				cTrainingExChilode.close();
				noTraining.setVisibility(View.GONE);
				ExlistView.setVisibility(View.VISIBLE);
			} else {
				noTraining.setVisibility(View.VISIBLE);
				ExlistView.setVisibility(View.GONE);
			}
			cTraining.close();
			dbOpenHelper.close();

			adapter = new ExpListAdapter(getApplicationContext(), trainingExGroup, training);
			ExlistView.setAdapter(adapter);

			ExlistView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
				public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,   int childPosition, long id) {
					Intent intent = new Intent(TrainingList.this, TrainingExDescription.class);
					intent.putExtra("groupPosition", groupPosition);
					intent.putExtra("childPosition", childPosition);
					startActivity(intent);
					return false;
				}
			});
			ExlistView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
					if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
						groupPosition = ExpandableListView.getPackedPositionGroup(id);
						flag = 0;
						showDialog(groupPosition);
						return true;
					} else {
						childePosition = ExpandableListView.getPackedPositionChild(id);
						flag = 1;
						showDialog(childePosition);
						return true;
					}
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
		Button dropBtn = (Button) alertDialog.findViewById(R.id.dropBtn);
		if (flag==0){
			dropBtn.setText(R.string.drop_traoning);
		} else {
			dropBtn.setText(R.string.drop_ex_on_traoning);
		}
		dropBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(TrainingList.this);
                confirmationDialog.setMessage(R.string.confirmation_message).setPositiveButton(R.string.confirmation_yes, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int arg1) {
                        DBHelper dbOpenHelper = new DBHelper(TrainingList.this, "gymnote");
                        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
                        Cursor cTraining = database.query("training", null, null, null, null, null, null);
                        cTraining.moveToPosition(groupPosition);
                        if(flag == 0){
                            training.remove(groupPosition);
                            trainingExGroup.remove(groupPosition);

                            database.delete("training_set", "training_id=" + cTraining.getString(0), null);
                            database.delete("training_exercise", "training_id=" + cTraining.getString(0), null);
                            database.delete("training", "_id=" + cTraining.getString(0), null);
                        } else {
                            Cursor cEx = database.query("training_exercise", new String[] {"_id", "training_id", "exercise_id", "categoryOfExercise_id"}, "training_id=?",
                                    new String[] { String.valueOf(cTraining.getString(0))},	null, null, null);
                            cEx.moveToPosition(childePosition);
                            trainingExGroup.get(groupPosition).remove(childePosition);
                            database.delete("training_set", "training_gymnastic_num=? AND training_id=?",
                                    new String[] { cEx.getString(0), cTraining.getString(0)} );
                            database.delete("training_exercise", "_id=" + cEx.getString(0), null);

                            cEx = database.query("training_exercise", new String[] {"_id", "training_id", "exercise_id", "categoryOfExercise_id"}, "training_id=?",
                                    new String[] { String.valueOf(cTraining.getString(0))},	null, null, null);
                            if (cEx.getCount()==0){
                                training.remove(groupPosition);
                                trainingExGroup.remove(groupPosition);
                                database.delete("training", "_id=" + cTraining.getString(0), null);
                            }
                            cEx.close();
                        }
                        cTraining = database.query("training", null, null, null, null, null, null);
                        if (cTraining.getCount()==0){
                            noTraining.setVisibility(View.VISIBLE);
                            ExlistView.setVisibility(View.GONE);
                        } else {
                            noTraining.setVisibility(View.GONE);
                            ExlistView.setVisibility(View.VISIBLE);
                        }

                        cTraining.close();
                        dbOpenHelper.close();
                        adapter.notifyDataSetChanged();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.training_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
			switch (id){
				case R.id.training:{
					Intent intent = new Intent(TrainingList.this, TrainingList.class);
					startActivity(intent);
					return true;
				}
				case R.id.exercise:{
					Intent intent = new Intent(TrainingList.this, ExList.class);
					startActivity(intent);
					return true;
				}
				case R.id.gaging: {
					Intent intent = new Intent(TrainingList.this, Gaging.class);
					startActivity(intent);
					return true;
				}
				case R.id.statistics: {
					Intent intent = new Intent(TrainingList.this, Statistics.class);
					startActivity(intent);
					return true;
				}
				case R.id.profile: {
					Intent intent = new Intent(TrainingList.this, UserProfile.class);
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
}
