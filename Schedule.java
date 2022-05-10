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

    // method used options: FC | FF | BF | WF
    private String method = "FF"; // method options:

    public Schedule() {
    }

    // methods for stage 2...
    // getCapable: list of servers from DATA return

    public String getMethod() {
        return method;
    }

    public Server getNextServer(ArrayList<Server> _servers, int reqCores) {
        if (_servers != null && !_servers.isEmpty() && method != null && !method.isEmpty()) {
            _servers.sort((Server a, Server b) -> { // before analysis, sort servers by core size
                return a.getCores() - b.getCores(); // TODO should we do this or will it screw up the algorithm??
            });
            if (method.equals("FC")) { // first capable method, return the first server that is capable
                return _servers.get(0);
            } else if (method.equals("FF")) { // first fit
                for (Server s : _servers) {
                    if (s.getState().equals("active") || s.getState().equals("inactive")) {
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
