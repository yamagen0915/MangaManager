package jp.gen.mangamanager;

import static jp.gen.mangamanager.Common.COLUMN_GENRE;
import static jp.gen.mangamanager.Common.COLUMN_ID;
import static jp.gen.mangamanager.Common.COLUMN_TITLE;
import static jp.gen.mangamanager.Common.GENRE_NOVELS;
import static jp.gen.mangamanager.Common.TABLE_TITLE;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListView;

public class Novels extends ParentListView{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		init();
	}

	@Override
	public void editName(final ContentItem item, final DBAccess db) {
		final EditText edit = new EditText(this);
		edit.setText(item.getTitle());
		
		OnClickListener listener = new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				db.update(TABLE_TITLE,COLUMN_TITLE,edit.getText().toString(),COLUMN_ID+"="+item.getId());
				init();
			}
		};
		this.showAlertDialog(edit, listener);
	}
	
	@Override
	public void deletItem(ContentItem item, DBAccess db) {
		super.deletItem(item, db);
		init();
	}
	
	@Override
	public void insertItem(final DBAccess db) {
		final EditText edit = new EditText(this);
		OnClickListener listener = new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				db.insert(TABLE_TITLE,
							new String[]{COLUMN_ID,COLUMN_TITLE,COLUMN_GENRE},
							new String[]{null,edit.getText().toString(),GENRE_NOVELS});
				init();
			}
		};
		this.showAlertDialog(edit,listener);
	}
	
	private void init(){
		ArrayList<HashMap<String, String>> contents = getContents(TABLE_TITLE,
																		new String[]{COLUMN_ID,COLUMN_TITLE,COLUMN_GENRE},
																		COLUMN_GENRE+"="+GENRE_NOVELS,
																		COLUMN_ID+" DESC");

		MyListAdapter adapter = getMyListAdapter(contents,R.layout.layout_list_top);
		ListView listView = (ListView) findViewById(R.id.listContent);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(this);
		listView.setOnItemLongClickListener(this);
	}

}
