package com.sparke.web.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class ConnectPHP {
	static InputStream is;

	public static InputStream getPHPData(String url) {
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(url);
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return is;
	}

	public static String getSignInData(InputStream is) {
		String result = null;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			sb.append(reader.readLine() + "\n");
			String line = "0";
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			result = sb.toString();
			result = result.toLowerCase();
			result = result.trim();
			if (result.equals("1")) {
				result = "�Բ���û������";
			} else if (result.equals("2")) {
				result = "����ǩ���������ظ�ǩ����";
			} else if (result.equals("3")) {
				result = "ǩ���ɹ���";
			}
		} catch (Exception ex) {
		}
		return result;
	}

	/**
	 * ���ղ�ѯǩ����Ա��inputstream��
	 * 
	 * @param is
	 * @return
	 */
	public static String getCheckData(InputStream is) {

		String result;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			sb.append(reader.readLine() + "\n");
			String line = "0";
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			result = sb.toString();
			result = result.toLowerCase();
			result = result.trim();
			if (result.equals("1")) {
				// ��������Ϊ1��������֤ʧ��
				return null;
			} else {
				// ������֤�ɹ�

			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;

	}
}
