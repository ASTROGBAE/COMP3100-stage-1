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

    // job scheduler algorithm
    Scheduler scheduler;

    // client messages
    static String[] clientCommand = {
            "GETS" // TODO add all from specs
    };

    // constructor
    public Client(String address, int port) {
        try {
            socket = new Socket(address, port); // initial socket to connect with server
            din = new BufferedReader(new InputStreamReader(socket.getInputStream())); // data in stream (buffered reader
                                                                                      // for ds-sim specs)
            dout = new DataOutputStream(socket.getOutputStream()); // data out stream
            scheduler = new Scheduler("./config_samples/ds-sample-config01.xml");
            user = System.getProperty("user.name"); // get system name
            running = true;
        } catch (Exception e) {
            System.out.println(e);
            // TODO write out what will happen if this fails???
        }
        // if all works, add in normal stuff...
    }

    // ~~~~~~~~~~~~~~~ RUN ALGORITHM ~~~~~~~~~~~~~~~

    void run() {
        while (running) {
            printWelcome();
            // connection attempt
            // OK attempt
            System.out.print("Sending HELO... ");
            try {
                while (!attemptHelo()) {
                    System.out.println("Cannot connect to server. Trying again...");
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                System.out.println("IOException in attempting HELO, printing stack trace...");
                e.printStackTrace();
            }
            System.out.println("SUCCESS: server OK");
            // Auth attempt
            System.out.print("Attempting AUTH as user: " + user + "...");
            try {
                while (!attemptAuth()) {
                    System.out.println("Cannot get authentication. Trying again...");
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                System.out.println("IOException in attempting to get authentication, printing stack trace...");
                e.printStackTrace();
            }
            System.out.println("SUCCESS: server OK");
            // Preparation
        }
    }

    // ~~~~~~~~~~~~~~~ COMMAND CATEGORY: connection ~~~~~~~~~~~~~~~
    // Commands: HELO, AUTH, QUIT

    private Boolean attemptHelo() throws IOException {
        String msgHelo = "HELO";
        dout.write((msgHelo + "\n").getBytes());
        dout.flush();
        return matchResponse("OK");
    }

    private Boolean attemptAuth() throws IOException {
        String msgAuth = "AUTH: "; // TODO implement auth info with port, etc?
        dout.write((msgAuth + user + "\n").getBytes());
        dout.flush();
        return matchResponse("OK");
    }

    private boolean attemptQuitAndClose() throws IOException {
        String msgQuit = "QUIT";
        dout.writeUTF(msgQuit); // send info to server
        dout.flush();
        // TODO check for return auth, while loop?
        if (matchResponse(msgQuit)) { // check server returns quit
            socket.close(); // connection ended! Close everything
            din.close();
            dout.close();
            return true;
        }
        return false;
    }

    // ~~~~~~~~~~~~~~~ COMMAND CATEGORY: preparation ~~~~~~~~~~~~~~~
    // Commands: REDY, DATA

    /**
     * Method handling Preparation phase (REDY <-> DATA)
     * 
     * @return true if server responds "DATA"
     * @throws IOException
     */
    private boolean attemptPreparation() throws IOException {
        dout.write(("REDY\n").getBytes()); // send ready message to server,
        dout.flush();
        return matchResponse("DATA");
    }

    // ~~~~~~~~~~~~~~~ COMMAND CATEGORY: Simulation event ~~~~~~~~~~~~~~~
    // commands: "JOBN", "JOBP", "JCPL", "RESF", "RESR", "NONE"

    private boolean recieveJob() throws IOException {
        switch (getMessage()) { // recieve message from server,
            case "JOBN": // server sends normal job
                String job = getMessage();
                // code block
                break;
            case "JOBP":
                // code block
                break;
            case "JCPL":
                // code block
                break;
            case "RESF":
                // code block
                break;
            case "RESR":
                // code block
                break;
            case "NONE":
                // code block
                break;
            default:
                // code block
        }
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

    // ~~~~~~~~~~~~~~~ HELPER METHODS ~~~~~~~~~~~~~~~
    // methods: printWelcome, getMessage, matchResponse

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

    private Boolean matchResponse(String expectedMsg) throws IOException {
        if (getMessage().equals(expectedMsg)) {
            return true;
        }
        return false;
    }
}
