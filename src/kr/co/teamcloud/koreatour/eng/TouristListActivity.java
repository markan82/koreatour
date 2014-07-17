package kr.co.teamcloud.koreatour.eng;

import java.util.ArrayList;
import java.util.HashMap;

import kr.co.teamcloud.koreatour.common.CommonConstants;
import kr.co.teamcloud.koreatour.util.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.ViewSwitcher;

public class TouristListActivity extends TourBaseActivity implements OnScrollListener {
	private final String TAG = "TouristListActivity";
	
	private ArrayList<HashMap<String, Object>> tourList = new ArrayList<HashMap<String, Object>>();

	private ViewSwitcher loadingView;
	private TextView textView;
	private EditText inKeyword;
	private Button btnSearch;
	private ListView listView;

	private SimpleAdapter simpleAdapter;

	private boolean isLockListView = true;	//스크롤시 자동으로 데이터를 조회하는지 여부(true:조회하지 않음)
	
	//지역 검색 URL
	private String areaBasedListUrl = CommonConstants.END_POINT_URL
			+ "areaBasedList"
			+ "?MobileOS=AND&MobileApp="+CommonConstants.MOBILE_APP+"&_type=json&listYN=Y&serviceKey="
			+ CommonConstants.SERVICE_KEY;
	
	//키워드 검색 URL
	private String searchKeywordUrl = CommonConstants.END_POINT_URL
			+ "searchKeyword"
			+ "?MobileOS=AND&MobileApp="+CommonConstants.MOBILE_APP+"&_type=json&listYN=Y&serviceKey="
			+ CommonConstants.SERVICE_KEY;

	// 검색 조건
	private int numOfRows = 20;		//한번에 조회할 갯수
	private int pageNo = 1;			//페이지 번호
	private String arrange = "B";	//정렬순서(인기순)
	private int contentTypeId = 76;	//컨텐츠
	private String keyword = "";
	private String areaCode;		//지역코드
	private String sigunguCode;		//시군구 코드
	private String areaName;
	private String sigunguName;

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
				searchList();
			}
		});

		simpleAdapter = new SimpleAdapter(TouristListActivity.this, tourList,
				android.R.layout.simple_expandable_list_item_2, new String[] {
						TAG_TITLE, TAG_ADDR1 }, new int[] { android.R.id.text1,
						android.R.id.text2 });

		listView = (ListView)findViewById(R.id.listView1);
		//푸터를 등록합니다. setAdapter 이전에 해야함
		loadingView = (ViewSwitcher)View.inflate(this, R.layout.loading_progress, null);
		listView.addFooterView(loadingView);
		listView.setAdapter(simpleAdapter);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView.setOnScrollListener(this);
//		listView.setOnItemClickListener(this);
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
			searchList();
		} else {
			// display error
			textView.setText("No network connection available.");
		}
	}
	
	private void searchList() {	
		StringBuilder sb = new StringBuilder();
		if( !"".equals(keyword) ) { //키워드 검색
			sb.append(searchKeywordUrl);
			sb.append("&keyword=").append(keyword);
		} else {
			sb.append(areaBasedListUrl);
		}
		sb.append("&numOfRows=").append(numOfRows);
		sb.append("&pageNo=").append(pageNo);
		sb.append("&arrange=").append(arrange);
		sb.append("&contentTypeId=").append(contentTypeId);
		
//		isLockListView = false;
		new TourListAsyncTask().execute(sb.toString());
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if( isLockListView == false ) {
			//현재 가장 처음 보이는 셀번호와 보여지는 셀번호를 더한값이 전체의 숫자와 동일하면 가장 아래로 스크롤되었다고 판단.
			int count = totalItemCount - visibleItemCount;
			if( totalItemCount!=0 && firstVisibleItem >= count ) {
				pageNo = pageNo + 1;
				isLockListView = true;
				searchList();
			}
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
				map.put(TAG_TOTAL_COUNT, 0);

				if ( "0000".equals(resultCode) ) {
					//TODO: 파일 캐쉬 구현 예정
					
					JSONObject body = reponse.getJSONObject(TAG_BODY);
					
					if( body.getInt(TAG_TOTAL_COUNT) > 0 ) 
					{
						map.put(TAG_TOTAL_COUNT, body.getInt(TAG_TOTAL_COUNT));
						
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
			
			if ("0000".equals(result.get(TAG_RESULT_CODE)) )
			{
				simpleAdapter.notifyDataSetChanged();
				if(pageNo == 1) listView.setSelectionFromTop(0, 0);
				
				//조회한 수와 검색 수가 같아지면 더보기 중단.
				int totalCount = (Integer)result.get(TAG_TOTAL_COUNT);
				if( totalCount <= tourList.size() ) {
					isLockListView = true;	//더보기 불가능
//					loadingView.setVisibility(View.GONE);
					loadingView.showNext();
				} else {
					isLockListView = false;	//더보기 가능
//					loadingView.setVisibility(View.VISIBLE);
					loadingView.showPrevious();
				}
			}
			else
			{
				result.get(TAG_RESULT_MSG);//TODO: 에러 메시지 출력
			}
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {}
}
