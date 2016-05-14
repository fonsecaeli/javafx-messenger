import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.EOFException;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class ClientGUI extends JFrame {

   public static void main(String[] args) {
      ClientGUI c = new ClientGUI();
   }

   private JTextField userText;
   private JTextArea chatHistory;
   private MessageHandler messageHandler;

   private static final int height = 2000;
   private static final int length = 2000;
   private static final String applicationName = "Instant Messenger-Client";


   public ClientGUI() {
      super(applicationName);
      setupMenus();
      userText = new JTextField();
      userText.setEditable(false);
      userText.addActionListener(new UserTextListener());
      add(userText, BorderLayout.NORTH);
      chatHistory = new JTextArea();
      chatHistory.setEditable(false);
      add(new JScrollPane(chatHistory), BorderLayout.CENTER);
      setSize(height, length);
      setVisible(true);
      chatHistory.setFont(new Font("Serif", Font.PLAIN, 30));
      userText.setFont(new Font("Serif", Font.PLAIN, 30));
      startRunning();
   }
   
   private class UserTextListener implements ActionListener {
      public void actionPerformed(ActionEvent event) {
         String message = event.getActionCommand();
         if(!message.equals("")) {
            try {
               messageHandler.send(new Message(message, InetAddress.getByName("127.0.0.1"), InetAddress.getByName("127.0.0.1"))); //for testing purposes only
               showMessage(message+"\n"); //shows message in the users chat window
            }
            catch(IOException e) {
               chatHistory.setEditable(true);
               chatHistory.append("\nSomething messed up sending message");
               chatHistory.setEditable(false);
            }
         }
         userText.setText("");
      }
   }

   private void setupMenus() {
      JMenuBar menubar = new JMenuBar();
      JMenu options = new JMenu("Options");
      JMenuItem exit = new JMenuItem("Exit");
      options.add(exit);
      menubar.add(options);
   
      JMenu help = new JMenu("Help");
      JMenuItem about = new JMenuItem("About");
      help.add(about);
      menubar.add(help);
   
      exit.addActionListener(
         new ActionListener() {
            public void actionPerformed(ActionEvent event) {
               System.exit(0);
            }
         });
   
      about.addActionListener(
         new ActionListener() {
            public void actionPerformed(ActionEvent event) {
               openReadme();
            }
         });
      setJMenuBar(menubar);
   }	
   
   //opens readme file
   private void openReadme() {
      try {
         ProcessBuilder pb = new ProcessBuilder("Notepad.exe", "Readme.txt");
         pb.start();
      }
      catch(IOException e) {
         e.printStackTrace();
      }
   }
   
   //connect to server 
   public void startRunning() {
      try {
         showMessage("Attempting connection... \n");
         messageHandler = new MessageHandler("127.0.0.1", 5678); //connection will alayws be to the server
         //after connected to server then you can send messages with intended reciepients and server will proccess
         //and send them off to the recipient
         //showMessage("Connected to: " + connection.getHostName()); //TODO fix this showing address thing
         showMessage("\nThe streams are set up and good to go!\n");         
         whileChatting();
      }
      catch(EOFException e) {
         showMessage("\nClient terminated connection");
      
      }
      catch(IOException e2) {
         e2.printStackTrace();
      }
      finally {
         showMessage("\nClosing stuff down");
         messageHandler.closeStuff();
         
      }
   }
   
   //while chatting with server 
   private void whileChatting() throws IOException {
      ableToType(true);
      Message message;
      do {
         message = (Message) messageHandler.readMessage();
         showMessage("\n" + message.getMessage());
      }
      while(!message.equals(" - END")); //TODO
      ableToType(false);
   }

   
   //updates chatWindow
   private void showMessage(final String text) {
   	//setting aside a thread to update a gui, so we dont have to create an entire new gui when we just want to update part
      SwingUtilities.invokeLater(
         new Runnable() {
            public void run() {
               chatHistory.append(text); //adds message to the chatWindow
            }
         }
         );
   }

   private void ableToType(final boolean trueOrFalse) {
         SwingUtilities.invokeLater(
            new Runnable() {
               public void run() {
                  userText.setEditable(trueOrFalse);
               }
            }
            );
   }
} 


   
   
