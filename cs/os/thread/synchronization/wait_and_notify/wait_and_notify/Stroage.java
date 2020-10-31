package os.thread.synchronization.wait_and_notify.wait_and_notify;

import java.util.ArrayList;

public class Stroage {
    static int MAX_SIZE = 5;
    ArrayList<String> box = new ArrayList<>();

    public synchronized void add(String item){
        while(box.size()>= MAX_SIZE){
            System.out.println("창고가 가득차 공장이 기다립니다.");
            try {
                wait(); // lock을 잠시 반납
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.printf("공장에서 %s를 생산했습니다.\n", item);
        box.add(item);
        print();
        notify();
    }

    public void remove(String item){
        synchronized (this) {
            String name = Thread.currentThread().getName();
            while(box.size()==0){ // 아이템 생산 대기
                System.out.printf("%s가 아이템 생산을 기다리고 있습니다.\n", name);
                try {
                    wait(); // lock을 잠시 반납
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            for (int i = 0; i < box.size(); i++) {
                if (box.get(i).equals(item)) {
                    box.remove(i);
                    notify(); // cook 또는 다른 Customer를 깨움
                    return;
                }
            }
            try {
                // 아이템 생산 대기
                System.out.printf("%s가 아이템 생산을 기다리고 있습니다.\n", name);
                wait(); // lock을 잠시 반납
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void print(){
        System.out.println(box.toString());
    }
}
