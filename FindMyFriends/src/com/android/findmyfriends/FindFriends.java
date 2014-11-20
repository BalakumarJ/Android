package com.android.findmyfriends;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class FindFriends extends MainActivity implements LocationListener {
	  double lat, lng;
	  private TextView textview;
	  private LocationManager locationManager;
	  private String provider;		  
	  public static String phoneNumber, user, ip_addr, myph, mr;
	  Bundle bundle;
	  DatagramSocket socket;
	  public static int port;
	  public String aa = new String("");
	  public int flag = 0; 
	  public int chat_flag = 0;
	  public void onCreate(Bundle savedInstanceState) {
		    super.onCreate(savedInstanceState);
		    setContentView(R.layout.activity_find_friends);
		    TelephonyManager tMgr =(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
		    mr = tMgr.getLine1Number();
		    
		    textview = (TextView) findViewById(R.id.textView1);
		    getNumber(this.getContentResolver()); 
		    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		    Criteria criteria = new Criteria();
		    provider = locationManager.getBestProvider(criteria, false);
		    Location location = locationManager.getLastKnownLocation(provider);
		    
		    if (location != null) {
		        //System.out.println("Provider " + provider + " has been selected.");
		        onLocationChanged(location);
		      } else {
		    	  textview.setText("Location not available");
		      }
		    
		  }
	  
	  public void find (View view) {
		  
			flag = 1;
			Thread send = new Thread(new senddata());
		    send.start();
		}
	  public void chat (View view) {
		  if (chat_flag == 1) {
			  socket.close();
			  Intent intent = new Intent(this, ChatWindow.class);
			  intent.putExtras(bundle);
			  
			  startActivity(intent);
		  } else {
			  textview.setText("No one around to chat");
		  }
	  }
	  public class listen implements Runnable {
		  
		  
			
			@Override
			public void run() {
				android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
				try {socket = new DatagramSocket(5000); 
				while (true) {
					byte[] client = new byte[1024];
					DatagramPacket clientPac = new DatagramPacket(client, 1024); 
					socket.receive(clientPac);
					byte[] senddata = new byte[1024];
					senddata = ("got").getBytes();
					DatagramPacket sendPacket = new DatagramPacket(senddata, senddata.length, InetAddress.getByName(ip_addr), port); 
					socket.send(sendPacket);	
					if (clientPac != null) {
						String Dat = new String(clientPac.getData());
						Dat = Dat.trim();
						Dat = Dat.replace(";;"+user, "");
						int lastIndex = 0;
						int count =0;
						String findstr = ";;";
						while(lastIndex != -1){
						       lastIndex = Dat.indexOf(findstr,lastIndex);
						       if( lastIndex != -1){
						             count ++;
						             lastIndex+=findstr.length();
						      }
						}
						
						Dat = Dat.replace(";;", "\n");
						final String disp = Dat; 
						if (count > 1) {
						runOnUiThread(new Runnable() {
						    public void run() {
						    	textview.setText(disp + "\nare near you!!!" );
						    	chat_flag = 1;
						    }
						}); } else {
							runOnUiThread(new Runnable() {
							    public void run() {
							    	textview.setText(disp + "\nis near you!!!" );
							    	chat_flag = 1;
							    }
							});
						}
						break;
					}
				}
				
				} catch (Exception e) {}
			}
		}
	  
	  public class senddata implements Runnable {
				
		 
			@Override
			public void run() {
				android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
				
				try {socket = new DatagramSocket(5000); 
				byte[] senddata = new byte[1024];
				
				if (mr != null && !mr.isEmpty()) {
	            	aa = mr + aa;
			    } else {
			    	aa = myph + aa;
			    }
				senddata = ("Start:" + user + ";;" + lat + ";;" + lng + ";;" + aa ).getBytes();
					while (true) {
						if (flag == 1) {
							DatagramPacket sendPacket;
							sendPacket = new DatagramPacket(senddata, senddata.length, InetAddress.getByName(ip_addr), port); 
							socket.send(sendPacket);
							byte[] client = new byte[100];
							DatagramPacket clientPac = new DatagramPacket(client, 100); 
							socket.setSoTimeout(5000);
							socket.receive(clientPac);
							if (clientPac != null) {
								String Dat = new String(clientPac.getData());
								Dat = Dat.trim();
								if (Dat.equals("got")) {
									runOnUiThread(new Runnable() {
									    public void run() {
									    	//textview.setText(aa);
									    	textview.setText("Searching for friends.....");
									    }
									}); 
									Thread list = new Thread(new listen());
									list.start();
									flag = 0; 
									break; 
								} else {
									runOnUiThread(new Runnable() {
									    public void run() {
									    	textview.setText("Connecting to server.....");
									    	
									    }
									}); 
								}
							}
						}
					}
					socket.close();
				} catch (Exception e) {}
			}
		}
	  
	  public void getNumber(ContentResolver cr)
	    {
	        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
	        
            
	        while (phones.moveToNext())
	        {
	        	phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
	        	phoneNumber = phoneNumber.replace("(", "");
	        	phoneNumber = phoneNumber.replace(") ", "");
	        	phoneNumber = phoneNumber.replace("-", "");
	        	
	        	aa = aa + ";;" + phoneNumber;
	        }
	        phones.close();// close cursor
	        //textview.setText(aa);
	    }
	      


	  /* Request updates at startup */
	  @Override
	  protected void onResume() {
	    super.onResume();
	    locationManager.requestLocationUpdates(provider, 400, 1, this);
	  }

	  /* Remove the locationlistener updates when Activity is paused */
	  @Override
	  protected void onPause() {
	    super.onPause();
	    locationManager.removeUpdates(this);
	    
	  }

	  @Override
	  public void onLocationChanged(Location location) {
	    lat = (double) (location.getLatitude());
	    lng = (double) (location.getLongitude());
	    
	  }

	  @Override
	  public void onStatusChanged(String provider, int status, Bundle extras) {
	    // Auto-generated method stub

	  }

	  @Override
	  public void onProviderEnabled(String provider) {
	    Toast.makeText(this, "Enabled new provider " + provider, Toast.LENGTH_SHORT).show();

	  }

	  @Override
	  public void onProviderDisabled(String provider) {
	    Toast.makeText(this, "Disabled provider " + provider, Toast.LENGTH_SHORT).show();
	  }
	
	  
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.find_friends, menu);
		bundle = getIntent().getExtras();
		ip_addr = bundle.getString(Server_ip);
		port = Integer.parseInt(bundle.getString(Server_port));
		user = bundle.getString(User_name);
		myph = bundle.getString(My_phone);
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
