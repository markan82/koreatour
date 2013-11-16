package kr.co.teamcloud.koreatour.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.teamcloud.koreatour.common.PaginationVo;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import android.util.*;

public class NaverSearchService {
	
	private final String REQUEST_URL = "http://openapi.naver.com/search";
	private final String NAVER_KEY = "2f8a1bcbadc2d964f11089d7cff8238c";
	
	private final String TAG_LAST_BUILD_DATE = "lastBuildDate";	//검색 결과를 생성한 시간입니다.
	private final String TAG_TOTAL = "total";	//	integer	검색 결과 문서의 총 개수를 의미합니다.
	private final String TAG_START = "start";	//	integer	검색 결과 문서 중, 문서의 시작점을 의미합니다.
	private final String TAG_DISPLAY = "display";	//	integer	검색된 검색 결과의 개수입니다.
	private final String TAG_ITEM = "item";	//	-	개별 검색 결과이며, title, link, description, bloggername, bloggerlink을 포함합니다.
	private final String TAG_TITLE = "title";	//	string	검색 결과 문서의 제목을 나타냅니다. 제목에서 검색어와 일치하는 부분은 <b> 태그로 감싸져 있습니다.
	private final String TAG_LINK = "link";	//	string	검색 결과 문서의 하이퍼텍스트 link를 나타냅니다.
	private final String TAG_DESCRIPTION = "description";	//	string	검색 결과 문서의 내용을 요약한 패시지 정보입니다. 문서 전체의 내용은 link 를 따라가면, 읽을 수 있습니다. 패시지에서 검색어와 일치하는 부분은 <b> 태그로 감싸져 있습니다.
	private final String TAG_BLOGGERNAME = "bloggername";	//	string	검색 결과 블로그 포스트를 작성한 블로거의 이름입니다.
	private final String TAG_BLOGGERLINK = "bloggerlink";	//	string	검색 결과 블로그 포스트를 작성한 블로거의 하이퍼텍스트 link입니다.
	
