package book_examples.producer_consumer.producer_consumer_without_sync;

public interface Buffer {
    public void blockingPut(int value) throws InterruptedException;
    public int blockingGet() throws InterruptedException;
}
