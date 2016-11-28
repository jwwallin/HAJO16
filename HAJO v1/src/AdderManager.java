import java.io.*;
import java.net.*;

public class AdderManager {
	
	// address and port number definitions
	public static String remoteName = "localhost";
	public static int remoteDatagramPort = 3126;
	public static int localDatagramPort = 15554;
	public static int myTcpPort = 15555;
	public static int adderStartPort = 15556; //adder ports are generated in increasing order beginning at adderStartPort
	
	//timeout count limit for TCP connection establishing
	public static int timeoutLimit = 5;
	
	// object streams for IO
	static ObjectInputStream in = null;
	static ObjectOutputStream out = null;

	//define socket variables
	static DatagramSocket datagramSock = null;
	static ServerSocket tcpSockServ = null;
	static Socket tcpSock = null;
	
	//adders
	static int[] adderPorts;
	static AdderThread[] adders;

	public static void main(String[] args) {
		//remote address
		InetAddress addr;

		// tcp socket timeout for connection creation
		int tcpTimeout = 0;

		try {
			// prepare TCP socket for usage with ObjectInput and -Output streams
			tcpSockServ = new ServerSocket(myTcpPort);
			tcpSockServ.setSoTimeout(5000); // 5 second timeout

			// create local datagram socket and connect to remote
			datagramSock = new DatagramSocket(localDatagramPort);
			addr = InetAddress.getByName(remoteName);
			datagramSock.connect(addr, remoteDatagramPort);

			// create packet to send local TCP-port number to WorkDistributor
			byte[] buff = Integer.toString(myTcpPort).getBytes();
			DatagramPacket p = new DatagramPacket(buff, buff.length);
			
			boolean retry = true;
			do{
				try {
					//send the packet
					datagramSock.send(p);
					
					// wait for TCP connection
					tcpSock = tcpSockServ.accept();
					System.out.println("Connection accepted");
					
				} catch (SocketTimeoutException e) {
					System.out.println("TCP timeout");
					tcpTimeout++;
					if (tcpTimeout == timeoutLimit) {
						System.out.println("Timeout limit reached");
						quit();
					}
				}
				
				// make sure connection was successfully created
				if (tcpSock == null || !tcpSock.isConnected()) {
					System.out.println("Connection failed");
				} else {
					retry = false;
				}
				
			} while (retry);
			
			
		} catch (IOException e) {
			e.printStackTrace();
			quit();
		}

		
		try {
			//instantiate input and output streams
			in = new ObjectInputStream(tcpSock.getInputStream());
			out = new ObjectOutputStream(tcpSock.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//fetch adder count
		boolean retry = true;
		int retryCount = 0;
		int adderCount = 0;
		while (retry) {
			
			//read adder count sent by workdistributor
			try {
				if (in.available() > 0) {
					adderCount = in.readInt();
					System.out.println("Requested adder count: " + adderCount);
					retry = false;
				}
			} catch (IOException e) {
				e.printStackTrace();
			} 
			
			//wait 
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
				retry = false;
			}
			
			//check for retries
			retryCount++;
			if (retryCount >= 50) {
				try {
					out.writeInt(-1);
					out.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
				quit();
			}
		}
		
		//generate adder ports
		adderPorts = new int[adderCount];
		for (int i = 0; i < adderCount; i++) {
			adderPorts[i] = adderStartPort + i;
		}
		
		//generate adders
		adders = new AdderThread[adderCount];
		for(int i = 0; i < adderCount; i++) {
			adders[i] = new AdderThread(adderPorts[i]);
			adders[i].start();
		}
		
		//give port numbers to the workdistributor
		try {
			for (int i = 0; i < adderCount; i++) {
				out.writeInt(adderPorts[i]);
				out.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
			quit();
		}
		
		long lastOpTime = System.currentTimeMillis();
		boolean run = true;
		while (run) {
			//check for current operations
			try {
				if (in.available() > 0) {
					switch (in.readInt()) {
					case 0:
						run = false;
						break;
					case 1:
						break;
					case 2:
						break;
					case 3:
						break;
					default:
						out.writeInt(-1);
						out.flush();
					
					}
					lastOpTime = System.currentTimeMillis();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			
			//if no operations by workdistributor in 1 minute
			if(System.currentTimeMillis() - lastOpTime > 60000) {
				quit();
			}
			
			// wait for 1 second
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				quit();
			}
		}
		
		quit();
	}

	private static void quit() {
		
		//stop adders if they are running
		if (adders != null) {
			for (AdderThread adder : adders) {
				System.out.println("Stopping adder");
				if (adder != null && adder.isAlive())
					adder.requestStop();
			}
		}
		
		// close all open streams
		if (in != null)
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		if (out != null)
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		// close all open sockets
		if (datagramSock != null)
			datagramSock.disconnect();

		if (tcpSockServ != null)
			try {
				tcpSockServ.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		if (tcpSock != null)
			try {
				tcpSock.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		System.out.println("Quitting AdderManager");
		System.exit(0);
	}

}
