package com.sparke.model;

import com.example.material.R;
import com.sparke.modules.FragmentShowNumber;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CheckNumListAdapter extends BaseAdapter {

	private Context context;

	public CheckNumListAdapter(Context context) {
		this.context = context;
	}

	private Context getContext() {
		return context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		LinearLayout layout = null;
		if (convertView != null) {
			layout = (LinearLayout) convertView;
		} else {
			layout = (LinearLayout) LayoutInflater.from(getContext()).inflate(
					R.layout.check_number_listnumber_cell, null);
		}

		// 设置列表项的各类子控件
		TextView textView_cell_name = (TextView) layout
				.findViewById(R.id.textView_check_number_listnumber_cell_name);
		TextView textView_cell_id = (TextView) layout
				.findViewById(R.id.textView_check_number_listnumber_cell_id);
		TextView textView_cell_time = (TextView) layout
				.findViewById(R.id.textView_check_number_listnumber_cell_time);

		textView_cell_name.setText(FragmentShowNumber.nameList.get(position));
		textView_cell_id.setText(FragmentShowNumber.idList.get(position));
		textView_cell_time.setText(FragmentShowNumber.timeList.get(position));
		return layout;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	// ///////////可能有问题///////////////////////
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return FragmentShowNumber.nameList.size();
	}
}
