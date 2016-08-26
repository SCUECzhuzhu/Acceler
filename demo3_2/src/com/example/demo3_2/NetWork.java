package com.example.demo3_2;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;

/**
 * 网络访问类使用Volley
 */
public class NetWork {
    
	// 等待改动的服务器url
    public static final String TAG = "NetWork";
    public static final String url = "http://2.shujukutest1.applinzi.com/lianjieAndroid/login.php";
    static OkHttpClient client = new OkHttpClient();
    static RequestQueue queue;
    public static String result;

    public static void sendToServer(Context context, final String name, final String password) {        
    	queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            
        	@Override
            public void onResponse(String response) {
                Log.v(TAG, response);               
            }
        	
        }, new Response.ErrorListener() {
            
        	@Override
            public void onErrorResponse(VolleyError error) {
                Log.v(TAG, error.toString());
            }
        	
        }) {           
        	@Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                
                // 改动
                params.put("name", name);
                params.put("passwd", password);
                Log.v(TAG, name);
                Log.v(TAG, password);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {             
            	Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                return params;
            }
        };
        queue.add(sr);
    }

    public static String postRequest(String name, String passwd) {
    	
        String url = "http://2.shujukutest1.applinzi.com/lianjieAndroid/login.php";
        Log.d(TAG, "Test");
        
        // 改动
        FormBody formBody = new FormBody.Builder()
                .add("name", name)
                .add("passwd", passwd)
                .build();

        final okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        new Thread(new Runnable() {
            
        	@Override
            public void run() {
                okhttp3.Response response = null;
                try {                 
                	response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        
                    	Log.d(TAG, "打印POST响应的数据1：" + response.body().string());
                        result = response.body().string();
                    } else {
                        throw new IOException("Unexpected code " + response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();                   
                }
            }
        }).start();
        
        return result;        
    }
    
    // 提交手机移动每十分钟移动的次数
    public static void postFrequency(String name,String frequency) {
        
    	String url = "http://2.shujukutest1.applinzi.com/lianjieAndroid/insert.php";
        FormBody formBody = new FormBody.Builder()
                .add("name",name)
                .add("frequency",frequency)
                .build();

        final okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        new Thread(new Runnable() {
            
        	@Override
            public void run() {
                okhttp3.Response response = null;
                try {
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        
                    	Log.i(TAG, "打印POST响应的数据2：" + response.body().string());
                    } else {
                        throw new IOException("Unexpected code " + response);
                    }
                } catch (IOException e) {
                	
                    e.printStackTrace();
                }
            }
        }).start();

    }
    
}
