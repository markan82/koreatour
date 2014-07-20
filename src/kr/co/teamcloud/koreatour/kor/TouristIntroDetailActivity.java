package kr.co.teamcloud.koreatour.kor;

import android.content.*;
import android.os.*;
import android.widget.*;
import java.util.*;

public class TouristIntroDetailActivity extends TourBaseActivity
{
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_tourist_intro_detail);
		
		Intent intent = getIntent();
		HashMap<String, Object> data = (HashMap<String, Object>)intent.getSerializableExtra("data");
		//Log.v("HashMapTest", hashMap.get("key"));
		
		TextView txtIntroDetail = (TextView)findViewById(R.id.txtIntroDetail);
		txtIntroDetail.setText(data.toString());
	}
	
}
