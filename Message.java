import java.util.*;
import java.net.*;
import java.io.*;


public class Message implements Serializable {

   private final String recipient; //need a unique key to identify the reciepine tof a message based off of  
   private final String sender;
   private final String message;


   public Message(String message, String recipient, String sender) {
      this.recipient = recipient;
      System.out.println(recipient);
      this.sender = sender;
      this.message = message; //only sending string messages to begin with
   }
   
   public Message(String message) {
      this.message = message;
      recipient = null; //fix
      sender = null; //fix
   }
   
   public String getSender() {
      return sender;
   }
   
   public String getRecipient() {
      return recipient;
   }
   
   public String getMessage() {
      return message;
   }

}
