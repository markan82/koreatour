package kr.co.teamcloud.koreatour.tourist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.teamcloud.koreatour.R;
import kr.co.teamcloud.koreatour.common.CommonConstants;
import kr.co.teamcloud.koreatour.util.JSONParser;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class TourListActivity extends Activity {
	private List<Map<String, Object>> tourList = new ArrayList<Map<String, Object>>();

	private TextView textView;
	private EditText inKeyword;
	private Button btnSearch;

	private SimpleAdapter simpleAdapter;

	// url to make request
	private String areaBasedListUrl = CommonConstants.requestURL
			+ "areaBasedList?MobileOS=AND&MobileApp=koreaTour&_type=json&listYN=Y&serviceKey="
			+ CommonConstants.serviceKey;
	
	private String searchKeywordUrl = CommonConstants.requestURL
			+ "searchKeyword?MobileOS=AND&MobileApp=koreaTour&_type=json&listYN=Y&serviceKey="
			+ CommonConstants.serviceKey;

	// JSON Node names
	private final String TAG_RESPONSE = "response";
	private final String TAG_HEADER = "header";
	private final String TAG_RESULT_CODE = "resultCode";
	private final String TAG_RESULT_MSG = "resultMsg";

	private final String TAG_BODY = "body";
	private final String TAG_ITEMS = "items";
	private final String TAG_ITEM = "item";

	private final String TAG_CONTENT_ID = "contentid";
	private final String TAG_CONTENT_TYPE_ID = "contenttypeid";
	private final String TAG_TITLE = "title";
	private final String TAG_ADDR1 = "addr1";
	private final String TAG_ADDR2 = "addr2";

	private final String TAG_FIRSTIMAGE = "firstimage";
	private final String TAG_FIRSTIMAGE2 = "firstimage2";
	private final String TAG_MAPX = "mapx";
	private final String TAG_MAPY = "mapy";
	private final String TAG_READCOUNT = "readcount";

	// 검색 조건
	private int numOfRows = 20;
	private int pageNo = 1;
	private String arrange = "B";
	private int contentTypeId = 12;
	private String keyword = "";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tourlist_list);

		textView = (TextView) findViewById(R.id.myText);
		inKeyword = (EditText) findViewById(R.id.in_keyword);
		btnSearch = (Button) findViewById(R.id.btn_search);
		btnSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				keyword = inKeyword.getText().toString();
				Toast.makeText(TourListActivity.this, keyword,
						Toast.LENGTH_LONG).show();
				if( !"".equals(keyword) ) {
					//키워드 검색
					StringBuilder sb = new StringBuilder(searchKeywordUrl);
					sb.append("&numOfRows=").append(numOfRows);
					sb.append("&pageNo=").append(pageNo);
					sb.append("&arrange=").append(arrange);
					sb.append("&contentTypeId=").append(contentTypeId);
					sb.append("&keyword=").append(keyword);
					new TourListAsyncTask().execute(sb.toString());
				}
			}
		});

		simpleAdapter = new SimpleAdapter(TourListActivity.this, tourList,
				android.R.layout.simple_expandable_list_item_2, new String[] {
						TAG_TITLE, TAG_ADDR1 }, new int[] { android.R.id.text1,
						android.R.id.text2 });

		ListView listView = (ListView) findViewById(R.id.listView1);
		listView.setAdapter(simpleAdapter);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO: Implement this method
				Map data = tourList.get(position);
				Toast.makeText(TourListActivity.this, "" + data.get(TAG_TITLE),
						Toast.LENGTH_LONG).show();
				Intent intent = new Intent(TourListActivity.this,
						TourDetailActivity.class);
				intent.putExtra(TAG_CONTENT_ID,
						(String) data.get(TAG_CONTENT_ID));
				intent.putExtra(TAG_CONTENT_TYPE_ID,
						(String) data.get(TAG_CONTENT_TYPE_ID));
				intent.putExtra(TAG_TITLE, (String) data.get(TAG_TITLE));
				// startActivity(intent);
			}
		});

		// 네트워크 연결 여부 확인하기
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			StringBuilder sb = new StringBuilder(areaBasedListUrl);
			sb.append("&numOfRows=").append(numOfRows);
			sb.append("&pageNo=").append(pageNo);
			sb.append("&arrange=").append(arrange);
			sb.append("&contentTypeId=").append(contentTypeId);
			new TourListAsyncTask().execute(sb.toString());
		} else {
			// display error
			textView.setText("No network connection available.");
		}
	}

	// Uses AsyncTask to create a task away from the main UI thread. This task
	// takes a
	// URL string and uses it to create an HttpUrlConnection. Once the
	// connection
	// has been established, the AsyncTask downloads the contents of the webpage
	// as
	// an InputStream. Finally, the InputStream is converted into a string,
	// which is
	// displayed in the UI by the AsyncTask's onPostExecute method.
	private class TourListAsyncTask extends AsyncTask<String, Void, Map> {

		@Override
		protected Map doInBackground(String... args) {			
			Log.d(CommonConstants.TAG, "[TourList] " + args[0]);

			Map map = new HashMap();
			try {
				JSONObject result = new JSONParser().getJSONFromUrl(args[0]);
				JSONObject reponse = result.getJSONObject(TAG_RESPONSE);
				JSONObject header = reponse.getJSONObject(TAG_HEADER);
				String resultCode = header.getString(TAG_RESULT_CODE);
				String resultMsg = header.getString(TAG_RESULT_MSG);

				map.put(TAG_RESULT_CODE, resultCode);
				map.put(TAG_RESULT_MSG, resultMsg);

				if ("0000".equals(resultCode)) {
					JSONObject body = reponse.getJSONObject(TAG_BODY);
					JSONObject items = body.getJSONObject(TAG_ITEMS);
					JSONArray item = items.getJSONArray(TAG_ITEM);
					for (int i = 0, l = item.length(); i < l; i++) {
						JSONObject obj = item.getJSONObject(i);

						String contentId = obj.getString(TAG_CONTENT_ID);
						String contentTypeId = obj
								.getString(TAG_CONTENT_TYPE_ID);
						String title = obj.getString(TAG_TITLE);
						String addr1 = obj.getString(TAG_ADDR1);
						// String addr2 = obj.getString(TAG_ADDR2);
						if (obj.has(TAG_ADDR2))
							addr1 += " " + obj.getString(TAG_ADDR2);

						Map data = new HashMap();
						data.put(TAG_CONTENT_ID, contentId);
						data.put(TAG_CONTENT_TYPE_ID, contentTypeId);
						data.put(TAG_TITLE, title);
						data.put(TAG_ADDR1, addr1);
						// if( obj.has(TAG_ADDR2) ) data.put(TAG_ADDR2,
						// obj.getString(TAG_ADDR2));
						tourList.add(data);
					}
				}

			} catch (JSONException e) {
				Log.e(CommonConstants.TAG, "[JSON Parser] Error parsing data "
						+ e.toString());
				map.put(TAG_RESULT_CODE, "9999");
				map.put(TAG_RESULT_MSG, e.getMessage());
			} catch (Exception e) {
				Log.e(CommonConstants.TAG, "[JSON Parser] Error parsing data "
						+ e.toString());
				map.put(TAG_RESULT_CODE, "9999");
				map.put(TAG_RESULT_MSG, e.getMessage());
			}

			return map;
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(Map result) {
			Toast.makeText(
					TourListActivity.this,
					"[" + result.get(TAG_RESULT_CODE) + "] "
							+ result.get(TAG_RESULT_MSG), Toast.LENGTH_LONG)
					.show();
			if ("0000".equals(result.get(TAG_RESULT_CODE)))
				simpleAdapter.notifyDataSetChanged();
		}
	}
}
