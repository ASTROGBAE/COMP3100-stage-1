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
    private int idx;

    // data structures
    private ArrayList<String> serverType;
    private boolean xmlRead;

    public Schedule() {
        serverType = new ArrayList<String>();
        xmlRead = false;
    }

    // get methods

    /**
     * 
     * @return String of next server type (descending in size).
     *         Null if server xml not read
     */
    public String getNextType() {
        // if (xmlRead) {
        //     // decrement idx
        //     idx --;
        //     if (idx < 0) {
        //         idx = serverType.size() -1;
        //     }
        //     // get obj
        //     String next = serverType.get(idx); // next strng to return
        //     // get server type with index
        //     System.out.print(next + "[" +idx+ "], ");
        //     return next;
        // }
        return serverType.get(serverType.size()-1); // no xml read yet
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
        for (int i = 0; i < serverNodes.getLength(); i++) { // check each server type
            Node _server = serverNodes.item(i);
            if (_server.getNodeType() == Node.ELEMENT_NODE) { // check if element type, need to cast before getting
                                                              // element attribute
                String type = ((Element) _server).getAttribute("type"); // get attribute "type"
                serverType.add(type); // add to server types list
            }
        }
        idx = serverType.size(); // will decrement in object...
        xmlRead = true; // now can call other objects!
        System.out.println("Read servers: " + serverType.toString());
        return true; // done! all read
    }
}
