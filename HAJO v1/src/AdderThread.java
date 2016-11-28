import java.net.*;
import java.io.*;

public class AdderThread extends Thread {
	
	public int port;
	boolean stop = false;

	public AdderThread(int port) {
		this.port = port;
	}
	
	public void run() {

		Socket tcpSock = null;
		try {
			ServerSocket tcpSockServ = new ServerSocket(port);
			tcpSockServ.setSoTimeout(10000); //10 sec timeout
			tcpSock = tcpSockServ.accept();
			tcpSockServ.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (tcpSock != null) {
			//TODO everything
			while(!stop) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void requestStop() {
		stop = true;
	}

}
