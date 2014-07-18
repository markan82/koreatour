package kr.co.teamcloud.koreatour.eng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kr.co.teamcloud.koreatour.common.CommonConstants;
import kr.co.teamcloud.koreatour.model.DetailImage;
import kr.co.teamcloud.koreatour.model.DetailInfo;
import kr.co.teamcloud.koreatour.util.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class TouristDetailActivity extends TourBaseActivity
{
	private final String TAG = "TouristDetailActivity";
	
	private TextView txtOverview;
	private ProgressDialog dialog;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_detail);
		
        txtOverview = (TextView) findViewById(R.id.txtOverview);
		
		String contentTypeId = getIntent().getStringExtra(TAG_CONTENT_TYPE_ID);
		String contentId = getIntent().getStringExtra(TAG_CONTENT_ID);
		String title = getIntent().getStringExtra(TAG_TITLE);
		setTitle(title);
		
		new TourAsyncTask().execute(contentTypeId, contentId);
    }
	
    private class TourAsyncTask extends AsyncTask<String, Integer, HashMap<String, Object>> {
    	@Override
    	protected void onPreExecute() {
    	    dialog = new ProgressDialog(TouristDetailActivity.this);
//    	    dialog.setTitle("이미지 다운로드중");
    	    dialog.setMessage("잠시만 기다리세요...");
    	    dialog.setIndeterminate(true);
    	    dialog.setCancelable(true);
    	    dialog.show();
    	    super.onPreExecute();
    	}  
    	
		@Override
		protected HashMap<String, Object> doInBackground(String... args) {
			String contentTypeId = args[0];
			String contentId = args[1];
			Log.d(TAG, "[param] contentTypeId: " + contentTypeId + ", contentId: " + contentId);

			String	createdtime	=null;
			String	homepage	=null;
			String	modifiedtime	=null;
			String	tel	=null;
			String	telname	=null;
			String	firstimage	=null;
			String	firstimage2	=null;
			String	areacode	=null;
			String	sigungucode	=null;
			String	cat1	=null;
			String	cat2	=null;
			String	cat3	=null;
			String	addr1	=null;
			String	addr2	=null;
			String	zipcode	=null;
			String	mapx	=null;
			String	mapy	=null;
			String	mlevel	=null;
			String	overview	=null;
			String	directions	=null;
			
			//이미지
			ArrayList<DetailImage> imageList = new ArrayList<DetailImage>();
			
			//소개정보
			String	accomcount	=null;	//	수용인원
//			String	chkbabycarriage	=null;	//	유모차 대여 여부(국문만 제공)
//			String	chkcreditcard	=null;	//	신용카드 가능 여부(국문만 제공)
//			String	chkpet	=null;	//	애완동물 가능 여부(국문만 제공)
			String	expagerange	=null;	//	체험가능 연령
			String	expguide	=null;	//	체험안내
			String	heritage1	=null;	//	세계 문화 유산
//			String	heritage2	=null;	//	세계 자연유산 유무(국문만 제공)
//			String	heritage3	=null;	//	세계 기록유산 유무(국문만 제공)
			String	infocenter	=null;	//	문의 및 안내
			String	opendate	=null;	//	개장일
			String	parking	=null;	//	주차시설
			String	restdate	=null;	//	쉬는날
			String	useseason	=null;	//	이용시기
			String	usetime	=null;	//	이용시간

			//반복정보
			ArrayList<DetailInfo> infoList = new ArrayList<DetailInfo>();
			
			String url = "";
			// 1. 공통정보 조회
			url = CommonConstants.END_POINT_URL 
					+ "detailCommon"
					+ "?MobileOS=AND&MobileApp="+CommonConstants.MOBILE_APP+"&_type=json&listYN=Y&serviceKey=" + CommonConstants.SERVICE_KEY
					+ "&defaultYN=Y&firstImageYN=Y&areacodeYN=Y&catcodeYN=Y&addrinfoYN=Y&mapinfoYN=Y&overviewYN=Y&transGuideYN=Y"
					+ "&contentId=" + contentId
					+ "&contentTypeId=" + contentTypeId;
			try {
				JSONObject result = new JSONParser().getJSONFromUrl(url);
				JSONObject reponse = result.getJSONObject(TAG_RESPONSE);
				JSONObject header = reponse.getJSONObject(TAG_HEADER);
				String resultCode = header.getString(TAG_RESULT_CODE);
				String resultMsg = header.getString(TAG_RESULT_MSG);
				if ( "0000".equals(resultCode) ) {
					JSONObject body = reponse.getJSONObject(TAG_BODY);
					if( body.getInt(TAG_TOTAL_COUNT) > 0 ) {
						JSONObject items = body.getJSONObject(TAG_ITEMS);
						Object item = items.get(TAG_ITEM);
						if(item instanceof JSONObject) {	//item 이 단수 건수인 경우				
			  				JSONObject obj = (JSONObject) item;
			  				createdtime	=(String)	obj.get(TAG_CREATEDTIME);	// 등록일
							homepage = (String) obj.get(TAG_HOMEPAGE); // 홈페이지 주소
							modifiedtime = (String) obj.get(TAG_MODIFIEDTIME); // 수정일
							tel = (String) obj.get(TAG_TEL); // 전화번호
							telname = (String) obj.get(TAG_TELNAME); // 전화번호명
							firstimage = (String) obj.get(TAG_FIRSTIMAGE); // 대표이미지(원본)
							firstimage2 = (String) obj.get(TAG_FIRSTIMAGE2); // 대표이미지(썸네일)
							areacode = (String) obj.get(TAG_AREACODE); // 지역코드
							sigungucode = (String) obj.get(TAG_SIGUNGUCODE); // 시군구코드
							cat1 = (String) obj.get(TAG_CAT1); // 대분류
							cat2 = (String) obj.get(TAG_CAT2); // 중분류
							cat3 = (String) obj.get(TAG_CAT3); // 소분류
							addr1 = (String) obj.get(TAG_ADDR1); // 주소
							addr2 = (String) obj.get(TAG_ADDR2); // 상세주소
							zipcode = (String) obj.get(TAG_ZIPCODE); // 우편번호
							mapx = (String) obj.get(TAG_MAPX); // GPS X좌표
							mapy = (String) obj.get(TAG_MAPY); // GPS Y좌표
							mlevel = (String) obj.get(TAG_MLEVEL); // Map Level
							overview = (String) obj.get(TAG_OVERVIEW); // 개요
							directions = (String) obj.get(TAG_DIRECTIONS); // 길안내
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Log.w(TAG, url, e);
			}
			
			//이미지 목록에 대표이미지 추가함.
			if( firstimage!=null && !"".equals(firstimage) ) {
				DetailImage detailImage = new DetailImage();
				detailImage.originimgurl = firstimage;
				detailImage.smallimageurl = firstimage2;
				imageList.add(detailImage);
			}
				
			// 2. 이미지정보 조회
			url = CommonConstants.END_POINT_URL 
					+ "detailImage"
					+ "?MobileOS=AND&MobileApp="+CommonConstants.MOBILE_APP+"&_type=json&listYN=Y&serviceKey=" + CommonConstants.SERVICE_KEY
					+ "&imageYN=Y"	// 음식사진의 경우에는 N 으로 조회
					+ "&contentId=" + contentId
					+ "&contentTypeId=" + contentTypeId;
			try {
				JSONObject result = new JSONParser().getJSONFromUrl(url);
				JSONObject reponse = result.getJSONObject(TAG_RESPONSE);
				JSONObject header = reponse.getJSONObject(TAG_HEADER);
				String resultCode = header.getString(TAG_RESULT_CODE);
				String resultMsg = header.getString(TAG_RESULT_MSG);
				if ( "0000".equals(resultCode) ) {
					JSONObject body = reponse.getJSONObject(TAG_BODY);
					if( body.getInt(TAG_TOTAL_COUNT) > 0 ) {
						JSONObject items = body.getJSONObject(TAG_ITEMS);
						Object item = items.get(TAG_ITEM);
						if(item instanceof JSONObject) {	//item 이 단수 건수인 경우				
			  				JSONObject obj = (JSONObject) item;
			  				DetailImage detailImage = new DetailImage();
			  				detailImage.imagename = (String)obj.get(TAG_IMAGENAME); // 이미지명
			  				detailImage.originimgurl = (String) obj.get(TAG_ORIGINIMGURL); // 원본
			  				detailImage.serialnum = (String) obj.get(TAG_SERIALNUM); // 이미지
			  				detailImage.smallimageurl = (String) obj.get(TAG_SMALLIMAGEURL); // 썸네일
							imageList.add(detailImage);
						} else if(item instanceof JSONArray) {
							JSONArray arr = (JSONArray) item;
							for(int i=0, s=arr.length(); i<s; i++) {
								JSONObject obj = (JSONObject) arr.get(i);
								DetailImage detailImage = new DetailImage();
				  				detailImage.imagename = (String)obj.get(TAG_IMAGENAME); // 이미지명
				  				detailImage.originimgurl = (String) obj.get(TAG_ORIGINIMGURL); // 원본
				  				detailImage.serialnum = (String) obj.get(TAG_SERIALNUM); // 이미지
				  				detailImage.smallimageurl = (String) obj.get(TAG_SMALLIMAGEURL); // 썸네일
								imageList.add(detailImage);
							}
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Log.w(TAG, url, e);
			}
			
			// 3. 소개정보 조회
			url = CommonConstants.END_POINT_URL 
					+ "detailIntro"
					+ "?MobileOS=AND&MobileApp="+CommonConstants.MOBILE_APP+"&_type=json&listYN=Y&serviceKey=" + CommonConstants.SERVICE_KEY
					+ "&introYN=Y"	
					+ "&contentId=" + contentId
					+ "&contentTypeId=" + contentTypeId;
			try {
				JSONObject result = new JSONParser().getJSONFromUrl(url);
				JSONObject reponse = result.getJSONObject(TAG_RESPONSE);
				JSONObject header = reponse.getJSONObject(TAG_HEADER);
				String resultCode = header.getString(TAG_RESULT_CODE);
				String resultMsg = header.getString(TAG_RESULT_MSG);
				if ( "0000".equals(resultCode) ) {
					JSONObject body = reponse.getJSONObject(TAG_BODY);
					if( body.getInt(TAG_TOTAL_COUNT) > 0 ) {
						JSONObject items = body.getJSONObject(TAG_ITEMS);
						Object item = items.get(TAG_ITEM);
						if(item instanceof JSONObject) {	//item 이 단수 건수인 경우				
			  				JSONObject obj = (JSONObject) item;
			  				accomcount	=(String)	obj.get(TAG_ACCOMCOUNT);	//	수용인원
//			  				chkbabycarriage	=(String)	obj.get(TAG_CHKBABYCARRIAGE);	//	유모차
//			  				chkcreditcard	=(String)	obj.get(TAG_CHKCREDITCARD);	//	신용카드
//			  				chkpet	=(String)	obj.get(TAG_CHKPET);	//	애완동물
			  				expagerange	=(String)	obj.get(TAG_EXPAGERANGE);	//	체험가능
			  				expguide	=(String)	obj.get(TAG_EXPGUIDE);	//	체험안내
			  				heritage1	=(String)	obj.get(TAG_HERITAGE1);	//	세계
//			  				heritage2	=(String)	obj.get(TAG_HERITAGE2);	//	세계
//			  				heritage3	=(String)	obj.get(TAG_HERITAGE3);	//	세계
			  				infocenter	=(String)	obj.get(TAG_INFOCENTER);	//	문의
			  				opendate	=(String)	obj.get(TAG_OPENDATE);	//	개장일
			  				parking	=(String)	obj.get(TAG_PARKING);	//	주차시설
			  				restdate	=(String)	obj.get(TAG_RESTDATE);	//	쉬는날
			  				useseason	=(String)	obj.get(TAG_USESEASON);	//	이용시기
			  				usetime	=(String)	obj.get(TAG_USETIME);	//	이용시간
						} 
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Log.w(TAG, url, e);
			}
			
			
			// 4. 반복정보 조회
			url = CommonConstants.END_POINT_URL 
					+ "detailInfo"
					+ "?MobileOS=AND&MobileApp="+CommonConstants.MOBILE_APP+"&_type=json&listYN=Y&serviceKey=" + CommonConstants.SERVICE_KEY
					+ "&detailYN=Y"	
					+ "&contentId=" + contentId
					+ "&contentTypeId=" + contentTypeId;
			try {
				JSONObject result = new JSONParser().getJSONFromUrl(url);
				JSONObject reponse = result.getJSONObject(TAG_RESPONSE);
				JSONObject header = reponse.getJSONObject(TAG_HEADER);
				String resultCode = header.getString(TAG_RESULT_CODE);
				String resultMsg = header.getString(TAG_RESULT_MSG);
				if ( "0000".equals(resultCode) ) {
					JSONObject body = reponse.getJSONObject(TAG_BODY);
					if( body.getInt(TAG_TOTAL_COUNT) > 0 ) {
						JSONObject items = body.getJSONObject(TAG_ITEMS);
						Object item = items.get(TAG_ITEM);
						if(item instanceof JSONObject) {	//item 이 단수 건수인 경우				
			  				JSONObject obj = (JSONObject) item;
			  				DetailInfo detailInfo = new DetailInfo();
			  				detailInfo.fldgubun	=(String)	obj.get(TAG_FLDGUBUN);	//	일련번호
			  				detailInfo.infoname	=(String)	obj.get(TAG_INFONAME);	//	제목
			  				detailInfo.infotext	=(String)	obj.get(TAG_INFOTEXT);	//	내용
			  				detailInfo.serialnum	=(String)	obj.get(TAG_SERIALNUM);	//	반복
			  				infoList.add(detailInfo);
						} else if(item instanceof JSONArray) {
							JSONArray arr = (JSONArray) item;
							for(int i=0, s=arr.length(); i<s; i++) {
								JSONObject obj = (JSONObject) arr.get(i);
								DetailInfo detailInfo = new DetailInfo();
				  				detailInfo.fldgubun	=(String)	obj.get(TAG_FLDGUBUN);	//	일련번호
				  				detailInfo.infoname	=(String)	obj.get(TAG_INFONAME);	//	제목
				  				detailInfo.infotext	=(String)	obj.get(TAG_INFOTEXT);	//	내용
				  				detailInfo.serialnum	=(String)	obj.get(TAG_SERIALNUM);	//	반복
				  				infoList.add(detailInfo);
							}
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Log.w(TAG, url, e);
			}
//			유형 1 : 등산로, 자연휴식년제 구역, 입산통제 구간, 개방 구간, 자연휴식년제 기간, 입산통제 기간
//			유형 2 : 관광코스안내, 촬영장소, 상점정보
//			유형 3 : 입장료, 관람료, 시설이용료,  주차요금, 이용가능시설, 장애인 편의시설
//			유형 4 : 한국어 안내서비스, 외국어 안내서비스, 내국인 예약안내, 외국인 예약안내 
			
			// 공통정보에 전화번호가 없는 경우 문의안내에서 전화번호 내용 가져오기
			if( tel == null || tel.trim().isEmpty() ) {
				tel = infocenter;	
			}
			
			//전화번호에서 번호만 파싱
			String telNum = null;
			if( tel != null && !"".equals(tel.trim())) {
				tel = tel.split("[,|\n|<br>]")[0];			// 구분자 또는 개행 문자열 처리
				tel = tel.replaceAll("\\<.*?>","").trim();	// HTML 태그 제거
				Pattern pattern = Pattern.compile("([0-9|-]+)");			// 전화번호(숫자) 만 추출
				Matcher matcher = pattern.matcher(tel);
				if( matcher.find() ) {
					telNum = matcher.group(0);
				}
				Log.d(TAG, "[telNum] " + telNum);			
			}
			
			//홈페이지 내용에서 링크 주소만 추출
			String homepageUrl = null;
			String homepageText = null;
			if( homepage != null && !"".equals(homepage.trim())) {
				Pattern pattern = Pattern.compile("<a[^>]*href=[\"']?([^\"']+)[\"']?[^>]*>");
				Matcher matcher = pattern.matcher(homepage);
				if( matcher.find() ) {
					homepageUrl = matcher.group(1);
					if( !homepageUrl.startsWith("http") ) 
						homepageUrl = "http://" + homepageUrl;
					homepageText = homepageUrl.replaceAll("https?://", "");
					if(homepageText.length()==(homepageText.lastIndexOf("/")+1)) {
						homepageText = homepageText.substring(0, homepageText.length()-1);
					}
				}
				Log.d(TAG, "[homepageUrl] " + homepageUrl);		
			}
			
			/*if(overview!=null && !overview.isEmpty() && overview.indexOf("br")<0) {
				overview = overview.replaceAll("\n", "<br>");
			}*/

			HashMap<String, Object> result = new HashMap<String, Object>();
			result.put("telNum", telNum);
			result.put("homepageUrl", homepageUrl);
			result.put("imageList",imageList);
			result.put("infoList",infoList);
			result.put(TAG_HOMEPAGE, homepage);
			result.put(TAG_TEL, tel);
			result.put(TAG_TELNAME, telname);
			result.put(TAG_AREACODE, areacode);
			result.put(TAG_SIGUNGUCODE, sigungucode);
			result.put(TAG_CAT1, cat1);
			result.put(TAG_CAT2, cat2);
			result.put(TAG_CAT3, cat3);
			result.put(TAG_ADDR1,addr1);
			result.put(TAG_ADDR2,addr2);
			result.put(TAG_ZIPCODE,zipcode);
			result.put(TAG_MAPX,mapx);
			result.put(TAG_MAPY,mapy);
			result.put(TAG_MLEVEL,mlevel);
			result.put(TAG_OVERVIEW,overview);
			result.put(TAG_DIRECTIONS,directions);
			result.put(TAG_ACCOMCOUNT,accomcount);
			result.put(TAG_EXPAGERANGE,expagerange);
			result.put(TAG_EXPGUIDE,expguide);
			result.put(TAG_HERITAGE1,heritage1);
			result.put(TAG_INFOCENTER,infocenter);
			result.put(TAG_OPENDATE,opendate);
			result.put(TAG_PARKING,parking);
			result.put(TAG_RESTDATE,restdate);
			result.put(TAG_USESEASON,useseason);
			result.put(TAG_USETIME,usetime);
			return result;
		}
		
		@Override
		protected void onProgressUpdate(Integer... progress) {
		    Log.d("LOST","onProgressUpdate:["+progress[0]+"]");
		    dialog.setProgress(progress[0]);
		}

		@Override
		protected void onPostExecute(HashMap<String, Object> result) {
			txtOverview.setText((String)result.get(TAG_OVERVIEW));
			dialog.hide();
		}
	}
}
