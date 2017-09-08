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

	// �ؼ�
	private FrameLayout button_create_activity;
	private FrameLayout button_scan_qrcode_sign_in;
	private FrameLayout button_check_numbers;

	// ��ά����Ϣ��֣�����ƣ�����ڣ���ص�
	private String meeting_name;
	private String meeting_date;
	private String meeting_place;

	/**
	 * �û�������Ϣ(���ֺ�ѧ��)����Ҫ���ñ��ش洢����ʱ�����ж��Ƿ��Ѿ���½�Ĺ��� ������Ҫ�Ӷ�ȡ�û�������ѧ�ŵĹ���
	 */
	private String userName;
	private String userId;

	// ǩ����ʱ�䣬��ʽΪ��2015-7-1 10:21:30
	private String sign_time;

	// ���������������ʱ����Ҫ����progressdialog
	ProgressDialog progressDialog;

	// ǩ����������ݿ�󣬷��ص���Ϣ��û������/�Ѿ�ǩ��/ǩ���ɹ���
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

		// ����
		rootView = inflater.inflate(R.layout.fragment_scancode, container,
				false);
		
		SharedPreferences pref = getContext().getSharedPreferences("Puma", Context.MODE_PRIVATE);
		userName = pref.getString("name", "");
		Log.d("TIME", "USER:" + userName);
		userId = pref.getString("number", "");
		Log.d("TIME", "ID:" + userId);

		// �ؼ�
		button_create_activity = (FrameLayout) rootView
				.findViewById(com.example.material.R.id.button_create_activity);
		button_scan_qrcode_sign_in = (FrameLayout) rootView
				.findViewById(R.id.button_scan_qrcode_sign_in);
		button_check_numbers = (FrameLayout) rootView
				.findViewById(R.id.button_check_numbers);

		// ����ǩ����ť
		button_create_activity.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// ����fragment
				FragmentManager fragmentManager = getActivity()
						.getSupportFragmentManager();
				FragmentTransaction transaction = fragmentManager
						.beginTransaction();
				transaction.replace(R.id.container,
						FragmentCreateQRCode.newInstance(1));
				transaction.commit();
			}
		});

		// ��Ҫǩ����ť
		button_scan_qrcode_sign_in.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// ����ɨ��ά�����û��ɨ�赽�Ķ�ά�룬�������ַ�������result��ʽ����
				Intent intent = new Intent(getActivity(), CaptureActivity.class);
				startActivityForResult(intent, 10);
			}
		});
	
		button_check_numbers.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ����fragme			
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
	 * �����Ƭ���ڵ�������
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
		Log.d("DIDAREN", "ɨ���ά�뷵����Ϣ");
		if (!qrdata.isEmpty()) {
			// ���������Ϣ��Ϊ��
			// ���ݡ�/���Ѷ�ά����Ϣ������ɲ���
			// �����趨�Ķ�ά���ʽΪ��xxxx�����ƣ�/xxxx���ص㣩/xxx�����ڣ�
			// �Ѳ�ֵĲ��ַ���string[]������
			String[] data_split = qrdata.split("/");
			Log.d("DIDAREN", "�ֽⳤ��Ϊ"+data_split.length);
			// �Ұ��Ƿ��������Լ��Ķ�ά���ж�������Ϊ����ֲ����Ƿ�Ϊ3������
			if (data_split.length == 3) {
				// ������Ϊ3�����֣����п���Ϊ�����Լ��Ķ�ά��
				Log.d("DIDAREN", "�ֽⳤ��Ϊ3");
				meeting_name = data_split[0];
				meeting_place = data_split[1];
				meeting_date = data_split[2];
				Log.d("DIDAREN", "meeting_name: " + data_split[0]);
				Log.d("DIDAREN", "meeting_place: " + data_split[1]);
				Log.d("DIDAREN", "meeting_date: " + data_split[2]);

				// �õ�ǩ��ʱ��
				getCurrentTime();

				// ���ɷ�����ַ��Ϣ
				String url = "http://sqlweixin.duapp.com/app/register_activity.php?name="
						+ userName
						+ "&sid="
						+ userId
						+ "&sign_time="
						+ sign_time
						+ "&meeting_name="
						+ meeting_name
						+ "&meeting_date=" + meeting_date;
				Log.d("DIDAREN", "��������url="+url);
				// �����������������ǩ����Ϣ�����������ݿ⣬ͬʱ����progressdialog
				progressDialog = new ProgressDialog(getContext());
				progressDialog.setMessage("���ڷ������硤����");
				progressDialog.show();

				// �������������߳�
				signIn(url);

				//progressDialog.dismiss();
			} else {
				// �����ά���ֳ��Ȳ�Ϊ3����ö�ά�벻���������ʶ��� ��ά��
				// ������ʾ�Ի���
				AlertDialog.Builder wrong_dialog = new AlertDialog.Builder(
						getContext());
				wrong_dialog.setMessage("�ö�ά�����");
				wrong_dialog.setCancelable(false);
				wrong_dialog.setPositiveButton("��֪����",
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
			// ���������Ϣ��Ϊ��
			// ���ݡ�/���Ѷ�ά����Ϣ������ɲ���
			// �����趨�Ķ�ά���ʽΪ��xxxx�����ƣ�/xxxx���ص㣩/xxx�����ڣ�
			// �Ѳ�ֵĲ��ַ���string[]������
			String[] data_split = qrdata.split("/");
			Log.d("DIDAREN", "�ֽⳤ��Ϊ"+data_split.length);
			// �Ұ��Ƿ��������Լ��Ķ�ά���ж�������Ϊ����ֲ����Ƿ�Ϊ3������
			if (data_split.length == 3) {
				// ������Ϊ3�����֣����п���Ϊ�����Լ��Ķ�ά��
				Log.d("DIDAREN", "�ֽⳤ��Ϊ3");
				meeting_name = data_split[0];
				meeting_place = data_split[1];
				meeting_date = data_split[2];
				Log.d("DIDAREN", "meeting_name: " + data_split[0]);
				Log.d("DIDAREN", "meeting_place: " + data_split[1]);
				Log.d("DIDAREN", "meeting_date: " + data_split[2]);

				// �õ�ǩ��ʱ��
				getCurrentTime();

				// ���ɷ�����ַ��Ϣ
				String url = "http://sqlweixin.duapp.com/app/register_activity.php?name="
						+ userName
						+ "&sid="
						+ userId
						+ "&sign_time="
						+ sign_time
						+ "&meeting_name="
						+ meeting_name
						+ "&meeting_date=" + meeting_date;
				Log.d("DIDAREN", "��������url="+url);
				// �����������������ǩ����Ϣ�����������ݿ⣬ͬʱ����progressdialog
				progressDialog = new ProgressDialog(getContext());
				progressDialog.setMessage("���ڷ������硤����");
				progressDialog.show();

				// �������������߳�
				signIn(url);

				//progressDialog.dismiss();
			} else {
				// �����ά���ֳ��Ȳ�Ϊ3����ö�ά�벻���������ʶ��� ��ά��
				// ������ʾ�Ի���
				AlertDialog.Builder wrong_dialog = new AlertDialog.Builder(
						getContext());
				wrong_dialog.setMessage("�ö�ά�����");
				wrong_dialog.setCancelable(false);
				wrong_dialog.setPositiveButton("��֪����",
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

				// sign_resultΪǩ���󷵻ص�����
				// ǩ���󷵻����ݷ����֣�����>>û������
				// ����>>�Ѿ�ǩ������
				// ����>>�ɹ�ǩ��
				sign_result = ConnectPHP.getSignInData(is);
				Log.d("DIDAREN", "ǩ���ظ�="+sign_result);
				if (!sign_result.equals(null)) {
					// ����������ݲ�Ϊ��
					msg.what = 1;
				} else {
					msg.what = 0;
				}

				// ��handler���½���
				handler.sendMessage(msg);
			}
		}.start();
	}

	private Handler handler = new Handler() {
		@SuppressLint("HandlerLeak")
		public void handleMessage(Message msg) {

			AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
			if (msg.what == 1) {
				if (sign_result.equals("ǩ���ɹ���")) {
					// ���ǩ���ɹ�
					dialog.setTitle(meeting_name);
					dialog.setMessage(sign_result + "\n" + "ǩ��ʱ��:" + sign_time);
					dialog.setPositiveButton("ȷ��",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
								}

							});
					dialog.show();
				} else {
					// ����ǡ��Ѿ�ǩ���������ߡ��û�����ڡ����
					dialog.setMessage(sign_result);
					dialog.setPositiveButton("ȷ��",
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
				dialog.setMessage("������������²���~");
				dialog.setPositiveButton("��֪����",
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
	 * �õ�ǩ��ʱ��
	 */
	public void getCurrentTime() {

		long time = System.currentTimeMillis();
		Date date = new Date(time);

		// �õ���ǰʱ�䣬ǩ��ʱ������Ϊ��ǰʱ��
		sign_time = new SimpleDateFormat("yyyy-MM-dd@hh:mm:ss").format(date);
	}
}