	/**
	 * 블로그 검색
	 * 블로그 검색결과를 이용하시면 여러분의 웹 사이트에서 손쉽게 네이버의 블로그 검색결과를 이용하실 수 있습니다.
	 * @param query	검색을 원하는 질의, UTF-8 인코딩 입니다.
	 * @param display	검색결과 출력건수를 지정합니다. 최대 100까지 가능합니다.
	 * @param start	검색의 시작위치를 지정할 수 있습니다. 최대 1000까지 가능합니다.
	 * @param sort	정렬 옵션입니다. sim : 유사도순 (기본값), date : 날짜순
	 */
	public Map searchBlog(String query, int page, int display, String sort) {
		
		// 페이지 계산
		PaginationVo pageVo = new PaginationVo();
		pageVo.setCurrentPageNo(page);
		pageVo.setRecordCountPerPage(display);
		pageVo.setPageSize(10);
		
		// 1. api 요청 url 생성
		StringBuilder url = new StringBuilder(REQUEST_URL);
		url.append("?key=").append(NAVER_KEY).append("&target=blog");
		try {
			url.append("&query=").append(URLEncoder.encode(query, "UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		url.append("&display=").append(display);
		url.append("&start=").append(pageVo.getStartRowNum());
		
		if( sort.isEmpty() ) sort = "sim";
		url.append("&sort=").append(sort);
		
		Log.d("Naver", url.toString());
		
		// 2. 리턴해줄 객체 생성
		Map result = new HashMap();			//리턴해 줄 맵
		List itemList = new ArrayList();	//블로그 리스트
		
		//3. api 요청 시작
		BufferedReader br = null;
		InputStreamReader isr = null;
		InputStream is = null;
		HttpURLConnection connection = null;
		try{
        	URL u = new URL( url.toString() );
        	connection = (HttpURLConnection) u.openConnection();
			connection.setConnectTimeout(1000);	//1초
			connection.setDoInput(true);
			connection.setUseCaches(false);
			connection.setDefaultUseCaches(false);
			
			Log.d("Naver", "call url: " + connection.getURL());
			Log.d("Naver", "responseCode: " + connection.getResponseCode() + " " + connection.getResponseMessage());
        	
			int ResponseCode = connection.getResponseCode(); 
			if( ResponseCode == HttpURLConnection.HTTP_OK ) {
				
				is = connection.getInputStream();
				isr = new InputStreamReader(is, "utf-8");
				br = new BufferedReader(isr);
				
				// 4. XML 파싱 시작
	        	XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
	        	XmlPullParser parser = parserCreator.newPullParser();
	        	parser.setInput(br);
	        	
	        	int parserEvent = parser.getEventType();
	               
				String tag = null;
				boolean isLastBuildDate = false,
						isTotal = false,
						isStart = false,
						isDisplay = false,
						isItem = false,
						isTitle = false,
						isLink = false,
						isDescription = false,
						isBlogername = false,
						isBloggerlink = false;
				
				Map item = null;
				while (parserEvent != XmlPullParser.END_DOCUMENT) {
					switch (parserEvent) {
						case XmlPullParser.START_DOCUMENT: 	// 문서의 시작
//							log.info("문서의 시작");
							break;
						case XmlPullParser.END_DOCUMENT: 	// 문서의 끝
//							log.info("문서의  끝");
							break;
						case XmlPullParser.TEXT:
							tag = parser.getName();
//							log.info(parser.getText());
							if ( isLastBuildDate ) {
								result.put(TAG_LAST_BUILD_DATE, parser.getText());
								isLastBuildDate = false;
							} else if ( isTotal ) {
								result.put(TAG_TOTAL, parser.getText());
								pageVo.setTotalRecordCount( Integer.parseInt(parser.getText()) ); 
								isTotal = false;
							} else if ( isStart ) {
								result.put(TAG_START, parser.getText());
								isStart = false;
							} else if ( isDisplay ) {
								result.put(TAG_DISPLAY, parser.getText());
								isDisplay = false;
							} else if ( isItem && isTitle ) {
								item.put(TAG_TITLE, parser.getText());
								isTitle = false;
							} else if ( isItem && isLink ) {
								item.put(TAG_LINK, parser.getText());
							} else if ( isItem && isDescription ) {
								item.put(TAG_DESCRIPTION, parser.getText());
							} else if ( isItem && isBlogername ) {
								item.put(TAG_BLOGGERNAME, parser.getText());
							} else if ( isItem && isBloggerlink ) {
								item.put(TAG_BLOGGERLINK, parser.getText());
							}
							break;
						case XmlPullParser.END_TAG:
							tag = parser.getName();
//							log.info("</" +tag + ">");
							if ( tag.equals(TAG_LAST_BUILD_DATE) ) {
								isLastBuildDate = false;
							} else if ( tag.equals(TAG_TOTAL) ) {
								isTotal = false;
							} else if ( tag.equals(TAG_START) ) {
								isStart = false;
							} else if ( tag.equals(TAG_DISPLAY) ) {
								isDisplay = false;
							} else if ( tag.equals(TAG_ITEM) ) {
								isItem = false;
								itemList.add(item);
							} else if ( tag.equals(TAG_TITLE) ) {
								isTitle = false;
							} else if ( tag.equals(TAG_LINK) ) {
								isLink = false;
							} else if ( tag.equals(TAG_DESCRIPTION) ) {
								isDescription = false;
							} else if ( tag.equals(TAG_BLOGGERNAME) ) {
								isBlogername = false;
							} else if ( tag.equals(TAG_BLOGGERLINK) ) {
								isBloggerlink = false;
							}
							break;
						case XmlPullParser.START_TAG:
							tag = parser.getName();
//							log.info("<" +tag + ">");
							if ( tag.equals(TAG_LAST_BUILD_DATE) ) {
								isLastBuildDate = true;
							} else if ( tag.equals(TAG_TOTAL) ) {
								isTotal = true;
							} else if ( tag.equals(TAG_START) ) {
								isStart = true;
							} else if ( tag.equals(TAG_DISPLAY) ) {
								isDisplay = true;
							} else if ( tag.equals(TAG_ITEM) ) {
								isItem = true;
								item = new HashMap();
							} else if ( tag.equals(TAG_TITLE) ) {
								isTitle = true;
							} else if ( tag.equals(TAG_LINK) ) {
								isLink = true;
							} else if ( tag.equals(TAG_DESCRIPTION) ) {
								isDescription = true;
							} else if ( tag.equals(TAG_BLOGGERNAME) ) {
								isBlogername = true;
							} else if ( tag.equals(TAG_BLOGGERLINK) ) {
								isBloggerlink = true;
							}
							break;
					}
					parserEvent = parser.next();
				}
			}
		} catch(MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(is!=null) try{is.close();}catch (IOException e) {}
			if(isr!=null) try{isr.close();}catch (IOException e) {}
			if(br!=null) try{br.close();}catch (IOException e) {}
			if(connection!=null) connection.disconnect();
		}
		
		// 결과 값 셋팅
		result.put("list", itemList);
		result.put("page", pageVo);
		return result;
	}
}
