/*******************************************************************************
 * Copyright (c) 28/12/2020. Author Doriela Grabocka. All rights reserved.
 ******************************************************************************/

package book_examples.gui_multithreading.sequential_parallel_streams;

import javax.sound.midi.Soundbank;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.LongSummaryStatistics;
import java.util.stream.LongStream;

public class StreamStatisticsComparison {
    public static void main(String[] args){
        SecureRandom random = new SecureRandom();

        long[] values = random.longs(10_000_000,1,1001).toArray();
        Instant seperateStart = Instant.now();
        long count = Arrays.stream(values).count();
        long sum = Arrays.stream(values).sum();
        long min = Arrays.stream(values).min().getAsLong();
        long max = Arrays.stream(values).max().getAsLong();
        double average = Arrays.stream(values).average().getAsDouble();
        Instant seperateEnd = Instant.now();

        System.out.println("Calculations performed separately:");
        System.out.printf("    count: %,d%n", count);
        System.out.printf("    sum: %,d%n", sum);
        System.out.printf("    min: %,d%n", min);
        System.out.printf("    max: %,d%n", max);
        System.out.printf("    average: %f%n", average);
        System.out.printf("Total time in milliseconds: %d%n%n", Duration.between(seperateStart, seperateEnd).toMillis());

        LongStream stream1 = Arrays.stream(values);
        System.out.println("Calculating statistics on sequential stream");
        Instant sequeantialStart = Instant.now();
        LongSummaryStatistics results1 = stream1.summaryStatistics();
        Instant sequeantialEnd = Instant.now();

        displayStatistics(results1);
        System.out.printf("Total time in milliseconds: %d%n%n", Duration.between(sequeantialStart, sequeantialEnd).toMillis());

        LongStream stream2 = Arrays.stream(values).parallel();
        System.out.println("Calculating statistics on parallel stream");
        Instant parallelStart = Instant.now();
        LongSummaryStatistics results2 = stream2.summaryStatistics();
        Instant parallelEnd = Instant.now();

        displayStatistics(results2);
        System.out.printf("Total time in milliseconds: %d%n%n", Duration.between(parallelStart, parallelEnd).toMillis());
    }

    private static void displayStatistics(LongSummaryStatistics stats){
        System.out.println("Statistics");
        System.out.printf("    count: %,d%n", stats.getCount());
        System.out.printf("    sum: %,d%n", stats.getSum());
        System.out.printf("    min: %,d%n", stats.getMin());
        System.out.printf("    max: %,d%n", stats.getMax());
        System.out.printf("    average: %f%n", stats.getAverage());
    }
}
