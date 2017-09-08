package com.sparke.modules;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dd.CircularProgressButton;
import com.example.material.R;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.sparke.web.util.ConnectPHP;

import com.rey.material.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentCheckNumber extends Fragment {

	private static final String ARG_SECTION_NUMBER = "section_number";

	// 控件
	private EditText editText_check_number_activity_name;
	private EditText editText_check_number_activity_password;
	private TextView textView_check_number_number_choose_date;
	private CircularProgressButton button_check_number_confirm;
	private FrameLayout mBack;

	// 选择日期
	private int select_year;
	private int select_month;
	private int select_day;

	// 传入服务器数据：活动名称，活动日期，活动密码
	private String activity_name;
	private String activity_date;
	private String activity_password;

	// url
	private String url = new String();

	// 三个数组存储签到人员名字，id，签到时间信息
	List<String> numNames = new ArrayList<String>();
	List<String> numIds = new ArrayList<String>();
	List<String> numTimes = new ArrayList<String>();

	// 线程执行标志位，用于：主线程等待子线程执行完成才能继续执行
	private static boolean THREAD_IS_FINISH = false;

	public static FragmentCheckNumber newInstance(int sectionNumber) {
		FragmentCheckNumber fragment = new FragmentCheckNumber();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public FragmentCheckNumber() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = null;

		// 界面
		rootView = inflater.inflate(R.layout.fragment_check_number, container,
				false);

		// 把时间初始化为当日日期，用于DatePickerDialog选择日期
		initCurrentDate();

		// 控件初始化
		editText_check_number_activity_name = (EditText) rootView
				.findViewById(R.id.editText_check_number_name);
		editText_check_number_activity_password = (EditText) rootView
				.findViewById(R.id.editText_check_number_password);
		button_check_number_confirm = (CircularProgressButton) rootView
				.findViewById(R.id.button_check_number_confirm);
		textView_check_number_number_choose_date = (TextView) rootView
				.findViewById(R.id.button_check_number_choose_date);
		mBack = (FrameLayout) rootView.findViewById(R.id.button_check_numbers_back);
		
		mBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FragmentManager fragmentManager = getActivity()
						.getSupportFragmentManager();
				FragmentTransaction transaction = fragmentManager
						.beginTransaction();
				transaction.replace(R.id.container, FragmentScanCode.newInstance(10));
				transaction.commit();
			}
		});

		// 点击选择日期按钮
		textView_check_number_number_choose_date
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Dialog.Builder builder = new DatePickerDialog.Builder(
								R.style.DateSimpleDialogLight) {
							@Override
							public void onPositiveActionClicked(
									DialogFragment fragment) {
								DatePickerDialog dialog = (DatePickerDialog) fragment
										.getDialog();
								String date = dialog
										.getFormattedDate(new SimpleDateFormat("yyyy-MM-dd"));
								textView_check_number_number_choose_date
										.setText(date);
								super.onPositiveActionClicked(fragment);
							}

							@Override
							public void onNegativeActionClicked(
									DialogFragment fragment) {
								super.onNegativeActionClicked(fragment);
							}
						};
						builder.positiveAction("确定").negativeAction("取消");
						DialogFragment fragment = DialogFragment
								.newInstance(builder);
						fragment.show(getChildFragmentManager(), "start");
					}
				});

		// 选择确认,把填写的数据上传到网络数据库
		button_check_number_confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (button_check_number_confirm.getProgress() == 0) {
					// 该方法用于获取用户输入数据，同时检测是否有空数据,最后生成url
					getInputData();

					// 如果url不为空，即用户输入全部为空
					if (!url.equals("")) {
						// 确认按钮设置不可点击
						button_check_number_confirm.setText("正在查询```");
						button_check_number_confirm.setClickable(false);

						// 访问网络,并获得数据,开启子线程
						networkThread(url);

						// 判断子线程是否执行完毕，如果没有，主线程一致执行该语句
						while (!THREAD_IS_FINISH) {

						}
						
					}
				} else if (button_check_number_confirm.getProgress() == 100) {
					button_check_number_confirm.setProgress(0);
				} else if (button_check_number_confirm.getProgress() == -1) {
					button_check_number_confirm.setProgress(0);
				}
			}
		});
		return rootView;
	}

	/**
	 * 获得碎片所在的上下文
	 * 
	 * @return Context
	 */
	private Context getContext() {
		return getActivity();
	}

	/**
	 * 对选择日期进行初始化，初始值为当前日期
	 */
	public void initCurrentDate() {
		Date current_date = new Date();
		// 把当日日期的年月日，存放在存储日期的变量里
		select_year = Integer.parseInt(new SimpleDateFormat("yyyy")
				.format(current_date));
		select_month = Integer.parseInt(new SimpleDateFormat("MM")
				.format(current_date));
		select_day = Integer.parseInt(new SimpleDateFormat("dd")
				.format(current_date));
	}

	/**
	 * 获取用户输入数据，同时检测是否有空数据
	 */
	public void getInputData() {

		// 获取用户输入的活动名称和密码
		activity_name = editText_check_number_activity_name.getText()
				.toString().trim();
		activity_password = editText_check_number_activity_password.getText()
				.toString().trim();
		activity_date = textView_check_number_number_choose_date.getText()
				.toString().trim();

		if (activity_name == null || activity_name.equals("")
				|| activity_password == null || activity_password.equals("")
				|| activity_date == null || activity_date.equals("")) {
			// 如果输入数据有空，则弹出提示
			Toast.makeText(getContext(), "缺少数据，请重新检查~", Toast.LENGTH_SHORT)
					.show();
			button_check_number_confirm.setProgress(-1);
		} else {
			// 如果输入数据不为空，则生成url
			url = "http://sqlweixin.duapp.com/app/check_activity.php?meeting_name="
					+ activity_name
					+ "&meeting_date="
					+ activity_date
					+ "&meeting_pwd=" + activity_password;
		}
	}

	/**
	 * 访问网络线程
	 * 
	 * @param url
	 */
	public void networkThread(final String url) {
		new Thread() {
			public void run() {
				// 连接网络，并获取返回数据，存储在inputstream中
				InputStream is = ConnectPHP.getPHPData(url);
				
				Message message = new Message();
				message.what = 1;
				
				// 根据获得的返回数据inputstream，把相关信息存储在list中
				getCheckNumData(is);

				// 把子线程标志位设为true
				THREAD_IS_FINISH = true;
				
				handler.sendMessage(message);
			}
		}.start();
	}

	public void getCheckNumData(InputStream is) {

		String result = null;

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			sb.append(reader.readLine() + "\n");
			String line = "0";
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			result = sb.toString();
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
			// 把从网络上抓取的数据转换成json数据
			JSONArray jArray = new JSONArray(result);
			JSONObject json_data = null;

			// json格式为：{"name":"黄志朗","sid":"20121000591","sign_time":"2015-8-13@11:10:34"}
			// 用于存放json数据中的标签为“name”，“sid”，“date”数据
			String data_name;
			String data_sid;
			String data_sign_time;
			for (int i = 0; i < jArray.length(); i++) {
				json_data = jArray.getJSONObject(i);

				// 取出数据
				data_name = json_data.getString("name");
				data_sid = json_data.getString("sid");
				data_sign_time = json_data.getString("sign_time");

				// 把数据加入数组中
				numNames.add(data_name);
				numIds.add(data_sid);
				numTimes.add(data_sign_time);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public Handler handler = new Handler(){
		public void handleMessage(Message message){
			if (message.what == 1) {
				button_check_number_confirm.setProgress(100);
				// 开启新的fragment，向fragment传送的数据为：姓名，id，签到时间数组，和活动名称和日期，名称和日期用于清除数据
				FragmentManager fragmentManager = getActivity()
						.getSupportFragmentManager();
				FragmentTransaction transaction = fragmentManager
						.beginTransaction();
				transaction.replace(R.id.container, FragmentShowNumber
						.newInstance(numNames, numIds, numTimes,
								activity_name, activity_date));
				transaction.commit();
			}
		}
	};
}
