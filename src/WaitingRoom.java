public class WaitingRoom {

    private int capacity;
    private Object store[] ;

    private int inptr = 0;
    private int outptr = 0;

    Semaphore spaces ;
    Semaphore elements = new Semaphore(0);

    WaitingRoom(int capacity) {
        this.capacity = capacity;
        store = new Object[capacity];
        spaces = new Semaphore(capacity);
    }

    public void produce(Object patient) {

        if (!spaces.tryP(0)) {
            if (patient != null)
                System.out.println("Waiting room is full.\n" + patient + " is waiting outside for an available seat.");

            spaces.P();
        }

        synchronized (this) {
            store[inptr] = patient;
            inptr = (inptr + 1) % capacity;

            if (patient != null)
                System.out.println(patient + " entered the waiting room." + "\n");

        }

        elements.V();
    }
    public Object consume() {
        Object value;
        elements.P();
            synchronized (this) {
                value = store[outptr];
                outptr = (outptr + 1) % capacity;
                if ( value != null )
                    System.out.println(value + " was removed from the waiting room.");
            }
            spaces.V();
            return value;


    }

}
