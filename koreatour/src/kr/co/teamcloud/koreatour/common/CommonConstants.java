package kr.co.teamcloud.koreatour.common;
import java.net.*;
import java.io.*;
public class CommonConstants
{
	public static final String TAG = "KoreaTour";	//디버그 태그

	//서비스 키
	public static String serviceKey = "KjHpwYu0kgrn0KQtrx+tZe+FZPAjgzCdA2/17aMuRVlsyAwZ4b+7NDHh2rhsLHb3ivbyjgAdT55AgAt59aHRHQ==";
	static {
		try
		{
			serviceKey = URLEncoder.encode(serviceKey, "utf-8");
		}
		catch (IOException e)
		{}
	}
		
	public static String requestURL = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/";
	
}
