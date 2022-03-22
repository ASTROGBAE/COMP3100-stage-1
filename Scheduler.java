import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import org.w3c.dom.*;
import org.xml.sax.SAXException;
import javax.xml.parsers.*;
import java.io.File;
import java.io.IOException;

public class Scheduler {
    static final String configPath = "./config_samples/ds-sample-config01.xml";
    ArrayList<Server> servers;
    Queue<Job> jobQueue;

    public Scheduler() {
        servers = new ArrayList<Server>();
        jobQueue = new LinkedList<Job>();
    }

    // TODO test this in its own file?
    // done using xml SAXParser
    public boolean readConfig() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance(); // init factory

        try {
            DocumentBuilder db = dbf.newDocumentBuilder(); // parse xml file
            Document doc = db.parse(new File(configPath)); // // create new doc

            // get <server> and <job> nodes
            NodeList serverNodes = doc.getElementsByTagName("server");// get server objects
            NodeList jobNodes = doc.getElementsByTagName("job");// get job objects

            for (int i = 0; i < serverNodes.getLength(); i++) { // add server objects
                Node _server = serverNodes.item(i);
                if (_server.getNodeType() == Node.ELEMENT_NODE) {
                    servers.add(elementToServer((Element) _server));
                }
            }
            for (int i = 0; i < jobNodes.getLength(); i++) { // add job objects
                Node _job = jobNodes.item(i);
                if (_job.getNodeType() == Node.ELEMENT_NODE) {
                    jobQueue.add(elementToJob((Element) _job));
                }
            }
            return true; // success!

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return false;

    }

    private Server elementToServer(Element element) {
        // TODO refactor code below it is UGLY AF

        // get server attributes
        String type = element.getAttribute("type");
        int limit = Integer.parseInt(element.getAttribute("limit"));
        int bootupTime = Integer.parseInt(element.getAttribute("bootupTime"));
        float hourlyRate = Float.parseFloat(element.getAttribute("hourlyRate"));
        int cores = Integer.parseInt(element.getAttribute("cores"));
        int memory = Integer.parseInt(element.getAttribute("memory"));
        int disk = Integer.parseInt(element.getAttribute("disk"));
        return new Server(type, limit, bootupTime, hourlyRate, cores, memory, disk);
    }

    private Job elementToJob(Element element) {
        // TODO refactor code below it is UGLY AF

        // get attributes
        String type = element.getAttribute("type");
        int populationRate = Integer.parseInt(element.getAttribute("populationRate"));
        int maxRunTime = Integer.parseInt(element.getAttribute("maxRunTime"));
        int minRunTime = Integer.parseInt(element.getAttribute("minRunTime"));
        return new Job(type, populationRate, maxRunTime, minRunTime);
    }
}
