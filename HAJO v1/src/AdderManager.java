import java.io.*;
import java.net.*;

public class AdderManager {

	public static void main(String[] args) {
		ObjectInputStream in = null;
		DatagramSocket s = null;

		InetAddress addr;
		byte[] addrBytes = {(byte) 127, (byte) 0, (byte) 0, (byte) 1};
		
		try {

			s = new DatagramSocket(15555);
			addr = InetAddress.getByAddress(addrBytes);
			byte[] buff = { (byte) 1024 };
			DatagramPacket p = new DatagramPacket(buff, buff.length);
			s.connect(addr, 15554);
			s.send(p);
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		
		
		if (s != null)
			s.disconnect();
		
	}

}
