package trader.observer;

import org.junit.Before;
import trader.CommonTestClassMembers;
import trader.broker.BrokerGateway;
import static org.mockito.Mockito.mock;

public abstract class BaseObserverTest  {

    static final String INSTRUMENT_VALUE = "EUR_USD";
    static final long INITIAL_QUANTITY = 100L;
    static final long UPDATE_QUANTITY = 2L;

    BrokerGateway brokerGatewayMock;
    CommonTestClassMembers commonTestMembers;

    @Before
    public void before(){

        brokerGatewayMock = mock(BrokerGateway.class);
        commonTestMembers = new CommonTestClassMembers();


    }
}
