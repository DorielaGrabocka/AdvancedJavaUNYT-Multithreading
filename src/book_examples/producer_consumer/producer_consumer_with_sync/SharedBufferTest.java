package book_examples.producer_consumer.producer_consumer_with_sync;

import book_examples.producer_consumer.producer_consumer_without_sync.Buffer;

import java.security.SecureRandom;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SharedBufferTest {

    public static void main(String[] args) throws InterruptedException{
        ExecutorService executorService = Executors.newCachedThreadPool();
        Buffer sharedLocation = new SynchronizedBuffer();
        System.out.printf("%-40s%s\t\t%s%n%n%-40s%s%n%n",
                "Operation",
                "Buffer",
                "Occupied",
                "---------",
                "------\t\t--------");
        executorService.execute(new Producer(sharedLocation));
        executorService.execute(new Consumer(sharedLocation));

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);
    }
}

class SynchronizedBuffer implements Buffer{
    private int buffer=-1;
    private boolean occupied=false;

    public synchronized void displayState(String operation){
        System.out.printf("%-40s%d\t\t%b%n%n", operation, buffer, occupied);
    }

    @Override
    public synchronized void blockingPut(int value) throws InterruptedException {
        while(occupied){
            System.out.println("Producer tries to write");
            displayState("Buffer full. Producer waits.");
            wait();
        }

        buffer = value;
        occupied=true;
        displayState("Producer writes: "+buffer);
        notifyAll();//tell waiting threads to enter runnable state
    }

    @Override
    public synchronized int blockingGet() throws InterruptedException {
        while(!occupied){
            System.out.println("Consumer tries to read");
            displayState("Buffer empty. Consumer waits.");
            wait();
        }
        occupied=false;
        displayState("Consumer reads: "+buffer);
        notifyAll();//this return immediately after notfying other threads and the buffer is returned
        return  buffer;
    }
}

class Producer implements Runnable{
    private static final SecureRandom generator = new SecureRandom();
    private final Buffer sharedLocation;
    public Producer(Buffer sharedLocation) {
        this.sharedLocation = sharedLocation;
    }

    @Override
    public void run() {
        int sum = 0;
        for (int i=1; i<=10; i++){
            try{
                Thread.sleep(generator.nextInt(3000));
                sum+=i;
                sharedLocation.blockingPut(i);
                //System.out.printf("\t\t%2d%n",sum);
            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }//end of for
        System.out.printf("Producer done producing%nTerminating Producer%n");
    }
}//end class Producer

class Consumer implements Runnable{
    private static final SecureRandom generator = new SecureRandom();
    private final Buffer sharedLocation;

    public Consumer(Buffer sharedLocation) {
        this.sharedLocation = sharedLocation;
    }

    @Override
    public void run() {
        int sum = 0;

        for (int i=1; i<=10; i++){
            try{
                Thread.currentThread().sleep(generator.nextInt(3000));
                sum+=sharedLocation.blockingGet();
                //System.out.printf("\t\t\t\t\t%2d%n",sum);
            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }//end of for
        System.out.printf("%n%s %d%n%s%n",
                "Consumer read values totalling", sum, "Terminating Consumer");
    }
}//end class Consumer

