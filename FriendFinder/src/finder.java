import java.net.*;
import java.io.*;
import java.util.*;


public class finder {

	public String Input = new String();
	Scanner scan = new Scanner(System.in);
	public static Socket[] socket_array;
	public static Thread[] server_thread;
	public int number_of_sockets;
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
	int chat_num = 0;
	
	private double distance(double lat1, double lon1, double lat2, double lon2) {
		double dist = (Math.acos(Math.sin(lat1 * (Math.PI / 180.0)) * Math.sin(lat2 * (Math.PI / 180.0)) 
				+ Math.cos(lat1 * (Math.PI / 180.0)) * Math.cos(lat2 * (Math.PI / 180.0)) 
				* Math.cos((lon1 - lon2) * (Math.PI / 180.0))) * (180.0/Math.PI)) * 60 * 1.1515 * 1.609344 ;
		return (dist);
	}
	
		
	public class Read_Socket_thread implements Runnable {
		Socket socket;
		
		public Read_Socket_thread(Socket socket) {
			this.socket = socket;
		}
		
		public void run() {
			while (true) {
				try {
					InputStreamReader reader = new InputStreamReader(socket.getInputStream());
					BufferedReader read = new BufferedReader(reader);
					String data = read.readLine();
					if (data != null) {
						System.out.println("Client: " + socket.getRemoteSocketAddress().toString() + "; message: " + data);
					}
					String curr = socket.getRemoteSocketAddress().toString();
					if ((data.substring(0,Math.min(6, data.length()))).equals("Start:")) {
						
						locs[i] =  data.substring(6,data.length());
						System.out.println(locs[i]);
						String[] info = locs[i].split(";;");
						users[i] = info[0];
						lat[i] = Double.parseDouble(info[1]);
						lng[i] = Double.parseDouble(info[2]);
						phs[i] = info[3];
						//port[i] = clientPacket.getPort();
						ip[i] = socket.getRemoteSocketAddress().toString();
						System.out.println(users[i]);
						
						i = i+1;
						if (i > 1) {
							double[][] dist = new double[i][i];
							String near = new String("");
							for (int k = 0; k < i; k++) {
								for (int l = 0; l < i; l++) {
									if(k == l) { dist[k][l] = 0; }
									else { dist[k][l] = distance(lat[k], lng[k], lat[l], lng[l]); }
									System.out.println(dist[k][l]);
								}
							}
							for (int k = 0; k < i; k++) {
								for (int l = 0; l < i; l++) {
									int lastIndex = 0;
									int count =0;
									String findstr = phs[l];
									while(lastIndex != -1){
									       lastIndex = locs[k].indexOf(findstr,lastIndex);
									       if( lastIndex != -1){
									             count ++;
									             lastIndex+=findstr.length();
									      }
									}
									if (k != l && dist[k][l] <= 0.50 && count >= 1) {
										near = near + ";;" + users[l];
										send_flag = 1;
									}
								}
								
								if (send_flag == 1) {
									System.out.println(near);
									String Input = near ;
									PrintStream writer = new PrintStream(socket_array[k].getOutputStream());
									writer.println(Input);
									writer.flush();
									near = "";
								}
			
									
								
							}
							
						}
					} else {
						if ((data.substring(0,Math.min(9, data.length()))).equals("Username:")) {
							
							System.out.println(data.substring(10,data.length()).trim()+ " Chat");
							chat_num += 1;
							
						} else {
							if (chat_num > 1) {
									for (int k =0; k < i; k++) {
										String Input = data ;
										PrintStream writer = new PrintStream(socket_array[k].getOutputStream());
										writer.println(Input);
										writer.flush();
									}
								
							}else  {
								for (int k =0; k < i; k++) {
									if (socket_array[k].getRemoteSocketAddress().toString().equals(curr)) {
										String Input = "Others have not joined chat session" ;
										PrintStream writer = new PrintStream(socket_array[k].getOutputStream());
										writer.println(Input);
										writer.flush();
									}
								}
								
								
							}
						}
						
					}
					
					
					
					
				} catch (IOException e) {
					break;
				}
			} 
		}
	}
		
	
	
	public void go(int port, int max_client) {
		//client_ouput = new ArrayList();
		
		System.out.println("Server is running");
		int in = 0;
		try {
			ServerSocket server = new ServerSocket(port);
			while (true) {
				
				Socket temp = server.accept();
				System.out.println("Connection accepted");
				if (max_client > 0) {
					int k = in+1;
					System.out.println("Connection " + k + " created");
					socket_array[in] = temp;
					number_of_sockets = in;
					
					server_thread[k] = new Thread(new Read_Socket_thread(socket_array[in]));
					server_thread[k].start();
					in += 1;
					max_client -= 1;
					//System.out.println(max_client +" " + i);
				}else {
					System.out.println("Max number of clients exceeded.");
				}
				
			}
		} catch (IOException e) {
			
		}

	}
	
	
	public static void main(String[] args) {
		finder server_connect = new finder();
		socket_array = new Socket[100];
		server_thread = new Thread[100];
		server_connect.go(5000, 100);
		try {
			for (int j = 0; j <= Integer.parseInt(args[1])+1; j++) {
				server_thread[j].join();
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	

}
