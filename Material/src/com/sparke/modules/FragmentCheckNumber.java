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

	// �ؼ�
	private EditText editText_check_number_activity_name;
	private EditText editText_check_number_activity_password;
	private TextView textView_check_number_number_choose_date;
	private CircularProgressButton button_check_number_confirm;
	private FrameLayout mBack;

	// ѡ������
	private int select_year;
	private int select_month;
	private int select_day;

	// ������������ݣ�����ƣ�����ڣ������
	private String activity_name;
	private String activity_date;
	private String activity_password;

	// url
	private String url = new String();

	// ��������洢ǩ����Ա���֣�id��ǩ��ʱ����Ϣ
	List<String> numNames = new ArrayList<String>();
	List<String> numIds = new ArrayList<String>();
	List<String> numTimes = new ArrayList<String>();

	// �߳�ִ�б�־λ�����ڣ����̵߳ȴ����߳�ִ����ɲ��ܼ���ִ��
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

		// ����
		rootView = inflater.inflate(R.layout.fragment_check_number, container,
				false);

		// ��ʱ���ʼ��Ϊ�������ڣ�����DatePickerDialogѡ������
		initCurrentDate();

		// �ؼ���ʼ��
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

		// ���ѡ�����ڰ�ť
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
						builder.positiveAction("ȷ��").negativeAction("ȡ��");
						DialogFragment fragment = DialogFragment
								.newInstance(builder);
						fragment.show(getChildFragmentManager(), "start");
					}
				});

		// ѡ��ȷ��,����д�������ϴ����������ݿ�
		button_check_number_confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (button_check_number_confirm.getProgress() == 0) {
					// �÷������ڻ�ȡ�û��������ݣ�ͬʱ����Ƿ��п�����,�������url
					getInputData();

					// ���url��Ϊ�գ����û�����ȫ��Ϊ��
					if (!url.equals("")) {
						// ȷ�ϰ�ť���ò��ɵ��
						button_check_number_confirm.setText("���ڲ�ѯ```");
						button_check_number_confirm.setClickable(false);

						// ��������,���������,�������߳�
						networkThread(url);

						// �ж����߳��Ƿ�ִ����ϣ����û�У����߳�һ��ִ�и����
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
	 * �����Ƭ���ڵ�������
	 * 
	 * @return Context
	 */
	private Context getContext() {
		return getActivity();
	}

	/**
	 * ��ѡ�����ڽ��г�ʼ������ʼֵΪ��ǰ����
	 */
	public void initCurrentDate() {
		Date current_date = new Date();
		// �ѵ������ڵ������գ�����ڴ洢���ڵı�����
		select_year = Integer.parseInt(new SimpleDateFormat("yyyy")
				.format(current_date));
		select_month = Integer.parseInt(new SimpleDateFormat("MM")
				.format(current_date));
		select_day = Integer.parseInt(new SimpleDateFormat("dd")
				.format(current_date));
	}

	/**
	 * ��ȡ�û��������ݣ�ͬʱ����Ƿ��п�����
	 */
	public void getInputData() {

		// ��ȡ�û�����Ļ���ƺ�����
		activity_name = editText_check_number_activity_name.getText()
				.toString().trim();
		activity_password = editText_check_number_activity_password.getText()
				.toString().trim();
		activity_date = textView_check_number_number_choose_date.getText()
				.toString().trim();

		if (activity_name == null || activity_name.equals("")
				|| activity_password == null || activity_password.equals("")
				|| activity_date == null || activity_date.equals("")) {
			// ������������пգ��򵯳���ʾ
			Toast.makeText(getContext(), "ȱ�����ݣ������¼��~", Toast.LENGTH_SHORT)
					.show();
			button_check_number_confirm.setProgress(-1);
		} else {
			// ����������ݲ�Ϊ�գ�������url
			url = "http://sqlweixin.duapp.com/app/check_activity.php?meeting_name="
					+ activity_name
					+ "&meeting_date="
					+ activity_date
					+ "&meeting_pwd=" + activity_password;
		}
	}

	/**
	 * ���������߳�
	 * 
	 * @param url
	 */
	public void networkThread(final String url) {
		new Thread() {
			public void run() {
				// �������磬����ȡ�������ݣ��洢��inputstream��
				InputStream is = ConnectPHP.getPHPData(url);
				
				Message message = new Message();
				message.what = 1;
				
				// ���ݻ�õķ�������inputstream���������Ϣ�洢��list��
				getCheckNumData(is);

				// �����̱߳�־λ��Ϊtrue
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
			// �Ѵ�������ץȡ������ת����json����
			JSONArray jArray = new JSONArray(result);
			JSONObject json_data = null;

			// json��ʽΪ��{"name":"��־��","sid":"20121000591","sign_time":"2015-8-13@11:10:34"}
			// ���ڴ��json�����еı�ǩΪ��name������sid������date������
			String data_name;
			String data_sid;
			String data_sign_time;
			for (int i = 0; i < jArray.length(); i++) {
				json_data = jArray.getJSONObject(i);

				// ȡ������
				data_name = json_data.getString("name");
				data_sid = json_data.getString("sid");
				data_sign_time = json_data.getString("sign_time");

				// �����ݼ���������
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
				// �����µ�fragment����fragment���͵�����Ϊ��������id��ǩ��ʱ�����飬�ͻ���ƺ����ڣ����ƺ����������������
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
