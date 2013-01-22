package jp.gen.mangamanager;



import static jp.gen.mangamanager.Common.COLUMN_ID;
import static jp.gen.mangamanager.Common.COLUMN_TITLE;
import static jp.gen.mangamanager.Common.COLUMN_TITLE_ID;
import static jp.gen.mangamanager.Common.TABLE_ROW;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

public class Row extends ParentListView implements Runnable{
	
	private Handler handler;
	private String titleId;
	private int rowTop;
	private int rowNew;
	private DBAccess db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		Intent intent = getIntent();
		ContentItem item = (ContentItem)intent.getSerializableExtra("item");
		this.titleId = item.getId();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		init();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0,0,0,"1Šª‚¾‚¯’Ç‰Á");
		menu.add(0,1,0,"‚Ü‚Æ‚ß‚Ä’Ç‰Á");
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		DBAccess db = new DBAccess(this);
		switch (item.getItemId()) {
		case 0:
			insertItem(db);
			break;
		case 1:
			insertItemCollectively(db);
			break;
		}
		return true;
	}
	
	@Override
	public void searchImage(Intent intent) {
		intent.putExtra("titleId",titleId);
		super.searchImage(intent);
	}
	
	@Override
	public void insertItem(final DBAccess db){
		final EditText edit = new EditText(this);
		edit.setInputType(InputType.TYPE_CLASS_NUMBER);
		
		OnClickListener listener = new OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				db.insert(TABLE_ROW,
							new String[]{COLUMN_ID,COLUMN_TITLE_ID,COLUMN_TITLE},
							new String[]{null,titleId,edit.getText().toString()});
				init();
			}
		};
		
		this.showAlertDialog(edit,listener);
	}
	@Override
	public void editName(final ContentItem item, final DBAccess db) {
		final EditText edit = new EditText(this);
		edit.setText(item.getTitle());
		edit.setInputType(InputType.TYPE_CLASS_NUMBER);
		
		OnClickListener listener = new OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				db.update(TABLE_ROW,COLUMN_TITLE,edit.getText().toString(),COLUMN_ID+"="+item.getId());
				init();
			}
		};
		
		this.showAlertDialog(edit, listener);
	}
	
	@Override
	public void deletItem(ContentItem item, DBAccess db) {
		db.delete(TABLE_ROW,COLUMN_ID+"="+item.getId());
		deleteFile(titleId+"_"+item.getId()+".jpg");
		init();
	}
	
	public void insertItemCollectively(DBAccess db) {
		this.db = db;
		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View view = inflater.inflate(R.layout.layout_insert,null);
		
		OnClickListener listener = new OnClickListener() {
			
				public void onClick(DialogInterface dialog, int which) {
					
					EditText edit = (EditText)view.findViewById(R.id.editInsert);
					edit.setInputType(InputType.TYPE_CLASS_NUMBER);
					if(!edit.equals("")){
						ArrayList<HashMap<String,String>> result = Row.this.db.select(TABLE_ROW,new String[] {COLUMN_TITLE},
																						COLUMN_TITLE_ID+ "=" + titleId,null,COLUMN_TITLE+" DESC");
						
						if(result.size() > 0){
							rowTop = Integer.parseInt(result.get(0).get(COLUMN_TITLE));
						}else{
							rowTop = 0;
						}
						rowNew = Integer.parseInt(edit.getText().toString());
						if((rowNew - rowTop) > 0){
							handler = new Handler();
							new Thread(Row.this).start();
						}
					}
				}
			};
			
		this.showAlertDialog(view, listener);
	}
	
	
	private void init(){
		ArrayList<HashMap<String,String>> contents = getContents(TABLE_ROW,
																	new String[]{COLUMN_ID,COLUMN_TITLE_ID,COLUMN_TITLE},
																	COLUMN_TITLE_ID+"="+this.titleId,
																	COLUMN_TITLE+" DESC");
		
		MyListAdapter adapter = getMyListAdapter(contents,R.layout.layout_list_row);
		ListView listView = (ListView)findViewById(R.id.listContent);
		listView.setAdapter(adapter);
		
		listView.setOnItemLongClickListener(this);
	}

	public void run() {
		for(int i = rowTop + 1;i <= rowNew;i ++){
			db.insert(TABLE_ROW,
					new String[] {COLUMN_ID, COLUMN_TITLE_ID,COLUMN_TITLE },
					new String[]{null,titleId,String.valueOf(i)});
		}
		handler.post(new Runnable() {
			public void run() {
				init();
			}
		});
	}
}
