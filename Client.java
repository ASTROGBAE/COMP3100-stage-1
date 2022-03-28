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
        sendMessage("HELO");
        return matchResponse("OK");
    }

    private Boolean attemptAuth() throws IOException {
        sendMessage("AUTH: " + user);
        return matchResponse("OK");
    }

    private boolean attemptQuitAndClose() throws IOException {
        sendMessage("QUIT");
        // TODO check for return auth, while loop?
        if (matchResponse("QUIT")) { // check server returns quit
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
        sendMessage("REDY"); // send message to server
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

    private boolean getJobs() throws IOException {
        sendMessage("REDY");
        int jobNum = Integer.parseInt(din.readLine().substring(0, 2)); // get job numbers from message, currently
                                                                       // hardcoded to 1 digit, fix?
        // have regex for first whitespace?
        sendMessage("OK"); // send OK for jobs
        for (int i = 0; i < jobNum; i++) {
            String line = din.readLine(); // server, need to read every line from data
            int jobID = Integer.parseInt(line.substring(8, 9)); // just a guess for the number
            // hardcoded number...
            String type = line.substring(2, 10);
            Job j = new Job(jobID, type);
            // add to jobs...
            // TODO refactor into scheduler
        }
        sendMessage("OK"); // send OK for jobs
        return true;
    }

    private boolean getServers() throws IOException {
        sendMessage("GETS All");
        // for servers, get thingo
        return false;
    }

    private boolean scheduleJob() {
        Job j = null; // get job from jons

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

    private void sendMessage(String msg) throws IOException {
        dout.write((msg + "\n").getBytes()); // send OK for jobs
        dout.flush();
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
