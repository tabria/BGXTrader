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
    }

    public RSIBuilder setPeriod(HashMap<String, String> settings) {
        long rsiPeriod = parsePeriod(settings);
        checkPeriodBoundaries(rsiPeriod);
        this.indicatorPeriod = rsiPeriod;
        return this;
    }

    public RSIBuilder setCandlePriceType(HashMap<String, String> settings) {
        this.candlePriceType = parseCandlePriceType(settings);
        return this;
    }

    public Indicator build(HashMap<String, String> settings){
        if (settings == null || settings.size() > SETTABLE_FIELDS_COUNT)
            throw new WrongIndicatorSettingsException();
        setPeriod(settings);
        setCandlePriceType(settings);
        return new RelativeStrengthIndex(indicatorPeriod, candlePriceType, candlestickList);
    }

    private long parsePeriod(HashMap<String, String> settings) {
        if(isNotDefault(settings, SETTINGS_PERIOD_KEY_NAME))
            return parseStringToLong(settings.get(SETTINGS_PERIOD_KEY_NAME));
        return DEFAULT_INDICATOR_PERIOD;
    }

    private boolean isNotDefault(HashMap<String, String> settings, String settingKeyName) {
        return settings.containsKey(settingKeyName) &&
                !settings.get(settingKeyName).isEmpty();
    }

    private long parseStringToLong(String period) {
        try{
            return Long.parseLong(period);
        } catch (Exception e){
            throw new WrongIndicatorSettingsException();
        }
    }

    private void checkPeriodBoundaries(long period) {
        if (period < MIN_INDICATOR_PERIOD || period > MAX_INDICATOR_PERIOD)
            throw  new OutOfBoundaryException();
    }

    private CandlePriceType parseCandlePriceType(HashMap<String, String> settings) {
        if (isNotDefault(settings, SETTINGS_CANDLE_PRICE_TYPE_KEY_NAME)) {
            try {
                return CandlePriceType.valueOf(settings.get(SETTINGS_CANDLE_PRICE_TYPE_KEY_NAME).toUpperCase());
            } catch (Exception e) {
                throw new WrongIndicatorSettingsException();
            }
        }
        return DEFAULT_CANDLESTICK_PRICE_TYPE;
    }
}
