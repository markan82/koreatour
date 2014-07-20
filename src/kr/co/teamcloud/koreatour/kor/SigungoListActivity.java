package kr.co.teamcloud.koreatour.kor;

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

public class SigungoListActivity extends TourBaseActivity implements OnItemClickListener
{
	private final String TAG = "SigungoListActivity";
	private ArrayList<HashMap<String, String>> list;
	private SimpleAdapter simpleAdapter;	
	
	private String areaCode;
	private String areaName;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_area_list);
		
		areaCode = getIntent().getStringExtra("areaCode");
		areaName = getIntent().getStringExtra("areaName");
		
		list = new ArrayList<HashMap<String, String>>();
		
		simpleAdapter = new SimpleAdapter(SigungoListActivity.this, list,
				android.R.layout.simple_list_item_1, 
				new String[] {"name"}, 
				new int[] {android.R.id.text1});
						
		final ListView listView = (ListView) findViewById(R.id.listView1);
		listView.setAdapter(simpleAdapter);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView.setOnItemClickListener(this);
			
		AQuery aq = new AQuery(SigungoListActivity.this);
		String url = CommonConstants.END_POINT_URL
				+ "areaCode"
				+ "?MobileOS=AND&MobileApp="+CommonConstants.MOBILE_APP+"&_type=json&listYN=Y&serviceKey="
				+ CommonConstants.SERVICE_KEY
				+ "&numOfRows=99&pageNo=1";
		if(areaCode!=null && !"".equals(areaCode)) url += "&areaCode=" + areaCode;
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
							if( !resultCode.equals("0000") ) {
								Toast.makeText(SigungoListActivity.this, "tour api call error : " + resultMsg, Toast.LENGTH_SHORT).show();
								status.invalidate();
								return;
							}

							// 결과 코드가 정상 코드인 경우에만 body 데이터 파싱
							// body 데이터
							JSONObject body = response.getJSONObject(TAG_BODY);
							int totalCount = body.getInt(TAG_TOTAL_COUNT);	//전체 결과 수
							if( totalCount > 0 ) {	
								JSONObject items = body.getJSONObject(TAG_ITEMS);
								Object item = items.get(TAG_ITEM);

								if(item instanceof JSONArray) {
									HashMap<String, String> map = new HashMap<String, String>();
									map.put("name", areaName + " " + getString(R.string.all));
									map.put("code", null);
									list.add(map);
									JSONArray itemArray = (JSONArray) item;
									for(int i=0, s=itemArray.length(); i<s; i++) {
										JSONObject itemObject = itemArray.getJSONObject(i);	
										if( itemObject.has("name") ) {
										map = new HashMap<String, String>();
										 map.put("name", itemObject.getString("name"));
										 map.put("code", itemObject.getString("code"));
										 list.add(map);
										}
									}	
								} else if(item instanceof JSONObject) {
									JSONObject itemObject = (JSONObject) item;
									if( itemObject.has("name") ) {
										HashMap<String, String> map = new HashMap<String, String>();
										map.put("name", itemObject.getString("name"));
										map.put("code", itemObject.getString("code"));
										list.clear();
										list.add(map);
									}
								}
								
								if( list.size()==1 ) {
									onItemClick(null, null, 0, 0);
								}
								
								simpleAdapter.notifyDataSetChanged();
							}
						} catch (JSONException e) {
							Toast.makeText(SigungoListActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
							status.invalidate();
						} catch (Exception e) {
							Toast.makeText(SigungoListActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
							status.invalidate();
						}
					} else {
						// ajax error
						Toast.makeText(SigungoListActivity.this, message, Toast.LENGTH_LONG).show();
						status.invalidate();
					}
				}
			});		
	}
	
	
	@Override
	public void onBackPressed() {
		//다시 지역 선택 리스트로 이동
		Intent intent = new Intent(SigungoListActivity.this, AreaListActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
		startActivity(intent);
		SigungoListActivity.this.finish();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		//상세 보기 화면으로 이동
		HashMap<String, String> data = list.get(position);
		String code = data.get("code");
		String name = data.get("name");
		
		Bundle extra = new Bundle();
		if( code == null ) {
			//해당 지역 전체를 선택한경우
			extra.putString("areaName", name);
			extra.putString("areaCode", areaCode);
			extra.putString("sigunguName", "");
			extra.putString("sigunguCode", "");
		} else {
			extra.putString("areaName", areaName);
			extra.putString("areaCode", areaCode);
			extra.putString("sigunguName", name);
			extra.putString("sigunguCode", code);
		}
		
		Intent intent = new Intent();
		intent.putExtras(extra);
		this.setResult(RESULT_OK, intent); // 성공했다는 결과값을 보내면서 데이터 꾸러미를 지고 있는 intent를 함께 전달한다.
		finish();
	}
}
