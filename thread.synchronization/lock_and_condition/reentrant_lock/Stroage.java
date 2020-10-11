package thread.synchronization.lock_and_condition.reentrant_lock;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Stroage {
    static int MAX_SIZE = 5;
    ArrayList<String> box = new ArrayList<>();

    private ReentrantLock lock = new ReentrantLock();
    // lock은 공유하므로 Add, Remove 중에서 1개만 동시에 수행 가능
    private Condition forFactory = lock.newCondition();
    private Condition forCustomer = lock.newCondition();

    public void add(String item){
        lock.lock();
        try {

            while (box.size() >= MAX_SIZE) {
                System.out.println("창고가 가득차 공장이 기다립니다.");
                try {
                    forFactory.await(); // Factory가 lock을 반납
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.printf("공장에서 %s를 생산했습니다.\n", item);
            box.add(item);
            print();
            forCustomer.signal(); // Customer에게 알림
        }finally {
            lock.unlock();
        }
    }

    public void remove(String item){
        lock.lock();
        try {
            String name = Thread.currentThread().getName();
            while (box.size() == 0) { // 아이템 생산 대기
                System.out.printf("Empty, %s가 %s 생산을 기다리고 있습니다.\n", name, item);
                try {
                    forCustomer.await(); // Customer는 lock을 반납
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            for (int i = 0; i < box.size(); i++) {
                if (box.get(i).equals(item)) {
                    box.remove(i);
                    System.out.printf("%s가 %s를 구매했습니다.\n", name, item);
                    System.out.print(item + "구매 후 : ");
                    print();
                    forFactory.signal(); // Factory에게 Item을 공급하도록 signal
                    return;
                }
            }
            try {
                // 아이템 생산 대기
                System.out.printf("%s가 %s 생산을 기다리고 있습니다.\n", name, item);
                forCustomer.await(); // Factory의 Item 공급을 대기
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }finally { // try-finally 를 이용한 lock 처리
            lock.unlock();
        }
    }

    public void print(){
        System.out.println(box.toString());
    }
}
