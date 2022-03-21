public class Job {

    String type;
    int minRunTime, maxRunTime, populationRate;

    public Job(String _type, int _minRunTime, int _maxRunTime, int _populationRate) {
        type = _type;
        minRunTime = _minRunTime;
        maxRunTime = _maxRunTime;
        populationRate = _populationRate;
    }
}
