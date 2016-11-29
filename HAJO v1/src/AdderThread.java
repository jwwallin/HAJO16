import java.net.*;
import java.io.*;

/**
 * @author Jussi Wallin
 *
 */
public class AdderThread extends Thread {
	
	public int port;
	public int startPort;
	boolean stop = false;

	public AdderThread(int port, int startPort) {
		this.port = port;
		this.startPort = startPort;
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
