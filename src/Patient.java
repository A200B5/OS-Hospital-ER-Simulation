public class Patient extends Thread {

    private int id;
    private WaitingRoom waitingRoom;

    public Patient(int id, WaitingRoom waitingRoom) {
        this.id = id;
        this.waitingRoom = waitingRoom;
    }

    @Override
   public void run() {
            System.out.println("P" + id + " arrived.");
            waitingRoom.addPatient(this);
            assignedByDoctor.acquire();
            treatmentFinished.acquire();
            System.out.println("P" + id + " left.");
        }
    
    public void assignToDoctor() {
        assignedByDoctor.release();
    }
    public void completeTreatment() {
        treatmentFinished.release();
    }
    public void sendBackToWaitingRoom() {
        waitingRoom.readdPatient(this);
    }
    public int getPatientId() {
        return id;
    }
    public String toString() {
        return "P" + id;
    }
}
