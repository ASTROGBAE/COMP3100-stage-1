import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Communication {

    // method used options: FC | FF | BF | WF
    private String method = "FF"; // method options:

    // socket fields
    String user;
    Socket socket;
    BufferedReader din;
    DataOutputStream dout;

    // data structyures
    ArrayList<Server> servers;
    int serverIdk;
    Queue<Job> jobQueue;

    // CONSTRUCTOR

    public Communication(Socket _socket) {
        socket = _socket;
        user = System.getProperty("user.name"); // get system name
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
        sendMessage("AUTH " + user);
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
        // System.out.print("Recieved job msg: " + msg + "...");
        if (msg != null) {
            // regex process
            Pattern pattern = Pattern.compile(jobsRegex);
            Matcher matcher = pattern.matcher(msg);
            if (matcher.find()) {
                if (matcher.group(1).equals("JOBN")) { // check message is valid for server (JOBN)
                    Job j = new Job(msg);
                    jobQueue.add(j); // add new job to jobs!
                    // System.out.println("Job added: " + jobID + ": " + j.toString());
                    return 1;
                } else if (matcher.group(1).equals("JCPL")) { // job finished!
                    // System.out.println("Job finished!");
                    return 2;
                }
            }
        }
        // System.out.println("Did not work!");
        return -1; // did not work!
    }

    // get server group from GETS capable, operate on new server list...
    private Server getNextServer(Job job, int _scheduledJobs) throws Exception {
        // TODO add check that a job exists in
        if (job != null) {
            wipeServers(); // clean servers list for next operation
            sendMessage("GETS Capable " + job.getGetsString()); // send message to server to get a server type,
                                                                // increment
            // schedule
            Integer dataNum = getDataAmount(getMessage()); // get amount of data from message if available

            System.out.println(dataNum); 
            Map<Server, Integer> fitServers = new LinkedHashMap<Server, Integer>(); // map fitness values to servers
            sendMessage("OK"); // send confirmation to server, recieved DATA
            if (dataNum > 0) {
                for (int i = 0; i < dataNum; i++) { // get server list
                    loadServer(getMessage());
                } // load server
                sendMessage("OK"); // send confirmation to server, recieved servers
                getMessage();
                // sort servers
                servers.sort((Server a, Server b) -> { // before analysis, sort servers by core size
                    return a.getCores() - b.getCores(); // TODO should we do this or will it screw up the algorithm??
                });
                // TODO moved scheduling information to communication, dunno what else to do
                if (method.equals("FC")) {
                    return servers.get(0);
                } else if (method.equals("FF") || method.equals("BF") || method.equals("WF")) {
                    int jobScheduledDifference = _scheduledJobs; // track how many jobs remaining in order to stop LSTJ
                                                                 // checks or not
                    for (Server s : servers) { // search servers for valid option
                        if (serverReady(s)) { // if readily available
                            if (jobScheduledDifference > 0) { // if jobs are scheduled but not account for in
                                                              // search...
                                sendMessage("LSTJ " + s.getTypeID()); // request list of servers
                                int jobNums = getDataAmount(getMessage()); // get data amount
                                sendMessage("OK"); // recieve job data
                                getMessage();
                                sendMessage("OK");
                                if (jobNums == 0) { // ALL conditions satisfied (ready and no jobs scheduled)
                                    // success!
                                    if (method.equals("FF")) {
                                        return s; // return first fit, if it exists
                                    } else { // if BF or WF, simply add to server list to work on it later...
                                        // fitness value: cores of server-core requirement of job
                                        // when fitness values calculated, find best choice
                                        fitServers.put((Server) s, (int) (s.getCores() - job.getCores()));
                                    }
                                } else { // free of scheduled jobs condition not satisfied, go to next server
                                    jobScheduledDifference -= jobNums; // decrement different of jobs by how many are on
                                                                       // this server
                                    continue; // go to next server in list
                                }
                            } else { // if first job, don't worry about job scheduling on servers
                                return s; // return first fit, if it exists
                            }
                        }
                    }
                    if (!fitServers.isEmpty()) { // server iteration complete, only BF or WF left
                        Server target = null; // server to return, not null if a server meets "BF" criteria
                        for (Server s : servers) { // find target server
                            if (serverReady(s)) {
                                if (target == null) { // if no target found, choose first viable option
                                    target = s;
                                } else { // target not null, choose s if highest store
                                    if (fitnessComparison(fitServers.get(s), fitServers.get(target), method) == 1) {
                                        target = s; // choose new
                                    }
                                }
                            }
                        }
                        if (target == null) { // if no server meets above criteria
                            for (Server s : servers) { // find any server, most valid fitness
                                if (target == null) { // first runthough, get highest
                                    target = s;
                                } else { // target not null, valid comparison method
                                    if (fitnessComparison(fitServers.get(s), fitServers.get(target), method) == 1) {
                                        target = s; // choose new
                                    }
                                }
                            }
                        }
                        return target; // return target server that meets fitness
                    }
                }
            }
        } else {
            // System.out.println("No servers! Trying again...");
            return null;
        }
        // System.out.println("Getting server...: " + servers.get(0).toString());
        return null;

    }

    // satisfy first condition of FF: state must be inactive or active second
    // condition: has sufficient resources (covered by capable command?)
    private Boolean serverReady(Server s) {
        return s.getState().equals("active") || s.getState().equals("inactive");
    }

    // if BF, return '1' if a is bigger. if WF, return '1' if a is smaller.
    private int fitnessComparison(int aFitness, int bFitness, String method) {
        if (method.equals("BF")) {
            return aFitness - bFitness;
        } else { // == WF
            return bFitness - aFitness;
        }
    }

    private Integer getDataAmount(String data) {
        // server
        System.out.println(data);
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
        String serverRegex = "^([^ ]+) (\\d+) .*";
        // TODO should regex have .* at the end?
        if (serverMsg != null && serverMsg.matches(serverRegex)) { // check message is valid for server
            Server s = new Server(serverMsg);
            if (s.isValid()) {
                servers.add(new Server(serverMsg)); // add server
                return true; // success!
            }
        }
        System.out.println("WARNING: could not load server");
        return false; // failure
    }

    public boolean attemptScheduleJob(int _scheduledJobs) throws Exception {
        if (jobQueue != null && !jobQueue.isEmpty()) { // check if jobs are empty
            Job _job = jobQueue.poll(); // pop job off queue
            if (_job != null) { // check there is a job and servers are not
                                // empty
                Server _server = getNextServer(_job, _scheduledJobs); // get server as per what the algorithm
                                                                      // in that method
                // states
                if (_server != null) {
                    sendMessage(String.format("SCHD %s %s", _job.getID(), _server.getTypeID())); // send
                                                                                                 // scheduling
                    // instrument to server
                    // TODO how to signal from reply that is was correct sent?
                    getMessage(); // get responce
                    return true;
                } else {
                    // System.out.println("No server! Getting server and attempting again...");
                }
            }
        } else {
            // System.out.println("Job queue null or empty!");

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
        // System.out.println("Greetings " + user);
        // System.out.println(String.format("Target IP: %s Target Port: %s",
        // socket.getInetAddress(), socket.getPort()));
        // System.out.println(String.format("Local IP: %s Local Port: %s",
        // socket.getLocalAddress(), socket.getLocalPort()));
    }

    private void sendMessage(String msg) throws IOException {
        dout.write((msg + "\n").getBytes()); // send OK for jobs
        dout.flush();
        // System.out.println("Sent msg: " + msg);
    }

    private String getMessage() throws IOException {
        String msg = din.readLine();
        // System.out.println("Recieved msg: " + msg);
        return msg;
    }

    private Boolean matchResponse(String expectedMsg) throws IOException {
        if (getMessage().equals(expectedMsg)) {
            return true;
        }
        return false;
    }
}
