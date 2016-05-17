import java.util.*;
import java.net.*;
import java.io.*;
import java.lang.*;

public class Server {

   public static void main(String[] args) {
      Server s = new Server();
   }
   
   private Map<String, MessageHandler> connections; //to hold all of the connecitons currently avalible for communication
   private Map<String, String> userIDs; //TODO hash the passwords so they are not exposed
   private Map<InetAddress, String> userNames;
   private ArrayList<Integer> usedPorts;
   private static int chatPort = 5678;
   private static int idPort = 9999;
   
   public Server() {
      usedPorts = new ArrayList<Integer>();
      usedPorts.add(new Integer(idPort));
      usedPorts.add(new Integer(chatPort));
      ServerSocket server = null;
      ServerSocket idSocket = null;
      this.connections = new HashMap<String, MessageHandler>(); //matches user names too messageHandlers so clients can request to chat with people by username instead of ip
      this.userIDs = new HashMap<String, String>();
      this.userNames = new HashMap<InetAddress, String>();
      userIDs.put("Eli", "Fonseca100");
      userIDs.put("Ravi", "Smith");
      Runnable authenticationThread = new WaitForConnection(true);
      Runnable chatAccepterThread = new WaitForConnection(false);
      new Thread(authenticationThread).start();
      new Thread(chatAccepterThread).start();

   }
   
   

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

      public void run() {
         while(true) {
            try {
               wait(server, auth);
            }
            catch(IOException e) {
               System.out.println("Error accepting connection from client");
            }
         }
      }

      private void wait(ServerSocket socket, boolean authentication) throws IOException {
         Socket connection = socket.accept(); 
         ObjectOutputStream output = new ObjectOutputStream(connection.getOutputStream());
         output.flush();
         Integer freePort = new Integer(getFreePort());
         try {
            output.writeObject(freePort); //sends the message to the server
            output.close();
         }
         catch(IOException e) {
            System.out.println("Error sending new connect port");
            e.printStackTrace();
         }
         socket = new ServerSocket(freePort.intValue());
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

      private int getFreePort() {
         int newPort = usedPorts.get(0);
         while(usedPorts.contains(new Integer(newPort))) {
            newPort = (int)(Math.random()*9000)+1028; //so we dont get any researved ports
            System.out.println("sutible port still not found");
         }
         System.out.println("found new port now");
         return newPort;
      }
   }
   
   private class IdHandler extends ConnectionHandler {
      
      public IdHandler(Socket clientSocket) {
         super(clientSocket);
      }  
      
      public void run() { //TODO this logic is broken whe the client closed the login in window we get a infinate loop instead of terminating the thread need to fix
         Message message = null;
         while(!Thread.currentThread().isInterrupted()) {
            if(this.clientHandler().isClosed() || this.clientHandler() == null) {
               Thread.currentThread().interrupt();
            }
            else {
               message = (Message) this.clientHandler().readMessage();   
               if(message != null) {  
                  Scanner lineScan = new Scanner(message.getMessage());
                  String userName = lineScan.next();
                  String password = lineScan.next();
                  lineScan.close();
                  String correctPassword = userIDs.get(userName); //eventually will have to unhash to password somehow
                  Message certification = null;
                  if(correctPassword != null && correctPassword.equals(password)) {
                     certification = new Message(userName);
                     userNames.put(this.sender(), userName);
                     this.clientHandler().send(certification); //if login in valid send the users username back to them
                     this.clientHandler().close();
                     Thread.currentThread().interrupt();
                  }
                  else {
                     this.clientHandler().send(certification);
                     
                  }
               } 
            }  
         }
      }
   }

   private class ChatHandler extends ConnectionHandler {
   
      private MessageHandler recipientHandler;
      private Queue<Message> messages;
      
      public ChatHandler(Socket clientSocket) {
         super(clientSocket);
      }
      
      public void run() {
         connections.put(userNames.get(this.sender()), this.clientHandler());
         System.out.println("Now connected to "+this.sender().getHostName());
      
         //read in the message object from the 
         //find the right MesasgeHandler in the HashMap for the recipient
         //then push the messages you recieve from the client to the recipient
         while(!Thread.currentThread().isInterrupted()) {
            if(this.clientHandler().isClosed() || this.clientHandler() == null) {
               Thread.currentThread().interrupt();
               System.out.println("chat thread eneded");
               break;
            }
            else {
               Message toBePushed = (Message) this.clientHandler().readMessage();
               if(toBePushed == null) {
                  Thread.currentThread().interrupt(); 
                  break;
               }  
               System.out.println(toBePushed.getMessage());      
               if(connections.get(toBePushed.getRecipient()) != null) {
                  recipientHandler = connections.get(toBePushed.getRecipient());
                  recipientHandler.send(toBePushed);
               }
               else {
                  this.clientHandler().send(new Message("Message failed to send, "+toBePushed.getRecipient()+" is not online"));
               }
            }
            //Thread.currentThread().sleep(500); //so this thread can yeild resurces to other threads that are running
         }
         connections.remove(this.sender()); //removes this clients connection from the serves map      
      }
   
   }
   
   
}