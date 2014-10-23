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
	ArrayList<String> training, trainingEx;
	ArrayList<ArrayList<String>> trainingExGroup;
	ExpListAdapter adapter;
	int groupPosition, childePosition, flag = -1, i=0;
	TextView noTraining;
	ExpandableListView ExlistView;
	private DBManadger db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_list);
			noTraining = (TextView)findViewById(R.id.noTrainingItem);
			ExlistView = (ExpandableListView)findViewById(R.id.trainingListView);

			training = new ArrayList<String>();
			trainingExGroup = new ArrayList<ArrayList<String>>();
			trainingEx = new ArrayList<String>();

			db = new DBManadger(this);

			Cursor cTraining = db.getTraining(null);
			cTraining.moveToFirst();
			if (cTraining.getCount()>0){
				do {
					training.add(cTraining.getString(1));
					Cursor cTrainingExChilode = db.getTraining_Exercise(null, cTraining.getString(0), null, null, null, null, null);
					cTrainingExChilode.moveToFirst();

					if(cTrainingExChilode.getCount()>0){
						do {
							Cursor cTrainingExName = db.getExercise(cTrainingExChilode.getString(2), null);
							cTrainingExName.moveToFirst();
							trainingEx.add(cTrainingExName.getString(2));
							cTrainingExName.close();
						} while (cTrainingExChilode.moveToNext());
					} else {
						db.deleteItem("training", "_id=" + cTraining.getString(0));
						training.remove(cTraining.getPosition());
					}
					trainingExGroup.add(trainingEx);
					trainingEx = new ArrayList<String>();
				} while (cTraining.moveToNext());
				noTraining.setVisibility(View.GONE);
				ExlistView.setVisibility(View.VISIBLE);
			} else {
				noTraining.setVisibility(View.VISIBLE);
				ExlistView.setVisibility(View.GONE);
			}
			cTraining.close();
			db.close();

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
					groupPosition = ExpandableListView.getPackedPositionGroup(id);
					childePosition = ExpandableListView.getPackedPositionChild(id);
					if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
						flag = 0;
						showDialog(groupPosition);
						return true;
					} else {
						flag = 1;
						showDialog(childePosition);
						return true;
					}
				}
			});
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

                        Cursor cTraining = db.getTraining(null);
                        cTraining.moveToPosition(groupPosition);
                        if(flag == 0){
                            training.remove(groupPosition);
                            trainingExGroup.remove(groupPosition);

                          db.deleteItem("training_set", "training_id=" + cTraining.getString(0));
													db.deleteItem("training_exercise", "training_id=" + cTraining.getString(0));
													db.deleteItem("training", "_id=" + cTraining.getString(0));
                        } else {
                            Cursor cEx = db.getTraining_Exercise(null, cTraining.getString(0), null, null, null, null, null);
                            cEx.moveToPosition(childePosition);
                            trainingExGroup.get(groupPosition).remove(childePosition);
                            db.deleteItem("training_set", "training_gymnastic_num=" + cEx.getString(0) + " AND training_id=" + cTraining.getString(0));
														db.deleteItem("training_exercise", "_id=" + cEx.getString(0));

														cEx = db.getTraining_Exercise(null, cTraining.getString(0), null, null, null, null, null);
                            if (cEx.getCount()==0){
                                training.remove(groupPosition);
                                trainingExGroup.remove(groupPosition);
															db.deleteItem("training", "_id=" + cTraining.getString(0));
                            }
                            cEx.close();
                        }
											cTraining = db.getTraining(null);
                        if (cTraining.getCount()==0){
                            noTraining.setVisibility(View.VISIBLE);
                            ExlistView.setVisibility(View.GONE);
                        } else {
                            noTraining.setVisibility(View.GONE);
                            ExlistView.setVisibility(View.VISIBLE);
                        }
                        cTraining.close();
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
			}
        return super.onOptionsItemSelected(item);
    }
}
