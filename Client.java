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
            System.out.println(e);
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
            System.out.print("Sending HELO... ");
            try {
                while (!comms.attemptHelo()) {
                    System.out.println("Cannot connect to server. Trying again...");
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                System.out.println("IOException in attempting HELO, printing stack trace...");
                e.printStackTrace();
            }
            System.out.println("SUCCESS: server OK");
            // Auth attempt
            System.out.print("Attempting AUTH...");
            try {
                while (!comms.attemptAuth()) {
                    System.out.println("Cannot get authentication. Trying again...");
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                System.out.println("IOException in attempting to get authentication, printing stack trace...");
                e.printStackTrace();
            }
            System.out.println("SUCCESS: server OK with AUTH");
            // get servers
            try {
                while (!comms.attemptGetServers()) {
                    System.out.println("Cannot get server list. Trying again...");
                }
                System.out.println("SUCCESS: server list recieved");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                System.out.println("IOException in attempting to get servers, printing stack trace...");
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                System.out.println("Exception in attempting to intialise job scheduler, printing stack trace...");
                e.printStackTrace();
            }
            // job and schedule loop
            int attempt = 1; // attempt log to be incremented each loop
            try {
                do {
                    // get job
                    try {
                        if (!comms.attemptGetJobs()) {
                            System.out.println(String.format("[%s] Could not find a job", attempt));
                        }
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        System.out.println("IOException in attempting to get next job, printing stack trace...");
                        e.printStackTrace();
                    }
                    // schedule job
                    if (!comms.attemptScheduleJob()) {
                        System.out.println(String.format("[%s] Could not find a job", attempt));
                    }
                    attempt++; // increment attempt log
                } while (comms.attemptGetJobs()); // TODO make condition better, is there a way to check and see if
                                                  // there are still jobs remaining in the simulation?
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // when no jobs exist, attempt to close

        }
    }
}
