package jp.gen.mangamanager;

import static jp.gen.mangamanager.Common.COLUMN_GENRE;
import static jp.gen.mangamanager.Common.COLUMN_ID;
import static jp.gen.mangamanager.Common.COLUMN_TITLE;
import static jp.gen.mangamanager.Common.COLUMN_TITLE_ID;
import static jp.gen.mangamanager.Common.TABLE_ROW;
import static jp.gen.mangamanager.Common.TABLE_TITLE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.gen.mangamanager.DownloadTask.onDownloadedResultListener;
import jp.gen.mangamanager.DownloadTask.onTimeOutListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class ParentListView extends Activity implements OnItemClickListener,OnItemLongClickListener{
	
	private DBAccess db = null;
	
	public ParentListView() {
		this.db = new DBAccess(this);
	}
	
	public ArrayList<HashMap<String,String>> getContents(String table,String[] columns,String where,String orderBy){
		return this.db.select(table, columns, where, null, orderBy);
	}
	
	public MyListAdapter getMyListAdapter(ArrayList<HashMap<String,String>> data,int resourceId){
		List<ContentItem> list = new ArrayList<ContentItem>();
		final MyListAdapter adapter = new MyListAdapter(ParentListView.this,list,resourceId);
		for (final HashMap<String,String> content :data) {
			final ContentItem item = new ContentItem();
			item.setId(content.get(COLUMN_ID));
			item.setTitle(content.get(COLUMN_TITLE));
			item.setGenre(content.get(COLUMN_GENRE));
			
			String filename = content.get(COLUMN_ID) +"_" + content.get(COLUMN_TITLE_ID) + ".jpg";
			Bitmap image = ManagementImage.getImage(filename,this);
			if(image != null){
				item.setCoverImage(image);
			}else{
				DownloadTask task = new DownloadTask();
				task.setOnDownloadListener(new onDownloadedResultListener() {
					
					public void onDownloaded(ArrayList<ContentItem> result) {
						if(result.size() > 0){
							String filename = content.get(COLUMN_ID) +"_" + content.get(COLUMN_TITLE_ID) + ".jpg";
							
							ManagementImage.setImage(
									result.get(0).getCoverImage(),
									filename,
									ParentListView.this
								);
							item.setCoverImage(result.get(0).getCoverImage());
							adapter.notifyDataSetChanged();
						}
					}
				});
				final Handler handler = new Handler();
				task.setTimeOutSecond(20);
				task.setOnTimeOutListener(new onTimeOutListener() {
					public void onTimeOut() {
						handler.post(new Runnable() {
							public void run() {
								Bitmap image = BitmapFactory.decodeResource(ParentListView.this.getResources(),R.drawable.ic_launcher);  
								item.setCoverImage(image);
								adapter.notifyDataSetInvalidated();
							}
						});
					}
				});
				task.execute(Rakuten.getSearchWord(content,this),"1");
			}
			list.add(item);
		}
		return adapter;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		menu.add("追加");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		insertItem(db);
		return super.onOptionsItemSelected(item);
	}

	public void onItemClick(AdapterView<?> parent, View arg1, int position,long arg3) {
		ContentItem item = (ContentItem)parent.getItemAtPosition(position);
		
		Intent intent = new Intent(this,Row.class);
		item.setCoverImage(null);//Bitmapはそのままだとシリアライズできない
		intent.putExtra("item",item);
		startActivity(intent);
	}

	public boolean onItemLongClick(AdapterView<?> parent, View arg1, int position,long arg3) {
		final ContentItem item = (ContentItem)parent.getItemAtPosition(position);
		CharSequence menu[] = new CharSequence[]{"画像を変更","名前を変更","削除"};
		new AlertDialog.Builder(this)
					.setTitle("メニュー")
					.setNegativeButton("キャンセル",null)
					.setItems(menu,new OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							switch (which) {
							case 0:
								Intent intent = new Intent(ParentListView.this,Search.class);
								item.setCoverImage(null);
								intent.putExtra("item",item);
								ParentListView.this.searchImage(intent);
								break;
							case 1:
								ParentListView.this.editName(item,db);
								break;
							case 2:
								ParentListView.this.deletItem(item,db);
								break;
							}
						}
					})
					.show();
		return false;
	}
	
	public void deletItem(ContentItem item,DBAccess db){
		db.delete(TABLE_TITLE,COLUMN_ID+"="+item.getId());
		db.delete(TABLE_ROW,COLUMN_TITLE_ID+"="+item.getId());
		this.deleteFile(item.getId()+"_null.jpg");
	}
	
	public void searchImage(Intent intent) {
		startActivity(intent);
	}
	
	public void editName(final ContentItem item,DBAccess db){}
	
	public void insertItem(DBAccess db){}

	public void showAlertDialog(View view,OnClickListener listener){
		new AlertDialog.Builder(this)
						.setTitle("タイトルを入力してください")
						.setView(view)
						.setPositiveButton("登録",listener)
						.setNegativeButton("キャンセル",null)
						.show();
	}
}
