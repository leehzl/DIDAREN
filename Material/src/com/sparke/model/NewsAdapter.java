package com.sparke.model;

import java.util.List;

import com.example.material.R;
import com.sparke.modules.ActivityNewsContent;
import com.sparke.modules.MainActivity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private final int VIEW_ITEM = 1;
	private final int VIEW_PROG = 0;

	private List<News> mDataList;
	private Context mContext;

	public NewsAdapter(List<News> data, Context context) {
		mDataList = data;
		mContext = context;
	}
	
	@Override
	public int getItemViewType(int position) {
		return mDataList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
	}

	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return mDataList.size();
	}

	@Override
	public void onBindViewHolder(ViewHolder arg0, int arg1) {
		// TODO Auto-generated method stub
		final int position = arg1;
		if(arg0 instanceof ItemHolder) {
			final News news = mDataList.get(arg1);
			if (null == news)
				return;
	
			final ItemHolder holder = (ItemHolder) arg0;
			holder.itemView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stu
					int[] startingLocation = new int[2];
					holder.itemView.getLocationOnScreen(startingLocation);
					startingLocation[0] += holder.itemView.getWidth() / 2;
					ActivityNewsContent.startAvtivityNewsContent(startingLocation, mContext, news.getHref(), news.getTitle());
					((MainActivity) mContext).overridePendingTransition(0, 0);		
				}
			});
			holder.mTitleTextView.setText(news.getTitle());
			holder.mPreviewTextView.setText(news.getPreview());
			holder.mAuthorTextView.setText(news.getAuthor());
			holder.mTimeTextView.setText(news.getTime());
			holder.mViewsTextView.setText(news.getViews() + "");
		} else {
			((ProgressViewHolder) arg0).mLoadingView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
		// TODO Auto-generated method stub
		RecyclerView.ViewHolder vh;
		if(arg1 == VIEW_ITEM) {
			View v = LayoutInflater.from(arg0.getContext()).inflate(R.layout.list_news_item, arg0, false);
			vh = new ItemHolder(v);
		} else {
			View v = LayoutInflater.from(arg0.getContext()).inflate(R.layout.loading_more, arg0, false);
			vh = new ProgressViewHolder(v);
		}
		return vh;
	}

	public void clear() {
		mDataList.clear();
		notifyDataSetChanged();
	}

	public void addAll(List<News> list) {
		mDataList.addAll(list);
		notifyDataSetChanged();
	}
	
	public static class ItemHolder extends RecyclerView.ViewHolder {
		public View itemView;
		public TextView mTitleTextView;
		public TextView mPreviewTextView;
		public TextView mAuthorTextView;
		public TextView mTimeTextView;
		public TextView mViewsTextView;

		public ItemHolder(View arg0) {
			super(arg0);
			itemView = arg0;
			mTitleTextView = (TextView) arg0
					.findViewById(R.id.news_item_title);
			mPreviewTextView = (TextView) arg0
					.findViewById(R.id.news_item_preview);
			mAuthorTextView = (TextView) arg0
					.findViewById(R.id.news_item_author);
			mTimeTextView = (TextView) arg0.findViewById(R.id.news_item_time);
			mViewsTextView = (TextView) arg0.findViewById(R.id.news_item_views);
		}
	}
	
	public static class ProgressViewHolder extends RecyclerView.ViewHolder {
		public LinearLayout mLoadingView;
		
		public ProgressViewHolder(View v) {
		   super(v);
		   mLoadingView = (LinearLayout) v;
		 }
	}
}
