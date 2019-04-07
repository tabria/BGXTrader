package trader.observer;

import org.junit.Before;
import trader.CommonTestClassMembers;
import trader.broker.BrokerGateway;
import trader.configuration.TradingStrategyConfiguration;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class BaseObserverTest  {

    static final String INSTRUMENT_VALUE = "EUR_USD";
    static final long INITIAL_QUANTITY = 100L;
    static final long UPDATE_QUANTITY = 2L;

    TradingStrategyConfiguration mockConfiguration;
    BrokerGateway brokerGatewayMock;
    CommonTestClassMembers commonTestMembers;

    @Before
    public void before(){

        this.mockConfiguration = mock(TradingStrategyConfiguration.class);
        setConfiguration();
        brokerGatewayMock = mock(BrokerGateway.class);
        commonTestMembers = new CommonTestClassMembers();


    }

    private void setConfiguration() {
        when(mockConfiguration.getInstrument()).thenReturn(INSTRUMENT_VALUE);
        when(mockConfiguration.getInitialCandlesQuantity()).thenReturn(INITIAL_QUANTITY);
        when(mockConfiguration.getUpdateCandlesQuantity()).thenReturn(UPDATE_QUANTITY);
    }
}
