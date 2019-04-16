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

public class AddOrderStrategyUseCaseTest {

    private UseCase addOrderStrategyUseCase;
    private Request request;

    @Before
    public void setUp() throws Exception {
        addOrderStrategyUseCase = new AddOrderStrategyUseCase();
        request = mock(Request.class);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallExecuteWithNull_Exception(){

        //addOrderStrategyUseCase.execute(null);
    }

    @Test
    public void WhenCallExecuteWithCorrectValue_CorrectResult(){
        when(request.getbody()).thenReturn("Test");
        Response<String> response = addOrderStrategyUseCase.execute(request);

        assertEquals("Test", response.getBody());
    }

}
