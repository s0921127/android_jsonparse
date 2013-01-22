package com.takanolab.android;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

public class JsonParse extends Activity{
	static final String SEARCH_URL = "https://api.foursquare.com/v2/venues/search?ll=35.6865271,139.692311&" +
			"oauth_token=G4RUWUY4YAKSUWLEWLXPHRBQJZATY4XPTIAI4OT02CXMLMM3&v=20121017";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
      //staticブロックなど、アプリケーション開始前に実行する。
    	StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        
        String scheme = "https";
        String authority = "api.foursquare.com";
        String path = "/v2/venues/search";
        
        String ll = "35.454564,139.359051";
        String oauth_token = "G4RUWUY4YAKSUWLEWLXPHRBQJZATY4XPTIAI4OT02CXMLMM3";
        String v = "20130122";
        Uri.Builder uriBuilder = new Uri.Builder();
        
        uriBuilder.scheme(scheme);
        uriBuilder.authority(authority);
        uriBuilder.path(path);
        uriBuilder.appendQueryParameter("ll", ll);
        uriBuilder.appendQueryParameter("oauth_token", oauth_token);
        uriBuilder.appendQueryParameter("v", v);
     
        String uri = uriBuilder.toString();
        
        HttpClient httpClient = new DefaultHttpClient();
        HttpParams params = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, 1000);
        HttpConnectionParams.setSoTimeout(params, 1000);
        
        HttpUriRequest httpRequest = new HttpGet(uri);
        
        HttpResponse httpResponse = null;
        
        try {
            httpResponse = httpClient.execute(httpRequest);
        }
        catch (ClientProtocolException e) {
            //例外処理
        }
        catch (IOException e){
            //例外処理
        }
        
        String json = null;
        
        if (httpResponse != null && httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
            HttpEntity httpEntity = httpResponse.getEntity();
            try {
                json = EntityUtils.toString(httpEntity);
            }
            catch (ParseException e) {
                //例外処理
            }
            catch (IOException e) {
                //例外処理
            }
            finally {
                try {
                    httpEntity.consumeContent();
                }
                catch (IOException e) {
                    //例外処理
                }
            }
        }
        
        httpClient.getConnectionManager().shutdown();
        
        // TextView 表示用のテキストバッファ
        StringBuffer stringBuffer = new StringBuffer();
        
        try {
			//Log.e("導入", "debug8");
			JSONObject rootObject = new JSONObject(json);
			JSONObject responseObject = rootObject.getJSONObject("response");
			JSONArray venuesArray = responseObject.getJSONArray("venues");

			int id = 0; /* 訪れたユーザの数の一番多いvenueの順番 */
			double distance = 1000;
			
			/* 取得したvenueリスト中のvenueの数をログに出力 */
			Log.e(String.valueOf(venuesArray.length()), "venue数");

			/* 取得したvenueリストのvenueの中で距離の一番近いvenueを検索 */
			for (int i = 0; i < venuesArray.length(); i++) {
				JSONObject locationObject = venuesArray.getJSONObject(i).getJSONObject("location");
					distance = Integer.parseInt(locationObject.getString("distance"));
					id = i;
					
					JSONArray categoriesArray = venuesArray.getJSONObject(id).getJSONArray("categories");

					//Log.e("オブジェクトのさかのぼり", "debug5");
					JSONObject bookObject[] = new JSONObject[2];

					bookObject[0] = venuesArray.getJSONObject(id);
					bookObject[1] = categoriesArray.getJSONObject(0);

					//Log.e("オブジェクトの代入", "debug6");
					/* 地名のデータを取得 */
					String name = bookObject[0].getString("name");

					/* カテゴリのデータを取得 */
					String category = bookObject[1].getString("shortName");

					// resultsString = "成功";
				
		            stringBuffer.append("場所：" + name + "\n");
		            stringBuffer.append("カテゴリ：" + category + "\n");
		            stringBuffer.append("距離：" + distance + "\n");
			}

			
        } 
        catch (JSONException e) {
            // 例外処理
        }
        
        TextView textView = new TextView(this);
        textView.setHorizontallyScrolling(true);
        textView.setText(stringBuffer);
        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(textView);
        setContentView(scrollView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        
    }
}