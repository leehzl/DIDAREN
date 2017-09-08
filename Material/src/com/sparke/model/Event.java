package com.sparke.model;

import java.io.Serializable;

public class Event implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	/**
	 * 事件名称
	 */
	private String mTitle;
	/**
	 * 事件开始时间
	 */
	private String mStart;
	/**
	 * 事件结束时间
	 */
	private String mEnd;
	/**
	 * 事件发生地点
	 */
	private String mPos;
	/**
	 * 事件座位号
	 */
	private int mSeat;	
	/**
	 * 事件备注
	 */
	private String mNote;
	/**
	 * 事件类型:1为考试时间, 2为待办事项
	 */
	private int mType;
	
	public Event() {
		mTitle = "";
		mStart = "";
		mEnd = "";
		mPos = "";
		mSeat = -1;
		mType = 0;
		mNote = "";
	}
	
	public String getTitle() {
		return mTitle;
	}
	public void setTitle(String title) {
		mTitle = title;
	}
	public String getStart() {
		return mStart;
	}
	public void setStart(String start) {
		mStart = start;
	}
	public String getEnd() {
		return mEnd;
	}
	public void setEnd(String end) {
		mEnd = end;
	}
	public String getPos() {
		return mPos;
	}
	public void setPos(String pos) {
		mPos = pos;
	}
	public int getSeat() {
		return mSeat;
	}
	public void setSeat(int seat) {
		mSeat = seat;
	}
	public String getNote() {
		return mNote;
	}
	public void setNote(String note) {
		mNote = note;
	}
	public int getType() {
		return mType;
	}
	public void setType(int type) {
		mType = type;
	}
}
