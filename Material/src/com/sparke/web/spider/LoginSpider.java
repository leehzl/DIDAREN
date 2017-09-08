package com.sparke.web.spider;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.sparke.database_task.DatabaseHelper;
import com.sparke.web.util.GetHttpClient;
import com.sparke.web.util.WebSite;

/**
 * ģ���¼��Ϣ�Ż���վ������
 * 
 * @author �˵�
 * 
 */
public class LoginSpider {

	/*
	 * sharedpreference��ſγ���Ϣ
	 */
	private SharedPreferences.Editor editor;

	private String[] morning_first = new String[7];
	private String[] morning_second = new String[7];
	private String[] afternoon_first = new String[7];
	private String[] afternoon_second = new String[7];
	private String[] evening_first = new String[7];
	private String[] evening_second = new String[7];

	private Context mContext;

	private HttpClient mHttpClient = null;
	private HttpPost mHttpPost = null;
	private HttpGet mHttpGet = null;

	private String mLoginCookies = "";
	private String mEnterCookies = "";

	private String mLt = "";
	/**
	 * ������Ϣ
	 */
	private String mError = "";
	/**
	 * �����Ż���վ��ַ
	 */
	private String mPortalSite = "";
	/**
	 * ���˽������ϵͳ��ַ
	 */
	private String mAdminSys = "";
	/**
	 * ���յĸ��˽������ϵͳ��ַ
	 */
	private String mFinalAdminSysSite = "";
	/**
	 * ���˽������ϵͳ��ַ��һЩ��Ϣ����ַ
	 */
	private String mPersonalInfoSite = "";
	private String mCourseSite = "";
	private String mScoreSite = "";
	private String mExamSite = "";
	/**
	 * ��¼�Ƿ�ɹ�
	 */
	private boolean mSuccessful = false;

	/**
	 * ���ص�¼״̬���ɹ���true������ʧ�ܣ�false��
	 * 
	 * @return
	 */
	public boolean isSuccessful() {
		return mSuccessful;
	}

	/**
	 * ������Ϣ
	 * 
	 * @return
	 */
	public String getErrorInfo() {
		return mError;
	}

	public LoginSpider(Context context) {
		mContext = context;
	}

	/**
	 * ��������
	 * 
	 * @param mode
	 *            : mode=0����HttpPost��mode=1����HttpGet
	 * @param url
	 *            : ����·��
	 * @param header
	 *            : ����ͷ��
	 * @param params
	 *            : �������
	 * @throws UnsupportedEncodingException
	 */
	public void setRequest(int mode, String url, Map<String, String> header,
			Map<String, String> params) throws UnsupportedEncodingException {
		switch (mode) {
		case 0:
			mHttpPost = new HttpPost(url);
			Set<String> set;
			if (header != null) {
				set = header.keySet();
				for (String h : set) {
					mHttpPost.setHeader(h, header.get(h));
				}
			}
			if (params != null) {
				set = params.keySet();
				List<NameValuePair> nvps = new ArrayList<NameValuePair>();
				for (String p : set) {
					nvps.add(new BasicNameValuePair(p, params.get(p)));
				}
				mHttpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			}
			break;
		case 1:
			mHttpGet = new HttpGet(url);
			Set<String> set1;
			if (header != null) {
				set1 = header.keySet();
				for (String h : set1) {
					mHttpGet.setHeader(h, header.get(h));
				}
			}
			break;
		}
	}

	/**
	 * ��Cookiesת�����ַ���
	 * 
	 * @param cookies
	 * @return
	 */
	private String cookiesToString(List<Cookie> cookies) {
		String result = "";
		if (!cookies.isEmpty()) {
			int nCount = cookies.size();
			StringBuilder cookieSB = new StringBuilder();
			Cookie cookie;
			for (int i = 0; i < nCount - 1; i++) {
				cookie = cookies.get(i);
				cookieSB.append(cookie.getName()).append('=')
						.append(cookie.getValue()).append(';');
			}
			cookie = cookies.get(nCount - 1);
			cookieSB.append(cookie.getName()).append('=')
					.append(cookie.getValue());
			result = cookieSB.toString();
		}
		return result;
	}

	/**
	 * ��¼ǰ��׼������1: ��ȡ��¼��Ϣ�ύ��cookie
	 * 
	 */
	public boolean getLoginCookies() {
		boolean isPrepare = false;
		try {
			mHttpClient = GetHttpClient.getNewHttpClient();
			setRequest(1, WebSite.CUG_LOGIN_WEBSITE, null, null);
			HttpResponse response = mHttpClient.execute(mHttpGet);
			int nStatus = response.getStatusLine().getStatusCode();
			if (nStatus == 200) {
				List<Cookie> cookies = ((AbstractHttpClient) mHttpClient)
						.getCookieStore().getCookies();
				mLt = parseLtFromLoginWeb(readFromStream(response.getEntity()));
				mLoginCookies = cookiesToString(cookies);
				if (!mLoginCookies.isEmpty()) {
					isPrepare = true;
				} else {
					mError = "������վ��ҳ��ò�ƿ�С���ˣ����Ժ�����";
				}
			} else {
				mError = "������վ��ҳ��ò�ƿ�С���ˣ����Ժ�����";
			}
		} catch (Exception e) {
			e.printStackTrace();
			mError = "������վ��ҳ��ò�ƿ�С���ˣ����Ժ�����";
			return isPrepare;
		}
		return isPrepare;
	}

