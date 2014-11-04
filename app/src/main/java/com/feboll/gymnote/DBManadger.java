package com.feboll.gymnote;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.sql.Struct;
import java.util.ArrayList;

public class DBManadger extends SQLiteAssetHelper {
	private static final String DATABASE_NAME = "gymnote.db";
	private static final int DATABASE_VERSION = 2;

	public DBManadger(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	//Получаем группы мышц--------------------------------------------------------------------------------------------------------------
	public Cursor getCategoryOfExercise() {
		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		String [] sqlSelect = {"_id", "categoryOfExercise_name"};
		String sqlTables = "categoryOfExercise";

		qb.setTables(sqlTables);
		Cursor c = qb.query(db, sqlSelect, null, null, null, null, null);
		c.moveToFirst();
		db.close();
		return c;
	}
	//----------------------------------------------------------------------------------------------------------------------------------

	//Получаем все упражнения-----------------------------------------------------------------------------------------------------------
	public Cursor getExercise(String exercise_id, String categoryOfExercise_id) {
		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		String [] sqlSelect = {"_id", "categoryOfExercise_id", "exercise", "exDescription", "exAtention", "exWeight", "exSet", "exDistance"};
		String sqlTables = "exercise";

		qb.setTables(sqlTables);
		Cursor c ;
		if (exercise_id == null)	{
			c = qb.query(db, sqlSelect, "categoryOfExercise_id=?", new String[]{categoryOfExercise_id}, null, null, null);
		} else if (exercise_id != null && categoryOfExercise_id != null) {
			c = qb.query(db, sqlSelect, "_id=? AND categoryOfExercise_id=?", new String[]{exercise_id, categoryOfExercise_id}, null, null, null);
		} else if (categoryOfExercise_id == null) {
			c = qb.query(db, sqlSelect, "_id=?", new String[]{exercise_id}, null, null, null);
		} else {
			c = qb.query(db, sqlSelect, null, null, null, null, null);
		}
		c.moveToFirst();
		db.close();
		return c;
	}
	//----------------------------------------------------------------------------------------------------------------------------------

	//Получаем все тренировки-----------------------------------------------------------------------------------------------------------
	public Cursor getTraining(String training_id) {
		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		String [] argAllValue = new String[]{training_id};
		String [] sqlSelect = {"_id", "training_start", "training_end"};
		String sqlTables = "training";

		qb.setTables(sqlTables);
		Cursor c = costumCursor(argAllValue, sqlSelect, sqlTables, db, qb);

		c.moveToFirst(); db.close();
		return c;
	}
	//----------------------------------------------------------------------------------------------------------------------------------

	//Получаем все упражнения в тренировке----------------------------------------------------------------------------------------------
	public Cursor getTraining_Exercise(String training_exercise_id, String training_id, String exercise_id, String categoryOfExercise_id,
																		 String trExYeasyValue, String trExNormalValue, String trExHardValue) {
		String [] argAllValue = new String[]{training_exercise_id, training_id, exercise_id, categoryOfExercise_id, trExYeasyValue, trExNormalValue, trExHardValue};
		String [] sqlSelect = {"_id", "training_id", "exercise_id", "categoryOfExercise_id", "trExYeasy", "trExNormal", "trExHard"};
		String sqlTables = "training_exercise";

		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		qb.setTables(sqlTables);
		Cursor c = costumCursor(argAllValue, sqlSelect, sqlTables, db, qb);

		c.moveToFirst();
		db.close();
		return c;
	}
	//----------------------------------------------------------------------------------------------------------------------------------

	//Получаем все подходы в упражнении-------------------------------------------------------------------------------------------------
	public Cursor getTraining_set(String training_set_id, String training_id, String exercise_id, String training_gymnastic_num) {
		String [] argAllValue = new String[]{training_set_id, training_id, exercise_id, training_gymnastic_num};
		String [] sqlSelect = {"_id", "training_id", "exercise_id", "training_gymnastic_num", "weight", "repetition", "warmUp"};
		String sqlTables = "training_set";

		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		qb.setTables(sqlTables);
		Cursor c = costumCursor(argAllValue, sqlSelect, sqlTables, db, qb);

		c.moveToFirst();
		db.close();
		return c;
	}
	//----------------------------------------------------------------------------------------------------------------------------------

	//Получаем все подходы в упражнении-------------------------------------------------------------------------------------------------
	public Cursor getTraining_set_max_weight(String training_set_id, String training_id, String exercise_id, String training_gymnastic_num) {
		String [] argAllValue = new String[]{training_set_id, training_id, exercise_id, training_gymnastic_num};
		String [] sqlSelect = {"_id", "training_id", "exercise_id", "training_gymnastic_num", "MAX(weight)", "repetition", "warmUp"};
		String sqlTables = "training_set";

		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		qb.setTables(sqlTables);
		Cursor c = costumCursor(argAllValue, sqlSelect, sqlTables, db, qb);

		c.moveToFirst();
		db.close();
		return c;
	}
	//----------------------------------------------------------------------------------------------------------------------------------

	//Получаем все профили--------------------------------------------------------------------------------------------------------------
	public Cursor getUser_profile() {
		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		String [] sqlSelect = {"_id", "firstStart", "name", "userWeight", "userHeight", "userThigh", "userBiceps",
													 "userShin", "userChest", "userWaist", "userSquats", "userDeadlift", "userBenchpress"};
		String sqlTables = "user_profile";

		qb.setTables(sqlTables);
		Cursor c = qb.query(db, sqlSelect, null, null, null, null, null);
		c.moveToFirst();
		db.close();
		return c;
	}
	//----------------------------------------------------------------------------------------------------------------------------------

	//Обновляем элемент-----------------------------------------------------------------------------------------------------------------
	public void updateItem(String sqlTables, ContentValues cv, String item) {
		SQLiteDatabase db = getReadableDatabase();
		db.update(sqlTables, cv, item, null);
		db.close();
	}
	//----------------------------------------------------------------------------------------------------------------------------------

	//Обновляем элемент-----------------------------------------------------------------------------------------------------------------
	public void deleteItem(String sqlTables, String item) {
		SQLiteDatabase db = getReadableDatabase();
		db.delete(sqlTables, item, null);
		db.close();
	}
	//----------------------------------------------------------------------------------------------------------------------------------

	//Встовляем элемент-----------------------------------------------------------------------------------------------------------------
	public void insertItem(String sqlTables, ContentValues cv) {
		SQLiteDatabase db = getReadableDatabase();
		db.insert(sqlTables, null, cv);
		db.close();
	}
	//----------------------------------------------------------------------------------------------------------------------------------

	//Получаем кастомный курсов --------------------------------------------------------------------------------------------------------
	public Cursor costumCursor(String [] argAllValue, String [] sqlSelect, String sqlTables, SQLiteDatabase db, SQLiteQueryBuilder qb) {
		int j=0;

		ArrayList<String> argValueA = new ArrayList<String>();
		String argSelection = "";

		for (int i=0; i<argAllValue.length; i++){
			if (argAllValue[i]!=null) {
				if (j>0) {
					argSelection = argSelection + " AND " + sqlSelect[i] + "=?";
				} else {
					argSelection = argSelection + sqlSelect[i] + "=?";
				}
				argValueA.add(argAllValue[i]);
				j++;
			}
		}
		String [] argValue = new String[argValueA.size()];
		for (int i=0; i<argValueA.size(); i++){
			argValue[i] = argValueA.get(i);
		}

		qb.setTables(sqlTables);
		Cursor costumCursor = qb.query(db, sqlSelect, argSelection, argValue, null, null, null);

		return costumCursor;
	}
	//----------------------------------------------------------------------------------------------------------------------------------
}
