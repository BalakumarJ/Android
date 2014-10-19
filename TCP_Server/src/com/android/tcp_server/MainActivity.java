package com.android.tcp_server;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends ActionBarActivity {
	public final static String Server_port = "com.android.TCP_Server.port";
	public final static String Server_maxClient = "com.android.TCP_Server.max_client";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}


	public void Serverview(View view) {
		Intent intent = new Intent(this, ServerWindow.class);
		EditText editText = (EditText) findViewById(R.id.display);
		String port = editText.getText().toString();
		EditText editText1 = (EditText) findViewById(R.id.display1);
		String max_client = editText1.getText().toString();
		intent.putExtra(Server_port, port);
		intent.putExtra(Server_maxClient, max_client);
	    startActivity(intent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
