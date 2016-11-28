import java.net.*;
import java.io.*;

public class AdderThread extends Thread {
	
	public static int port;

	public AdderThread(int port) {
		this.port = port;
	}
	
	public void run() {

		Socket tcpSock = null;
		try {
			ServerSocket tcpSockServ = new ServerSocket(port);
			tcpSockServ.setSoTimeout(10000); //10 sec timeout
			tcpSock = tcpSockServ.accept();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (tcpSock != null) {
			//TODO everything
		}
	}

}
