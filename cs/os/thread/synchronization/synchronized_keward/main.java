package os.thread.synchronization.synchronized_keward;

public class main {
    public static void main(String args[]) {
        Factory factory = new Factory();
        Customer c1 = new Customer("c1", factory);
        Customer c2 = new Customer("c2", factory);

        new Thread(c1).start();
        new Thread(c2).start();
    }
}
