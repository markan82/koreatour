package kr.co.teamcloud.koreatour.eng;

import android.app.Activity;

public class TourBaseActivity extends Activity {
	
	// JSON Node names
	protected final String TAG_RESPONSE = "response";
	protected final String TAG_HEADER = "header";
	protected final String TAG_RESULT_CODE = "resultCode";
	protected final String TAG_RESULT_MSG = "resultMsg";

	protected final String TAG_BODY = "body";
	protected final String TAG_NUM_OF_ROWS = "numOfRows";
	protected final String TAG_PAGE_NO = "pageNo";
	protected final String TAG_TOTAL_COUNT = "totalCount";
	
	protected final String TAG_ITEMS = "items";
	protected final String TAG_ITEM = "item";

	protected final String TAG_CONTENT_ID = "contentid";
	protected final String TAG_CONTENT_TYPE_ID = "contenttypeid";
	protected final String TAG_TITLE = "title";
	protected final String TAG_ADDR1 = "addr1";
	protected final String TAG_ADDR2 = "addr2";

	protected final String TAG_FIRSTIMAGE = "firstimage";
	protected final String TAG_FIRSTIMAGE2 = "firstimage2";
	protected final String TAG_MAPX = "mapx";
	protected final String TAG_MAPY = "mapy";
	protected final String TAG_READCOUNT = "readcount";
}
