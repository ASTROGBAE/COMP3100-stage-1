import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server {

    private String serverType, state;
    private int serverID, curStateTime, core, memory, disk,
            wJobs, rJobs, failures, totalFailtime, mttf, mttr, madf, lastStartTime; // server parameter fields, need to
                                                                                    // get these from
    private Object[] params = { serverType, serverID, state, curStateTime, core, memory, disk,
            wJobs, rJobs, failures, totalFailtime, mttf, mttr, madf, lastStartTime };
    private boolean valid = false; // boolean to check if job is valid from input param (jobn)

    public Server(String gets) {
        // by default, the GETS command will get data for type to rjobs (failures to
        // lastStartTime not included)
        String serverRegex = "^([^ ]+) (\\d+) (inactive|booting|idle|active|unavailable) (\\d+) (\\d+) (\\d+) (\\d+) (\\d+) (\\d+)";
        if (gets != null && !gets.isEmpty() && gets.matches(serverRegex)) { // check gets is valid for server
            Pattern pattern = Pattern.compile(serverRegex); // regex process
            Matcher matcher = pattern.matcher(gets);
            if (matcher.find()) { // group matches
                serverType = matcher.group(1);
                serverID = Integer.parseInt(matcher.group(2));
                for (int i = 0; i < params.length; i++) { // loop to enter values via regex
                    if (i == 0 || i == 2) { // if serverType or state (parse string instead of int)
                        params[i] = Integer.parseInt(matcher.group(i + 1)); // read in all int values
                    }
                    params[i] = Integer.parseInt(matcher.group(i + 1)); // read in all int values
                }
            }
            // System.out.println("Server loaded successfully.");
            valid = true;
        }
    }

    // fields
    public String getTypeID() {
        return serverType + " " + serverID;
    }

    public int getCores() {
        return core;
    }

    public String getState() {
        return state;
    }

    public boolean isValid() {
        return valid;
    }
}
