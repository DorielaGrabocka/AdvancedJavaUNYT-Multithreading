package book_examples.producer_consumer.producer_consumer_without_sync;

import java.security.SecureRandom;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SharedBufferTest {
    public static void main(String[] args) throws InterruptedException{
        UnsynchronizdBuffer  sharedLocation = new UnsynchronizdBuffer();

        ExecutorService executorService = Executors.newCachedThreadPool();

        System.out.println("Action\t\t\tValue\tSum of Produced\tSum of Consumed");
        System.out.printf("------\t\t\t-----\t---------------\t----------------%n%n");

        executorService.execute(new Producer(sharedLocation));
        executorService.execute(new Consumer(sharedLocation));

        executorService.shutdown();

        executorService.awaitTermination(1, TimeUnit.MINUTES);

        System.out.println();
        System.out.println("---------------------------------------------------------------------");
        System.out.println("---------------------------------------------------------------------");
        System.out.println("Now using ArrayBlockingQueue");
        ExecutorService executorService1 = Executors.newCachedThreadPool();

        BlockingBuffer  sharedLocation1 = new BlockingBuffer();

        executorService1.execute(new Producer(sharedLocation1));
        executorService1.execute(new Consumer(sharedLocation1));

        executorService1.shutdown();
        executorService1.awaitTermination(1, TimeUnit.MINUTES);
    }
}

class UnsynchronizdBuffer implements Buffer {
    private int buffer =-1;

    @Override
    public void blockingPut(int value) throws InterruptedException {
        System.out.printf("Producer writes \t%2d", value);
        buffer=value;
    }

    @Override
    public int blockingGet() throws InterruptedException {
        System.out.printf("Consumer reads \t\t%2d", buffer);
        return buffer;
    }
}

class BlockingBuffer implements Buffer{
    private final ArrayBlockingQueue<Integer> buffer;

    public BlockingBuffer() {
        this.buffer = new ArrayBlockingQueue<>(1);
    }

    public void blockingPut(int value) throws InterruptedException{
        buffer.put(value);
        System.out.printf("Producer writes \t%2d\t%s%d%n", value, "Buffer cells occupied: ", buffer.size());
    }

    public int blockingGet() throws InterruptedException {
        int readValue = buffer.take();
        System.out.printf("Consumer reads \t\t%2d\t%s%d%n",
                readValue,
                "Buffer cells occupied: ",
                buffer.size());
        return readValue;
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
                System.out.printf("\t\t%2d%n",sum);
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
                System.out.printf("\t\t\t\t\t%2d%n",sum);
            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }//end of for
        System.out.printf("%n%s %d%n%s%n",
                "Consumer read values totalling", sum, "Terminating Consumer");
    }
}//end class Consumer
