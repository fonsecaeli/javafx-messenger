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

NOTES:
Only a single client can be open of a computer at a time because the server allocated connections based off of the IP of the
connected client.  So it the serve will not authenticate two clients at the same time on the same computer.

Known Bugs:
If you are in a chat with someone, and then you close chat window and then connect back to the any other user
a StreamCorruptedException is thrown.  After this point the client program is in a bad state.  I have not figured out how to fix this bug
yet so at the moment I am letting the Exception to be thrown.  Users will have to restart the program in order to begin chatting again.

How to look at the code:
The two main classes are the Serve and the ClientGUI.  All the other classes have been incorporated into either of these classes.
The best way to examine the code would be to read the code in the following order:
1. MessageHandler handles the logic of sending and receiving messages
2. Message the messages that are sent
3. ConnectionHandler super class of all other runnable
4. WaitForConnection inner class of Server
5. IdHandler inner class of Server
6. ChatHandler inner class of Server
7. Server the class that uses all of the preceding code
8. ClientGUI by now you have read all the classes that are inside of this

Testing Description:
Run the program and try to chat with someone.  To test the different features you will probably have to open
different client and try to chat in between them.

Grading Rubric:

1.	Functionality 21 points
    a.	Does the IM allow users to communicate on the local network (does it theoretically allow for connection
    between different networks if NAT was disabled? 16 points
        i.	10 == one major error
        ii. 13 == Some exceptions not handled, but they were difficult to raise
        iii.15 == a number of minor problems
        iv.	16 == no errors
    c.	Does the IM allow multiple users to communicate at the same time? 5 points?
        i.	0 == More than one major error
        ii.	3 == Reasonable attempt at this feature
        iii.5 == no error found
2.	Program Design 10 point
    a.	Structure 5 points
        i.	0 == unacceptable structure
        ii.	2 == poorly structured
        iii.4.5 == reasonably well structured but uses many global variables or magic numbers
        iv.	5 == all small, coherent, independent modules unless well justified
    b.	GUI design 5 points
        i.	0 == unacceptable visual appearance
        ii.	3 == simple bland gui that gets the job done some features may not work
        iii.5 == appealing to the eye and all functions work
3.	Style and Documentation 9 points
    i.	0 == unacceptable; no comments, no indentation, etc.
    ii.	3 == difficult to read and understand, few comments, etc
    iii.5 == generally good, but major comments missing
    iv.	7 == generally good, but several imperfections
    v.	8.5 == one or two small imperfections, improper indentation, a poorly chosen identifier
    vi.	9 == good, magic number in the GUI don't count!




