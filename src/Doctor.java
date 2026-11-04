import java.util.Random;

public class Doctor extends Thread {

    private int id;
    private WaitingRoom waitingRoom;
    private TreatmentRoom treatmentRoom;
    private Random random;

    public Doctor(int id, WaitingRoom waitingRoom, TreatmentRoom treatmentRoom) {
        this.id = id;
        this.waitingRoom = waitingRoom;
        this.treatmentRoom = treatmentRoom;
        this.random = new Random();
    }

    @Override
    public void run() {
        while (true) {


            Object item = waitingRoom.consume();


            if (item == null) {
                System.out.println("Doctor " + id + " is leaving.");
                return;
            }

            Patient patient = (Patient) item;


            System.out.println("Doctor " + id + " is assigned to P" + patient.getPatientId() + ".");
            patient.assignToDoctor();


            System.out.println("Doctor " + id + " is trying to acquire a treatment room.");
            
            if (treatmentRoom.acquire(3000)) {

                System.out.println("Doctor " + id + " acquired a treatment room.");
                System.out.println("Doctor " + id + " begins treating P" + patient.getPatientId() + ".");


                try {
                    int treatmentTime = 5000 + random.nextInt(3000);
                    Thread.sleep(treatmentTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                System.out.println("Doctor " + id + " finished treating P" + patient.getPatientId() + ".");
                treatmentRoom.release();
                System.out.println("Doctor " + id + " released the treatment room.");
                
                patient.completeTreatment();
                
                System.out.println("Doctor " + id + " is now available.");
                
            } else {
                // Failure Scenario: Could not acquire a Treatment Room within 3 seconds
                System.out.println("Doctor " + id + " could not acquire a treatment room.");
                System.out.println("Doctor " + id + " is sending P" + patient.getPatientId() + " back to the waiting room.");
                

                patient.sendBackToWaitingRoom();
                
                System.out.println("Doctor " + id + " is now available.");
            }


        }
    }
}