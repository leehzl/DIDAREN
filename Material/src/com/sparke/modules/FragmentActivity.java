package com.sparke.modules;

import java.util.ArrayList;

import com.example.material.R;
import com.sparke.model.News;
import com.sparke.model.NewsAdapter;
import com.sparke.web.spider.NewsListSpider;
import com.sparke.web.util.WebSite;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class FragmentActivity extends Fragment implements OnRefreshListener {

	private static final String SELECTED_POSITION = "selected_menu_position";

	private RecyclerView mRecyclerView;
	private NewsAdapter mAdapter;
	private LinearLayoutManager mLinearLayoutManager;
	private ArrayList<News> mDataList = new ArrayList<News>();

	private SwipeRefreshLayout mSwipeRefreshLayout;

	private int visibleThreshold = 1;
	private int lastVisibleItem, totalItemCount;
	private boolean loading;

	private NewsListSpider mSpider = null;
	private ActivityTask mTask = null;

	public FragmentActivity() {
	}

	public static FragmentActivity newInstance(int number) {
		FragmentActivity fragment = new FragmentActivity();
		Bundle args = new Bundle();
		args.putInt(SELECTED_POSITION, number);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		int n = getArguments().getInt(SELECTED_POSITION);
		((MainActivity) activity).onSectionAttached(n);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (container == null)
			return null;
		View view = inflater.inflate(R.layout.fragment_activity, container,
				false);

		mSwipeRefreshLayout = (SwipeRefreshLayout) view
				.findViewById(R.id.activity_refresh);
		mSwipeRefreshLayout.setColorSchemeResources(R.color.danny_background,
				R.color.cpb_blue, R.color.cpb_green_dark);
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mRecyclerView = (RecyclerView) view.findViewById(R.id.activity_list);
		mRecyclerView.setHasFixedSize(true);
		mLinearLayoutManager = new LinearLayoutManager(getActivity());
		mRecyclerView.setLayoutManager(mLinearLayoutManager);
		mAdapter = new NewsAdapter(mDataList, getActivity());
		mRecyclerView.setAdapter(mAdapter);
		mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				totalItemCount = mLinearLayoutManager.getItemCount();
				lastVisibleItem = mLinearLayoutManager
						.findLastVisibleItemPosition();
				if (!loading
						&& totalItemCount <= (lastVisibleItem + visibleThreshold)) {
					int currentPage = mDataList.size() / 10;
					if (mDataList.size() % 10 != 0) {
						currentPage++;
					}
					if (currentPage < mSpider.getPages()) {
						mDataList.add(null);
						mAdapter.notifyItemInserted(mDataList.size() - 1);
						String[] params = new String[2];
						params[0] = "1";
						params[1] = WebSite.ACTIVITY_WEBSITE + "page/"
								+ (currentPage + 1) + "/";
						mTask = (ActivityTask) (new ActivityTask().execute(params));
						loading = true;
					} else {
						Toast.makeText(getActivity(), "已是最后一页",
								Toast.LENGTH_LONG).show();
					}
				}
			}
		});
		
		mSwipeRefreshLayout.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mSwipeRefreshLayout.setRefreshing(true);
				String[] params = new String[2];
				params[0] = "0";
				params[1] = WebSite.ACTIVITY_WEBSITE;
				mTask = (ActivityTask) (new ActivityTask().execute(params));
			}
		}, 1000);
		return view;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if(mTask != null) {
			if(mSwipeRefreshLayout.isRefreshing() == true) {
				mSwipeRefreshLayout.setRefreshing(false);
			}
			mTask.cancel(true);
		}
	};

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		String[] params = new String[2];
		params[0] = "0";
		params[1] = WebSite.ACTIVITY_WEBSITE;
		mTask = (ActivityTask) (new ActivityTask().execute(params));
	}
	
	class ActivityTask extends AsyncTask<String, Void, ArrayList<News>> {

		private int mMode;

		@Override
		protected ArrayList<News> doInBackground(String... params) {
			// TODO Auto-generated method stub
			ArrayList<News> list = new ArrayList<News>();
			mMode = Integer.parseInt(params[0]);
			String url = params[1];
			if (WebSite.isNetworkAvailable(getActivity())) {
				if (mSpider == null) {
					mSpider = new NewsListSpider(url);
				} else {
					mSpider.setUrl(url);
				}
				list.addAll(mSpider.pullToRefresh());
			}
			return list;
		}

		@Override
		protected void onPostExecute(ArrayList<News> result) {
			if (mMode == 0) {
				mSwipeRefreshLayout.setRefreshing(false);
			} else if (mMode == 1) {
				mDataList.remove(mDataList.size() - 1);
				mAdapter.notifyItemRemoved(mDataList.size());
				loading = false;
			}
			if (!result.isEmpty()) {
				if (mMode == 0) {
					mDataList.removeAll(mDataList);
					mAdapter.notifyDataSetChanged();
				}
				for (News news : result) {
					mDataList.add(news);
					mAdapter.notifyItemInserted(mDataList.size());
				}
			} else {
				Toast.makeText(getActivity(), "确认网络连接后，请重试", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

}
