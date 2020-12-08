/*
 * Copyright (c)  7/12/2020. Author Doriela Grabocka. All rights reserved.
 */

package book_examples.producer_consumer.circular_buffer;

public interface Buffer {
    void blockingPut(int value) throws InterruptedException;
    int blockingGet() throws InterruptedException;
    void displayState(String message) throws InterruptedException;


}
