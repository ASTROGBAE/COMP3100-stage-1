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

    // void run() {
    // while (running) {
    // printWelcome();
    // // connection attempt
    // // OK attempt
    // System.out.print("Sending HELO... ");
    // try {
    // while (!attemptHelo()) {
    // System.out.println("Cannot connect to server. Trying again...");
    // }
    // } catch (IOException e) {
    // // TODO Auto-generated catch block
    // System.out.println("IOException in attempting HELO, printing stack
    // trace...");
    // e.printStackTrace();
    // }
    // System.out.println("SUCCESS: server OK");
    // // Auth attempt
    // System.out.print("Attempting AUTH as user: " + user + "...");
    // try {
    // while (!attemptAuth()) {
    // System.out.println("Cannot get authentication. Trying again...");
    // }
    // } catch (IOException e) {
    // // TODO Auto-generated catch block
    // System.out.println("IOException in attempting to get authentication, printing
    // stack trace...");
    // e.printStackTrace();
    // }
    // System.out.println("SUCCESS: server OK");
    // // Preparation
    // }
    // }
}
