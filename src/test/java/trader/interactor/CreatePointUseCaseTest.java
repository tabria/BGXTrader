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

public class CreatePointUseCaseTest {

    private UseCase createPointUseCase;
    private Request request;

    @Before
    public void setUp() throws Exception {
        createPointUseCase = new CreatePointUseCase();
        request = mock(Request.class);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallExecuteWithNull_Exception(){
        createPointUseCase.execute(null);
    }

    @Test
    public void WhenCallExecuteWithCorrectValue_CorrectResult(){
        when(request.getRequestDataStructure()).thenReturn("Test");
        Response<String> response = createPointUseCase.execute(request);

        assertEquals("Test", response.getResponseDataStructure());
    }

}
