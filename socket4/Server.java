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

public class Server { // Thread구현.
	public static void main(String args[]) {
		ServerSocket server = null;
		List<Socket> list = new Vector<>();
		//List<EchoThread> list2 = new Vector<>(); 쓰레드를 통채로 관리 가능.
		
		
		try {
			server = new ServerSocket(9001);
			System.out.println("클라이언트의 접속을 대기중");
			while (true) {
				Socket socket = server.accept(); 
				list.add(socket); //socket의 정보가 list에 들어감.
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
	List<Socket> list; //리스트 공유.
	Socket socket;

	public serverThread() { }
	public serverThread(Socket socket, List<Socket> list) { //list정보를 알아야 브로드캐스트 가능.
		this.socket = socket;
		this.list = list;
	}

	@Override
	public void run() {
		InetAddress address = socket.getInetAddress();
		System.out.println(address.getHostAddress() + " 로부터 접속했습니다.");

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
				System.out.println(socket + " : 접속해제");
				System.out.println(list);
				if(socket != null) {
					socket.close();
				}
			} catch (IOException e) {				
				e.printStackTrace();
			}
			
		}
	}
	//synchronized(동기화) 처리 해줌으로 메세지가 엉키지않음.
	public synchronized void broadcast(String msg) throws IOException{ //run메소드와는 별개인 메소드. 연결된 모든 소켓을 다 뿌려줘야함.
		for(Socket socket:list){
			OutputStream out = socket.getOutputStream();
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(out));
			pw.println(msg);
			pw.flush();
		}
	}
}
