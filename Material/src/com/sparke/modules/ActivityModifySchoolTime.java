package com.sparke.modules;

import java.text.SimpleDateFormat;

import com.example.material.R;
import com.rey.material.app.DatePickerDialog;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.app.TimePickerDialog;
import com.sparke.model.Event;
import com.sparke.model.EventAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityModifySchoolTime extends FragmentActivity {

	public static final String EVENT = "event";
	/**
	 * 1代表添加考试时间 2代表添加待办事项 3代表修改考试时间 4代表修改待办事项
	 */
	public static final String CLICK_POSITION = "click_position";
	public static final String SELECT_POSITION = "select_position";

	private TextView mActionBarTitle;
	private TextView mDone;
	private TextView mTitleTip;
	private EditText mTitle;
	private TextView mStartTip;
	private TextView mStart;
	private TextView mEndTip;
	private TextView mEnd;
	private TextView mPosTip;
	private EditText mPos;
	private TextView mSeatTip;
	private EditText mSeat;
	private EditText mNote;
	
	private FrameLayout mDelete;
	private CardView mDeleteLayout;
	private TextView mDeleteText;
	
	private String mStartString;

	private LinearLayout mEndLayout;
	private LinearLayout mSeatLayout;

	private Event mEvent;
	private int mClickedPosition;
	private int mSelectedPosition;

	public static void startActivityNewsContent(Context startingActivity,
			Event event, int selectedPosition, int clickedPosition) {
		Intent intent = new Intent(startingActivity,
				ActivityModifySchoolTime.class);
		intent.putExtra(EVENT, event);
		intent.putExtra(CLICK_POSITION, clickedPosition);
		intent.putExtra(SELECT_POSITION, selectedPosition);
		((Activity) startingActivity).startActivityForResult(intent,
				clickedPosition);
	}

	public static void startActivityNewsContent(Context startingActivity,
			int position) {
		Intent intent = new Intent(startingActivity,
				ActivityModifySchoolTime.class);
		intent.putExtra(CLICK_POSITION, position);
		((Activity) startingActivity).startActivityForResult(intent, position);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_school_time);

		mActionBarTitle = (TextView) findViewById(R.id.modify_school_time_back);
		mDone = (TextView) findViewById(R.id.modify_school_time_done);
		mEndLayout = (LinearLayout) findViewById(R.id.modify_school_time_end_layout);
		mSeatLayout = (LinearLayout) findViewById(R.id.modify_school_time_seat_layout);

		mTitle = (EditText) findViewById(R.id.modify_school_time_title);
		mTitleTip = (TextView) findViewById(R.id.modify_school_time_title_tip);

		mStartTip = (TextView) findViewById(R.id.modify_school_time_start_tip);
		mStart = (TextView) findViewById(R.id.modify_school_time_start);

		mEndTip = (TextView) findViewById(R.id.modify_school_time_end_tip);
		mEnd = (TextView) findViewById(R.id.modify_school_time_end);

		mPosTip = (TextView) findViewById(R.id.modify_school_time_pos_tip);
		mPos = (EditText) findViewById(R.id.modify_school_time_pos);

		mSeatTip = (TextView) findViewById(R.id.modify_school_time_seat_tip);
		mSeat = (EditText) findViewById(R.id.modify_school_time_seat);

		mNote = (EditText) findViewById(R.id.modify_school_time_note);
		mDelete = (FrameLayout) findViewById(R.id.modify_school_time_delete);
		mDeleteLayout = (CardView) findViewById(R.id.modify_school_time_delete_layout);
		mDeleteText = (TextView) findViewById(R.id.modify_school_time_delete_text);

		mEvent = (Event) getIntent().getSerializableExtra(EVENT);
		mClickedPosition = getIntent().getIntExtra(CLICK_POSITION, 0);
		mSelectedPosition = getIntent().getIntExtra(SELECT_POSITION, -1);

		mActionBarTitle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Dialog.Builder builder = new SimpleDialog.Builder(
						R.style.SimpleDialogLight) {
					@Override
					public void onPositiveActionClicked(DialogFragment fragment) {
						setResult(RESULT_CANCELED);
						ActivityModifySchoolTime.this.finish();
						super.onPositiveActionClicked(fragment);
					}

					@Override
					public void onNegativeActionClicked(DialogFragment fragment) {
						Toast.makeText(ActivityModifySchoolTime.this,
								"Canceled", Toast.LENGTH_SHORT).show();
						super.onNegativeActionClicked(fragment);
					}
				};
				builder.title("放弃编辑？").positiveAction("确定")
						.negativeAction("取消");
				DialogFragment fragment = DialogFragment.newInstance(builder);
				fragment.show(getSupportFragmentManager(), null);
			}
		});

		mDone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String title = ActivityModifySchoolTime.this.mTitle.getText()
						.toString();
				String start = mStart.getText().toString();

				if (title == null || title.isEmpty()) {
					Toast.makeText(ActivityModifySchoolTime.this, "名称不能为空",
							Toast.LENGTH_SHORT).show();
				} else if (start == null || start.isEmpty()) {
					Toast.makeText(ActivityModifySchoolTime.this, "时间不能为空",
							Toast.LENGTH_SHORT).show();
				} else {
					Event event = new Event();
					event.setTitle(title);
					String[] strings = start.split(" ");
					event.setStart(strings[0] + "-" + strings[2]);

					String str;
					if (mClickedPosition == 1 || mClickedPosition == 3) {
						str = mEnd.getText().toString();
						if (str != null && !str.isEmpty()) {
							event.setEnd(str);
						} else {
							event.setEnd("");
						}

						str = mSeat.getText().toString();
						if (str != null && !str.isEmpty()) {
							event.setSeat(Integer.parseInt(str));
						} else {
							event.setSeat(-1);
						}
						
						event.setType(1);
					} else if(mClickedPosition == 2 || mClickedPosition == 4) {
						event.setEnd("");
						event.setSeat(-1);
						event.setType(2);
					}

					str = mPos.getText().toString();
					if (str != null && !str.isEmpty()) {
						event.setPos(str);
					} else {
						event.setPos("");
					}

					str = mNote.getText().toString();
					if (str != null && !str.isEmpty()) {
						event.setNote(str);
					} else {
						event.setNote("");
					}

					Intent intent = new Intent();
					intent.putExtra("DELETE", false);
					intent.putExtra("EVENT", event);
					intent.putExtra("OLDPOSITION", mSelectedPosition);
					setResult(RESULT_OK, intent);
					ActivityModifySchoolTime.this.finish();
				}
			}
		});

		mStart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Dialog.Builder builder = new DatePickerDialog.Builder(
						R.style.DateSimpleDialogLight) {
					@Override
					public void onPositiveActionClicked(DialogFragment fragment) {
						DatePickerDialog dialog = (DatePickerDialog) fragment
								.getDialog();
						final String date = dialog.getFormattedDate(new SimpleDateFormat("yyyy年MM月dd日"));


						Dialog.Builder timeBuilder = new TimePickerDialog.Builder(
								R.style.TimeSimpleDialogLight, 24, 00) {
							@Override
							public void onPositiveActionClicked(
									DialogFragment fragment) {
								TimePickerDialog dialog = (TimePickerDialog) fragment
										.getDialog();
								String time = dialog.getFormattedTime(new SimpleDateFormat("HH:mm:ss"));
								time = time.substring(0, time.lastIndexOf(":"));
								time = date + "-" + time;
								mStartString = time;
								mStart.setText(EventAdapter.getTimeSting(time, null));
								super.onPositiveActionClicked(fragment);
							}

							@Override
							public void onNegativeActionClicked(
									DialogFragment fragment) {
								Toast.makeText(ActivityModifySchoolTime.this,
										"Cancelled", Toast.LENGTH_SHORT).show();
								super.onNegativeActionClicked(fragment);
							}
						};
						timeBuilder.positiveAction("确定").negativeAction("取消");
						DialogFragment timeFragment = DialogFragment
								.newInstance(timeBuilder);
						timeFragment.show(getSupportFragmentManager(),
								"start_time");

						super.onPositiveActionClicked(fragment);
					}

					@Override
					public void onNegativeActionClicked(DialogFragment fragment) {
						Toast.makeText(ActivityModifySchoolTime.this,
								"Cancelled", Toast.LENGTH_SHORT).show();
						super.onNegativeActionClicked(fragment);
					}
				};
				builder.positiveAction("确定").negativeAction("取消");
				DialogFragment fragment = DialogFragment.newInstance(builder);
				fragment.show(getSupportFragmentManager(), "start");

			}
		});

		mEnd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Dialog.Builder timeBuilder = new TimePickerDialog.Builder(
						R.style.TimeSimpleDialogLight, 24, 00) {
					@Override
					public void onPositiveActionClicked(DialogFragment fragment) {
						TimePickerDialog dialog = (TimePickerDialog) fragment
								.getDialog();
						String time = dialog.getFormattedTime(new SimpleDateFormat("HH:mm:ss"));
						time = time.substring(0, time.lastIndexOf(":"));
						mEnd.setText(time);
						super.onPositiveActionClicked(fragment);
					}

					@Override
					public void onNegativeActionClicked(DialogFragment fragment) {
						Toast.makeText(ActivityModifySchoolTime.this,
								"Cancelled", Toast.LENGTH_SHORT).show();
						super.onNegativeActionClicked(fragment);
					}
				};
				timeBuilder.positiveAction("确定").negativeAction("取消");
				DialogFragment timeFragment = DialogFragment
						.newInstance(timeBuilder);
				timeFragment.show(getSupportFragmentManager(), "end_time");

			}
		});

		String[] items = new String[] { "关闭", "提前5分钟", "提前半小时", "提前1小时", "提前5小时", "提前1天" };
		ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
				R.layout.row_spn, items);
		adapter.setDropDownViewResource(R.layout.row_spn_dropdown);

		mDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("DELETE", true);
				intent.putExtra("EVENT", new Event());
				intent.putExtra("OLDPOSITION", mSelectedPosition);
				setResult(RESULT_OK, intent);
				ActivityModifySchoolTime.this.finish();
			}
		});
		classify(mClickedPosition);
	}

	private void classify(int position) {
		switch (position) {
		case 1:
			initAddExam();
			break;
		case 2:
			initAddEvent();
			break;
		case 3:
			initModifyExam();
			break;
		case 4:
			initModifyEvent();
			break;
		}
	}

	private void initAddExam() {
		mDeleteLayout.setVisibility(View.GONE);
		mActionBarTitle.setText(R.string.school_time);

		mTitleTip.setText(R.string.school_time_title);
		mTitle.setHint(R.string.school_time_title_hint);

		mStartTip.setText(R.string.school_time_start);
		mStart.setHint(R.string.school_time_start_hint);

		mEndTip.setText(R.string.school_time_end);
		mEnd.setHint(R.string.school_time_end_hint);

		mPosTip.setText(R.string.school_time_pos);
		mPos.setHint(R.string.school_time_pos_hint);

		mSeatTip.setText(R.string.school_time_seat);
		mSeat.setHint(R.string.school_time_seat_hint);

	}

	private void initAddEvent() {
		mDeleteLayout.setVisibility(View.GONE);
		mActionBarTitle.setText(R.string.school_time_event);

		mTitleTip.setText(R.string.school_time_event);
		mTitle.setHint(R.string.school_time_event_hint);

		mStartTip.setText(R.string.school_time_event_start);
		mStart.setHint(R.string.school_time_event_start_hint);

		mEndLayout.setVisibility(View.GONE);

		mPosTip.setText(R.string.school_time_event_pos);
		mPos.setHint(R.string.school_time_event_pos_hint);

		mSeatLayout.setVisibility(View.GONE);
	}

	private void initModifyExam() {
		mActionBarTitle.setText(R.string.school_time);
		mDeleteText.setText(R.string.school_time_delete1);
		
		mTitleTip.setText(R.string.school_time_title);
		mTitle.setHint(R.string.school_time_title_hint);
		
		mStartTip.setText(R.string.school_time_start);
		mStart.setHint(R.string.school_time_start_hint);

		mEndTip.setText(R.string.school_time_end);
		mEnd.setHint(R.string.school_time_end_hint);

		mPosTip.setText(R.string.school_time_pos);
		mPos.setHint(R.string.school_time_pos_hint);

		mSeatTip.setText(R.string.school_time_seat);
		mSeat.setHint(R.string.school_time_seat_hint);


		String str = mEvent.getTitle();
		if (str != null && !str.isEmpty()) {
			mTitle.setText(str);
			mTitle.setSelection(str.length());
		}

		str = mEvent.getStart();
		if (str != null && !str.isEmpty()) {
			mStart.setText(EventAdapter.getTimeSting(str, null));
		}

		str = mEvent.getEnd();
		if (str != null && !str.isEmpty()) {
			mEnd.setText(str);
		}

		str = mEvent.getPos();
		if (str != null && !str.isEmpty()) {
			mPos.setText(str);
			mPos.setSelection(str.length());
		}

		str = mEvent.getSeat() + "";
		if (str != null && !str.isEmpty() && !str.equals("-1")) {
			mSeat.setText(str);
			mSeat.setSelection(str.length());
		}

		str = mEvent.getNote();
		if (str != null && !str.isEmpty()) {
			mNote.setText(str);
			mNote.setSelection(str.length());
		}

	}

	private void initModifyEvent() {
		mActionBarTitle.setText(R.string.school_time_event);
		mDeleteText.setText(R.string.school_time_delete2);
		
		mTitleTip.setText(R.string.school_time_event);
		mTitle.setHint(R.string.school_time_event_hint);

		mStartTip.setText(R.string.school_time_event_start);
		mStart.setHint(R.string.school_time_event_start_hint);

		mEndLayout.setVisibility(View.GONE);

		mPosTip.setText(R.string.school_time_event_pos);
		mPos.setHint(R.string.school_time_event_pos_hint);

		mSeatLayout.setVisibility(View.GONE);

		String str = mEvent.getTitle();
		if (str != null && !str.isEmpty()) {
			mTitle.setText(str);
			mTitle.setSelection(str.length());
		}

		str = mEvent.getStart();
		if (str != null && !str.isEmpty()) {
			mStart.setText(EventAdapter.getTimeSting(str, null));
		}

		str = mEvent.getPos();
		if (str != null && !str.isEmpty()) {
			mPos.setText(str);
			mPos.setSelection(str.length());
		}

		str = mEvent.getNote();
		if (str != null && !str.isEmpty()) {
			mNote.setText(str);
			mNote.setSelection(str.length());
		}
	}

}
