package trader.strategy.bgxstrategy.configuration;

import trader.exception.NullArgumentException;

import java.util.HashMap;

public class Configuration {

    private HashMap<String, String> indicators;


    public Configuration() {
        this.indicators = new HashMap<>();
    }

    public HashMap<String, String> getIndicators() {
        return this.indicators;
    }

    public void setIndicators(HashMap<String, String> indicators) {
        if(indicators == null)
            throw new NullArgumentException();
        this.indicators = indicators;
    }
}
