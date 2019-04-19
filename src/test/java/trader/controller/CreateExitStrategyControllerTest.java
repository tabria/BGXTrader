package trader.controller;

import org.junit.Before;
import org.junit.Test;
import trader.exit.ExitStrategy;
import trader.presenter.Presenter;
import trader.requestor.*;
import trader.responder.Response;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CreateExitStrategyControllerTest extends BaseControllerTest{

    private static final String CREATE_EXIT_STRATEGY_CONTROLLER = "CreateExitStrategyController";

    private CreateExitStrategyController<ExitStrategy> controller;
    private ExitStrategy exitStrategyMock;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        exitStrategyMock = mock(ExitStrategy.class);
        controller = new CreateExitStrategyController<>(useCaseFactoryMock, presenterMock);
    }

    @Test
    public void givenCorrectSettings_WhenCallGetRequest_ThenReturnCorrectRequest() {
        setFakeRequestFactoryCreator();
        setFakeRequestFactory();

        Request exitRequest = controller.getRequest(CREATE_EXIT_STRATEGY_CONTROLLER, settings);

        assertEquals(requestMock, exitRequest);
    }

    @Test
    public void givenCorrectSettings_WhenCallMake_ThenReturnCorrectUseCase(){
        setFakes();
        setFakeResponseBody();

        UseCase useCase = controller.make(CREATE_EXIT_STRATEGY_CONTROLLER, presenterMock);

        assertEquals(useCaseMock, useCase);
    }

    @Test
    public void givenSettings_WhenCallExecute_ThenReturnTrade(){
        when(useCaseFactoryMock.make(anyString(), any(Presenter.class))).thenReturn(useCaseMock);
        when(useCaseMock.execute(any(Request.class))).thenReturn(responseMock);
        when(responseMock.getBody()).thenReturn(exitStrategyMock);
        Response<ExitStrategy> createExitStrategyResponse = controller.execute(settings);

        assertEquals(exitStrategyMock, createExitStrategyResponse.getBody());
    }

    private void setFakeResponseBody(){
        when(responseMock.getBody()).thenReturn(exitStrategyMock);
    }

}
