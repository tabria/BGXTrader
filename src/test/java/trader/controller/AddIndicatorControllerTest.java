package trader.controller;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trader.broker.BrokerGateway;
import trader.configuration.TradingStrategyConfiguration;
import trader.entity.candlestick.candle.CandleGranularity;
import trader.entity.indicator.Indicator;
import trader.exception.BadRequestException;
import trader.exception.NullArgumentException;
import trader.requestor.UseCase;
import trader.requestor.Request;
import trader.requestor.RequestBuilder;
import trader.requestor.UseCaseFactory;
import trader.responder.Response;
import trader.strategy.observable.PriceObservable;
import java.util.HashMap;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AddIndicatorControllerTest {

    private static final String ADD_INDICATOR_CONTROLLER_NAME = "AddIndicatorController";

    private PriceObservable priceObservableMock;
    private TradingStrategyConfiguration configurationMock;
    private Indicator indicatorMock;
    private UseCase useCaseMock;
    private Request requestMock;
    private RequestBuilder requestBuilderMock;
    private UseCaseFactory useCaseFactoryMock;
    private HashMap<String, String> settings;
    private AddIndicatorController addIndicatorController;
    private BrokerGateway gatewayMock;

    @Before
    public void setUp() {
        priceObservableMock = mock(PriceObservable.class);
        configurationMock = mock(TradingStrategyConfiguration.class);
        indicatorMock = mock(Indicator.class);
        useCaseMock = mock(UseCase.class);
        requestMock = mock(Request.class);
        requestBuilderMock = mock(RequestBuilder.class);
        useCaseFactoryMock = mock(UseCaseFactory.class);
        gatewayMock = mock(BrokerGateway.class);
        settings = new HashMap<>();
        addIndicatorController = new AddIndicatorController(requestBuilderMock, useCaseFactoryMock, priceObservableMock, configurationMock, gatewayMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreateControllerWithNullRequestBuilder_Exception(){
        new AddIndicatorController(null, useCaseFactoryMock, priceObservableMock, configurationMock, gatewayMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreateControllerWithNullUserCaseFactory_Exception(){
        new AddIndicatorController(requestBuilderMock, null, priceObservableMock, configurationMock, gatewayMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreateControllerWithNullObservable_Exception(){
        new AddIndicatorController(requestBuilderMock, useCaseFactoryMock, null, configurationMock, gatewayMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreateControllerWithNullConfiguration_Exception(){
        new AddIndicatorController(requestBuilderMock, useCaseFactoryMock, priceObservableMock, null, gatewayMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreateControllerWithNullBrokerGateway_Exception(){
        new AddIndicatorController(requestBuilderMock, useCaseFactoryMock, priceObservableMock, configurationMock, null);
    }

    @Test
    public void WhenCallGetRequestWithCorrectSettings_ReturnCorrectResult(){
        when(requestMock.getRequestDataStructure()).thenReturn(indicatorMock);
        when(requestBuilderMock.build(ADD_INDICATOR_CONTROLLER_NAME, settings)).thenReturn(requestMock);
        Request<?> rsiIndicatorRequest = addIndicatorController.getRequest(ADD_INDICATOR_CONTROLLER_NAME, settings);

        Assert.assertEquals(indicatorMock, rsiIndicatorRequest.getRequestDataStructure());
    }

    @Test
    public void WhenCallMakeWithCorrectSetting_CorrectResult(){
        String useCaseName = "AddIndicatorController";
        when(useCaseFactoryMock.make(useCaseName)).thenReturn(useCaseMock);
        UseCase useCase = addIndicatorController.make(useCaseName);

        Assert.assertEquals(useCaseMock, useCase);
    }

    @Test(expected = BadRequestException.class)
    public void WhenCallExecuteWithCorrectSettings_CorrectResponse(){
        setExecuteSettings();
        addIndicatorController.execute(settings);
        //addIndicatorController.execute(RSI_INDICATOR, settings, priceObservableMock);
    }

    private void setExecuteSettings() {
        Response responseMock = mock(Response.class);
        when(responseMock.getResponseDataStructure()).thenReturn(indicatorMock);
        when(useCaseFactoryMock.make(anyString())).thenReturn(useCaseMock);
        when(useCaseMock.execute(requestMock)).thenReturn(responseMock);
        when(requestBuilderMock.build(ADD_INDICATOR_CONTROLLER_NAME, settings)).thenReturn(requestMock);
        when(configurationMock.getInstrument()).thenReturn("EUR_USD");
        when(configurationMock.getInitialCandlesQuantity()).thenReturn(12L);
        when(indicatorMock.getGranularity()).thenReturn(CandleGranularity.H1);
        doThrow(BadRequestException.class).when(priceObservableMock).registerObserver(any(Observer.class));
    }

}
