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

        String jobsRegex = "^(\\w{4}) (\\d+) (\\d+) .*";

        String msg = getMessage();
        if (msg != null && msg.matches(jobsRegex)) { // check message is valid for server
            String type = "";
            int number = 0;
            // regex process
            Pattern pattern = Pattern.compile(jobsRegex);
            Matcher matcher = pattern.matcher(msg);
            if (matcher.find()) { // group matches
                type = matcher.group(1);
                number = Integer.parseInt(matcher.group(2));
            }
            servers.add(new Server(number, type)); // add server
        }

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
        // server
        String dataRegex = "^DATA (\\d+) .*";
        String msg = getMessage(); // get message, need to see if DATA...
        if (msg == null || !msg.matches(dataRegex)) { // responce doesnt match, end
            return false;
        }
        // regex process
        Pattern pattern = Pattern.compile(dataRegex);
        Matcher matcher = pattern.matcher(msg);
        int serverN = 0;
        if (matcher.find()) { // group matches
            serverN = Integer.parseInt(matcher.group(1)); // number of servers to add, parsed from regex
        }
        // DATA succeeded!
        sendMessage("OK"); // send confirmation to server, recieve jobs
        // recieve jobs per line
        String serverRegex = "^(\\w+) (\\d+) .*";
        for (int i = 0; i < serverN; i++) { // iterate through number of jobs
            msg = getMessage();
            if (msg != null && msg.matches(serverRegex)) { // check message is valid for server
                String type = "";
                int number = 0;
                // regex process
                pattern = Pattern.compile(serverRegex);
                matcher = pattern.matcher(msg);
                if (matcher.find()) { // group matches
                    type = matcher.group(1);
                    number = Integer.parseInt(matcher.group(2));
                }
                servers.add(new Server(number, type)); // add server
            }
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
        return msg;
    }

    private Boolean matchResponse(String expectedMsg) throws IOException {
        if (getMessage().equals(expectedMsg)) {
            return true;
        }
        return false;
    }
}
