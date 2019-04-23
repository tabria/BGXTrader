package trader.exit.service;

import org.junit.Before;
import org.junit.Test;
import trader.broker.BrokerGateway;
import trader.strategy.TradingStrategyConfiguration;
import trader.entity.candlestick.Candlestick;
import trader.entity.candlestick.candle.CandleGranularity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UpdateCandlesServiceTest {

    private UpdateCandlesService service;
    private BrokerGateway brokerGatewayMock;
    private TradingStrategyConfiguration configurationMock;

    @Before
    public void setUp() throws Exception {
        brokerGatewayMock = mock(BrokerGateway.class);
        configurationMock = mock(TradingStrategyConfiguration.class);
        service = new UpdateCandlesService();
    }

    @Test
    public void givenEmptySettings_WhenCallUpdateCandles_ThenSetCandlesQuantityToInitial() {
        int candlesCount = 10;
        setFakeBrokerGatewayCandlesUpdate(candlesCount);
        setFakeConfigurations("EUR_USD", 200, CandleGranularity.M10, 2 );
        int initialSize = service.getSettings().size();
        service.updateCandles(brokerGatewayMock, configurationMock);
        int actualSize = service.getSettings().size();


        assertEquals(3, actualSize - initialSize);
        assertEquals("EUR_USD", service.getSettings().get("instrument"));
        assertEquals("200", service.getSettings().get("quantity"));
        assertEquals("M10", service.getSettings().get("granularity"));
    }

    @Test
    public void givenFilledSettings_WhenCallUpdateCandles_ThenSetCandlesQuantityToUpdate() {
        int candlesCount = 10;
        setFakeBrokerGatewayCandlesUpdate(candlesCount);
        setFakeConfigurations("EUR_USD", 200, CandleGranularity.M10, 2 );
        service.updateCandles(brokerGatewayMock, configurationMock);

        int initialSize = service.getSettings().size();
        service.updateCandles(brokerGatewayMock, configurationMock);
        int actualSize = service.getSettings().size();


        assertEquals(actualSize, initialSize);
        assertEquals("EUR_USD", service.getSettings().get("instrument"));
        assertEquals("2", service.getSettings().get("quantity"));
        assertEquals("M10", service.getSettings().get("granularity"));
    }

    @Test
    public void givenEmptySettings_WhenCallUpdateCandles_ThenCandlesticksListMustAddAllCandles() {
        int candlesCount = 10;
        setFakeBrokerGatewayCandlesUpdate(candlesCount);
        setFakeConfigurations("EUR_USD", 200, CandleGranularity.M10, 2 );
        int initialSize = service.getCandlesticks().size();
        service.updateCandles(brokerGatewayMock, configurationMock);
        int actualSize = service.getCandlesticks().size();

        assertEquals(candlesCount, actualSize-initialSize);
    }

    @Test
    public void givenFilledSettings_WhenCallUpdateCandles_ThenCandlesticksListMustAddOnlyLastCandle() {
        int candlesCount = 10;
        setFakeBrokerGatewayCandlesUpdate(candlesCount);
        setFakeConfigurations("EUR_USD", 200, CandleGranularity.M10, 2 );
        service.updateCandles(brokerGatewayMock, configurationMock);

        int initialSize = service.getCandlesticks().size();
        service.updateCandles(brokerGatewayMock, configurationMock);
        int actualSize = service.getCandlesticks().size();


        assertEquals( candlesCount + 1, actualSize);
        assertEquals(  1, actualSize - initialSize);
    }

    private void setFakeBrokerGatewayCandlesUpdate(int candlesCount) {
        List<Candlestick> targetList = setFakeCandlestickList(candlesCount);
        when(brokerGatewayMock.getCandles(any(HashMap.class))).thenReturn(targetList);
    }

    private List<Candlestick> setFakeCandlestickList(int candlesQuantity){
        List<Candlestick> candlesticks = new ArrayList<>();
        Candlestick candlestickMock = mock(Candlestick.class);
        for (int i = 0; i < candlesQuantity ; i++)
            candlesticks.add(candlestickMock);
        return candlesticks;
    }

    private void setFakeConfigurations(String instrument, long initialCandleQuantity, CandleGranularity granularity, long updateQuantity){
        when(configurationMock.getInstrument()).thenReturn(instrument);
        when(configurationMock.getInitialCandlesQuantity()).thenReturn(initialCandleQuantity);
        when(configurationMock.getExitGranularity()).thenReturn(granularity);
        when(configurationMock.getUpdateCandlesQuantity()).thenReturn(updateQuantity);
    }

}
