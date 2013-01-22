package jp.gen.mangamanager;

import java.io.FileOutputStream;
import java.io.InputStream;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ManagementImage {
	
	public static Bitmap getImage(String filename,Context context){
		Bitmap image = null;
		try {
			InputStream in = context.openFileInput(filename);
			image = BitmapFactory.decodeStream(in);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return image;
	}
	
	public static void setImage(Bitmap image,String filename,Context context){
		try {
			final FileOutputStream out = context.openFileOutput(filename,Context.MODE_PRIVATE);
			image.compress(Bitmap.CompressFormat.JPEG,100,out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
