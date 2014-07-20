package kr.co.teamcloud.koreatour.kor;

import android.annotation.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.widget.AbsListView.*;
import android.widget.AdapterView.*;
import android.widget.SearchView.*;
import java.io.*;
import java.net.*;
import java.util.*;
import kr.co.teamcloud.koreatour.common.*;
import kr.co.teamcloud.koreatour.util.*;
import org.json.*;

import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;

public class TouristListActivity extends TourBaseActivity implements OnScrollListener, OnClickListener, OnItemClickListener {
	private final String TAG = "TouristListActivity";
	
	private ArrayList<HashMap<String, Object>> tourList = new ArrayList<HashMap<String, Object>>();

	private ViewSwitcher loadingView;
//	private TextView textView;
	private TextView noResultMsg;
	private EditText inKeyword;
	private Button btnSearch;
	private Button btnSearchArea;
	private ListView listView;

	private TouristListAdapter adapter;

	private boolean isLockListView = true;	//스크롤시 자동으로 데이터를 조회하는지 여부(true:조회하지 않음)
	
	private final int REQCODE_AREA_ACTIVITY = 1;
	
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
	private int contentTypeId = 12;	//컨텐츠
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

//		textView = (TextView) findViewById(R.id.myText);
		noResultMsg = (TextView) findViewById(R.id.txtNoResultMsg);
		btnSearchArea = (Button) findViewById(R.id.btnSearchArea);
		btnSearchArea.setOnClickListener(this);
		
		inKeyword = (EditText) findViewById(R.id.inKeyword);
		btnSearch = (Button) findViewById(R.id.btnSearch);
		btnSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				Toast.makeText(TouristListActivity.this, keyword, Toast.LENGTH_LONG).show();
				pageNo = 1; 		//페이지 번호 초기화
				//tourList.clear();	//목록 초기화
				searchList();
			}
		});

		/** 아답타 등록 */
		adapter = new TouristListAdapter(this, tourList);    

		listView = (ListView)findViewById(R.id.listView1);
		//푸터를 등록합니다. setAdapter 이전에 해야함
		loadingView = (ViewSwitcher)View.inflate(this, R.layout.loading_progress, null);
		listView.addFooterView(loadingView);
		listView.setAdapter(adapter);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView.setOnScrollListener(this);
