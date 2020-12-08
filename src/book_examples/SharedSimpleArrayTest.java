package book_examples;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SharedSimpleArrayTest {
    public static void main(String[] args){
        SimpleArray array = new SimpleArray(6);
        ArrayWriter writer1 = new ArrayWriter(array, 1);
        ArrayWriter writer2 = new ArrayWriter(array, 11);

        ExecutorService executorService = Executors.newCachedThreadPool();

        executorService.execute(writer1);
        executorService.execute(writer2);

        executorService.shutdown();

        try{
            if(executorService.awaitTermination(1, TimeUnit.MINUTES)){
                System.out.println("Contents of SimpleArray:");
                System.out.println(array);
            }
            else{
                System.out.println("Time out while waiting for tasks to finish!");
            }
        }catch(InterruptedException e){
            System.out.println("Thread is interrupted!");
            e.printStackTrace();
        }


    }
}

class ArrayWriter implements Runnable{
    private final SimpleArray sharedSimpleArray;
    private final int startValue;

    public ArrayWriter(SimpleArray sharedSimpleArray, int startValue) {
        this.sharedSimpleArray = sharedSimpleArray;
        this.startValue = startValue;
    }

    @Override
    public void run() {
        for(int i=startValue; i<startValue+3;i++){
            sharedSimpleArray.add(i);
        }
    }
}

class SimpleArray{
    private final int[] array;
    private static final SecureRandom generator = new SecureRandom();
    private int writeIndex = 0;

    public SimpleArray(int size) {
        this.array = new int[size];
    }

    public synchronized void add(int value){
        int position = writeIndex;

        try{
            Thread.currentThread().sleep(generator.nextInt(500));
        }catch (InterruptedException e){
            System.out.println("Thread was interrupted");
        }
        //here we are updating the value
        array[position] = value;
        System.out.printf("%s wrote %2d to element %d.%n",
                Thread.currentThread().getName(),
                value,
                position);
        ++writeIndex;
        System.out.printf("The next writing index is %d%n", writeIndex);
    }

    @Override
    public String toString() {
        return Arrays.toString(array);
    }
}

