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

public class AddExitStrategyUseCaseTest {

    private UseCase addExitStrategyUseCase;
    private Request request;

    @Before
    public void setUp() throws Exception {
        addExitStrategyUseCase = new AddExitStrategyUseCase();
        request = mock(Request.class);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallExecuteWithNull_Exception(){
        //addExitStrategyUseCase.execute(null);
    }

    @Test
    public void WhenCallExecuteWithCorrectValue_CorrectResult(){
        when(request.getBody()).thenReturn("Test");
        Response<String> response = addExitStrategyUseCase.execute(request);

        assertEquals("Test", response.getBody());
    }

}
