package com.sparke.modules;

import java.io.InputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;

import com.example.material.R;
import com.sparke.web.util.ConnectPHP;
import com.zxing.activity.CaptureActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class FragmentScanCode extends Fragment {

	private static final String SELECTED_POSITION = "selected_menu_position";

	// 控件
	private FrameLayout button_create_activity;
	private FrameLayout button_scan_qrcode_sign_in;
	private FrameLayout button_check_numbers;

	// 二维码信息拆分：活动名称，活动日期，活动地点
	private String meeting_name;
	private String meeting_date;
	private String meeting_place;

	/**
	 * 用户名字信息(名字和学号)，需要调用本地存储，暂时不做判断是否已经登陆的功能 这里需要加读取用户姓名和学号的功能
	 */
	private String userName;
	private String userId;

	// 签到的时间，格式为：2015-7-1 10:21:30
	private String sign_time;

	// 当访问网络服务器时候，需要弹出progressdialog
	ProgressDialog progressDialog;

	// 签到后访问数据库后，返回的信息（没有这个活动/已经签到/签到成功）
	private String sign_result;

	public FragmentScanCode() {
	}

	public static FragmentScanCode newInstance(int sectionNumber) {
		FragmentScanCode fragment = new FragmentScanCode();
		Bundle args = new Bundle();
		args.putInt(SELECTED_POSITION, sectionNumber);
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
		View rootView = null;

		// 界面
		rootView = inflater.inflate(R.layout.fragment_scancode, container,
				false);
		
		SharedPreferences pref = getContext().getSharedPreferences("Puma", Context.MODE_PRIVATE);
		userName = pref.getString("name", "");
		Log.d("TIME", "USER:" + userName);
		userId = pref.getString("number", "");
		Log.d("TIME", "ID:" + userId);

		// 控件
		button_create_activity = (FrameLayout) rootView
				.findViewById(com.example.material.R.id.button_create_activity);
		button_scan_qrcode_sign_in = (FrameLayout) rootView
				.findViewById(R.id.button_scan_qrcode_sign_in);
		button_check_numbers = (FrameLayout) rootView
				.findViewById(R.id.button_check_numbers);

		// 发起签到按钮
		button_create_activity.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// 开启fragment
				FragmentManager fragmentManager = getActivity()
						.getSupportFragmentManager();
				FragmentTransaction transaction = fragmentManager
						.beginTransaction();
				transaction.replace(R.id.container,
						FragmentCreateQRCode.newInstance(1));
				transaction.commit();
			}
		});

		// 我要签到按钮
		button_scan_qrcode_sign_in.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// 开启扫二维码活动，该活动将扫描到的二维码，解析成字符串后用result形式传回
				Intent intent = new Intent(getActivity(), CaptureActivity.class);
				startActivityForResult(intent, 10);
			}
		});
	
		button_check_numbers.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 开启fragme			
				FragmentManager fragmentManager = getActivity()
						.getSupportFragmentManager();
				FragmentTransaction transaction = fragmentManager
						.beginTransaction();
				transaction.replace(R.id.container,
						FragmentCheckNumber.newInstance(1));
				transaction.commit();
			}
		});
		return rootView;
	}

	@Override
	public void onStart() {
		super.onStart();
		// list.setRefreshing();
	}

	/**
	 * 获得碎片所在的上下文
	 * 
	 * @return
	 */
	private Context getContext() {
		return getActivity();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		Log.d("DIDAREN", "activityresult");
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		String qrdata = data.getExtras().getString("result");
		Log.d("DIDAREN", "扫描二维码返回信息");
		if (!qrdata.isEmpty()) {
			// 如果返回信息不为空
			// 根据“/”把二维码信息拆分若干部分
			// 我们设定的二维码格式为：xxxx（名称）/xxxx（地点）/xxx（日期）
			// 把拆分的部分放入string[]数组中
			String[] data_split = qrdata.split("/");
			Log.d("DIDAREN", "分解长度为"+data_split.length);
			// 我把是否是我们自己的二维码判断条件设为：拆分部分是否为3个部分
			if (data_split.length == 3) {
				// 如果拆分为3各部分，很有可能为我们自己的二维码
				Log.d("DIDAREN", "分解长度为3");
				meeting_name = data_split[0];
				meeting_place = data_split[1];
				meeting_date = data_split[2];
				Log.d("DIDAREN", "meeting_name: " + data_split[0]);
				Log.d("DIDAREN", "meeting_place: " + data_split[1]);
				Log.d("DIDAREN", "meeting_date: " + data_split[2]);

				// 得到签到时间
				getCurrentTime();

				// 生成访问网址信息
				String url = "http://sqlweixin.duapp.com/app/register_activity.php?name="
						+ userName
						+ "&sid="
						+ userId
						+ "&sign_time="
						+ sign_time
						+ "&meeting_name="
						+ meeting_name
						+ "&meeting_date=" + meeting_date;
				Log.d("DIDAREN", "插入数据url="+url);
				// 访问网络服务器，把签到信息插入网络数据库，同时生成progressdialog
				progressDialog = new ProgressDialog(getContext());
				progressDialog.setMessage("正在访问网络···");
				progressDialog.show();

				// 开启访问网络线程
				signIn(url);

				//progressDialog.dismiss();
			} else {
				// 如果二维码拆分长度不为3，则该二维码不是软件所能识别的 二维码
				// 弹出提示对话框
				AlertDialog.Builder wrong_dialog = new AlertDialog.Builder(
						getContext());
				wrong_dialog.setMessage("该二维码错误。");
				wrong_dialog.setCancelable(false);
				wrong_dialog.setPositiveButton("我知道了",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
							}
						});
				wrong_dialog.show();
			}
		}
	}
	public void receiveActivityResult(String qrdata) {
		
		Log.d("DIDAREN", "sCANcODE: " + qrdata);
		if (!qrdata.isEmpty()) {
			// 如果返回信息不为空
			// 根据“/”把二维码信息拆分若干部分
			// 我们设定的二维码格式为：xxxx（名称）/xxxx（地点）/xxx（日期）
			// 把拆分的部分放入string[]数组中
			String[] data_split = qrdata.split("/");
			Log.d("DIDAREN", "分解长度为"+data_split.length);
			// 我把是否是我们自己的二维码判断条件设为：拆分部分是否为3个部分
			if (data_split.length == 3) {
				// 如果拆分为3各部分，很有可能为我们自己的二维码
				Log.d("DIDAREN", "分解长度为3");
				meeting_name = data_split[0];
				meeting_place = data_split[1];
				meeting_date = data_split[2];
				Log.d("DIDAREN", "meeting_name: " + data_split[0]);
				Log.d("DIDAREN", "meeting_place: " + data_split[1]);
				Log.d("DIDAREN", "meeting_date: " + data_split[2]);

				// 得到签到时间
				getCurrentTime();

				// 生成访问网址信息
				String url = "http://sqlweixin.duapp.com/app/register_activity.php?name="
						+ userName
						+ "&sid="
						+ userId
						+ "&sign_time="
						+ sign_time
						+ "&meeting_name="
						+ meeting_name
						+ "&meeting_date=" + meeting_date;
				Log.d("DIDAREN", "插入数据url="+url);
				// 访问网络服务器，把签到信息插入网络数据库，同时生成progressdialog
				progressDialog = new ProgressDialog(getContext());
				progressDialog.setMessage("正在访问网络···");
				progressDialog.show();

				// 开启访问网络线程
				signIn(url);

				//progressDialog.dismiss();
			} else {
				// 如果二维码拆分长度不为3，则该二维码不是软件所能识别的 二维码
				// 弹出提示对话框
				AlertDialog.Builder wrong_dialog = new AlertDialog.Builder(
						getContext());
				wrong_dialog.setMessage("该二维码错误。");
				wrong_dialog.setCancelable(false);
				wrong_dialog.setPositiveButton("我知道了",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
							}
						});
				wrong_dialog.show();
			}
		}

	}

	public void signIn(final String url) {
		new Thread() {
			public void run() {
				Message msg = new Message();
				InputStream is = ConnectPHP.getPHPData(url);

				// sign_result为签到后返回的数据
				// 签到后返回数据分三种：返回>>没有这个活动
				// 返回>>已经签到过了
				// 返回>>成功签到
				sign_result = ConnectPHP.getSignInData(is);
				Log.d("DIDAREN", "签到回复="+sign_result);
				if (!sign_result.equals(null)) {
					// 如果返回数据不为空
					msg.what = 1;
				} else {
					msg.what = 0;
				}

				// 用handler更新界面
				handler.sendMessage(msg);
			}
		}.start();
	}

	private Handler handler = new Handler() {
		@SuppressLint("HandlerLeak")
		public void handleMessage(Message msg) {

			AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
			if (msg.what == 1) {
				if (sign_result.equals("签到成功！")) {
					// 如果签到成功
					dialog.setTitle(meeting_name);
					dialog.setMessage(sign_result + "\n" + "签到时间:" + sign_time);
					dialog.setPositiveButton("确认",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
								}

							});
					dialog.show();
				} else {
					// 如果是“已经签到过”或者“该活动不存在”情况
					dialog.setMessage(sign_result);
					dialog.setPositiveButton("确认",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
								}

							});
					dialog.show();
				}
			} else if (msg.what == 0) {
				dialog.setMessage("网络出错，请重新操作~");
				dialog.setPositiveButton("我知道了",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
							}

						});
				dialog.show();
			}
			progressDialog.dismiss();
		}
	};

	/**
	 * 得到签到时间
	 */
	public void getCurrentTime() {

		long time = System.currentTimeMillis();
		Date date = new Date(time);

		// 得到当前时间，签到时间设置为当前时间
		sign_time = new SimpleDateFormat("yyyy-MM-dd@hh:mm:ss").format(date);
	}
}
