package trader.entity.indicator;

import trader.entity.candlestick.Candlestick;
import trader.entity.candlestick.candle.CandleGranularity;
import trader.entity.candlestick.candle.CandlePriceType;
import trader.exception.OutOfBoundaryException;
import trader.exception.WrongIndicatorSettingsException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class BaseIndicatorBuilder {
    private static final long DEFAULT_INDICATOR_PERIOD = 14L;
    private static final long MIN_INDICATOR_PERIOD = 1L;
    private static final long MAX_INDICATOR_PERIOD = 4000L;
    private static final CandlePriceType DEFAULT_CANDLESTICK_PRICE_TYPE = CandlePriceType.CLOSE;
    private static final CandleGranularity DEFAULT_GRANULARITY = CandleGranularity.M30;
    private static final String SETTINGS_PERIOD_KEY_NAME = "period";
    private static final String SETTINGS_CANDLE_PRICE_TYPE_KEY_NAME = "candlePriceType";
    private static final String SETTINGS_GRANULARITY_NAME = "granularity";
    private static final String SETTINGS_POSITION_NAME = "position";

    protected List<Candlestick> candlestickList;
    protected long indicatorPeriod;
    protected CandlePriceType candlePriceType;
    protected CandleGranularity granularity;
    protected String position;

    public BaseIndicatorBuilder() {
        this.candlestickList = new ArrayList<>();
    }

    public BaseIndicatorBuilder setPeriod(HashMap<String, String> settings) {
        long indicatorPeriod = parsePeriod(settings);
        checkPeriodBoundaries(indicatorPeriod);
        this.indicatorPeriod = indicatorPeriod;
        return this;
    }

    public BaseIndicatorBuilder setCandlePriceType(HashMap<String, String> settings) {
        this.candlePriceType = parseCandlePriceType(settings);
        return this;
    }

    public BaseIndicatorBuilder setGranularity(HashMap<String, String> settings){
        this.granularity = parseGranularity(settings);
        return this;
    }

    public BaseIndicatorBuilder setPosition(HashMap<String,String> settings){
        verifyPositionExistence(settings);
        this.position = settings.get(SETTINGS_POSITION_NAME);
        return this;
    }

    protected boolean isNotDefault(HashMap<String, String> settings, String settingKeyName) {
        return  settings.containsKey(settingKeyName) &&
                settings.get(settingKeyName) != null &&
                !settings.get(settingKeyName).isEmpty();
    }

    private long parsePeriod(HashMap<String, String> settings) {
        if(isNotDefault(settings, SETTINGS_PERIOD_KEY_NAME))
            return parseStringToLong(settings.get(SETTINGS_PERIOD_KEY_NAME));
        return DEFAULT_INDICATOR_PERIOD;
    }

    private long parseStringToLong(String period) {
        try{
            return Long.parseLong(period.trim());
        } catch (Exception e){
            throw new WrongIndicatorSettingsException();
        }
    }

    private void checkPeriodBoundaries(long period) {
        if (period < MIN_INDICATOR_PERIOD || period > MAX_INDICATOR_PERIOD)
            throw  new OutOfBoundaryException();
    }

    private void verifyPositionExistence(HashMap<String, String> settings) {
        if (settings == null ||
                !settings.containsKey(SETTINGS_POSITION_NAME) ||
                settings.get(SETTINGS_POSITION_NAME) == null ||
                settings.get(SETTINGS_POSITION_NAME).trim().isEmpty())
            throw new WrongIndicatorSettingsException();
    }

    private CandlePriceType parseCandlePriceType(HashMap<String, String> settings) {
        if (isNotDefault(settings, SETTINGS_CANDLE_PRICE_TYPE_KEY_NAME)) {
            try {
                return CandlePriceType.valueOf(settings.get(SETTINGS_CANDLE_PRICE_TYPE_KEY_NAME).trim().toUpperCase());
            } catch (Exception e) {
                throw new WrongIndicatorSettingsException();
            }
        }
        return DEFAULT_CANDLESTICK_PRICE_TYPE;
    }

    private CandleGranularity parseGranularity(HashMap<String, String> settings) {
        if (isNotDefault(settings, SETTINGS_GRANULARITY_NAME)) {
            try {
                return CandleGranularity.valueOf(settings.get(SETTINGS_GRANULARITY_NAME).trim().toUpperCase());
            } catch (Exception e) {
                throw new WrongIndicatorSettingsException();
            }
        }
        return DEFAULT_GRANULARITY;
    }
}
