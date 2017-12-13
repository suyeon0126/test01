package socket4;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

public class Server { // Thread����.
	public static void main(String args[]) {
		ServerSocket server = null;
		List<Socket> list = new Vector<>();
		//List<EchoThread> list2 = new Vector<>(); �����带 ��ä�� ���� ����.
		
		
		try {
			server = new ServerSocket(9001);
			System.out.println("Ŭ���̾�Ʈ�� ������ �����");
			while (true) {
				Socket socket = server.accept(); 
				list.add(socket); //socket�� ������ list�� ��.
				new serverThread(socket, list).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (server != null) {
					server.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

class serverThread extends Thread {
	List<Socket> list; //����Ʈ ����.
	Socket socket;

	public serverThread() { }
	public serverThread(Socket socket, List<Socket> list) { //list������ �˾ƾ� ��ε�ĳ��Ʈ ����.
		this.socket = socket;
		this.list = list;
	}

	@Override
	public void run() {
		InetAddress address = socket.getInetAddress();
		System.out.println(address.getHostAddress() + " �κ��� �����߽��ϴ�.");

		try {
			InputStream in = socket.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));			

			String message = null;
			while ((message = br.readLine()) != null) {				
				broadcast(message);
			}
			br.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				list.remove(socket);
				System.out.println(socket + " : ��������");
				System.out.println(list);
				if(socket != null) {
					socket.close();
				}
			} catch (IOException e) {				
				e.printStackTrace();
			}
			
		}
	}
	//synchronized(����ȭ) ó�� �������� �޼����� ��Ű������.
	public synchronized void broadcast(String msg) throws IOException{ //run�޼ҵ�ʹ� ������ �޼ҵ�. ����� ��� ������ �� �ѷ������.
		for(Socket socket:list){
			OutputStream out = socket.getOutputStream();
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(out));
			pw.println(msg);
			pw.flush();
		}
	}
}
