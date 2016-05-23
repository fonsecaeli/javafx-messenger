// Eli F.
// Section: C
// Final Project
// Description: main class that takes care of the GUI for the client, uses supporting classes for messeging logic
// Class name: ClientGUI
// Version 1.0
// 5/22/16

import javafx.application.*;
import java.net.*;
import javafx.event.*;
import static javafx.geometry.HPos.RIGHT;
import javafx.scene.input.*;
import javafx.stage.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.scene.image.*;
import javafx.stage.*;
import javafx.scene.control.*;
import javafx.application.*;
import javafx.concurrent.*;
import java.io.*;
import java.net.*;

public class ClientGUI extends Application {

	/**
	 * name that goes at the top of the GUI window
	 */
   private static final String loginWindowName = "JavaIM-Login";

   /**
    * ip address of the server, would idealy be the public ip if the server is located on a computer that can be freely accessed
    * but for testing and demo purpose it is set to the local ip of the server on the lakeside wifi
    */
   private static final String serverIP = "10.83.3.83"; //should be public ip address for the server


   private String userName;
   private String recipient; //whomever this client is currently talking with
	MessageHandler messageHandler; //Message handler for this clients to use for all communication with the server
   private MessageHandler loginHandler;
   private int loginPort = 9999;
   private int chatPort = 5678;
   
   //gui components that need to be easily accessed by differnt part of the program
   private TextArea history;
   private TextField userText;
   private Stage primaryStage;
   

   @Override
   /**
    * the "main" method for a javafx application, the entry point into the program where it all begins...
    * @param primaryStage the main container object for GUI components
    */
   public void start(Stage primaryStage) {
      this.primaryStage = primaryStage;
      //connect to server, what ever the ip address and port will be predetermined and constant for all clients
      messageHandler = new MessageHandler(serverIP, 5678);
      loginHandler = new MessageHandler(serverIP, loginPort);    
      openLoginWindow(primaryStage);
   }
   
   
   public void openLoginWindow(Stage primaryStage) {
      primaryStage.setTitle(loginWindowName);
      primaryStage.getIcons().add(new Image("1462788563_messenger2.png"));
      GridPane grid = new GridPane();
      grid.setAlignment(Pos.CENTER);
      grid.setHgap(10);
      grid.setVgap(10);
      grid.setPadding(new Insets(25, 25, 25, 25));
   
      Text scenetitle = new Text("JavaIM");
      scenetitle.setId("welcome-text");
      grid.add(scenetitle, 0, 0, 2, 1);
   
      Label userName = new Label("User Name:");
      grid.add(userName, 0, 1);
   
      TextField userTextField = new TextField();
      grid.add(userTextField, 1, 1);
   
      Label pw = new Label("Password:");
      grid.add(pw, 0, 2);
   
      PasswordField pwBox = new PasswordField();
      grid.add(pwBox, 1, 2);
      
         
      Button btn = new Button("Sign in");
      HBox hbBtn = new HBox(10);
      hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
      hbBtn.getChildren().add(btn);
      grid.add(hbBtn, 1, 4);
   
      final Text actiontarget = new Text();
      grid.add(actiontarget, 0, 6);
      actiontarget.setId("actiontarget");
      grid.setColumnSpan(actiontarget, 2);
      grid.setHalignment(actiontarget, RIGHT);
      actiontarget.setId("actiontarget");
      
      //event handler for the password field
      pwBox.setOnAction(
         new EventHandler<ActionEvent>() {

         	@Override
            public void handle(ActionEvent e) {
               processQuery(userTextField, pwBox, actiontarget);
            }
         }
         );
   	//B handler for the login button
      btn.setOnAction(
         new EventHandler<ActionEvent>() {
         
            @Override
            public void handle(ActionEvent e) {
               processQuery(userTextField, pwBox, actiontarget);
            }
         });
   
      Scene scene = new Scene(grid, 300, 275);
      primaryStage.setScene(scene);
      scene.getStylesheets().add(ClientGUI.class.getResource("Login.css").toExternalForm()); //adding css style sheet
      primaryStage.show();
   }
   
