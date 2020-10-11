package thread.synchronization.wait_and_notify.synchronized_keward;

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
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}


