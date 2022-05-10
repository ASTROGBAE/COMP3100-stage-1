import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Job {

    private int[] params = new int[6]; // array to store params submitTime, jobID, estRuntime, core, memory, disk
    private boolean valid = false; // boolean to check if job is valid from input param (jobn)

    public Job(String jobn) {
        // regex details
        String jobsRegex = "^(\\w{4}) (\\d+) (\\d+) (\\d+) (\\d+) (\\d+) (\\d+)";
        // TODO check if you need to match everything else in msg (.* at the end?)
        Pattern pattern = Pattern.compile(jobsRegex);
        Matcher matcher = pattern.matcher(jobn);
        if (jobn != null && !jobn.isEmpty() && matcher.find()) {
            if (matcher.group(1).equals("JOBN")) { // if valid, then add details!
                for (int i = 0; i < params.length; i++) {
                    params[i] = Integer.parseInt(matcher.group(i + 2)); // read in all values
                }
                valid = true;
            }
        }
    }

    public int getID() {
        return params[1];
    }

    public String getGetsString() {
        return params[3] + " " + params[4] + " " + params[5];
    }

    public boolean isValid() {
        return valid;
    }

    public int getCores() {
        return params[3];
    }
}
