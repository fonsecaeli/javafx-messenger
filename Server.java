// Eli F.
// Section: C
// Final Project
// Description: class that handles all of the logic of the server that handles requests of many clients who want to chat
// Class name: Server
// Version 1.0
// 5/22/16

import java.util.*;
import java.net.*;
import java.io.*;

public class Server {

   /**
    * entry point into the program where everything starts
    * just creates a new server to listen for requests
    * 
    * @param args input from the console
    */
   public static void main(String[] args) {
      Server s = new Server();
   }
   
   private Map<String, MessageHandler> connections; //to hold all of the connecitons currently avalible for communication
   private Map<String, String> userIDs; //TODO hash the passwords so they are not exposed
   private Map<InetAddress, String> userNames; //should not allow too clients to sign in with the same account because it causes issues because only one of them will recieve messages sent to that account
   private Map<String, Queue<Message>> unreadMessages;
   private ArrayList<Integer> usedPorts;
   private static int chatPort = 5678;
   private static int idPort = 9999;
   
   /**
    * constructor for a server object
    *
    * basically just hogs 2 thread and waits for connections and then spins threads off to handle those connections
    */
   public Server() {
      usedPorts = new ArrayList<Integer>();
      usedPorts.add(new Integer(idPort));
      usedPorts.add(new Integer(chatPort));
      ServerSocket server = null;
      ServerSocket idSocket = null;
      this.connections = new HashMap<>(); //matches user names too messageHandlers so clients can request to chat with people by username instead of ip
      this.userIDs = new HashMap<>();
      this.userNames = new HashMap<>();
      //naive approach to have a list of registered users
      //some other program needs to be written to handle new users who want to sign up for an account
      userIDs.put("Eli", "F");
      userIDs.put("Ravi", "Smith");
      userIDs.put("Andrew", "Andrew");
      userIDs.put("T","C");
      userIDs.put("David", "S");
      //spins new threads off to handle waiting for connections
      Runnable authenticationThread = new WaitForConnection(true);
      Runnable chatAccepterThread = new WaitForConnection(false);
      new Thread(authenticationThread).start();
      new Thread(chatAccepterThread).start();

   }
   
   
   /**
    * class to handle waiting for a connection, either will wait for people trying to 
    * authenticate, or for people who want to start a new chat
    */
   private class WaitForConnection implements Runnable {
      private ServerSocket server;
      private boolean auth;

      public WaitForConnection(boolean auth) {
         this.auth = auth;
         try {
            if(auth) {
               this.server = new ServerSocket(idPort);
               System.out.println("id connected");
            }
            else {
               this.server = new ServerSocket(chatPort);
               System.out.println("chat connected");
            }
         }
         catch(IOException e) {
            System.out.println("Error binding to port");
            e.printStackTrace();
         }
      }

      /**
       * waits for a connection to be made
       */
      public void run() {
         while(true) {
            try {
               wait(server, auth);
            }
            catch(IOException e) {
               System.out.println("Error accepting connection from client");
               e.printStackTrace();
            }
            catch(Exception e) {
               e.printStackTrace();
            }
         }
      }

      /**
       * handles the my protocal for accepting new connections to the server
       * since my servesockets are binding on only 2 ports once one accpets a connection then the port 
       * it was listening on is now taken.  so my program sends a new port number to the client and then closes the connection
       * then the server will open a new socketserver on the new port and listen for new conneciton.  
       * client will connect and we will still have the original two dedicated ports listening for new connection free
       * 
       * @param  socket         socket for intial conneciton
       * @param  authentication if this method is being called to wait for authentication or chat purposes
       * @throws IOException  
       */
      private void wait(ServerSocket socket, boolean authentication) throws IOException {
         Socket connection = socket.accept(); 
         ObjectOutputStream output = new ObjectOutputStream(connection.getOutputStream());
         output.flush();
         Integer freePort = new Integer(getFreePort());
         try {
            output.writeObject(freePort); //sends the message to the client
            output.close();
         }
         catch(IOException e) {
            System.out.println("Error sending new connect port");
            e.printStackTrace();
         }
         socket = new ServerSocket(freePort.intValue());
         usedPorts.add(freePort);
         connection = socket.accept();
         Runnable handler;
         if(authentication) {
            handler = new IdHandler(connection);   
         }
         else {
            handler = new ChatHandler(connection);
         }
         new Thread(handler).start();
         System.out.println("new thread to handle connection created");

      }

      /**
       * gets a free port within range of ports that are not reserved for other programs
       * 
       * @return a free port
       */
      private int getFreePort() {
         int minPort = 1028;
         int maxPort = 9000;
         int newPort = (int)(Math.random()*maxPort)+minPort;
         while(usedPorts.contains(new Integer(newPort))) {
            newPort = (int)(Math.random()*maxPort)+minPort; //so we dont get any researved ports
            System.out.println("sutible port still not found");
         }
         System.out.println("found new port now");
         return newPort;
      }
   }
   
   /**
    * class to handle users trying to authenticate to server
    */
   private class IdHandler extends ConnectionHandler {
      
