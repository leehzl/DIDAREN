package com.sparke.modules;

import com.example.material.R;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.widget.FloatingActionButton;
import com.rey.material.widget.Spinner;
import com.sparke.model.Event;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;

public class FragmentSchool extends Fragment implements OnClickListener {

	private static final String SELECTED_POSITION = "selected_menu_position";

	private FloatingActionButton mFab;

	private FragmentSchoolTime mFragmentSchoolTime;
	private FragmentSchoolExam mFragmentSchoolExam;
	private FragmentSchoolCourse mFragmentSchoolCourse;

	private LinearLayout mSchoolTimeBtn;
	private LinearLayout mSchoolCourseBtn;
	private LinearLayout mSchoolExamBtn;

	private FragmentManager mFragmentManager;

	private int mPostion = 1;

	public FragmentSchool() {
	}

	public static FragmentSchool newInstance(int number) {
		FragmentSchool fragment = new FragmentSchool();
		Bundle bundle = new Bundle();
		bundle.putInt(SELECTED_POSITION, number);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		int n = getArguments().getInt(SELECTED_POSITION);
		((MainActivity) activity).onSectionAttached(n);
		switch (n) {
		case 7:
			mPostion = 2;
			break;
		case 8:
			mPostion = 3;
			break;
		case 9:
			mPostion = 1;
			break;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null)
			return null;
		mFragmentManager = getChildFragmentManager();
		View view = inflater
				.inflate(R.layout.fragment_school, container, false);
		mSchoolTimeBtn = (LinearLayout) view.findViewById(R.id.school_time);
		mSchoolCourseBtn = (LinearLayout) view.findViewById(R.id.school_course);
		mSchoolExamBtn = (LinearLayout) view.findViewById(R.id.school_exam);

		mFab = (FloatingActionButton) view.findViewById(R.id.fab_add);
		mFab.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Dialog.Builder builder = null;
				switch (mPostion) {
				case 1:
					builder = new SimpleDialog.Builder(
							R.style.SimpleDialogLight) {
						@Override
						public void onPositiveActionClicked(
								DialogFragment fragment) {
							if (getSelectedValue().equals("考试时间")) {
								ActivityModifySchoolTime
										.startActivityNewsContent(
												getActivity(), 1);
							} else if (getSelectedValue().equals("待办事项")) {
								ActivityModifySchoolTime
										.startActivityNewsContent(
												getActivity(), 2);
							}
							super.onPositiveActionClicked(fragment);
						}

						@Override
						public void onNegativeActionClicked(
								DialogFragment fragment) {
							Toast.makeText(getActivity(), "Cancelled",
									Toast.LENGTH_SHORT).show();
							super.onNegativeActionClicked(fragment);
						}
					};
					((SimpleDialog.Builder) builder)
							.items(new String[] { "考试时间", "待办事项" }, 0)
							.title("添加内容").positiveAction("确定")
							.negativeAction("取消");
					DialogFragment fragment = DialogFragment
							.newInstance(builder);
					fragment.show(getChildFragmentManager(), null);
					break;
				case 3:
					builder = new SimpleDialog.Builder(
							R.style.SimpleDialogLight) {

						@Override
						protected void onBuildDone(Dialog dialog) {
							dialog.layoutParams(
									ViewGroup.LayoutParams.MATCH_PARENT,
									ViewGroup.LayoutParams.WRAP_CONTENT);
							Spinner yearSpinner = (Spinner) dialog
									.findViewById(R.id.school_score_year);
							Spinner termSpinner = (Spinner) dialog
									.findViewById(R.id.school_score_term);
							SharedPreferences preferences = getActivity()
									.getSharedPreferences("course_year",
											Context.MODE_PRIVATE);
							String[] years = new String[] {
									preferences.getString("first", ""),
									preferences.getString("second", ""),
									preferences.getString("third", ""),
									preferences.getString("forth", ""),
									preferences.getString("fifth", "") };
							String[] terms = new String[]{"全部", "1", "2"};
							
							ArrayAdapter<String> termAdapter = new ArrayAdapter<>(getActivity(), R.layout.row_spn, terms);
							termAdapter.setDropDownViewResource(R.layout.row_spn_dropdown);
							termSpinner.setAdapter(termAdapter);
							
							ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(
									getActivity(), R.layout.row_spn, years);
							yearAdapter.setDropDownViewResource(R.layout.row_spn_dropdown);
							yearSpinner.setAdapter(yearAdapter);
						}

						@Override
						public void onPositiveActionClicked(
								DialogFragment fragment) {
							Spinner yearSpinner = (Spinner) fragment.getDialog().findViewById(R.id.school_score_year);
							Spinner termSpinner = (Spinner) fragment.getDialog().findViewById(R.id.school_score_term);
							String year = yearSpinner.getSelectedItem().toString();
							String term = termSpinner.getSelectedItem().toString();
							mFragmentSchoolExam.queryScore(year, term);
							super.onPositiveActionClicked(fragment);
						}

						@Override
						public void onNegativeActionClicked(
								DialogFragment fragment) {
							super.onNegativeActionClicked(fragment);
						}
					};

					builder.title("选择学期").positiveAction("确定")
							.negativeAction("取消")
							.contentView(R.layout.dialog_select_term);

					DialogFragment fragment2 = DialogFragment
							.newInstance(builder);
					fragment2.show(getChildFragmentManager(), null);

					break;
				}

			}
		});

		mSchoolTimeBtn.setOnClickListener(this);
		mSchoolCourseBtn.setOnClickListener(this);
		mSchoolExamBtn.setOnClickListener(this);
		setPosition(mPostion);

		return view;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.school_time:
			setPosition(1);
			break;
		case R.id.school_course:
			setPosition(2);
			break;
		case R.id.school_exam:
			setPosition(3);
			break;
		default:
			break;
		}
	}

	public int getPostion() {
		return mPostion;
	}

	public void setPosition(int pos) {
		FragmentTransaction transaction = mFragmentManager.beginTransaction();
		mPostion = pos;
		clearChoice();
		hideFragments(transaction);
		switch (mPostion) {
		case 1:
			if(mFab.getVisibility() == View.GONE) {
				mFab.setVisibility(View.VISIBLE);
			}
			mSchoolTimeBtn.setBackgroundResource(R.color.danny_background_dark);
			if (mFragmentSchoolTime == null) {
				mFragmentSchoolTime = FragmentSchoolTime.newInstance();
				transaction.add(R.id.school, mFragmentSchoolTime);
			} else {
				transaction.show(mFragmentSchoolTime);
			}
			break;
		case 2:
			if(mFab.getVisibility() == View.VISIBLE) {
				mFab.setVisibility(View.GONE);
			}
			mSchoolCourseBtn
					.setBackgroundResource(R.color.danny_background_dark);
			if (mFragmentSchoolCourse == null) {
				mFragmentSchoolCourse = FragmentSchoolCourse.newInstance(0);
				transaction.add(R.id.school, mFragmentSchoolCourse);
			} else {
				transaction.show(mFragmentSchoolCourse);
			}
			break;
		case 3:
			if(mFab.getVisibility() == View.GONE) {
				mFab.setVisibility(View.VISIBLE);
			}
			mSchoolExamBtn.setBackgroundResource(R.color.danny_background_dark);
			if (mFragmentSchoolExam == null) {
				mFragmentSchoolExam = FragmentSchoolExam.newInstance(0);
				transaction.add(R.id.school, mFragmentSchoolExam);
			} else {
				transaction.show(mFragmentSchoolExam);
			}
			break;
		}
		transaction.commit();
	}

	private void hideFragments(FragmentTransaction transaction) {
		if (mFragmentSchoolCourse != null) {
			transaction.hide(mFragmentSchoolCourse);
		}
		if (mFragmentSchoolExam != null) {
			transaction.hide(mFragmentSchoolExam);
		}
		if (mFragmentSchoolTime != null) {
			transaction.hide(mFragmentSchoolTime);
		}
	}

	private void clearChoice() {
		mSchoolCourseBtn.setBackgroundResource(R.color.danny_background);
		mSchoolExamBtn.setBackgroundResource(R.color.danny_background);
		mSchoolTimeBtn.setBackgroundResource(R.color.danny_background);
	}

	public void passModifyInfo(Event event, int oldPos, int clickedPos,
			boolean isDelete) {
		mFragmentSchoolTime.updateModifyItem(event, oldPos, clickedPos,
				isDelete);
	}

}
