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

		// create byte array stream for output
		final ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
		
		//define socket variables
		DatagramSocket datagramSock = null;
		ServerSocket tcpSock = null;

		InetAddress addr;

		// tcp socket timeout for connection
		int tcpTimeout = 0;

		try {
			// create new outputstream for
			out = new ObjectOutputStream(byteArrayOut);

			// prepare TCP socket for usage with ObjectInput and -Output streams
			tcpSock = new ServerSocket(myTcpPort);
			tcpSock.setSoTimeout(5000); // 5 second timeout

			// create local datagram socket and connect to remote
			datagramSock = new DatagramSocket(15555);
			addr = InetAddress.getByName(remoteName);
			datagramSock.connect(addr, remoteDatagramPort);

			// create packet and send local TCP-port number to WorkDistributor
			byte[] buff = Integer.toString(myTcpPort).getBytes();
			DatagramPacket p = new DatagramPacket(buff, buff.length);
			datagramSock.send(p);

			do{
				try {
					// wait for TCP connection
					tcpSock.accept();
					System.out.println("Connection accepted");
					
				} catch (SocketTimeoutException e) {
					System.out.println("TCP timeout");
					tcpTimeout++;
					if (tcpTimeout == timeoutLimit) {
						System.out.println("Timeout limit reached");
						System.exit(0);
					}
				} //try
				
			} while (true);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		} //try
		
		
		

		if (datagramSock != null)
			datagramSock.disconnect();
		if (tcpSock != null)
			try {
				tcpSock.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

	}

}
