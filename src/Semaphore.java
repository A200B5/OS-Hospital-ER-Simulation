public class Semaphore {
    protected int value = 0;

    public Semaphore() {
        value = 0;
    }

    public Semaphore(int initial) {
        value = initial;
    }

    public synchronized void P() {
        while (value <= 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        value--;
    }

    public synchronized boolean tryP(long timeout) {
        if (value > 0) {
            value--;
            return true;
        }

        long deadline = System.currentTimeMillis() + timeout;
        long remaining = timeout;

        while (value <= 0 && remaining > 0) {
            try {
                wait(remaining);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
            remaining = deadline - System.currentTimeMillis();
        }

        if (value > 0) {
            value--;
            return true;
        }

        return false;
    }

    public synchronized void V() {
        value++;
        notifyAll();
    }
}