import java.io.*;
import java.net.*;

public class Client {

    // fields
    Socket socket;
    DataInputStream din;
    DataOutputStream dout;

    // client messages
    static String msgOk = "HELO";
    static String msgAuth = "AUTH"; // TODO implement auth info with port, etc?
    static String msgQuite = "QUIT";
    static String[] clientCommand = {
            "GETS" // TODO add all from specs
    };

    // server messages
    // TODO if necessary??
    static String[] serverCommand = {
            "JOBN", "JOBP", "JCPL", "RESF", "RESR", "NONE"
    };

    // constructor
    public Client(String address, int port) {
        try {
            socket = new Socket(address, port);
            dout = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            System.out.println(e);
            // TODO write out what will happen if this fails???
        }
    }

    // other methods

    boolean attemptOk() {
        try {
            dout.writeUTF(msgOk); // send ok to server
            dout.flush();
            // TODO check for return OK, while loop?
            return true; // if returned ok
        } catch (Exception e) {
            System.out.println(e);
        }
        return false;
    }

    boolean attemptAuth() {
        try {
            dout.writeUTF(msgAuth); // send info to server
            dout.flush();
            // TODO check for return auth, while loop?
            return true; // if returned ok
        } catch (Exception e) {
            System.out.println(e);
        }
        return false;
    }
}
