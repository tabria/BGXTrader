package trader.strategy.BGXStrategy.configuration;

import trader.candlestick.candle.CandlePriceType;
import trader.connector.CandlesUpdaterConnector;
import trader.indicator.Indicator;
import trader.indicator.ma.MovingAverageBuilder;
import trader.indicator.ma.enums.MAType;

public enum MovingAveragesSettings {

    PRICE_SMA_SETTINGS (1, CandlePriceType.CLOSE, MAType.SIMPLE),
    DAILY_SMA_SETTINGS (1,  CandlePriceType.OPEN, MAType.SIMPLE),
    FAST_WMA_SETTINGS (5, CandlePriceType.CLOSE, MAType.WEIGHTED),
    MIDDLE_WMA_SETTINGS (20, CandlePriceType.CLOSE, MAType.WEIGHTED),
    SLOW_WMA_SETTINGS (100, CandlePriceType.CLOSE, MAType.WEIGHTED);

    private long period;
    private CandlePriceType priceType;
    private MAType maType;

    MovingAveragesSettings(long period, CandlePriceType priceType, MAType maType) {
        this.period = period;
        this.priceType = priceType;
        this.maType = maType;
    }

    public Indicator build(CandlesUpdaterConnector connector){
       return new MovingAverageBuilder(connector).setPeriod(period).setCandlePriceType(priceType).setMAType(maType).build();
    }
}
