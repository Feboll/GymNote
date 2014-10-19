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
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.feboll.gymnote.R;

import java.io.Console;
import java.util.ArrayList;

public class ExItemDescription extends ActionBarActivity {
	EditText exNameEd, exDesEdit;
	int cExGroudPos = 0, flag = 0, groupPosition, childPosition;
	String itemId;
	String editTitle, editDes;
	ArrayList<String> data;
	TextView groupName, exTitle, exDes;
	Button dropBtn, editBtn;

	private DBManadger db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ex_item_description);

			groupPosition = getIntent().getExtras().getInt("groupPosition");
			childPosition = getIntent().getExtras().getInt("childPosition");

			cExGroudPos = groupPosition;

			db = new DBManadger(this);

			Cursor cExGroud = db.getCategoryOfExercise();
			cExGroud.moveToFirst();

			data = new ArrayList<String>();
			do data.add(cExGroud.getString(1)); while (cExGroud.moveToNext());

			Cursor cExChilode = db.getExercise(null, String.valueOf(groupPosition + 1));
				cExChilode.moveToPosition(childPosition);

				cExGroud.moveToPosition(groupPosition);

				groupName = (TextView)findViewById(R.id.exGroup);
				exTitle = (TextView)findViewById(R.id.exName);
				exDes = (TextView)findViewById(R.id.exDes);

				groupName.setText(cExGroud.getString(1));
				exTitle.setText(cExChilode.getString(2));
			editTitle = cExChilode.getString(2);
				exDes.setText(cExChilode.getString(3));
			editDes = cExChilode.getString(3);
				itemId = cExChilode.getString(0);

			cExChilode.close();
			cExGroud.close();
			db.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ex_item_description, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
			int id = item.getItemId();
			switch (id){
				case R.id.training:{
					Intent intent = new Intent(ExItemDescription.this, TrainingList.class);
					startActivity(intent);
					return true;
				}
				case R.id.exercise:{
					Intent intent = new Intent(ExItemDescription.this, ExList.class);
					startActivity(intent);
					return true;
				}
				case R.id.gaging: {
					Intent intent = new Intent(ExItemDescription.this, Gaging.class);
					startActivity(intent);
					return true;
				}
				case R.id.statistics: {
					Intent intent = new Intent(ExItemDescription.this, Statistics.class);
					startActivity(intent);
					return true;
				}
				case R.id.profile: {
					Intent intent = new Intent(ExItemDescription.this, UserProfile.class);
					startActivity(intent);
					return true;
				}
			}
        return super.onOptionsItemSelected(item);
    }

	public void editItem (View v){
		showDialog(1);
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog dialogDetails = null;
		View dialogview = null;
		LayoutInflater inflater = LayoutInflater.from(this);
		dialogview = inflater.inflate(R.layout.edit_ex_item, null);
		AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(this);
		dialogDetails = dialogbuilder.setView(dialogview).create();
		return dialogDetails;
	}
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		final AlertDialog alertDialog = (AlertDialog) dialog;
		exNameEd = (EditText) alertDialog.findViewById(R.id.titleEx);
		exDesEdit = (EditText) alertDialog.findViewById(R.id.editDes);

		editBtn = (Button) alertDialog.findViewById(R.id.editBtn);
		dropBtn = (Button) alertDialog.findViewById(R.id.dropBtn);

		ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(ExItemDescription.this, android.R.layout.simple_spinner_item, data);
		adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		Spinner spinner = (Spinner) alertDialog.findViewById(R.id.spinner1);
		spinner.setAdapter(adapterSpinner);
		spinner.setSelection(cExGroudPos);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				cExGroudPos = position;
				flag = 1;
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {  }
		});

		exNameEd.setText(editTitle);
		exDesEdit.setText(editDes);

		editBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (exNameEd.getText().length()!=0) {

					 ContentValues cv = new ContentValues();
					 cv.put("exercise", exNameEd.getText().toString());
					 cv.put("categoryOfExercise_id", cExGroudPos+1);
					 cv.put("exDescription", exDesEdit.getText().toString());

					db.updateItem("exercise", cv, "_id=" + itemId);

					Cursor cExGroud = db.getCategoryOfExercise();
					cExGroud.moveToPosition(cExGroudPos);

					 groupName.setText(cExGroud.getString(1));
					editTitle = exNameEd.getText().toString();
					editDes = exDesEdit.getText().toString();
					 exTitle.setText(exNameEd.getText().toString());
					 exDes.setText(exDesEdit.getText().toString());

					 groupPosition = cExGroudPos;
					 Cursor cExChilode = db.getExercise(null, String.valueOf(groupPosition+1));
					if (flag==1) childPosition = cExChilode.getCount()-1;

					flag=0;
					cExGroud.close();
					cExChilode.close();
					alertDialog.dismiss();
				} else {
					Toast.makeText(getBaseContext(), "Введите название упражнения", Toast.LENGTH_SHORT).show();
				}
			}
		});

		dropBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(ExItemDescription.this);
			confirmationDialog.setMessage(R.string.confirmation_message).setPositiveButton(R.string.confirmation_yes, new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int arg1) {
							 db.deleteItem("exercise", "_id=" + itemId);
							alertDialog.dismiss();
							finish();
					}
			}).setNegativeButton(R.string.confirmation_no, new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int arg1) { }
			}).show();
			}
		});
	}
}
