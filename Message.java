// Eli F.
// Section: C
// Final Project
// Description: holds pertinate information of a message that would be sent 
// between client and server
// Class name: Message
// Version 1.0
// 5/22/16

import java.io.*;

public class Message implements Serializable {

   private final String recipient; //need a unique key to identify the recipient tof a message based off of
   private final String sender;
   private final String message;

   /**
    * constructor for a message object
    *
    * @param  message   the string message
    * @param  recipient the intended recipient of this message (userName)
    * @param  sender    the client who sent this message (userName)
    */
   public Message(String message, String recipient, String sender) {
      this.recipient = recipient;
      this.sender = sender;
      this.message = message; //only sending string messages to begin with
   }

   /**
    * makes a message bassed of just a string, useful for the server side use
    *
    * @param  message the message 
    * @return         the message to be sent
    */
   public Message(String message) {
      this.message = message;
      recipient = null; //fix
      sender = null; //fix
   }

   /**
    * accesser method for the sender field
    *
    * @return the senders name
    */
   public String getSender() {
      return sender;
   }

   /**
    * accesser method for the recipients name
    *
    * @return the name of the recipient
    */
   public String getRecipient() {
      return recipient;
   }

   /**
    * accesser method for the string message contained within this object
    *
    * @return the string in object form
    */
   public String getMessage() {
      return message;
   }

   @Override
   /**
    * toString method for this object
    *
    * @return  the objects string message
    */
   public String toString() {
      return this.message;
   }
}
