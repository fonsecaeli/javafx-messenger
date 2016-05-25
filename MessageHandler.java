// Eli F.
// Section: C
// Final Project
// Description: handles all the logic behind sending and recieving messages with java sockets
// Class name: MessageHandler
// Version 1.0
// 5/22/16

import java.net.*;
import java.util.*;
import java.io.*;


public class MessageHandler {

    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String connectIP;
    private Socket connection;
    private int port;

    /**
     * use to construct messagehandler for a client program, will aoutmaticaly initiat connection
     * to the server where messages are processed and then sent out to recipients
     *
     * @param  serverIP the ip adress of the server the client wants to be connected too
     */
    public MessageHandler(String serverIP, int port) {
        this.connectIP = serverIP;
        this.port = port;
        connectToServer();
        setupStreams();
    }


    /**
     * used for server side action since in that case we will already have a socket open and ready
     *
     * @param  connection connection between the server and client that needs to be handled
     */
    public MessageHandler(Socket connection) {
        this.connection = connection;
        setupStreams();
    }

    /**
     * access method for the port being taken by this connection handlers socket conneciton
     *
     * @return the port being used
     */
    public int getPort() {
        return this.port;
    }


    /**
     * connects to the server ip set at construction
     */
    public void connectToServer() {
        try {
            int newPort = 5000; //so random free port
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

    /**
     * tester method to see if the connection in the connection handler has been closed
     *
     * @return if the connection is closed or not
     */
    public boolean isClosed() {
        return connection.isClosed() || input == null;
    }

    /**
     * sets up the nessisary stream line so that a client or server can communicated with each other
     */
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

    /**
     * reads a message from the Object stream within this handler
     *
     * @return the Object that was read from the stream
     * @throws SocketException if either the clinet or server has terminated without warning
     * should be handled differntly depending if you are a client or a server
     */
    public Object readMessage() throws SocketException {
        Object message = null;
        try {
            message = input.readObject();
            System.out.println("recieved "+((Message) message).getMessage());
            System.out.println("outputStream: "+connection.getInputStream());
            System.out.println("ip: "+connection.getInetAddress());
            System.out.println("isClosed: "+connection.isClosed());
            System.out.println("isConnected: "+connection.isConnected());
            System.out.println("isInputshutdown: "+connection.isInputShutdown());
            System.out.println("isOutputShutdown: "+ connection.isOutputShutdown());
        }
        catch(ClassNotFoundException e) {
            System.out.println("Unknown object sent along stream");
        } catch(IOException e) {
            System.out.println("input stream is null");
            e.printStackTrace();
        }

        return message;
    }

    /**
     * house keeping stuff, just closes everything down when we are finished
     */
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

    /**
     * sends a given message object along the object streams within the handler
     *
     * @param message the message to be send
     */
    public void send(Message message) {
        try {
            output.writeObject(message); //sends the message to the server
            output.flush();
            /*System.out.println("tried to send, "+message.getMessage());
            System.out.println("outputStream: "+connection.getOutputStream());
            System.out.println("ip: "+connection.getInetAddress());
            System.out.println("isClosed: "+connection.isClosed());
            System.out.println("isConnected: "+connection.isConnected());
            System.out.println("isInputshutdown: "+connection.isInputShutdown());
            System.out.println("isOutputShutdown: "+ connection.isOutputShutdown());*/

        }
        catch(IOException e) {
            System.out.println("Error sending message");
            e.printStackTrace();
        }
    }

    //just used for testing, Message objects will be the actuall data that is being sent between server and client
   /*public void send(String message) {
      try {
         output.writeObject(message);
         output.flush();
      }
      catch(IOException e) {
         System.out.println("Error sending message, check input and output streams are null");
      }
   }*/

    /**
     * possibly useful for later iterations of this program, a method to get the public ip address of the machine this handler
     * is being used on
     *
     * @return the ip address of this computer, according to whatismyipaddress.com
     */
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