package socket4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client { // Thread구현.
	public static void main(String args[]) {
		try {
			String name = "["+args[0]+"]";
			Socket socket = new Socket("127.0.0.1", 9001);
			System.out.println("접속됨..");

			BufferedReader keyboard = 
					new BufferedReader(new InputStreamReader(System.in));

			InputStream in = socket.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			OutputStream out = socket.getOutputStream();
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(out));
			
			new ClientThread(socket, br).start();
			
			String message = null;
			while ((message = keyboard.readLine()) != null) {
				if (message.equals("quit")) break;
				pw.println(name+message);
				pw.flush();
				//String echoMessage = br.readLine(); 쓰레드에서 일 하고 있기 때문에 없어도 됨.
				//System.out.println(echoMessage);
				
			}			
			pw.close();
			br.close();
			socket.close();

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
}

class ClientThread extends Thread{
	Socket socket;
	BufferedReader br; //읽기.
	
	ClientThread() { }
	ClientThread(Socket socket, BufferedReader br) { 
		this.socket = socket;
		this.br = br;
	}
	
	@Override
	public void run() {		
		try {
			String msg = null;
			while((msg = br.readLine()) != null){
				System.out.println(msg);
			}
		} catch (Exception e) {
			
		} finally {
			
		}
	}
	
}