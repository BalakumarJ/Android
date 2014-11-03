
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class UDP_Server {
	byte[][] buffer = new byte[100][1024];
	
	
	DatagramSocket Serversocket;
	String[] users = new String[100];
	String[] ip = new String[100];
	int[] port = new int[100];
	public static int i = 0;
	public static int datapac = 0;
	//int check = 0;
	
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
					//System.out.println(clientPacket);
					if ((Data.substring(0,Math.min(9, Data.length()))).equals("Username:")) {
						users[i] = Data.substring(10,Data.length()).trim();
						port[i] = clientPacket.getPort();
						ip[i] = (clientPacket.getAddress().toString()).substring(1,(clientPacket.getAddress().toString()).length());
						System.out.println(users[i]);
						i = i+1;
					} else {
						if (i > 1) {
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
							byte[] senddata = new byte[1024];
							senddata = ("Insufficient number of users to establish chat").getBytes();
							DatagramPacket sendPacket = new DatagramPacket(senddata, senddata.length, InetAddress.getByName(ip[0]), port[0]); 
							//System.out.println(sendPacket);
							socket.send(sendPacket);	
							//datapac = 0;
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
			//Thread server_write = new Thread(new write(Serversocket));
			//server_write.start();
		} catch (Exception e) {
			
		}
		
	}
		
	public static void main(String[] args)  {
		UDP_Server udp = new UDP_Server();
		udp.go();

	}

}
