package com.sparke.modules;

import com.andexert.expandablelayout.library.ExpandableLayout;
import com.example.material.R;
import com.sparke.model.Event;
import com.yalantis.guillotine.animation.GuillotineAnimation;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

	private static final long DURATION = 0;

	private GuillotineAnimation mGuillotineAnimation;
	private TextView mActionBarTitle;
	private TextView mMenuUserText;
	private Toolbar mToolbar;
	private FrameLayout mRoot;
	private ImageView mHamburger;
	private LinearLayout mMenuUser;
	private LinearLayout mMenuElectric;
	private LinearLayout mMenuScan;
	private LinearLayout mMenuExit;
	private LinearLayout mMenuActivity;
	private LinearLayout mMenuSchoolTeacher;

	private ExpandableLayout mNewsExpandableLayout;
	private TextView mNewsImportBtn;
	private TextView mNewsNoticeBtn;
	private TextView mNewsHotBtn;

	private ExpandableLayout mSchoolExpandableLayout;
	private TextView mSchoolTimeBtn;
	private TextView mSchoolCourseBtn;
	private TextView mSchoolExamBtn;

	private String isStudent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.avtivity_main);

		
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		mRoot = (FrameLayout) findViewById(R.id.root);
		mHamburger = (ImageView) findViewById(R.id.hamburger);
		mActionBarTitle = (TextView) findViewById(R.id.actionBarTitle);

		if (mToolbar != null) {
			setSupportActionBar(mToolbar);
			getSupportActionBar().setTitle(null);
		}

		// 弹出
		View menuView = LayoutInflater.from(this).inflate(R.layout.menu_layout,
				null);
		mRoot.addView(menuView);
		// 添加弹出的菜单
		mGuillotineAnimation = new GuillotineAnimation.GuillotineBuilder(
				menuView, menuView.findViewById(R.id.menu_hamburger),
				mHamburger).setStartDelay(DURATION)
				.setActionBarViewForAnimation(mToolbar).build();

		SharedPreferences pref = getSharedPreferences("Puma",
				Context.MODE_PRIVATE);
		isStudent = pref.getString("isStudent", "true");

		// 初始化菜单按钮
		InitMenu(menuView);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		int isNotification = intent.getIntExtra("Notification", 0);
		if (isNotification == 1) {
			//如果是来自事件提醒的notification
			NotificationManager notificationManager = (NotificationManager) getSystemService(Activity.NOTIFICATION_SERVICE);
			notificationManager.cancel(0);
			mGuillotineAnimation.close();
			FragmentManager manager = getSupportFragmentManager();
			FragmentSchool fragmentSchool = (FragmentSchool) manager
					.findFragmentByTag("School");
			if (fragmentSchool == null) {
				FragmentTransaction transaction = manager
						.beginTransaction();
				transaction.replace(R.id.container,
						FragmentSchool.newInstance(9), "School");
				transaction.commit();
			} else {
				if (fragmentSchool.getPostion() != 1) {
					fragmentSchool.setPosition(1);
				}
			}
		}	
	}

	public void resetMenuUserText() {
		mMenuUserText.setText(R.string.menu_user);
	}

	private void InitMenu(View menuView) {
		mMenuUser = (LinearLayout) menuView.findViewById(R.id.menu_user);
		mMenuUserText = (TextView) menuView.findViewById(R.id.menu_user_text);
		if (hasUser() != null) {
			String temp = hasUser();
			mMenuUserText.setText(temp);
		}
		mMenuUser.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentManager fragmentManager = getSupportFragmentManager();
				mGuillotineAnimation.close();
				if (mMenuUserText.getText().toString().equals("登录")) {
					if (fragmentManager.findFragmentByTag("Login") == null) {
						FragmentTransaction transaction = fragmentManager
								.beginTransaction();
						transaction.replace(R.id.container,
								FragmentLogin.newInstance(1), "Login");
						transaction.commit();
					}
				} else {
					if (fragmentManager.findFragmentByTag("Personnal") == null) {
						FragmentTransaction transaction = fragmentManager
								.beginTransaction();
						transaction.replace(R.id.container,
								FragmentPersonal.newInstance(2), "Personnal");
						transaction.commit();
					}
				}
			}
		});

		mNewsExpandableLayout = (ExpandableLayout) menuView
				.findViewById(R.id.newsExpandableLayout);
		mNewsExpandableLayout.getHeaderLayout().setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						mNewsExpandableLayout.click();
						if (mSchoolExpandableLayout.isOpened() == true) {
							mSchoolExpandableLayout.hide();
						}
					}
				});

		mNewsImportBtn = (TextView) mNewsExpandableLayout.getContentLayout()
				.findViewById(R.id.news_important);
		mNewsNoticeBtn = (TextView) mNewsExpandableLayout.getContentLayout()
				.findViewById(R.id.news_notice);
		mNewsHotBtn = (TextView) mNewsExpandableLayout.getContentLayout()
				.findViewById(R.id.news_hot);
		mNewsImportBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FragmentManager fragmentManager = getSupportFragmentManager();
				mGuillotineAnimation.close();
				FragmentNews fragment = (FragmentNews) fragmentManager
						.findFragmentByTag("News");
				if (fragment == null) {
					FragmentTransaction transaction = fragmentManager
							.beginTransaction();
					transaction.replace(R.id.container,
							FragmentNews.newInstance(4), "News");
					transaction.commit();
				} else {
					if (fragment.getPosition() != 1) {
						fragment.setPosition(1);
					}
				}
			}
		});
		mNewsNoticeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FragmentManager fragmentManager = getSupportFragmentManager();
				mGuillotineAnimation.close();
				FragmentNews fragment = (FragmentNews) fragmentManager
						.findFragmentByTag("News");
				if (fragment == null) {
					FragmentTransaction transaction = fragmentManager
							.beginTransaction();
					transaction.replace(R.id.container,
							FragmentNews.newInstance(5), "News");
					transaction.commit();
				} else {
					if (fragment.getPosition() != 0) {
						fragment.setPosition(0);
					}
				}
			}
		});
		mNewsHotBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FragmentManager fragmentManager = getSupportFragmentManager();
				mGuillotineAnimation.close();
				FragmentNews fragment = (FragmentNews) fragmentManager
						.findFragmentByTag("News");
				if (fragment == null) {
					;
					FragmentTransaction transaction = fragmentManager
							.beginTransaction();
					transaction.replace(R.id.container,
							FragmentNews.newInstance(6), "News");
					transaction.commit();
				} else {
					if (fragment.getPosition() != 2) {
						fragment.setPosition(2);
					}
				}
			}
		});

		mMenuActivity = (LinearLayout) findViewById(R.id.menu_activity);
		mMenuActivity.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FragmentManager fragmentManager = getSupportFragmentManager();
				mGuillotineAnimation.close();
				FragmentActivity fragment = (FragmentActivity) fragmentManager
						.findFragmentByTag("Activity");
				if (fragment == null) {
					FragmentTransaction transaction = fragmentManager
							.beginTransaction();
					transaction.replace(R.id.container,
							FragmentActivity.newInstance(11), "Activity");
					transaction.commit();
				}
			}
		});

		mMenuSchoolTeacher = (LinearLayout) findViewById(R.id.menu_school_teacher);
		mMenuSchoolTeacher.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!mMenuUserText.getText().toString().equals("登录")) {
					FragmentManager fragmentManager = getSupportFragmentManager();
					mGuillotineAnimation.close();
					FragmentSchoolCourse fragment = (FragmentSchoolCourse) fragmentManager
							.findFragmentByTag("course");
					if(fragment == null) {
						FragmentTransaction transaction = fragmentManager.beginTransaction();
						transaction.replace(R.id.container,
								FragmentSchoolCourse.newInstance(12), "course");
						transaction.commit();
					}
				} else {
					Toast.makeText(MainActivity.this, "请先登录",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		mSchoolExpandableLayout = (ExpandableLayout) menuView
				.findViewById(R.id.schoolExpandableLayout);
		mSchoolExpandableLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mSchoolExpandableLayout.click();
				if (mNewsExpandableLayout.isOpened() == true) {
					mNewsExpandableLayout.hide();
				}
			}
		});
		mSchoolTimeBtn = (TextView) mSchoolExpandableLayout.getContentLayout()
				.findViewById(R.id.school_time);
		mSchoolCourseBtn = (TextView) mSchoolExpandableLayout
				.getContentLayout().findViewById(R.id.school_course);
		mSchoolExamBtn = (TextView) mSchoolExpandableLayout.getContentLayout()
				.findViewById(R.id.school_exam);
		mSchoolTimeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!mMenuUserText.getText().toString().equals("登录")) {
					FragmentManager fragmentManager = getSupportFragmentManager();
					mGuillotineAnimation.close();
					FragmentSchool fragmentSchool = (FragmentSchool) fragmentManager
							.findFragmentByTag("School");
					if (fragmentSchool == null) {
						FragmentTransaction transaction = fragmentManager
								.beginTransaction();
						transaction.replace(R.id.container,
								FragmentSchool.newInstance(9), "School");
						transaction.commit();
					} else {
						if (fragmentSchool.getPostion() != 1) {
							fragmentSchool.setPosition(1);
						}
					}
				} else {
					Toast.makeText(MainActivity.this, "请先登录",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		mSchoolCourseBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!mMenuUserText.getText().toString().equals("登录")) {
					FragmentManager fragmentManager = getSupportFragmentManager();
					mGuillotineAnimation.close();
					FragmentSchool fragmentSchool = (FragmentSchool) fragmentManager
							.findFragmentByTag("School");
					if (fragmentSchool == null) {
						FragmentTransaction transaction = fragmentManager
								.beginTransaction();
						transaction.replace(R.id.container,
								FragmentSchool.newInstance(7), "School");
						transaction.commit();
					} else {
						if (fragmentSchool.getPostion() != 2) {
							fragmentSchool.setPosition(2);
						}
					}
				} else {
					Toast.makeText(MainActivity.this, "请先登录",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		mSchoolExamBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!mMenuUserText.getText().toString().equals("登录")) {
					FragmentManager fragmentManager = getSupportFragmentManager();
					mGuillotineAnimation.close();
					FragmentSchool fragmentSchool = (FragmentSchool) fragmentManager
							.findFragmentByTag("School");
					if (fragmentSchool == null) {
						FragmentTransaction transaction = fragmentManager
								.beginTransaction();
						transaction.replace(R.id.container,
								FragmentSchool.newInstance(8), "School");
						transaction.commit();
					} else {
						if (fragmentSchool.getPostion() != 3) {
							fragmentSchool.setPosition(3);
						}
					}
				} else {
					Toast.makeText(MainActivity.this, "请先登录",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		updateMenu(isStudent);

		mMenuElectric = (LinearLayout) menuView
				.findViewById(R.id.menu_electric);
		mMenuElectric.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentManager fragmentManager = getSupportFragmentManager();
				mGuillotineAnimation.close();
				if (fragmentManager.findFragmentByTag("Electric") == null) {
					FragmentTransaction transaction = fragmentManager
							.beginTransaction();
					transaction.replace(R.id.container,
							FragmentElectric.newInstance(3), "Electric");
					transaction.commit();
				}
			}
		});

		mMenuScan = (LinearLayout) menuView.findViewById(R.id.menu_scan);
		mMenuScan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!mMenuUserText.getText().toString().equals("登录")) {
					FragmentManager fragmentManager = getSupportFragmentManager();
					mGuillotineAnimation.close();
					if (fragmentManager.findFragmentByTag("Scan") == null) {
						FragmentTransaction transaction = fragmentManager
								.beginTransaction();
						transaction.replace(R.id.container,
								FragmentScanCode.newInstance(10), "Scan");
						transaction.commit();
					}
				} else {
					Toast.makeText(MainActivity.this, "请先登录",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		mMenuExit = (LinearLayout) menuView.findViewById(R.id.menu_exit);
		mMenuExit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.exit(0);
			}
		});
	}
	
	
	

	/**
	 * 判断用户是否登录过
	 * 
	 * @return 用户名
	 */
	private String hasUser() {
		SharedPreferences pref = getSharedPreferences("Puma", MODE_PRIVATE);
		return pref.getString("name", null);
	}

	/**
	 * 设置ActionBar的标题
	 * 
	 * @param number
	 */
	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mActionBarTitle.setText(R.string.menu_user);
			break;
		case 2:
			String temp = hasUser();
			mActionBarTitle.setText(temp);
			mMenuUserText.setText(temp);
			break;
		case 3:
			mActionBarTitle.setText(R.string.menu_electric);
			break;
		case 4:
			mActionBarTitle.setText(R.string.menu_news);
			break;
		case 5:
			mActionBarTitle.setText(R.string.menu_news);
			break;
		case 6:
			mActionBarTitle.setText(R.string.menu_news);
			break;
		case 7:
			mActionBarTitle.setText(R.string.school);
			break;
		case 8:
			mActionBarTitle.setText(R.string.school);
			break;
		case 9:
			mActionBarTitle.setText(R.string.school);
			break;
		case 10:
			mActionBarTitle.setText(R.string.qr_code);
			break;
		case 11:
			mActionBarTitle.setText(R.string.activity);
			break;
		case 12:
			mActionBarTitle.setText("教学日历");
			break;
		}
	}
	
	public void updateMenu(String flag) {
		if(flag.equals("true")) {
			mSchoolExpandableLayout.setVisibility(View.VISIBLE);
			mMenuSchoolTeacher.setVisibility(View.GONE);
		} else {
			mSchoolExpandableLayout.setVisibility(View.GONE);
			mMenuSchoolTeacher.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			String result = data.getExtras().getString("result");
			if (result != null) {
				FragmentScanCode fragmentScanCode = (FragmentScanCode) getSupportFragmentManager()
						.findFragmentByTag("Scan");
				if (fragmentScanCode != null) {
					fragmentScanCode.receiveActivityResult(result);
				}
			} else {
				Event event = (Event) data.getSerializableExtra("EVENT");
				int oldPos = data.getIntExtra("OLDPOSITION", -1);
				boolean isDelete = data.getBooleanExtra("DELETE", false);
				FragmentSchool fragment = (FragmentSchool) getSupportFragmentManager()
						.findFragmentByTag("School");
				if (fragment != null) {
					fragment.passModifyInfo(event, oldPos, requestCode,
							isDelete);
				}
			}
		}
	}
}
