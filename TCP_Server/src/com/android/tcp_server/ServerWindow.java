package com.android.tcp_server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

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


public class ServerWindow extends ActionBarActivity {
	public ServerSocket server;
	private Button button;
	private EditText display;
	private TextView textView; 
	public String Input;    
	public Handler whandle = new Handler();
	public int i = 0;
	public int k = 0;
	public int max_client, port;
	public Socket[] socket_array;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_server_window);
		button = (Button) findViewById(R.id.button2);
		display = (EditText) findViewById(R.id.display3);
		textView = (TextView) findViewById(R.id.textView2);
		button.setOnClickListener(new View.OnClickListener() 
	    {
			public void onClick(View v) 
	        {
	        Input = display.getText().toString();
	        display.setText(null);	
	        k = 1;
	        }
	    }); 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Intent intent = getIntent();
		port = Integer.parseInt(intent.getStringExtra(MainActivity.Server_port));
		max_client = Integer.parseInt(intent.getStringExtra(MainActivity.Server_maxClient));
		Thread accept = new Thread(new soc_create());
		accept.start();
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
	public class read implements Runnable {
		Socket clientsocket;
		String prev = "NULL";
		public read(Socket client) {
			this.clientsocket = client;
		}
		@Override
		public void run() {
			android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
			while (true) {
				if (k == 1) {
					try {
						PrintStream writer = new PrintStream(clientsocket.getOutputStream());
						writer.println(Input);
						writer.flush();
						try {
							TimeUnit.SECONDS.sleep(1);
						} catch (InterruptedException e) {
							
						}
						k = 0;
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}
			}
		}
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
					        textView.setText("Client: " + client.getRemoteSocketAddress().toString() + "; message: " + data);
					    }
					});
				} catch (IOException e) {
					break;
				}
			}
			
		}
	}
	
	public class soc_create implements Runnable {
		public int i = 0;
		//public Socket[] socket_array;
		ServerSocket server;
		@Override
		public void run() {
			android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
			try {
		    	server = new ServerSocket(port);
		    	runOnUiThread(new Runnable() {
				    public void run() {
				    	textView.setText("Server running.");
				    }
				}); 
				
				
			} catch (IOException e) {
			}
			
			while (true) {
				if (i < max_client) {
					try{
						Socket temp = server.accept();
						//socket_array[i] = temp;
						i = i+1;
						runOnUiThread(new Runnable() {
						    public void run() {
						    	textView.setText("Connection " + i + " created.");
						    }
						});
						Thread recv_cli = new Thread(new write(temp));
						recv_cli.start();
						Thread pos_cli = new Thread(new read(temp));
						pos_cli.start();
					} catch (IOException e) {
						
					}
									
				} else {
					runOnUiThread(new Runnable() {
					    public void run() {
					    	textView.setText("Maximum number exceeded.");
					    }
					});
				}
			}
			
		}
	}
		
}
