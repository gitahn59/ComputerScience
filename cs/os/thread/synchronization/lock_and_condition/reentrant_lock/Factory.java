package os.thread.synchronization.lock_and_condition.reentrant_lock;

public class Factory implements Runnable{
    public Factory(Stroage stroage) {
        this.stroage = stroage;
    }

    Stroage stroage;
    String [] items = {"computer", "audio", "audio"};

    public void supply(){
        String item = items[(int)(Math.random()*3)];
        stroage.add(item);
    }

    @Override
    public void run() {
        while(true) {
            supply();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}


