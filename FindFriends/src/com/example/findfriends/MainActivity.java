package com.example.findfriends;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;





public class MainActivity extends ActionBarActivity {
	public final static String Server_ip = "com.android.Find Friends.ip";
	public final static String Server_port = "com.android.Find Friends.port";
	public final static String User_name = "com.android.Find Friends.user";
	public final static String My_phone = "com.android.Find Friends.phs";
	//public static String Server_ip, Server_port, User_name;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	public void Chat(View view) {
		Intent intent = new Intent(this, FindFriends.class);
		EditText editText = (EditText) findViewById(R.id.display);
		String ip = editText.getText().toString();
		EditText editText1 = (EditText) findViewById(R.id.display1);
		String port = editText1.getText().toString();
		EditText editText2 = (EditText) findViewById(R.id.display2);
		String user = editText2.getText().toString();
		EditText editText3 = (EditText) findViewById(R.id.display3);
		String phs = editText3.getText().toString();
		intent.putExtra(Server_ip, ip);
		intent.putExtra(Server_port, port);
		intent.putExtra(User_name, user);
		intent.putExtra(My_phone, phs);
	    startActivity(intent);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
