package os.thread.synchronization.lock_and_condition.reentrant_lock;

public class Customer implements Runnable{
    String name;
    String item;
    Stroage stroage;

    public Customer(String name, String item, Stroage stroage) {
        this.name = name;
        this.item = item;
        this.stroage = stroage;
    }

    public void buy(){
        stroage.remove(item);
    }

    @Override
    public void run() {
        while(true){
            buy();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
