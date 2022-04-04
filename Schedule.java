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

public class Schedule {

    // tracking variables
    private String largest;

    // data structures
    private boolean xmlRead;

    public Schedule() {
        xmlRead = false;
    }

    // get methods

    /**
     * 
     * @return String of next server type (descending in size).
     *         Null if server xml not read
     */
    public String getNextType() {
        return largest; // no xml read yet
    }

    // read methods
    // TODO fix throws to be more complicated???
    public boolean readTypes() throws ParserConfigurationException, SAXException, IOException {
        // read and build xml string
        String configPath = "ds-system.xml"; // read if in same folder
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(configPath));
        // get type nodes...
        NodeList serverNodes = doc.getElementsByTagName("server");// get server objects
        // iterate through server nodes, read type into type array
        int prev_core = 0;
        for (int i = 0; i < serverNodes.getLength(); i++) { // check each server type
            Node _server = serverNodes.item(i);
            if (_server.getNodeType() == Node.ELEMENT_NODE) { // check if element type, n(eed to cast before getting
                                                              // element attribute
                String type = ((Element) _server).getAttribute("type"); // get attribute "type"
                int current_core = Integer.parseInt(((Element) _server).getAttribute("cores")); // get current core to compare to previous
                if (current_core > prev_core) { // if new biggest server type (do not change if the same)
                    largest = type;
                    prev_core = current_core;
                }
            }
        }
        xmlRead = true; // now can call other objects!
        return true; // done! all read
    }
}
