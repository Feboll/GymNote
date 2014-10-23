package com.feboll.gymnote;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.feboll.gymnote.R;

import java.util.ArrayList;

public class UserProfileItem extends ActionBarActivity {
	ArrayList<String> profileTitle;
	ArrayAdapter<String> listadapter;
	ArrayList<String> daynames, value, valueTitle;
	ListView valueList;
	int profilePosition = 0;
	TextView imt, profileName, imtDescription;
	private DBManadger db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_item);

			valueList = (ListView)findViewById(R.id.value_list);
			String valueT[] = new String[] {"Вес", "Рост", "Бедро", "Бицепс", "Голень", "Грудь", "Талия"};
			valueTitle = new ArrayList<String>();
			profileTitle = new ArrayList<String>();
			profileTitle.add("");
			value = new ArrayList<String>();
			daynames = new ArrayList<String>();
			for (int i=0; i<valueT.length; i++){
				valueTitle.add(valueT[i]);
				value.add("0");
			}
			profilePosition = getIntent().getIntExtra("position", 0);
			imt = (TextView)findViewById(R.id.IMT);
			profileName = (TextView) findViewById(R.id.profileName);
			imtDescription = (TextView)findViewById(R.id.imtDescription);

			db = new DBManadger(this);

			final Cursor cProfile = db.getUser_profile();
			cProfile.moveToPosition(profilePosition);
			profileName.setText(cProfile.getString(2));
			profileTitle.clear();
			value.clear();
			//Это переделать
				for (int i=3; i<=9; i++){
					if(cProfile.getString(i)!=null)	value.add(cProfile.getString(i));
						else value.add("0");
					if(i==3) daynames.add(valueTitle.get(i-3) + " - " + value.get(i-3) + " кг.");
						else daynames.add(valueTitle.get(i-3) + " - " + value.get(i-3) + " см.");
				}
			//

			float wCount = cProfile.getFloat(3);
			float hCount = (float) Math.pow(cProfile.getFloat(4)/100, 2);
			float myfloatvariable = wCount/hCount;
			String imtCount = String.format("%.2f", myfloatvariable);
			if(cProfile.getFloat(4)!=0) imt.setText(imtCount);
			  else imt.setText("0");
			if (myfloatvariable<=16) imtDescription.setText(R.string.imt_description_1);
			  else if (myfloatvariable>=16 && myfloatvariable<18.5) imtDescription.setText(R.string.imt_description_2);
				else if (myfloatvariable>=18.5 && myfloatvariable<25) imtDescription.setText(R.string.imt_description_3);
				else if (myfloatvariable>=25 && myfloatvariable<30) imtDescription.setText(R.string.imt_description_4);
				else if (myfloatvariable>=30 && myfloatvariable<35) imtDescription.setText(R.string.imt_description_5);
				else if (myfloatvariable>=35 && myfloatvariable<40) imtDescription.setText(R.string.imt_description_6);
				else if (myfloatvariable>=40) imtDescription.setText(R.string.imt_description_7);

			cProfile.close();
			db.close();

			listadapter = new ArrayAdapter(this, R.layout.list_view, R.id.label, daynames);
			valueList.setAdapter(listadapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_profile_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      int id = item.getItemId();
			switch (id){
				case R.id.training:{
					Intent intent = new Intent(UserProfileItem.this, TrainingList.class);
					startActivity(intent);
					return true;
				}
				case R.id.exercise:{
					Intent intent = new Intent(UserProfileItem.this, ExList.class);
					startActivity(intent);
					return true;
				}
				case R.id.gaging: {
					Intent intent = new Intent(UserProfileItem.this, Gaging.class);
					startActivity(intent);
					return true;
				}
				case R.id.statistics: {
					Intent intent = new Intent(UserProfileItem.this, Statistics.class);
					startActivity(intent);
					return true;
				}
				case R.id.profile: {
					Intent intent = new Intent(UserProfileItem.this, UserProfile.class);
					startActivity(intent);
					return true;
				}
			}
        return super.onOptionsItemSelected(item);
    }
}