      /**
       * constructor for IdHandler
       * @param  clientSocket the socket through the conneciton is
       */
      public IdHandler(Socket clientSocket) {
         super(clientSocket);
      }  
      
      /**
       * waits for people to authenticate to the server
       */
      public void run() { 
         System.out.println("waiting to authenticate");
         while(!Thread.currentThread().isInterrupted()) {
            if(this.clientHandler().isClosed() || this.clientHandler() == null) {
               Thread.currentThread().interrupt();
            }
            else {
               Message message = null;
               try {
                  message = (Message) this.clientHandler().readMessage();  
                  System.out.println(message.getMessage());
               }  
               catch(SocketException e) {
                  e.printStackTrace();
                  System.out.println("client has terminated connection");
                  Thread.currentThread().interrupt();
                  break;
               }
               catch(NullPointerException e) {
                  Thread.currentThread().interrupt();
                  break;
               }
               Scanner lineScan = new Scanner(message.getMessage());
               String userName = lineScan.next();
               String password = lineScan.next();
               lineScan.close();
               String correctPassword = userIDs.get(userName); //eventually will have to unhash to password somehow
               Message certification = null;
               if(userNames.get(this.sender()) == null && correctPassword != null && correctPassword.equals(password)) {
                  certification = new Message(userName);
                  userNames.put(this.sender(), userName);
                  this.clientHandler().send(certification); //if login in valid send the users username back to them
                  this.clientHandler().close();
                  System.out.println("authorized");
                  Thread.currentThread().interrupt();
                  break;
               }
               else if(userNames.get(this.sender()) != null) {
                  certification = new Message(userNames.get(this.sender())+" is currently signed in");
                  this.clientHandler().send(certification);
                  System.out.println("access denied");
               }
               else {
                  this.clientHandler().send(new Message("Incorrect password or username"));
                  
               }
               //userNames.remove(this.sender());
               //System.out.println("ended1");

            }  
         }
      }
   }

   /**
    * handler for people trying to connect to server to chat with someone else
    */
   private class ChatHandler extends ConnectionHandler {
   
      private MessageHandler recipientHandler;
      
      /**
       * constructor for ChatHandler
       * 
       * @param clientSocket the socket by which the clinet is connected to the server
       */
      public ChatHandler(Socket clientSocket) {
         super(clientSocket);
      }
      
      /**
       * waits for people to send messeges, will take these messeges and push them to the intended recipient 
       * if they are onlineif they are not online will put the messages in a qeue to be sent once the user comes online
       */
      public void run() {
         connections.put(userNames.get(this.sender()), this.clientHandler());
         /*System.out.println("Now connected to "+this.sender().getHostName());
         if(unreadMessages.get(this.sender()) != null) {
            Queue<Message> messages = unrmessages.get(this.sender());
            for(int i = 0; i < unrmessages.size(); i++) {
               if(!this.clientHandler().isClosed() && this.clientHandler() != null) {
                  this.clientHandler().send(unrmessages.poll());
               }
            }
         }*/
      
         //read in the message object from the 
         //find the right MesasgeHandler in the HashMap for the recipient
         //then push the messages you recieve from the client to the recipient
         while(!Thread.currentThread().isInterrupted()) {
            System.out.println(connections);
            if(this.clientHandler().isClosed() || this.clientHandler() == null) {
               Thread.currentThread().interrupt();
               System.out.println("chat thread eneded");
               break;
            }
            else {
               Message toBePushed = null;
               try {
                  toBePushed = (Message) this.clientHandler().readMessage();
               }
               catch(SocketException e) {
                  System.out.println("client terminated connection");
                  Thread.currentThread().interrupt();
                  break;
               }
               if(toBePushed == null) {
                  System.out.println("stuff messed up");
                  Thread.currentThread().interrupt(); 
                  break;
               } 
               System.out.println(toBePushed.getMessage());      
               if(connections.get(toBePushed.getRecipient()) != null) {
                  recipientHandler = connections.get(toBePushed.getRecipient());
                  recipientHandler.send(toBePushed);
                  System.out.println("message sent");
               }
               else {
                  this.clientHandler().send(new Message("Message failed to send, "+toBePushed.getRecipient()+" is not online"));
                  System.out.println("message failed to send");
                  //System.out.println(connections);
                  /*try {
                     Queue<Message> messages = new LinkedList<Message>();
                     messages.add(toBePushed);
                     unreadMessages.put(toBePushed.getRecipient(), messages);
                  }
                  catch(Exception e) {
                     unreadMessages.get(toBePushed.getRecipient()).add(toBePushed);
                  }*/
               }
            }
            //Thread.currentThread().sleep(500); //so this thread can yeild resurces to other threads that are running
         }
         removeConnections();

      }

      private void removeConnections() {
         connections.remove(userNames.get(this.sender())); //removes this clients connection from the serves map 
         userNames.remove(this.sender());
         usedPorts.remove(this.clientHandler().getPort());
         System.out.println("ended2");
      }

   
   }
   
   
}