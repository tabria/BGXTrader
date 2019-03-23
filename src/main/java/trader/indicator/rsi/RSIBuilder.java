package trader.indicator.rsi;

import trader.candlestick.CandlesUpdatable;
import trader.candlestick.updater.CandlesUpdater;
import trader.connector.CandlesUpdaterConnector;
import trader.exception.*;
import trader.indicator.Indicator;
import trader.candlestick.candle.CandlePriceType;

public final class RSIBuilder {

    private static final long DEFAULT_INDICATOR_PERIOD = 14L;
    private static final long MIN_INDICATOR_PERIOD = 1L;
    private static final long MAX_INDICATOR_PERIOD = 1000L;
    private static final CandlePriceType DEFAULT_CANDLESTICK_PRICE_TYPE = CandlePriceType.CLOSE;
    public static final int SETTABLE_FIELDS_COUNT = 2;

    private CandlesUpdaterConnector updaterConnector;
    private long indicatorPeriod;
    private CandlePriceType candlePriceType;

    public RSIBuilder(CandlesUpdaterConnector updaterConnector){
            setCandlesUpdaterConnector(updaterConnector);
            setPeriod(DEFAULT_INDICATOR_PERIOD);
            setCandlePriceType(DEFAULT_CANDLESTICK_PRICE_TYPE);
    }

    public RSIBuilder setPeriod(long indicatorPeriod) {
        if (notInBoundary(indicatorPeriod))
            throw  new OutOfBoundaryException();
        this.indicatorPeriod = indicatorPeriod;
        return this;
    }

    public RSIBuilder setCandlePriceType(CandlePriceType candlePriceType) {
        if (isNull(candlePriceType))
            throw  new NullArgumentException();
        this.candlePriceType = candlePriceType;
        return this;
    }
    
    public Indicator build(){
        return new RelativeStrengthIndex(indicatorPeriod, candlePriceType, createCandlesUpdater());
    }

    public Indicator build(String[] settings){
        if (settings == null || settings.length != SETTABLE_FIELDS_COUNT)
            throw new WrongIndicatorSettingsException();
        setPeriod(Long.parseLong(settings[0]));
        setCandlePriceType(CandlePriceType.valueOf(settings[1]));
        return new RelativeStrengthIndex(indicatorPeriod, candlePriceType, createCandlesUpdater());
    }

    private void setCandlesUpdaterConnector(CandlesUpdaterConnector connector){
        if (connector == null)
            throw new NoSuchConnectorException();
        updaterConnector = connector;
    }

    private CandlesUpdatable createCandlesUpdater() {
        return new CandlesUpdater(updaterConnector);
    }
    
    private boolean notInBoundary(long candlesticksQuantity) {
        return candlesticksQuantity < MIN_INDICATOR_PERIOD || candlesticksQuantity > MAX_INDICATOR_PERIOD;
    }
    
    private boolean isNull(Object object) {
        return object == null;
    }

}
