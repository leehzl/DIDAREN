package com.sparke.modules;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.example.material.R;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.sparke.database_task.DatabaseHelper;
import com.sparke.model.Score;
import com.sparke.web.spider.LoginSpider;
import com.sparke.web.util.WebSite;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentSchoolExam extends Fragment {

	private static final String SELECTED_POSITION = "selected_menu_position";
	private static final long START_DELAY = 500;
	private static final int MSG_START_PROGRESS = 55;
	private static final int MSG_SCROLL_PROGRESS = 56;

	private DatabaseHelper helper;
	private SQLiteDatabase db;

	private LinearLayout linearLayout_score;
	private FrameLayout button_score_compute_gpa;
	private TextView textView_score_gpa;
	private TextView textView_score_warming;
	private TextView textView_score_null;
	
	private TextView select_all;

	private TextView mTitle;

	private Handler mHandler;

	private String mCurrentYear;
	private String mCurrentTerm;

	private String mSelectedYear;
	private String mSelectedTerm;

	private boolean isQuery = false;
	
	private boolean isSelectAll = false;

	private ProgressDialog mProgressDialog;
	private LoginSpider mSpider;
	
	private ScrollView mScrollView;

	// 各种布局
	private LinearLayout.LayoutParams LP_MW = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.MATCH_PARENT,
			LinearLayout.LayoutParams.WRAP_CONTENT);
	private LinearLayout.LayoutParams layout_course = new LinearLayout.LayoutParams(
			0, LinearLayout.LayoutParams.MATCH_PARENT, 5);
	private LinearLayout.LayoutParams layout_credit = new LinearLayout.LayoutParams(
			0, LinearLayout.LayoutParams.MATCH_PARENT, 2);
	private LinearLayout.LayoutParams layout_score = new LinearLayout.LayoutParams(
			0, LinearLayout.LayoutParams.MATCH_PARENT, 2);
	private LinearLayout.LayoutParams layout_choose = new LinearLayout.LayoutParams(
			0, LinearLayout.LayoutParams.MATCH_PARENT, 2);
	private LinearLayout.LayoutParams layout_style = new LinearLayout.LayoutParams(
			0, LinearLayout.LayoutParams.MATCH_PARENT, 2);

	// 成绩列表底色标志
	private boolean column_background_flag = true;

	// 绩点
	private double gpa = 0;

	// 总学分
	private double total_credit = 0;

	// 存放各个分数。用来计算学分绩点
	private List<Double> list_credit = new ArrayList<Double>();
	private List<Double> list_grade = new ArrayList<Double>();
	private List<CheckBox> list_checkBox = new ArrayList<CheckBox>();

	// 记录choose的id
	private int choose_id = 0;

	public static FragmentSchoolExam newInstance(int number) {
		FragmentSchoolExam fragment = new FragmentSchoolExam();
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
		View view = inflater.inflate(R.layout.fragment_score, container, false);
		helper = new DatabaseHelper(getContext(), "DIDAREN.db", null, 1);
		db = helper.getReadableDatabase();
		linearLayout_score = (LinearLayout) view
				.findViewById(R.id.linearLayout_score);
		button_score_compute_gpa = (FrameLayout) view
				.findViewById(R.id.button_score_compute_gpa);
		textView_score_gpa = (TextView) view
				.findViewById(R.id.textView_score_gpa);
		textView_score_warming = (TextView) view
				.findViewById(R.id.textView_score_warming);
		textView_score_null = (TextView) view
				.findViewById(R.id.textView_score_null);
		
		mScrollView = (ScrollView) view.findViewById(R.id.score_scrollview);
		
		select_all = (TextView) view.findViewById(R.id.score_select_all);
		select_all.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(list_checkBox.size() != 0) {
					for(CheckBox checkBox : list_checkBox) {
						if(isSelectAll == false) {
							checkBox.setChecked(true);
						} else {
							checkBox.setChecked(false);
						}
					}
					if(isSelectAll == false) {
						isSelectAll = true;
					} else {
						isSelectAll = false;
					}
				}
			}
		});

		SharedPreferences preferences = getActivity().getSharedPreferences(
				"course_year", Context.MODE_PRIVATE);

		mProgressDialog = new ProgressDialog(getActivity());
		mProgressDialog.setTitle(getResources().getString(
				R.string.loading_text3));
		mProgressDialog.setCancelable(false);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	
		mCurrentYear = preferences.getString("current_year", null);
		mSelectedYear = preferences.getString("select_year", null);
		mCurrentTerm = preferences.getString("current_term", null);
		mSelectedTerm = preferences.getString("select_term", null);

		mHandler = new Handler(getActivity().getMainLooper()) {
			@Override
			public void handleMessage(android.os.Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case MSG_START_PROGRESS:
					mProgressDialog.show();
					mTitle.setText(getTitle(mSelectedYear, mSelectedTerm));
					String[] params = new String[3];
					params[0] = "0";
					params[1] = mSelectedYear;
					params[2] = mSelectedTerm;
					new ScoreDBTask().execute(params);
					break;
				case MSG_SCROLL_PROGRESS:
					mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
					break;
				default:
					break;
				}
			};
		};

		// createScoreList();

		button_score_compute_gpa.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (list_checkBox.size() != 0) {
					int count=0;
					for (int i = 0; i < list_checkBox.size(); i++) {
						if (list_checkBox.get(i).isChecked()) {
							// 如果选中该行，则对相应的绩点进行计算
							double temp1 = list_credit.get(i);
							double temp2 = list_grade.get(i);
							gpa += temp1 * temp2;
							total_credit += temp1;
							count++;
						}
					}
					
					if(count != 0) {
						gpa = gpa / total_credit;

						// 对绩点进行保留2位小数，并且四舍五入计算
						BigDecimal b = new BigDecimal(gpa);
						gpa = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

						textView_score_gpa.setText("绩点：" + gpa);
						textView_score_gpa.setVisibility(View.VISIBLE);
						textView_score_warming.setVisibility(View.VISIBLE);
						
						mHandler.sendEmptyMessageDelayed(MSG_SCROLL_PROGRESS, START_DELAY);

						// 重置
						gpa = 0;
						total_credit = 0;
						count = 0;
						
					} else {
						Toast.makeText(getActivity(), "请选择需要计算绩点的课程", Toast.LENGTH_SHORT).show();
					}				
				} else {
					Toast.makeText(getActivity(), "当前学期无考试信息", Toast.LENGTH_SHORT).show();
				}
			}
		});

		mTitle = (TextView) view.findViewById(R.id.school_score_title);
		mTitle.setText(getTitle(mSelectedYear, mSelectedTerm));

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		mHandler.sendEmptyMessageDelayed(MSG_START_PROGRESS, START_DELAY);
	}

	@Override
	public void onPause() {
		super.onPause();
		mHandler.removeCallbacksAndMessages(null);
	}

	public String getTitle(String year, String term) {
		String title = "";
		if (year.equals("全部")) {
			if (term.equals("全部")) {
				title = title + "全部学年 全部成绩";
			} else {
				title = title + "全部学年 " + "第" + term + "学期成绩";
			}
		} else {
			if (term.equals("全部")) {
				title = title + year + "学年 " + "全部成绩";
			} else {
				title = title + year + "学年 " + "第" + term + "学期成绩";
			}
		}
		return title;
	}

	public void queryScore(String year, String term) {
		SharedPreferences.Editor editor = getActivity().getSharedPreferences(
				"course_year", Context.MODE_PRIVATE).edit();
		editor.putBoolean(mCurrentYear+mCurrentTerm, false);
		editor.commit();
		
		mProgressDialog.show();
		mSelectedYear = year;
		mSelectedTerm = term;
		mTitle.setText(getTitle(year, term));
		
		list_checkBox.clear();
		list_credit.clear();
		list_grade.clear();
		
		
		textView_score_gpa.setVisibility(View.GONE);
		textView_score_warming.setVisibility(View.GONE);
		textView_score_null.setVisibility(View.GONE);
		String[] params = new String[3];
		params[0] = "1";
		params[1] = year;
		params[2] = term;
		new ScoreDBTask().execute(params);
	}

	private LinearLayout addColumn(Score score, int checkBox_id) {
		// TODO Auto-generated method stub
		LinearLayout linearLayout_column = new LinearLayout(getContext());
		linearLayout_column.setOrientation(LinearLayout.HORIZONTAL);
		TextView textView_course = new TextView(getContext());
		TextView textView_credit = new TextView(getContext());
		TextView textView_style = new TextView(getContext());
		TextView textView_score = new TextView(getContext());
		CheckBox checkBox_choose = new CheckBox(getContext());
		textView_course.setLayoutParams(layout_course);
		textView_credit.setLayoutParams(layout_credit);
		textView_style.setLayoutParams(layout_style);
		textView_score.setLayoutParams(layout_score);
		checkBox_choose.setLayoutParams(layout_choose);
		textView_course.setGravity(Gravity.CENTER);
		textView_credit.setGravity(Gravity.CENTER);
		textView_style.setGravity(Gravity.CENTER);
		textView_score.setGravity(Gravity.CENTER);
		checkBox_choose.setGravity(Gravity.CENTER);
		textView_course.setTextColor(android.graphics.Color.BLACK);
		textView_credit.setTextColor(android.graphics.Color.BLACK);
		textView_style.setTextColor(android.graphics.Color.BLACK);
		textView_score.setTextColor(android.graphics.Color.BLACK);

		String course = score.getCourse();
		String credit = score.getCredit();
		String style = score.getStyle();
		String strScore = score.getScore();
		String grade = score.getGrade();

		checkBox_choose.setId(choose_id);
		list_checkBox.add(checkBox_choose);
		list_credit.add(Double.parseDouble(credit));
		list_grade.add(Double.parseDouble(grade));

		if (column_background_flag) {
			linearLayout_column.setBackgroundColor(getActivity().getResources().getColor(R.color.danny_white));
			column_background_flag = false;
		} else {
			linearLayout_column
					.setBackgroundColor(getActivity().getResources().getColor(R.color.light_gray));
			column_background_flag = true;
		}

		textView_course.setText(course);
		textView_credit.setText(credit);
		textView_style.setText(style);
		textView_score.setText(strScore);
		linearLayout_column.addView(textView_course);
		linearLayout_column.addView(textView_credit);
		linearLayout_column.addView(textView_style);
		linearLayout_column.addView(textView_score);
		linearLayout_column.addView(checkBox_choose);

		return linearLayout_column;
	}

	class ScoreDBTask extends AsyncTask<String, Void, ArrayList<Score>> {

		private int mKind;

		@Override
		protected ArrayList<Score> doInBackground(String... params) {
			// TODO Auto-generated method stub
			ArrayList<Score> result = new ArrayList<Score>();
			mKind = Integer.parseInt(params[0]);
			String year = params[1];
			String term = params[2];

			SharedPreferences preferences = getActivity().getSharedPreferences(
					"course_year", Context.MODE_PRIVATE);
			boolean isStore = preferences.getBoolean(year + term, false);
			isQuery = isStore;
			Cursor cursor = null;
			if (year.equals("全部")) {
				if (term.equals("全部")) {
					if (isStore == true) {
						cursor = db.rawQuery(
								"select * from Score order by year,term", null);
					}
				} else {
					if (isStore == true) {
						cursor = db
								.rawQuery(
										"select * from Score where term=? order by year,term",
										new String[] { params[2] });
					}
				}
			} else {
				if (term.equals("全部")) {
					if (isStore == true) {
						cursor = db
								.rawQuery(
										"select * from Score where year=? order by year,term",
										new String[] { params[1] });
					}
				} else {
					if (isStore == true) {
						cursor = db
								.rawQuery(
										"select * from Score where year=? and term=? order by year,term",
										new String[] { params[1], params[2] });
					}
				}
			}

			if (cursor != null && cursor.moveToFirst()) {
				do {
					Score temp = new Score();
					temp.setYear(params[1]);
					temp.setTerm(params[2]);
					temp.setCourse(cursor.getString(cursor
							.getColumnIndex("course")));
					temp.setStyle(cursor.getString(cursor
							.getColumnIndex("style")));
					temp.setCredit(cursor.getString(cursor
							.getColumnIndex("credit")));
					temp.setScore(cursor.getString(cursor
							.getColumnIndex("score")));
					temp.setGrade(cursor.getString(cursor
							.getColumnIndex("grade")));
					temp.setNumber(cursor.getString(cursor
							.getColumnIndex("number")));
					result.add(temp);
				} while (cursor.moveToNext());
			}
			return result;
		}

		@Override
		protected void onPostExecute(ArrayList<Score> result) {
			if (linearLayout_score != null) {
				linearLayout_score.removeAllViews();
			}

			if (!result.isEmpty()) {
				storeFlag();
				SharedPreferences.Editor editor = getActivity().getSharedPreferences("course_year", Context.MODE_PRIVATE).edit();
				editor.putString("select_year", mSelectedYear);
				editor.putString("select_term", mSelectedTerm);
				editor.commit();
				mProgressDialog.dismiss();
				for (Score score : result) {
					LinearLayout linearLayout_column = new LinearLayout(
							getContext());
					linearLayout_column = addColumn(score, choose_id);
					linearLayout_score.addView(linearLayout_column, LP_MW);
					choose_id++;
				}
			} else {
				switch (mKind) {
				case 0:
					mProgressDialog.dismiss();
					textView_score_gpa.setVisibility(View.GONE);
					textView_score_warming.setVisibility(View.GONE);
					textView_score_null.setVisibility(View.VISIBLE);
					break;
				case 1:
					if (isQuery == false) {
						String[] params = new String[3];
						params[0] = "0";
						new ScoreNetWorkTask().execute(params);
					} else {
						mProgressDialog.dismiss();
						textView_score_gpa.setVisibility(View.GONE);
						textView_score_warming.setVisibility(View.GONE);
						textView_score_null.setVisibility(View.VISIBLE);
					}
					break;
				case 2:
					mProgressDialog.dismiss();
					textView_score_gpa.setVisibility(View.GONE);
					textView_score_warming.setVisibility(View.GONE);
					textView_score_null.setVisibility(View.VISIBLE);
					break;
				}
			}
		}

	}

	class ScoreNetWorkTask extends AsyncTask<String, Void, Bitmap> {

		private int mKind;

		@Override
		protected Bitmap doInBackground(String... params) {
			// TODO Auto-generated method stub
			Bitmap captcha = null;
			mKind = Integer.parseInt(params[0]);
			if (WebSite.isNetworkAvailable(getContext())) {
				switch (mKind) {
				case 0:
					mSpider = new LoginSpider(getContext());
					if (mSpider.getLoginCookies() == true) {
						captcha = mSpider.grabCAPTCHA();
					}
					break;
				case 1:
					captcha = mSpider.grabCAPTCHA();
					break;
				case 2:
					if (true == mSpider.login(params[1], params[2], params[3])) {
						if (true == mSpider.loginAdminSys()) {
							mSpider.getScoreFromAdminSys();
							mSpider.getSelectedTermScoreFromAdminSys(params[4],
									params[5]);
						}
					}
					Resources res = getResources();
					captcha = BitmapFactory.decodeResource(res,
							R.drawable.ic_launcher);
					break;
				}
			}
			return captcha;
		}

		@Override
		protected void onPostExecute(final Bitmap result) {
			mProgressDialog.dismiss();
			if (result != null) {
				switch (mKind) {
				case 0:
					Dialog.Builder builder = new SimpleDialog.Builder(
							R.style.SimpleDialogLight) {
						@Override
						protected void onBuildDone(Dialog dialog) {
							dialog.layoutParams(
									ViewGroup.LayoutParams.MATCH_PARENT,
									ViewGroup.LayoutParams.WRAP_CONTENT);
							ImageView captcha = (ImageView) dialog
									.findViewById(R.id.score_captcha);
							captcha.setImageBitmap(result);
						}

						@Override
						public void onPositiveActionClicked(
								DialogFragment fragment) {
							EditText ediText = (EditText) fragment.getDialog()
									.findViewById(R.id.score_input);
							String[] params = new String[6];
							params[0] = "2";
							SharedPreferences preferences = getActivity()
									.getSharedPreferences("Puma",
											Context.MODE_PRIVATE);
							params[1] = preferences.getString("number", "");
							params[2] = preferences.getString("psw", "");
							params[3] = ediText.getText().toString().trim();
							params[4] = mSelectedYear;
							params[5] = mSelectedTerm;
							new ScoreNetWorkTask().execute(params);
							mProgressDialog.show();
							super.onPositiveActionClicked(fragment);
						}

						@Override
						public void onNegativeActionClicked(
								DialogFragment fragment) {
							super.onNegativeActionClicked(fragment);
						}
					};
					builder.title("输入验证码").positiveAction("确定")
							.negativeAction("取消")
							.contentView(R.layout.dialog_input_captcha);
					DialogFragment fragment = DialogFragment
							.newInstance(builder);
					fragment.show(getChildFragmentManager(), "");
					break;
				case 1:
					// mSrc.setImageBitmap(result);
					break;
				case 2:
					storeFlag();
					mProgressDialog.show();
					String[] params = new String[3];
					params[0] = "2";
					params[1] = mSelectedYear;
					params[2] = mSelectedTerm;
					new ScoreDBTask().execute(params);
					break;
				}
			} else {
				Toast.makeText(getContext(), "确认网络连接后，请重试", Toast.LENGTH_SHORT)
						.show();
				mTitle.setText(getTitle(mCurrentYear, mCurrentTerm));
				textView_score_gpa.setVisibility(View.GONE);
				textView_score_warming.setVisibility(View.GONE);
				textView_score_null.setVisibility(View.GONE);
			}
		}
	}

	public void storeFlag() {
		SharedPreferences preferences = getActivity().getSharedPreferences(
				"course_year", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(preferences.getString("second", null) + "1", true);
		editor.putBoolean(mSelectedYear + mSelectedTerm, true);
		if (mSelectedYear.equals("全部")) {
			if (mSelectedTerm.equals("全部")) {
				editor.putBoolean(preferences.getString("second", null) + "1",
						true);
				editor.putBoolean(preferences.getString("second", null) + "2",
						true);
				editor.putBoolean(preferences.getString("second", null) + "全部",
						true);
				editor.putBoolean(preferences.getString("third", null) + "1",
						true);
				editor.putBoolean(preferences.getString("third", null) + "2",
						true);
				editor.putBoolean(preferences.getString("third", null) + "全部",
						true);
				editor.putBoolean(preferences.getString("forth", null) + "1",
						true);
				editor.putBoolean(preferences.getString("forth", null) + "2",
						true);
				editor.putBoolean(preferences.getString("forth", null) + "全部",
						true);
				editor.putBoolean(preferences.getString("fifth", null) + "1",
						true);
				editor.putBoolean(preferences.getString("fifth", null) + "2",
						true);
				editor.putBoolean(preferences.getString("fifth", null) + "全部",
						true);
			} else if (mSelectedTerm.equals("1")) {
				editor.putBoolean(preferences.getString("second", null) + "1",
						true);
				editor.putBoolean(preferences.getString("third", null) + "1",
						true);
				editor.putBoolean(preferences.getString("forth", null) + "1",
						true);
				editor.putBoolean(preferences.getString("fifth", null) + "1",
						true);
			} else {
				editor.putBoolean(preferences.getString("second", null) + "2",
						true);
				editor.putBoolean(preferences.getString("third", null) + "2",
						true);
				editor.putBoolean(preferences.getString("forth", null) + "2",
						true);
				editor.putBoolean(preferences.getString("fifth", null) + "2",
						true);
			}
		} else {
			if (mSelectedTerm.equals("全部")) {
				editor.putBoolean(mSelectedYear + "1", true);
				editor.putBoolean(mSelectedYear + "2", true);
			} else {
				editor.putBoolean(mSelectedYear + mSelectedTerm, true);
			}
		}

		editor.commit();
	}

	/**
	 * 获得碎片所在的上下文
	 * 
	 * @return context
	 */
	private Context getContext() {

		return getActivity();
	}

}
