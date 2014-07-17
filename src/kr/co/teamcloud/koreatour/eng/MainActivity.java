package kr.co.teamcloud.koreatour.eng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity
{
	
	private static final String TAG = "KoreaTour";	//디버그 태그
	
	private List<Map<String, Object>> tourList = new ArrayList<Map<String, Object>>();
	
	private TextView textView;
	
	
	private SimpleAdapter simpleAdapter;
	
	// url to make request
	private static String url = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/areaBasedList?MobileOS=AND&MobileApp=koreaTour&_type=json";
	
	// JSON Node names
	private static final String TAG_RESPONSE = "response";
	private static final String TAG_HEADER = "header";
	private static final String TAG_RESULT_CODE = "resultCode";
	private static final String TAG_RESULT_MSG = "resultMsg";
	
	
	private static final String TAG_BODY = "body";
	private static final String TAG_ITEMS = "items";
	private static final String TAG_ITEM = "item";

	private static final String TAG_CONTENT_ID = "contentid";
	private static final String TAG_CONTENT_TYPE_ID = "contenttypeid";
	private static final String TAG_TITLE= "title";
	private static final String TAG_ADDR1= "addr1";
	
	/*
	private static final String TAG_ITEM = "firstimage";
	private static final String TAG_ITEM = "firstimage2";
	private static final String TAG_ITEM = "mapx";
	private static final String TAG_ITEM = "mapy";
	private static final String TAG_ITEM = "readcount";
	*/
	
	

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		Intent i = new Intent(MainActivity.this, TouristListActivity.class);
	    startActivity(i);
	    finish();
		
		
		//네트워크 연결 여부 확인하기
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			// fetch data
			//new DownloadWebpageTask().execute(requestUrl);
		} else {
			// display error
			textView.setText("No network connection available.");
		}			
    }
	
	// Uses AsyncTask to create a task away from the main UI thread. This task takes a 
     // URL string and uses it to create an HttpUrlConnection. Once the connection
     // has been established, the AsyncTask downloads the contents of the webpage as
     // an InputStream. Finally, the InputStream is converted into a string, which is
     // displayed in the UI by the AsyncTask's onPostExecute method.
     private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
	 
        @Override
        protected String doInBackground(String... urls) {
              
            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid. - " + e.getMessage();
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            textView.setText(result);
			
			try {
				JSONObject jsonObj = new JSONObject(result);
				JSONObject reponse = jsonObj.getJSONObject(TAG_RESPONSE);
				JSONObject header = reponse.getJSONObject(TAG_HEADER);
				JSONObject body = reponse.getJSONObject(TAG_BODY);
				JSONObject items = body.getJSONObject(TAG_ITEMS);				
				JSONArray item = items.getJSONArray(TAG_ITEM);
				for(int i = 0, l=item.length(); i < l; i++){
					JSONObject obj = item.getJSONObject(i);
					
					String title = obj.getString(TAG_TITLE);
					String addr1 = obj.getString(TAG_ADDR1);
					
					Map data = new HashMap();
					data.put(TAG_TITLE, title);
					data.put(TAG_ADDR1, addr1);
					tourList.add(data);
				}
				
				simpleAdapter.notifyDataSetChanged();
				
			} catch (JSONException e) {
				Log.e("JSON Parser", "Error parsing data " + e.toString());
				Toast.makeText(MainActivity.this, "Error parsing data " + e.toString(), Toast.LENGTH_LONG).show();
			}
       }
    }
	
	// Given a URL, establishes an HttpUrlConnection and retrieves
	// the web page content as a InputStream, which it returns as
	// a string.
	private String downloadUrl(String myurl) throws IOException {
		InputStream is = null;
		BufferedReader br = null;		
		try {
			URL url = new URL(myurl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000 /* milliseconds */);
			conn.setConnectTimeout(15000 /* milliseconds */);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			
			// Starts the query
			conn.connect();
			int response = conn.getResponseCode();
			Log.d(TAG, "The response is: " + response);
	
			is = conn.getInputStream();
			br = new BufferedReader(new InputStreamReader(is, "utf-8"));			
			StringBuilder sb = new StringBuilder();
			String inputLine = null;
			while ((inputLine = br.readLine()) != null)
			{
				sb.append(inputLine);
			}
			
			conn.disconnect();
			return sb.toString();
		} finally {
			if (br != null) {
				try { br.close(); } catch(IOException e){}
			} 
			if (is != null) {
				try { is.close(); } catch(IOException e){}
			} 				
		}
	}
}
