package com.sparke.model;

import java.util.List;

import com.example.material.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PersonnalItemAdapter extends ArrayAdapter<PersonnalItem> {

	private int resourceId;
	
	public PersonnalItemAdapter(Context context, int resource,
			List<PersonnalItem> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		resourceId = resource;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		PersonnalItem item = getItem(position);
		View view;
		ViewHolder viewHolder;
		if(convertView == null) {
			view = LayoutInflater.from(getContext()).inflate(resourceId, null);
			viewHolder = new ViewHolder();
			viewHolder.keyView = (TextView) view.findViewById(R.id.key);
			viewHolder.valueView = (TextView) view.findViewById(R.id.value);
			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.keyView.setText(item.getKey());
		viewHolder.valueView.setText(item.getValue());
		return view;
	}
	
	class ViewHolder {
		TextView keyView;
		TextView valueView;
	}

}
