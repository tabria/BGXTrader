package trader.strategy.BGXStrategy.configuration;

import trader.candlestick.candle.CandlePriceType;
import trader.connector.CandlesUpdaterConnector;
import trader.indicator.Indicator;
import trader.indicator.rsi.RSIBuilder;

public enum RSISettings {

    RSI_SETTINGS (14, CandlePriceType.CLOSE);

    private long period;
    private CandlePriceType priceType;

    RSISettings(long period, CandlePriceType priceType) {
        this.period = period;
        this.priceType = priceType;
    }

    public Indicator build(CandlesUpdaterConnector connector){
        return new RSIBuilder(connector).setPeriod(period).setCandlePriceType(priceType).build();
    }

}
