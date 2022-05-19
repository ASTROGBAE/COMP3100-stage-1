public class Schedule {
    Job job;
    Server server;
    int submitTime, waitingTime, startTime, endTime, turnaroundTime, rentalCost, resourceUtil;

    public Schedule(Job _job, Server _server) {
        submitTime = _job.getsubmitTime(); // get job submit time
        waitingTime = Integer.parseInt(_server.getCurStartTime()); // beginning of start time
        // todo should I take into account state for this??
        startTime = submitTime + waitingTime; // TODO is this right?
        endTime = job.getEstRuntime(); // TODO should I base off of estimated runtime?
        turnaroundTime = startTime + endTime;
    }
}
