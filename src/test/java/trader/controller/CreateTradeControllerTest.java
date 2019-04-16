package trader.controller;

import org.junit.Before;
import org.junit.Test;
import trader.CommonTestClassMembers;
import trader.configuration.TradingStrategyConfiguration;
import trader.entity.trade.Trade;
import trader.exception.NullArgumentException;
import trader.responder.Response;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CreateTradeControllerTest extends BaseControllerTest<TradingStrategyConfiguration> {

    private static final String CREATE_TRADE_CONTROLLER = "CreateTradeController";

    private CommonTestClassMembers commonMembers;
    private CreateTradeController<Trade> controller;
    private Trade tradeMock;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        commonMembers = new CommonTestClassMembers();
        tradeMock = mock(Trade.class);
        controller = new CreateTradeController<Trade>(requestOLDBuilderMock, useCaseFactoryMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreatedWithNullUseCaseFactory_Exception(){
        new CreateTradeController<Trade>(requestOLDBuilderMock, null);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreatedWithNullRequestBuilder_Exception(){
        new CreateTradeController<Trade>(null, useCaseFactoryMock);
    }

    @Test
    public void WhenCreatedWithCorrectSettings_CorrectResits(){
        assertEquals(requestOLDBuilderMock, commonMembers.extractFieldObject(controller, "requestBuilder"));
        assertEquals(useCaseFactoryMock, commonMembers.extractFieldObject(controller, "useCaseFactory"));
    }

    @Test
    public void WhenCallExecuteWithCorrectSetting_CorrectResult(){
        setExecuteSettings(CREATE_TRADE_CONTROLLER);
        when(responseMock.getBody()).thenReturn(tradeMock);
        Response<Trade> createTradeResponse = controller.execute(settings);

        assertEquals(tradeMock, createTradeResponse.getBody());
    }

    @Test
    public void WhenCallExecuteWithEmptySetting_DefaultTrade(){

        setExecuteSettings(CREATE_TRADE_CONTROLLER);
        when(responseMock.getBody()).thenReturn(tradeMock);
        Response<Trade> createTradeResponse = controller.execute(settings);

        assertEquals(tradeMock, createTradeResponse.getBody());
    }

}
