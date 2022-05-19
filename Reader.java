import java.util.ArrayList;

import org.w3c.dom.*;
import org.xml.sax.SAXException;
import javax.xml.parsers.*;
import java.io.File;
import java.io.IOException;

/**
 * Schedule process psuedocode:
 * initliase (no parameters)
 * read in xml string (will get types) (GETS system.xml from client)
 * While jobs exist:
 * Client: GETS + getNextType:
 * Server: sends number of servers
 * Client: reads servers, iterators through
 * 
 * if client runs out of servers:
 * Client: GETS + getNextType:
 * 
 */

public class Reader {

    String configPath = "ds-system.xml"; // read if in same folder

    public ArrayList<ServerType> getServerTypes() throws SAXException, IOException, ParserConfigurationException {
        // return var
        ArrayList<ServerType> s = new ArrayList<ServerType>();
        // read and build xml string
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(configPath));
        // get type nodes...
        NodeList serverNodes = doc.getElementsByTagName("server");// get server objects
        // iterate through server nodes, read type into type array
        for (int i = 0; i < serverNodes.getLength(); i++) { // check each server type
            Node _server = serverNodes.item(i);
            if (_server.getNodeType() == Node.ELEMENT_NODE) { // check if element type, n(eed to cast before getting
                                                              // element attribute
                String type = ((Element) _server).getAttribute("type"); // get attributes
                int limit = Integer.parseInt(((Element) _server).getAttribute("limit"));
                int bootupTime = Integer.parseInt(((Element) _server).getAttribute("bootupTime"));
                float hourlyRate = Float.parseFloat(((Element) _server).getAttribute("hourlyRate"));
                int cores = Integer.parseInt(((Element) _server).getAttribute("cores"));
                int memory = Integer.parseInt(((Element) _server).getAttribute("memory"));
                int disk = Integer.parseInt(((Element) _server).getAttribute("disk"));
                s.add(new ServerType(type, limit, bootupTime, hourlyRate, cores, memory, disk)); // add new type
            }
        }
        return s;
    }

    public ArrayList<JobType> getJobTypes() throws ParserConfigurationException, SAXException, IOException {
        // return var
        ArrayList<JobType> s = new ArrayList<JobType>();
        // read and build xml string
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(configPath));
        // get type nodes...
        NodeList serverNodes = doc.getElementsByTagName("server");// get server objects
        // iterate through server nodes, read type into type array
        for (int i = 0; i < serverNodes.getLength(); i++) { // check each server type
            Node _server = serverNodes.item(i);
            if (_server.getNodeType() == Node.ELEMENT_NODE) { // check if element type, n(eed to cast before getting
                                                              // element attribute
                String type = ((Element) _server).getAttribute("type"); // get attributes
                int minRunTime = Integer.parseInt(((Element) _server).getAttribute("minRunTime"));
                int maxRunTime = Integer.parseInt(((Element) _server).getAttribute("maxRunTime"));
                int populationRate = Integer.parseInt(((Element) _server).getAttribute("populationRate"));
                s.add(new JobType(type, minRunTime, maxRunTime, populationRate)); // add new type
            }
        }
        return s;
    }
}