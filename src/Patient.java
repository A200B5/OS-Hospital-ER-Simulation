public class Patient extends Thread {

    private int id;
    private WaitingRoom waitingRoom;

    public Patient(int id, WaitingRoom waitingRoom) {
        this.id = id;
        this.waitingRoom = waitingRoom;
    }

    @Override
    public void run() {

        // Patient arrival and waiting room logic

    }

    public int getPatientId() {
        return id;
    }
}