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
5. If the person you are trying to communicate with is not online then your messages will not be process and you will receive
a message informing you that the other client is not online.

Known Bugs:
If you are in a chat with someone who is offline, and then you close chat window and then connect back to the same person
a StreamCorruptedException is thrown.  After this point the program is in a bad state.  I have not figured out how to fix this bug
yet so at the moment I am letting the Exception to be thrown.  Users will have to restart the program in order to begin chatting again


