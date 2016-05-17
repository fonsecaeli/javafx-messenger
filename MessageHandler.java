import java.net.*;
import java.util.*;
import java.io.*;


public class MessageHandler {

   private ObjectOutputStream output;
   private ObjectInputStream input;
   private String connectIP;
   private Socket connection;
   private int port;
   
   
   //use to construct messagehandler for a client program, will aoutmaticaly initiat connection 
   //to the server where messages are processed and then sent out to recipients
   public MessageHandler(String serverIP, int port) {
      this.connectIP = serverIP;
      this.port = port;
      connectToServer();
      setupStreams();
   }
      
   //used for server side action since in that case we will already have a socket open and ready
   public MessageHandler(Socket connection) {
      this.connection = connection;
      setupStreams();
   }
   
   
   //connect to a server
   public void connectToServer() {
      try {
         int newPort = 0;
         Socket con = new Socket(InetAddress.getByName(connectIP), port);
         ObjectInputStream in = new ObjectInputStream(con.getInputStream());
         try {
            Integer port = (Integer) in.readObject();
            newPort = port.intValue();
         }
         catch(ClassNotFoundException e) {
            System.out.println("Unknown object sent along stream");
         }
         catch(IOException e) {
            System.out.println("input stream is null");
         }
         con.close();
         if(newPort == 0) {
            System.out.println("Error getting port number for connection");
            System.exit(1);
         }
         connection = new Socket(InetAddress.getByName(connectIP), newPort);
         InetAddress inetAddress = connection.getInetAddress();

      }
      catch(IOException e) {
         System.out.println("Error connecting to server");
         e.printStackTrace();
         System.exit(1);
      }
   
   }
   
   public boolean isClosed() {
      return connection.isClosed() || input == null;
   }

   //setting up streams for the client 
   public void setupStreams() {
      try {
         if(connection == null) {
            System.out.println("Connection null");
         }
         output = new ObjectOutputStream(connection.getOutputStream());
         output.flush();
         input = new ObjectInputStream(connection.getInputStream());
      }
      catch(IOException e) {
         System.out.println("Error setting up streams");
      }
   }
   
   //allows classes using this object to readMessages send
   public Object readMessage() {
      Object message = null;
      try {
         message = input.readObject(); 
      }
      catch(ClassNotFoundException e) {
         System.out.println("Unknown object sent along stream");
      }
      catch(IOException e) {
         System.out.println("input stream is null");
         //e.printStackTrace();
      }
      return message;
   }

   
   //house keeping, closing all the streams and sockets down
   public void close() {
      try {
         output.close();
         input.close();
         connection.close();
      }
      catch(IOException e) {
         e.printStackTrace();
      }
   }
    
   public void send(Message message) {
      try {
         output.writeObject(message); //sends the message to the server
         output.flush(); 
      }
      catch(IOException e) {
         System.out.println("Error sending message");
         e.printStackTrace();
      }
   }
   
   //just used for testing, Message objects will be the actuall data that is being sent between server and client
   public void send(String message) {
      try {
         output.writeObject(message);
         output.flush();
      }
      catch(IOException e) {
         System.out.println("Error sending message, check input and output streams are null");
      }
   }
   
   public static InetAddress getPublicIP() {
      InetAddress address = null;
      try{
         URL URL = new URL("http://bot.whatismyipaddress.com");
         HttpURLConnection Conn = (HttpURLConnection)URL.openConnection();
         InputStream InStream = Conn.getInputStream();
         InputStreamReader Isr = new InputStreamReader(InStream);
         BufferedReader Br = new BufferedReader(Isr);
         address = InetAddress.getByName(Br.readLine());      
      }
      catch(Exception e){
         System.out.println("Issue getting public ip address");
         e.printStackTrace();
      }
      return address;
   }


   
}