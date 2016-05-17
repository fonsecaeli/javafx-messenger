
 //Eli F.

import javafx.application.Application;
import javafx.event.ActionEvent;
import java.net.*;

import javafx.event.EventHandler;
import static javafx.geometry.HPos.RIGHT;
import javafx.geometry.Insets;
import javafx.scene.input.KeyEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.control.TextArea;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.EOFException;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetAddress;

public class Login extends Application {

   private static final String applicationName = "Instant Messenger-Client";
   private static final String loginWindowName = "JavaIM-Login";
   private static final String serverIP = "10.83.3.83";
   private String userName; //this clients address
   private String recipient; //whomever this client is currently talking with
   
   private MessageHandler messageHandler; //Message handler for this clients to use for all communication with the server
   private MessageHandler loginHandler;
   
   private TextArea history;
   private TextField userText;
   
   private Stage primaryStage;
   //private boolean passwordIncorrect = true;

   @Override
   public void start(Stage primaryStage) {
      this.primaryStage = primaryStage;
      //connect to server, what ever the ip address and port will be predetermined and constant for all clients
      loginHandler = new MessageHandler(serverIP, 9999);    
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
            public void handle(ActionEvent e) {
               processQuery(userTextField, pwBox, actiontarget);
            }
         }
         );
   
      btn.setOnAction(
         new EventHandler<ActionEvent>() {
         
            @Override
            public void handle(ActionEvent e) {
               processQuery(userTextField, pwBox, actiontarget);
            }
         });
   
      Scene scene = new Scene(grid, 300, 275);
      primaryStage.setScene(scene);
      scene.getStylesheets().add(Login.class.getResource("Login.css").toExternalForm()); //adding css style sheet
      primaryStage.show();
   }
   
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
            System.out.println("server terminated connection");
            System.exit(1);
         }
         if(response.equals(userTextField.getText())) {
            loginHandler.close();
            openHomePage(response);
         }
         else {
            actiontarget.setText("Incorrect username or password!");
            pwBox.clear();
         }
      
      
            //proccess the username  and password text in some way
            //if the password or user name is incorrect prompt user for new entry and set the field to editable
      }
      else {
         actiontarget.setText("Invalid username or password!");
      }
   }
   public void openHomePage(String userName) {
      primaryStage.hide();
      primaryStage = new Stage();
      primaryStage.getIcons().add(new Image("1462788563_messenger2.png"));
      GridPane grid = new GridPane();
      grid.setAlignment(Pos.CENTER);
      grid.setHgap(10);
      grid.setVgap(10);
      grid.setPadding(new Insets(25, 25, 25, 25));
   
      //TreeView<String> pendingConections = new TreeView<String>();
      //TreeItem<String> root
   
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
      
      btn.setOnAction(
         new EventHandler<ActionEvent>() {
         
            @Override
            public void handle(ActionEvent e) {
               //need to add error handleling for non valid ip address
               if(recipient.getText() != null) {
                  openChatWindow(userName, recipient.getText());
               }
            }
         });
         
      Scene scene = new Scene(grid);
      primaryStage.setScene(scene);
      scene.getStylesheets().add(Login.class.getResource("Login.css").toExternalForm()); //adding css style sheet
      primaryStage.show();
   
   
   
      
      
   }
   
   public void openChatWindow(String userName, String recipient) {
      primaryStage.close();
      primaryStage = new Stage();
      primaryStage.setTitle("Chat session with: "+recipient);
      primaryStage.getIcons().add(new Image("1462788563_messenger2.png"));
      
      userText = new TextField();
      userText.setPrefSize(300, 20);
      history = new TextArea();
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
               updateChatWindow(userName + ": " + message.getMessage()+"\n");
               userText.clear();
            }
         });
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
      scene.getStylesheets().add(Login.class.getResource("Login.css").toExternalForm()); //adding css style sheet
      startRunning();
      primaryStage.show();
      
     
      
   
   }
   
   public void startRunning() {
      messageHandler = new MessageHandler(serverIP, 5678);
      updateChatWindow("Attempting connection... \n");
          //connection will alayws be to the server
         //after connected to server then you can send Messages with intended reciepients and server will proccess
         //and send them off to the recipient
         //showMessage("Connected to: " + connection.getHostName()); //TODO fix this showing address thing
      updateChatWindow("The streams are set up and good to go!\n");         
      Task<Void> whileChatting = 
            new Task<Void>() {
               @Override protected Void call() {
                  ableToType(true);
                  try {
                     Message message;
                     do {
                        message = (Message) messageHandler.readMessage();
                        updateChatWindow("\n" + message.getMessage());
                     }
                     while(!message.equals("END")); //TODO better exit strategy needed
                     ableToType(false); 
                  }
                  finally {
                     updateChatWindow("Closing stuff down\n");
                     messageHandler.close();
                     return null;
                     
                  }
               
               }
            };
      Thread th = new Thread(whileChatting);
      th.setDaemon(true);
      th.start();
   }

   
   //updates chatWindow
   private void updateChatWindow(final String text) {
   	//setting aside a thread to update a gui, so we dont have to create an entire new gui when we just want to update part
      Platform.runLater(
         new Runnable() {
            public void run() {
               history.appendText(text); //adds Message to the chatWindow
            }
         }
         );
   }

   private void ableToType(final boolean trueOrFalse) {
      Platform.runLater(
            new Runnable() {
               public void run() {
                  userText.setEditable(trueOrFalse);
               }
            }
            );
   }

      
   
   public static void main(String[] args) {
      launch(args);
   }

}
