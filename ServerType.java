
public class ServerType {
    String type;
    float hourlyRate;
    int limit, bootupTime, cores, memory, disk;

    public ServerType(String _type, int _limit, int _bootupTime, float _hourlyRate, int _cores, int _memory,
            int _disk) {
        type = _type;
        limit = _limit;
        bootupTime = _bootupTime;
        hourlyRate = _hourlyRate;
        cores = _cores;
        memory = _memory;
        disk = _disk;
    }
}
