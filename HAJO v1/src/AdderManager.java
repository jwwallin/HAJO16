import java.io.*;
import java.net.*;

public class AdderManager {

	public static String remoteName = "localhost";
	public static int remoteDatagramPort = 3126;
	public static int myTcpPort = 15554;
	public static int adderStartPort = 15549;

	public static int timeoutLimit = 5;

	public static void main(String[] args) {
		// object streams for io
		ObjectInputStream in = null;
		ObjectOutputStream out = null;
		
		//define socket variables
		DatagramSocket datagramSock = null;
		ServerSocket tcpSockServ = null;
		Socket tcpSock = null;

		InetAddress addr;

		// tcp socket timeout for connection
		int tcpTimeout = 0;

		try {
			// prepare TCP socket for usage with ObjectInput and -Output streams
			tcpSockServ = new ServerSocket(myTcpPort);
			tcpSockServ.setSoTimeout(5000); // 5 second timeout

			// create local datagram socket and connect to remote
			datagramSock = new DatagramSocket(15555);
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
						System.exit(0);
					}
				} //try

				if (tcpSock == null || !tcpSock.isConnected()) {
					System.out.println("Connection failed");
				} else {
					retry = false;
				}
				
			} while (retry);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		} //try

		
		try {
			//instantiate input and output streams
			in = new ObjectInputStream(tcpSock.getInputStream());
			out = new ObjectOutputStream(tcpSock.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//fetch adder count
		int adderCount = 0;
		try {
			if (in.available() > 0)
				adderCount = in.readInt();
				System.out.println("Requested adder count: " + adderCount);
		} catch (IOException e) {
			e.printStackTrace();
		}
		

		boolean run = true;
		while (run) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				run = false;
			}
		}


		// close all open sockets
		if (datagramSock != null)
			datagramSock.disconnect();
		
		if (tcpSockServ != null)
			try {
				tcpSockServ.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
		if (tcpSock != null)
			try {
				tcpSock.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

	}

}
