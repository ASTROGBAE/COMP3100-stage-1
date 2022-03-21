import java.io.*;
import java.net.*;

public class Client {

    // client fields
    boolean running;
    String user;

    // socket fields
    Socket socket;
    BufferedReader din;
    DataOutputStream dout;

    // client messages
    static String[] clientCommand = {
            "GETS" // TODO add all from specs
    };

    // server commands
    // TODO if necessary??
    static String[] serverCommand = {
            "JOBN", "JOBP", "JCPL", "RESF", "RESR", "NONE"
    };

    // constructor
    public Client(String address, int port) {
        try {
            socket = new Socket(address, port);
            din = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            dout = new DataOutputStream(socket.getOutputStream());
            user = System.getProperty("user.name");
            running = true;
        } catch (Exception e) {
            System.out.println(e);
            // TODO write out what will happen if this fails???
        }
        // if all works, add in normal stuff...
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

    // TODO how to establish and keep a connection?

    /**
     * Command categories
     * 
     * Categories:
     * - Connection
     * - Preparation
     * - Simulation Event
     * - Client Action
     * - Error
     * - Acknowledgement
     * src: distus-MQ section: 8
     */

    // helper methods (put somewhere else???)
    private void printWelcome() {
        System.out.println("Greetings " + user);
        System.out.println(
                String.format("Target IP: %s Target Port: %s", socket.getInetAddress(), socket.getPort()));
        System.out.println(
                String.format("Local IP: %s Local Port: %s", socket.getLocalAddress(), socket.getLocalPort()));
    }

    private String getMessage() throws IOException {
        return din.readLine();
    }

    // ~~~~~~~~~~~~~~~ COMMAND CATEGORY: connection ~~~~~~~~~~~~~~~

    private boolean attemptOk() {
        String msgOk = "HELO";
        try {
            dout.write((msgOk + "\n").getBytes());
            dout.flush();
            // TODO check for return OK, while loop?
            return true; // if returned ok
        } catch (Exception e) {
            System.out.println(e);
        }
        return false;
    }

    private boolean attemptAuth() {
        String msgAuth = "AUTH: "; // TODO implement auth info with port, etc?
        try {
            dout.write((msgAuth + user + "\n").getBytes());
            dout.flush();
            // TODO check for return auth, while loop?
            return true; // if returned ok
        } catch (Exception e) {
            System.out.println(e);
        }
        return false;
    }

    private boolean attemptQuit() {
        String msgQuit = "QUIT";
        try {
            dout.writeUTF(msgQuit); // send info to server
            dout.flush();
            // TODO check for return auth, while loop?
            return true; // if returned ok
        } catch (Exception e) {
            System.out.println(e);
        }
        return false;
    }

    // ~~~~~~~~~~~~~~~ COMMAND CATEGORY: preparation ~~~~~~~~~~~~~~~

    private boolean attemptPreparation() {
        return false;
    }

    // ~~~~~~~~~~~~~~~ COMMAND CATEGORY: Simulation event ~~~~~~~~~~~~~~~

    private boolean recieveJob() {
        return false;
    }

    // ~~~~~~~~~~~~~~~ COMMAND CATEGORY: Client action ~~~~~~~~~~~~~~~

    private boolean attemptJobAction() {
        return false;
    }

    // ~~~~~~~~~~~~~~~ COMMAND CATEGORY: Error ~~~~~~~~~~~~~~~

    private boolean recieveError() {
        return false; // TODO add this functionality into a revieve message?
    }

    // ~~~~~~~~~~~~~~~ COMMAND CATEGORY: preparation ~~~~~~~~~~~~~~~

    private boolean ok() {
        return false;
    }
}