	/**
	 * ��¼ǰ��׼������2: ץȡ��֤��ͼƬ
	 * 
	 * @return ��֤��ͼƬ
	 */
	public Bitmap grabCAPTCHA() {
		if (!mLoginCookies.isEmpty()) {
			try {
				String urlCAPTCHA = WebSite.CUG_LOGIN_CAPTCHA_WEBSITE
						+ "?random=" + Math.random();
				Map<String, String> header = new HashMap<String, String>();
				header.put("Cookie", mLoginCookies);
				header.put("Host", WebSite.CUG_HOST_WEBSITE);
				header.put("Referer", WebSite.CUG_LOGIN_WEBSITE);
				header.put(
						"User-Agent",
						"Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; Touch; MASPJS; rv:11.0) like Gecko");
				setRequest(1, urlCAPTCHA, header, null);
				HttpResponse response = mHttpClient.execute(mHttpGet);
				int status = response.getStatusLine().getStatusCode();
				if (status == 200) {
					return BitmapFactory.decodeStream(response.getEntity()
							.getContent());
				} else {
					mError = "��ȡ��֤��ʧ�ܣ����Ժ�����";
					return null;
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				mError = "��ȡ��֤��ʧ�ܣ����Ժ�����";
				return null;
			}
		} else {
			mError = "��ȡ��֤��ʧ�ܣ����Ժ�����";
			return null;
		}
	}

	/**
	 * ��¼�����Ż���վ
	 * 
	 * @param user
	 *            �û���
	 * @param psw
	 *            ����
	 * @param CAPTCHA
	 *            ��֤��
	 * @return ��¼�ɹ����
	 */
	public boolean login(String user, String psw, String CAPTCHA) {
		if (!mLoginCookies.isEmpty() && mHttpClient != null) {
			try {
				Map<String, String> header = new HashMap<String, String>();
				header.put("Cookie", mLoginCookies);
				header.put("Host", WebSite.CUG_HOST_WEBSITE);
				header.put("Referer", WebSite.CUG_LOGIN_WEBSITE);
				header.put(
						"User-Agent",
						"Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; Touch; MASPJS; rv:11.0) like Gecko");
				header.put("Content-Type", "application/x-www-form-urlencoded");

				Map<String, String> params = new HashMap<String, String>();
				params.put("isremenberme", "0");
				params.put("useValidateCode", "0");
				params.put("username", user);
				params.put("password", psw);
				params.put("j_captcha_response", CAPTCHA);
				params.put("lt", mLt);
				params.put("_eventId", "submit");

				setRequest(0, WebSite.CUG_LOGIN_WEBSITE, header, params);
				mHttpPost.getParams().setParameter(
						"http.protocol.handle-redirects", false);
				/*
				 * httpclient��ʵ��ִ�������һ�����ض���ķ�����ִ����
				 * �������õ���response���ض���֮���ַ��ִ��������response��
				 * ���Եò���locatioon������responseCode==200,��������java���Ի����µ�302��
				 * ˵�����ʱ���response���ض���֮���response ���ʱ����Ҫ��ֹHttpClient���Զ��ض���
				 */
				HttpResponse response = mHttpClient.execute(mHttpPost);
				int nStatus = response.getStatusLine().getStatusCode();
				if (nStatus == 302) {
					Log.d("DIDAREN", "Login Success");
					SharedPreferences.Editor editor = mContext
							.getSharedPreferences("Puma", Context.MODE_PRIVATE)
							.edit();
					editor.putString("psw", psw);
					editor.commit();

					List<Cookie> cookies = ((AbstractHttpClient) mHttpClient)
							.getCookieStore().getCookies();
					Header location = response.getFirstHeader("Location");
					mPortalSite = location.getValue();

					setRequest(1, mPortalSite, null, null);
					HttpResponse response2 = mHttpClient.execute(mHttpGet);
					cookies = ((AbstractHttpClient) mHttpClient)
							.getCookieStore().getCookies();
					mEnterCookies = cookiesToString(cookies);
					mAdminSys = parseAdminSys(readFromStream(response2
							.getEntity()));
					return true;
				} else if (nStatus == 200) {
					// ��¼ʧ��
					mError = parseErrorInfo(readFromStream(response.getEntity()));
					return false;
				} else {
					mError = "δ֪����������";
					return false;
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				mError = "δ֪����������";
				return false;
			}
		} else {
			mError = "δ֪����������";
			return false;
		}
	}

	/**
	 * ��¼��ʦ�Ż���վ
	 * 
	 * @param user
	 *            �û���
	 * @param psw
	 *            ����
	 * @param CAPTCHA
	 *            ��֤��
	 * @return ��¼�ɹ����
	 */
	public Boolean loginTeacher(String user, String psw, String CAPTCHA) {
		if (!mLoginCookies.isEmpty() && mHttpClient != null) {
			try {
				Map<String, String> header = new HashMap<String, String>();
				header.put("Cookie", mLoginCookies);
				header.put("Host", WebSite.CUG_HOST_WEBSITE);
				header.put("Referer", WebSite.CUG_LOGIN_WEBSITE);
				header.put(
						"User-Agent",
						"Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; Touch; MASPJS; rv:11.0) like Gecko");
				header.put("Content-Type", "application/x-www-form-urlencoded");

				Map<String, String> params = new HashMap<String, String>();
				params.put("isremenberme", "0");
				params.put("useValidateCode", "0");
				params.put("username", user);
				params.put("password", psw);
				params.put("j_captcha_response", CAPTCHA);
				params.put("lt", mLt);
				params.put("_eventId", "submit");

				setRequest(0, WebSite.CUG_LOGIN_WEBSITE, header, params);
				mHttpPost.getParams().setParameter(
						"http.protocol.handle-redirects", false);
				/*
				 * httpclient��ʵ��ִ�������һ�����ض���ķ�����ִ����
				 * �������õ���response���ض���֮���ַ��ִ��������response��
				 * ���Եò���locatioon������responseCode==200,��������java���Ի����µ�302��
				 * ˵�����ʱ���response���ض���֮���response ���ʱ����Ҫ��ֹHttpClient���Զ��ض���
				 */
				HttpResponse response = mHttpClient.execute(mHttpPost);
				int nStatus = response.getStatusLine().getStatusCode();
				if (nStatus == 302) {
					Log.d("DIDAREN", "Login Success");
					SharedPreferences.Editor editor = mContext
							.getSharedPreferences("Puma", Context.MODE_PRIVATE)
							.edit();
					editor.putString("psw", psw);
					editor.commit();

					List<Cookie> cookies = ((AbstractHttpClient) mHttpClient)
							.getCookieStore().getCookies();
					Header location = response.getFirstHeader("Location");
					mPortalSite = location.getValue();

					setRequest(1, mPortalSite, null, null);
					HttpResponse response2 = mHttpClient.execute(mHttpGet);
					cookies = ((AbstractHttpClient) mHttpClient)
							.getCookieStore().getCookies();
					mEnterCookies = cookiesToString(cookies);
					mAdminSys = parseTeacherAdminSys(readFromStream(response2
							.getEntity()));
					Log.d("Tea", "�������ϵͳ" + mAdminSys);
					return true;
				} else if (nStatus == 200) {
					// ��¼ʧ��
					mError = parseErrorInfo(readFromStream(response.getEntity()));
					return false;
				} else {
					mError = "δ֪����������";
					return false;
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				mError = "δ֪����������";
				return false;
			}
		} else {
			mError = "δ֪����������";
			return false;
		}
	}

	/**
	 * ��¼����ϵͳ
	 * 
	 * @return �ɹ����
	 */
	public Boolean loginAdminSys() {
		if (!mEnterCookies.isEmpty() && !mAdminSys.isEmpty()) {
			try {
				int TRY_TIMES = 4;
				HttpResponse response = null;
				String next = "";
				String host;
				Boolean isOK = true;
				for (int i = 0; i < TRY_TIMES; i++) {
					isOK = true;
					next = mAdminSys;
					host = WebSite.CUG_HOST_WEBSITE;
					int nStatus;
					Map<String, String> header = new HashMap<String, String>();
					do {
						header.clear();
						header.put("Accept-Encoding", "gzip,deflate");
						header.put("Accept",
								"text/html,application/xhtml+xml,*/*");
						header.put("DNT", "1");
						header.put(
								"User-Agent",
								"Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; Touch; MASPJS; rv:11.0) like Gecko");
						String temp = getHostStringFromURL(next);
						if (temp.contains(".")) {
							host = temp;
						}
						header.put("Host", host);
						if (host.equals(WebSite.CUG_HOST_WEBSITE)) {
							header.put("Cookie", mLoginCookies + ";"
									+ mEnterCookies);
						} else {
							if (!next.contains(host)) {
								next = "http://" + host + next;
							}
						}
						header.put("Connection", "Keep-Alive");
						header.put("Accept-Language",
								"zh-Hans-CN,zh-Hans;q=0.5");
						header.put("Referer", mPortalSite);
						Log.d("DIDAREN", "Next url:" + next);
						setRequest(1, next, header, null);
						mHttpGet.getParams().setParameter(
								"http.protocol.handle-redirects", false);
						response = mHttpClient.execute(mHttpGet);
						nStatus = response.getStatusLine().getStatusCode();
						if (nStatus == 302) {
							readFromStream(response.getEntity());
							/*****/
							next = response.getFirstHeader("Location")
									.getValue();
							if (next.contains("error.jsp")) {
								isOK = false;
								break;
							}
						}
					} while (nStatus == 302);
					if (isOK == true)
						break;
				}
				if (isOK == true && response != null) {
					mFinalAdminSysSite = next;
					parseRelativeSiteFromAdminSys(readFromStream(response
							.getEntity()));
					return true;
				} else {
					mError = "��¼�������ϵͳ���������µ�¼";
					return false;
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				mError = "δ֪����������";
				return false;
			}
		} else {
			mError = "δ֪����������";
			return false;
		}
	}

	/**
	 * ��¼��ʦ����ϵͳ
	 * 
	 * @return �ɹ����
	 */
	public Boolean loginTeacherAdminSys() {
		if (!mEnterCookies.isEmpty() && !mAdminSys.isEmpty()) {
			try {
				int TRY_TIMES = 4;
				HttpResponse response = null;
				String next = "";
				String host;
				Boolean isOK = true;
				for (int i = 0; i < TRY_TIMES; i++) {
					isOK = true;
					next = mAdminSys;
					host = WebSite.CUG_HOST_WEBSITE;
					int nStatus;
					Map<String, String> header = new HashMap<String, String>();
					do {
						header.clear();
						header.put("Accept-Encoding", "gzip,deflate");
						header.put("Accept",
								"text/html,application/xhtml+xml,*/*");
						header.put("DNT", "1");
						header.put(
								"User-Agent",
								"Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; Touch; MASPJS; rv:11.0) like Gecko");
						String temp = getHostStringFromURL(next);
						if (temp.contains(".")) {
							host = temp;
						}
						header.put("Host", host);
						if (host.equals(WebSite.CUG_HOST_WEBSITE)) {
							header.put("Cookie", mLoginCookies + ";"
									+ mEnterCookies);
						} else {
							if (!next.contains(host)) {
								next = "http://" + host + next;
							}
						}
						header.put("Connection", "Keep-Alive");
						header.put("Accept-Language",
								"zh-Hans-CN,zh-Hans;q=0.5");
						header.put("Referer", mPortalSite);
						Log.d("DIDAREN", "Next url:" + next);
						setRequest(1, next, header, null);
						mHttpGet.getParams().setParameter(
								"http.protocol.handle-redirects", false);
						response = mHttpClient.execute(mHttpGet);
						nStatus = response.getStatusLine().getStatusCode();
						if (nStatus == 302) {
							readFromStream(response.getEntity());
							/*****/
							next = response.getFirstHeader("Location")
									.getValue();
							if (next.contains("error.jsp")) {
								isOK = false;
								break;
							}
						}
					} while (nStatus == 302);
					if (isOK == true)
						break;
				}
				if (isOK == true && response != null) {
					mFinalAdminSysSite = next;
					parseTeacherRelativeSiteFromAdminSys(readFromStream(response
							.getEntity()));
					return true;
				} else {
					mError = "��¼�������ϵͳ���������µ�¼";
					return false;
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				mError = "δ֪����������";
				return false;
			}
		} else {
			mError = "δ֪����������";
			return false;
		}
	}

	/**
	 * �Ӹ��˽���ϵͳ�л�ȡ������Ϣ
	 * 
	 * @return �ɹ����
	 */
	public boolean getPersonalInfoFromAdminSys() {
		if (!mFinalAdminSysSite.isEmpty() && !mPersonalInfoSite.isEmpty()) {
			try {
				String url = getSubStringFromUrl(mFinalAdminSysSite)
						+ mPersonalInfoSite + "/";
				Map<String, String> header = new HashMap<String, String>();
				header.put("Accept", "text/html,application/xhtml+xml,*/*");
				header.put("Accept-Encoding", "gzip,deflate");
				header.put("Accept-Language", "zh-Hans-CN,zh-Hans;q=0.5");
				header.put("Connection", "Keep-Alive");
				header.put("Host", getHostStringFromURL(mFinalAdminSysSite));
				header.put("Referer", mFinalAdminSysSite);
				header.put(
						"User-Agent",
						"Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; Touch; MASPJS; rv:11.0) like Gecko");
				setRequest(1, url, header, null);
				HttpResponse response = mHttpClient.execute(mHttpGet);
				int nStatus = response.getStatusLine().getStatusCode();
				if (nStatus == 200) {
					parsePersonalInfo(readFromStream(response.getEntity()));
					return true;
				} else {
					mError = "�޷���֤���, �����µ�¼";
					return false;
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				mError = "�޷���֤���, �����µ�¼";
				return false;
			}
		} else {
			mError = "�޷���֤���, �����µ�¼";
			return false;
		}
	}

	/**
	 * �ӽ�ʦ����ϵͳ�л�ȡ��ʦ��Ϣ
	 * 
	 * @return �ɹ����
	 */
	public Boolean getTeacherPersonalInfoFromAdminSys() {
		if (!mFinalAdminSysSite.isEmpty() && !mPersonalInfoSite.isEmpty()) {
			try {
				String url = getSubStringFromUrl(mFinalAdminSysSite)
						+ mPersonalInfoSite;
				Map<String, String> header = new HashMap<String, String>();
				header.put("Accept", "text/html,application/xhtml+xml,*/*");
				header.put("Accept-Encoding", "gzip,deflate");
				header.put("Accept-Language", "zh-Hans-CN,zh-Hans;q=0.5");
				header.put("Connection", "Keep-Alive");
				header.put("Host", getHostStringFromURL(mFinalAdminSysSite));
				header.put("Referer", mFinalAdminSysSite);
				header.put(
						"User-Agent",
						"Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; Touch; MASPJS; rv:11.0) like Gecko");
				setRequest(1, url, header, null);
				HttpResponse response = mHttpClient.execute(mHttpGet);
				int nStatus = response.getStatusLine().getStatusCode();
				if (nStatus == 200) {
					parseTeacherPersonalInfo(readFromStream(response
							.getEntity()));
					return true;
				} else {
					mError = "�޷���֤���, �����µ�¼";
					return false;
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				mError = "�޷���֤���, �����µ�¼";
				return false;
			}
		} else {
			mError = "�޷���֤���, �����µ�¼";
			return false;
		}
	}

	/**
	 * �Ӹ��˽���ϵͳ�л�ȡ�γ̱�
	 * 
	 * @return �ɹ����
	 */
	public boolean getCourseTableFromAdminSys() {
		if (!mFinalAdminSysSite.isEmpty() && !mCourseSite.isEmpty()) {
			try {
				String url = getSubStringFromUrl(mFinalAdminSysSite)
						+ mCourseSite + "/";
				Map<String, String> header = new HashMap<String, String>();
				header.put("Accept", "text/html,application/xhtml+xml,*/*");
				header.put("Accept-Encoding", "gzip,deflate");
				header.put("Accept-Language", "zh-Hans-CN,zh-Hans;q=0.5");
				header.put("Connection", "Keep-Alive");
				header.put("Host", getHostStringFromURL(mFinalAdminSysSite));
				header.put("Referer", mFinalAdminSysSite);
				header.put(
						"User-Agent",
						"Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; Touch; MASPJS; rv:11.0) like Gecko");
				setRequest(1, url, header, null);
				HttpResponse response = mHttpClient.execute(mHttpGet);
				int nStatus = response.getStatusLine().getStatusCode();
				if (nStatus == 200) {
					parseCourseInfo(readFromStream(response.getEntity()));
					return true;
				} else {
					mError = "�޷���ȡ�γ���Ϣ, �����µ�¼";
					return false;
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				mError = "�޷���ȡ�γ���Ϣ, �����µ�¼";
				return false;
			}
		} else {
			mError = "�޷���ȡ�γ���Ϣ, �����µ�¼";
			return false;
		}
	}

	/**
	 * �ӽ�ʦ����ϵͳ�л�ȡ��ʦ���˿α�
	 * 
	 * @return �ɹ����
	 */
	public Boolean getTeacherCourseTableFromAdminSys() {
		if (!mFinalAdminSysSite.isEmpty() && !mCourseSite.isEmpty()) {
			try {
				String url = getSubStringFromUrl(mFinalAdminSysSite)
						+ mCourseSite;
				Map<String, String> header = new HashMap<String, String>();
				header.put("Accept", "text/html,application/xhtml+xml,*/*");
				header.put("Accept-Encoding", "gzip,deflate");
				header.put("Accept-Language", "zh-Hans-CN,zh-Hans;q=0.5");
				header.put("Connection", "Keep-Alive");
				header.put("Host", getHostStringFromURL(mFinalAdminSysSite));
				header.put("Referer", mFinalAdminSysSite);
				header.put(
						"User-Agent",
						"Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; Touch; MASPJS; rv:11.0) like Gecko");
				setRequest(1, url, header, null);
				HttpResponse response = mHttpClient.execute(mHttpGet);
				int nStatus = response.getStatusLine().getStatusCode();
				if (nStatus == 200) {
					parseTeacherCourseInfo(readFromStream(response.getEntity()));
					mSuccessful = true;
					return true;
				} else {
					mError = "�޷���ȡ�γ���Ϣ, �����µ�¼";
					return false;
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				mError = "�޷���ȡ�γ���Ϣ, �����µ�¼";
				return false;
			}
		} else {
			mError = "�޷���ȡ�γ���Ϣ, �����µ�¼";
			return false;
		}
	}

	/**
	 * �Ӹ��˽���ϵͳ�л�ȡ����ʱ��
	 * 
	 * @return
	 */
	public Boolean getExamTimeFromAdminSys() {
		if (!mFinalAdminSysSite.isEmpty() && !mExamSite.isEmpty()) {
			try {
				String url = getSubStringFromUrl(mFinalAdminSysSite)
						+ mExamSite + "/";
				Map<String, String> header = new HashMap<String, String>();
				header.put("Accept", "text/html,application/xhtml+xml,*/*");
				header.put("Accept-Encoding", "gzip,deflate");
				header.put("Accept-Language", "zh-Hans-CN,zh-Hans;q=0.5");
				header.put("Connection", "Keep-Alive");
				header.put("Host", getHostStringFromURL(mFinalAdminSysSite));
				header.put("Referer", mFinalAdminSysSite);
				header.put(
						"User-Agent",
						"Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; Touch; MASPJS; rv:11.0) like Gecko");
				setRequest(1, url, header, null);
				HttpResponse response = mHttpClient.execute(mHttpGet);
				int nStatus = response.getStatusLine().getStatusCode();
				if (nStatus == 200) {
					insertTipEvent();
					parseExamTimeInfo(readFromStream(response.getEntity()));
					return true;
				} else {
					mError = "�޷���ȡ����ʱ��, �����µ�¼";
					return false;
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				mError = "�޷���ȡ����ʱ��, �����µ�¼";
				return false;
			}
		} else {
			mError = "�޷���ȡ����ʱ��, �����µ�¼";
			return false;
		}
	}
	

	/**
	 * 
	 * �ӽ���ϵͳ�л�ȡ���Գɼ�
	 * 
	 * @return boolean
	 * 
	 * */
	public Boolean getScoreFromAdminSys() {
		// TODO Auto-generated method stub
		if (!mFinalAdminSysSite.isEmpty() && !mExamSite.isEmpty()) {
			Log.d("score", "Get Score From Admin Sys");
			try {
				String url = getSubStringFromUrl(mFinalAdminSysSite)
						+ mScoreSite;
				Map<String, String> header = new HashMap<String, String>();
				header.put("Accept",
						"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
				header.put("Accept-Encoding", "gzip,deflate");
				header.put("Accept-Language", "zh-CN,zh;q=0.8");
				header.put("Cache-Control", "max-age=0");
				header.put("Connection", "Keep-Alive");
				header.put("Content-Type", "application/x-www-form-urlencoded");
				header.put("Origin", "http://"
						+ getHostStringFromURL(mFinalAdminSysSite));
				Log.d("score", "1 host: "
						+ getHostStringFromURL(mFinalAdminSysSite));
				Log.d("score", "1 referer:" + mFinalAdminSysSite);
				header.put("Host", getHostStringFromURL(mFinalAdminSysSite));
				header.put("Referer", mFinalAdminSysSite);
				header.put(
						"User-Agent",
						"Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; Touch; MASPJS; rv:11.0) like Gecko");
				setRequest(1, url, header, null);
				HttpResponse response = mHttpClient.execute(mHttpGet);
				int nStatus = response.getStatusLine().getStatusCode();
				if (nStatus == 200) {
					String temp = readFromStream(response.getEntity());
					mViewState = parseViewState(temp);
					parseCurYearAndTerm(temp);
					parseScoreInfo(temp);
					mSuccessful = true;
					return true;
				} else {
					mError = "�޷���ȡ���Գɼ��������µ�¼";
					return false;
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				mError = "�޷���ȡ���Գɼ��������µ�¼";
				return false;
			}
		} else {
			mError = "�޷���ȡ���Գɼ��������µ�¼";
			return false;
		}
	}

	/**
	 * �����ݿ��в��뿼��ʱ������
	 */
	private void insertTipEvent() {
		SharedPreferences preferences = mContext.getSharedPreferences("Puma",
				Context.MODE_PRIVATE);
		String isFirst = preferences.getString("isFirst", "1");
		if (isFirst.equals("1")) {
			DatabaseHelper helper = new DatabaseHelper(mContext, "DIDAREN.db",
					null, 1);
			SQLiteDatabase db = helper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("title", "����ϵͳ�п���ʱ��ķ���ʱ�䲻ȷ����So������ˢ�£���һʱ���ȡ����ʱ����Ϣ!");
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy��MM��dd��-HH:mm");
			String time = dateFormat.format(new Date());
			values.put("start", time);
			values.put("end", "");
			values.put("pos", "");
			values.put("seat", -1);
			values.put("note", "");
			values.put("type", 1);
			db.insert("EventList", null, values);
			db.close();
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString("isFirst", "0");
			editor.commit();
		}
	}

	/**
	 * ��Post Dataʱ��Ҫ��ҳ��lt����Ϣ
	 * 
	 * @param content�Ż���ҳ
	 * @return lt
	 */
	private String parseLtFromLoginWeb(String content) {
		Document doc = Jsoup.parse(content);
		Elements btnDivs = doc.getElementsByClass("btn");
		Element btnDiv = btnDivs.first();
		Elements lts = btnDiv.select("input[name=lt]");
		Element lt = lts.first();
		return lt.attr("value");
	}

	/**
	 * ����¼���ɹ�ʱ����������ҳ����ʾ�Ĵ�����Ϣ
	 * 
	 * @param content
	 * @return ������Ϣ
	 */
	private String parseErrorInfo(String content) {
		String error = "";
		Document doc = Jsoup.parse(content);
		Element status = doc.getElementById("status");
		if (status != null) {
			error = status.text();
		}
		return error;
	}

	/**
	 * �������������ϵͳ����ַ
	 * 
	 * @param content
	 * @return
	 */
	private String parseAdminSys(String content) {
		String result = "";
		Document doc = Jsoup.parse(content);
		Elements elements = doc.select("a");
		for (Element a : elements) {
			if (a.text().equals("�������ϵͳ")) {
				result = a.attr("href");
			}
		}
		return result;
	}

	/**
	 * ��������ʦ�Ľ������ϵͳ����ַ
	 * 
	 * @param content
	 * @return
	 */
	private String parseTeacherAdminSys(String content) {
		String result = "";
		Document doc = Jsoup.parse(content);
		Elements elements = doc.select("a");
		for (Element a : elements) {
			if (a.text().equals("�������")) {
				result = a.attr("href");
			}
		}
		return result;
	}

	/**
	 * �������������ϵͳ�������Ϣ����ַ
	 * 
	 * @param content
	 */
	private void parseRelativeSiteFromAdminSys(String content) {
		Document doc = Jsoup.parse(content);
		Elements lis = doc.getElementsByTag("li");
		Elements temps;
		Element temp;
		for (Element li : lis) {
			String str = li.text();
			if (str.equals("������Ϣ")) {
				temps = li.getElementsByTag("a");
				temp = temps.first();
				Log.d("DIDAREN", str + " : " + temp.attr("href"));
				mPersonalInfoSite = temp.attr("href");
			}
			if (str.equals("ѧ�����˿α�")) {
				temps = li.getElementsByTag("a");
				temp = temps.first();
				Log.d("DIDAREN", str + " : " + temp.attr("href"));
				mCourseSite = temp.attr("href");
			}
			if (str.equals("ѧ�����Բ�ѯ")) {
				temps = li.getElementsByTag("a");
				temp = temps.first();
				Log.d("DIDAREN", str + " : " + temp.attr("href"));
				mExamSite = temp.attr("href");
			}
			if (str.equals("�ɼ���ѯ")) {
				temps = li.getElementsByTag("a");
				temp = temps.first();
				Log.d("DIDAREN", str + " : " + temp.attr("href"));
				mScoreSite = temp.attr("href");
			}
		}
	}

	/**
	 * ��������ʦ�������ϵͳ�������Ϣ����ַ
	 * 
	 * @param content
	 */
	private void parseTeacherRelativeSiteFromAdminSys(String content) {
		Document doc = Jsoup.parse(content);
		Elements lis = doc.getElementsByTag("li");
		Elements temps;
		Element temp;
		for (Element li : lis) {
			String str = li.text();
			if (str.equals("���˼���")) {
				temps = li.getElementsByTag("a");
				temp = temps.first();
				mPersonalInfoSite = temp.attr("href");
			}
			if (str.equals("��ʦ���˿α��ѯ")) {
				temps = li.getElementsByTag("a");
				temp = temps.first();
				mCourseSite = temp.attr("href");
			}
		}
	}

	/**
	 * ����������Ϣ
	 * 
	 * @param content
	 */
	private void parsePersonalInfo(String content) {
		SharedPreferences.Editor editor = mContext.getSharedPreferences("Puma",
				Context.MODE_PRIVATE).edit();
		Document doc = Jsoup.parse(content);
		Elements tds = doc.getElementsByTag("td");
		String id = "";
		Element span = null;
		for (Element td : tds) {
			span = td.getElementsByTag("span").first();
			if (span != null) {
				id = span.attr("id");
				if (id.equals("xh")) {
					editor.putString("number", span.text());
					continue;
				}
				if (id.equals("xm")) {
					editor.putString("name", span.text());
					continue;
				}
				if (id.equals("lbl_xb")) {
					editor.putString("sex", span.text());
					continue;
				}
				if (id.equals("lbl_csrq")) {
					editor.putString("born", span.text());
					continue;
				}
				if (id.equals("lbl_lydq")) {
					editor.putString("home", span.text());
					continue;
				}
				if (id.equals("lbl_xy")) {
					editor.putString("college", span.text());
					continue;
				}
				if (id.equals("lbl_zymc")) {
					editor.putString("major", span.text());
					continue;
				}
				if (id.equals("lbl_xzb")) {
					editor.putString("class", span.text());
					continue;
				}
				if (id.equals("lbl_dqszj")) {
					editor.putString("year", span.text());
					continue;
				}
				if (id.equals("lbl_ssh")) {
					editor.putString("room", span.text());
					continue;
				}
			}
		}
		editor.commit();
	}

	/**
	 * ������ʦ������Ϣ
	 * 
	 * @param content
	 */
	private void parseTeacherPersonalInfo(String content) {
		SharedPreferences.Editor editor = mContext.getSharedPreferences("Puma",
				Context.MODE_PRIVATE).edit();
		Document doc = Jsoup.parse(content);
		Element tbody = doc.getElementsByTag("tbody").first();
		Element temp = tbody.getElementsByAttributeValue("id", "xm").first();
		if (temp != null) {
			// ����
			editor.putString("name", temp.text());
		}
		temp = tbody.getElementsByAttributeValue("id", "zgh1").first();
		if (temp != null) {
			// ְ����
			editor.putString("number", temp.text());
		}
		temp = tbody.getElementsByAttributeValue("id", "xb").first();
		temp = temp.getElementsByAttributeValue("selected", "selected").first();
		if (temp != null) {
			// �Ա�
			editor.putString("sex", temp.text());
		}
		temp = tbody.getElementsByAttributeValue("id", "csrq").first();
		if (temp != null) {
			// ��������
			editor.putString("born", temp.attr("value"));
		}
		temp = tbody.getElementsByAttributeValue("id", "bm").first();
		temp = temp.getElementsByAttributeValue("selected", "selected").first();
		if (temp != null) {
			// ѧԺ
			editor.putString("college", temp.text());
		}
		temp = tbody.getElementsByAttributeValue("id", "zc").first();
		temp = temp.getElementsByAttributeValue("selected", "selected").first();
		if (temp != null) {
			// ְ��
			editor.putString("home", temp.text());
		}
		temp = tbody.getElementsByAttributeValue("id", "jxyjfx").first();
		if (temp != null) {
			// ѧ�Ʒ���
			editor.putString("major", temp.attr("value"));
		}
		editor.commit();
	}

	/**
	 * �����γ���Ϣ
	 * 
	 * @param content
	 */
	private void parseCourseInfo(String content) {
		Document doc = Jsoup.parse(content);
		Elements tables = doc.getElementsByClass("blacktab");
		Element table = tables.first();
		Elements trs = table.getElementsByTag("tr");
		int count = 0;
		for (Element tr : trs) {
			assistParseCourse(tr, count);
			count++;
		}
		editor.putString("course_morning_first", Arrays.toString(morning_first));
		editor.putString("course_morning_second",
				Arrays.toString(morning_second));
		editor.putString("course_afternoon_first",
				Arrays.toString(afternoon_first));
		editor.putString("course_afternoon_second",
				Arrays.toString(afternoon_second));
		editor.putString("course_evening_first", Arrays.toString(evening_first));
		editor.putString("course_evening_second",
				Arrays.toString(evening_second));
		Log.d("DIDAREN", "ת��" + Arrays.toString(morning_first).length()
				+ Arrays.toString(morning_first));
		Log.d("DIDAREN", "ת��" + Arrays.toString(morning_second).length()
				+ Arrays.toString(morning_second));
		Log.d("DIDAREN", "ת��" + Arrays.toString(afternoon_first).length()
				+ Arrays.toString(afternoon_first));
		editor.commit();
	}

	/**
	 * ������ʦ�γ���Ϣ
	 * 
	 * @param content
	 */
	private void parseTeacherCourseInfo(String content) {
		Document doc = Jsoup.parse(content);
		Elements tables = doc.getElementsByClass("blacktab");
		Element table = tables.first();
		Elements trs = table.getElementsByTag("tr");
		int count = 0;
		for (Element tr : trs) {
			assistParseCourse(tr, count);
			Log.d("Tea", count + tr.text() + "\n");
			count++;
		}
		editor.putString("course_morning_first", Arrays.toString(morning_first));
		editor.putString("course_morning_second",
				Arrays.toString(morning_second));
		editor.putString("course_afternoon_first",
				Arrays.toString(afternoon_first));
		editor.putString("course_afternoon_second",
				Arrays.toString(afternoon_second));
		editor.putString("course_evening_first", Arrays.toString(evening_first));
		editor.putString("course_evening_second",
				Arrays.toString(evening_second));
		Log.d("Tea",
				"ת��" + Arrays.toString(morning_first).length()
						+ Arrays.toString(morning_first));
		Log.d("Tea",
				"ת��" + Arrays.toString(morning_second).length()
						+ Arrays.toString(morning_second));
		Log.d("Tea",
				"ת��" + Arrays.toString(afternoon_first).length()
						+ Arrays.toString(afternoon_first));
		editor.commit();
	}

	/**
	 * ��������ʱ����Ϣ
	 * 
	 * @param content
	 */
	private void parseExamTimeInfo(String content) {
		Document doc = Jsoup.parse(content);
		Element dataList = doc.getElementsByClass("datelist").first();
		Elements datas = dataList.getElementsByTag("tr");

		DatabaseHelper helper = new DatabaseHelper(mContext, "DIDAREN.db",
				null, 1);
		SQLiteDatabase db = helper.getWritableDatabase();

		int nSize = datas.size();
		for (int i = 1; i < nSize; i++) {
			Elements columns = datas.get(i).getElementsByTag("td");
			if (columns.get(3).text().length() != 1) {
				String title = columns.get(1).text();
				String str = columns.get(3).text();
				str = str.replace("(", "-");
				str = str.replace(")", "");
				int nEnd = str.lastIndexOf("-");
				String start = str.substring(0, nEnd);
				String end = str.substring(nEnd + 1, str.length());
				String pos = columns.get(4).text();
				String seat = columns.get(6).text();
				Cursor cursor = db.rawQuery(
						"select * from EventList where title = ?",
						new String[] { title });
				if (!cursor.moveToFirst()) {
					db.execSQL(
							"insert into EventList (title, start, end, pos, seat, type) values(?, ?, ?, ?, ?, ?)",
							new String[] { title, start, end, pos, seat, "1" });
				}
				cursor.close();
			}
		}
		db.close();
	}

	/**
	 * Э�������γ���Ϣ
	 * 
	 * @param tr
	 */
	private void assistParseCourse(Element tr, int flag) {
		Elements tds = tr.getElementsByTag("td");
		String firstText = tds.first().text();

		// ��ʼ��sharedpreference ��editor
		editor = mContext.getSharedPreferences("course", Activity.MODE_PRIVATE)
				.edit();

		if (flag == 2) {
			String temp = tds.get(1).text();
			int section = Integer
					.parseInt(temp.substring(1, temp.indexOf("��")));
			int count = tds.size();
			for (int i = 2; i < count; i++) {
				Element td = tds.get(i);
				temp = "��" + numberToString(i - 1) + "��" + section + ","
						+ (section + 1) + "��";
				morning_first[i - 2] = td.text();
				Log.d("DIDAREN", "�����һ��-" + td.text());
			}
		} else if (flag == 4) {
			String temp;
			int section = Integer.parseInt(firstText.substring(1,
					firstText.indexOf("��")));
			int count = tds.size();
			for (int i = 1; i < count; i++) {
				Element td = tds.get(i);
				temp = "��" + numberToString(i) + "��" + section + ","
						+ (section + 1) + "��";
				morning_second[i - 1] = td.text();
				Log.d("DIDAREN", "����ڶ���-" + td.text());
			}
		} else if (flag == 6) {
			String temp = tds.get(1).text();
			int section = Integer
					.parseInt(temp.substring(1, temp.indexOf("��")));
			int count = tds.size();
			for (int i = 2; i < count; i++) {
				Element td = tds.get(i);
				temp = "��" + numberToString(i - 1) + "��" + section + ","
						+ (section + 1) + "��";
				afternoon_first[i - 2] = td.text();
				Log.d("DIDAREN", "�����һ��-" + td.text());
			}
		} else if (flag == 8) {
			String temp;
			int section = Integer.parseInt(firstText.substring(1,
					firstText.indexOf("��")));
			int count = tds.size();
			for (int i = 1; i < count; i++) {
				Element td = tds.get(i);
				temp = "��" + numberToString(i) + "��" + section + ","
						+ (section + 1) + "��";
				afternoon_second[i - 1] = td.text();
				Log.d("DIDAREN", "����ڶ���-" + td.text());
			}
		} else if (flag == 10) {
			String temp = tds.get(1).text();
			int section = Integer
					.parseInt(temp.substring(1, temp.indexOf("��")));
			int count = tds.size();
			for (int i = 2; i < count; i++) {
				Element td = tds.get(i);
				temp = "��" + numberToString(i - 1) + "��" + section + ","
						+ (section + 1) + "��";
				evening_first[i - 2] = td.text();
				Log.d("DIDAREN", "���ϵ�һ��-" + td.text());
			}
		} else if (flag == 12) {
			String temp;
			int section = Integer.parseInt(firstText.substring(1,
					firstText.indexOf("��")));
			int count = tds.size();
			for (int i = 1; i < count; i++) {
				Element td = tds.get(i);
				temp = "��" + numberToString(i) + "��" + section + ","
						+ (section + 1) + "��";
				evening_second[i - 1] = td.text();
				Log.d("DIDAREN", "���ϵڶ���-" + td.text());
			}
		}
	}

	/**
	 * ������ת���ɺ��֣�1->һ��2->��
	 * 
	 * @param number
	 * @return
	 */
	private String numberToString(int number) {
		String str = null;
		switch (number) {
		case 1:
			str = "һ";
			break;
		case 2:
			str = "��";
			break;
		case 3:
			str = "��";
			break;
		case 4:
			str = "��";
			break;
		case 5:
			str = "��";
			break;
		case 6:
			str = "��";
			break;
		case 7:
			str = "��";
			break;
		}
		return str;
	}

	/**
	 * ���������ҳ��HTML����
	 */
	private String readFromStream(HttpEntity entity) throws Exception {
		byte[] bResult = EntityUtils.toByteArray(entity);
		return new String(bResult, "gb2312");
	}

	/**
	 * ������ս������ϵͳ���Ӵ�
	 * 
	 * @param url
	 * @return
	 */
	private String getSubStringFromUrl(String url) {
		int start = 0;
		int end = url.indexOf("/", url.indexOf("/", url.indexOf("/") + 2) + 1) + 1;
		return url.substring(start, end);
	}

	private String getHostStringFromURL(String url) {
		int start = url.indexOf("//") + 2;
		int end = url.indexOf("/", start);
		return url.substring(start, end);
	}

	private String mViewState;

	/**
	 * ����ѡ�е�ѧ�ڻ�ȡ���Գɼ�
	 * 
	 * @return boolean
	 */
	public boolean getSelectedTermScoreFromAdminSys(String year, String term) {
		if (!mFinalAdminSysSite.isEmpty() && !mExamSite.isEmpty()) {
			Log.d("score", "Get SelectedTerm Score From Admin Sys");
			try {
				String url = getSubStringFromUrl(mFinalAdminSysSite)
						+ mScoreSite + "/";
				Map<String, String> header = new HashMap<String, String>();
				header.put("Accept", "text/html,application/xhtml+xml,*/*");
				header.put("Accept-Encoding", "gzip,deflate");
				header.put("Accept-Language", "zh-Hans-CN,zh-Hans;q=0.5");
				header.put("Connection", "Keep-Alive");
				header.put("Host", getHostStringFromURL(mFinalAdminSysSite));
				// header.put("Referer", url);
				header.put(
						"User-Agent",
						"Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; Touch; MASPJS; rv:11.0) like Gecko");
				Map<String, String> params = new HashMap<String, String>();
				params.put("ddlxn", year);
				Log.d("score", " 2 Get SelectedTerm Score From Admin Sys YEAR:"
						+ year);
				params.put("ddlxq", term);
				Log.d("score", "2 Get SelectedTerm Score From Admin Sys Term:"
						+ term);
				params.put("__EVENTTARGET", "");
				params.put("__EVENTARGUMENT", "");
				params.put("__VIEWSTATE", mViewState);
				Log.d("score", "2 Get SelectedTerm Score From Admin Sys View:"
						+ mViewState);
				params.put("btnCx", " ��  ѯ ");
				setRequest(0, url, header, params);
				HttpResponse response = mHttpClient.execute(mHttpPost);
				int nStatus = response.getStatusLine().getStatusCode();
				Log.d("score", nStatus
						+ "Get SelectedTerm Score From Admin Sys success");
				if (nStatus == 200) {
					Log.d("score",
							"Get SelectedTerm Score From Admin Sys success");
					mError = readFromStream(response.getEntity());
					parseScoreInfo(mError);
					return true;
				} else {
					mError = "δ֪�����޷���ȡ�γ���Ϣ";
					return false;
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				mError = "δ֪�����޷���ȡ�γ���Ϣ";
				return false;
			}
		} else {
			mError = "δ֪�����޷���ȡ�γ���Ϣ";
			return false;
		}
	}

	private String parseViewState(String content) {
		Log.d("DIDAREN", "Parse ViewSatet");
		Document doc = Jsoup.parse(content);
		Element element = doc
				.getElementsByAttributeValue("name", "__VIEWSTATE").first();
		return element.attr("value");
	}

	private void parseCurYearAndTerm(String content) {
		Document document = Jsoup.parse(content);
		Elements options = document.getElementsByAttributeValue("selected",
				"selected");
		SharedPreferences.Editor editor = mContext.getSharedPreferences(
				"course_year", Context.MODE_PRIVATE).edit();
		String year = options.get(0).attr("value");
		String term = options.get(1).attr("value");
		editor.putString("current_year", year);
		editor.putString("current_term", term);
		editor.putString("select_year", year);
		editor.putString("select_term", term);
		Log.d("score", year + term + "true");
		editor.putBoolean(year + term, true);
		editor.commit();
	}

	/**
	 * ����ѧ���ɼ���Ϣ
	 * 
	 * @param content
	 */
	private void parseScoreInfo(String content) {
		// TODO Auto-generated method stub
		Log.d("score", "Parse Score Info");
		// �����ַ�����ſ�������
		String year;
		int term;
		String course;
		String number;
		String style;
		double credit;
		String score;
		double grade;
		int score_int;

		// �õ���ȡ���ݿ����
		DatabaseHelper helper = new DatabaseHelper(mContext, "DIDAREN.db",
				null, 1);
		SQLiteDatabase db = helper.getWritableDatabase();

		Document doc = Jsoup.parse(content);

		Element dataList = doc.getElementsByClass("datelist").first();
		Elements datas = dataList.getElementsByTag("tr");

		int dataSize = datas.size();
		for (int i = 1; i < dataSize; i++) {
			Elements column = datas.get(i).getElementsByTag("td");
			year = column.get(0).text();
			term = Integer.parseInt(column.get(1).text());
			number = column.get(2).text();
			course = column.get(3).text();
			style = column.get(4).text();
			credit = Double.parseDouble(column.get(6).text());
			score = column.get(11).text();
			if (isNumber(score)) {
				// ����ɼ�������
				score_int = Integer.parseInt(score);
				if (score_int < 60) {
					// ������
					grade = 0;
				} else {
					score_int -= 60;
					grade = ((float) (score_int / 5)) * 0.5 + 1;
				}
				Log.d("score", year + "-" + term + "-" + course + "-" + style
						+ "-" + credit + "-�ɼ���" + score + "-����:" + grade);
				Cursor cursor = db.rawQuery(
						"select * from Score where number = ?",
						new String[] { number });
				if (!cursor.moveToFirst()) {
					db.execSQL(
							"insert into Score (course, style, year, term, credit, score, grade, number) values(?, ?, ?, ?, ?, ?, ?, ?)",
							new String[] { course, style, year, term + "",
									credit + "", score, grade + "", number });
				}
				cursor.close();
			} else {
				if (score.equals("����") || score.equals("A")) {
					grade = 4.5;
				} else if (score.equals("����") || score.equals("B")) {
					grade = 3.5;
				} else if (score.equals("�е�") || score.equals("C")) {
					grade = 2.5;
				} else if (score.equals("����") || score.equals("D")) {
					grade = 1.0;
				} else if (score.equals("������") || score.equals("E")) {
					grade = 0;
				} else {
					grade = 0;
				}
				Log.d("score", year + "-" + term + "-" + course + "-" + style
						+ "-" + credit + "-�ɼ���" + score + "-����:" + grade);
				Cursor cursor = db.rawQuery(
						"select * from Score where number = ?",
						new String[] { number });
				if (!cursor.moveToFirst()) {
					db.execSQL(
							"insert into Score (course, style, year, term, credit, score, grade, number) values(?, ?, ?, ?, ?, ?, ?, ?)",
							new String[] { course, style, year, term + "",
									credit + "", score, grade + "", number });
				}
				cursor.close();
			}
		}

		// //��ȡyear��Ϣ start
		int year_index;
		Element element_choose_year = doc.getElementById("ddlxn");
		Elements elements_choose_year = element_choose_year
				.getElementsByTag("option");
		SharedPreferences pref = mContext.getSharedPreferences("course_year",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString("first", elements_choose_year.get(0).text());
		year_index = elements_choose_year.size() - 1;
		editor.putString("second", elements_choose_year.get(year_index).text());
		year_index = elements_choose_year.size() - 2;
		editor.putString("third", elements_choose_year.get(year_index).text());
		year_index = elements_choose_year.size() - 3;
		editor.putString("forth", elements_choose_year.get(year_index).text());
		year_index = elements_choose_year.size() - 4;
		editor.putString("fifth", elements_choose_year.get(year_index).text());
		editor.commit();
		db.close();
	}

	/**
	 * �жϳɼ��Ƿ�Ϊ����
	 * 
	 * @param str
	 * @return boolean
	 */
	public boolean isNumber(String str) {

		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		} else {
			return true;
		}
	}
}
