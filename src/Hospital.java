import java.util.Scanner;

public class Hospital {

    public static void main(String[] args) throws InterruptedException {

        Scanner input = new Scanner(System.in);

        System.out.print("Enter waiting room capacity: ");
        int capacity = input.nextInt();

        System.out.print("Enter number of doctors: ");
        int doctorsCount = input.nextInt();

        System.out.print("Enter number of patients: ");
        int patientsCount = input.nextInt();


        System.out.println("Waiting room capacity: " + capacity);
        System.out.println("Number of doctors: " + doctorsCount);
        System.out.println("Number of patients: " + patientsCount);



        WaitingRoom waitingRoom = new WaitingRoom(capacity);
        TreatmentRoom treatmentRoom = new TreatmentRoom(2);


        Doctor[] doctors = new Doctor[doctorsCount];

        for (int i = 0; i < doctorsCount; i++) {
            doctors[i] = new Doctor(i + 1, waitingRoom, treatmentRoom);
        }


        Patient[] patients = new Patient[patientsCount];


        System.out.println("Patients arriving:");

        for (int i = 0; i < patientsCount; i++) {
            patients[i] = new Patient(i + 1, waitingRoom);
            System.out.print("P" + i + " ");
        }

        System.out.print("\n");

        for (int i = 0; i < doctorsCount; i++) {
            doctors[i].start();
        }


        for (int i = 0; i < patientsCount; i++) {
            patients[i].start();
        }


        //Termination

        // Wait for all patients to finish
        for (Patient patient : patients) {
            patient.join();
        }

        System.out.println("All patients have been treated.");

        for (int i = 0; i < doctorsCount; i++) {
            waitingRoom.produce(null);
        }


        for (Doctor doctor : doctors) {
            doctor.join();
        }


        input.close();
        System.out.println("Simulation completed.");
    }
}