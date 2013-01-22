package jp.gen.mangamanager;

import static jp.gen.mangamanager.Common.COLUMN_ID;
import static jp.gen.mangamanager.Common.COLUMN_TITLE;
import static jp.gen.mangamanager.Common.TABLE_TITLE;

import java.util.ArrayList;
import java.util.HashMap;

import jp.gen.mangamanager.DownloadTask.onDownloadedResultListener;
import jp.gen.mangamanager.DownloadTask.onTimeOutListener;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class Search extends ParentListView implements DialogInterface.OnClickListener,View.OnClickListener,onDownloadedResultListener,onTimeOutListener{
	
	private String filename = "";
	private DownloadTask task;
	private ListView listView;
	private EditText text;
	private ProgressDialog proDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//�e�L�X�g�{�b�N�X�̃t�H�[�J�X���O���B
		this.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
		setContentView(R.layout.activity_search);
		
		String searchTitle = "";
		Intent intent = getIntent();
		ContentItem item = (ContentItem)intent.getSerializableExtra("item");
		String titleId = intent.getStringExtra("titleId");
		Button button = (Button)findViewById(R.id.btnSearch);
		text = (EditText) findViewById(R.id.editSearch);
		
		listView = (ListView)findViewById(R.id.listSearch);
		listView.setOnItemClickListener(this);
		
		button.setOnClickListener(this);

		//�^�C�g��Id�������Ă����Row
		//�����Ă��Ȃ����Top
		if(titleId != null){
			HashMap<String,String> result = new DBAccess(this).select(TABLE_TITLE,new String[]{COLUMN_TITLE},COLUMN_ID+"="+titleId).get(0);
			searchTitle = result.get(COLUMN_TITLE);
		}
		
		filename =  item.getId()+ "_"+titleId +".jpg";
		searchTitle += item.getTitle();
		
		text.setText(searchTitle);
		this.searchContents(searchTitle);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View arg1, int position,long arg3) {
		ContentItem item = (ContentItem)parent.getItemAtPosition(position);
		ManagementImage.setImage(item.getCoverImage(),filename, Search.this);
		finish();
	}

	private void searchContents(String searchWord){
		proDialog = new ProgressDialog(this);
		proDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		proDialog.setTitle("�u"+searchWord+"�v�Ō�����");
		proDialog.setCancelable(false);
		proDialog.setButton("�L�����Z��",this);
		proDialog.show();
		
		final Handler handler = new Handler();
		task = new DownloadTask();
		task.setOnDownloadListener(new onDownloadedResultListener() {
			
			public void onDownloaded(ArrayList<ContentItem> result) {
				proDialog.dismiss();
				
				//�����������ʌ������
				if(result.size() != 0){
					final MyListAdapter adapter = new MyListAdapter(Search.this,result,R.layout.layout_list_search);
					handler.post(new Runnable() {
						public void run() {
							listView.setAdapter(adapter);
						}
					});
				}else {
					showNotFound();
				}
				
			}
		});
		task.setTimeOutSecond(20);
		task.setOnTimeOutListener(new onTimeOutListener() {
			public void onTimeOut() {
				proDialog.dismiss();
				handler.post(new Runnable() {
					public void run() {
						Search.this.showNotFound();
					}
				});
			}
		});
		task.execute(searchWord,"20");
	}
	
	//�L�����Z���{�^��
	public void onClick(DialogInterface dialog, int which) {
		task.cancel(true);
		dialog.dismiss();
	}
	
	//�����{�^��
	public void onClick(View v) {
		this.searchContents(text.getText().toString());
	}

	public void onDownloaded(ArrayList<ContentItem> result) {
		
	}

	public void onTimeOut() {
		proDialog.dismiss();
		this.showNotFound();
	}
	
	private void showNotFound(){
		new AlertDialog.Builder(Search.this)
									.setMessage("������܂���ł���")
									.setCancelable(true)
									.setPositiveButton("OK",null)
									.show();
	}
}
