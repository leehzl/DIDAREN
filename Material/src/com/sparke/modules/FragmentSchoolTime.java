package com.sparke.modules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.example.material.R;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.sparke.database_task.DatabaseHelper;
import com.sparke.model.Event;
import com.sparke.model.EventAdapter;
import com.sparke.web.spider.LoginSpider;
import com.sparke.web.util.WebSite;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class FragmentSchoolTime extends Fragment {

	private static final long START_DELAY = 1000;
	private static final int MSG_GETCONTENT_PROGRESS = 1000;
	private static final int MSG_REFRESH_PROGRESS = 1001;

	private RecyclerView mRecyclerView;
	private LinearLayoutManager mLinearLayoutManager;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private EventAdapter mAdapter;

	private Handler mHandler;

	private ArrayList<Event> mDataList;

	private LoginSpider mSpider;

	public FragmentSchoolTime() {
	}

	public static FragmentSchoolTime newInstance() {
		FragmentSchoolTime fragment = new FragmentSchoolTime();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null)
			return null;
		View view = inflater.inflate(R.layout.fragment_school_time, container,
				false);
		mDataList = new ArrayList<Event>();

		mSwipeRefreshLayout = (SwipeRefreshLayout) view
				.findViewById(R.id.school_time_refresh);
		mSwipeRefreshLayout.setColorSchemeResources(
				R.color.danny_background_dark, R.color.cpb_blue_dark,
				R.color.cpb_green_dark);
		mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				String[] params = new String[2];
				params[0] = "0";
				new TimeNetTask().execute(params);
			}
		});
		mRecyclerView = (RecyclerView) view.findViewById(R.id.school_time_list);
		mRecyclerView.setHasFixedSize(true);
		mLinearLayoutManager = new LinearLayoutManager(getActivity());
		mRecyclerView.setLayoutManager(mLinearLayoutManager);
		mAdapter = new EventAdapter(mDataList, getActivity());
		mRecyclerView.setAdapter(mAdapter);

		mHandler = new Handler(getActivity().getMainLooper()) {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case MSG_GETCONTENT_PROGRESS:
					String[] params = new String[] { "0" };
					new EventTask().execute(params);
					break;
				case MSG_REFRESH_PROGRESS:
					mSwipeRefreshLayout.setRefreshing(false);
					break;
				default:
					break;
				}
			}
		};
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		mHandler.sendEmptyMessageAtTime(MSG_GETCONTENT_PROGRESS, START_DELAY);
	}

	@Override
	public void onPause() {
		super.onPause();
		mHandler.removeCallbacksAndMessages(null);
	}

	public void updateModifyItem(Event event, int oldPos, int clickedPos,
			boolean isDelete) {
		Comparator<Event> comparator = new Comparator<Event>() {

			@Override
			public int compare(Event lhs, Event rhs) {
				// TODO Auto-generated method stub
				if (lhs != null && rhs != null) {
					if (lhs.getType() != rhs.getType()) {
						return lhs.getType() - rhs.getType();
					} else {
						return lhs.getStart().compareTo(rhs.getStart());
					}
				}
				return 0;
			}
		};
		if (clickedPos == 1) {
			String[] params = new String[10];
			params[0] = "1";
			params[1] = event.getTitle();
			params[2] = event.getStart();
			params[3] = event.getEnd();
			params[4] = event.getPos();
			params[5] = event.getSeat() + "";
			params[7] = event.getNote();
			params[8] = event.getType() + "";
			mDataList.add(event);
			new EventTask().execute(params);
		} else if (clickedPos == 2) {
			String[] params = new String[10];
			params[0] = "2";
			params[1] = event.getTitle();
			params[2] = event.getStart();
			params[3] = event.getEnd();
			params[4] = event.getPos();
			params[5] = event.getSeat() + "";
			params[7] = event.getNote();
			params[8] = event.getType() + "";
			mDataList.add(event);
			new EventTask().execute(params);
		} else if (clickedPos == 3) {
			if (!isDelete) {
				String[] params = new String[12];
				params[0] = "3";
				params[1] = mDataList.get(oldPos).getTitle();
				params[2] = mDataList.get(oldPos).getStart();
				params[3] = event.getTitle();
				params[4] = event.getStart();
				params[5] = event.getEnd();
				params[6] = event.getPos();
				params[7] = event.getSeat() + "";
				params[9] = event.getNote();
				params[10] = event.getType() + "";
				mDataList.get(oldPos).setTitle(params[3]);
				mDataList.get(oldPos).setStart(params[4]);
				mDataList.get(oldPos).setEnd(params[5]);
				mDataList.get(oldPos).setPos(params[6]);
				mDataList.get(oldPos).setSeat(event.getSeat());
				mDataList.get(oldPos).setType(event.getType());
				mDataList.get(oldPos).setNote(params[9]);
				new EventTask().execute(params);
			} else {
				String[] params = new String[5];
				params[0] = "5";
				params[1] = mDataList.get(oldPos).getTitle();
				params[2] = mDataList.get(oldPos).getStart();
				params[4] = mDataList.get(oldPos).getNote();
				mDataList.remove(oldPos);
				new EventTask().execute(params);
			}
		} else if (clickedPos == 4) {
			if (!isDelete) {
				String[] params = new String[12];
				params[0] = "3";
				params[1] = mDataList.get(oldPos).getTitle();
				params[2] = mDataList.get(oldPos).getStart();
				params[3] = event.getTitle();
				params[4] = event.getStart();
				params[5] = event.getEnd();
				params[6] = event.getPos();
				params[7] = event.getSeat() + "";
				params[9] = event.getNote();
				params[10] = event.getType() + "";
				mDataList.get(oldPos).setTitle(params[3]);
				mDataList.get(oldPos).setStart(params[4]);
				mDataList.get(oldPos).setEnd(params[5]);
				mDataList.get(oldPos).setPos(params[6]);
				mDataList.get(oldPos).setSeat(event.getSeat());
				mDataList.get(oldPos).setType(event.getType());
				mDataList.get(oldPos).setNote(params[9]);
				new EventTask().execute(params);
			} else {
				String[] params = new String[5];
				params[0] = "6";
				params[1] = mDataList.get(oldPos).getTitle();
				params[2] = mDataList.get(oldPos).getStart();
				params[4] = mDataList.get(oldPos).getNote();
				mDataList.remove(oldPos);
				new EventTask().execute(params);
			}
		}
		Collections.sort(mDataList, comparator);
	}

	class TimeNetTask extends AsyncTask<String, Void, Bitmap> {

		private int mKind;

		@Override
		protected Bitmap doInBackground(String... params) {
			// TODO Auto-generated method stub
			Bitmap captcha = null;
			mKind = Integer.parseInt(params[0]);
			if (WebSite.isNetworkAvailable(getActivity())) {
				switch (mKind) {
				case 0:
					mSpider = new LoginSpider(getActivity());
					if (mSpider.getLoginCookies() == true) {
						captcha = mSpider.grabCAPTCHA();
					}
					break;
				case 1:
					captcha = mSpider.grabCAPTCHA();
					break;
				case 2:
					if (true == mSpider.login(params[1], params[2], params[3])) {
						if (true == mSpider.loginAdminSys()) {
							mSpider.getExamTimeFromAdminSys();
						}
					}
					Resources res = getResources();
					captcha = BitmapFactory.decodeResource(res,
							R.drawable.ic_launcher);
					break;
				}
			}
			return captcha;
		}

		@Override
		protected void onPostExecute(final Bitmap result) {
			mSwipeRefreshLayout.setRefreshing(false);
			if (result != null) {
				switch (mKind) {
				case 0:
					Dialog.Builder builder = new SimpleDialog.Builder(
							R.style.SimpleDialogLight) {
						@Override
						protected void onBuildDone(Dialog dialog) {
							dialog.layoutParams(
									ViewGroup.LayoutParams.MATCH_PARENT,
									ViewGroup.LayoutParams.WRAP_CONTENT);
							ImageView captcha = (ImageView) dialog
									.findViewById(R.id.score_captcha);
							captcha.setImageBitmap(result);
						}

						@Override
						public void onPositiveActionClicked(
								DialogFragment fragment) {
							EditText ediText = (EditText) fragment.getDialog()
									.findViewById(R.id.score_input);
							String[] params = new String[6];
							params[0] = "2";
							SharedPreferences preferences = getActivity()
									.getSharedPreferences("Puma",
											Context.MODE_PRIVATE);
							params[1] = preferences.getString("number", "");
							params[2] = preferences.getString("psw", "");
							params[3] = ediText.getText().toString().trim();
							new TimeNetTask().execute(params);
							mSwipeRefreshLayout.setRefreshing(true);
							super.onPositiveActionClicked(fragment);
						}

						@Override
						public void onNegativeActionClicked(
								DialogFragment fragment) {
							super.onNegativeActionClicked(fragment);
						}
					};
					builder.title("输入验证码").positiveAction("确定")
							.negativeAction("取消")
							.contentView(R.layout.dialog_input_captcha);
					DialogFragment fragment = DialogFragment
							.newInstance(builder);
					fragment.show(getChildFragmentManager(), "");
					break;
				case 2:
					mSwipeRefreshLayout.setRefreshing(true);
					String[] params = new String[3];
					params[0] = "0";
					new EventTask().execute(params);
					break;
				}
			} else {
				Toast.makeText(getActivity(), "确认网络连接后，请重试", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	class EventTask extends AsyncTask<String, Void, ArrayList<Event>> {

		private int mMode;

		@Override
		protected ArrayList<Event> doInBackground(String... params) {
			// TODO Auto-generated method stub
			ArrayList<Event> result = new ArrayList<Event>();

			mMode = Integer.parseInt(params[0]);
			DatabaseHelper helper = new DatabaseHelper(getActivity(),
					"DIDAREN.db", null, 1);
			SQLiteDatabase db = helper.getWritableDatabase();
			switch (mMode) {
			case 0:
				Cursor cursor = db.rawQuery(
						"select * from EventList order by type,start", null);
				if (cursor.moveToFirst()) {
					do {
						Event temp = new Event();
						temp.setTitle(cursor.getString(cursor
								.getColumnIndex("title")));
						temp.setStart(cursor.getString(cursor
								.getColumnIndex("start")));
						temp.setEnd(cursor.getString(cursor
								.getColumnIndex("end")));
						temp.setPos(cursor.getString(cursor
								.getColumnIndex("pos")));
						temp.setSeat(cursor.getInt(cursor
								.getColumnIndex("seat")));
						temp.setType(cursor.getInt(cursor
								.getColumnIndex("type")));
						temp.setNote(cursor.getString(cursor
								.getColumnIndex("note")));
						result.add(temp);
					} while (cursor.moveToNext());
				}
				cursor.close();
				break;
			case 1:
				ContentValues values = new ContentValues();
				values.put("title", params[1]);
				values.put("start", params[2]);
				values.put("end", params[3]);
				values.put("pos", params[4]);
				values.put("seat", params[5]);
				values.put("note", params[7]);
				values.put("type", params[8]);
				db.insert("EventList", null, values);
				break;
			case 2:
				ContentValues values2 = new ContentValues();
				values2.put("title", params[1]);
				values2.put("start", params[2]);
				values2.put("end", params[3]);
				values2.put("pos", params[4]);
				values2.put("seat", params[5]);
				values2.put("note", params[7]);
				values2.put("type", params[8]);
				db.insert("EventList", null, values2);
				break;
			case 3:
				ContentValues values3 = new ContentValues();
				values3.put("title", params[3]);
				values3.put("start", params[4]);
				values3.put("end", params[5]);
				values3.put("pos", params[6]);
				values3.put("seat", params[7]);
				values3.put("note", params[9]);
				values3.put("type", params[10]);
				db.update("EventList", values3, "title=? and start=?",
						new String[] { params[1], params[2] });
				break;
			case 4:
				ContentValues values4 = new ContentValues();
				values4.put("title", params[3]);
				values4.put("start", params[4]);
				values4.put("end", params[5]);
				values4.put("pos", params[6]);
				values4.put("seat", params[7]);
				values4.put("note", params[9]);
				values4.put("type", params[10]);
				db.update("EventList", values4, "title=? and start=?",
						new String[] { params[1], params[2] });
				break;
			case 5:
				db.delete("EventList", "title=? and start=?", new String[] {
						params[1], params[2] });
				break;
			case 6:
				db.delete("EventList", "title=? and start=?", new String[] {
						params[1], params[2] });
				break;
			}
			db.close();

			return result;
		}

		@Override
		protected void onPostExecute(ArrayList<Event> result) {
			if (mMode == 0) {
				if(mSwipeRefreshLayout.isRefreshing() == true) {
					mSwipeRefreshLayout.setRefreshing(false);
					Toast.makeText(getActivity(), "考试时间还未公布T.T", Toast.LENGTH_SHORT).show();
				}
				mDataList.removeAll(mDataList);
				mDataList.addAll(result);
				mAdapter.notifyDataSetChanged();
			}
		}
	}
}
