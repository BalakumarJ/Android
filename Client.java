
import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
	
	public class read implements Runnable {
		Socket client;
		public read(Socket client) {
			this.client = client;
		}
		
		public void run() {
			Scanner scan = new Scanner(System.in);
			
			while (true) {
				String Input = scan.nextLine();
				if (Input.equals("\\disconnect")) {
					try {
						client.close();
						break;
					} catch (IOException e){
						e.printStackTrace();
					}
					System.out.println("Connection Terminated");
				}else {
					try {
						PrintWriter writer = new PrintWriter(client.getOutputStream());
						writer.println(Input);
						writer.flush();
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
		
		public void run() {
			while (true) {
				try {
					InputStreamReader reader = new InputStreamReader(client.getInputStream());
					BufferedReader read = new BufferedReader(reader);
					String data = read.readLine();
					if (data != null) {
						System.out.println("Server message: " + data);
					}
				} catch (IOException e) {
					break;
				}
			}
			
		}
	}

	public void go(String ip, int port) {
		
		try {
			Socket client = new Socket(ip,port);
			System.out.println("Type \\disconnect to exit connection");
			Thread read_thrd = new Thread(new read(client));
			read_thrd.start();
			Thread write_thrd = new Thread(new write(client));
			write_thrd.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
	}
	public static void main(String[] args) {
		
		Client client = new Client();
		client.go(args[0], Integer.parseInt(args[1]));

	}

}
