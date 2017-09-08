package com.sparke.modules;

import java.util.ArrayList;
import java.util.List;

import com.example.material.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentSchoolCourse extends Fragment {

	private static final String SELECTED_POSITION = "selected_menu_position";
	
	//选中课表弹出toast
	private Toast toast_course;
	
	//toast相关
	private LinearLayout.LayoutParams toast_textview_layout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
	

	// sharedpreferences
	private SharedPreferences pref;

	// Linearlayout
	private LinearLayout linearLayout_course_morning_first;
	private LinearLayout linearLayout_course_morning_second;
	private LinearLayout linearLayout_course_afternoon_first;
	private LinearLayout linearLayout_course_afternoon_second;
	private LinearLayout linearLayout_course_evening_first;
	private LinearLayout linearLayout_course_evening_second;

	// course格子的layout
	private LinearLayout.LayoutParams layout_course = new LinearLayout.LayoutParams(
			0, LinearLayout.LayoutParams.MATCH_PARENT, 2);

	// 获取sharedpreference的课程数据
	private String[] list_morning_first;
	private String[] list_morning_second;
	private String[] list_afternoon_first;
	private String[] list_afternoon_second;
	private String[] list_evening_first;
	private String[] list_evening_second;

	public static FragmentSchoolCourse newInstance(int number) {
		FragmentSchoolCourse fragment = new FragmentSchoolCourse();
		Bundle args = new Bundle();
		args.putInt(SELECTED_POSITION, number);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(
				SELECTED_POSITION));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_school_course, container, false);
		linearLayout_course_morning_first = (LinearLayout) view
				.findViewById(R.id.linearLayout_course_morning_first);
		linearLayout_course_morning_second = (LinearLayout) view
				.findViewById(R.id.linearLayout_course_morning_second);
		linearLayout_course_afternoon_first = (LinearLayout) view
				.findViewById(R.id.linearLayout_course_afternoon_first);
		linearLayout_course_afternoon_second = (LinearLayout) view
				.findViewById(R.id.linearLayout_course_afternoon_second);
		linearLayout_course_evening_first = (LinearLayout) view
				.findViewById(R.id.linearLayout_course_evening_first);
		linearLayout_course_evening_second = (LinearLayout) view
				.findViewById(R.id.linearLayout_course_evening_second);
		
		toast_course = new Toast(getContext());
		toast_textview_layout.setMargins(50, 16, 50, 4);

		layout_course.setMargins(1, 1, 1, 1);
		getSharedPreferencesData();

		createCourseListView();
		
		SharedPreferences preferences = getActivity().getSharedPreferences("Puma",
				Context.MODE_PRIVATE);
		String isFirst = preferences.getString("isTip", "0");
		if (isFirst.equals("0")) {
			Toast.makeText(getActivity(), "点击小绿块，可详细显示对应课程信息", Toast.LENGTH_LONG).show();
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString("isTip", "1");
			editor.commit();
		}
		return view;
	}

	private void createCourseListView() {
		// TODO Auto-generated method stub
			addCourseToLinearLayout(list_morning_first,
					linearLayout_course_morning_first);
			addCourseToLinearLayout(list_morning_second,
					linearLayout_course_morning_second);
			addCourseToLinearLayout(list_afternoon_first,
					linearLayout_course_afternoon_first);
			addCourseToLinearLayout(list_afternoon_second,
					linearLayout_course_afternoon_second);
			addCourseToLinearLayout(list_evening_first,
					linearLayout_course_evening_first);
			addCourseToLinearLayout(list_evening_second,
					linearLayout_course_evening_second);

	}

	private void addCourseToLinearLayout(String[] list_course,
			LinearLayout linearLayout_column) {
		// TODO Auto-generated method stub

		int space_index;

		String course_name_single;
		String course_style_single;
		String course_time_single;
		String course_teacher_single;
		String course_place_single;

		for (int i = 0; i < list_course.length; i++) {
			final List<String> course_name = new ArrayList<String>();
			final List<String> course_style = new ArrayList<String>();
			final List<String> course_time = new ArrayList<String>();
			final List<String> course_teacher = new ArrayList<String>();
			final List<String> course_place = new ArrayList<String>();
			TextView textView_course = new TextView(getContext());
			textView_course.setLayoutParams(layout_course);
			textView_course.setGravity(Gravity.CENTER);
			textView_course.setTextColor(android.graphics.Color.WHITE);
			if (list_course[i].length() != 1) {
				while (list_course[i].indexOf(" ") > 0) {
					// 课程名称
					space_index = list_course[i].indexOf(" ");
					course_name_single = list_course[i].substring(0,
							space_index);
					course_name.add(course_name_single);
					list_course[i] = list_course[i].replace(course_name_single
							+ " ", "");

					// 课程性质
					space_index = list_course[i].indexOf(" ");
					course_style_single = list_course[i].substring(0,
							space_index);
					course_style.add(course_style_single);
					// list_course[i] =
					// list_course[i].replace(course_style_single+" ", "");
					list_course[i] = list_course[i]
							.substring(course_style_single.length() + 1);

					// 课程时间
					space_index = list_course[i].indexOf(" ");
					course_time_single = list_course[i].substring(0,
							space_index);
					course_time.add(course_time_single);
					list_course[i] = list_course[i].replace(course_time_single
							+ " ", "");

					// 课程老师
					space_index = list_course[i].indexOf(" ");
					course_teacher_single = list_course[i].substring(0,
							space_index);
					course_teacher.add(course_teacher_single);
					list_course[i] = list_course[i].replace(
							course_teacher_single + " ", "");

					// 如果是最后一个
					if (list_course[i].indexOf(" ") == -1) {
						list_course[i] = list_course[i] + " ";
					}
					// 课程地点
					space_index = list_course[i].indexOf(" ");
					course_place_single = list_course[i].substring(0,
							space_index);
					course_place.add(course_place_single);
					list_course[i] = list_course[i].replace(course_place_single
							+ " ", "");

				}
				textView_course
						.setText(showTextView(course_name, course_place));
				textView_course.setClickable(true);
				textView_course.setBackgroundResource(R.color.danny_background);
				textView_course.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (toast_course.equals(null)) {
							toast_course = new Toast(getContext());
							LinearLayout linearLayout_toast = new LinearLayout(getContext());
							TextView textView_toast = new TextView(getContext());
							textView_toast.setText(showToastText(course_name, course_style, course_time, course_teacher, course_place));
							linearLayout_toast.addView(textView_toast);
							linearLayout_toast.setBackgroundResource(R.color.toast_color);
							textView_toast.setTextColor(getResources().getColor(R.color.danny_white));
							textView_toast.setTextSize(14);
							textView_toast.setLayoutParams(toast_textview_layout);
							toast_course.setView(linearLayout_toast);
							toast_course.setDuration(Toast.LENGTH_LONG);
							toast_course.show();
						}else {
							toast_course.cancel();
							toast_course = new Toast(getContext());
							LinearLayout linearLayout_toast = new LinearLayout(getContext());
							TextView textView_toast = new TextView(getContext());
							textView_toast.setText(showToastText(course_name, course_style, course_time, course_teacher, course_place));
							linearLayout_toast.addView(textView_toast);
							linearLayout_toast.setBackgroundResource(R.color.toast_color);
							textView_toast.setTextColor(getResources().getColor(R.color.danny_white));
							textView_toast.setTextSize(14);
							textView_toast.setLayoutParams(toast_textview_layout);
							toast_course.setView(linearLayout_toast);
							toast_course.setDuration(Toast.LENGTH_LONG);
							toast_course.show();
						}
					}
				});
			} else {
				textView_course.setText(list_course[i]);
			}
			linearLayout_column.addView(textView_course);

		}
	}

	protected String showToastText(List<String> course_name,
			List<String> course_style, List<String> course_time,
			List<String> course_teacher, List<String> course_place) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < course_name.size(); i++) {
			sb.append("名称:" + course_name.get(i) + "\n");
			sb.append("性质:" + course_style.get(i) + "\n");
			sb.append("时间:" + course_time.get(i) + "\n");
			sb.append("老师:" + course_teacher.get(i) + "\n");
			sb.append("地点:" + course_place.get(i) + "\n");
			sb.append("\n");
		}
		sb.replace(sb.length() - 2, sb.length() - 1, "");
		return sb.toString();
	}

	private String showTextView(List<String> course_name,
			List<String> course_place) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < course_name.size(); i++) {
			sb.append(course_name.get(i));
			sb.append("@");
			sb.append(course_place.get(i));
			sb.append("\n\n");
		}
		sb.replace(sb.length() - 2, sb.length() - 1, "");
		return sb.toString();
	}

	private void getSharedPreferencesData() {
		// TODO Auto-generated method stub
		String morning_first_string;
		String morning_second_string;
		String afternoon_first_string;
		String afternoon_second_string;
		String evening_first_string;
		String evening_second_string;

		pref = getContext().getSharedPreferences("course",
				Activity.MODE_PRIVATE);

		morning_first_string = pref.getString("course_morning_first", null);
		morning_second_string = pref.getString("course_morning_second", null);
		afternoon_first_string = pref.getString("course_afternoon_first", null);
		afternoon_second_string = pref.getString("course_afternoon_second",
				null);
		evening_first_string = pref.getString("course_evening_first", null);
		evening_second_string = pref.getString("course_evening_second", null);
		morning_first_string = morning_first_string.substring(1,
				morning_first_string.length() - 1);
		morning_second_string = morning_second_string.substring(1,
				morning_second_string.length() - 1);
		afternoon_first_string = afternoon_first_string.substring(1,
				afternoon_first_string.length() - 1);
		afternoon_second_string = afternoon_second_string.substring(1,
				afternoon_second_string.length() - 1);
		evening_first_string = evening_first_string.substring(1,
				evening_first_string.length() - 1);
		evening_second_string = evening_second_string.substring(1,
				evening_second_string.length() - 1);
		list_morning_first = morning_first_string.split(", ");
		list_morning_second = morning_second_string.split(", ");
		list_afternoon_first = afternoon_first_string.split(", ");
		list_afternoon_second = afternoon_second_string.split(", ");
		list_evening_first = evening_first_string.split(", ");
		list_evening_second = evening_second_string.split(", ");

	}

	/**
	 * 获得碎片所在的上下文
	 * 
	 * @return
	 */
	private Context getContext() {
		return getActivity().getApplicationContext();
	}
}