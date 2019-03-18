package trader.candle;

import trader.exceptions.NullArgumentException;

public class CircularArrayQueue<E> implements CircularQueue<E> {

    private static final int DEFAULT_CAPACITY = 1000;
    private E[] queueData;
    private int startIndex;
    private int endIndex;
    private int size;

    public  CircularArrayQueue(){
        this(DEFAULT_CAPACITY);
    }

    public CircularArrayQueue(int capacity){
    queueData = (E[]) new Object[capacity];
    startIndex = 0;
    endIndex = 0;
    size = 0;
    }

    @Override
    public int size(){
        return size;
    }

    @Override
    public boolean isEmpty(){
        return size == 0;
    }

    @Override
    public void enqueue(E element){
        if(element == null) throw new NullArgumentException();
        queueData[startIndex] = element;
        size++;
    }

}