public class TreatmentRoom {

    private Semaphore available;

    public TreatmentRoom(int capacity) {
        available = new Semaphore(capacity);
    }

    public boolean acquire(long timeout) {
        return available.tryP(timeout);
    }

    public void release() {
        available.V();
    }
}