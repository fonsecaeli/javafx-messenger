# javafx-messenger
How to use this program:
1. Run the Server.java class, this will open a new server on the machine it is being run on
2. Open clients on any computer on the local network using ClientGUI.java, they will automatically connect to the server
    Note: one must change the server IP field to match the IP of the computer the server is being run
    on for the application to work.  It this program where actually in production the Server would have a
    fixed location so this wouldn't be a problem
3. Enter your username and password through the client to be authenticated to the server
    List of current user names and passwords stored in Server.java:
    Eli, F
    Ravi, Smith
    Andrew, Wei
    Amanda O'Neal
4. After you are authenticated by the Server you can connect to other users by user name, just enter the user name you want to
connect to and a new chat session will open.  If the other user is connected you will be able to chat back and forth

