package kr.co.teamcloud.koreatour.eng;

import java.util.ArrayList;
import java.util.HashMap;

import kr.co.teamcloud.koreatour.common.CommonConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

public class AreaListActivity extends TourBaseActivity implements OnItemClickListener
{
	private final String TAG = "AreaListActivity";
	private ArrayList<HashMap<String, String>> list;
	private SimpleAdapter simpleAdapter;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_area_list);
		
		list = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("name", getString(R.string.all_area));
		map.put("code", null);
		list.add(map);
		
		simpleAdapter = new SimpleAdapter(AreaListActivity.this, list,
				android.R.layout.simple_list_item_1, 
				new String[] {"name"}, 
				new int[] {android.R.id.text1});
						
		ListView listView = (ListView) findViewById(R.id.listView1);
		listView.setAdapter(simpleAdapter);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView.setOnItemClickListener(this);
			
		AQuery aq = new AQuery(AreaListActivity.this);
		String url = CommonConstants.END_POINT_URL
				+ "areaCode"
				+ "?MobileOS=AND&MobileApp="+CommonConstants.MOBILE_APP+"&_type=json&listYN=Y&serviceKey="
				+ CommonConstants.SERVICE_KEY
				+ "&numOfRows=99&pageNo=1";
		Log.d(TAG, "[url] " + url);
		aq.ajax(url, JSONObject.class, CommonConstants.CACHE_EXPIRE, new AjaxCallback<JSONObject>() {
				@Override
				public void callback(String url, JSONObject json, AjaxStatus status) {   
					int responseCode = status.getCode();
					String message = status.getMessage();
					Log.d(TAG, responseCode +" : "+ message);

					if (json != null) {
						Log.d(TAG, "[receive] " + json.toString());
						// successful ajax call
						try {
							JSONObject response = json.getJSONObject(TAG_RESPONSE);

							// header 데이터
							JSONObject header = response.getJSONObject(TAG_HEADER);
							String resultCode = header.getString(TAG_RESULT_CODE);
							String resultMsg = header.getString(TAG_RESULT_MSG);
							if( !"0000".equals(resultCode) ) {
								Toast.makeText(AreaListActivity.this, "tour api call error : " + resultMsg, Toast.LENGTH_SHORT).show();
								status.invalidate();
								return;
							}

							// 결과 코드가 정상 코드인 경우에만 body 데이터 파싱
							JSONObject body = response.getJSONObject(TAG_BODY);
							int totalCount = body.getInt(TAG_TOTAL_COUNT);	//전체 결과 수
							if( totalCount > 0 ) {	
								JSONObject items = body.getJSONObject(TAG_ITEMS);
								Object item = items.get(TAG_ITEM);
								if(item instanceof JSONArray) {
									JSONArray itemArray = (JSONArray) item;
									for(int i=0, s=itemArray.length(); i<s; i++) {
										JSONObject itemObject = itemArray.getJSONObject(i);	
										HashMap<String, String> map = new HashMap<String, String>();
										map.put("name", itemObject.getString("name"));
										map.put("code", itemObject.getString("code"));
										list.add(map);
									}	
								} 
								simpleAdapter.notifyDataSetChanged();
							}
						} catch (JSONException e) {
							Toast.makeText(AreaListActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
							status.invalidate();
						} catch (Exception e) {
							Toast.makeText(AreaListActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
							status.invalidate();
						}
					} else {
						// ajax error
						Toast.makeText(AreaListActivity.this, message, Toast.LENGTH_LONG).show();
						
						 //we believe the request is a failure, don't cache it
                        status.invalidate();
					}
				}
			});		
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		HashMap<String, String> data = list.get(position);
		String code = data.get("code");
		String name = data.get("name");
		
		if( code==null || "8".equals(code)) {// 세종 자치시:8
			Intent intent = new Intent();
			intent.putExtra("areaName", name);
			intent.putExtra("areaCode", code);
			this.setResult(RESULT_OK, intent); // 성공했다는 결과값을 보내면서 데이터 꾸러미를 지고 있는 intent를 함께 전달한다.
		} else {
			//시군구 선택 화면으로 이동
			Intent intent = new Intent(this, SigungoListActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
			intent.putExtra("areaName", name);
			intent.putExtra("areaCode", code);
			startActivity(intent);
		}
		
		finish();
	}
}
