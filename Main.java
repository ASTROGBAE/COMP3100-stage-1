class Main {

    // TODO figure out if to use connection-orientated (socket) or datagram
    // (connect-less)?
    // for now, assume CONNECTION-ORIENTATED!

    // TODO implement general process

    static String address = "localhost";
    static int port = 6666;

    static Client client = new Client(address, port);

    // main method
    public static void main(String[] args) {
        client.attemptOk();
        client.attemptAuth();
    }
}