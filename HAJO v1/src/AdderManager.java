import java.io.*;
import java.net.*;

public class AdderManager {

	public static void main(String[] args) {
		ObjectInputStream in;
		DatagramSocket s;

		InetAddress addr;
		byte[] addrBytes = {(byte) 127, (byte) 0, (byte) 0, (byte) 1};
		
		try {

			s = new DatagramSocket();
			addr = InetAddress.getByAddress(addrBytes);
			byte[] buff = { (byte) 15555 };
			DatagramPacket p = new DatagramPacket(buff, buff.length);
			s.connect(addr, 15554);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
