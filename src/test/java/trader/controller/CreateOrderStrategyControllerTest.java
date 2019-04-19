package trader.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import trader.exception.NullArgumentException;
import trader.order.OrderStrategy;
import trader.requestor.*;
import trader.responder.Response;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(PowerMockRunner.class)
@PrepareForTest(RequestBuilderCreator.class)
public class CreateOrderStrategyControllerTest {

    private static final String CREATE_ORDER_STRATEGY_CONTROLLER = "CreateOrderStrategyController";

    private Map<String, Object> settings;
    private Request requestMock;
    private Response responseMock;
    private RequestBuilder requestBuilderMock;
    private UseCaseFactory useCaseFactoryMock;
    private UseCase useCaseMock;
    private CreateOrderStrategyController<OrderStrategy> controller;
    private OrderStrategy orderStrategyMock;

    @Before
    public void setUp() throws Exception {

        settings = new HashMap<>();
        requestMock = mock(Request.class);
        responseMock = mock(Response.class);
        useCaseMock = mock(UseCase.class);
        requestBuilderMock = mock(RequestBuilder.class);
        useCaseFactoryMock = mock(UseCaseFactory.class);
        orderStrategyMock = mock(OrderStrategy.class);
        controller = new CreateOrderStrategyController<>(useCaseFactoryMock);
    }

    @Test
    public void givenCorrectSettings_WhenCallGetRequestThenReturnCorrectRequest() {
        setFakeRequestFactoryCreator();
        setFakeRequestFactory();

        Request request = controller.getRequest(CREATE_ORDER_STRATEGY_CONTROLLER, settings);

        assertEquals(requestMock, request);
    }

    @Test
    public void givenCorrectSettings_WhenCallMake_ThenReturnCorrectUseCase(){
        setFakeUseCaseFactory();
        UseCase useCase = controller.make(CREATE_ORDER_STRATEGY_CONTROLLER);

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

        Response<OrderStrategy> response = controller.execute(settings);
        assertEquals(orderStrategyMock, response.getBody());
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
        when(responseMock.getBody()).thenReturn(orderStrategyMock);
    }

}
