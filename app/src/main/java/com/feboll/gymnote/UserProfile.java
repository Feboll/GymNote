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
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.feboll.gymnote.R;

import java.util.ArrayList;

public class UserProfile extends ActionBarActivity {

	ArrayAdapter<String> ProfileListAdapter;
	ArrayList<String> ProfileName;
	int groupPoz, childePoz, resume = 0;
	Button dropBtn;
    ListView prifile_list;
    TextView noProfile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

			ProfileName = new ArrayList<String>();
			prifile_list = (ListView)findViewById(R.id.profile_list);
			noProfile = (TextView)findViewById(R.id.noProfile);

			DBHelper dbOpenHelper = new DBHelper(this, "gymnote");
			SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
			Cursor cProfile = database.query("user_profile", null, null, null,	null, null, null);
			cProfile.moveToFirst();

			if (cProfile.getCount()==0){
				prifile_list.setVisibility(View.GONE);
				noProfile.setVisibility(View.VISIBLE);
			} else {
				prifile_list.setVisibility(View.VISIBLE);
				noProfile.setVisibility(View.GONE);
				do {
					ProfileName.add(cProfile.getString(2));
				} while(cProfile.moveToNext());
			}

			ProfileListAdapter = new ArrayAdapter<String>(this, R.layout.list_view, R.id.label, ProfileName);
			prifile_list.setAdapter(ProfileListAdapter);

			cProfile.close();
			dbOpenHelper.close();

			prifile_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Intent intent = new Intent(UserProfile.this, UserProfileItem.class);
					intent.putExtra("position", position);
					startActivity(intent);
				}
			});
			prifile_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
					showDialog(position+1);
					return false;
				}
			});
    }

    @Override
    protected void onResume() {
        DBHelper dbOpenHelper = new DBHelper(this, "gymnote");
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        Cursor cProfile = database.query("user_profile", null, null, null,	null, null, null);
        cProfile.moveToFirst();
        if (cProfile.getCount()==0){
            prifile_list.setVisibility(View.GONE);
            noProfile.setVisibility(View.VISIBLE);
        } else {
            prifile_list.setVisibility(View.VISIBLE);
            noProfile.setVisibility(View.GONE);
            ProfileName.clear();
            do {
                ProfileName.add(cProfile.getString(2));
            } while(cProfile.moveToNext());
        }
        ProfileListAdapter.notifyDataSetChanged();

        cProfile.close();
        dbOpenHelper.close();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
			switch (id){
				case R.id.training:{
					Intent intent = new Intent(UserProfile.this, TrainingList.class);
					startActivity(intent);
					return true;
				}
				case R.id.exercise:{
					Intent intent = new Intent(UserProfile.this, ExList.class);
					startActivity(intent);
					return true;
				}
				case R.id.gaging: {
					Intent intent = new Intent(UserProfile.this, Gaging.class);
					startActivity(intent);
					return true;
				}
				case R.id.statistics: {
					Intent intent = new Intent(UserProfile.this, Statistics.class);
					startActivity(intent);
					return true;
				}
				case R.id.profile: {
					Intent intent = new Intent(UserProfile.this, UserProfile.class);
					startActivity(intent);
					return true;
				}
			}
        return super.onOptionsItemSelected(item);
    }

	public void addProfile(View v){
		showDialog(0);
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog dialogDetails = null;
		View dialogview = null;
		LayoutInflater inflater = LayoutInflater.from(this);
		if (id == 0){
			dialogview = inflater.inflate(R.layout.add_profile, null);
		} else {
			dialogview = inflater.inflate(R.layout.popup_drop_btn, null);
		}

		AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(this);
		dialogDetails = dialogbuilder.setView(dialogview).create();
		return dialogDetails;
	}
	@Override
	protected void onPrepareDialog(final int id, Dialog dialog) {
		final AlertDialog alertDialog = (AlertDialog) dialog;
		if (id==0){
			final Button addBtn = (Button) alertDialog.findViewById(R.id.addBtn);
			final EditText profileTitleE = (EditText) alertDialog.findViewById(R.id.profileTitle);
			addBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					ContentValues cv = new ContentValues();
					DBHelper dbOpenHelper = new DBHelper(UserProfile.this, "gymnote");
					SQLiteDatabase database = dbOpenHelper.getWritableDatabase();

					cv.put("name", profileTitleE.getText().toString());
					ProfileName.add(profileTitleE.getText().toString());
					database.insert("user_profile", null, cv);

					ProfileListAdapter.notifyDataSetChanged();
                    prifile_list.setVisibility(View.VISIBLE);
                    noProfile.setVisibility(View.GONE);

					dbOpenHelper.close();
					profileTitleE.setText("");
					alertDialog.dismiss();
				}
			});
		} else {
			Button dropBtn = (Button) alertDialog.findViewById(R.id.dropBtn);
			dropBtn.setText(R.string.drop_profile_btn);
			dropBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
                    AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(UserProfile.this);
                    confirmationDialog.setMessage(R.string.confirmation_message).setPositiveButton(R.string.confirmation_yes, new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int arg1) {
                            DBHelper dbOpenHelper = new DBHelper(UserProfile.this, "gymnote");
                            SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
                            Cursor cProfile = database.query("user_profile", null, null, null,	null, null, null);
                            cProfile.moveToPosition(id - 1);
                            ProfileName.remove(id-1);
                            database.delete("user_profile", "_id =" + cProfile.getString(0), null);
                            ProfileListAdapter.notifyDataSetChanged();
                            cProfile = database.query("user_profile", null, null, null,	null, null, null);
                            if (cProfile.getCount()==0){
                                prifile_list.setVisibility(View.GONE);
                                noProfile.setVisibility(View.VISIBLE);
                            }
                            dbOpenHelper.close();
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
