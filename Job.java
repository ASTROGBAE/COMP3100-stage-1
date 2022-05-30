import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Job {

    private int[] params = new int[6]; // array to store params submitTime, jobID, estRuntime, core, memory, disk
    private boolean valid = false; // boolean to check if job is valid from input param (jobn)

    public Job(String jobn) {
        if (jobn != null && !jobn.isEmpty()) {
            // JOBN REGEX
            // JOBN submitTime jobID estRuntime core memory disk
            String jobsRegex = "^(\\w{4}) (\\d+) (\\d+) (\\d+) (\\d+) (\\d+) (\\d+)";
            Pattern pattern = Pattern.compile(jobsRegex);
            Matcher matcher = pattern.matcher(jobn);
            if (matcher.find() && matcher.group(1).equals("JOBN")) {
                for (int i = 0; i < params.length; i++) {
                    params[i] = Integer.parseInt(matcher.group(i + 2)); // read in all values
                }
                valid = true;
            } else {
                // LSTJ REGEX
                // jobID jobState submitTime startTime estRunTime core memory disk
                // requirements
                jobsRegex = "^(\\d+) (\\d+) (\\d+) (\\d+) (\\d+) (\\d+) (\\d+) (\\d+)";
                pattern = Pattern.compile(jobsRegex);
                matcher = pattern.matcher(jobn);
                if (matcher.find()) { // if matches LSTJ command
                    params[0] = Integer.parseInt(matcher.group(3)); // submitTime
                    params[1] = Integer.parseInt(matcher.group(1)); // jobID
                    params[2] = Integer.parseInt(matcher.group(5)); // estRunTime
                    params[3] = Integer.parseInt(matcher.group(6)); // core
                    params[4] = Integer.parseInt(matcher.group(7)); // memory
                    params[5] = Integer.parseInt(matcher.group(8)); // disk
                }
            }

        }
    }

    public int getsubmitTime() {
        return params[0];
    }

    public int getID() {
        return params[1];
    }

    public int getEstRuntime() {
        return params[2];
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
