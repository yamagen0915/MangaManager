package jp.gen.mangamanager;



import static jp.gen.mangamanager.Common.COLUMN_GENRE;
import static jp.gen.mangamanager.Common.COLUMN_ID;
import static jp.gen.mangamanager.Common.COLUMN_TITLE;
import static jp.gen.mangamanager.Common.COLUMN_TITLE_ID;
import static jp.gen.mangamanager.Common.TABLE_ROW;
import static jp.gen.mangamanager.Common.TABLE_TITLE;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAccess{
	protected final Context context;
	protected DataBase helper;
	protected SQLiteDatabase db;
	
	public DBAccess(Context context){
		this.context = context;
		helper = new DataBase(this.context);
	}
	
	public ArrayList<HashMap<String, String>> select(String table,String[] columns,String where) {
		return this.select(table, columns,where,null, null);		
	}
	
	public ArrayList<HashMap<String,String>> select(String table,String[] columns,String where,String[] params,String orderBy){
		ArrayList<HashMap<String,String>> datas = new ArrayList<HashMap<String,String>>();
		this.open();
		Cursor result = db.query(table, columns,where,params, null,null,orderBy);
		
		boolean isResultFinish = result.moveToFirst();
		
		while(isResultFinish){
			HashMap<String,String> hashMap = new HashMap<String, String>();
			
			for(int i = 0;i < columns.length;i ++){
				String colomn = columns[i];
				String data = result.getString(i);
				hashMap.put(colomn, data);
			}
			datas.add(hashMap);
			isResultFinish = result.moveToNext();
		}
		this.close();
		return datas;
	}
	
	public void update(String table,String column,String value,String where){
		this.open();
		ContentValues values = new ContentValues();
		values.put(column,value);
		db.update(TABLE_TITLE, values,where,null);
		this.close();
	}
	
	public void insert(String table,String[] columns,String[] values){
		this.open();
		int i = 0;
		ContentValues data = new ContentValues();
		for (String value : values) {
			data.put(columns[i], value);
			i++;
		}
		db.insert(table, "0",data);
		this.close();
	}
	
	public void delete(String table,String whereData)
	{
		this.open();
		db.delete(table, whereData, null);
		this.close();
	}
	
	
	private DBAccess open(){
		db = helper.getWritableDatabase();
		return this;
	}
	
	private void close(){
		helper.close();
	}
	
	public class DataBase extends SQLiteOpenHelper{

		public DataBase(Context context) {
			super(context,"NoisyReminder.db",null,1);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {			
			StringBuilder sql = new StringBuilder();
			sql.append("create table ").append(TABLE_TITLE).append(" (");
			sql.append(COLUMN_ID).append(" integer primary key autoincrement");
			sql.append(",").append(COLUMN_TITLE).append(" varchar(512) not null");
			sql.append(",").append(COLUMN_GENRE).append(" integer not null");
			sql.append(");");
			db.execSQL(sql.toString());
			
			sql = new StringBuilder();
			sql.append("create table ").append(TABLE_ROW).append(" (");
			sql.append(COLUMN_ID).append(" integer primary key autoincrement");
			sql.append(",").append(COLUMN_TITLE_ID).append(" integer not null");
			sql.append(",").append(COLUMN_TITLE).append(" integer not null");
			sql.append(");");
			
			db.execSQL(sql.toString());
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}

}
