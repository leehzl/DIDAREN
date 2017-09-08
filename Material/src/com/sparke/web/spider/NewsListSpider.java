package com.sparke.web.spider;

import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sparke.model.News;
import com.sparke.web.util.GetHttpClient;
import com.sparke.web.util.WebSite;

public class NewsListSpider implements NewsRefreshInterface {
	
	private String mHotUrl;
	private int mMaxPages;
	
	public NewsListSpider() {
	}
	
	public NewsListSpider(String url) {
		mHotUrl = url;
	}
	
	public void setUrl(String url) {
		mHotUrl = url;
	}
	
	public int getPages(){
		return mMaxPages;
	}

	@Override
	public ArrayList<News> pullToRefresh() {
		ArrayList<News> newsList = new ArrayList<News>();
		try {
			HttpClient httpClient = GetHttpClient.getDefaultHttpClient();
			HttpGet httpGet = new HttpGet(mHotUrl);
			httpGet.setHeader("Connection", "keep-alive");
			httpGet.setHeader("User-Agent",
					"Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; Touch; MASPJS; rv:11.0) like Gecko");
			httpGet.setHeader("Host", WebSite.XG_HOST);
			httpGet.setHeader("Referer", WebSite.XG);
			HttpResponse response = httpClient.execute(httpGet);
			int nStatus = response.getStatusLine().getStatusCode();
			if(nStatus == 200) {
				newsList.addAll(parseNews(readFromStream(response.getEntity())));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return newsList;
		}
		return newsList;
	}
	
	/**
	 * 抓取新闻
	 */
	private ArrayList<News> parseNews(String content) {
		ArrayList<News> newsList = new ArrayList<News>();
		Document doc = Jsoup.parse(content);
		Elements pages = doc.getElementsByClass("active");
		String strTemp = pages.get(pages.size() - 1).text();
		if(strTemp.matches("\\d+ ")) {
			mMaxPages = Integer.parseInt(strTemp);
		} else {
			mMaxPages = Integer.parseInt(pages.get(pages.size() - 2).text());
		}
		Elements lists = doc.getElementsByClass("arclist");
		if(!lists.isEmpty()) {
			for(Element list : lists) {
				News news = new News();
				Element div, temp;
				String[] str;
				div = list.getElementsByClass("arcListLeft").first();
				temp = div.getElementsByTag("a").first();
				news.setHref(temp.attr("href"));
				news.setTitle(temp.text());
				temp = div.getElementsByTag("p").first();
				if(temp == null) {
					news.setPreview(" ");
				} else {
					news.setPreview(temp.text());
				}

				div = list.getElementsByClass("arcListInfo").first();
				str = div.text().replace("阅读:", " ").split("\\s+");
				news.setAuthor(str[0]);
				news.setViews(Integer.parseInt(str[1]));
				news.setTime(str[2]);
				newsList.add(news);
			}
		}
		return newsList;
	}
	
	/**
	 * 获得整个网页的HTML代码
	 */
	private String readFromStream(HttpEntity entity) throws Exception {
		byte[] bResult = EntityUtils.toByteArray(entity);
		return new String(bResult, "utf-8");
	}
}
