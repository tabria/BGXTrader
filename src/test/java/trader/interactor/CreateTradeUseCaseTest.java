package trader.interactor;

import org.junit.Before;
import org.junit.Test;
import trader.exception.NullArgumentException;
import trader.requestor.Request;
import trader.requestor.UseCase;
import trader.responder.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CreateTradeUseCaseTest {

    private UseCase createTradeUseCase;
    private Request request;

    @Before
    public void setUp() throws Exception {
        createTradeUseCase = new CreateTradeUseCase();
        request = mock(Request.class);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallExecuteWithNull_Exception(){

        //createTradeUseCase.execute(null);
    }

    @Test
    public void WhenCallExecuteWithCorrectValue_CorrectResult(){
        when(request.getBody()).thenReturn("Test");
        Response<String> response = createTradeUseCase.execute(request);

        assertEquals("Test", response.getBody());
    }

}
