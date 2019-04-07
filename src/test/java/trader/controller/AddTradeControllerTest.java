package trader.controller;

import org.junit.Before;
import org.junit.Test;
import trader.CommonTestClassMembers;
import trader.broker.BrokerGateway;
import trader.configuration.TradingStrategyConfiguration;
import trader.exception.NullArgumentException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class AddTradeControllerTest extends BaseControllerTest<TradingStrategyConfiguration> {

    private AddTradeController controller;
    private BrokerGateway brokerGateway;
    private CommonTestClassMembers commonMembers;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        setConfigurationMock(mock(TradingStrategyConfiguration.class));
        brokerGateway = mock(BrokerGateway.class);
        commonMembers = new CommonTestClassMembers();
        controller = new AddTradeController(brokerGateway, configurationMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreatedWithNullBrokerGateway_Exception(){
        new AddTradeController<>(null, configurationMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreatedWithNullConfiguration_Exception(){
        new AddTradeController<>(brokerGateway, null);
    }

    @Test
    public void WhenCreatedWithCorrectSettings_CorrectResits(){
        assertEquals(brokerGateway, commonMembers.extractFieldObject(controller, "brokerGateway"));
        assertEquals(configurationMock, commonMembers.extractFieldObject(controller, "configuration"));
    }
}
