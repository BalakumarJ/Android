package com.example.findfriends;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ChatWindow extends ActionBarActivity {
	
	private Button button;
	private EditText display;
	private TextView textView; 
	public String Data;  
	public String ip;
	public static String user = FindFriends.user;
	int flag = 0;
	int flag_scr = 0;
	
	Intent intent = getIntent();
	public Socket client = FindFriends.client;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_window);
		button = (Button) findViewById(R.id.button1);
		display = (EditText) findViewById(R.id.display1);
		textView = (TextView) findViewById(R.id.textView1);
		textView.setMovementMethod(new ScrollingMovementMethod());
		button.setOnClickListener(new View.OnClickListener() 
	    {
			public void onClick(View v) 
	        {
	        Data = display.getText().toString();
	        display.setText(null);
	        flag = 1;
	        }
	    }); 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chat_window, menu);
		Thread start_connect = new Thread(new connect());
		start_connect.start();
		return true;
	}
	
public class connect implements Runnable {
		
		@Override
		public void run() {
			android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
			try {
				
				Thread disp = new Thread(new disp_content(client));
				disp.start();
				Thread send = new Thread(new senddata(client));
				send.start();
			} catch (Exception e) {
				
			}
		}
	}

public class senddata implements Runnable {
	Socket socket;
	
	public senddata(Socket socket) {
		this.socket = socket;
	}		
	@Override
	public void run() {
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
		
		String Input = ("Username: " + user);
		try {
			PrintStream writer = new PrintStream(client.getOutputStream());
			writer.println(Input);
			writer.flush();
				
		} catch (Exception e) {
			
		}
		while (true) {
			if (flag == 1) {
				Input = (user + ": " + Data);
		        try {
		        	PrintStream writer = new PrintStream(client.getOutputStream());
					writer.println(Input);
					writer.flush();
		        	flag = 0;
		        } catch (Exception e) {
		        	
		        }
			}
		}
	}
}



public class disp_content implements Runnable {
	Socket socket;
	public disp_content(Socket socket) {
		this.socket = socket;
	}	
	@Override
	public void run() {
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
		while (true) {
			try {
				InputStreamReader reader = new InputStreamReader(client.getInputStream());
				BufferedReader read = new BufferedReader(reader);
				String data = read.readLine();
				final String Da = data;
				if (data.equals("Others have not joined chat session")) {
					flag_scr = 1;
					runOnUiThread(new Runnable() {
					    public void run() {
					    	textView.setText(Da + "\n");
					    	
					    }
					});
					
				}else {
					if (flag_scr == 1) {
						flag_scr = 0;
						runOnUiThread(new Runnable() {
						    public void run() {
						    	textView.setText(Da + "\n");
						    }
						});
					} else {
					runOnUiThread(new Runnable() {
					    public void run() {
					    	textView.append(Da + "\n");
					    	final int scrollAmount = textView.getLayout().getLineTop(textView.getLineCount()) - textView.getHeight();
					        if (scrollAmount > 0)
					            textView.scrollTo(0, scrollAmount);
					        else
					            textView.scrollTo(0,0);	
					    }
					});
					}
				}
				 			
			} catch (Exception e) {
				//textView.setText(e + " :Error");
			}
			
		}
	}
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
