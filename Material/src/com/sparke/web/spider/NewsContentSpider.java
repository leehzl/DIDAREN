package com.sparke.web.spider;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sparke.database_task.DatabaseHelper;
import com.sparke.web.util.GetHttpClient;
import com.sparke.web.util.WebSite;

public class NewsContentSpider {

	private String mUrl;
	private Context mContext;

	public NewsContentSpider(String url, Context context) {
		mUrl = url;
		mContext = context;
	}

	public String getNewsContent() {
		String content;
		content = searchFromDB();
		Boolean isEmpty = content.isEmpty() ? true : false;
		if (isEmpty) {
			if (WebSite.isNetworkAvailable(mContext)) {
				try {
					HttpClient httpClient = GetHttpClient
							.getDefaultHttpClient();
					HttpGet httpGet = new HttpGet(mUrl);
					httpGet.setHeader("Connection", "keep-alive");
					httpGet.setHeader(
							"User-Agent",
							"Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; Touch; MASPJS; rv:11.0) like Gecko");
					httpGet.setHeader("Host", WebSite.XG_HOST);
					httpGet.setHeader("Referer", WebSite.XG);
					HttpResponse response = httpClient.execute(httpGet);
					int nStatus = response.getStatusLine().getStatusCode();
					if (nStatus == 200) {
						content = getContentFromWeb(readFromStream(response
								.getEntity()));
						storeContentToDB(content);
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					return content;
				}
			}
		}
		return content;
	}

	private String getContentFromWeb(String string) {
		Document doc = Jsoup.parse(string);
		Element div = doc.getElementsByClass("pcon").first();
		return div.html();
	}

	private void storeContentToDB(String content) {
		DatabaseHelper helper = new DatabaseHelper(mContext, "DIDAREN.db",
				null, 1);
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("insert into NewsContent (href, content) values(?, ?)",
				new String[] { mUrl, content});
		db.close();
	}

	private String searchFromDB() {
		String content = "";
		String args[] = { mUrl };
		DatabaseHelper helper = new DatabaseHelper(mContext, "DIDAREN.db",
				null, 1);
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from NewsContent where href=?",
				args);
		if (cursor.moveToFirst()) {
			content = cursor.getString(cursor.getColumnIndex("content"));
		}
		cursor.close();
		db.close();
		return content;
	}

	/**
	 * 获得整个网页的HTML代码
	 */
	private String readFromStream(HttpEntity entity) throws Exception {
		byte[] bResult = EntityUtils.toByteArray(entity);
		return new String(bResult, "utf-8");
	}
}
