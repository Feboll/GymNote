package com.feboll.gymnote;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;

public class Statistics extends ActionBarActivity {
	Cursor cExGroup, cExChilode, cExSet;
	ArrayAdapter<String> exAdapterSpinner;
	int groupPos = 0, exPosition = 0, valuePosition = 0;
	int[] weightCount;
	String[] trainingCount;
	private GraphicalView mChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

			final ArrayList<String> groupData = new ArrayList<String>(), exData = new ArrayList<String>(), valueType = new ArrayList<String>();
			valueType.add(getString(R.string.max_weight_stat));
			valueType.add(getString(R.string.intensity_stat));

			exData.add("");

			final DBHelper dbOpenHelper = new DBHelper(Statistics.this, "gymnote");
			final SQLiteDatabase database = dbOpenHelper.getWritableDatabase();

			cExGroup = database.query("categoryOfExercise", null, null, null, null, null, null);
			cExGroup.moveToFirst();
			do {
				groupData.add(cExGroup.getString(1));
				cExChilode = database.query("exercise", new String[] {"_id", "exercise", "categoryOfExercise_id", "exDescription"}, "categoryOfExercise_id=?",
						new String[] { cExGroup.getString(0)},	null, null, null);
				cExChilode.moveToFirst();
				do {
					exData.add(cExChilode.getString(1));
				}while (cExChilode.moveToNext());
			} while (cExGroup.moveToNext());

			cExGroup.close();
			dbOpenHelper.close();

			ArrayAdapter<String> groupAdapterSpinner = new ArrayAdapter<String>(Statistics.this, android.R.layout.simple_spinner_item, groupData);
      groupAdapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			exAdapterSpinner = new ArrayAdapter<String>(Statistics.this, android.R.layout.simple_spinner_item, exData);
      exAdapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			ArrayAdapter<String> valueAdapterSpinner = new ArrayAdapter<String>(Statistics.this, android.R.layout.simple_spinner_item, valueType);
      valueAdapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			Spinner groupSpinner = (Spinner) findViewById(R.id.groupS);
			final Spinner exSpinner = (Spinner) findViewById(R.id.exS);
      Spinner valueSpinner = (Spinner) findViewById(R.id.value_type);

			groupSpinner.setAdapter(groupAdapterSpinner);
			exSpinner.setAdapter(exAdapterSpinner);
			valueSpinner.setAdapter(valueAdapterSpinner);

			groupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					DBHelper dbOpenHelper = new DBHelper(Statistics.this, "gymnote");
					SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
					exData.clear();
					groupPos = position;
						cExChilode = database.query("exercise", new String[] {"_id", "exercise", "categoryOfExercise_id", "exDescription"}, "categoryOfExercise_id=?",
								new String[] { String.valueOf(position+1)},	null, null, null);
						cExChilode.moveToFirst();
						do {
							exData.add(cExChilode.getString(1));
						}while (cExChilode.moveToNext());
					exSpinner.setSelection(0);
					checkCount(groupPos, 0, 0);
					cExChilode.close();
					dbOpenHelper.close();
					exAdapterSpinner.notifyDataSetChanged();
				}
				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}
			});

			exSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					exPosition = position;
					checkCount(groupPos, exPosition, valuePosition);
				}
				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}
			});
			valueSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					valuePosition = position;
					checkCount(groupPos, exPosition, valuePosition);
				}
				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}
			});
		}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.statistics, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
			switch (id){
				case R.id.training:{
					Intent intent = new Intent(Statistics.this, TrainingList.class);
					startActivity(intent);
					return true;
				}
				case R.id.exercise:{
					Intent intent = new Intent(Statistics.this, ExList.class);
					startActivity(intent);
					return true;
				}
				case R.id.gaging: {
					Intent intent = new Intent(Statistics.this, Gaging.class);
					startActivity(intent);
					return true;
				}
				case R.id.statistics: {
					Intent intent = new Intent(Statistics.this, Statistics.class);
					startActivity(intent);
					return true;
				}
				case R.id.profile: {
					Intent intent = new Intent(Statistics.this, UserProfile.class);
					startActivity(intent);
					return true;
				}
				/*case R.id.chat: {
					return true;
				}
				case R.id.settings:{
					return true;
				}*/
			}
        return super.onOptionsItemSelected(item);
    }

	public void checkCount(int _group, int _childe, int _value){
		int i=0, weightSum;
		weightCount = new int[1];
		trainingCount = new String[1];

		DBHelper dbOpenHelper = new DBHelper(Statistics.this, "gymnote");
		SQLiteDatabase database = dbOpenHelper.getWritableDatabase();

		Cursor cExName = database.query("exercise", new String[] {"_id", "exercise", "categoryOfExercise_id"}, "categoryOfExercise_id=?",
				new String[] { String.valueOf(_group+1)},	null, null, null);
		cExName.moveToPosition(_childe);
		Cursor cEx = database.query("training_exercise", new String[] {"_id", "training_id", "exercise_id", "categoryOfExercise_id"}, "exercise_id=?",
				new String[] { cExName.getString(0)},	null, null, null);
		cEx.moveToFirst();
		if(cEx.getCount()>0){
			Cursor cExTime = database.query("training", new String[] {"_id", "training_start", "training_end"}, "_id=?",
					new String[] { cEx.getString(0)},	null, null, null);
			cExTime.moveToFirst();

			weightCount = new int[cEx.getCount()];
			trainingCount = new String[cEx.getCount()];
			do {
				if (_value==0) {
					cExSet = database.query("training_set",
							new String[] {"_id", "MAX(weight)", "repetition", "training_gymnastic_num", "training_id", "warmUp"},
							"training_gymnastic_num=?", new String[] {cEx.getString(0)},	null, null, null);
					cExSet.moveToFirst();
					weightCount[i] = cExSet.getInt(1);
				} else {
					cExSet = database.query("training_set",
							new String[] {"_id", "weight", "repetition", "training_gymnastic_num", "training_id", "warmUp"},
							"training_gymnastic_num=?", new String[] {cEx.getString(0)},	null, null, null);
					cExSet.moveToFirst();
					if (cExSet.getCount()>0){
						weightSum = 0;
						do {
							weightSum += (cExSet.getInt(1)*cExSet.getInt(2));
						} while (cExSet.moveToNext());
						weightCount[i] = weightSum;
					}
				}
				trainingCount[i]=cExTime.getString(1).substring(0, cExTime.getString(1).indexOf(" "));
				i++;
			}while (cEx.moveToNext());
		} else {
			Toast.makeText(Statistics.this, "Нет данных по данному упражнению", Toast.LENGTH_SHORT).show();
		}
		//рисуем график
		int min = weightCount[0], max = weightCount[0];
		TimeSeries series = new TimeSeries("Line1");
		for (int j = 0; j < trainingCount.length; j++){
			series.add(j+1, weightCount[j]);
			if(weightCount[j]<min) min = weightCount[j];
			if(weightCount[j]>max) max = weightCount[j];
		}
		XYMultipleSeriesDataset dateset = new XYMultipleSeriesDataset();
		dateset.addSeries(series);

		DisplayMetrics metrics = Statistics.this.getResources().getDisplayMetrics();
		float textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, metrics);
		float pointSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, metrics);
		int marginBord = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, metrics));
		int marginTop = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, metrics));

		XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
		mRenderer.setBackgroundColor(getResources().getColor(R.color.app_background));
		mRenderer.setMarginsColor(getResources().getColor(R.color.app_background));
		mRenderer.setShowGrid(true);

		mRenderer.setShowCustomTextGrid(true);
		mRenderer.setAntialiasing(true);
		mRenderer.setPanEnabled(true, false);
		mRenderer.setZoomEnabled(true, true);
		mRenderer.setZoomButtonsVisible(false);
		for (int j = 0; j < trainingCount.length; j++){
			mRenderer.addXTextLabel(j+1, trainingCount[j]);
		}

		mRenderer.setXLabelsColor(getResources().getColor(R.color.button_background));
		mRenderer.setYLabelsColor(0, getResources().getColor(R.color.button_background));
		mRenderer.setXLabelsAlign(Paint.Align.CENTER);
		mRenderer.setXLabels(0);
		mRenderer.setXLabelsAngle(0f);
		mRenderer.setXLabelsPadding(10);
		mRenderer.setXAxisMin(0.8);
		mRenderer.setXAxisMax(i-i/3);
		mRenderer.setYAxisMax(max+max*0.1);
		mRenderer.setYAxisMin(min-max*0.1);
		mRenderer.setYLabelsAlign(Paint.Align.RIGHT);
		mRenderer.setPointSize(pointSize);
		mRenderer.setInScroll(true);

		mRenderer.setGridColor(getResources().getColor(R.color.button_background));
		mRenderer.setMargins(new int[]{marginTop, marginBord, marginTop, marginTop});
		mRenderer.setShowLegend(false);
		mRenderer.setLabelsTextSize(textSize);
		mRenderer.setXRoundedLabels(true);


		XYSeriesRenderer renderer = new XYSeriesRenderer();
		renderer.setLineWidth(marginTop);
		renderer.setShowLegendItem(true);
		renderer.setColor(getResources().getColor(R.color.button_background));
		renderer.setPointStyle(PointStyle.CIRCLE);
		renderer.setFillPoints(true);

		mRenderer.addSeriesRenderer(renderer);
		LinearLayout layout = (LinearLayout) findViewById(R.id.graph);
		if (mChartView == null) {
			mChartView = ChartFactory.getLineChartView(Statistics.this, dateset,	mRenderer);
			layout.addView(mChartView, new LayoutParams	(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		} else {
			layout.removeAllViews();
			mChartView = ChartFactory.getLineChartView(Statistics.this, dateset,	mRenderer);
			layout.addView(mChartView, new LayoutParams	(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			mChartView.repaint();
		}
		cExChilode.close();
		dbOpenHelper.close();
		exAdapterSpinner.notifyDataSetChanged();
	}
}
