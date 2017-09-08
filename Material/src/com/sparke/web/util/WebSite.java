package com.sparke.web.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * ��ҳץȡ����ҳ������Ҫ�õ�����վ
 * @author �˵�
 */
public class WebSite {
	
	/**
	 * �й����ʴ�ѧ����Ϣ�Ż���վ
	 */
	public static String CUG_LOGIN_WEBSITE = "https://portal.cug.edu.cn/zfca/login?service=http%3A%2F%2Fportal.cug.edu.cn%2Fportal.do";
	/**
	 * �й����ʴ�ѧ������
	 */
	public static String CUG_HOST_WEBSITE = "portal.cug.edu.cn";
	/**
	 * �й����ʴ�ѧ����Ϣ�Ż���վ�е���֤����ַ
	 */
	public static String CUG_LOGIN_CAPTCHA_WEBSITE = "https://portal.cug.edu.cn/zfca/captcha.htm";
	/**
	 * ѧ����������
	 */
	public static String XG_HOST = "www.xuegong.cug.edu.cn";
	/**
	 * ѧ��������ҳ
	 */
	public static String XG = "http://www.xuegong.cug.edu.cn/";
	/**
	 * ѧ������ѧ��Ҫ����ַ
	 */
	public static String IMPORTANT_NEWS_WEBSITE = "http://www.xuegong.cug.edu.cn/category/news/";
	/**
	 * ѧ�������ȵ��ע��ַ
	 */
	public static String HOT_NEWS_WEBSITE = "http://www.xuegong.cug.edu.cn/category/hot/";
	/**
	 * ѧ������֪ͨ������ַ
	 */
	public static String NOTICE_NEWS_WEBSITE = "http://www.xuegong.cug.edu.cn/category/notice/";
	/**
	 * ѧ�������Ԥ����ַ
	 */
	public static String ACTIVITY_WEBSITE = "http://www.xuegong.cug.edu.cn/category/activity/";
	
	/**
	 * �ж������Ƿ����
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
