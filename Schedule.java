import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

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
                int current_core = Integer.parseInt(((Element) _server).getAttribute("cores")); // get current core to
                                                                                                // compare to previous
                if (current_core > prev_core) { // if new biggest server type (do not change if the same)
                    largest = type;
                    prev_core = current_core;
                }
            }
        }
        xmlRead = true; // now can call other objects!
        return true; // done! all read
    }

    // methods for stage 2...
    // getCapable: list of servers from DATA return
    public Server getNextServer(String getCapable, int reqCores) {
        ArrayList<Server> servers = new ArrayList<Server>(); // list to store servers in for processing
        Scanner scanner = new Scanner(getCapable); // get scanner to read server list line by line
        while (scanner.hasNextLine()) { // read in servers line by line, add to server arraylist
            Server s = new Server(scanner.nextLine());
            if (s.isValid()) { // read in new server, if it is valid
                servers.add(s);
            }
        }
        scanner.close(); // finish scanning in server list form capable return
        if (!servers.isEmpty()) { // if servers added, continue
            getMethod(servers, reqCores, "");
        }
        return null; // is servers is empty
    }

    private Server getMethod(ArrayList<Server> _servers, int reqCores, String method) {
        if (_servers != null && !_servers.isEmpty() && method != null && !method.isEmpty()) {
            _servers.sort((Server a, Server b) -> { // before analysis, sort servers by core size
                return a.getCores() - b.getCores(); // TODO should we do this or will it screw up the algorithm??
            });
            if (method.equals("FC")) { // first capable method, return the first server that is capable
                return _servers.get(0);
            } else if (method.equals("FF")) { // first fit
                for (Server s : _servers) {
                    if (serverReady(s)) {
                        return s; // return first fit, if it exists
                    }
                }
                // if no target found from above, simply return first capable TODO is this
                // right?
                return _servers.get(0);
            }

            else if (method.equals("BF") || method.equals("WF")) { // if best fit or worst fit
                // (similar algorithms)
                Map<Server, Integer> fitServers = new LinkedHashMap<Server, Integer>(); // map fitness values to servers
                // fitness value: cores of server-core requirement of job
                for (Server s : _servers) { // populate fitness map
                    fitServers.put((Server) s, (int) (s.getCores() - reqCores));
                }
                // when fitness values calculated, find best choice
                Server target = null; // server to return, not null if a server meets "BF" criteria
                for (Server s : _servers) { // find target server
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
                    for (Server s : _servers) { // find any server, most valid fitness
                        if (target == null) { // first runthough, get highest
                            target = s;
                        } else { // target not null, valid comparison method
                            if (fitnessComparison(fitServers.get(s), fitServers.get(target), method) == 1) {
                                target = s; // choose new
                            }
                        }
                    }
                }
                return target;
            }
        }
        return null; // invalid input
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
}
