package os.thread.synchronization.synchronized_keward;

public class Customer implements Runnable{
    String name;
    Factory factory;

    public Customer(String name, Factory factory) {
        this.name = name;
        this.factory = factory;
    }

    @Override
    public void run() {
        while(factory.getCount()>0){
            int cnt = (int)(Math.random() * 3) +1;
            factory.sell(cnt);
            System.out.println("공장의 잔여 아이템 수 : " + factory.getCount());
        }
    }
}
