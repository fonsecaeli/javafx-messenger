import java.io.*;
import java.net.*;

public class ClientTest {
	public static void main(String[] args) throws IOException {
		String serverIP = "127.0.0.1";
      int port = 5678;
		MessageHandler mh = new MessageHandler(serverIP, port);
      mh.send("Test");
      do {
      	mh.send("test");
      }
      while(true); //TODO
	}
}