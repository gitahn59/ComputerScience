package os.thread.synchronization.synchronized_keward;

public class Factory {
    int count = 10;

    public int getCount() {
        return count;
    }

    public void sell(int num){
        if(count >=num){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            count -=num;
        }
    }
}


