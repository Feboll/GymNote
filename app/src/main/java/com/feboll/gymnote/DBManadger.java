package com.feboll.gymnote;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by Iam on 11.10.2014.
 */
public class DBManadger extends SQLiteAssetHelper {
	private static final String DATABASE_NAME = "gymnote";
	private static final int DATABASE_VERSION = 4;

	public DBManadger(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	//Получаем группы мышц--------------------------------------------------------------------------------------------------------------
	public Cursor getAllCategoryOfExercise() {
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
	public Cursor getAllExercise() {
		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		String [] sqlSelect = {"_id", "categoryOfExercise_id", "exercise", "exDescription", "exAtention", "exWeight", "exSet", "exDistance"};
		String sqlTables = "exercise";

		qb.setTables(sqlTables);
		Cursor c = qb.query(db, sqlSelect, null, null, null, null, null);
		c.moveToFirst();
		db.close();
		return c;
	}
	//----------------------------------------------------------------------------------------------------------------------------------
	//Получаем все упражнения для данной группы-----------------------------------------------------------------------------------------
	public Cursor getAllGroupExercise(String id, String group) {
		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		String [] sqlSelect = {"_id", "categoryOfExercise_id", "exercise", "exDescription", "exAtention", "exWeight", "exSet", "exDistance"};
		String sqlTables = "exercise";

		qb.setTables(sqlTables);
		Cursor c;
		if (id == null) {
			c = qb.query(db, sqlSelect, "categoryOfExercise_id=?", new String[]{group}, null, null, null);
		} else {
			c = qb.query(db, sqlSelect, "_id=? AND categoryOfExercise_id=?", new String[]{id, group}, null, null, null);
		}
		c.moveToFirst();
		db.close();
		return c;
	}
	//----------------------------------------------------------------------------------------------------------------------------------

	//Получаем все тренировки-----------------------------------------------------------------------------------------------------------
	public Cursor getAllTraining() {
		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		String [] sqlSelect = {"_id", "training_start", "training_end"};
		String sqlTables = "training";

		qb.setTables(sqlTables);
		Cursor c = qb.query(db, sqlSelect, null, null, null, null, null);
		c.moveToFirst();
		db.close();
		return c;
	}
	//----------------------------------------------------------------------------------------------------------------------------------

	//Получаем все упражнения в тренировке----------------------------------------------------------------------------------------------
	public Cursor getAllTraining_exercise() {
		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		String [] sqlSelect = {"_id", "training_id", "exercise_id", "categoryOfExercise_id", "trExYeasy", "trExNormal", "trExHard"};
		String sqlTables = "training_exercise";

		qb.setTables(sqlTables);
		Cursor c = qb.query(db, sqlSelect, null, null, null, null, null);
		c.moveToFirst();
		db.close();
		return c;
	}
	//----------------------------------------------------------------------------------------------------------------------------------

	//Получаем все подходы в упражнении-------------------------------------------------------------------------------------------------
	public Cursor getAllTraining_set() {
		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		String [] sqlSelect = {"_id", "weight", "repetition", "training_gymnastic_num", "training_id", "warmUp", "exercise_id"};
		String sqlTables = "training_set";

		qb.setTables(sqlTables);
		Cursor c = qb.query(db, sqlSelect, null, null, null, null, null);
		c.moveToFirst();
		db.close();
		return c;
	}
	//----------------------------------------------------------------------------------------------------------------------------------

	//Получаем все профили--------------------------------------------------------------------------------------------------------------
	public Cursor getAllUser_profile() {
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
}
