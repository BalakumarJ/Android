
import java.net.*;
import java.io.*;
import java.util.*;

public class Server {
	
	public String Input = new String();
	Scanner scan = new Scanner(System.in);
	public static Socket[] socket_array;
	public static Thread[] server_thread;
	public int number_of_sockets;
		
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
				} catch (IOException e) {
					break;
				}
			} 
		}
	}
		
	public class Write_Socket_thread implements Runnable {
		
		BufferedReader reader;
		Socket socket;
		
		public Write_Socket_thread(int number) {
			
			
		}
	
			public void run() {
				System.out.println("Thread created for connection");
				
				while (true) {
					try {	
						Input = scan.nextLine();
						for (int trial = 0; trial <= number_of_sockets; trial++) {
							PrintWriter writer = new PrintWriter(socket_array[trial].getOutputStream());
							writer.println(Input);
							writer.flush();
						}
					} catch (Exception e) {
						//e.printStackTrace();
					}
				}				
		}
	}
	
	
	public void go(int port, int max_client) {
		//client_ouput = new ArrayList();
		
		System.out.println("Server is running");
		int i = 0;
		try {
			ServerSocket server = new ServerSocket(port);
			while (true) {
				
				Socket temp = server.accept();
				System.out.println("Connection accepted");
				if (max_client > 0) {
					int k = i+1;
					System.out.println("Connection " + k + " created");
					socket_array[i] = temp;
					number_of_sockets = i;
					if (i == 0) {
						server_thread[i] = new Thread(new Write_Socket_thread(number_of_sockets));
						server_thread[i].start();
					}
					server_thread[k] = new Thread(new Read_Socket_thread(socket_array[i]));
					server_thread[k].start();
					i += 1;
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
		Server server_connect = new Server();
		socket_array = new Socket[Integer.parseInt(args[1])+1];
		server_thread = new Thread[Integer.parseInt(args[1])+1];
		server_connect.go(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
		try {
			for (int j = 0; j <= Integer.parseInt(args[1])+1; j++) {
				server_thread[j].join();
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	

}



