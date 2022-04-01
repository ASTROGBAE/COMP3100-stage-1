import java.util.ArrayList;

import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import javax.xml.parsers.*;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

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
        if (xmlRead) {
            // decrement idx
            if (idx <= serverType.size()) {
                idx = serverType.size() - 1;
            } else {
                idx--;
            }
            // get server type with index
            return serverType.get(idx);
        }
        return null; // no xml read yet
    }

    // read methods
    // TODO fix throws to be more complicated???
    public boolean readTypes(String xml) throws ParserConfigurationException, SAXException, IOException {
        if (xml != null && !xml.isEmpty()) {
            // read and build xml string
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xml));
            // create document for server nodes
            Document doc = builder.parse(is);
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
            idx = serverType.size() - 1;
            xmlRead = true; // now can call other objects!
            return true; // done! all read
        }
        return false; // xml empty or null, return false...
    }

    /**
     * for testing purposes when an xml is not available
     * 
     * @return
     */
    private boolean readTestArray(ArrayList<String> testArray) {
        serverType = testArray;
        return true;
    }
}
