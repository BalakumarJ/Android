package com.android.tcp_client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Clientwindow extends ActionBarActivity {
	private Socket client;
	private Button button;
	private EditText display;
	private TextView textView; 
	public String Input;
	private InetAddress ip;    
	public Handler whandle = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.clientwindow_disp);
		
		button = (Button) findViewById(R.id.button2);
		display = (EditText) findViewById(R.id.display3);
		textView = (TextView) findViewById(R.id.textView2);
		button.setOnClickListener(new View.OnClickListener() 
	    {
			public void onClick(View v) 
	        {
	        Input = display.getText().toString();
	        display.setText(null);
	        if (Input.equals("\\disconnect")) {
				try {
					client.close();
				} catch (IOException e){
					e.printStackTrace();
				}
				textView.setText("Connection Terminated");
			}else {
				try {
					PrintStream writer = new PrintStream(client.getOutputStream());
					writer.println(Input);
					writer.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	        }
	    });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Intent intent = getIntent();
		String host = intent.getStringExtra(MainActivity.Server_IP);
		String port = intent.getStringExtra(MainActivity.Server_port);
	    try {
	    	ip = InetAddress.getByName(host);
			client = new Socket(ip,Integer.parseInt(port));
			Toast.makeText(this,"We are now connected to: " + ip + "\n", Toast.LENGTH_LONG).show(); 
			textView.setText("Type \\disconnect to exit connection");	
			Thread write = new Thread(new write(client));
			write.start();
		} catch (IOException e) {
			Toast.makeText(this,"Connect to " + ip + "failed.Exception" + e + "\n", Toast.LENGTH_LONG).show(); 

		}
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
	
	public class write implements Runnable {
		Socket client;
		public write(Socket client) {
			this.client = client;
		}
		@Override
		public void run() {
			 android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
			while (true) {
				try {
					InputStreamReader reader = new InputStreamReader(client.getInputStream());
					BufferedReader read = new BufferedReader(reader);
					final String data = read.readLine();
					runOnUiThread(new Runnable() {
					    public void run() {
					        textView.setText(data);
					    }
					});
				} catch (IOException e) {
					break;
				}
			}
			
		}
	}
		
}
