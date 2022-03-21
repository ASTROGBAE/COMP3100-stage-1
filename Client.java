import java.io.*;
import java.net.*;
import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Client {

    // client fields
    boolean running;
    String user;
    ArrayList<Server> servers;
    ArrayList<Job> jobs;

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

    void run() {
        while (running) {
            printWelcome();
            // connection attempt
            // OK attempt
            System.out.print("Sending HELO... ");
            try {
                while (!attemptOk()) {
                    System.out.println("Cannot connect to server. Trying again...");
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
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
                e.printStackTrace();
            }
            System.out.println("SUCCESS: server OK");
            // Preparation
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

    // TODO test this in its own file?
    // done using xml SAXParser
    private boolean readConfig() {
        String FILENAME = "/users/mkyong/staff.xml";
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance(); // init factory

        try {
            DocumentBuilder db = dbf.newDocumentBuilder(); // parse xml file
            Document doc = db.parse(new File(FILENAME)); // // create new doc

            // get <server>
            NodeList serverNodes = doc.getElementsByTagName("server");// get server objects

            for (int i = 0; i < serverNodes.getLength(); i++) {
                Node _server = serverNodes.item(i);

                if (_server.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) _server;

                    // TODO refactor code below it is UGLY AF

                    // get server attributes
                    String type = element.getAttribute("type");
                    int limit = Integer.parseInt(element.getAttribute("limit"));
                    int bootUpTime = Integer.parseInt(element.getAttribute("bootUpTime"));
                    float hourlyRate = Integer.parseInt(element.getAttribute("hourlyRate"));
                    int cores = Integer.parseInt(element.getAttribute("cores"));
                    int memory = Integer.parseInt(element.getAttribute("memory"));
                    int disk = Integer.parseInt(element.getAttribute("disk"));

                    // initialise server object, add new list
                    servers.add(new Server(type, limit, bootUpTime, hourlyRate, cores, memory, disk));
                }
            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return false;

    }

    // ~~~~~~~~~~~~~~~ COMMAND CATEGORY: connection ~~~~~~~~~~~~~~~

    private Boolean attemptOk() throws IOException {
        String msgOk = "HELO";
        dout.write((msgOk + "\n").getBytes());
        dout.flush();
        String reply = getMessage();
        if (reply.equals("OK")) {
            return true;
        }
        return false;
    }

    private Boolean attemptAuth() throws IOException {
        String msgAuth = "AUTH: "; // TODO implement auth info with port, etc?
        dout.write((msgAuth + user + "\n").getBytes());
        dout.flush();
        String reply = getMessage();
        if (reply.equals("OK")) {
            return true;
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

    private boolean readSystem() {
        return false;
    }

    private boolean attemptPreparation() throws IOException {
        dout.write(("REDY\n").getBytes()); // send ready message to server,
        dout.flush();
        return true;
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
