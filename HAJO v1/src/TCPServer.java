import java.io.*;
import java.net.*;

//idea kopsattu stackoverflowsta. Pitää testailla ja säätää et sopii meijän ohjelmaan
public class TCPServer{
	public TCPServer() throws IOException {
		new Thread(new Runnable() {
			public void run() {
				try{
					ServerSocket serverS = new ServerSocket(1234);
					Socket clientS = serverS.accept();
					long startTime = System.currentTimeMillis();
					int read;
					int totalRead = 0;
					InputStream clientInputStream = clientS.getInputStream();
					while ((read = clientInputStream.read(buffer)) != -1){
						totalRead += read;
					}
					long endTime = System.currentTimeMillis();
					System.out.println(totalRead + " bittiä " + (endTime - startTime) + " millisekunnissa.");

				}catch (IOException e){
				}
			}
		}).start();

		//kuuntelevan threadin toteutus samalla tavalla
	}
}