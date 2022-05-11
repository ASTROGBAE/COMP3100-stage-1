import java.io.*;
import java.net.*;

public class Client {

    // client fields
    boolean running;
    Communication comms;

    // client messages
    static String[] clientCommand = {
            "GETS" // TODO add all from specs
    };

    // constructor
    public Client(String address, int port) {
        try {
            comms = new Communication(new Socket(address, port));
            running = true;
        } catch (Exception e) {
            // System.out.println("ERROR: failed to create socket, ending programming...");
            running = false;
            // TODO write out what will happen if this fails???
        }
        // if all works, add in normal stuff...
    }

    // ~~~~~~~~~~~~~~~ RUN ALGORITHM ~~~~~~~~~~~~~~~

    void run() {
        while (running) {
            comms.printWelcome();
            // connection attempt
            // OK attempt
            // System.out.print("Sending HELO... ");
            try {
                int attempt = 0;
                while (!comms.attemptHelo()) {
                    // System.out.println("Cannot connect to server. Trying again... [" + attempt +
                    // "].");
                    attempt++;
                    if (attempt > 10) {
                        // System.out.println("Connection attempted timed out, exiting program");
                        running = false;
                        break;
                    }
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                // System.out.println("IOException in attempting HELO, printing stack
                // trace...");
                e.printStackTrace();
                running = false;
                break;
            }
            // System.out.println("SUCCESS: server OK");
            // Auth attempt
            // System.out.print("Attempting AUTH...");
            try {
                int attempt = 0;
                while (!comms.attemptAuth()) {
                    // System.out.println("Cannot get authentication. Trying again... [" + attempt +
                    // "].");
                    attempt++;
                    if (attempt > 10) {
                        // System.out.println("Connection attempted timed out, exiting program");
                        running = false;
                        break;
                    }
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                // System.out.println("IOException in attempting to get authentication, printing
                // stack trace...");
                e.printStackTrace();
                running = false;
                break;
            }
            // System.out.println("SUCCESS: server OK with AUTH");
            // job, server and schedule loop
            boolean jobsRemaining = true; // false if attemptGetJob == 0 or REDY returns NONE
            int scheduledJobs = 0; // tally of active jobs to indicate when to request job status (LTSJ) or not
            int attempt = 1; // attempt log to be incremented each loop
            do {
                // get job
                boolean jobRecieved = false; // only run the rest of the logic if the below attempt results in a job...
                try {
                    switch (comms.attemptGetJob()) { // attempt get job
                        case 2: // complete
                            // System.out.println(String.format("[%s] Job completed.", attempt));
                            scheduledJobs--; // one job off schedule
                            break;
                        case 1: // success
                            // System.out.println(String.format("[%s] Job recieved.", attempt));
                            jobRecieved = true;
                            break;
                        case 0: // no more jobs
                            // System.out.println(String.format("[%s] Server has no more jobs, ending
                            // scheduling loop...", attempt));
                            jobsRemaining = false; // change loop boolean
                            break;
                        case -1: // failure
                            // System.out.println(String.format("[%s] Job recieve was invalid, trying
                            // again.", attempt));
                            break;
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    // System.out.println("IOException in attempting to get job, printing stack
                    // trace...");
                    e.printStackTrace();
                    running = false;
                    break;
                }
                if (jobRecieved) { // only run this if a job has been recieved from above!
                    // get servers and attempt to get job
                    try {
                        if (comms.attemptScheduleJob(scheduledJobs)) {
                            scheduledJobs++; // one job scheduled...
                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        // System.out.println("Exception in attempting to schedule job, printing stack
                        // trace...");
                        e.printStackTrace();
                        running = false;
                        break;

                    }
                }
                attempt++; // increment log
            } while (jobsRemaining); // loop while still jobs remaining (i.e. server has no responded 'NONE' to
            // 'REDY')
            // when no job scheduling done, attempt to close
            // System.out.println("Client attempting to quit and close socket...");
            try {
                int attemptClose = 0;
                while (!comms.attemptQuitAndClose()) {
                    // System.out.println("Quite failed, attempting again... [" + attempt + "].");
                    attemptClose++;
                    if (attemptClose > 10) {
                        // System.out.println("Connection attempted timed out, exiting program");
                        running = false;
                        break;
                    }
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                running = false;
                break;
            }
            running = false;
        }
        // System.out.println("Quit successful. Exitting app...");
    }
}
