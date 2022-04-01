import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTesting {

    // strings to test
    static String[] data = {
            "DATA 5 124"
    };

    static String[] jobs = {
            "JOBN 37 0 653 3 700 3800"
    };

    static String[] servers = {
            "juju 0 inactive -1 2 4000 16000 0 0",
            "juju 1 inactive -1 2 4000 16000 0 0",
            "joon 0 inactive -1 4 16000 64000 0 0",
            "joon 1 inactive -1 4 16000 64000 0 0",
            "super-silk 0 inactive -1 16 64000 512000 0 0"
    };

    static String dataRegex = "^DATA (\\d+) .*"; // return grouping from outer to inner: DATA 5, then 5
    // do matcher.group 0, 1, etc.
    // TADA! got it working!!
    static String jobsRegex = "^(\\w{4}) (\\d+) (\\d+) .*";
    // this regex may work with data, jobs and server!
    // --DATA:
    // 0: DATA 5
    // 1: DATA
    // 2: 5
    // --JOBS
    // 0:
    static String serverRegex = "^(\\w+) (\\d+) .*";

    static Pattern pattern = Pattern.compile(serverRegex);
    static Matcher matcher = pattern.matcher(servers[0]);

    public static void main(String[] args) {
        if (matcher.find()) {
            System.out.println(data[0].matches(dataRegex));
            System.out.println(matcher.group(0));
            System.out.println(matcher.group(1));
            System.out.println(matcher.group(2));
            System.out.println(matcher.group(3));
        }
    }
}
