public class Server {

    String type;
    int limit, bootupTime, hourlyRate, cores, memory, disk;

    public Server(String _type, int _limit, int _bootupTime, int _hourlyRate, int _cores, int _memory, int _disk) {
        type = _type;
        limit = _limit;
        bootupTime = _bootupTime;
        hourlyRate = _hourlyRate;
        cores = _cores;
        memory = _memory;
        disk = _disk;
    }

}
