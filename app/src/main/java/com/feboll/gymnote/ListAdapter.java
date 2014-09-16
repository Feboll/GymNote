package com.feboll.gymnote;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Iam on 18.07.2014.
 */
public class ListAdapter extends BaseAdapter {
	Context ctx;
	LayoutInflater lInflater;
	ArrayList<String> setList, weightList;

	ListAdapter(Context context, ArrayList<String> _setList, ArrayList<String> _weightList) {
		ctx = context;
		setList = _setList;
		weightList = _weightList;
		lInflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	// кол-во элементов
	@Override
	public int getCount() {
		return setList.size();
	}

	// элемент по позиции
	@Override
	public Object getItem(int position) {
		return setList.get(position);
	}

	// id по позиции
	@Override
	public long getItemId(int position) {
		return position;
	}

	// пункт списка
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// используем созданные, но не используемые view
		View view = convertView;
		if (view == null) {
			view = lInflater.inflate(R.layout.set_list, parent, false);
		}

		// заполняем View в пункте списка данными из товаров: наименование, цена
		// и картинка
		((TextView) view.findViewById(R.id.number)).setText(position+1 + ".");
		((TextView) view.findViewById(R.id.set)).setText(setList.get(position));
		((TextView) view.findViewById(R.id.weight)).setText(weightList.get(position));

		return view;
	}

}
