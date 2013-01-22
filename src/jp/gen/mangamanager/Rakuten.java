package jp.gen.mangamanager;

import static jp.gen.mangamanager.Common.COLUMN_ID;
import static jp.gen.mangamanager.Common.COLUMN_TITLE;
import static jp.gen.mangamanager.Common.COLUMN_TITLE_ID;
import static jp.gen.mangamanager.Common.TABLE_TITLE;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

public class Rakuten {
	
	private static final String devID = "1014625260581689227";
	
	static private String getRequestUrl(String keyword,String hits)
	{
		Uri.Builder uri = new Uri.Builder();
		uri.scheme("https");
		uri.authority("app.rakuten.co.jp");
		uri.path("/services/api/BooksBook/Search/20121128");
		uri.appendQueryParameter("applicationId", devID);
		uri.appendQueryParameter("hits", hits);
		uri.appendQueryParameter("title",keyword);
	
		return uri.toString();
	}
	
	static private String getJson(String keyword,String hits)
	{
		String url = getRequestUrl(keyword, hits);
		HttpClient httpClient = new DefaultHttpClient();
		
		HttpUriRequest httpRequest = new HttpGet(url);
		HttpResponse httpResponse = null;
		
		try{
			httpResponse = httpClient.execute(httpRequest);
		}catch(IOException e){
			Log.e("エラー",e.toString());
		}
		
		String json = "";
		
		if(httpResponse != null && httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
		{
			HttpEntity entity = httpResponse.getEntity();
			try{
				json = EntityUtils.toString(entity);
			}catch(Exception e){
				Log.e("エラー","JSONに変換エラー");
			}
		}
		
		httpClient.getConnectionManager().shutdown();
		return json;
	}
	
	static public ArrayList<JSONObject> getItems(String keyword,String hits){
		ArrayList<JSONObject> items = new ArrayList<JSONObject>();
		String json = getJson(keyword, hits);
		Log.d("manga",json);
		
		try {
			JSONArray itemArray = new JSONObject(json).getJSONArray("Items");
			
			for(int i = 0; i < itemArray.length(); i++){
				JSONObject object = itemArray.getJSONObject(i).getJSONObject("Item");
				items.add(object);
			}
			
		} catch (Exception e) {
			Log.e("manga","json:"+e.toString());
		}
		return items;
	}
	
	static public Bitmap getImageFromURL(String imageUrl)
	{
		byte[] result  = null;
		byte[] line = new byte[1024];
		InputStream stream = null;
		HttpURLConnection connect = null;
		ByteArrayOutputStream out = null;
		try{
			URL  url = new URL(imageUrl);
			connect = (HttpURLConnection) url.openConnection();
			connect.setRequestMethod("GET");
			connect.connect();
			stream = connect.getInputStream();
			
			out  = new ByteArrayOutputStream();
			int size = 0;
			while(true)
			{
				size = stream.read(line);
				if(size <= 0)
				{
					break;
				}
				out.write(line,0,size);
			}
			
			result = out.toByteArray();
			
		}catch(Exception e){
			Log.e("画像取得失敗",e.toString());
		}finally{
			try{
				if(connect != null){
					connect.disconnect();
				}
				if(stream != null){
					stream.close();
				}
				if(out != null){
					out.close();
				}
			}catch(Exception e){
				Log.e("ストリームの終了失敗",e.toString());
			}
		}
		return BitmapFactory.decodeByteArray(result,0, result.length);
	}
	
	static String getSearchWord(HashMap<String,String> content,Context context){
		String word = "";
		DBAccess db = new DBAccess(context);
		if(content.get(COLUMN_TITLE_ID) == null){
			word = content.get(COLUMN_TITLE);
		}else{
			HashMap<String,String> result = db.select(TABLE_TITLE,new String[]{COLUMN_TITLE},COLUMN_ID+"="+content.get(COLUMN_TITLE_ID)).get(0);
			word = result.get(COLUMN_TITLE) + content.get(COLUMN_TITLE);
		}
		return word;
	}

}
