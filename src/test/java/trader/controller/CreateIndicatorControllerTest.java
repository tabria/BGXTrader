package trader.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import trader.broker.BrokerGateway;
import trader.broker.connector.BrokerConnector;
import trader.configuration.TradingStrategyConfiguration;
import trader.entity.indicator.Indicator;
import trader.exception.NullArgumentException;
import trader.requestor.*;
import trader.responder.Response;
import trader.strategy.observable.PriceObservable;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RequestBuilderCreator.class)
public class CreateIndicatorControllerTest {

    private static final String CREATE_INDICATOR_CONTROLLER_NAME = "CreateIndicatorController";

    private Map<String, Object> settings;
    private UseCaseFactory useCaseFactoryMock;
    private UseCase useCaseMock;
    private RequestBuilder requestBuilderMock;
    private Request requestMock;
    private Response responseMock;
    private Indicator indicatorMock;
    private CreateIndicatorController createIndicatorController;

    @Before
    public void setUp() {
        settings = new HashMap<>();
        useCaseFactoryMock = mock(UseCaseFactory.class);
        useCaseMock = mock(UseCase.class);
        requestMock = mock(Request.class);
        requestBuilderMock = mock(RequestBuilder.class);
        responseMock = mock(Response.class);
        indicatorMock = mock(Indicator.class);
        createIndicatorController = new CreateIndicatorController(useCaseFactoryMock);
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
        UseCase useCase = createIndicatorController.make(CREATE_INDICATOR_CONTROLLER_NAME);

        assertEquals(useCaseMock, useCase);
    }

    @Test
    public void givenCorrectSettings_WhenCallExecute_ThenReturnCorrectResponse(){
        setFakeRequestFactoryCreator();
        setFakeRequestFactory();
        setFakeRequestFactory();
        setFakeUseCaseFactory();
        setFakeUseCase();
        setFakeResponseBody();

        Response<Indicator> response = createIndicatorController.execute(settings);
        assertEquals(indicatorMock, response.getBody());
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
        when(responseMock.getBody()).thenReturn(indicatorMock);
    }

}
