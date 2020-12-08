/*******************************************************************************
 * Copyright (c) 8/12/2020. Author Doriela Grabocka. All rights reserved.
 ******************************************************************************/

package book_examples.producer_consumer.lock_and_condition;
import book_examples.producer_consumer.circular_buffer.Buffer;

import java.security.SecureRandom;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SharedBufferTest2 {
    public static void main(String[] args) throws InterruptedException{
        ExecutorService executorService = Executors.newCachedThreadPool();
        SynchronizedBuffer sharedLocation = new SynchronizedBuffer();

        System.out.printf("%-40s%s\t\t%s%n%-40s%s%n%n", "Operation",
                "Buffer", "Occupied", "---------", "------\t\t---------");

        executorService.execute(new Producer(sharedLocation));
        executorService.execute(new Consumer(sharedLocation));

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);
    }
}

class SynchronizedBuffer implements Buffer{

    //Lock to control synchronization with this buffer
    private final Lock accessLock = new ReentrantLock();

    //conditions to control reading and writing
    private final Condition canWrite = accessLock.newCondition();
    private final Condition canRead = accessLock.newCondition();

    private int buffer = -1; //shared by producer and consumer threads
    private boolean occupied = false; //whether buffer is occupied

    @Override
    public void blockingPut(int value) throws InterruptedException {
        accessLock.lock();
        try{
            while(occupied){
                System.out.printf("Buffer full. Producer waits!\n");
                canWrite.await();
            }

            buffer=value;//set new value for buffer
            //indicate that the producer cannot store another value
            occupied = true;
            displayState("Producer writes: "+buffer);
            canRead.signalAll();//signal all waiting threads to acquire the lock
        }
        finally{
            accessLock.unlock();
        }
    }

    @Override
    public int blockingGet() throws InterruptedException {
        accessLock.lock();
        int value;

        try{
            while (!occupied){
                System.out.printf("Consumer tries to read.");
                displayState("Buffer is empty! Consumer waits\n");
                canRead.await();
            }
            value = buffer;
            occupied = false;
            displayState("Consumer reads: "+value);
            canWrite.signalAll();
        }
        finally{
            accessLock.unlock();
        }

        return value;
    }

    @Override
    public void displayState(String message) throws InterruptedException {

        try{
            accessLock.lock();
            System.out.printf("%-40s%d\t\t%b%n%n", message, buffer, occupied);
        }
        finally {
            accessLock.unlock();
        }

    }
}//end of SynchronizedBuffer


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