import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Communication {

    // socket fields
    String user;
    Socket socket;
    BufferedReader din;
    DataOutputStream dout;

    // data structyures
    Schedule schd;
    ArrayList<Server> servers;
    Queue<Job> jobQueue;

    // CONSTRUCTOR

    public Communication(Socket _socket) {
        socket = _socket;
        user = System.getProperty("user.name"); // get system name
        servers = new ArrayList<Server>();
        jobQueue = new LinkedList<Job>();
        try {
            din = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            dout = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // ~~~~~~~~~~~~~~~ COMMAND CATEGORY: connection ~~~~~~~~~~~~~~~
    // Commands: HELO, AUTH, QUIT

    public Boolean attemptHelo() throws IOException {
        sendMessage("HELO");
        return matchResponse("OK");
    }

    public Boolean attemptAuth() throws IOException {
        sendMessage("AUTH: " + user);
        return matchResponse("OK");
    }

    public boolean attemptQuitAndClose() throws IOException {
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
     * @return true if jobs found and added to queue
     * @throws IOException
     */
    public int attemptGetJob() throws IOException {
        sendMessage("REDY"); // send message to server
        String data = getMessage(); // TODO check if there are other ways to get jobs other than using JOBN!
        String dat = data.substring(0, 5);
        if (data.substring(0, 4).equals("JOBN")) { // check if valid data (beginning must be "JOBN")
            int jobID = Integer.parseInt(data.substring(8, 9)); // get jobID
            jobQueue.add(new Job(jobID)); // add new job to server!
            return 1;
        } else if (data.equals("NONE")) {
            return 0; // no more jobs!
        }
        return -1; // invalid or no responce...
    }

    public boolean attemptGetServers() throws Exception {
        sendMessage("GETS All"); // send message to server
        int serverN; // number of servers to add - calculated below
        String msg = getMessage();
        if (msg != null && msg.substring(0, 4).equals("DATA")) { // check if message is valid (start with data)
            // for a job and long enough to
            // get job numbers
            serverN = Integer.parseInt(msg.substring(5, 6)); // get number (TODO hardcoded to 1 digit)
            if (serverN <= 0) { // no servers to add or invalid number...
                System.out.println("no server to add or invalid num");
                return false;
            }
        } else { // above if statement false
            System.out.println("DATA matcher invalid");
            return false;
        }
        // get jobs!
        sendMessage("OK"); // send message to server
        for (int i = 0; i < serverN; i++) { // iterate through number of jobs
            msg = getMessage();
            int number = 0; String type = "";
            if (msg.matches("^super-silk(.*)")) {
                // TODO read in all types of servers, iterate through and pattern match that way...
                String n = msg.substring(11, 12);
                number = Integer.parseInt(n); // server number // TODO super hardcoded
                type = msg.substring(0, 10); // TODO super hardcoded
            } else {
            String n = msg.substring(5, 6);
            number = Integer.parseInt(n); // server number // TODO super hardcoded
            type = msg.substring(0, 4); // TODO super hardcoded
            }
            servers.add(new Server(number, type));
        }
        // attempt to make or update server schedule
        if (schd == null) { // create new schedule with new index at schedule size-1
            schd = new Schedule(serverN);
        } else { // update schedule size, do not change index
            schd.setServerNumbers(serverN);
        }
        sendMessage("OK"); // send OK to server, servers recieved!
        getMessage(); // recieve message, will be "."
        System.out.println("servers recieved and logged");
        return true;
    }

    public boolean attemptScheduleJob() throws IOException {
        if (jobQueue != null && !jobQueue.isEmpty()) { // check if jobs are empty
            Job _job = jobQueue.poll();
            if (_job != null && (servers != null && !servers.isEmpty())) { // check there is a job and servers are not
                                                                           // empty
                Server _server = servers.get(schd.getindex());
                sendMessage(String.format("SCHD %s %s %s", _job.number, _server.type, _server.number)); // send
                                                                                                        // scheduling
                // instrument to server
                // TODO how to signal from reply that is was correct sent?
                getMessage(); // get responce
                return true;
            }
        }
        return false; // no job to be polled
    }

    // ~~~~~~~~~~~~~~~ COMMAND CATEGORY: Error ~~~~~~~~~~~~~~~

    public boolean recieveError() {
        return false; // TODO add this functionality into a revieve message?
    }

    // ~~~~~~~~~~~~~~~ COMMAND CATEGORY: preparation ~~~~~~~~~~~~~~~

    public boolean ok() {
        return false;
    }

    // ~~~~~~~~~~~~~~~ HELPER METHODS ~~~~~~~~~~~~~~~
    // methods: printWelcome, getMessage, matchResponse

    public void printWelcome() {
        System.out.println("Greetings " + user);
        System.out.println(
                String.format("Target IP: %s Target Port: %s", socket.getInetAddress(), socket.getPort()));
        System.out.println(
                String.format("Local IP: %s Local Port: %s", socket.getLocalAddress(), socket.getLocalPort()));
    }

    private void sendMessage(String msg) throws IOException {
        dout.write((msg + "\n").getBytes()); // send OK for jobs
        dout.flush();
        System.out.println("Sent msg: " + msg);
    }

    private String getMessage() throws IOException {
        String msg = din.readLine();
        System.out.println("Recieved msg: " + msg);
        return msg ;
    }

    private Boolean matchResponse(String expectedMsg) throws IOException {
        if (getMessage().equals(expectedMsg)) {
            return true;
        }
        return false;
    }
}
