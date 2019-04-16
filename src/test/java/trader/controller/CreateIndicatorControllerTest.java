package trader.controller;

import org.junit.Before;
import org.junit.Test;
import trader.broker.BrokerGateway;
import trader.configuration.TradingStrategyConfiguration;
import trader.entity.indicator.Indicator;
import trader.exception.NullArgumentException;
import trader.requestor.RequestOLDBuilder;
import trader.requestor.UseCase;
import trader.requestor.Request;
import trader.requestor.UseCaseFactory;
import trader.responder.Response;
import trader.strategy.observable.PriceObservable;
import java.util.HashMap;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CreateIndicatorControllerTest {

    private static final String ADD_INDICATOR_CONTROLLER_NAME = "CreateIndicatorController";

    private PriceObservable priceObservableMock;
    private TradingStrategyConfiguration configurationMock;
    private Indicator indicatorMock;
    private UseCase useCaseMock;
    private Request requestMock;
    private RequestOLDBuilder requestOLDBuilderMock;
    private UseCaseFactory useCaseFactoryMock;
    private HashMap<String, String> settings;
    private CreateIndicatorController createIndicatorController;
    private BrokerGateway gatewayMock;

    @Before
    public void setUp() {
        priceObservableMock = mock(PriceObservable.class);
        configurationMock = mock(TradingStrategyConfiguration.class);
        indicatorMock = mock(Indicator.class);
        useCaseMock = mock(UseCase.class);
        requestMock = mock(Request.class);
        requestOLDBuilderMock = mock(RequestOLDBuilder.class);
        useCaseFactoryMock = mock(UseCaseFactory.class);
        gatewayMock = mock(BrokerGateway.class);
        settings = new HashMap<>();
        createIndicatorController = new CreateIndicatorController(requestOLDBuilderMock, useCaseFactoryMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreateControllerWithNullRequestBuilder_Exception(){
        new CreateIndicatorController(null, useCaseFactoryMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreateControllerWithNullUserCaseFactory_Exception(){
        new CreateIndicatorController(requestOLDBuilderMock, null);
    }

    @Test
    public void WhenCallGetRequestWithCorrectSettings_ReturnCorrectResult(){
        when(requestMock.getBody()).thenReturn(indicatorMock);
        when(requestOLDBuilderMock.build(ADD_INDICATOR_CONTROLLER_NAME, settings)).thenReturn(requestMock);
        Request<?> rsiIndicatorRequest = createIndicatorController.getRequest(ADD_INDICATOR_CONTROLLER_NAME, settings);

        assertEquals(indicatorMock, rsiIndicatorRequest.getBody());
    }

    @Test
    public void WhenCallMakeWithCorrectSetting_CorrectResult(){
        String useCaseName = "CreateIndicatorController";
        when(useCaseFactoryMock.make(useCaseName)).thenReturn(useCaseMock);
        UseCase useCase = createIndicatorController.make(useCaseName);

        assertEquals(useCaseMock, useCase);
    }

    @Test
    public void WhenCallExecuteWithCorrectSettings_CorrectResponse(){
        setExecuteSettings();
        Response<Indicator> response = createIndicatorController.execute(settings);

        assertEquals(indicatorMock, response.getBody());
    }

    private void setExecuteSettings() {
        Response responseMock = mock(Response.class);
        when(responseMock.getBody()).thenReturn(indicatorMock);
        when(useCaseFactoryMock.make(anyString())).thenReturn(useCaseMock);
        when(useCaseMock.execute(requestMock)).thenReturn(responseMock);
        when(requestOLDBuilderMock.build(ADD_INDICATOR_CONTROLLER_NAME, settings)).thenReturn(requestMock);
    }

}
