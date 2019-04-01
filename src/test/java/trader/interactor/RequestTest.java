package trader.interactor;

import org.junit.Before;
import org.junit.Test;
import trader.exception.NullArgumentException;
import trader.entity.indicator.Indicator;
import trader.requestor.Request;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class RequestTest {

    private Request<ArrayList<String>> request;

    @Before
    public void setUp() {
        request = new RequestImpl<>();
    }

    @Test
    public void testToReturnCorrectDataStructure(){
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Test");
        request.setRequestDataStructure(arrayList);
        ArrayList<String> requestDataStructure = request.getRequestDataStructure();

        assertEquals("Test", requestDataStructure.get(0));
    }

}