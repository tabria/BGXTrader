package trader.interactor.addbgxconfiguration;

import trader.configuration.BGXConfigurationImpl;
import trader.entity.candlestick.candle.CandleGranularity;
import trader.exception.EmptyArgumentException;
import trader.interactor.ResponseImpl;
import trader.interactor.addbgxconfiguration.enums.Constants;
import trader.requestor.Request;
import trader.requestor.UseCase;
import trader.responder.Response;
import trader.configuration.TradingStrategyConfiguration;

import java.math.BigDecimal;
import java.util.Map;

public class AddBGXConfigurationUseCase implements UseCase {


    private static final String CANDLES_QUANTITY = "candlesQuantity";
    private static final String INITIAL = "initial";
    private static final String UPDATE = "update";
    private static final String RISK = "risk";
    private static final String RISK_PER_TRADE = "riskPerTrade";
    private static final String INDICATOR = "indicator";
    private static final String ENTRY_STRATEGY = "entryStrategy";
    private static final String ORDER_STRATEGY = "orderStrategy";
    private static final String EXIT_STRATEGY = "exitStrategy";
    private static final String ENTRY = "entry";
    private static final String ORDER = "order";
    private static final String EXIT = "exit";
    private static final String STOP_LOSS_FILTER = "stopLossFilter";
    private static final String TARGET = "target";
    private static final String RSI_FILTER = "rsiFilter";
    private static final String ENTRY_FILTER = "entryFilter";

    public <T, E> Response<E> execute(Request<T> request) {
        Map<String, Map<String, String>> settings = (Map<String, Map<String, String>>) request.getBody();
//        TradingStrategyConfiguration bgxConfiguration = (TradingStrategyConfiguration) request.getBody();
        setConfigurations(settings);
        return null; //setResponse((E) bgxConfiguration);
    }

    private <E> Response<E> setResponse(E bgxConfiguration) {
        Response<E> response = new ResponseImpl<>();
        response.setResponseDataStructure(bgxConfiguration);
        return response;
    }

    private void setConfigurations(Map<String, Map<String, String>> settings) {
            TradingStrategyConfiguration bgxConfiguration = new BGXConfigurationImpl();
            setCandlesQuantities(bgxConfiguration, settings);
            setRiskPerTrade(bgxConfiguration, settings);
            setIndicators(bgxConfiguration, settings);
            setEntryStrategy(bgxConfiguration, settings);
            setOrderStrategy(bgxConfiguration, settings);
            setExitStrategy(bgxConfiguration, settings);

    }

    private void setIndicators(TradingStrategyConfiguration bgxConfiguration, Map<String, Map<String, String>> bgxSettings) {
        for (Map.Entry<String, Map<String, String>> entry : bgxSettings.entrySet()) {
            String keyName = entry.getKey();
            if(keyName.toLowerCase().trim().contains(INDICATOR)){
              //  bgxConfiguration.addIndicator(entry.getValue());
            }
        }
    }

    void setCandlesQuantities(TradingStrategyConfiguration bgxConfiguration, Map<String, Map<String, String>> bgxSettings) {
        if(containsFieldType(bgxSettings, Constants.CANDLES_QUANTITY.toString())){
            Map<String, String> quantitiesValues = bgxSettings.get(Constants.CANDLES_QUANTITY.toString());
            if(containsField(quantitiesValues, Constants.INITIAL.toString())){
                long initialValue = Long.parseLong(quantitiesValues.get(Constants.INITIAL.toString()).trim());
               // bgxConfiguration.setInitialCandlesQuantity(initialValue);
            }
            if(containsField(quantitiesValues, Constants.UPDATE.toString())){
                long updateValue = Long.parseLong(quantitiesValues.get(Constants.UPDATE.toString()).trim());
              //  bgxConfiguration.setUpdateCandlesQuantity(updateValue);
            }
        }
    }



    void setRiskPerTrade(TradingStrategyConfiguration bgxConfiguration, Map<String, Map<String, String>> bgxSettings) {
        if(containsFieldType(bgxSettings, RISK)){
            Map<String, String> riskValue = bgxSettings.get(RISK);
            if(containsField(riskValue, RISK_PER_TRADE)){
                BigDecimal riskPerTrade =  new BigDecimal(riskValue.get(RISK_PER_TRADE).trim());
                bgxConfiguration.setRiskPerTrade(riskPerTrade);
            }
        }
    }

    void setEntryStrategy(TradingStrategyConfiguration bgxConfiguration, Map<String, Map<String, String>> bgxSettings) {
        if(containsFieldType(bgxSettings, ENTRY)){
            Map<String, String> entryStrategy = bgxSettings.get(ENTRY);
            if(containsField(entryStrategy, ENTRY_STRATEGY))
                bgxConfiguration.setEntryStrategy(entryStrategy.get(ENTRY_STRATEGY).trim());
            if(containsField(entryStrategy, STOP_LOSS_FILTER)) {
                BigDecimal stopLossValue =  new BigDecimal(entryStrategy.get(STOP_LOSS_FILTER).trim());
                bgxConfiguration.setStopLossFilter(stopLossValue);
            }
            if(containsField(entryStrategy, TARGET)) {
                BigDecimal stopLossValue =  new BigDecimal(entryStrategy.get(TARGET).trim());
                bgxConfiguration.setTarget(stopLossValue);
            }
            if(containsField(entryStrategy, RSI_FILTER)) {
                BigDecimal stopLossValue =  new BigDecimal(entryStrategy.get(RSI_FILTER).trim());
                bgxConfiguration.setRsiFilter(stopLossValue);
            }
            if(containsField(entryStrategy, ENTRY_FILTER)) {
                BigDecimal stopLossValue =  new BigDecimal(entryStrategy.get(ENTRY_FILTER).trim());
                bgxConfiguration.setEntryFilter(stopLossValue);
            }

        }
    }

    void setOrderStrategy(TradingStrategyConfiguration bgxConfiguration, Map<String, Map<String, String>> bgxSettings) {
        if(containsFieldType(bgxSettings, ORDER)){
            Map<String, String> orderValues = bgxSettings.get(ORDER);
            if(containsField(orderValues, ORDER_STRATEGY)){
                bgxConfiguration.setOrderStrategy(orderValues.get(ORDER_STRATEGY).trim());
            }
        }
    }

    void setExitStrategy(TradingStrategyConfiguration bgxConfiguration, Map<String, Map<String, String>> bgxSettings) {
        if(containsFieldType(bgxSettings, EXIT)){
            Map<String, String> exitValues = bgxSettings.get(EXIT);
            if(containsField(exitValues, EXIT_STRATEGY))
                bgxConfiguration.setExitStrategy(exitValues.get(EXIT_STRATEGY).trim());
            if(containsField(exitValues, "exitGranularity"))
                 bgxConfiguration.setExitGranularity(parseGranularity(exitValues.get("exitGranularity")));
        }
    }

    private boolean containsField(Map<String, String> quantitiesValues, String s) {
        return quantitiesValues.containsKey(s);
    }

    private boolean containsFieldType(Map<String, Map<String, String>> bgxSettings, String s) {
        return bgxSettings.containsKey(s);
    }

    private CandleGranularity parseGranularity(String setting) {
        try {
            return CandleGranularity.valueOf(setting.trim().toUpperCase());
        } catch (Exception e) {
            throw new EmptyArgumentException();
        }
    }

}
