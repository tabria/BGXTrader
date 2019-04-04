package trader.controller;

import org.junit.Before;
import org.junit.Test;
import trader.broker.BrokerConnector;
import trader.exception.NullArgumentException;
import trader.requestor.RequestBuilder;
import trader.requestor.UseCaseFactory;
import trader.strategy.bgxstrategy.configuration.TradingStrategyConfiguration;

import static org.mockito.Mockito.mock;

public class UpdateIndicatorControllerTest {

    private UseCaseFactory useCaseFactoryMock;
    private RequestBuilder requestBuilderMock;
    private TradingStrategyConfiguration tradingStrategyConfigurationMock;
    private BrokerConnector brokerConnectorMock;

    @Before
    public void setUp() throws Exception {
        useCaseFactoryMock = mock(UseCaseFactory.class);
        requestBuilderMock = mock(RequestBuilder.class);
        tradingStrategyConfigurationMock = mock(TradingStrategyConfiguration.class);
        brokerConnectorMock = mock(BrokerConnector.class);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreateControllerWithNullRequestBuilder_Exception(){
        new UpdateIndicatorController(null, useCaseFactoryMock, tradingStrategyConfigurationMock, brokerConnectorMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreateControllerWithNullUserCaseFactory_Exception(){
        new UpdateIndicatorController(requestBuilderMock, null, tradingStrategyConfigurationMock, brokerConnectorMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreateControllerWithNullTradingStrategyConfiguration_Exception(){
        new UpdateIndicatorController(requestBuilderMock, useCaseFactoryMock, null, brokerConnectorMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreateControllerWithNullBrokerConnector_Exception(){
        new UpdateIndicatorController(requestBuilderMock, useCaseFactoryMock, tradingStrategyConfigurationMock, null);
    }


}
