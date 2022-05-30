import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server {

    private Object[] params = new Object[15];
    // store objects: serverType, serverID, state, curStateTime, core, memory, disk,
    // wJobs, rJobs, failures, totalFailtime, mttf, mttr, madf, lastStartTime;
    private int getParamLength = 9; // buffer for objects read in initialisation
    private int totalTurnaroundTime;
    private boolean valid = false; // boolean to check if job is valid from input param (jobn)
    private ArrayList<Schedule> schedules; // schedules of each job currently operating on the server

    public Server(String gets) {
        // by default, the GETS command will get data for type to rjobs (failures to
        // lastStartTime not included)
        totalTurnaroundTime = 0; // init total list of turnaround times
        schedules = new ArrayList<Schedule>(); // init schedule list
        String serverRegex = "^([^ ]+) (\\d+) (inactive|booting|idle|active|unavailable) -?(\\d+) (\\d+) (\\d+) (\\d+) (\\d+) (\\d+)";
        if (gets != null && !gets.isEmpty() && gets.matches(serverRegex)) { // check gets is valid for server
            Pattern pattern = Pattern.compile(serverRegex); // regex process
            Matcher matcher = pattern.matcher(gets);
            if (matcher.find()) { // group matches
                for (int i = 0; i < getParamLength; i++) { // loop to enter values via regex
                    if (i == 0 || i == 2) { // if serverType or state (parse string instead of int)
                        params[i] = matcher.group(i + 1); // read in all string values
                    } else {
                        params[i] = Integer.parseInt(matcher.group(i + 1)); // read in all int values
                    }
                }
            }
            // System.out.println("Server loaded successfully.");
            valid = true;
        }
    }

    // fields
    public String getTypeID() {
        return params[0] + " " + params[1];
    }

    public int getCores() {
        return (int) params[4];
    }

    public String getState() {
        return (String) params[2];
    }

    public int getCurStartTime() {
        return (int) params[3];
    }

    public boolean isValid() {
        return valid;
    }

    // set total turnaround times of each schedule, if they exist
    public void setTotalTurnaroundTime() {
        if (!schedules.isEmpty()) {
            int total = 0;
            for (Schedule s : schedules) {
                total += s.turnaroundTime;
            }
            totalTurnaroundTime = total; // update new turnaround time
        } else { // if empty simply return booting time, 0
            switch (getState()) {
                case ("booting"): // add from
                case ("inactive"):
                    totalTurnaroundTime = getCurStartTime(); // beginning of start time from booting
                    break;
                case ("unavailable"): // add VERY high weight, not good
                    totalTurnaroundTime = 9999999; // beginning of start time from booting
                    break;
                default: // if idle or active
                    totalTurnaroundTime = 0; // means server is good to go!
            }
        }
    }

    // get total turnaround times of each schedule, if they exist
    public int getTotalTurnaroundTime() {
        return totalTurnaroundTime;
    }

    // add new schedule to list
    public void addSchedule(Schedule _schedule) {
        schedules.add(_schedule);
    }

    // remove first schedule from the list (FIFO)
    public void removeSchedule() {
        if (!schedules.isEmpty()) {
            schedules.remove(schedules.size() - 1);
        }
    }
}
