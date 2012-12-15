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
import android.view.ViewGroup.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

public class JsonParse extends Activity implements LocationListener {
	static final String SEARCH_URL = "https://api.foursquare.com/v2/venues/search?ll=35.6865271,139.692311&" +
			"oauth_token=G4RUWUY4YAKSUWLEWLXPHRBQJZATY4XPTIAI4OT02CXMLMM3&v=20121017";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // LocationManagerを取得
        LocationManager mLocationManager =
             (LocationManager) getSystemService(Context.LOCATION_SERVICE);
 
        // Criteriaオブジェクトを生成
        Criteria criteria = new Criteria();
 
        // Accuracyを指定(低精度)
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
         
        // PowerRequirementを指定(低消費電力)
        criteria.setPowerRequirement(Criteria.POWER_LOW);
         
        // ロケーションプロバイダの取得
        String provider = mLocationManager.getBestProvider(criteria, true);
 
        // LocationListenerを登録
        mLocationManager.requestLocationUpdates(provider, 0, 0, this);
        
    }
    
    @Override
    public void onLocationChanged(Location location) {
        // 緯度の表示
        //TextView tv_lat = (TextView) findViewById(R.id.Latitude);
        //tv_lat.setText("Latitude:"+location.getLatitude());
 
        // 経度の表示
        //TextView tv_lng = (TextView) findViewById(R.id.Longitude);
        //tv_lng.setText("Longitude:"+location.getLongitude());
    	
    	//staticブロックなど、アプリケーション開始前に実行する。
    	StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        
        String scheme = "https";
        String authority = "api.foursquare.com";
        String path = "/v2/venues/search";
        
        String ll = location.getLatitude() + ", " + location.getLongitude();
        String oauth_token = "G4RUWUY4YAKSUWLEWLXPHRBQJZATY4XPTIAI4OT02CXMLMM3";
        String v = "20121017";
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
            JSONObject rootObject = new JSONObject(json);
            JSONObject responseObject = rootObject.getJSONObject("response");
            JSONArray  venuesArray = responseObject.getJSONArray("venues");
            JSONArray  categoriesArray = venuesArray.getJSONObject(0).getJSONArray("categories");
                      
            JSONObject bookObject[] = new JSONObject[2];
            
            bookObject[0] = venuesArray.getJSONObject(0);
            bookObject[1] = categoriesArray.getJSONObject(0);
            
            // 地名のデータを取得
            String name = bookObject[0].getString("name");
            
            //　カテゴリのデータを取得
            String category = bookObject[1].getString("shortName");
            
            // 「地名」と「カテゴリ」をテキストに追加
            stringBuffer.append("場所：" + name + "\n");
            stringBuffer.append("カテゴリ：" + category + "\n");
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
 
    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
         
    }
 
    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
         
    }
 
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
 
    }
}