package com.sparke.modules;

import com.dd.CircularProgressButton;
import com.example.material.R;
import com.sparke.web.spider.LoginSpider;
import com.sparke.web.util.WebSite;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentLogin extends Fragment {

	private static final String SELECTED_POSITION = "selected_menu_position";

	private EditText mUserEditText;
	private EditText mPswEditText;
	private EditText mCAPTCHAEditText;
	private ImageView mCAPTCHAView;
	private CircularProgressButton mSubmit;
	private TextView mTipText;

	private PrepareToLoginTask mTask = null;
	private LoginSpider spider = null;

	public static FragmentLogin newInstance(int number) {
		FragmentLogin fragment = new FragmentLogin();
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
		if (container == null)
			return null;
		View view = inflater.inflate(R.layout.fragment_login, container, false);
		initWidget(view);
		return view;
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mTask != null) {
			mTask.cancel(true);
		}
	}

	/**
	 * 初始化布局中的组件
	 * 
	 * @param view
	 */
	private void initWidget(View view) {
		mUserEditText = (EditText) view.findViewById(R.id.user);
		mPswEditText = (EditText) view.findViewById(R.id.psw);
		mCAPTCHAEditText = (EditText) view.findViewById(R.id.captcha);
		mTipText = (TextView) view.findViewById(R.id.tip);
		mCAPTCHAView = (ImageView) view.findViewById(R.id.captcha_view);
		mCAPTCHAView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mTask == null) {
					// 更换验证码
					String[] params = new String[1];
					params[0] = "1";
					mTask = (PrepareToLoginTask) new PrepareToLoginTask()
							.execute(params);
				}
			}
		});
		mSubmit = (CircularProgressButton) view.findViewById(R.id.submit);
		mSubmit.setIndeterminateProgressMode(true);
		mSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!mTipText.getText().toString().equals(R.string.login_tip)) {
					mTipText.setText(R.string.login_tip);
				}
				if (mTask == null) {
					if (mSubmit.getProgress() == 0) {
						mSubmit.setProgress(50);
						String[] params = new String[4];
						params[0] = "2";
						params[1] = mUserEditText.getText().toString().trim();
						params[2] = mPswEditText.getText().toString().trim();
						params[3] = mCAPTCHAEditText.getText().toString()
								.trim();
						mTask = (PrepareToLoginTask) new PrepareToLoginTask()
								.execute(params);
					} else if (mSubmit.getProgress() == 100) {
						mSubmit.setProgress(0);
					} else if (mSubmit.getProgress() == -1) {
						mSubmit.setProgress(0);
					}
				} else {
					if (mSubmit.getProgress() == -1) {
						mSubmit.setProgress(0);
					}
				}
			}
		});

		String[] params = new String[1];
		params[0] = "0";
		if (mTask != null) {
			mTask.cancel(true);
		}
		mTask = (PrepareToLoginTask) new PrepareToLoginTask().execute(params);
	}

	/**
	 * 获得碎片所在的上下文
	 * 
	 * @return
	 */
	private Context getContext() {
		return getActivity().getApplicationContext();
	}

	class PrepareToLoginTask extends AsyncTask<String, Void, Bitmap> {

		private int mKind;

		@Override
		protected Bitmap doInBackground(String... params) {
			// TODO Auto-generated method stub
			Bitmap captcha = null;
			mKind = Integer.parseInt(params[0]);
			if (WebSite.isNetworkAvailable(getContext())) {
				switch (mKind) {
				case 0:
					spider = new LoginSpider(getContext());
					if (spider.getLoginCookies() == true) {
						captcha = spider.grabCAPTCHA();
					}
					break;
				case 1:
					captcha = spider.grabCAPTCHA();
					break;
				case 2:
					if (params[1].contains("20089")) {
						if (true == spider.loginTeacher(params[1], params[2],
								params[3])) {
							if (true == spider.loginTeacherAdminSys()) {
								if (spider.getTeacherPersonalInfoFromAdminSys() == true) {
									if (spider.getTeacherCourseTableFromAdminSys() == true) {
										SharedPreferences.Editor editor = getActivity()
												.getSharedPreferences("Puma",
														Context.MODE_PRIVATE)
												.edit();
										editor.putString("isStudent", "false");
										editor.commit();

										Resources res = getResources();
										captcha = BitmapFactory.decodeResource(
												res, R.drawable.ic_launcher);
									}
								}
							}
						}
					} else {
						if (true == spider.login(params[1], params[2],
								params[3])) {
							if (true == spider.loginAdminSys()) {
								if (spider.getPersonalInfoFromAdminSys() == true) {
									if(spider.getCourseTableFromAdminSys() == true) {
										if(spider.getExamTimeFromAdminSys() == true) {
											if(spider.getScoreFromAdminSys() == true) {
												SharedPreferences.Editor editor = getActivity()
														.getSharedPreferences("Puma",
																Context.MODE_PRIVATE)
														.edit();
												editor.putString("isStudent", "true");
												editor.commit();

												Resources res = getResources();
												captcha = BitmapFactory.decodeResource(res,
														R.drawable.ic_launcher);
											}
										}
									}
								}
							}
						}
					}

					break;
				}
			}
			return captcha;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (result != null) {
				switch (mKind) {
				case 0:
					mCAPTCHAView.setImageBitmap(result);
					mTask = null;
					break;
				case 1:
					mCAPTCHAView.setImageBitmap(result);
					mTask = null;
					break;
				case 2:
					mTask = null;
					if (!spider.isSuccessful()) {
						mSubmit.setProgress(-1);
						mTipText.setText(spider.getErrorInfo());
						String[] params = new String[1];
						params[0] = "0";
						mTask = (PrepareToLoginTask) new PrepareToLoginTask()
								.execute(params);
						Toast.makeText(getContext(), spider.getErrorInfo(),
								Toast.LENGTH_SHORT).show();
					} else {
						mSubmit.setProgress(100);
						mTipText.setText("");
						FragmentManager fragmentManager = getActivity()
								.getSupportFragmentManager();
						FragmentTransaction transaction = fragmentManager
								.beginTransaction();
						transaction.replace(R.id.container,
								FragmentPersonal.newInstance(2), "Personnal");
						transaction.commit();
					}
					break;
				}
			} else {
				mSubmit.setProgress(-1);
				mTask = null;
				if (spider == null) {
					Toast.makeText(getContext(), "网络似乎开小差了，请重试",
							Toast.LENGTH_SHORT).show();
				} else {
					String[] params = new String[1];
					params[0] = "0";
					mTask = (PrepareToLoginTask) new PrepareToLoginTask()
							.execute(params);
					mTipText.setText(spider.getErrorInfo());
				}
			}
		}
	}
}
