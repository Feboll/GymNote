package com.feboll.gymnote;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

import java.util.ArrayList;

public class ExListForTraining extends ActionBarActivity {

	public final static String THIEF_G = "com.feboll.gymnote.THIEF_G";
	public final static String THIEF_P = "com.feboll.gymnote.THIEF_P";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ex_list_for_training);

			ExpandableListView ExlistView = (ExpandableListView)findViewById(R.id.expandableListView);
			ArrayList<String> groups = new ArrayList<String>();
			ArrayList<ArrayList<String>> groupChilde = new ArrayList<ArrayList<String>>();
			ArrayList<String> children = new ArrayList<String>();

			DBHelper dbOpenHelper = new DBHelper(this, "gymnote");
			SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
			Cursor cExGroud = database.query("categoryOfExercise", null, null, null, null, null, null);
			Cursor cExChilode;
			cExGroud.moveToFirst();
			do {
				groups.add(cExGroud.getString(1));
				cExChilode = database.query("exercise", new String[] {"_id", "exercise", "categoryOfExercise_id"}, "categoryOfExercise_id=?", new String[] { cExGroud.getString(0)},
						null, null, null);
				cExChilode.moveToFirst();
				do children.add(cExChilode.getString(1)); while (cExChilode.moveToNext());
				groupChilde.add(children);
				children = new ArrayList<String>();
			} while (cExGroud.moveToNext());
			cExGroud.close();
			cExChilode.close();
			dbOpenHelper.close();

			ExpListAdapter adapter = new ExpListAdapter(getApplicationContext(), groupChilde, groups);
			ExlistView.setAdapter(adapter);
			ExlistView.setOnChildClickListener(new OnChildClickListener() {
				public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,   int childPosition, long id) {
					Intent intent = new Intent(ExListForTraining.this, newTrainingEx.class);
					intent.putExtra(THIEF_G, groupPosition);
					intent.putExtra(THIEF_P, childPosition);
					setResult(RESULT_OK, intent);
					finish();
					return false;
				}
			});
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ex_list_for_training, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
			switch (id){
				case R.id.training:{
					Intent intent = new Intent(ExListForTraining.this, TrainingList.class);
					startActivity(intent);
					return true;
				}
				case R.id.exercise:{
					Intent intent = new Intent(ExListForTraining.this, ExList.class);
					startActivity(intent);
					return true;
				}
				case R.id.gaging: {
					Intent intent = new Intent(ExListForTraining.this, Gaging.class);
					startActivity(intent);
					return true;
				}
				case R.id.statistics: {
					Intent intent = new Intent(ExListForTraining.this, Statistics.class);
					startActivity(intent);
					return true;
				}
				case R.id.profile: {
					Intent intent = new Intent(ExListForTraining.this, UserProfile.class);
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
