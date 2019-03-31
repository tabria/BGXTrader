package trader.indicator.rsi;

import trader.candlestick.Candlestick;
import trader.exception.*;
import trader.indicator.Indicator;
import trader.candlestick.candle.CandlePriceType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class RSIBuilder {

    private static final long DEFAULT_INDICATOR_PERIOD = 14L;
    private static final long MIN_INDICATOR_PERIOD = 1L;
    private static final long MAX_INDICATOR_PERIOD = 1000L;
    private static final CandlePriceType DEFAULT_CANDLESTICK_PRICE_TYPE = CandlePriceType.CLOSE;
    private static final int SETTABLE_FIELDS_COUNT = 2;
    private static final String SETTINGS_PERIOD_KEY_NAME = "period";
    private static final String SETTINGS_CANDLE_PRICE_TYPE_KEY_NAME = "candlePriceType";

    private List<Candlestick> candlestickList;
    private long indicatorPeriod;
    private CandlePriceType candlePriceType;

    public RSIBuilder(){
        this.candlestickList = new ArrayList<>();
        setPeriod("");
        setCandlePriceType("");
    }

    public RSIBuilder setPeriod(String period) {
        long rsiPeriod = parsePeriod(period);
        checkBoundaries(rsiPeriod);
        this.indicatorPeriod = rsiPeriod;
        return this;
    }

    public RSIBuilder setCandlePriceType(String candlePriceType) {
        this.candlePriceType = parseCandlePriceType(candlePriceType);
        return this;
    }

    public Indicator build(HashMap<String, String> settings){
        if (settings == null || settings.size() != SETTABLE_FIELDS_COUNT)
            throw new WrongIndicatorSettingsException();
        setPeriod(settings.get(SETTINGS_PERIOD_KEY_NAME));
        setCandlePriceType(settings.get(SETTINGS_CANDLE_PRICE_TYPE_KEY_NAME));
        return new RelativeStrengthIndex(indicatorPeriod, candlePriceType, candlestickList);
    }

    private long parsePeriod(String period) {
        if(!period.isEmpty())
            return parseStringToLong(period);
        return DEFAULT_INDICATOR_PERIOD;
    }

    private long parseStringToLong(String period) {
        try{
            return Long.parseLong(period);
        } catch (Exception e){
            throw new WrongIndicatorSettingsException();
        }
    }

    private void checkBoundaries(long period) {
        if (period < MIN_INDICATOR_PERIOD || period > MAX_INDICATOR_PERIOD)
            throw  new OutOfBoundaryException();
    }

    private CandlePriceType parseCandlePriceType(String candlePriceType) {
        if (!candlePriceType.isEmpty()) {
            try {
                return CandlePriceType.valueOf(candlePriceType.toUpperCase());
            } catch (Exception e) {
                throw new WrongIndicatorSettingsException();
            }
        }
        return DEFAULT_CANDLESTICK_PRICE_TYPE;
    }
}
