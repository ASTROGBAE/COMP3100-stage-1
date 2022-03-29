public class Schedule {

    private int serverNumber, index;

    public Schedule(int _serverNumber) throws Exception {
        if (_serverNumber >= 0) {
            serverNumber = _serverNumber;
            index = _serverNumber;
        } else {
            throw new Exception("Exception: invalid Schedule server number (<=0)");
        }
    }

    // get methods
    public int getindex() {
        int idx = index; // get index to return before decrementing
        decriment();
        return idx;
    }

    // other methods
    public void setIndex(int number) {
        if (number >= 0) {
            index = number;
        }
    }

    public void setServerNumbers(int number) {
        if (number > 0) {
            serverNumber = number - 1;
        }
    }

    private void decriment() {
        if (index > 0) { // if greater than zero, decriment
            index--;
        } else { // if ==0, go back to length-1!
            index = serverNumber - 1;
        }
    }
}
