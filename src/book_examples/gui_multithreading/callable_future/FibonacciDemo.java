/*******************************************************************************
 * Copyright (c) 28/12/2020. Author Doriela Grabocka. All rights reserved.
 ******************************************************************************/

package book_examples.gui_multithreading.callable_future;


import java.text.NumberFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class FibonacciDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("Synchronous Long Running Calculations");
        TimeData synchnronousResult1  = startFibonacci(45);
        TimeData synchnronousResult2  = startFibonacci(44);
        double synchronousTime = calculateTime(synchnronousResult1, synchnronousResult2);
        System.out.printf("   Total calculation time = %.3f seconds%n", synchronousTime);

        System.out.printf("%nAsynchronous Long Running Calculations%n");
        CompletableFuture<TimeData> futureresult1 = CompletableFuture.supplyAsync(()->startFibonacci(45));
        CompletableFuture<TimeData> futureresult2 = CompletableFuture.supplyAsync(()->startFibonacci(44));

        //wait for results from asynchronous operations
        TimeData asynchronousResult1 = futureresult1.get();
        TimeData asynchronousResult2 = futureresult2.get();
        double asynchronousTime = calculateTime(asynchronousResult1,asynchronousResult2);

        System.out.printf("   Total calculation time = %.3f seconds%n", asynchronousTime);

        String percentage = NumberFormat.getPercentInstance().format(synchronousTime/asynchronousTime);
        System.out.printf("%nSynchronous calculations took %s more time than the asynchronous calculations%n", percentage);
    }

    private static double calculateTime(TimeData result1, TimeData result2) {
        TimeData bothThreads = new TimeData();
        bothThreads.start = result1.start.compareTo(result2.start)<0?result1.start:result2.start;
        bothThreads.end = result1.end.compareTo(result2.end)>0?result1.end:result2.end;

        return bothThreads.timeInSeconds();
    }

    private static TimeData startFibonacci(int i) {
        TimeData timeData = new TimeData();

        System.out.printf("  Calculating fibonacci(%d)%n",i);
        timeData.start = Instant.now();
        long fibonacciValue = fibonacci(i);
        timeData.end = Instant.now();

        displayResults(i, fibonacciValue, timeData);

        return timeData;
    }

    private static void displayResults(int n, long value, TimeData timeData) {
        System.out.printf("   fibonacci(%d) = %d%n", n, value);
        System.out.printf("   Calculation time for fibonacci(%d) = %.3f seconds%n", n, timeData.timeInSeconds());
    }

    private static long fibonacci(long i) {
        if(i == 0 || i==1)
            return i;
        return fibonacci(i-1)+fibonacci(i-2);
    }
}

class TimeData{
    public Instant start;
    public Instant end;

    public double timeInSeconds(){
        return Duration.between(start, end).toMillis()/1000.0;
    }
}//end class TimeData
