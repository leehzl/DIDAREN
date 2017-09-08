package com.sparke.modules;

import java.util.ArrayList;

import com.example.material.R;

import android.content.Context;
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

import com.sparke.model.News;
import com.sparke.model.NewsAdapter;
import com.sparke.web.spider.NewsListSpider;
import com.sparke.web.util.WebSite;

public class FragmentNewsImportant extends Fragment implements
		OnRefreshListener {

	private RecyclerView mRecyclerView;
	private NewsAdapter mAdapter;
	private LinearLayoutManager mLinearLayoutManager;
	private ArrayList<News> mDataList = new ArrayList<News>();

	private SwipeRefreshLayout mSwipeRefreshLayout;

	private int visibleThreshold = 5;
	private int lastVisibleItem, totalItemCount;
	private boolean loading;

	private NewsListSpider mSpider = null;
	private ImportantNewsTask mTask = null;

	public FragmentNewsImportant() {
	}

	public static FragmentNewsImportant newInstance() {
		FragmentNewsImportant fragment = new FragmentNewsImportant();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null)
			return null;
		View view = inflater.inflate(R.layout.fragment_news_important,
				container, false);

		mSwipeRefreshLayout = (SwipeRefreshLayout) view
				.findViewById(R.id.news_important_refresh);
		mSwipeRefreshLayout.setColorSchemeResources(R.color.danny_background,
				R.color.cpb_blue_dark, R.color.cpb_green_dark);
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mRecyclerView = (RecyclerView) view
				.findViewById(R.id.news_important_list);
		mRecyclerView.setHasFixedSize(true);
		mLinearLayoutManager = new LinearLayoutManager(getContext());
		mRecyclerView.setLayoutManager(mLinearLayoutManager);
		mAdapter = new NewsAdapter(mDataList, getContext());
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
						params[1] = WebSite.IMPORTANT_NEWS_WEBSITE + "page/"
								+ (currentPage + 1) + "/";
						mTask = (ImportantNewsTask) (new ImportantNewsTask().execute(params));
						loading = true;
					} else {
						Toast.makeText(getContext(), "已是最后一页",
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
				params[1] = WebSite.IMPORTANT_NEWS_WEBSITE;
				mTask = (ImportantNewsTask) (new ImportantNewsTask().execute(params));
			}
		}, 1000);
		
		return view;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if(mTask != null) {
			mTask.cancel(true);
		}
	};

	/**
	 * 获得碎片所在的上下文
	 * 
	 * @return
	 */
	private Context getContext() {
		return getActivity();
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		String[] params = new String[2];
		params[0] = "0";
		params[1] = WebSite.IMPORTANT_NEWS_WEBSITE;
		mTask = (ImportantNewsTask) (new ImportantNewsTask().execute(params));
	}

	class ImportantNewsTask extends AsyncTask<String, Void, ArrayList<News>> {

		private int mMode;

		@Override
		protected ArrayList<News> doInBackground(String... params) {
			// TODO Auto-generated method stub
			ArrayList<News> list = new ArrayList<News>();
			mMode = Integer.parseInt(params[0]);
			String url = params[1];
			if (WebSite.isNetworkAvailable(getContext())) {
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
				Toast.makeText(getContext(), "确认网络连接后，请重试", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}
}
