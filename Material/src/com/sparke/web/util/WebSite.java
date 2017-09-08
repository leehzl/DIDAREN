package com.sparke.web.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网页抓取、网页分析需要用到的网站
 * @author 运德
 */
public class WebSite {
	
	/**
	 * 中国地质大学·信息门户网站
	 */
	public static String CUG_LOGIN_WEBSITE = "https://portal.cug.edu.cn/zfca/login?service=http%3A%2F%2Fportal.cug.edu.cn%2Fportal.do";
	/**
	 * 中国地质大学·主机
	 */
	public static String CUG_HOST_WEBSITE = "portal.cug.edu.cn";
	/**
	 * 中国地质大学·信息门户网站中的验证码网址
	 */
	public static String CUG_LOGIN_CAPTCHA_WEBSITE = "https://portal.cug.edu.cn/zfca/captcha.htm";
	/**
	 * 学工处·主机
	 */
	public static String XG_HOST = "www.xuegong.cug.edu.cn";
	/**
	 * 学工处·首页
	 */
	public static String XG = "http://www.xuegong.cug.edu.cn/";
	/**
	 * 学工处·学工要闻网址
	 */
	public static String IMPORTANT_NEWS_WEBSITE = "http://www.xuegong.cug.edu.cn/category/news/";
	/**
	 * 学工处·热点关注网址
	 */
	public static String HOT_NEWS_WEBSITE = "http://www.xuegong.cug.edu.cn/category/hot/";
	/**
	 * 学工处·通知公告网址
	 */
	public static String NOTICE_NEWS_WEBSITE = "http://www.xuegong.cug.edu.cn/category/notice/";
	/**
	 * 学工处·活动预告网址
	 */
	public static String ACTIVITY_WEBSITE = "http://www.xuegong.cug.edu.cn/category/activity/";
	
	/**
	 * 判断网络是否可用
	 */
	public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectionManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable()){
        	return true;
        } else {
        	return false;
        } 
	}
}
