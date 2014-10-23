package com.feboll.gymnote;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;


public class MyActivity extends ActionBarActivity {
	Button newTrainingBtn, resumeTrainingBtn;
	TextView trainingCount, lastTraining, weightCount, heightCount;
	private DBManadger db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

			newTrainingBtn = (Button)findViewById(R.id.button01);
			resumeTrainingBtn = (Button)findViewById(R.id.button02);
			trainingCount = (TextView)findViewById(R.id.trainingCount);
			lastTraining = (TextView)findViewById(R.id.lastTraining);
			weightCount = (TextView)findViewById(R.id.weightCount);
			heightCount = (TextView)findViewById(R.id.heightCount);

			db = new DBManadger(this);

			Cursor cTraining = db.getTraining(null);
			cTraining.moveToLast();

			Cursor cUser = db.getUser_profile();
			cUser.moveToFirst();

			if (cUser.getCount()>0){
				weightCount.setText(cUser.getString(3) + " кг.");
				heightCount.setText(cUser.getString(4) + " см.");
			} else {
				weightCount.setText("-");
				heightCount.setText("-");
			}

			if (cTraining.getCount()!=0 && cTraining.getString(2)==null){
				newTrainingBtn.setVisibility(View.GONE);
				resumeTrainingBtn.setVisibility(View.VISIBLE);
				trainingCount.setText(String.valueOf(cTraining.getCount()-1));
				if(cTraining.getCount()-1>0){
					cTraining.moveToPrevious();
					lastTraining.setText(cTraining.getString(1));
				} else {
					lastTraining.setText("-");
				}
			} else {
				newTrainingBtn.setVisibility(View.VISIBLE);
				resumeTrainingBtn.setVisibility(View.GONE);
				trainingCount.setText(String.valueOf(cTraining.getCount()));
				cTraining.moveToLast();
				if (cTraining.getCount()==0){
					lastTraining.setText("-");
				} else {
					lastTraining.setText(cTraining.getString(1));
				}
			}

			cUser.close();
			cTraining.close();
			db.close();
    }

	@Override
	protected void onResume() {
		Cursor cTraining = db.getTraining(null);
		cTraining.moveToLast();
		if (cTraining.getCount()!=0 && cTraining.getString(2)==null){
			newTrainingBtn.setVisibility(View.GONE);
			resumeTrainingBtn.setVisibility(View.VISIBLE);
			trainingCount.setText(String.valueOf(cTraining.getCount()-1));
			if(cTraining.getCount()-1>0){
				cTraining.moveToPrevious();
				lastTraining.setText(cTraining.getString(1));
			} else {
				lastTraining.setText("-");
			}
		} else {
			newTrainingBtn.setVisibility(View.VISIBLE);
			resumeTrainingBtn.setVisibility(View.GONE);
			trainingCount.setText(String.valueOf(cTraining.getCount()));
			cTraining.moveToLast();
			if (cTraining.getCount()==0){
				lastTraining.setText("-");
			} else {
				lastTraining.setText(cTraining.getString(1));
			}
		}
		Cursor cUser = db.getUser_profile();
		cUser.moveToFirst();
		if (cUser.getCount()>0){
			weightCount.setText(cUser.getString(3) + " кг.");
			heightCount.setText(cUser.getString(4) + " см.");
		} else {
			weightCount.setText("-");
			heightCount.setText("-");
		}
		cUser.close();
		cTraining.close();
		super.onResume();
	}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
			switch (id){
				case R.id.training:{
					Intent intent = new Intent(MyActivity.this, TrainingList.class);
					startActivity(intent);
					return true;
				}
				case R.id.exercise:{
					Intent intent = new Intent(MyActivity.this, ExList.class);
					startActivity(intent);
					return true;
				}
				case R.id.gaging: {
					Intent intent = new Intent(MyActivity.this, Gaging.class);
					startActivity(intent);
					return true;
				}
				case R.id.statistics: {
					Intent intent = new Intent(MyActivity.this, Statistics.class);
					startActivity(intent);
					return true;
				}
				case R.id.profile: {
					Intent intent = new Intent(MyActivity.this, UserProfile.class);
					startActivity(intent);
					return true;
				}
			}
        return super.onOptionsItemSelected(item);
    }

	public void newTraining(View v){
		Intent intent = new Intent(MyActivity.this, newTrainingEx.class);
		intent.putExtra("resume", 0);
		startActivity(intent);
	}
	public void resumeTraining(View v){
		Intent intent = new Intent(MyActivity.this, newTrainingEx.class);
		intent.putExtra("resume", 1);
		startActivity(intent);
	}
}