   /**
    * processes a users input into the login and password fields of the login in page
    * asks the server to authenticate and then informs the client if they have been authenticated or not
    * 
    * @param userTextField username entry box
    * @param pwBox         password entry box
    * @param actiontarget  text that tells the user what the response from the server was
    */
   private void processQuery(TextField userTextField, PasswordField pwBox, Text actiontarget) {
      if(userTextField.getText() != null && !userTextField.getText().isEmpty() &&
         pwBox.getText() != null && !pwBox.getText().isEmpty()) {
         actiontarget.setText("processing...");
         Message loginPacket = new Message(userTextField.getText() + " " + pwBox.getText());
         loginHandler.send(loginPacket);
         String response = "";
         try {
            response = ((Message) loginHandler.readMessage()).getMessage();
         }
         catch(SocketException e) {
            actiontarget.setText("server has gone offline at this time, please try again later.");
            try {
            	Thread.currentThread().sleep(100000);
            }
            catch(Exception ex) {}
            System.exit(1);
         }
         catch(Exception e) {
            e.printStackTrace();
         }
         if(response.equals(userTextField.getText())) {
            loginHandler.close();
            userName = response;
            openHomePage();
         }
         else {
            actiontarget.setText(response);
            pwBox.clear();
         }
      }
      else {
         actiontarget.setText("Invalid username or password!");
      }
   }

