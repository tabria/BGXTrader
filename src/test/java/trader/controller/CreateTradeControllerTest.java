package trader.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.matchers.Null;
import trader.CommonTestClassMembers;
import trader.broker.BrokerGateway;
import trader.configuration.TradingStrategyConfiguration;
import trader.entity.trade.Trade;
import trader.exception.NullArgumentException;
import trader.requestor.*;
import trader.responder.Response;

import java.util.HashMap;

import static org.hamcrest.CoreMatchers.any;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
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
        setConfigurationMock(mock(TradingStrategyConfiguration.class));
        commonMembers = new CommonTestClassMembers();
        tradeMock = mock(Trade.class);
        controller = new CreateTradeController<Trade>(requestBuilderMock, useCaseFactoryMock ,configurationMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreatedWithNullUseCaseFactory_Exception(){
        new CreateTradeController<Trade>(requestBuilderMock, null, configurationMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreatedWithNullRequestBuilder_Exception(){
        new CreateTradeController<Trade>(null, useCaseFactoryMock , configurationMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreatedWithNullConfiguration_Exception(){
        new CreateTradeController<Trade>(requestBuilderMock, useCaseFactoryMock ,null);
    }

    @Test
    public void WhenCreatedWithCorrectSettings_CorrectResits(){
        assertEquals(requestBuilderMock, commonMembers.extractFieldObject(controller, "requestBuilder"));
        assertEquals(configurationMock, commonMembers.extractFieldObject(controller, "configuration"));
        assertEquals(useCaseFactoryMock, commonMembers.extractFieldObject(controller, "useCaseFactory"));
    }

    @Test
    public void WhenCallExecuteWithCorrectSetting_CorrectResult(){
        setExecuteSettings(CREATE_TRADE_CONTROLLER);
        when(responseMock.getResponseDataStructure()).thenReturn(tradeMock);

//        when(responseMock.getResponseDataStructure()).thenReturn(configurationMock);
//        when(useCaseFactoryMock.make(anyString())).thenReturn(useCaseMock);
//        when(useCaseMock.execute(requestMock)).thenReturn(responseMock);
//        when(requestBuilderMock.build(controllerName, settings)).thenReturn(requestMock);
//
//        when(useCaseMock.execute(eq(requestMock), any(HashMap.class))).thenReturn(responseMock);
        Response<Trade> createTradeResponse = controller.execute(settings);

        assertEquals(tradeMock, createTradeResponse.getResponseDataStructure());
    }

    @Test
    public void WhenCallExecuteWithEmptySetting_DefaultTrade(){

        setExecuteSettings(CREATE_TRADE_CONTROLLER);
        when(responseMock.getResponseDataStructure()).thenReturn(tradeMock);
        Response<Trade> createTradeResponse = controller.execute(settings);

        assertEquals(tradeMock, createTradeResponse.getResponseDataStructure());
    }

//    @Test
//    public void test(){
//        UseCaseFactory ucf = new UseCaseFactoryImpl();
//        RequestBuilder rqb = new RequestBuilderImpl();
//
//        TraderController<Trade> tc = new CreateTradeController<>(rqb, ucf, configurationMock);
//
//        tc.execute(new HashMap<>());
//    }

}
