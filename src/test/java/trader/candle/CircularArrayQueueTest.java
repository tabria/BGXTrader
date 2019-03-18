package trader.candle;

import org.junit.Before;
import org.junit.Test;
import trader.CommonTestClassMembers;
import trader.exceptions.NullArgumentException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class CircularArrayQueueTest {

    private CommonTestClassMembers commonMembers;
    private CircularQueue<CandleStick> queue;
    private CandleStick mockCandle;

    @Before
    public void setUp() throws Exception {
        commonMembers = new CommonTestClassMembers();
        queue = new CircularArrayQueue<>();
        mockCandle = mock(CandleStick.class);
    }

    @Test
    public void createCircularQueueWithDefaultCapacity(){
        CircularQueue<CandleStick> queue = new CircularArrayQueue<>();
        Object[] queueData = (Object[]) commonMembers.extractFieldObject(queue, "queueData");
        assertEquals(1000, queueData.length);
    }

    @Test
    public void createCircularQueueWithCustomCapacity(){
        CircularQueue<CandleStick> queue = new CircularArrayQueue<>(20);
        Object[] queueData = (Object[]) commonMembers.extractFieldObject(queue, "queueData");
        assertEquals(20, queueData.length);
    }

    @Test
    public void testSizeWithEmptyQueue(){
        assertEquals(0, queue.size());
    }

    @Test
    public void testIsEmptyOnEmptyQueue(){
        assertTrue(queue.isEmpty());
    }

    @Test
    public void testIsEmptyWithNotEmptyQueue(){
        queue.enqueue(mockCandle);
        assertFalse(queue.isEmpty());
    }

    @Test
    public void WhenEnqueueSizeMustIncrease(){
        int initialSize = queue.size();
        queue.enqueue(mockCandle);
        queue.enqueue(mockCandle);
        assertEquals(initialSize+2, queue.size());
    }

    @Test(expected = NullArgumentException.class)
    public void WhenEnqueueNullElement_Exception(){
        queue.enqueue(null);
    }
}
