package trader.controller;

import org.junit.Before;
import org.junit.Test;
import trader.entity.indicator.Indicator;
import trader.requestor.*;
import trader.responder.Response;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CreateIndicatorControllerTest extends BaseControllerTest {

    private static final String CREATE_INDICATOR_CONTROLLER_NAME = "CreateIndicatorController";

    private Indicator indicatorMock;
    private CreateIndicatorController createIndicatorController;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        indicatorMock = mock(Indicator.class);
        createIndicatorController = new CreateIndicatorController(useCaseFactoryMock, presenterMock);
    }

    @Test
    public void givenCorrectSettings_WhenCallGetRequestThenReturnCorrectRequest() {
        setFakeRequestFactoryCreator();
        setFakeRequestFactory();

        Request brokerConnectorRequest = createIndicatorController.getRequest(CREATE_INDICATOR_CONTROLLER_NAME, settings);

        assertEquals(requestMock, brokerConnectorRequest);
    }

    @Test
    public void givenCorrectSettings_WhenCallMake_ThenReturnCorrectUseCase(){
        setFakeUseCaseFactory();
        UseCase useCase = createIndicatorController.make(CREATE_INDICATOR_CONTROLLER_NAME, presenterMock);

        assertEquals(useCaseMock, useCase);
    }

    @Test
    public void givenCorrectSettings_WhenCallExecute_ThenReturnCorrectResponse(){
        setFakes();
        setFakeResponseBody();

        Response<Indicator> response = createIndicatorController.execute(settings);
        assertEquals(indicatorMock, response.getBody());
    }

    private void setFakeResponseBody(){
        when(responseMock.getBody()).thenReturn(indicatorMock);
    }

}
