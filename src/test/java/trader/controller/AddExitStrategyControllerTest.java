package trader.controller;

import org.junit.Before;
import org.junit.Test;
import trader.exception.NullArgumentException;
import trader.exit.ExitStrategy;
import trader.responder.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AddExitStrategyControllerTest extends BaseControllerTest {


    private TraderController<ExitStrategy> controller;
    private ExitStrategy exitStrategyStrategy;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        exitStrategyStrategy = mock(ExitStrategy.class);
        controller = new AddExitStrategyController<>(requestBuilderMock, useCaseFactoryMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreateControllerWithNullRequestBuilder_Exception(){
        new AddExitStrategyController<>(null, useCaseFactoryMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreateControllerWithNullUseCaseFactory_Exception(){
        new AddExitStrategyController<>(requestBuilderMock, null);
    }

    @Test
    public void WhenCallGetRequestWithCorrectSettings_ReturnCorrectResult(){
        setExecuteSettings("AddExitStrategyController");
        when(responseMock.getResponseDataStructure()).thenReturn(exitStrategyStrategy);
        Response<ExitStrategy> response = controller.execute(settings);

        assertEquals(exitStrategyStrategy, response.getResponseDataStructure());
    }

}
