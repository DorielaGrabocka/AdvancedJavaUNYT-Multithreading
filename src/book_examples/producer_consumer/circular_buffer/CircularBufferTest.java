/*
 * Copyright (c)  7/12/2020. Author Doriela Grabocka. All rights reserved.
 */

package book_examples.producer_consumer.circular_buffer;

import java.security.SecureRandom;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CircularBufferTest {
    public static void main(String[] args) throws InterruptedException{
        ExecutorService executorService = Executors.newCachedThreadPool();

        CircularBuffer sharedLocation = new CircularBuffer();
        sharedLocation.displayState("Initial state");

        executorService.execute(new Producer(sharedLocation));
        executorService.execute(new Consumer(sharedLocation));

        executorService.shutdown();

        executorService.awaitTermination(1, TimeUnit.MINUTES);
    }
}//end of Test class

class CircularBuffer implements Buffer{
    private final int[] buffer= {-1,-1,-1};
    private int occupiedCells=0;
    private int writeIndex=0;
    private int readIndex=0;

    @Override
    public synchronized void blockingPut(int value) throws InterruptedException{
        while(occupiedCells==buffer.length){
            System.out.printf("Buffer is full! Producer waits.%n");
            wait();
        }

        buffer[writeIndex] = value;
        writeIndex = (writeIndex+1)%buffer.length;
        occupiedCells++;
        displayState("Producer writes "+ value);
        notifyAll();
    }

    @Override
    public synchronized int blockingGet() throws InterruptedException{
        while(occupiedCells==0){
            System.out.printf("Buffer is empty! Consumer waits.%n");
            wait();
        }
        int value = buffer[readIndex];
        readIndex = (readIndex+1)%buffer.length;
        occupiedCells--;
        displayState("Consumer reads "+value);
        notifyAll();
        return value;
    }

    @Override
    public synchronized void displayState(String message) throws InterruptedException{
        System.out.printf("%s%s%d)%n%s", message, " (buffer cells occupied: ", occupiedCells, " buffer cells: ");

        for (int value: buffer) {
            System.out.printf("  %2d  ", value);
        }

        System.out.printf("%n                   ");

        for (int i = 0; i < buffer.length; i++)
            System.out.print("---- ");


        System.out.printf("%n                   ");

        for (int i = 0; i < buffer.length; i++) {
            if(i==writeIndex && i==readIndex)
                System.out.print(" WR ");
            else  if(i==writeIndex)
                System.out.print(" W ");
            else if(i==readIndex)
                System.out.print(" R ");
            else
                System.out.print("   ");
        }

        System.out.printf("%n%n");

    }
}//end of CircularBuffer class


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
