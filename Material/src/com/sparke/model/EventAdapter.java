package com.sparke.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.example.material.R;
import com.sparke.modules.ActivityModifySchoolTime;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class EventAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private static final int NORMAL_ITEM = 1;
	private static final int EXTENT_ITEM = 2;

	private ArrayList<Event> mDataList;
	private Context mContext;

	public EventAdapter(ArrayList<Event> data, Context context) {
		mDataList = data;
		mContext = context;
	}

	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return mDataList.size();
	}

	@Override
	public int getItemViewType(int position) {
		if (position == 0)
			return EXTENT_ITEM;
		int currentType = mDataList.get(position).getType();
		int preType = mDataList.get(position - 1).getType();
		return currentType == preType ? NORMAL_ITEM : EXTENT_ITEM;
	}

	@Override
	public void onBindViewHolder(ViewHolder arg0, int arg1) {
		// TODO Auto-generated method stub
		Event event = mDataList.get(arg1);
		int temp;
		String str;
		if (arg0 instanceof ExtendItemHolder) {
			ExtendItemHolder exHolder = (ExtendItemHolder) arg0;
			str = event.getType() == 1 ? "考试时间" : "待办事项";
			exHolder.mType.setText(str);
		}

		NormalItemHolder holder = (NormalItemHolder) arg0;
		holder.mTitle.setText(event.getTitle());

		str = getTimeSting(event.getStart(), event.getEnd());
		holder.mTime.setText(str);

		try {
			long nd = 1000 * 24 * 60 * 60;
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy年MM月dd日-HH:mm");
			long diff = dateFormat.parse(event.getStart()).getTime()
					- new Date().getTime();
			if (diff < 0) {
				holder.mDeadline.setText("已过");
			} else {
				long day = diff / nd;
				holder.mDeadline.setText("" + day);
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		str = event.getPos();
		if (str == null || str.isEmpty()) {
			holder.mPos.setVisibility(View.GONE);
		} else {
			if (holder.mPos.getVisibility() == View.GONE) {
				holder.mPos.setVisibility(View.VISIBLE);
			}
			holder.mPos.setText(str);
		}

		temp = event.getSeat();
		if (temp == -1) {
			holder.mSeat.setVisibility(View.GONE);
		} else {
			if (holder.mSeat.getVisibility() == View.GONE) {
				holder.mSeat.setVisibility(View.VISIBLE);
			}
			holder.mSeat.setText(temp + "");
		}

		str = event.getNote();
		if (str == null || str.isEmpty()) {
			holder.mNote.setVisibility(View.GONE);
		} else {
			if (holder.mNote.getVisibility() == View.GONE) {
				holder.mNote.setVisibility(View.VISIBLE);
			}
			holder.mNote.setText(str);
		}
	}

	public static String getTimeSting(String start, String end) {
		String str = "";
		String[] strArray = start.split("-");
		str = str + strArray[0] + " ";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		try {
			Date date = sdf.parse(strArray[0]);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			switch (calendar.get(Calendar.DAY_OF_WEEK)) {
			case 1:
				str = str + "周日 ";
				break;
			case 2:
				str = str + "周一 ";
				break;
			case 3:
				str = str + "周二 ";
				break;
			case 4:
				str = str + "周三 ";
				break;
			case 5:
				str = str + "周四 ";
				break;
			case 6:
				str = str + "周五 ";
				break;
			case 7:
				str = str + "周六 ";
				break;
			}
			if (end != null && !end.isEmpty()) {
				str = str + "(" + strArray[1] + "-" + end + ")";
			} else {
				str = str + strArray[1];
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return str;
		}
		return str;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
		// TODO Auto-generated method stub
		if (arg1 == NORMAL_ITEM) {
			return new NormalItemHolder(LayoutInflater.from(mContext).inflate(
					R.layout.list_item_base_school_time, arg0, false));
		} else {
			return new ExtendItemHolder(LayoutInflater.from(mContext).inflate(
					R.layout.list_item_school_time, arg0, false));
		}
	}

	public class NormalItemHolder extends RecyclerView.ViewHolder {
		public TextView mTitle;
		public TextView mTime;
		public TextView mPos;
		public TextView mSeat;
		public TextView mNote;
		public TextView mDeadline;

		public NormalItemHolder(View arg0) {
			super(arg0);
			// TODO Auto-generated constructor stub
			mTitle = (TextView) arg0.findViewById(R.id.school_time_title);
			mTime = (TextView) arg0.findViewById(R.id.school_time_time);
			mPos = (TextView) arg0.findViewById(R.id.school_time_pos);
			mSeat = (TextView) arg0.findViewById(R.id.school_time_seat);
			mNote = (TextView) arg0.findViewById(R.id.school_time_note);
			mDeadline = (TextView) arg0.findViewById(R.id.school_time_left);

			arg0.findViewById(R.id.school_time_container).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Event event = mDataList.get(getAdapterPosition());
							if (event.getType() == 1) {
								ActivityModifySchoolTime
										.startActivityNewsContent(mContext,
												event, getAdapterPosition(), 3);
							} else if (event.getType() == 2) {
								ActivityModifySchoolTime
										.startActivityNewsContent(mContext,
												event, getAdapterPosition(), 4);
							}
						}
					});
		}
	}

	public class ExtendItemHolder extends NormalItemHolder {

		public TextView mType;

		public ExtendItemHolder(View arg0) {
			super(arg0);
			// TODO Auto-generated constructor stub
			mType = (TextView) arg0.findViewById(R.id.school_time_type);
		}

	}

}
