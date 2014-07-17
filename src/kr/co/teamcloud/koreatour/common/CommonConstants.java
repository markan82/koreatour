package kr.co.teamcloud.koreatour.common;
import java.io.IOException;
import java.net.URLEncoder;

public class CommonConstants {
	public static final String TAG = "KoreaTour";	//디버그 태그

	//서비스 키
//	public static String SERVICE_KEY = "xfiW2nwemu0p9O3uIqRinmgTI3vbqKOv1w6gelUMh4piordR6raquqVywCoDHHBe9N06R4Pg+/sq5ov8ZlejxA==";
	public static String SERVICE_KEY = "xfiW2nwemu0p9O3uIqRinmgTI3vbqKOv1w6gelUMh4piordR6raquqVywCoDHHBe9N06R4Pg%2B%2Fsq5ov8ZlejxA%3D%3D";
	public static String END_POINT_URL = "http://api.visitkorea.or.kr/openapi/service/rest/EngService/";
	public static String MOBILE_APP = "Tour+in+Korea";
	
	public static void main(String[] args) {
		try {
			System.out.println( URLEncoder.encode(SERVICE_KEY, "utf-8") );
		} catch (IOException e) {}
	}
}
