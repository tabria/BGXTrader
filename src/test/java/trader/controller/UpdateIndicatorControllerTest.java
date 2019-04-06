package trader.controller;

import org.junit.Before;
import org.junit.Test;
import trader.broker.BrokerGateway;
import trader.exception.NullArgumentException;
import trader.requestor.RequestBuilder;
import trader.requestor.UseCaseFactory;
import trader.configuration.TradingStrategyConfiguration;

import static org.mockito.Mockito.mock;

public class UpdateIndicatorControllerTest {

    private UseCaseFactory useCaseFactoryMock;
    private RequestBuilder requestBuilderMock;
    private TradingStrategyConfiguration tradingStrategyConfigurationMock;
    private BrokerGateway brokerGatewayMock;

    @Before
    public void setUp() throws Exception {
        useCaseFactoryMock = mock(UseCaseFactory.class);
        requestBuilderMock = mock(RequestBuilder.class);
        tradingStrategyConfigurationMock = mock(TradingStrategyConfiguration.class);
        brokerGatewayMock = mock(BrokerGateway.class);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreateControllerWithNullRequestBuilder_Exception(){
        new UpdateIndicatorController(null, useCaseFactoryMock, tradingStrategyConfigurationMock, brokerGatewayMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreateControllerWithNullUserCaseFactory_Exception(){
        new UpdateIndicatorController(requestBuilderMock, null, tradingStrategyConfigurationMock, brokerGatewayMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreateControllerWithNullTradingStrategyConfiguration_Exception(){
        new UpdateIndicatorController(requestBuilderMock, useCaseFactoryMock, null, brokerGatewayMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreateControllerWithNullBrokerConnector_Exception(){
        new UpdateIndicatorController(requestBuilderMock, useCaseFactoryMock, tradingStrategyConfigurationMock, null);
    }


}
