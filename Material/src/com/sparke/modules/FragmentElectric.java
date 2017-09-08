package com.sparke.modules;

import java.sql.Date;
import java.text.SimpleDateFormat;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.dd.CircularProgressButton;
import com.example.material.R;
import com.rey.material.widget.Spinner;
import com.rey.material.widget.Spinner.OnItemSelectedListener;
import com.sparke.web.util.GetHttpClient;
import com.sparke.web.util.WebSite;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentElectric extends Fragment {

	private static final String SELECTED_POSITION = "selected_menu_position";

	private Spinner area_spinner = null;
	private Spinner bui_spinner = null;
	private EditText room_editText;
	private CircularProgressButton query_button;
	private TextView remain_textView;
	private TextView moredays_textView;
	private TextView warm_textView;
	private String area_id;
	private String bui_id;
	private String room_id;
	ArrayAdapter<String> area_adapter = null;
	ArrayAdapter<String> bui_adapter = null;
	String remain_is = null;
	String days_is = null;
	private SharedPreferences pref;
	private SharedPreferences.Editor editor;
	private boolean has_query = false;
	private int area_index;
	private int bui_index;

	private String[] area_strings = new String[] { "东区", "西区", "北区" };
	private String[] east_bui_strings = new String[] { "研一", "研二", "研三", "东16",
			"东17", "东34", "东35" };
	private String[] east_buiid_strings = new String[] { "01", "02", "03",
			"16", "17", "34", "35" };
	private String[] west_bui_strings = new String[] { "新峰A", "新峰B", "新峰C",
			"51", "52", "53", "54", "55", "56", "57", "58", "60", "62", "63" };
	private String[] west_buiid_strings = new String[] { "101", "102", "103",
			"51", "52", "53", "54", "55", "56", "57", "58", "60", "62", "63" };
	private String[] north_bui_strings = new String[] { "1栋", "2栋", "3栋", "4栋",
			"5栋", "6栋", "7栋", "8栋", "9栋", "10栋", "11栋", "12栋", "13栋", "14栋",
			"15栋", "16栋", "17栋", "18栋", "19栋", "20栋", "21栋", "22栋", "23栋",
			"24栋", "25栋" };
	private String[] north_buiid_strings = new String[] { "01", "02", "03",
			"04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14",
			"15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25" };

	public static FragmentElectric newInstance(int number) {
		FragmentElectric fragment = new FragmentElectric();
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
		View view = inflater.inflate(R.layout.fragment_electric, container,
				false);

		// 本地存储
		pref = getContext().getSharedPreferences("elecQuery",
				Activity.MODE_PRIVATE);
		editor = getContext().getSharedPreferences("elecQuery",
				Activity.MODE_PRIVATE).edit();
		has_query = pref.getBoolean("has_query", false);

		if (has_query) {
			area_id = pref.getString("area_id", "");
			bui_id = pref.getString("bui_id", "");
			room_id = pref.getString("room_id", "");
			area_index = pref.getInt("area_index", 0);
			bui_index = pref.getInt("bui_index", 0);
		}
		setSpinner(view);
		return view;
	}

	private void setSpinner(View view) {
		area_spinner = (Spinner) view.findViewById(R.id.electric_area_spinner);
		bui_spinner = (Spinner) view.findViewById(R.id.bui_spinner);
		room_editText = (EditText) view.findViewById(R.id.room_editText);
		query_button = (CircularProgressButton) view
				.findViewById(R.id.query_button);
		query_button.setIndeterminateProgressMode(true);
		remain_textView = (TextView) view.findViewById(R.id.remain_textView);
		moredays_textView = (TextView) view
				.findViewById(R.id.moredays_textView);
		warm_textView = (TextView) view.findViewById(R.id.warm_textView);

		room_editText.setText(room_id);
		// 绑定适配器
		area_spinner.setSelected(false);
		area_adapter = new ArrayAdapter<String>(getContext(), R.layout.row_spn,
				area_strings);
		area_adapter.setDropDownViewResource(R.layout.row_spn_dropdown);
		area_spinner.setAdapter(area_adapter);

		bui_spinner = (Spinner) view.findViewById(R.id.bui_spinner);
		bui_adapter = new ArrayAdapter<String>(getContext(), R.layout.row_spn);

		if (has_query) {
			area_spinner.setSelection(area_index);
			if (area_id.equals("01")) {
				bui_adapter = new ArrayAdapter<String>(getContext(),
						R.layout.row_spn, east_bui_strings);
			} else if (area_id.equals("03")) {
				bui_adapter = new ArrayAdapter<String>(getContext(),
						R.layout.row_spn, west_bui_strings);
			} else if (area_id.equals("04")) {
				bui_adapter = new ArrayAdapter<String>(getContext(),
						R.layout.row_spn, north_bui_strings);
			}
		} else {
			bui_adapter = new ArrayAdapter<String>(getContext(),
					R.layout.row_spn, east_bui_strings);
			bui_adapter.setDropDownViewResource(R.layout.row_spn_dropdown);
		}
		bui_adapter.setDropDownViewResource(R.layout.row_spn_dropdown);
		bui_spinner.setAdapter(bui_adapter);
		bui_spinner.setSelection(bui_index);

		area_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(Spinner parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				// position为当前选中的值的序号
				editor.clear().commit();
				editor.putInt("area_index", position);
				if (position == 0) {
					// 选择东区
					area_id = "01";
					editor.putString("area_id", "01");
					bui_adapter = new ArrayAdapter<String>(getContext(),
							R.layout.row_spn, east_bui_strings);
					bui_adapter
							.setDropDownViewResource(R.layout.row_spn_dropdown);
					bui_spinner.setAdapter(bui_adapter);
				} else if (position == 1) {
					// 选择西区
					area_id = "03";
					editor.putString("area_id", "03");
					bui_adapter = new ArrayAdapter<String>(getContext(),
							R.layout.row_spn, west_bui_strings);
					bui_adapter
							.setDropDownViewResource(R.layout.row_spn_dropdown);
					bui_spinner.setAdapter(bui_adapter);
				} else if (position == 2) {
					// 选择北区
					area_id = "04";
					editor.putString("area_id", "04");
					bui_adapter = new ArrayAdapter<String>(getContext(),
							R.layout.row_spn, north_bui_strings);
					bui_adapter
							.setDropDownViewResource(R.layout.row_spn_dropdown);
					bui_spinner.setAdapter(bui_adapter);
				}
			}
		});

		bui_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(Spinner parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				editor.putInt("bui_index", position);
				if (area_id == "01") {
					// 东区
					bui_id = east_buiid_strings[position];

				} else if (area_id == "03") {
					// 西区
					bui_id = west_buiid_strings[position];
				} else if (area_id == "04") {
					// 北区
					bui_id = north_buiid_strings[position];
				}
				editor.putString("bui_id", bui_id);
			}
		});

		query_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (query_button.getProgress() == 0) {
					query_button.setProgress(50);
					if (!WebSite.isNetworkAvailable(getContext())) {
						// 如果没有网络情况点击，弹出没有网络提示语
						Toast.makeText(getContext(), "没有网络，无法查询~",
								Toast.LENGTH_SHORT).show();
						query_button.setProgress(-1);
					} else {
						// 有网络
						if (!room_editText.getText().toString().trim()
								.equals("")) {
							// 如果宿舍号输入不为空
							String[] urls = new String[2];
							room_id = room_editText.getText().toString().trim();
							editor.putString("room_id", room_id);
							editor.putBoolean("has_query", true);
							editor.commit();
							urls[0] = "http://202.114.196.179:8080/get_digit.aspx?areaid="
									+ area_id
									+ "&buiid="
									+ bui_id
									+ "&roomid="
									+ room_id;
							SimpleDateFormat dateFormat = new SimpleDateFormat(
									"yyyyMMdd");
							String current_date = dateFormat.format(new Date(
									System.currentTimeMillis()));
							String before_date = dateFormat.format(new Date(
									System.currentTimeMillis() - 1000 * 60 * 60
											* 24 * 5));
							urls[1] = "http://202.114.196.179:8080/get_used.aspx?areaid="
									+ area_id
									+ "&buiid="
									+ bui_id
									+ "&roomid="
									+ room_id
									+ "&begin="
									+ before_date
									+ "&end=" + current_date;
							new ElectricTask().execute(urls);
						} else {
							Toast.makeText(getContext(), "数据输入有误！",
									Toast.LENGTH_SHORT).show();
							query_button.setProgress(-1);
						}
					}
				} else if (query_button.getProgress() == 100) {
					query_button.setProgress(0);
					remain_textView.setText("");
					moredays_textView.setText("");
					warm_textView.setVisibility(View.GONE);
				} else if (query_button.getProgress() == -1) {
					query_button.setProgress(0);
					remain_textView.setText("");
					moredays_textView.setText("");
					warm_textView.setVisibility(View.GONE);
				}

			}

		});
	}

	class ElectricTask extends AsyncTask<String, Void, String[]> {

		@Override
		protected String[] doInBackground(String... params) {
			// TODO Auto-generated method stub
			String[] result = new String[2];
			String remain_uri = params[0];
			String used_uri = params[1];
			try {
				HttpClient httpClient = GetHttpClient.getDefaultHttpClient();
				HttpGet httpGet = new HttpGet(remain_uri);
				HttpResponse response = httpClient.execute(httpGet);
				byte[] bResult = EntityUtils.toByteArray(response.getEntity());
				result[0] = new String(bResult, "UTF-8");
				httpClient.getConnectionManager().shutdown();

				HttpClient httpClient2 = GetHttpClient.getDefaultHttpClient();
				HttpGet httpGet2 = new HttpGet(used_uri);
				HttpResponse response2 = httpClient2.execute(httpGet2);
				bResult = EntityUtils.toByteArray(response2.getEntity());
				result[1] = new String(bResult, "UTF-8");
				httpClient2.getConnectionManager().shutdown();

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(String[] result) {
			if (result[0] != null && result[1] != null) {
				getData(result[0], result[1]);
			} else {
				query_button.setProgress(-1);
				remain_textView.setText("服务器繁忙，请稍后查询~");
			}
		}
	}

	private void getData(String remain_is, String days_is) {
		query_button.setProgress(100);
		Double remain_digit = null;
		Double has_used = null;
		int moredays = 0;

		try {
			JSONObject jObject = new JSONObject(remain_is);
			remain_digit = jObject.getDouble("remain_digit");
			remain_textView.setText("剩余用电量：" + remain_digit);
			JSONObject days_jObject = new JSONObject(days_is);
			has_used = days_jObject.getDouble("used");
			moredays = countDays(remain_digit, has_used);
			moredays_textView.setText("预计支撑天数：" + moredays);
			warm_textView.setVisibility(View.VISIBLE);
		} catch (Exception exc) {
			exc.printStackTrace();
			query_button.setProgress(-1);
		}
		query_button.setProgress(100);
	}

	public int countDays(Double remain_digit, Double has_used) {
		int moredays = 0;
		moredays = (int) (remain_digit / (has_used / 5.0));
		return moredays;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 获得碎片所在的上下文
	 * 
	 * @return
	 */
	private Context getContext() {
		return getActivity().getApplicationContext();
	}
}
