package jp.gen.mangamanager;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class DownloadTask extends AsyncTask<String,Void,ArrayList<ContentItem>>{
	
	private onDownloadedResultListener mDownloadedResultListener = null;
	private onTimeOutListener mTimeOutListener = null;
	private int timeoutSecond = 0;
	
	@Override
	protected ArrayList<ContentItem> doInBackground(String... params) {
		
		if(timeoutSecond > 0) this.setTimeOut();
		
		String searchWord = params[0];
		String hits = params[1];
		
		ArrayList<ContentItem> contents = new ArrayList<ContentItem>();
		ArrayList<JSONObject> rakutenItems = Rakuten.getItems(searchWord, hits);
		
		try {
			for(int i = 0; i < rakutenItems.size(); i++){
				JSONObject object = rakutenItems.get(i);
				
				String title = object.getString("title").toString();
				String imageUrl = object.getString("mediumImageUrl").toString();
				
				ContentItem item = new ContentItem();
				item.setTitle(title);
				item.setCoverImage(Rakuten.getImageFromURL(imageUrl));
				
				contents.add(item);
			}
		} catch (Exception e) {
			Log.e("manga","json:"+e.toString());
		}
		return contents;
	}
	
	@Override  
    protected void onPostExecute(ArrayList<ContentItem> result) {
		if(result != null && mDownloadedResultListener != null)
		{
			mDownloadedResultListener.onDownloaded(result);
		}
    }
	
	private void setTimeOut() {
		Timer timer = new Timer();
		timer.schedule(new TimeOut(this,this.mTimeOutListener),this.timeoutSecond);
	}
	
	public void setTimeOutSecond(int second){
		this.timeoutSecond = second * 1000;
	}
	
	public void setOnDownloadListener(onDownloadedResultListener listener) {
		mDownloadedResultListener = listener;
	}
	
	public void setOnTimeOutListener(onTimeOutListener listener) {
		mTimeOutListener = listener;
	}
	
	interface onDownloadedResultListener {
		void onDownloaded(ArrayList<ContentItem> result);
	}
	
	interface onTimeOutListener {
		void onTimeOut();
	}
	
	private class TimeOut extends TimerTask{
		
		private DownloadTask task;
		private onTimeOutListener listener;
		
		public TimeOut(DownloadTask task,onTimeOutListener listener) {
			this.task = task;
			this.listener = listener;
		}
		
		@Override
		public void run() {
			boolean isCanceled = this.task.cancel(true);
			if(listener != null && isCanceled){
				Log.d("manga","timeout");
				listener.onTimeOut();
			}
		}
	}


}
