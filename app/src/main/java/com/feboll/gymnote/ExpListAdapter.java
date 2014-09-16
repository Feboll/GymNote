package com.feboll.gymnote;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class ExpListAdapter extends BaseExpandableListAdapter {
	private ArrayList<ArrayList<String>> mGroupChilde;
	private Context mContext;
	private ArrayList<String> mGroups;

	public ExpListAdapter (Context context,ArrayList<ArrayList<String>> groupChilde, ArrayList<String> groups){
		mContext = context;
		mGroupChilde = groupChilde;
		mGroups = groups;
	}

	@Override
	public int getGroupCount() {
		return mGroupChilde.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return mGroupChilde.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return mGroupChilde.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return mGroupChilde.get(groupPosition).get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.ex_list_group_view, null);
		}
		if (isExpanded){
			//Изменяем что-нибудь, если текущая Group раскрыта
		}
		else{
			//Изменяем что-нибудь, если текущая Group скрыта
		}
		TextView textGroup = (TextView) convertView.findViewById(R.id.textGroup);
		textGroup.setText(mGroups.get(groupPosition));
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.ex_list_childe_view, null);
		}
		TextView textChild = (TextView) convertView.findViewById(R.id.textChild);
		textChild.setText(mGroupChilde.get(groupPosition).get(childPosition));

		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}
