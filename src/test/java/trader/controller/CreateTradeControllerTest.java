package trader.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import trader.entity.trade.Trade;
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
public class CreateTradeControllerTest {

    private static final String CREATE_TRADE_CONTROLLER = "CreateTradeController";

    private Map<String, Object> settings;
    private RequestBuilder requestBuilderMock;
    private Request requestMock;
    private UseCaseFactory useCaseFactoryMock;
    private UseCase useCaseMock;
    private Response responseMock;
    private CreateTradeController<Trade> controller;
    private Trade tradeMock;

    @Before
    public void setUp() throws Exception {

        settings = new HashMap<>();
        requestBuilderMock = mock(RequestBuilder.class);
        requestMock = mock(Request.class);
        useCaseMock = mock(UseCase.class);
        useCaseFactoryMock = mock(UseCaseFactory.class);
        responseMock = mock(Response.class);
        tradeMock = mock(Trade.class);
        controller = new CreateTradeController<>(useCaseFactoryMock);
    }

    @Test
    public void givenCorrectSettings_WhenCallGetRequestThenReturnCorrectRequest() {
        setFakeRequestFactoryCreator();
        setFakeRequestFactory();

        Request brokerConnectorRequest = controller.getRequest(CREATE_TRADE_CONTROLLER, settings);

        assertEquals(requestMock, brokerConnectorRequest);
    }


    @Test
    public void givenCorrectSettings_WhenCallMake_ThenReturnCorrectUseCase(){
        setFakeRequestFactoryCreator();
        setFakeRequestFactory();
        setFakeUseCaseFactory();
        setFakeUseCase();
        setFakeResponseBody();

        UseCase useCase = controller.make(CREATE_TRADE_CONTROLLER);

        assertEquals(useCaseMock, useCase);
    }


    @Test
    public void givenSettings_WhenCallExecute_ThenReturnTrade(){
        when(useCaseFactoryMock.make(anyString())).thenReturn(useCaseMock);
        when(useCaseMock.execute(any(Request.class))).thenReturn(responseMock);
        when(responseMock.getBody()).thenReturn(tradeMock);
        Response<Trade> createTradeResponse = controller.execute(settings);

        assertEquals(tradeMock, createTradeResponse.getBody());
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
        when(responseMock.getBody()).thenReturn(tradeMock);
    }

}
