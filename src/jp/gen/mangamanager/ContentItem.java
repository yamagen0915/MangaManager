package jp.gen.mangamanager;

import java.io.Serializable;

import android.graphics.Bitmap;

public class ContentItem implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String title = null;
	private String id = null;
	private String genre = null;
	private Bitmap coverImage = null;
	
	public String getTitle(){
		return this.title;
	}
	public void setTitle(String title){
		this.title = title;
	}
	
	public String getId(){
		return this.id;
	}
	public void setId(String id){
		this.id = id;
	}
	
	public String getGenre(){
		return this.genre;
	}
	public void setGenre(String genre){
		this.genre = genre;
	}
	
	public Bitmap getCoverImage(){
		return this.coverImage;
	}
	public void setCoverImage(Bitmap coverImage){
		this.coverImage = coverImage;
	}

}
