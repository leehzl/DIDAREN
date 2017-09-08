package com.sparke.model;

public class News {
	private String mTitle;
	private String mPreview;
	private String mAuthor;
	private String mTime;
	private String mHref;
	private int mViews;
	private Boolean isLike;
	
	public News() {
		isLike = false;
	}
	
	public String getTitle() {
		return mTitle;
	}
	public void setTitle(String mTitle) {
		this.mTitle = mTitle;
	}
	public String getPreview() {
		return mPreview;
	}
	public void setPreview(String mPreview) {
		this.mPreview = mPreview;
	}
	public String getAuthor() {
		return mAuthor;
	}
	public void setAuthor(String mAuthor) {
		this.mAuthor = mAuthor;
	}
	public String getTime() {
		return mTime;
	}
	public void setTime(String mTime) {
		this.mTime = mTime;
	}
	public int getViews() {
		return mViews;
	}
	public void setViews(int mViews) {
		this.mViews = mViews;
	}
	public String getHref() {
		return mHref;
	}
	public void setHref(String mHref) {
		this.mHref = mHref;
	}

	public Boolean isLike() {
		return isLike;
	}

	public void setLike(Boolean isLike) {
		this.isLike = isLike;
	}
}
