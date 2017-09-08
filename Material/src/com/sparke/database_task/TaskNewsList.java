package com.sparke.database_task;

import java.util.ArrayList;

import com.sparke.model.News;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

public class TaskNewsList extends AsyncTask<String, Void, ArrayList<News>> {

	private ArrayList<News> mList;
	private Context mContext;

	public TaskNewsList(ArrayList<News> list, Context context) {
		mList = list;
		mContext = context;
	}

	@Override
	protected ArrayList<News> doInBackground(String... params) {
		// TODO Auto-generated method stub
		int mode = Integer.parseInt(params[0]);
		String table = params[1];
		switch (mode) {
		case 0:
			getNewsListFromDB(table);
			break;
		case 1:
			storeNewsListIntoDB(table);
			break;
		}
		return null;

	}

	private void getNewsListFromDB(String table) {
		DatabaseHelper helper = new DatabaseHelper(mContext, "News.db",
				null, 1);
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from " + table, null);
		if (cursor.moveToFirst()) {
			do {
				News temp = new News();
				String str = cursor.getString(cursor.getColumnIndex("href"));
				temp.setHref(str);
				str = cursor.getString(cursor.getColumnIndex("title"));
				temp.setTitle(str);
				str = cursor.getString(cursor.getColumnIndex("author"));
				temp.setAuthor(str);
				str = cursor.getString(cursor.getColumnIndex("time"));
				temp.setTime(str);
				str = cursor.getString(cursor.getColumnIndex("preview"));
				temp.setPreview(str);
				int n = cursor.getInt(cursor.getColumnIndex("like"));
				if (n == 0) {
					temp.setLike(false);
				} else {
					temp.setLike(true);
				}
				n = cursor.getInt(cursor.getColumnIndex("views"));
				temp.setViews(n);
				mList.add(temp);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
	}

	private void storeNewsListIntoDB(String table) {
		DatabaseHelper helper = new DatabaseHelper(mContext, "News.db",
				null, 1);
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("delete from " + table);
		int count = 0;
		for (News news : mList) {
			db.execSQL(
					"insert into "
							+ table
							+ "(href,title,author,time,preview,like,views) values(?,?,?,?,?,?,?)",
					new String[] { news.getHref(), news.getTitle(),
							news.getAuthor(), news.getTime(),
							news.getPreview(), (news.isLike() ? "0" : "1") , news.getViews()+""});
			count++;
			if ((count % 10) == 0)
				break;
		}
		db.close();
	}
}
