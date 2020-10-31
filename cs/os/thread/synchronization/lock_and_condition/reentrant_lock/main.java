package os.thread.synchronization.lock_and_condition.reentrant_lock;

public class main {
    public static void main(String args[]) {
        Stroage stroage = new Stroage();

        Factory factory = new Factory(stroage);
        Customer c1 = new Customer("c1", "computer", stroage);
        Customer c2 = new Customer("c2", "audio", stroage);

        new Thread(factory).start();
        new Thread(c1,"c1").start();
        new Thread(c2,"c2").start();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
