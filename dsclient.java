import java.io.*;
import java.net.*;

class dsclient {

    // TODO figure out if to use connection-orientated (socket) or datagram
    // (connect-less)?
    // for now, assume CONNECTION-ORIENTATED!

    // client fields
    // Boolean connected = false;
    // Boolean authenticated = false;
    // TODO not necessary? just use connect>
    static Socket socket;
    static DataInputStream din;
    static DataOutputStream dout;

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

    // TODO implement general process

    // client methods
    private static boolean init() {
        if (socket != null && din != null && dout != null) {
            return true;
        }
        return false;
    }

    private static boolean attemptOk() {
        if (init()) {
            try {
                dout.writeUTF(msgOk); // send ok to server
                dout.flush();
                // TODO check for return OK, while loop?
                return true; // if returned ok
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        return false;
    }

    private static boolean attemptAuth() {
        if (init()) {
            try {
                dout.writeUTF(msgAuth); // send info to server
                dout.flush();
                // TODO check for return auth, while loop?
                return true; // if returned ok
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        return false;
    }

    // main method
    public static void main(String[] args) {
        try {
            socket = new Socket("localhost", 6666);
            dout = new DataOutputStream(socket.getOutputStream());
            while (!attemptOk()) {
                // TODO put while loop in method and simply call this method here?
            }
            dout.close();
            socket.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}