   /**
    * homepage gui where a clinet can connect with other users by username 
    */
   public void openHomePage() {


      primaryStage.hide();
      primaryStage = new Stage();
      primaryStage.setOnCloseRequest(
         new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
           		messageHandler.close();
            }
         }); 
      primaryStage.getIcons().add(new Image("1462788563_messenger2.png"));
      GridPane grid = new GridPane();
      grid.setAlignment(Pos.CENTER);
      grid.setHgap(10);
      grid.setVgap(10);
      grid.setPadding(new Insets(25, 25, 25, 25));
   
      Text scenetitle = new Text("Welcome "+userName);
      scenetitle.setId("welcome-text");
      grid.add(scenetitle, 0, 0, 2, 1);
   
      Label connectTo = new Label("Connect to:");
      grid.add(connectTo, 0, 1);
   
      TextField recipient = new TextField();
      grid.add(recipient, 1, 1);
         
      Button btn = new Button("Connect");
      HBox hbBtn = new HBox(10);
      hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
      hbBtn.getChildren().add(btn);
      grid.add(hbBtn, 1, 4);

      recipient.setOnAction(
      	new EventHandler<ActionEvent>() {

         	@Override
            public void handle(ActionEvent e) {
             if(recipient.getText() != null) {
                  openChatWindow(recipient.getText());
               }
            }
         });
      
      //listner for the submit button
      btn.setOnAction(
         new EventHandler<ActionEvent>() {
         
            @Override
            public void handle(ActionEvent e) {
               //need to add error handleling for non valid ip address
               if(recipient.getText() != null) {
                  openChatWindow(recipient.getText());
               }
            }
         });
         
      Scene scene = new Scene(grid);
      primaryStage.setScene(scene);
      scene.getStylesheets().add(ClientGUI.class.getResource("Login.css").toExternalForm()); //adding css style sheet
      primaryStage.show();
   }
   
   /**
    * opens a chat window with a specified user
    *
    * @param recipient the username of the person the client is chatting with will be displayed for reference
    */
   public void openChatWindow(String recipient) {
      primaryStage.close();
      primaryStage = new Stage();
      primaryStage.setOnCloseRequest(
         new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
               openHomePage();
            }
         }); 
      primaryStage.setTitle("Chat session with: "+recipient);
      primaryStage.getIcons().add(new Image("1462788563_messenger2.png"));
      
      userText = new TextField();
      userText.setPrefSize(300, 20);
      history = new TextArea();
      history.setEditable(false);
      BorderPane border = new BorderPane();
      HBox sendBox = new HBox();
      sendBox.setPadding(new Insets(15, 20, 15, 20));
      sendBox.setSpacing(10);
      Button submitB = new Button("Send");
      submitB.setPrefSize(100,20);
      sendBox.getChildren().add(userText);
      sendBox.getChildren().add(submitB);
      HBox historyBox = new HBox();
      historyBox.setPadding(new Insets(20));
      historyBox.getChildren().add(history);
      
      border.setBottom(sendBox);
      border.setCenter(historyBox);
      
      
      userText.setOnAction(
         new EventHandler<ActionEvent>() {
         
            @Override
            public void handle(ActionEvent e) {
               Message message = new Message(userName + ": " + userText.getText(), recipient, userName);
               messageHandler.send(message);
               updateChatWindow(message.getMessage()+"\n");
               userText.clear();
            }
         });
      //was considering adding a send as you type feature, like live text so what ever you typed automaticaly got sent
   /*userText.setOnKeyTyped(
   new EventHandler<KeyEvent>() {
   
   	public void handle(KeyEvent key) {
   		Message message = new Message(key.getCharacter(), recipient, userName);
   		messageHandler.send(message);
   		updateChatWindow(key.getCharacter());
   	}
   }
   );*/
      
      Scene scene = new Scene(border, 400, 700);
      primaryStage.setScene(scene);
      scene.getStylesheets().add(ClientGUI.class.getResource("Login.css").toExternalForm()); //adding css style sheet
      primaryStage.show();
      startRunning();
   }

   public void openConnection() {
   	messageHandler = new MessageHandler(serverIP, 5678);	
   }
   
   /**
    * sets up all the nessisary detials to connect with the server so you can have a chat with someone
    */
   public void startRunning() {
      updateChatWindow("Attempting connection... \n");
          //connection will alayws be to the server
         //after connected to server then you can send Messages with intended reciepients and server will proccess
         //and send them off to the recipient
         //showMessage("Connected to: " + connection.getHostName()); //TODO fix this showing address thing
      updateChatWindow("The streams are set up and good to go!\n");   
      //have to run the task of checking for incoming messages on its own seperate thread because otherwise it would freeze the gui      
      Task<Void> whileChatting = 
            new Task<Void>() {
               @Override protected Void call() {
                  ableToType(true);
                  try {
                     Message message;
                     do {
                        message = (Message) messageHandler.readMessage();
                        updateChatWindow(message.getMessage()+"\n");
                     }
                     while(!message.equals(message.getMessage().substring(message.getMessage().indexOf(":"))+"END")); //TODO better exit strategy needed
                     ableToType(false); 
                  }
                  finally {        
                     openHomePage();
                     return null;
                     
                  }
               
               }
            };
      Thread th = new Thread(whileChatting);
      th.setDaemon(true);
      th.start();
   }

   
   /**
    * updates the chatHistory window with a given String
    * 
    * @param text the text to be displayed on the chat history box
    */
   private void updateChatWindow(final String text) {
   	//setting aside a thread to update a gui, so we dont have to create an entire new gui when we just want to update part
      Platform.runLater(
         new Runnable() {
            public void run() {
               history.setEditable(true);
               history.appendText(text); //adds Message to the chatWindow
               history.setEditable(false);
            }
         }
         );
   }

   /**
    * sets the users input box to able to type or not
    * 
    * @param trueOrFalse if the user should be able to type or not
    */
   private void ableToType(final boolean trueOrFalse) {
      Platform.runLater(
            new Runnable() {
               public void run() {
                  userText.setEditable(trueOrFalse);
               }
            }
            );
   }

   /**
    * entry point into the program, calls the start method (the one at the top) and sets everything in motion
    * 
    * @param args input from the console
    */
   public static void main(String[] args) {
      launch(args);
   }

}
