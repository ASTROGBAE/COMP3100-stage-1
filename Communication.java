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
    private Server getNextServer(Job job, boolean firstJob) throws Exception {
        // TODO add check that a job exists in
        if (job != null) {
            wipeServers(); // clean servers list for next operation
            sendMessage("GETS Capable " + job.getGetsString()); // send message to server to get a server type,
                                                                // increment
            // schedule
            String[] rawServerData = getData(getMessage()); // get amount of data from message if available
            if (rawServerData != null && rawServerData.length > 0) {
                for (String s : rawServerData) { // convert server raw data into server list
                    loadServer(s);
                }
                // iterate through server list and populate schedule (need to do this each time
                // as servers are wiped each it)
                for (Server server : servers) {
                    sendMessage("LSTJ " + server.getTypeID()); // get current job data
                    String[] rawJobData = getData(getMessage()); // get amount of data from message if available
                    if (rawJobData != null && rawJobData.length != 0) {
                        for (String jobStr : rawJobData) {
                            server.addSchedule(new Schedule(new Job(jobStr), server));
                        }
                    }
                    server.setTotalTurnaroundTime(); // calculate turnaround time based on populated schedule
                }
                // iterate through server list and update turnaround times based on queued jobs
                for (Server server : servers) {
                    server.setTotalTurnaroundTime();
                }
                // sort servers by increasing turnarouround time
                servers.sort((Server a, Server b) -> { // before analysis, sort servers by core size
                    return a.getTotalTurnaroundTime() - b.getTotalTurnaroundTime();
                });
                // add sheduled job to server schedule list for first in list
                servers.get(0).addSchedule(new Schedule(job, servers.get(0)));
                return servers.get(0); // return the first value with the smallest turnaround time
            }
        } else {
            // System.out.println("No servers! Trying again...");
            return null;
        }
        // System.out.println("Getting server...: " + servers.get(0).toString());
        return null;

    }

    private String[] getData(String data) throws IOException {
        String dataRegex = "^DATA (\\d+) .*";
        // regex process
        Pattern pattern = Pattern.compile(dataRegex);
        Matcher matcher = pattern.matcher(data);
        if (data == null | !matcher.find()) { // group matches
            return null;
        }
        // if data is valid, continue
        int dataNum = Integer.parseInt(matcher.group(1)); // number of data items to add
        String[] dataList = new String[dataNum]; // list of valid data strings to return
        if (dataNum <= 0) {
            sendMessage("OK"); // recieve job data (will be ".")
            getMessage();
        } else { // data number > 0, meaning something exists to be recorded!
            sendMessage("OK"); // confirm to send over job info (will be newlines of length dataNum)
            for (int i = 0; i < dataNum; i++) {
                dataList[i] = getMessage(); // read in reposonce
            }
            sendMessage("OK"); // confirm data is recieved, answer will be "."
            getMessage();
        }
        return dataList;
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
        // System.out.println("WARNING: could not load server");
        return false; // failure
    }

    public boolean attemptScheduleJob(boolean _firstJob) throws Exception {
        if (jobQueue != null && !jobQueue.isEmpty()) { // check if jobs are empty
            Job _job = jobQueue.poll(); // pop job off queue
            if (_job != null) { // check there is a job and servers are not
                                // empty
                Server _server = getNextServer(_job, _firstJob); // get server as per what the algorithm
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