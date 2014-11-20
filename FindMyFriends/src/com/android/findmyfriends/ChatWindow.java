package com.android.findmyfriends;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

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
	public String Input;  
	public String ip;
	DatagramSocket clientsocket;
	Intent intent = getIntent();
	public static String ip_addr = FindFriends.ip_addr;
	public static String user = FindFriends.user;
	public static int port = FindFriends.port;
	
	int flag = 0;
	
	
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
	        Input = display.getText().toString();
	        display.setText(null);
	        flag = 1;
	        }
	    }); 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//Intent intent = getIntent();
		/*ip_addr = intent.getStringExtra(MainActivity.Server_ip);
		port = Integer.parseInt(intent.getStringExtra(MainActivity.Server_port));
		user = intent.getStringExtra(MainActivity.User_name);*/
		Thread start_connect = new Thread(new connect());
		start_connect.start();
		getMenuInflater().inflate(R.menu.chat_window, menu);
		return true;
	}
	
	public class connect implements Runnable {
		
		@Override
		public void run() {
			android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
			try {
				clientsocket = new DatagramSocket(5000);
				Thread disp = new Thread(new disp_content(clientsocket));
				disp.start();
				Thread send = new Thread(new senddata(clientsocket));
				send.start();
			} catch (Exception e) {
				
			}
		}
	}
	
	public class senddata implements Runnable {
		DatagramSocket socket;
		
		public senddata(DatagramSocket socket) {
			this.socket = socket;
		}		
		@Override
		public void run() {
			android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
			byte[] senddata = new byte[1024];
			senddata = ("Username: " + user).getBytes();
			try {
				DatagramPacket sendPacket;
				sendPacket = new DatagramPacket(senddata, senddata.length, InetAddress.getByName(ip_addr), port); 
				socket.send(sendPacket);	
			} catch (Exception e) {
				
			}
			while (true) {
				if (flag == 1) {
					senddata = (user + ": " + Input).getBytes();
			        try {
			        	DatagramPacket sendPacket;
			        	sendPacket = new DatagramPacket(senddata, senddata.length, InetAddress.getByName(ip_addr), port); 
			        	socket.send(sendPacket);
			        	/*byte[] client = new byte[1024];
						DatagramPacket clientPac = new DatagramPacket(client, 1024); 
						socket.receive(clientPac);
						String Data = new String(clientPac.getData());
						final String Da = Data;
						
						runOnUiThread(new Runnable() {
						    public void run() {
						    	textView.append(Da);
						    }
						});*/
						
			        	flag = 0;
			        } catch (Exception e) {
			        	
			        }
				}
			}
		}
	}
	
	public static String getLocalIpAddress() {
	    try {
	        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
	            NetworkInterface intf = en.nextElement();
	            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
	                InetAddress inetAddress = enumIpAddr.nextElement();
	                if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
	                    return inetAddress.getHostAddress();
	                }
	            }
	        }
	    } catch (SocketException ex) {
	        ex.printStackTrace();
	    }
	    return null;
	}
	
	public class disp_content implements Runnable {
		DatagramSocket socket;
		public disp_content(DatagramSocket socket) {
			this.socket = socket;
		}	
		@Override
		public void run() {
			android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
			while (true) {
				try {
					//textView.setText("yes");
					 ip = getLocalIpAddress();
					ip = InetAddress.getLocalHost().getHostAddress().toString();
					byte[] client = new byte[1024];
					DatagramPacket clientPac = new DatagramPacket(client, 1024); 
					socket.receive(clientPac);
					//textView.setText("no");
					String Data = new String(clientPac.getData());
					Data = Data.substring(0,clientPac.getLength());
					Data = Data.trim();
					final String[] message = Data.split(";;"); 
					final String Da = Data;
					/*
					runOnUiThread(new Runnable() {
					    public void run() {
					    	textView.append(Da);
					    }
					});*/
					if (!(message[0].equals(ip))) {
						runOnUiThread(new Runnable() {
						    public void run() {
						    	textView.append(message[2] + "\n");
						    	final int scrollAmount = textView.getLayout().getLineTop(textView.getLineCount()) - textView.getHeight();
						        if (scrollAmount > 0)
						            textView.scrollTo(0, scrollAmount);
						        else
						            textView.scrollTo(0,0);	
						    }
						}); 
					} else {
						runOnUiThread(new Runnable() {
						    public void run() {
						    	textView.append(Da);
						    }
						});
					} 			
				} catch (Exception e) {
					//textView.setText(e + " :Error");
				}
				
			}
		}
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
