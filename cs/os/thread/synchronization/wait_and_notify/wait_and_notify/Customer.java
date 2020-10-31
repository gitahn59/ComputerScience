package os.thread.synchronization.wait_and_notify.wait_and_notify;

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
        System.out.printf("%s가 %s를 구매했습니다.\n", name, item);

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
