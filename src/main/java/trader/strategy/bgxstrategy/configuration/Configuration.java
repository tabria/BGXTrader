package trader.strategy.bgxstrategy.configuration;

import trader.exception.NullArgumentException;
import trader.exception.OverflowException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Configuration {

    private static final String DEFAULT_INSTRUMENT = "EUR_USD";
    private static final Long DEFAULT_INITIAL_CANDLES_QUANTITY = 4999L;
    private static final Long DEFAULT_UPDATE_CANDLES_QUANTITY = 2L;
    private static final int QUANTITY_ELEMENT_COUNT = 2;

    private HashMap<String, String> indicators;
    private List<Long> candlesQuantity;
    private String instrument;


    public Configuration() {
        this.indicators = new HashMap<>();
        this.candlesQuantity = setDefaultQuantity();
        this.instrument = DEFAULT_INSTRUMENT;
    }

    public HashMap<String, String> getIndicators() {
        return this.indicators;
    }

    public void setIndicators(HashMap<String, String> indicators) {
        if(indicators == null)
            throw new NullArgumentException();
        this.indicators = indicators;
    }

    public String getInstrument() {
        return this.instrument;
    }

    public List<Long> getCandlesQuantity() {
        return this.candlesQuantity;
    }

    public void setCandlesQuantity(List<Long> quantities) {
        if(quantities == null)
            throw new NullArgumentException();
        if(quantities.size() != QUANTITY_ELEMENT_COUNT)
            throw new OverflowException();
        this.candlesQuantity = quantities;
    }

    private List<Long> setDefaultQuantity() {
        List<Long> defaultQuantities = new ArrayList<>(2);
        defaultQuantities.add(DEFAULT_INITIAL_CANDLES_QUANTITY);
        defaultQuantities.add(DEFAULT_UPDATE_CANDLES_QUANTITY);
        return defaultQuantities;
    }
}
