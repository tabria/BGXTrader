package trader.exit.service;

import trader.broker.BrokerGateway;
import trader.configuration.TradingStrategyConfiguration;
import trader.entity.candlestick.Candlestick;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UpdateCandlesService {

    private HashMap<String, String> settings;
    private List<Candlestick> candlesticks;

    public UpdateCandlesService() {
        this.settings = new HashMap<>();
        candlesticks = new ArrayList<>();
    }

    public List<Candlestick> getCandlesticks() {
        return candlesticks;
    }

    public HashMap<String, String> getSettings() {
        return settings;
    }

    public void updateCandles(BrokerGateway brokerGateway, TradingStrategyConfiguration configuration) {
        setCandlesQuantity(configuration);
        setCandlesticks(brokerGateway.getCandles(settings));
    }

    private void setCandlesticks(List<Candlestick> candles) {
        if(candlesticks.size() >0)
            candlesticks.add(candles.get(candles.size()-1));
        else
            candlesticks.addAll(candles);
    }

    private void setCandlesQuantity(TradingStrategyConfiguration configuration) {
        if(settings.size() == 0)
            initializeRequestSettings(configuration);
        else
            setUpdateQuantityInSettings(configuration);
    }

    private void setUpdateQuantityInSettings(TradingStrategyConfiguration configuration) {
        if(candlesticks.size() != 0)
            settings.put("quantity", String.valueOf(configuration.getUpdateCandlesQuantity()));
    }

    private void initializeRequestSettings(TradingStrategyConfiguration configuration){
        settings.put("instrument", configuration.getInstrument());
        settings.put("quantity", String.valueOf(configuration.getInitialCandlesQuantity()));
        settings.put("granularity", configuration.getExitGranularity().toString());
    }

}
