package jp.gen.mangamanager;

import static jp.gen.mangamanager.Common.COLUMN_TITLE;
import static jp.gen.mangamanager.Common.COLUMN_TITLE_ID;
import static jp.gen.mangamanager.Common.TABLE_ROW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyListAdapter extends ArrayAdapter<ContentItem>{

	private LayoutInflater inflater;
	private int resourceId;
	private Context context;
	
	public MyListAdapter(Context context,List<ContentItem> obj,int resourceId) {
		super(context,0,obj);
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.resourceId = resourceId;
		this.context = context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = inflater.inflate(resourceId,null);
		}
		
		ImageView imageView = (ImageView)convertView.findViewById(R.id.imageCover);
		TextView textTitle = (TextView)convertView.findViewById(R.id.textTitle);
		TextView textRowMax = (TextView)convertView.findViewById(R.id.textRowMax);
		
		ContentItem item = this.getItem(position);
		textTitle.setText(item.getTitle());
		
		if(item.getCoverImage() != null){
			imageView.setImageBitmap(item.getCoverImage());			
		}else{
			imageView.setImageResource(R.drawable.loading);
		}
		
		if(textRowMax != null){
			String rowMax = getRowMax(item);
			textRowMax.setText(rowMax+"Šª");
		}
		
		return convertView;
	}
	
	private String getRowMax(ContentItem item){
		DBAccess db = new DBAccess(this.context);
		ArrayList<HashMap<String,String>> result = db.select(TABLE_ROW,
														new String[]{COLUMN_TITLE},
														COLUMN_TITLE_ID+"="+item.getId(),
														null,COLUMN_TITLE+" DESC");
		if(result.size() == 0){
			return "0";
		}
		return result.get(0).get(COLUMN_TITLE);
	}

}
