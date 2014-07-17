package kr.co.teamcloud.koreatour.eng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class TouristListActivity extends TourBaseActivity {
	private final String TAG = "TouristListActivity";
	
	private ArrayList<HashMap<String, Object>> tourList = new ArrayList<HashMap<String, Object>>();

	private TextView textView;
	private EditText inKeyword;
	private Button btnSearch;

	private SimpleAdapter simpleAdapter;

	//지역 검색 URL
	private String areaBasedListUrl = CommonConstants.END_POINT_URL
			+ "areaBasedList"
			+ "?MobileOS=AND&MobileApp=koreaTour&_type=json&listYN=Y&serviceKey="
			+ CommonConstants.SERVICE_KEY;
	
	//키워드 검색 URL
	private String searchKeywordUrl = CommonConstants.END_POINT_URL
			+ "searchKeyword"
			+ "?MobileOS=AND&MobileApp=koreaTour&_type=json&listYN=Y&serviceKey="
			+ CommonConstants.SERVICE_KEY;

	// 검색 조건
	private int numOfRows = 20;		//한번에 조회할 갯수
	private int pageNo = 1;			//페이지 번호
	private String arrange = "B";	//정렬순서(인기순)
	private int contentTypeId = 76;	//컨텐츠
	private String keyword = "";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tourlist_list);

		textView = (TextView) findViewById(R.id.myText);
		inKeyword = (EditText) findViewById(R.id.in_keyword);
		btnSearch = (Button) findViewById(R.id.btn_search);
		btnSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				keyword = inKeyword.getText().toString();
				Toast.makeText(TouristListActivity.this, keyword, Toast.LENGTH_LONG).show();
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

		simpleAdapter = new SimpleAdapter(TouristListActivity.this, tourList,
				android.R.layout.simple_expandable_list_item_2, new String[] {
						TAG_TITLE, TAG_ADDR1 }, new int[] { android.R.id.text1,
						android.R.id.text2 });

		ListView listView = (ListView)findViewById(R.id.listView1);
		listView.setAdapter(simpleAdapter);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				HashMap<String, Object> data = tourList.get(position);
				Toast.makeText(TouristListActivity.this, "" + data.get(TAG_TITLE), Toast.LENGTH_LONG).show();
				Intent intent = new Intent(TouristListActivity.this, TouristDetailActivity.class);
				intent.putExtra(TAG_CONTENT_ID, (String)data.get(TAG_CONTENT_ID));
				intent.putExtra(TAG_CONTENT_TYPE_ID, (String)data.get(TAG_CONTENT_TYPE_ID));
				intent.putExtra(TAG_TITLE, (String)data.get(TAG_TITLE));
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

	private class TourListAsyncTask extends AsyncTask<String, Void, HashMap<String, Object>> {
		@Override
		protected HashMap<String, Object> doInBackground(String... args) {			
			Log.d(TAG, "[url] " + args[0]);

			HashMap<String, Object> map = new HashMap<String, Object>();
			try {
				JSONObject result = new JSONParser().getJSONFromUrl(args[0]);
				JSONObject reponse = result.getJSONObject(TAG_RESPONSE);
				JSONObject header = reponse.getJSONObject(TAG_HEADER);
				String resultCode = header.getString(TAG_RESULT_CODE);
				String resultMsg = header.getString(TAG_RESULT_MSG);

				map.put(TAG_RESULT_CODE, resultCode);
				map.put(TAG_RESULT_MSG, resultMsg);

				if ( "0000".equals(resultCode) ) {
					//TODO: 파일 캐쉬 구현 예정
					
					JSONObject body = reponse.getJSONObject(TAG_BODY);
					
					if( body.getInt(TAG_TOTAL_COUNT) > 0 ) 
					{
						JSONObject items = body.getJSONObject(TAG_ITEMS);
						JSONArray item = items.getJSONArray(TAG_ITEM);
						for (int i = 0, l = item.length(); i < l; i++) {
							JSONObject obj = item.getJSONObject(i);
	
							String contentId = obj.getString(TAG_CONTENT_ID);
							String contentTypeId = obj.getString(TAG_CONTENT_TYPE_ID);
							String title = obj.getString(TAG_TITLE);
							
							//주소
							String addr = obj.getString(TAG_ADDR1);
							if ( obj.has(TAG_ADDR2) && !"".equals(obj.getString(TAG_ADDR2)) )
								addr += " " + obj.getString(TAG_ADDR2);
	
							HashMap<String, Object> data = new HashMap<String, Object>();
							data.put(TAG_CONTENT_ID, contentId);
							data.put(TAG_CONTENT_TYPE_ID, contentTypeId);
							data.put(TAG_TITLE, title);
							data.put(TAG_ADDR1, addr);
							tourList.add(data);
						}
					}
				}

			} catch (JSONException e) {
				Log.e(TAG, "[JSON Parser] " + e.toString());
				map.put(TAG_RESULT_CODE, "9999");
				map.put(TAG_RESULT_MSG, e.getMessage());
			} catch (Exception e) {
				Log.e(TAG, "[Exception] " + e.toString());
				map.put(TAG_RESULT_CODE, "9999");
				map.put(TAG_RESULT_MSG, e.getMessage());
			}

			return map;
		}

		@Override
		protected void onPostExecute(HashMap<String, Object> result) {
			Toast.makeText(TouristListActivity.this, "[" + result.get(TAG_RESULT_CODE) + "] " + result.get(TAG_RESULT_MSG), Toast.LENGTH_LONG).show();
			if ("0000".equals(result.get(TAG_RESULT_CODE)))
				simpleAdapter.notifyDataSetChanged();
			else
				;//TODO: 에러 메시지 출력
		}
	}
}
