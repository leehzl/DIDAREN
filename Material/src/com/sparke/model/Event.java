package com.sparke.model;

import java.io.Serializable;

public class Event implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	/**
	 * �¼�����
	 */
	private String mTitle;
	/**
	 * �¼���ʼʱ��
	 */
	private String mStart;
	/**
	 * �¼�����ʱ��
	 */
	private String mEnd;
	/**
	 * �¼������ص�
	 */
	private String mPos;
	/**
	 * �¼���λ��
	 */
	private int mSeat;	
	/**
	 * �¼���ע
	 */
	private String mNote;
	/**
	 * �¼�����:1Ϊ����ʱ��, 2Ϊ��������
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
