package com.sparke.modules;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.material.R;
import com.sparke.database_task.DatabaseHelper;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

public class ActivityWelcome extends Activity {

	private boolean isFirstIn = false;
	private static final int TIME = 1500;
	private static final int GO_HOME = 1000;
	private static final int GO_GUIDE = 1001;

	private DatabaseHelper helper;
	private SQLiteDatabase db;

	private String lastest_title;
	private long diff_lastest = Long.MAX_VALUE;

	private List<String> notify_titles = new ArrayList<String>();

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case GO_HOME:
				goHome();
				break;

			case GO_GUIDE:
				goGuide();
				break;
			}

		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		NeedNotify();
		init();

	}

	private void NeedNotify() {
		// TODO Auto-generated method stub
		new Thread() {
			public void run() {
				Message hasDataToNotify = new Message();
				// 访问数据库
				helper = new DatabaseHelper(ActivityWelcome.this, "DIDAREN.db",
						null, 1);
				db = helper.getReadableDatabase();

				// 提醒的标志时间,3个小时之内
				long flag_time = 1000 * 60 * 60 * 3;

				Cursor cursor = db.query("EventList", null, null, null, null,
						null, null);
				while (cursor.moveToNext()) {
					String start = cursor.getString(cursor
							.getColumnIndex("start"));
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"yyyy年MM月dd日-HH:mm");
					try {
						long diff_time = dateFormat.parse(start).getTime()
								- new Date().getTime();
						if (diff_time > 0 && diff_time < flag_time) {
							String title = cursor.getString(cursor
									.getColumnIndex("title"));
							if (diff_time < diff_lastest) {
								diff_lastest = diff_time;
								lastest_title = title;
							}
							notify_titles.add(title);
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
				if (notify_titles.size() > 0) {
					hasDataToNotify.what = 1;
				} else {
					hasDataToNotify.what = 0;
				}
				handler.sendMessage(hasDataToNotify);
			}
		}.start();
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message message) {
			if (message.what == 1) {
				NotificationManager manager = (NotificationManager) getSystemService(Activity.NOTIFICATION_SERVICE);
				Intent intent = new Intent(ActivityWelcome.this,
						MainActivity.class);
				intent.putExtra("Notification", 1);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				PendingIntent pi = PendingIntent.getActivity(
						ActivityWelcome.this, 0, intent,
						PendingIntent.FLAG_UPDATE_CURRENT);
				Notification notification = new Notification(
						R.drawable.ic_launcher, "地大人温馨小提示~",
						System.currentTimeMillis());
				notification.defaults = Notification.DEFAULT_ALL;
				if (notify_titles.size() == 1) {
					notification.setLatestEventInfo(ActivityWelcome.this,
							lastest_title, "即将开始，请及时做好准备。", pi);
				} else {
					notification.setLatestEventInfo(ActivityWelcome.this,
							lastest_title + " 等" + notify_titles.size() + "件事",
							"即将开始，请及时做好准备。", pi);
				}
				manager.notify(0, notification);
			}
		}
	};

	private void init() {
		SharedPreferences perPreferences = getSharedPreferences("didaren",
				MODE_PRIVATE);
		isFirstIn = perPreferences.getBoolean("isFirstIn", true);
		if (!isFirstIn) {
			mHandler.sendEmptyMessageDelayed(GO_HOME, TIME);
		} else {
			mHandler.sendEmptyMessageDelayed(GO_GUIDE, TIME);
			Editor editor = perPreferences.edit();
			editor.putBoolean("isFirstIn", false);
			editor.commit();
		}

	}

	private void goGuide() {
		Intent i = new Intent(ActivityWelcome.this, ActivityGuide.class);
		startActivity(i);
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		finish();
	}

	private void goHome() {
		Intent i = new Intent(ActivityWelcome.this, MainActivity.class);
		startActivity(i);
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		finish();
	}
	
	private boolean isForeground(Context context, String className) {  
	       if (context == null || TextUtils.isEmpty(className)) {  
	           return false;  
	       }  
	  
	       ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);  
	       List<RunningTaskInfo> list = am.getRunningTasks(1);  
	       if (list != null && list.size() > 0) {  
	           ComponentName cpn = list.get(0).topActivity;  
	           if (className.equals(cpn.getClassName())) {  
	               return true;  
	           }  
	       }  
	  
	       return false;  
	   }  
}