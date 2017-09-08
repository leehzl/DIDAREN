package com.sparke.modules;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.dd.CircularProgressButton;
import com.example.material.R;
import com.rey.material.app.DatePickerDialog;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentCreateQRCode extends Fragment {

	private static final String ARG_SECTION_NUMBER = "section_number";

	// 控件
	private EditText editText_create_qrcode_name;
	private EditText editText_create_qrcode_place;
	private EditText editText_create_qrcode_password;
	private TextView button_create_qrcode_choose_date;
	private CircularProgressButton button_create_qrcode_confirm;
	private FrameLayout mBack;

	// 选择活动日期
	private int select_year;
	private int select_month;
	private int select_day;

	// 存放用户输入数据
	private static String activity_name;
	private String activity_place;
	private String activity_password;

	// 二维码存放的数据
	private static String qrcode_data = new String();

	// 活动日期
	private static String activity_date;

	public static FragmentCreateQRCode newInstance(int sectionNumber) {

		FragmentCreateQRCode fragment = new FragmentCreateQRCode();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public FragmentCreateQRCode() {
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = null;

		// 界面
		rootView = inflater.inflate(R.layout.fragment_create_qrcode, container,
				false);

		// 控件
		editText_create_qrcode_name = (EditText) rootView
				.findViewById(R.id.editText_create_qrcode_name);
		editText_create_qrcode_place = (EditText) rootView
				.findViewById(R.id.editText_create_qrcode_place);
		editText_create_qrcode_password = (EditText) rootView
				.findViewById(R.id.editText_create_qrcode_password);
		button_create_qrcode_choose_date = (TextView) rootView
				.findViewById(R.id.button_create_qrcode_choose_date);
		button_create_qrcode_confirm = (CircularProgressButton) rootView
				.findViewById(R.id.button_create_qrcode_confirm);
		
		mBack = (FrameLayout) rootView.findViewById(R.id.button_create_qrcode_back);
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

		// 初始化日期，设置为当前日期
		initCurrentDate();

		// 点击选择日期按钮，选择活动举行日期，存放在select_year,select_month,select_day
		button_create_qrcode_choose_date
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
								SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
								String date = dialog
										.getFormattedDate(df);
								
								try {
									activity_date = date;
									Calendar calendar = Calendar.getInstance();
									calendar.setTime(df.parse(date));
									select_year = calendar.get(Calendar.YEAR);
									select_month = calendar.get(Calendar.MONTH) + 1;
									select_day = calendar
											.get(Calendar.DAY_OF_MONTH);
								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								button_create_qrcode_choose_date.setText(date);
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

		// 点击确认按钮，把填写数据上传到网络数据库
		button_create_qrcode_confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (button_create_qrcode_confirm.getProgress() == 0) {
					// 该方法用于获取用户输入数据，同时检测是否有空数据,最后组装成二维码数据
					button_create_qrcode_confirm.setProgress(50);
	
					if (getInputData()) {
						// 该表确认按钮的内容，并且设置不可点击
						button_create_qrcode_confirm.setClickable(false);

						// 访问服务器地址，插入该会议信息到数据库
						String url = "http://sqlweixin.duapp.com/app/create_activity.php?meeting_name="
								+ activity_name
								+ "&meeting_place="
								+ activity_place
								+ "&meeting_date="
								+ activity_date
								+ "&meeting_pwd="
								+ activity_password;

						// 开启访问网络线程
						networkThread(url);
					} else {
						button_create_qrcode_confirm.setProgress(-1);
					}
				} else if (button_create_qrcode_confirm.getProgress() == 100) {
					button_create_qrcode_confirm.setProgress(0);
				} else if (button_create_qrcode_confirm.getProgress() == -1) {
					button_create_qrcode_confirm.setProgress(0);
				}

			}
		});

		return rootView;
	}

	/**
	 * 对选择日期进行初始化，初始值为当前日期 select_year,select_month,select_day来存储选择的年月日 初始化为当前日期
	 */
	public void initCurrentDate() {
		Date current_date = new Date();
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
	public Boolean getInputData() {

		// 获取用户输入的数据：活动名称，活动地点，密码
		activity_name = editText_create_qrcode_name.getText().toString().trim();
		activity_place = editText_create_qrcode_place.getText().toString()
				.trim();
		activity_password = editText_create_qrcode_password.getText()
				.toString().trim();

		if (activity_name.equals("") || activity_password.equals("")
				|| activity_place.equals("")) {
			// 如果输入数据有空，则弹出提示
			Toast.makeText(getContext(), "缺少数据，请重新检查~", Toast.LENGTH_SHORT)
					.show();
			button_create_qrcode_confirm.setProgress(-1);
			return false;
		} else {
			// 检测数据均不为空
			qrcode_data = activity_name + "/" + activity_place + "/"
					+ activity_date;
			return true;
		}
	}

	/**
	 * 访问网络线程
	 */
	public void networkThread(final String url) {
		new Thread() {
			public void run() {
				Message message = new Message();
				try {
					HttpClient httpclient = new DefaultHttpClient();
					HttpGet httpget = new HttpGet(url);
					httpclient.execute(httpget);
				} catch (Exception e) {
					e.printStackTrace();
				}
				message.what = 1;
				handler.sendMessage(message);
			}
		}.start();
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message message) {
			if (message.what == 1) {
				// 会议内容已经成功插入数据库，接下来开启显示二维码活动
				button_create_qrcode_confirm.setProgress(100);
				FragmentManager fragmentManager = getActivity()
						.getSupportFragmentManager();
				FragmentTransaction transaction = fragmentManager
						.beginTransaction();
				transaction.replace(
						R.id.container,
						FragmentShowQRCode.newInstance(activity_name + "&"
								+ activity_date, qrcode_data));
				transaction.commit();
			}
		}
	};

	/**
	 * 获得碎片所在的上下文
	 * 
	 * @return
	 */
	private Context getContext() {
		return getActivity();
	}
}
