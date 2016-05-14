import java.io.*;
import java.net.*;


public abstract class ConnectionHandler implements Runnable {
   
   private final InetAddress sender;
   private final MessageHandler clientHandler;

   public ConnectionHandler(Socket clientSocket) {
         this.clientHandler = new MessageHandler(clientSocket);
         this.sender = clientSocket.getInetAddress();      
   }
   
   public InetAddress sender() {
      return this.sender;
   }
   
   public MessageHandler clientHandler() {
      return this.clientHandler;
   }
}