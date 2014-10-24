package com.andriod.multiactivity;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;



public class DispActivity extends ListActivity {
	ArrayAdapter<String> adapter;
	int clickCounter=0;
	ArrayList<String> listItems=new ArrayList<String>();
	private File[] imagelist;
	public String[] pdflist;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_disp);

		File images =new File(Environment.getExternalStorageDirectory().getPath()  + "/FileFolder/");  
		imagelist = images.listFiles(new FilenameFilter(){  
			
			public boolean accept(File dir, String name)  
			{  
				return ((name.endsWith(".pdf")) || (name.endsWith(".txt")) || (name.endsWith(".jpg")) || (name.endsWith(".mp3")));  
			}  
		}); 
		pdflist = new String[imagelist.length]; 
		for(int i = 0;i<imagelist.length;i++)
		{
			pdflist[i] = imagelist[i].getName();
		}
		this.setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, pdflist));
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		PackageManager packageManager = getPackageManager();
		 Intent testIntent = new Intent(Intent.ACTION_VIEW);
		 List list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);
		 testIntent.setType("application/pdf");	 
		 
		 if (list.size() > 0 && imagelist[(int) id].isFile() && pdflist[(int) id].contains(".pdf")) {
			 
			 Intent intent = new Intent();
		     intent.setAction(Intent.ACTION_VIEW);
		     Uri uri = Uri.fromFile(imagelist[(int) id].getAbsoluteFile());
		     intent.setDataAndType(uri, "application/pdf");
		     startActivity(intent);
		     
		 }
		 
		 if (list.size() > 0 && imagelist[(int) id].isFile() && pdflist[(int) id].contains(".mp3")) {
		     
		    	 Intent intent = new Intent();
			     intent.setAction(Intent.ACTION_VIEW);
			     Uri uri = Uri.fromFile(imagelist[(int) id].getAbsoluteFile());
			     intent.setDataAndType(uri, "audio/mp3");
			     startActivity(intent);
			
		 }
		 
		 if (list.size() > 0 && imagelist[(int) id].isFile() && pdflist[(int) id].contains(".jpg")) {
			 
			 Intent intent = new Intent();
		     intent.setAction(Intent.ACTION_VIEW);
		     Uri uri = Uri.fromFile(imagelist[(int) id].getAbsoluteFile());
		     intent.setDataAndType(uri, "image/jpeg");
		     startActivity(intent);
		 }
		 
		 if (list.size() > 0 && imagelist[(int) id].isFile() && pdflist[(int) id].contains(".txt")) {
			 
			 Intent intent = new Intent();
		     intent.setAction(Intent.ACTION_VIEW);
		     Uri uri = Uri.fromFile(imagelist[(int) id].getAbsoluteFile());
		     intent.setDataAndType(uri, "text/plain");
		     startActivity(intent);
		 }

	} 
}     