//		listView.setOnItemClickListener(this);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				HashMap<String, Object> data = tourList.get(position);
				Intent intent = new Intent(TouristListActivity.this, TouristDetailActivity.class);
				intent.putExtra(TAG_CONTENT_ID, (String)data.get("contentId"));
				intent.putExtra(TAG_CONTENT_TYPE_ID, (String)data.get("contentTypeId"));
				intent.putExtra(TAG_TITLE, (String)data.get("title"));
				startActivity(intent);
			}
		});

		// 네트워크 연결 여부 확인하기
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			searchList();
		} else {
			// display error
//			textView.setText("No network connection available.");
		}
	}
	
	private void searchList() {	
		keyword = inKeyword.getText().toString();
		
		StringBuilder sb = new StringBuilder();
		if( keyword != null && !"".equals(keyword) ) { //키워드 검색
			sb.append(searchKeywordUrl);
			try {			
				sb.append("&keyword=").append(URLEncoder.encode(keyword, "utf-8"));
			} catch (IOException e) {}
		} else {
			sb.append(areaBasedListUrl);
		}
		sb.append("&numOfRows=").append(numOfRows);
		sb.append("&pageNo=").append(pageNo);
		sb.append("&arrange=").append(arrange);
		sb.append("&contentTypeId=").append(contentTypeId);
		if( areaCode != null && !"".equals(areaCode)) sb.append("&areaCode=").append(areaCode);
	  	if( sigunguCode != null && !"".equals(sigunguCode)) sb.append("&sigunguCode=").append(sigunguCode);
		
		new TourAsyncTask().execute(sb.toString());
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

	private class TourAsyncTask extends AsyncTask<String, Void, HashMap<String, Object>> {
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
						Object item = items.get(TAG_ITEM);
						
						if(pageNo == 1) tourList.clear();
						

			  			if(item instanceof JSONArray) {	//item 이 복수 건수인 경우				
			  				JSONArray array = (JSONArray) item;
			  				for (int i = 0, l = array.length(); i < l; i++) {
								JSONObject obj = array.getJSONObject(i);
								String contentId = obj.getString(TAG_CONTENT_ID);
								String contentTypeId = obj.getString(TAG_CONTENT_TYPE_ID);
								String title = obj.getString(TAG_TITLE);
								String image = null;
								if( obj.has(TAG_FIRSTIMAGE2) ) image = obj.getString(TAG_FIRSTIMAGE2);
								//주소
								String addr = null;
								if ( obj.has(TAG_ADDR1) ) addr = obj.getString(TAG_ADDR1);
								if ( obj.has(TAG_ADDR2) ) addr += " " + obj.getString(TAG_ADDR2);
								HashMap<String, Object> data = new HashMap<String, Object>();
								data.put("contentId", contentId);
								data.put("contentTypeId", contentTypeId);
								data.put("title", title);
								data.put("addr", addr.trim());
								data.put("image", image);
								tourList.add(data);
							}
			  			} else if(item instanceof JSONObject) {	//item 이 단수 건수인 경우				
			  				JSONObject obj = (JSONObject) item;
			  				String contentId = obj.getString(TAG_CONTENT_ID);
							String contentTypeId = obj.getString(TAG_CONTENT_TYPE_ID);
							String title = obj.getString(TAG_TITLE);
							String image = null;
							if( obj.has(TAG_FIRSTIMAGE2) ) image = obj.getString(TAG_FIRSTIMAGE2);
							//주소
							String addr = null;
							if ( obj.has(TAG_ADDR1) ) addr = obj.getString(TAG_ADDR1);
							if ( obj.has(TAG_ADDR2) ) addr += " " + obj.getString(TAG_ADDR2);
							HashMap<String, Object> data = new HashMap<String, Object>();
							data.put("contentId", contentId);
							data.put("contentTypeId", contentTypeId);
							data.put("title", title);
							data.put("addr", addr.trim());
							data.put("image", image);
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
//			Toast.makeText(TouristListActivity.this, "[" + result.get(TAG_RESULT_CODE) + "] " + result.get(TAG_RESULT_MSG), Toast.LENGTH_LONG).show();
			
			if ("0000".equals(result.get(TAG_RESULT_CODE)) )
			{
				int totalCount = (Integer)result.get(TAG_TOTAL_COUNT);
				if( totalCount > 0) {
					if( noResultMsg.getVisibility()==View.VISIBLE )
						noResultMsg.setVisibility(View.GONE);
					
					adapter.notifyDataSetChanged();
					if(pageNo == 1) listView.setSelectionFromTop(0, 0);
					
					//조회한 수와 검색 수가 같아지면 더보기 중단.
					if( totalCount <= tourList.size() ) {
						isLockListView = true;	//더보기 불가능
						loadingView.setVisibility(View.GONE);
						//loadingView.showNext();
					} else {
						isLockListView = false;	//더보기 가능
						loadingView.setVisibility(View.VISIBLE);
						//loadingView.showPrevious();
					}
				} else {
					//검색된 결과가 없음.
					if( noResultMsg.getVisibility()==View.GONE )
						noResultMsg.setVisibility(View.VISIBLE);
//					isLockListView = true;	//더보기 불가능
				}
			}
			else
			{
				//result.get(TAG_RESULT_MSG);//TODO: 에러 메시지 출력
				Toast.makeText(TouristListActivity.this, "[ERROR] " + result.get(TAG_RESULT_MSG), Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		switch (requestCode) {
			case REQCODE_AREA_ACTIVITY: // requestCode가 AREA_ACTIVITY인 케이스
				if (resultCode == RESULT_OK) { // AREA_ACTIVITY에서 넘겨진 resultCode가 OK일때만 실행
					areaCode = intent.getStringExtra("areaCode");
					sigunguCode = intent.getStringExtra("sigunguCode");
					areaName = intent.getStringExtra("areaName");
					sigunguName = intent.getStringExtra("sigunguName");
	
					if (sigunguName != null && !"".equals(sigunguName))
						btnSearchArea.setText(areaName + " > " + sigunguName);
					else
						btnSearchArea.setText(areaName);
	
					pageNo = 1;				//페이지 초기화
					tourList.clear();		//목록 초기화
					searchList();
				}
				break;
		}
	}
		
	@Override
	public void onClick(View view){
		Intent intent = null;
		switch( view.getId() ){
			case R.id.btnSearchArea:
				intent = new Intent(this, AreaListActivity.class);
				startActivityForResult(intent, REQCODE_AREA_ACTIVITY);
			break;
		}			
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.tourist_menu, menu);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			/*
			searchItem = menu.findItem(R.id.action_search);
			searchView = (SearchView) searchItem.getActionView();
			searchView.setQueryHint("물품명 또는 분류");
			searchView.setOnQueryTextListener(queryTextListener);      

			SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
			if(null!=searchManager ) {   
				searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
			}
			searchView.setIconifiedByDefault(true);
			*/

		}
		return true;
	}
	
	private OnQueryTextListener queryTextListener = new OnQueryTextListener() {
		@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
		@Override
		public boolean onQueryTextSubmit(String query) {
			/*
			⁄⁄ TODO Auto-generated method stub
			⁄⁄searchItem.collapseActionView();
			⁄⁄Log.d(TAG,"onQueryTextSubmit");
			Cursor mCount= db.rawQuery("select count(*) from datainfo where GetName like '%" + query+"%' or cate like '%"+ query+"%'", null);
			mCount.moveToFirst();
			count= mCount.getInt(0);
			mCount.close();
			if(count <=0){
				Toast.makeText(LostActivity.con, "검색된 데이타가 없습니다.", Toast.LENGTH_LONG).show();
				return false;
			}
			datainfo = new DataInfo[count];
			Cursor result = db.rawQuery("SELECT * from datainfo where GetName like '%" + query+"%' or cate like '%"+ query+"%' order by GetDate desc", null);
			result.moveToFirst();	
			int i =0;
			while (i<count){
				datainfo[i] = new DataInfo();
				datainfo[i].SetLostId(result.getString(1));
				datainfo[i].SetLostName(result.getString(2));
				datainfo[i].SetLostURL(result.getString(3));
				datainfo[i].SetLostDate(result.getString(5));
				datainfo[i].SetLostTitle(result.getString(4));
				datainfo[i].SetTakePlace(result.getString(6));
				datainfo[i].SetTakeContact(result.getString(7));
				datainfo[i].SetLostPos(result.getString(9));
				datainfo[i].SetLostPlace(result.getString(10));
				datainfo[i].SetLostThing(result.getString(11));
				datainfo[i].SetLostStatus(result.getString(12));
				datainfo[i].SetLostImageUrl(result.getString(14));
				i++;
				result.moveToNext();
			}
			result.close();	
			appendRow(datainfo);
			resizeRow(count);
			⁄⁄Log.d(TAG,"onQueryTextSubmit"+count);
			imm= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
			searchView.setQuery("", false);
			searchView.setIconified(true);
			⁄⁄Toast.makeText(LostActivity.con, "onQueryTextSubmit:["+count+"]", Toast.LENGTH_LONG).show();
			*/
			return false;
		}
		@Override
		public boolean onQueryTextChange(String newText) {
			// TODO Auto-generated method stub
			return false;
		}
	};
}
