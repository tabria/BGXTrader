package trader.controller;

import org.junit.Before;
import org.junit.Test;
import trader.broker.connector.BrokerConnector;
import trader.requestor.*;
import trader.responder.Response;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CreateBrokerConnectorControllerTest extends BaseControllerTest{

    private static final String BROKER_CONFIGURATION_CONTROLLER = "CreateBrokerConnectorController";

    private BrokerConnector brokerConnectorMock;
    private CreateBrokerConnectorController createBrokerConnectorController;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        brokerConnectorMock = mock(BrokerConnector.class);
        createBrokerConnectorController = new CreateBrokerConnectorController( useCaseFactoryMock, presenterMock);
    }

    @Test
    public void givenCorrectSettings_WhenCallGetRequest_ThenReturnCorrectRequest(){
        setFakeRequestFactoryCreator();
        setFakeRequestFactory();

        Request brokerConnectorRequest = createBrokerConnectorController.getRequest(BROKER_CONFIGURATION_CONTROLLER, settings);

        assertEquals(requestMock, brokerConnectorRequest);
    }

    @Test
    public void givenCorrectSettings_WhenCallMake_ThenReturnCorrectUseCase(){
        setFakeUseCaseFactory();
        UseCase useCase = createBrokerConnectorController.make(BROKER_CONFIGURATION_CONTROLLER, presenterMock);

        assertEquals(useCaseMock, useCase);
    }

    @Test
    public void givenCorrectSettings_WhenCallExecute_ReturnCorrectResponse(){
        setFakes();
        setFakeResponseBody();

        Response<BrokerConnector> brokerConnectorResponse = createBrokerConnectorController.execute(settings);

        assertEquals(brokerConnectorMock, brokerConnectorResponse.getBody());
    }

    private void setFakeResponseBody(){
        when(responseMock.getBody()).thenReturn(brokerConnectorMock);
    }
}