
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class UDP_Server {
	byte[][] buffer = new byte[100][1024];
	String[] phs = new String[100];
	Double[] lat = new Double[100];
	Double[] lng = new Double[100];
	DatagramSocket Serversocket;
	String[] users = new String[100];
	String[] ip = new String[100];
	String[] locs = new String[100];
	int[] port = new int[100];
	public static int i = 0;
	public static int datapac = 0;
	public int loc_flag = 0;
	int send_flag = 0;
	//int check = 0;
	
	private double distance(double lat1, double lon1, double lat2, double lon2) {
		double dist = (Math.acos(Math.sin(lat1 * (Math.PI / 180.0)) * Math.sin(lat2 * (Math.PI / 180.0)) 
				+ Math.cos(lat1 * (Math.PI / 180.0)) * Math.cos(lat2 * (Math.PI / 180.0)) 
				* Math.cos((lon1 - lon2) * (Math.PI / 180.0))) * (180.0/Math.PI)) * 60 * 1.1515 * 1.609344 ;
		return (dist);
	}
	
	/*public class nearyou implements Runnable {
		DatagramSocket socket;
		
		public nearyou(DatagramSocket socket) {
			this.socket = socket;
		}
				
		public void run () {
			while(true) {
				if (loc_flag == 1) {
					System.out.println("We are in");
					loc_flag = 0;
				}
			}
		}
	}*/
	
	
	public class read implements Runnable {
		DatagramSocket socket;
		
		public read(DatagramSocket socket) {
			this.socket = socket;
		}
				
		public void run () {
			//System.out.println("Server is Running");
			while (true) {
				byte[] clientdata = new byte[1024];
				DatagramPacket clientPacket = new DatagramPacket(clientdata, 1024); 
				try {
					socket.receive(clientPacket);
					String Data = new String(clientPacket.getData());
					Data = Data.substring(0,clientPacket.getLength());
					Data = Data.trim();
					System.out.println(Data);
					if ((Data.substring(0,Math.min(6, Data.length()))).equals("Start:")) {
					
						locs[i] =  Data.substring(6,Data.length());
						System.out.println(locs[i]);
						String[] info = locs[i].split(";;");
						users[i] = info[0];
						lat[i] = Double.parseDouble(info[1]);
						lng[i] = Double.parseDouble(info[2]);
						phs[i] = info[3];
						port[i] = clientPacket.getPort();
						ip[i] = (clientPacket.getAddress().toString()).substring(1,(clientPacket.getAddress().toString()).length());
						System.out.println(users[i]);
						byte[] senddata = new byte[1024];
						senddata = ("got").getBytes();
						DatagramPacket sendPacket = new DatagramPacket(senddata, senddata.length, InetAddress.getByName(ip[i]), port[i]); 
						socket.send(sendPacket);	
						i = i+1;
						if (i > 1) {
							double[][] dist = new double[i][i];
							String near = new String("");
							for (int k = 0; k < i; k++) {
								for (int l = 0; l < i; l++) {
									if(k == l) { dist[k][l] = 0; }
									else { dist[k][l] = distance(lat[k], lng[k], lat[l], lng[l]); }
								}
							}
							for (int k = 0; k < i; k++) {
								for (int l = 0; l < i; l++) {
									int lastIndex = 0;
									int count =0;
									String findstr = ";;";
									while(lastIndex != -1){
									       lastIndex = locs[k].indexOf(findstr,lastIndex);
									       if( lastIndex != -1){
									             count ++;
									             lastIndex+=findstr.length();
									      }
									}
									if (k != l && dist[k][l] <= 1.00 && count >= 1) {
										near = near + ";;" + users[l];
										send_flag = 1;
									}
								}
								while (true) {
								if (send_flag == 1) {
									System.out.println(near);
									senddata = near.getBytes();
									DatagramPacket sendPac = new DatagramPacket(senddata, senddata.length, InetAddress.getByName(ip[k]), port[k]);
									socket.send(sendPac);
									byte[] client = new byte[100];
									DatagramPacket clientPac = new DatagramPacket(client, 100); 
									socket.setSoTimeout(5000);
									socket.receive(clientPac);
									if (clientPac != null) {
										String Dat = new String(clientPac.getData());
										Dat = Dat.trim();
										if (Dat.equals("got")) { break; }
									}
								}
			
									
								}
							}
							
						}
						
					} else {
					if ((Data.substring(0,Math.min(9, Data.length()))).equals("Username:")) {
						/*users[i] = Data.substring(10,Data.length()).trim();
						port[i] = clientPacket.getPort();
						ip[i] = (clientPacket.getAddress().toString()).substring(1,(clientPacket.getAddress().toString()).length());
						System.out.println(Data.substring(10,Data.length()).trim()+ " Chat");
						i = i+1;*/
					} else {
						if (i > 1) {
							//System.out.println("here");
							byte[] senddata = new byte[1024];
							senddata = ((clientPacket.getAddress().toString()).substring(1,(clientPacket.getAddress().toString()).length()) + 
										";;" + clientPacket.getPort() + ";;" + Data).getBytes();
							System.out.println((clientPacket.getAddress().toString()).substring(1,(clientPacket.getAddress().toString()).length()) + 
										";;" + clientPacket.getPort() + ";;" + Data);
							for (int j = 0; j < i; j++) {
								DatagramPacket sendPacket = new DatagramPacket(senddata, senddata.length, InetAddress.getByName(ip[j]), port[j]); 
								//System.out.println(sendPacket);
								socket.send(sendPacket);	
							}
								
							//}
						}else  {
							System.out.println(ip[0] + " " + port[0]);
							byte[] senddata = new byte[1024];
							senddata = ("Insufficient number of users to establish chat").getBytes();
							DatagramPacket sendPacket = new DatagramPacket(senddata, senddata.length, InetAddress.getByName(ip[0]), port[0]); 
							//System.out.println(sendPacket);
							socket.send(sendPacket);	
							//datapac = 0;
						}
					}
				}
				} catch (Exception e){
					e.printStackTrace();
				}
				
			}
		}
	}
	
	public class write implements Runnable {
		
		DatagramSocket socket;
		
		public write(DatagramSocket socket) {
			this.socket = socket;
		}
		
		public void run() {
			//System.out.println("Server is Running");
			while (true) {
				if (i > 1) {
					//System.out.println("Server");
					if (datapac > 0) {
						for (int j = 0; j < i; j++) {
							try {
								DatagramPacket sendPacket = new DatagramPacket(buffer[0], buffer[0].length, InetAddress.getByName(ip[j]), port[j]); 
								System.out.println(sendPacket);
								socket.send(sendPacket);	
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						for (int k = 0; k < datapac-1 ; k++) {
							buffer[k] = buffer[k+1];
						}
						datapac = datapac - 1;	
						}
				}else  {
					//System.out.println(datapac);
					if (datapac > 0) {
						//System.out.println(datapac);
						//System.out.println("serf");
						byte[] senddata = new byte[1024];
						senddata = ("Insufficient number of users to establish chat").getBytes();
						try {
							DatagramPacket sendPacket = new DatagramPacket(senddata, senddata.length, InetAddress.getByName(ip[0]), port[0]); 
							System.out.println(sendPacket);
							socket.send(sendPacket);	
							//datapac = 0;
						} catch (Exception e) {
							e.printStackTrace();
						}

				}
				}
				
			}
		}
	}
	
	public void go() {
		try {
			Serversocket = new DatagramSocket(5000);
			System.out.println("Server is Running");
			Thread server_read = new Thread(new read(Serversocket));
			server_read.start();
			//Thread near = new Thread(new nearyou(Serversocket));
			//near.start();
		} catch (Exception e) {
			
		}
		
	}
		
	public static void main(String[] args)  {
		UDP_Server udp = new UDP_Server();
		udp.go();

	}

}
