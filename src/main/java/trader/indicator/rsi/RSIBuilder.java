package trader.indicator.rsi;

import trader.candle.CandlesUpdater;
import trader.connector.ApiConnector;
import trader.exception.*;
import trader.indicator.Indicator;
import trader.candle.CandlestickPriceType;

public final class RSIBuilder {

    private static final long DEFAULT_INDICATOR_PERIOD = 14L;
    private static final long MIN_INDICATOR_PERIOD = 1L;
    private static final long MAX_INDICATOR_PERIOD = 1000L;
    private static final CandlestickPriceType DEFAULT_CANDLESTICK_PRICE_TYPE = CandlestickPriceType.CLOSE;
    public static final int SETTABLE_FIELDS_COUNT = 2;

    private ApiConnector apiConnector;
    private long indicatorPeriod;
    private CandlestickPriceType candlestickPriceType;

    public RSIBuilder(ApiConnector apiConnector){
            setApiConnector(apiConnector);
            setPeriod(DEFAULT_INDICATOR_PERIOD);
            setCandlestickPriceType(DEFAULT_CANDLESTICK_PRICE_TYPE);
    }

    public RSIBuilder setPeriod(long indicatorPeriod) {
        if (notInBoundary(indicatorPeriod))
            throw  new OutOfBoundaryException();
        this.indicatorPeriod = indicatorPeriod;
        return this;
    }

    public RSIBuilder setCandlestickPriceType(CandlestickPriceType candlestickPriceType) {
        if (isNull(candlestickPriceType))
            throw  new NullArgumentException();
        this.candlestickPriceType = candlestickPriceType;
        return this;
    }
    
    public Indicator build(){
        return new RelativeStrengthIndex(indicatorPeriod, candlestickPriceType, createCandlesUpdater());
    }

    public Indicator build(String[] settings){
        if (settings == null || settings.length != SETTABLE_FIELDS_COUNT)
            throw new WrongIndicatorSettingsException();
        setPeriod(Long.parseLong(settings[0]));
        setCandlestickPriceType(CandlestickPriceType.valueOf(settings[1]));
        return new RelativeStrengthIndex(indicatorPeriod, candlestickPriceType, createCandlesUpdater());
    }


    private void setApiConnector(ApiConnector connector){
        if (connector == null)
            throw new NoSuchConnectorException();
        apiConnector = connector;
    }

    private CandlesUpdater createCandlesUpdater() {
        return new CandlesUpdater(apiConnector);
    }
    
    private boolean notInBoundary(long candlesticksQuantity) {
        return candlesticksQuantity < MIN_INDICATOR_PERIOD || candlesticksQuantity > MAX_INDICATOR_PERIOD;
    }
    
    private boolean isNull(Object object) {
        return object == null;
    }

}
