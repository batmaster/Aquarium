package com.rmutsv.aquarium;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;

public class Request {
	
	private Request() {
		
	}
	
	public static String getDevices(Context context) throws IOException {
		String username = SharedValues.getStringPref(context, SharedValues.KEY_USERNAME);
//		return request(String.format("SELECT p.* FROM ping p, registers r WHERE r.username = '%s' AND p.bid = r.bid", username));
		String res = request(String.format("SELECT p.* FROM ping p, registers r WHERE r.username = '%s' AND p.bid = r.bid", username));
		System.out.println();
		return res;
	}
	
	private static String request(String str) throws IOException {
		str = str.replace("'", "xxaxx").replace("(", "xxbxx").replace(")", "xxcxx").replace(">", "xxdxx");
		try {
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
			HttpConnectionParams.setSoTimeout(httpParameters, 10000);

			HttpClient httpClient = new DefaultHttpClient(httpParameters);
			HttpPost httpPost = new HttpPost(SharedValues.HOST_DB);
	
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("sql", str));
			
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
	
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity entity = httpResponse.getEntity();
	
			if (entity != null) {
				InputStream is = entity.getContent();
				StringBuffer sb = new StringBuffer();
				String line = null;
	
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				while ((line = reader.readLine()) != null) {
					sb.append(line + "!!!");
				}
				reader.close();
	
				return sb.toString();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		}
		
		return "";
	}
}
