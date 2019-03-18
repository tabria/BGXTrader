package trader.candle;

public interface CircularQueue<E> {
    int size();

    boolean isEmpty();

    void enqueue(E element);
}
