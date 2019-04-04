package trader.interactor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trader.responder.Response;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class ResponseImplTest {

    private Response<ArrayList<String>> response;

    @Before
    public void setUp() throws Exception {
        response = new ResponseImpl<>();
    }

    @Test
    public void testToReturnCorrectDataStructure(){
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Test");
        response.setResponseDataStructure(arrayList);
        ArrayList<String> responseDataStructure = response.getResponseDataStructure();

        assertEquals("Test", responseDataStructure.get(0));
    }

}