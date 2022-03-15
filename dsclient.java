import java.io.*;
import java.net.*;

class dsclient {

    // TODO figure out if to use connection-orientated (socket) or datagram
    // (connect-less)?
    // for now, assume CONNECTION-ORIENTATED!

    // client messages
    String msgGreet = "HELO";
    String msgAuth = "AUTH"; // TODO implement auth info with port, etc?
    String msgQuite = "QUIT";
    String[] clientCommand = {
            "GETS" // TODO add all from specs
    };

    // server messages
    // TODO if necessary??
    String[] serverCommand = {
            "JOBN", "JOBP", "JCPL", "RESF", "RESR", "NONE"
    };

    // TODO implement general process

    // main method
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 6666);
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
            dout.writeUTF("Hello Server");
            dout.flush();
            dout.close();
            socket.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}