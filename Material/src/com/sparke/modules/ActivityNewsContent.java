package com.sparke.modules;

import com.example.material.R;
import com.gc.materialdesign.views.ButtonFlat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rey.material.widget.ProgressView;
import com.sparke.util.RevealBackgroundView;
import com.sparke.web.spider.NewsContentSpider;

public class ActivityNewsContent extends Activity implements
		RevealBackgroundView.OnStateChangeListener {

	public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";
	public static final String NEWS_HREF = "news_href";
	public static final String NEWS_TITLE = "news_title";

	private static final long START_DELAY = 1000;
	private static final int MSG_START_PROGRESS = 1000;

	private RevealBackgroundView mRevealBackgroundView;
	private RelativeLayout mContainerLayout;
	private TextView mTitleTextView;
	private ImageView mBackView;
	
	private LinearLayout mProgressView;
	private ProgressView mProgress;
	
	private LinearLayout mErrorView;
	private ButtonFlat mRetryBtn;

	private WebView mContentView;
	private String mUrl;

	private Handler mHandler;

	public static void startAvtivityNewsContent(int[] startingLocation,
			Context startingActivity, String href, String title) {
		Intent intent = new Intent(startingActivity, ActivityNewsContent.class);
		intent.putExtra(ARG_REVEAL_START_LOCATION, startingLocation);
		intent.putExtra(NEWS_HREF, href);
		intent.putExtra(NEWS_TITLE, title);
		startingActivity.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news_content);
		mRevealBackgroundView = (RevealBackgroundView) findViewById(R.id.revealBackground);

		mUrl = getIntent().getStringExtra(NEWS_HREF);
		mContainerLayout = (RelativeLayout) findViewById(R.id.news_content_container);
		mTitleTextView = (TextView) findViewById(R.id.news_content_title);
		mTitleTextView.setText(getIntent().getStringExtra(NEWS_TITLE));
		mBackView = (ImageView) findViewById(R.id.news_content_back);
		mBackView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		setupRevealBackground(savedInstanceState);

		mProgressView = (LinearLayout) findViewById(R.id.news_content_progress_view);
		mProgress = (ProgressView) findViewById(R.id.news_content_progress);

		mErrorView = (LinearLayout) findViewById(R.id.news_content_error);
		mRetryBtn = (ButtonFlat) findViewById(R.id.news_content_btn_retry);
		mRetryBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mProgressView.getVisibility() == View.GONE) {
					mErrorView.setVisibility(View.GONE);
					mProgressView.setVisibility(View.VISIBLE);
					mProgress.start();
				}

			}
		});

		mContentView = (WebView) findViewById(R.id.news_content);
		mContentView.getSettings().setSupportZoom(true);
		mContentView.getSettings().setBuiltInZoomControls(true);
		mContentView.getSettings().setDisplayZoomControls(false);
		
		mHandler = new Handler(this.getMainLooper()) {
			@Override
            public void handleMessage(android.os.Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
        		case MSG_START_PROGRESS:
        			mProgressView.setVisibility(View.VISIBLE);
        			mProgress.start();
        			String[] params = new String[2];
        			new GetNewsContentTask().execute(params);
        			break;
        		default:
        			break;
        		}             
            };
		};
	}

	@Override
	protected void onResume() {
		super.onResume();
		mHandler.sendEmptyMessageDelayed(MSG_START_PROGRESS, START_DELAY);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mHandler.removeCallbacksAndMessages(null);
	}

	private void setupRevealBackground(Bundle savedInstanceState) {
		mRevealBackgroundView.setOnStateChangeListener(this);
		if (savedInstanceState == null) {
			final int[] startingLocation = getIntent().getIntArrayExtra(
					ARG_REVEAL_START_LOCATION);
			mRevealBackgroundView.getViewTreeObserver().addOnPreDrawListener(
					new ViewTreeObserver.OnPreDrawListener() {

						@Override
						public boolean onPreDraw() {
							// TODO Auto-generated method stub
							mRevealBackgroundView.getViewTreeObserver()
									.removeOnPreDrawListener(this);
							mRevealBackgroundView
									.startFromLocation(startingLocation);
							return false;
						}
					});
		} else {
			mRevealBackgroundView.setToFinishedFrame();
		}
	}

	@Override
	public void onStateChange(int state) {
		// TODO Auto-generated method stub
		if (RevealBackgroundView.STATE_FINISHED == state) {
			mContainerLayout.setVisibility(View.VISIBLE);
		} else {
			mContainerLayout.setVisibility(View.INVISIBLE);
		}
	}

	class GetNewsContentTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			NewsContentSpider spider = new NewsContentSpider(mUrl,
					ActivityNewsContent.this);
			String result = spider.getNewsContent();
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			mProgress.stop();
			mProgressView.setVisibility(View.GONE);
			if (result.isEmpty()) {
				mErrorView.setVisibility(View.VISIBLE);
			} else {
				mContentView.setVisibility(View.VISIBLE);
				mContentView.setBackgroundColor(0);
				mContentView.loadDataWithBaseURL(null, result, "text/html", "utf-8", null);
			}
		}
	}
}
