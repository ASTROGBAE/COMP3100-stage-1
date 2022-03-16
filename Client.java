import java.io.*;
import java.net.*;

public class Client {

    // client fields
    boolean running;

    // socket fields
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
            running = true;
        } catch (Exception e) {
            System.out.println(e);
            // TODO write out what will happen if this fails???
        }
    }

    void close() {
        try {
            socket.close();
            din.close();
            dout.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    void run() throws Exception {
        while (running) {
            System.out.println("Starting server... ");
            int i = 1;
            int sleep = 600;
            while (!attemptOk()) {
                Thread.sleep(sleep); // InterruptedException
                System.out.println(String.format("Attemping OK (%s) attempt, sleep %sms", i, sleep));
                i++;
            }
            if (attemptAuth()) {
                System.out.println("Auth successful");
            } else {
                System.out.println("Auth unsuccessful");
            }
        }
    }

    // other methods
    // TODO how to establish and keep a connection?

    private boolean attemptOk() {
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

    private boolean attemptAuth() {
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
