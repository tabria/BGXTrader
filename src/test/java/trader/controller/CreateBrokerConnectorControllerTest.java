package trader.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import trader.broker.connector.BrokerConnector;
import trader.requestor.*;
import trader.responder.Response;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RequestBuilderCreator.class)
public class CreateBrokerConnectorControllerTest{

    private static final String BROKER_CONFIGURATION_CONTROLLER = "CreateBrokerConnectorController";

    private Map<String, Object> settings;
    private UseCaseFactory useCaseFactoryMock;
    private UseCase useCaseMock;
    private RequestBuilder requestBuilderMock;
    private Request requestMock;
    private Response responseMock;
    private BrokerConnector brokerConnectorMock;
    private CreateBrokerConnectorController createBrokerConnectorController;

    @Before
    public void setUp() throws Exception {
        settings = new HashMap<>();
        useCaseFactoryMock = mock(UseCaseFactory.class);
        useCaseMock = mock(UseCase.class);
        requestMock = mock(Request.class);
        requestBuilderMock = mock(RequestBuilder.class);
        responseMock = mock(Response.class);
        brokerConnectorMock = mock(BrokerConnector.class);
        createBrokerConnectorController = new CreateBrokerConnectorController( useCaseFactoryMock);
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
        UseCase useCase = createBrokerConnectorController.make(BROKER_CONFIGURATION_CONTROLLER);

        assertEquals(useCaseMock, useCase);
    }

    @Test
    public void givenCorrectSettings_WhenCallExecute_ReturnCorrectResponse(){
        setFakeRequestFactoryCreator();
        setFakeRequestFactory();
        setFakeRequestFactory();
        setFakeUseCaseFactory();
        setFakeUseCase();
        setFakeResponseBody();

        Response<BrokerConnector> brokerConnectorResponse = createBrokerConnectorController.execute(settings);

        assertEquals(brokerConnectorMock, brokerConnectorResponse.getBody());
    }

    private void setFakeRequestFactoryCreator(){
        PowerMockito.mockStatic(RequestBuilderCreator.class);
        PowerMockito.when(RequestBuilderCreator.create(any())).thenReturn(requestBuilderMock);
    }

    private void setFakeRequestFactory(){
        when(requestBuilderMock.build(settings)).thenReturn(requestMock);
    }

    private void setFakeUseCaseFactory(){
        when(useCaseFactoryMock.make(anyString())).thenReturn(useCaseMock);
    }

    private void setFakeUseCase(){
        when(useCaseMock.execute(requestMock)).thenReturn(responseMock);
    }

    private void setFakeResponseBody(){
        when(responseMock.getBody()).thenReturn(brokerConnectorMock);
    }
}