import java.io.*;
import java.net.*;

public class ServerTest {
	public static void main(String[] args) throws IOException {
		String clientSentence;          
		String capitalizedSentence;          
		ServerSocket welcomeSocket = new ServerSocket(5678); 
		Socket connectionSocket = welcomeSocket.accept();
		MessageHandler mh = new MessageHandler(connectionSocket);
      System.out.println("Server connected to client");
		while(true) {             	
         try {
            System.out.println(mh.readMessage()); 
         }
         catch(Exception e) {
         	System.out.println("oops");
         }    
		} 
	}
}