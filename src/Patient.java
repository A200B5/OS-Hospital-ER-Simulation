public class Patient extends Thread {


    private int id;
    private WaitingRoom waitingRoom;

    private Semaphore assignedByDoctor;
    private Semaphore treatmentFinished;

    public Patient(int id, WaitingRoom waitingRoom) {
        this.id = id;
        this.waitingRoom = waitingRoom;

        assignedByDoctor = new Semaphore(0);
        treatmentFinished = new Semaphore(0);
    }

    @Override
    public void run() {
        System.out.println("P" + id + " arrived.");

        waitingRoom.produce(this);

        assignedByDoctor.P();

        treatmentFinished.P();

        System.out.println("P" + id + " left.");
    }

    public void assignToDoctor() {
        assignedByDoctor.V();
    }

    public void completeTreatment() {
        treatmentFinished.V();
    }

    public void sendBackToWaitingRoom() {
        waitingRoom.produce(this);
    }

    public int getPatientId() {
        return id;
    }

    @Override
    public String toString() {
        return "P" + id;
    }

}
