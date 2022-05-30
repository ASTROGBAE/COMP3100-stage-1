// waiting time is defined as the submission time if booting or inactive, 0 if server is active/idle and free, >0 otherwise
// waiting time is unrealistically big if unavailable

// TODO plan
// make a schedule list in each server, calculate out stats while scheduler is trying to choose an option.
// loop through all servers the job can take, map each option with a performance value.
// choose the performance value which is lowest (highest? idk)

public class Schedule {
    int submitTime, waitingTime, startTime, endTime, turnaroundTime, rentalCost, resourceUtil;

    public Schedule(Job _job, Server _server) {
        submitTime = _job.getsubmitTime(); // get job submit time (correct)
        // waiting time logic from state
        switch (_server.getState()) {
            case ("booting"): // add from
            case ("inactive"):
                waitingTime = _server.getCurStartTime(); // beginning of start time from booting
                break;
            case ("unavailable"): // add VERY high weight, not good
                waitingTime = 9999999; // beginning of start time from booting
                break;
            default: // if idle or active
                waitingTime = _server.getTotalTurnaroundTime(); // get total turnaround time present in server
        }
        startTime = submitTime + waitingTime; // TODO is this right?
        endTime = _job.getEstRuntime(); // TODO should I base off of estimated runtime?
        turnaroundTime = startTime + endTime;
        rentalCost = 0; // TODO complete
        resourceUtil = 0;
    }
}
