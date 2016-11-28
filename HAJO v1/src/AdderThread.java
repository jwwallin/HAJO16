import java.net.*;
import java.io.*;

public class AdderThread extends Thread {
	
	public static int port;

	public AdderThread(int port) {
		this.port = port;
	}
	
	public void run() {
		try {
			ServerSocket tcpSockServ = new ServerSocket(port);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
