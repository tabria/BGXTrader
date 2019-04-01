package trader.interactor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trader.exception.NullArgumentException;
import trader.requestor.Request;
import trader.responder.Response;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AddIndicatorUseCaseTest {

    private UseCase addIndicatorUseCase;
    private Request request;
    
    @Before
    public void setUp() throws Exception {
        addIndicatorUseCase = new AddIndicatorUseCase();
        request = mock(Request.class);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallExecuteWithNull_Exception(){
        addIndicatorUseCase.execute(null);
    }

    @Test
    public void WhenCallExecuteWithCorrectValue_CorrectResult(){
        when(request.getRequestDataStructure()).thenReturn("Test");
        Response<String> response = addIndicatorUseCase.execute(request);

        assertEquals("Test", response.getResponseDataStructure());
    }

}
