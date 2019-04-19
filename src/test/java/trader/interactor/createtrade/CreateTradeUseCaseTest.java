package trader.interactor.createtrade;

import org.junit.Before;
import org.junit.Test;
import trader.entity.trade.Direction;
import trader.entity.trade.Trade;
import trader.exception.NullArgumentException;
import trader.requestor.Request;
import trader.requestor.UseCase;
import trader.responder.Response;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CreateTradeUseCaseTest {

    private Map<String, Object> settings;
    private UseCase createTradeUseCase;
    private Request request;

    @Before
    public void setUp() throws Exception {
        settings = new HashMap<>();
        createTradeUseCase = new CreateTradeUseCase();
        request = mock(Request.class);
    }

    @Test
    public void givenNullOrEmptySettings_WhenCallExecute_ThenReturnDefaultTrade(){
        when(request.getBody()).thenReturn(settings);
        Response<Trade> responseForNull = createTradeUseCase.execute(request);

        Map<String, String> emptySettings = new HashMap<>();
        settings.put("settings", emptySettings);
        Response<Trade> responseForEmpty = createTradeUseCase.execute(request);

        assertIfTradeIsDefault(responseForNull);
        assertIfTradeIsDefault(responseForEmpty);
    }

    @Test
    public void givenCorrectSettngs_WhenCallExecute_ThenReturnNewTrade(){
        when(request.getBody()).thenReturn(settings);
        Map<String, String> realSettings = new HashMap<>();
        realSettings.put("direction", "down");
        realSettings.put("tradable", "true");
        realSettings.put("entryPrice", "1.12345");
        realSettings.put("stopLossPrice", "5.1234");
        settings.put("settings", realSettings);

        Response<Trade> responseNewTrade = createTradeUseCase.execute(request);

        Trade trade = responseNewTrade.getBody();

        assertTrue(trade.getTradable());
        assertEquals(Direction.DOWN, trade.getDirection());
        assertEquals(BigDecimal.valueOf(1.12345) ,trade.getEntryPrice());
        assertEquals(BigDecimal.valueOf(5.1234) ,trade.getStopLossPrice());
    }

    private void assertIfTradeIsDefault(Response<Trade> response) {
        Trade trade = response.getBody();
        assertFalse(trade.getTradable());
        assertEquals("FLAT", trade.getDirection().toString());
        assertEquals(BigDecimal.valueOf(0.00010), trade.getEntryPrice());
        assertEquals(BigDecimal.valueOf(0.00010), trade.getStopLossPrice());
    }
//
//    @Test
//    public void WhenCallBuildWithCreateTradeControllerWithCorrectCustomSettings_CorrectTrade(){
//        settings.put("direction", "down");
//        settings.put("tradable", "true");
//        settings.put("entryPrice", "1.12345");
//        settings.put("stopLossPrice", "5.1234");
//        Request<?> createTradeRequest = requestOLDBuilder.build("CreateTradeController", settings);
//        Trade trade = (Trade) createTradeRequest.getBody();
//
//        assertTrue(trade.getTradable());
//        assertEquals(Direction.DOWN, trade.getDirection());
//        assertEquals(BigDecimal.valueOf(1.12345) ,trade.getEntryPrice());
//        assertEquals(BigDecimal.valueOf(5.1234) ,trade.getStopLossPrice());
//    }

}
