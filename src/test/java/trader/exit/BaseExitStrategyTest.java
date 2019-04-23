package trader.exit;

import org.junit.Before;
import trader.CommonTestClassMembers;
import trader.broker.BrokerGateway;
import trader.strategy.TradingStrategyConfiguration;
import trader.entity.candlestick.Candlestick;
import trader.entity.candlestick.candle.CandleGranularity;
import trader.entity.price.Price;
import trader.entity.trade.BrokerTradeDetails;
import trader.exit.service.UpdateCandlesService;
import trader.presenter.Presenter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class BaseExitStrategyTest {

    protected UpdateCandlesService updateCandlesServiceMock;
    protected BrokerGateway brokerGatewayMock;
    protected TradingStrategyConfiguration configurationMock;
    protected CommonTestClassMembers commonMembers;
    protected BrokerTradeDetails tradeDetailsMock;
    protected Presenter presenterMock;
    protected Price priceMock;

    @Before
    public void setUp() throws Exception {
        updateCandlesServiceMock = mock(UpdateCandlesService.class);
        brokerGatewayMock = mock(BrokerGateway.class);
        configurationMock = mock(TradingStrategyConfiguration.class);
        tradeDetailsMock = mock(BrokerTradeDetails.class);
        presenterMock = mock(Presenter.class);
        priceMock = mock(Price.class);
        commonMembers = new CommonTestClassMembers();
    }

    protected void setFakeBrokerGateway(String tradeStopLossPrice) {
        when(brokerGatewayMock.getTradeDetails(anyInt())).thenReturn(tradeDetailsMock);
        when(brokerGatewayMock.getTradeStopLossPrice(anyString())).thenReturn(new BigDecimal(tradeStopLossPrice));
    }

    protected List<Candlestick> setFakeCandlesticksList(String candleHighPrice, String candleLowPrice, String candleClosePrice) {
        Candlestick candlestickMock = mock(Candlestick.class);
        when(candlestickMock.getHighPrice()).thenReturn(new BigDecimal(candleHighPrice));
        when(candlestickMock.getLowPrice()).thenReturn(new BigDecimal(candleLowPrice));
        when(candlestickMock.getClosePrice()).thenReturn(new BigDecimal(candleClosePrice));
        List<Candlestick> candlesticks = new ArrayList<>();
        candlesticks.add(candlestickMock);
        candlesticks.add(candlestickMock);
        return candlesticks;
    }

    protected void setFakeTradeDetails(String tradeID, String openPrice, String stopLossPrice, String initialUnits, String currentUnits) {
        when(tradeDetailsMock.getTradeID()).thenReturn(tradeID);
        when(tradeDetailsMock.getOpenPrice()).thenReturn(new BigDecimal(openPrice));
        when(tradeDetailsMock.getStopLossPrice()).thenReturn(new BigDecimal(stopLossPrice));
        when(tradeDetailsMock.getInitialUnits()).thenReturn(new BigDecimal(initialUnits));
        when(tradeDetailsMock.getCurrentUnits()).thenReturn(new BigDecimal(currentUnits));
    }

    protected void setFakePrice(String bid, String ask) {
        when(priceMock.getBid()).thenReturn(new BigDecimal(bid));
        when(priceMock.getAsk()).thenReturn(new BigDecimal(ask));
    }

    private void setFakeConfigurations(String instrument, long initialCandleQuantity, CandleGranularity granularity, long updateQuantity) {
        when(configurationMock.getInstrument()).thenReturn(instrument);
        when(configurationMock.getInitialCandlesQuantity()).thenReturn(initialCandleQuantity);
        when(configurationMock.getExitGranularity()).thenReturn(granularity);
        when(configurationMock.getUpdateCandlesQuantity()).thenReturn(updateQuantity);
    }
}
