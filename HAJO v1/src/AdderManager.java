import java.io.*;
import java.net.*;

/**
 * @author Jussi Wallin, Antti Auranen, Niklas Luomala
 * 
 */
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
	
	//the shared data structure
	static AdderSharedData data;

	
	
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
				if (checkAvailable()) {
					adderCount = readInt();
					System.out.println("Requested adder count: " + adderCount);
					retry = false;
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
					sendInt(-1);
				quit();
			}
		}
		
		//create new shared data structure for the requested adder count
		data = new AdderSharedData(adderCount);
		
		//generate adder ports
		adderPorts = new int[adderCount];
		for (int i = 0; i < adderCount; i++) {
			adderPorts[i] = adderStartPort + i;
		}
		
		//generate adders
		adders = new AdderThread[adderCount];
		for(int i = 0; i < adderCount; i++) {
			adders[i] = new AdderThread(adderPorts[i], adderStartPort, data);
			adders[i].setName("Adder-" + i);
			adders[i].start();
		}
		
		//give port numbers to the workdistributor
			for (int i = 0; i < adderCount; i++)
				sendInt(adderPorts[i]);
		
		long lastOpTime = System.currentTimeMillis();
		boolean run = true;
		while (run) {
			//check for current operations
				if (checkAvailable()) {
					switch (readInt()) {
					
					//work distributor has requested  a stop to all adders
					case 0:
						run = false;
						break;
						
					//work distributor has requested total sum
					case 1:
						sendInt(data.getTotalSum());
						break;
						
					//work distributor has requested the adder with greatest sum
					case 2:
						int biggestIndex = 0;
						int biggest = data.getAdderDataSum(0);
						for (int i = 1; i < adderCount; i++) {
							int num = data.getAdderDataSum(i);
							if (num > biggest) {
								biggestIndex = i;
								biggest = num;
							}
							
							sendInt(biggestIndex);
						}
						break;
						
					//work distributor has requested the total count of numbers
					case 3:
						sendInt(data.getTotalNumberCount());
						break;
					default:
						sendInt(-1);
					
					}
					lastOpTime = System.currentTimeMillis();
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
	
	/**
	 * sends the integer through the TCP socket created earlier
	 * @param i
	 */
	private static void sendInt(int i) {
		try {
			out.writeInt(i);
			out.flush();
		} catch (IOException e) {
			System.out.println("Exception while sending an integer");
			e.printStackTrace();
			quit();
		}
	}
	
	/**
	 * reads the next integer from the TCP socket
	 * @return
	 */
	private static int readInt() {
		try {
			return in.readInt();
		} catch (IOException e) {
			System.out.println("Exception while reading an integer");
			e.printStackTrace();
			quit();
		}
		return 0;
	}
	
	/**
	 * checks availability of input from the TCP socket
	 * @return 
	 */
	private static boolean checkAvailable() {
		try {
			
			if (in.available() > 0)
				return true;
			
		} catch (IOException e) {
			System.out.println("Exception while checking availability");
			e.printStackTrace();
			quit();
		}
		return false;
	}

	
	
	/**
	 * closes all connections and adder threads
	 */
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
