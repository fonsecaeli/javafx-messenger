// Eli F.
// Section: C
// Final Project
// Description: abstract upper class for a handler to a connection made to the server clas 
// Class name: ConnectionHandler
// Version 1.0
// 5/22/16

import java.io.*;
import java.net.*;


public abstract class ConnectionHandler implements Runnable {
   
   private final InetAddress sender;
   private final MessageHandler clientHandler;

   /**
    * constructor for ConnectionHandler
    * @param  clientSocket the socket that connects client and server
    */
   public ConnectionHandler(Socket clientSocket) {
         this.clientHandler = new MessageHandler(clientSocket);
         this.sender = clientSocket.getInetAddress();      
   }
   
   /**
    * access method for the senders ip address
    * 
    * @return the clients ip address
    */
   public InetAddress sender() {
      return this.sender;
   }
   
   /**
    * accesser method for the message handler for this connection
    * 
    * @return the message handler for the connection being handled by the server
    */
   public MessageHandler clientHandler() {
      return this.clientHandler;
   }
}