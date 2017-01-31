import java.net.*;
import java.io.*;

/**
 * @author Jussi Wallin
 *
 */
public class AdderThread extends Thread {
	
	//port number and the first (0th) adder port number
	public int port;
	public int startPort;
	
	//boolean for the manager to be able to request a stop 
	boolean stop = false;
	
	// shared data structure
	AdderSharedData data;
	
	// object streams for IO
	static ObjectInputStream in = null;

	
	public AdderThread(int port, int startPort, AdderSharedData data) {
		this.port = port;
		this.startPort = startPort;
		this.data = data;
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
		
		if (tcpSock != null || tcpSock.isConnected()) {
			
			try {
				//instantiate input and output streams
				in = new ObjectInputStream(tcpSock.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
				stop = true;
			}
			
			// main adder loop
			while(!stop) {
				
				try {
					//read if available
					if (in.available() > 0) {
						int curr = in.readInt();
						switch (curr) {
						//integer read is zero => stop this adder
						case 0:
							stop = true;
							break;
						// else add to the value of this adder
						default:
							data.increaseAdderDataSum(port-startPort, curr);
						}
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
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
