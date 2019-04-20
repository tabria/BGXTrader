package trader.interactor.createbgxconfiguration;

import trader.configuration.BGXConfigurationImpl;
import trader.interactor.ResponseImpl;
import trader.interactor.createbgxconfiguration.enums.Constants;
import trader.presenter.Presenter;
import trader.requestor.Request;
import trader.requestor.UseCase;
import trader.responder.Response;
import trader.configuration.TradingStrategyConfiguration;

import java.util.Map;

public class CreateBGXConfigurationUseCase implements UseCase {

    private Presenter presenter;

    public CreateBGXConfigurationUseCase(Presenter presenter) {
        this.presenter = presenter;
    }

    public <T, E> Response<E> execute(Request<T> request) {
        Map<String, Map<String, String>> settings = (Map<String, Map<String, String>>) request.getBody();
        TradingStrategyConfiguration bgxConfiguration = setConfigurations(settings);
        Response<E> response = setResponse((E) bgxConfiguration);
        presenter.execute(response);
        return response;
    }

    private <E> Response<E> setResponse(E bgxConfiguration) {
        Response<E> response = new ResponseImpl<>();
        response.setBody(bgxConfiguration);
        return response;
    }

    private TradingStrategyConfiguration setConfigurations(Map<String, Map<String, String>> settings) {
            TradingStrategyConfiguration bgxConfiguration = new BGXConfigurationImpl();
            setIndicators(bgxConfiguration, settings);
            setCandlesQuantities(bgxConfiguration, settings);
            setRiskPerTrade(bgxConfiguration, settings);
            setEntryStrategy(bgxConfiguration, settings);
            setOrderStrategy(bgxConfiguration, settings);
            setExitStrategy(bgxConfiguration, settings);
        return bgxConfiguration;
    }

    void setIndicators(TradingStrategyConfiguration bgxConfiguration, Map<String, Map<String, String>> bgxSettings) {
        for (Map.Entry<String, Map<String, String>> entry : bgxSettings.entrySet()) {
            String keyName = entry.getKey();
            if(keyName.toLowerCase().trim().contains(Constants.INDICATOR.toString()))
                bgxConfiguration.addIndicator(entry.getValue());
        }
    }

    void setCandlesQuantities(TradingStrategyConfiguration bgxConfiguration, Map<String, Map<String, String>> bgxSettings) {
        if(fieldExist(bgxSettings, Constants.CANDLES_QUANTITY.toString())){
            Map<String, String> quantitiesValues = bgxSettings.get(Constants.CANDLES_QUANTITY.toString());
            if(fieldExist(quantitiesValues, Constants.INITIAL.toString()))
                bgxConfiguration.setInitialCandlesQuantity(getEntryValue(quantitiesValues, Constants.INITIAL.toString()));
            if(fieldExist(quantitiesValues, Constants.UPDATE.toString()))
                bgxConfiguration.setUpdateCandlesQuantity(getEntryValue(quantitiesValues, Constants.UPDATE.toString()));
        }
    }

    void setRiskPerTrade(TradingStrategyConfiguration bgxConfiguration, Map<String, Map<String, String>> bgxSettings) {
        if(fieldExist(bgxSettings, Constants.RISK.toString())){
            Map<String, String> riskValue = bgxSettings.get(Constants.RISK.toString());
            if(fieldExist(riskValue, Constants.RISK_PER_TRADE.toString()))
                bgxConfiguration.setRiskPerTrade(getEntryValue(riskValue, Constants.RISK_PER_TRADE.toString()));
        }
    }

    void setEntryStrategy(TradingStrategyConfiguration bgxConfiguration, Map<String, Map<String, String>> bgxSettings) {
        if(fieldExist(bgxSettings, Constants.ENTRY.toString())){
            Map<String, String> entryStrategy = bgxSettings.get(Constants.ENTRY.toString());
            if(fieldExist(entryStrategy, Constants.ENTRY_STRATEGY.toString()))
                bgxConfiguration.setEntryStrategy(getEntryValue(entryStrategy, Constants.ENTRY_STRATEGY.toString()));
            if(fieldExist(entryStrategy, Constants.STOP_LOSS_FILTER.toString()))
                bgxConfiguration.setStopLossFilter(getEntryValue(entryStrategy, Constants.STOP_LOSS_FILTER.toString()));
            if(fieldExist(entryStrategy, Constants.TARGET.toString()))
                bgxConfiguration.setTarget(getEntryValue(entryStrategy, Constants.TARGET.toString()));
            if(fieldExist(entryStrategy, Constants.RSI_FILTER.toString()))
                bgxConfiguration.setRsiFilter(getEntryValue(entryStrategy, Constants.RSI_FILTER.toString()));
            if(fieldExist(entryStrategy, Constants.ENTRY_FILTER.toString()))
                bgxConfiguration.setEntryFilter(getEntryValue(entryStrategy, Constants.ENTRY_FILTER.toString()));
        }
    }

    void setOrderStrategy(TradingStrategyConfiguration bgxConfiguration, Map<String, Map<String, String>> bgxSettings) {
        if(fieldExist(bgxSettings, Constants.ORDER.toString())){
            Map<String, String> orderValues = bgxSettings.get(Constants.ORDER.toString());
            if(fieldExist(orderValues, Constants.ORDER_STRATEGY.toString()))
                bgxConfiguration.setOrderStrategy(getEntryValue(orderValues, Constants.ORDER_STRATEGY.toString()));
        }
    }

    void setExitStrategy(TradingStrategyConfiguration bgxConfiguration, Map<String, Map<String, String>> bgxSettings) {
        if(fieldExist(bgxSettings, Constants.EXIT.toString())) {
            Map<String, String> exitValues = bgxSettings.get(Constants.EXIT.toString());
            if (fieldExist(exitValues, Constants.EXIT_STRATEGY.toString()))
                bgxConfiguration.setExitStrategy(getEntryValue(exitValues, Constants.EXIT_STRATEGY.toString()));
            if (fieldExist(exitValues, Constants.EXIT_GRANULARITY.toString()))
                bgxConfiguration.setExitGranularity(getEntryValue(exitValues, Constants.EXIT_GRANULARITY.toString()));
        }
    }

    private boolean fieldExist(Map<?, ?> collection, String fieldName) {
        return collection.containsKey(fieldName);
    }

    private String getEntryValue(Map<String, String> collection, String keyName) {
            return collection.get(keyName);
    }

}
