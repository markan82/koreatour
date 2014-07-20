package kr.co.teamcloud.koreatour.kor;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.androidhive.ImageLoader;

public class TouristListAdapter extends BaseAdapter {
    
    private Activity activity;
    private ArrayList<HashMap<String, Object>> data;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader; 
    
    public TouristListAdapter(Activity a, ArrayList<HashMap<String, Object>> d) {
        activity = a;
        data = d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    private class ViewHolder {
		public TextView title;
		public TextView address;
		public TextView add;
		public ImageView thumb_image;
	}
    
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=convertView;
        final ViewHolder holder;
        if(convertView==null) {
        	view = inflater.inflate(R.layout.tourlist_list_item, null);
        	holder = new ViewHolder();
			holder.title = (TextView)view.findViewById(R.id.title); // title
			holder.address = (TextView)view.findViewById(R.id.address); // address
			holder.add = (TextView)view.findViewById(R.id.add); // add
			holder.thumb_image=(ImageView)view.findViewById(R.id.list_image); // thumb image
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
        HashMap<String, Object> map = data.get(position);
        
        String title = (String)map.get("title");
        String addr = (String)map.get("addr");
        String imgUrl = (String)map.get("image");
        
        holder.title.setText(title);
        holder.address.setText(addr);
        if( imgUrl!=null && imgUrl.startsWith("http") ) {
        	((View)holder.thumb_image.getParent()).setVisibility(View.VISIBLE);
        	imageLoader.DisplayImage(imgUrl, holder.thumb_image);
        } else {
        	((View)holder.thumb_image.getParent()).setVisibility(View.GONE);
        }
        return view;
    }
}
