public class Doctor extends Thread {

    private int id;
    private WaitingRoom waitingRoom;
    private TreatmentRoom treatmentRoom;

    public Doctor(int id, WaitingRoom waitingRoom,
                  TreatmentRoom treatmentRoom) {

        this.id = id;
        this.waitingRoom = waitingRoom;
        this.treatmentRoom = treatmentRoom;
    }

    @Override
    public void run() {
        while (true) {

            Object item = waitingRoom.consume();

            // Check for termination signal
            if (item == null) {
                System.out.println("Doctor " + id + " is leaving.");
                return;
            }

            Patient patient = (Patient) item;

            // Treatment logic will go here

        }

    }
}