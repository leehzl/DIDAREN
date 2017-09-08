package com.sparke.database_task;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static String CREATE_HOT_NEWS_LIST_TABLE = "create table HotNewsList ("
			+ "id integer primary key  autoincrement,"
			+ "href text,"
			+ "title text,"
			+ "preview text,"
			+ "author text,"
			+ "time text,"
			+ "like integer,"
			+ "views integer)";
	
	private static String CREATE_NEWS_CONTENT_TABLE = "create table NewsContent ("
			+ "href text,"
			+ "content text)";
	
	private static String CREATE_EVENT_TABLE ="create table EventList ("
			+ "id integer primary key,"
			+ "title text,"
			+ "start text,"
			+ "end text,"
			+ "pos text,"
			+ "seat integer,"
			+ "note text,"
			+ "type integer)";
	
	/**
	 * course：课程名称
	 * style：课程性质（必修，选修，通选）
	 * year：学年（2014-2015）
	 * term：学期（1）
	 * credit：学分
	 * score：成绩
	 * grade:该门课程的绩点
	 */
	private static String CREATE_SCORE_TABLE = "create table Score (" +
			"id integer primary key," +
			"course text," +
			"style text," +
			"year text," +
			"term integer," +
			"credit text," +
			"score text," +
			"grade text," +
			"number text)";
		
	public DatabaseHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_HOT_NEWS_LIST_TABLE);
		db.execSQL(CREATE_NEWS_CONTENT_TABLE);
		db.execSQL(CREATE_EVENT_TABLE);
		db.execSQL(CREATE_SCORE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("drop table if exists HotNewsList");
		db.execSQL("drop table if exists NewsContent");
		db.execSQL("drop table if exists EventList");
		db.execSQL("drop table if exists Score");
		onCreate(db);
	}
}
