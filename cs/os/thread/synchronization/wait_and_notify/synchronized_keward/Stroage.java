package os.thread.synchronization.wait_and_notify.synchronized_keward;

import java.util.ArrayList;

public class Stroage {
    static int MAX_SIZE = 5;
    ArrayList<String> box = new ArrayList<>();

    public synchronized void add(String item){
        if(box.size() < MAX_SIZE){
            box.add(item);
            System.out.printf("공장에서 %s를 생산했습니다.\n", item);
            print();
        }else{
            System.out.println("창고가 가득차 공장이 기다립니다.");
        }
    }

    public boolean remove(String item){
        synchronized (this) {
            while(box.size()==0){
                String name = Thread.currentThread().getName();
                System.out.printf("%s가 아이템 생산을 기다리고 있습니다.\n", name);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            for (int i = 0; i < box.size(); i++) {
                if (box.get(i).equals(item)) {
                    box.remove(i);
                    return true;
                }
            }
        }

        return false;
    }

    public void print(){
        System.out.println(box.toString());
    }
}
