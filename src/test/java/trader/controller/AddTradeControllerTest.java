package trader.controller;

import org.junit.Before;
import trader.broker.BrokerGateway;

import static org.mockito.Mockito.mock;

public class AddTradeControllerTest extends BaseControllerTest<BrokerGateway> {

    private AddTradeController controller;
    private BrokerGateway brokerGateway;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        setConfigurationMock(mock(BrokerGateway.class));
        brokerGateway = mock(BrokerGateway.class);
        controller = new AddTradeController(brokerGateway);
    }
}
