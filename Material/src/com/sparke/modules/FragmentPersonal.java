package com.sparke.modules;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.example.material.R;
import com.sparke.model.PersonnalItem;
import com.sparke.model.PersonnalItemAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;

public class FragmentPersonal extends Fragment {

	private static final String SELECTED_POSITION = "selected_menu_position";

	private List<PersonnalItem> mPersonnalList;

	private FrameLayout mLoginOut;

	public FragmentPersonal() {
	}

	public static FragmentPersonal newInstance(int number) {
		FragmentPersonal fragment = new FragmentPersonal();
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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPersonnalList = getPersonnalItems();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null)
			return null;
		View view = inflater.inflate(R.layout.fragment_personnal, container,
				false);
		mLoginOut = (FrameLayout) view.findViewById(R.id.login_out);
		mLoginOut.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SharedPreferences preferences = getActivity()
						.getSharedPreferences("Puma", Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = preferences.edit();
				Map<String, ?> map = preferences.getAll();
				Set<String> headers = map.keySet();
				for (String key : headers) {
					editor.remove(key);
				}
				editor.commit();
				preferences = getActivity().getSharedPreferences("course_year",
						Context.MODE_PRIVATE);
				editor = preferences.edit();
				map = preferences.getAll();
				headers = map.keySet();
				for (String key : headers) {
					editor.remove(key);
				}
				editor.commit();
				preferences = getActivity().getSharedPreferences("course",
						Context.MODE_PRIVATE);
				editor = preferences.edit();
				map = preferences.getAll();
				headers = map.keySet();
				for (String key : headers) {
					editor.remove(key);
				}
				editor.commit();

				((MainActivity) getActivity()).resetMenuUserText();
				getActivity().deleteDatabase("DIDAREN.db");
				FragmentManager fragmentManager = getActivity()
						.getSupportFragmentManager();
				FragmentTransaction transaction = fragmentManager
						.beginTransaction();
				transaction.replace(R.id.container,
						FragmentLogin.newInstance(1), "Login");
				transaction.commit();

			}
		});

		PersonnalItemAdapter adapter = new PersonnalItemAdapter(getContext(),
				R.layout.list_personnal_item, mPersonnalList);
		ListView listView = (ListView) view.findViewById(R.id.list_personnal);
		listView.setAdapter(adapter);
		
		SharedPreferences pref = getContext().getSharedPreferences("Puma",
				Context.MODE_PRIVATE);
		String isStudent = pref.getString("isStudent", "true");
		((MainActivity) getActivity()).updateMenu(isStudent);
		return view;
	}

	/**
	 * 获得碎片所在的上下文
	 * 
	 * @return
	 */
	private Context getContext() {
		return getActivity().getApplicationContext();
	}

	/**
	 * 获得个人信息
	 * 
	 * @return
	 */
	private List<PersonnalItem> getPersonnalItems() {
		List<PersonnalItem> items = new ArrayList<PersonnalItem>();
		SharedPreferences pref = getContext().getSharedPreferences("Puma",
				Context.MODE_PRIVATE);
		String isStudent = pref.getString("isStudent", "true");
		
		Map<String, String> map = (Map<String, String>) pref.getAll();
	
		if (isStudent.equals("true")) {
			map.remove("psw");
			map.remove("isStudent");
			map.remove("isFirst");
			map.remove("isTip");
			Set<String> keys = map.keySet();
			for (String key : keys) {
				PersonnalItem item = new PersonnalItem(exchange(key),
						map.get(key));
				items.add(item);
			}
		} else {
			map.remove("psw");
			map.remove("isStudent");
			map.remove("room");
			map.remove("year");
			map.remove("class");
			map.remove("isFirst");
			map.remove("isTip");
			Set<String> keys = map.keySet();
			for (String key : keys) {
				PersonnalItem item = new PersonnalItem(teacherExchange(key),
						map.get(key));
				items.add(item);
			}
		}
		return items;
	}

	private String exchange(String key) {
		if (key.equals("number"))
			return "学号";
		if (key.equals("name"))
			return "姓名";
		if (key.equals("sex"))
			return "性别";
		if (key.equals("born"))
			return "出生日期";
		if (key.equals("home"))
			return "家乡";
		if (key.equals("college"))
			return "学院";
		if (key.equals("major"))
			return "专业";
		if (key.equals("class"))
			return "班级";
		if (key.equals("year"))
			return "入学年份";
		if (key.equals("room"))
			return "宿舍";
		return "";
	}

	private String teacherExchange(String key) {
		if (key.equals("number"))
			return "职工号";
		if (key.equals("name"))
			return "姓名";
		if (key.equals("sex"))
			return "性别";
		if (key.equals("born"))
			return "出生日期";
		if (key.equals("home"))
			return "职称";
		if (key.equals("college"))
			return "学院";
		if (key.equals("major"))
			return "学科方向";
		return "";
	}

}
