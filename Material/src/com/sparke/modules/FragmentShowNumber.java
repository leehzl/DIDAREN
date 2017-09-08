package com.sparke.modules;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.example.material.R;
import com.sparke.model.CheckNumCellData;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FragmentShowNumber extends Fragment {

	//�ؼ�
	private TextView textView_check_number_list_total;
	private TextView check_number_list_clear_text;
	private FrameLayout button_check_number_list_clear;
	private FrameLayout mBack;
	private LinearLayout linearLayout_invite;
	
	//ÿ��ǩ����Ա����Ϊһ���ࣨCheckNumCellData��
	CheckNumCellData[] datas;
	
	//���ǩ����Ա����
	public static ArrayList<String> nameList;
	public static ArrayList<String> idList;
	public static ArrayList<String> timeList;
	
	//���ֲ���
	private LinearLayout.LayoutParams LP_MW = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
	private LinearLayout.LayoutParams layout_name = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT,1);
	private LinearLayout.LayoutParams layout_id = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT,2);
	private LinearLayout.LayoutParams layout_time = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT,4);
	
	//�б���ɫ��־λ
	private boolean column_background_color_flag = true;
	//����ƣ����ڣ�������������
	private String activity_name;
	private String activity_date;
	
	public static FragmentShowNumber newInstance(List<String> numNames, List<String> numIds, List<String> numTimes, String activity_name, String activity_date) {
		FragmentShowNumber fragment = new FragmentShowNumber();
        Bundle args = new Bundle();
        //args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        
        //�Ѵ������Ĳ����ӽ�args��
        //numNames:ǩ����Ա��������
        //numIds:ǩ����Աѧ������
        //numTimes:ǩ����Աǩ��ʱ������
        args.putStringArrayList("numNames", (ArrayList<String>)numNames);
        args.putStringArrayList("numIds", (ArrayList<String>)numIds);
        args.putStringArrayList("numTimes", (ArrayList<String>)numTimes);
        args.putString("activity_name", activity_name);
        args.putString("activity_date", activity_date);
        fragment.setArguments(args);
        return fragment;
    }
	
	public FragmentShowNumber(){
	}
	
	 public View onCreateView(LayoutInflater inflater, ViewGroup container,
	          Bundle savedInstanceState) {
		 
		 View rootView = null;
		 
		 //args�е�����ȡ����
		 if (getArguments() != null) {
			nameList = getArguments().getStringArrayList("numNames");
			idList = getArguments().getStringArrayList("numIds");
			timeList = getArguments().getStringArrayList("numTimes");
			
			activity_name = getArguments().getString("activity_name");
			activity_date = getArguments().getString("activity_date");
			
		}
		 
		 //�󶨽���
		 rootView = inflater.inflate(R.layout.fragment_check_number_listnumber
				 , container, false);
		 
		 //����ؼ�
		 textView_check_number_list_total = (TextView)rootView.findViewById(R.id.textView_check_number_list_total);
		 button_check_number_list_clear = (FrameLayout)rootView.findViewById(R.id.button_check_number_list_clear);
		 linearLayout_invite = (LinearLayout)rootView.findViewById(R.id.linearLayout_invite_number);
		 check_number_list_clear_text = (TextView) rootView.findViewById(R.id.button_check_number_list_clear_text);
		 mBack = (FrameLayout) rootView.findViewById(R.id.button_check_number_list_back);
		 
		 mBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FragmentManager fragmentManager = getActivity()
						.getSupportFragmentManager();
				FragmentTransaction transaction = fragmentManager
						.beginTransaction();
				transaction.replace(R.id.container,
						FragmentCheckNumber.newInstance(1));
				transaction.commit();
			}
		});
		 
		 
		 //��̬����ǩ����Ա�б�
		 createInviteList();
		 
		 //��ʾǩ����Ա����
		 textView_check_number_list_total.setText("����"+nameList.size()+"��ǩ��");
		 
		 //���ǩ������С�ڻ��ߵ���0��������ݲ��ɵ��
		 if (nameList.size()<=0) {
			button_check_number_list_clear.setClickable(false);
		}
		 button_check_number_list_clear.setOnClickListener(new OnClickListener() {
			
			@Override
			//���ǩ������
			public void onClick(View v) {
				//�Ѱ�ť�������óɡ����������
				check_number_list_clear_text.setText("�������������");
				button_check_number_list_clear.setClickable(false);
				
				//���ʵ���ַ
				String url =  "http://sqlweixin.duapp.com/app/delete_activity.php?meeting_name="+activity_name+"&meeting_date="+activity_date;
				
				//���������߳�
				networkThread(url);
			}
		});
		 return rootView;
	 }
	
	private void createInviteList() {
		// TODO Auto-generated method stub
		for (int i = 0; i < nameList.size(); i++) {
			LinearLayout linearLayout_column = new LinearLayout(getContext());
			linearLayout_column = addColumn(i);
			linearLayout_invite.addView(linearLayout_column,LP_MW);
		}
	}

	private LinearLayout addColumn(int i) {
		// TODO Auto-generated method stub
		LinearLayout linearLayout_column = new LinearLayout(getContext());
		linearLayout_column.setOrientation(LinearLayout.HORIZONTAL);
		TextView textView_name = new TextView(getContext());
		TextView textView_id = new TextView(getContext());
		TextView textView_time = new TextView(getContext());
		layout_name.setMargins(0, 3, 0, 3);
		layout_id.setMargins(0, 3, 0, 3);
		layout_time.setMargins(0, 3, 0, 3);
		textView_name.setLayoutParams(layout_name);
		textView_id.setLayoutParams(layout_id);
		textView_time.setLayoutParams(layout_time);
		textView_name.setGravity(Gravity.CENTER);
		textView_id.setGravity(Gravity.CENTER);
		textView_time.setGravity(Gravity.CENTER);
		textView_name.setTextColor(android.graphics.Color.BLACK);
		textView_id.setTextColor(android.graphics.Color.BLACK);
		textView_time.setTextColor(android.graphics.Color.BLACK);
		
		if (column_background_color_flag) {
			linearLayout_column.setBackgroundColor(getActivity().getResources().getColor(R.color.danny_white));
			column_background_color_flag = false;
		}else {
			linearLayout_column.setBackgroundColor(getActivity().getResources().getColor(R.color.light_gray));
			column_background_color_flag = true;
		}
		textView_name.setText(nameList.get(i));
		textView_id.setText(idList.get(i));
		textView_time.setText(timeList.get(i));
		linearLayout_column.addView(textView_name);
		linearLayout_column.addView(textView_id);
		linearLayout_column.addView(textView_time);
		return linearLayout_column;
	}

	/**
     * �����Ƭ���ڵ�������
     * @return
     */
    private Context getContext() {
    	return getActivity();
    }
    
    /**
	 * ���������߳�
	 */
	public void networkThread(final String url){
		new Thread(){
			public void run(){
				Message message = new Message();
				try{
					HttpClient httpclient = new DefaultHttpClient();
					HttpGet httpget = new HttpGet(url);
		            httpclient.execute(httpget);
				}catch(Exception e){
					e.printStackTrace();
				}
				message.what = 1;
				handler.sendMessage(message);
			}
		}.start();
	}
	
	private Handler handler = new Handler(){
		public void handleMessage(Message message){
			if(message.what == 1){
				//�������ݳɹ����
				//�����������
				textView_check_number_list_total.setText("����0��ǩ��");
				
				//�����Ի���
				AlertDialog.Builder delete_success = new AlertDialog.Builder(getContext());
				delete_success.setMessage("����ɹ���");
				delete_success.setCancelable(false);
				delete_success.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
		    			FragmentTransaction transaction = fragmentManager.beginTransaction();
		    			transaction.replace(R.id.container, FragmentScanCode.newInstance(1));
		    			transaction.commit();
					}
				});
				delete_success.show();
			}
		}
	};
    
}
