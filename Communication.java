import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

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
        schd = new Schedule();
        jobQueue = new LinkedList<Job>();
        servers = new ArrayList<Server>();
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
     * @return 2 if complete, 1 is job added, 0 if no more jobs, -1 invalid
     * @throws IOException
     */

    public int attemptGetJob() throws IOException {
        sendMessage("REDY"); // send message to server
        String jobsRegex = "^(\\w{4}) (\\d+) (\\d+) .*";
        String msg = getMessage();
        if (msg.equals("NONE")) { // check if message is NONE, means no more jobs
            return 0;
        }
        if (msg != null) {
            // regex process
            Pattern pattern = Pattern.compile(jobsRegex);
            Matcher matcher = pattern.matcher(msg);
            if(matcher.find()) {
                if (matcher.group(1).equals("JOBN")) { // check message is valid for server (JOBN)
                    int jobID = Integer.parseInt(matcher.group(3));
                    jobQueue.add(new Job(jobID)); // add new job to jobs!
                    return 1;
                }
                else if (matcher.group(1).equals("JCPL")) { // job finished!
                    return 2;
                }
            }
        }
        return -1; // did not work!
    }

    private Server getNextServer() throws Exception {
        // check server list
        if (servers != null) {
            if (servers.size() == 0) { // if servers have run out, go to next type
                wipeServers(); // clean servers
                sendMessage("GETS Type " + schd.getNextType()); // send message to server to get a server type, increment
                                                           // schedule
                int dataNum = getDataAmount(getMessage()); // get amount of data from message if available
                sendMessage("OK"); // send confirmation to server, recieved DATA
                for (int i = 0; i < dataNum; i++) {
                    loadServer(getMessage()); // load server
                }
                sendMessage("OK"); // send confirmation to server, recieved servers
                getMessage();
                System.out.println("servers recieved and logged");
                return servers.remove(0); // pop initial value
            }
        }
        return null;
    }

    private Integer getDataAmount(String data) {
        // server
        String dataRegex = "^DATA (\\d+) .*";
        if (data == null || !data.matches(dataRegex)) { // responce doesnt match, end
            return null;
        }
        // regex process
        Pattern pattern = Pattern.compile(dataRegex);
        Matcher matcher = pattern.matcher(data);
        if (matcher.find()) { // group matches
            return Integer.parseInt(matcher.group(1)); // number of servers to add, parsed from regex SUCCESS!
        } else {
            return null;
        }
    }

    private boolean wipeServers() {
        servers = new ArrayList<Server>(); // reset server list
        return true;
    }

    private boolean loadServer(String serverMsg) {
        String serverRegex = "^([\\w|-]+) (\\d+) .*";
        if (serverMsg != null && serverMsg.matches(serverRegex)) { // check message is valid for server
            String type = "";
            int number = 0;
            // regex process
            Pattern pattern = Pattern.compile(serverRegex);
            Matcher matcher = pattern.matcher(serverMsg);
            if (matcher.find()) { // group matches
                type = matcher.group(1);
                number = Integer.parseInt(matcher.group(2));
            }
            servers.add(new Server(number, type)); // add server
            System.out.println("Server loaded successfully.");
            return true; // success!
        }
        return false; // failure
    }

    public boolean attemptReadXml() throws ParserConfigurationException, SAXException, IOException {
        return schd.readTypes();
    }

    public boolean attemptScheduleJob() throws Exception {
        if (jobQueue != null && !jobQueue.isEmpty()) { // check if jobs are empty
            Job _job = jobQueue.poll(); // pop job off queue
            if (_job != null) { // check there is a job and servers are not
                                // empty
                Server _server = getNextServer(); // get server as per what the algorithm in that method states
                if (_server != null) {
                    sendMessage(String.format("SCHD %s %s %s", _job.number, _server.type, _server.number)); // send
                                                                                                            // scheduling
                    // instrument to server
                    // TODO how to signal from reply that is was correct sent?
                    getMessage(); // get responce
                    return true;
                }
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
