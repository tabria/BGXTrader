package trader.controller;

import org.junit.Before;
import org.junit.Test;
import trader.entry.EntryStrategy;
import trader.requestor.*;
import trader.responder.Response;;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CreateEntryStrategyControllerTest extends BaseControllerTest {

    private static final String CREATE_ENTRY_STRATEGY_CONTROLLER = "CreateEntryStrategyController";

    private CreateEntryStrategyController controller;
    private EntryStrategy entryStrategyMock;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        entryStrategyMock = mock(EntryStrategy.class);
        controller = new CreateEntryStrategyController(useCaseFactoryMock, presenterMock);
    }

    @Test
    public void givenCorrectSettings_WhenCallGetRequest_ThenReturnCorrectRequest(){
        setFakeRequestFactoryCreator();
        setFakeRequestFactory();

        Request request = controller.getRequest(CREATE_ENTRY_STRATEGY_CONTROLLER, settings);

        assertEquals(requestMock, request);
    }

    @Test
    public void givenCorrectSettings_WhenCallMake_ThenReturnCorrectUseCase(){
        setFakeUseCaseFactory();
        UseCase useCase = controller.make(CREATE_ENTRY_STRATEGY_CONTROLLER, presenterMock);

        assertEquals(useCaseMock, useCase);
    }

    @Test
    public void givenCorrectSettings_WhenCallExecute_ThenReturnCorrectResponse(){
        setFakes();
        setFakeResponseBody();

        Response<EntryStrategy> controllerResponse = controller.execute(settings);

        assertEquals(entryStrategyMock, controllerResponse.getBody());
    }

    private void setFakeResponseBody(){
        when(responseMock.getBody()).thenReturn(entryStrategyMock);
    }

}
