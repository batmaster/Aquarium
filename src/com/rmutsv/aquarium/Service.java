package com.rmutsv.aquarium;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

public class Service {
	
	public static final int SOCKET_TIMEOUT_TRYING = 5;
	public static final int TIMEOUT = 10000;
	
	public static String sendHttpRequest(final Context context, final String parameter, int socketTimeoutTrying) {
		Log.d("Try", socketTimeoutTrying + "");
		String serverResponse = "ERR";
		
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpParams params = httpclient.getParams();
			HttpConnectionParams.setConnectionTimeout(params, TIMEOUT);
			HttpConnectionParams.setSoTimeout(params, TIMEOUT);
			
			URI website = new URI("http://" + SharedValues.getStringPref(context, SharedValues.KEY_IP) + ":"+ SharedValues.getStringPref(context, SharedValues.KEY_PORT) + "/?relay=" + parameter);
			Log.d("http", website.toString());
			HttpGet getRequest = new HttpGet();
			getRequest.setURI(website);
			HttpResponse response = httpclient.execute(getRequest);
			InputStream content = null;
			content = response.getEntity().getContent();
			BufferedReader in = new BufferedReader(new InputStreamReader(content));
			serverResponse = in.readLine();
			content.close();
			
		} catch (URISyntaxException e) {
			e.printStackTrace();
//			serverResponse = "URISyntaxException";
		} catch (ClientProtocolException e) {
			e.printStackTrace();
//			serverResponse = "ClientProtocolException";
		} catch (HttpHostConnectException e) {
			e.printStackTrace();	
//			serverResponse = "HttpHostConnectException";
		} catch (ConnectTimeoutException e) {
			e.printStackTrace();	
//			serverResponse = "ConnectTimeoutException";
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
//			if (socketTimeoutTrying - 1 == 0)
//				serverResponse = "SocketTimeoutException";
//			else
//				serverResponse = sendHttpRequest(context, parameter, socketTimeoutTrying - 1);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Log.d("http", "Response: " + serverResponse);
		return serverResponse;
	}
}
