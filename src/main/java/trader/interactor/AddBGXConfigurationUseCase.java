package trader.interactor;

import org.yaml.snakeyaml.Yaml;
import trader.entity.candlestick.candle.CandleGranularity;
import trader.exception.BadRequestException;
import trader.exception.EmptyArgumentException;
import trader.exception.NullArgumentException;
import trader.requestor.Request;
import trader.requestor.UseCase;
import trader.responder.Response;
import trader.configuration.TradingStrategyConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class AddBGXConfigurationUseCase extends BaseUseCase {


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

    @Override
    public <T, E> Response<E> execute(Request<T> request) {
        if(request == null)
            throw new NullArgumentException();
        TradingStrategyConfiguration bgxConfiguration = (TradingStrategyConfiguration) request.getRequestDataStructure();
        setConfigurations(bgxConfiguration);
        return setResponse((E) bgxConfiguration);
    }

    private <E> Response<E> setResponse(E bgxConfiguration) {
        Response<E> response = new ResponseImpl<>();
        response.setResponseDataStructure(bgxConfiguration);
        return response;
    }

    private void setConfigurations(TradingStrategyConfiguration bgxConfiguration) {
        String location = bgxConfiguration.getFileLocation();
        Yaml yaml = new Yaml();
        try(InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(location)){
            HashMap<String, HashMap<String, String>> bgxSettings = yaml.load(is);
            setCandlesQuantities(bgxConfiguration, bgxSettings);
            setRiskPerTrade(bgxConfiguration, bgxSettings);
            setIndicators(bgxConfiguration, bgxSettings);
            setEntryStrategy(bgxConfiguration, bgxSettings);
            setOrderStrategy(bgxConfiguration, bgxSettings);
            setExitStrategy(bgxConfiguration, bgxSettings);
        } catch (IOException | RuntimeException e) {
            BadRequestException badRequestException = new BadRequestException();
            badRequestException.initCause(e);
            throw badRequestException;
        }
    }

    private void setIndicators(TradingStrategyConfiguration bgxConfiguration, HashMap<String, HashMap<String, String>> bgxSettings) {
        for (HashMap.Entry<String, HashMap<String, String>> entry : bgxSettings.entrySet()) {
            String keyName = entry.getKey();
            if(keyName.toLowerCase().trim().contains(INDICATOR)){
                bgxConfiguration.addIndicator(entry.getValue());
            }
        }
    }

    void setCandlesQuantities(TradingStrategyConfiguration bgxConfiguration, HashMap<String, HashMap<String, String>> bgxSettings) {
        if(bgxSettings.containsKey(CANDLES_QUANTITY)){
            Map<String, String> quantitiesValues = bgxSettings.get(CANDLES_QUANTITY);
            if(quantitiesValues.containsKey(INITIAL)){
                long initialValue = Long.parseLong(quantitiesValues.get(INITIAL).trim());
                bgxConfiguration.setInitialCandlesQuantity(initialValue);
            }
            if(quantitiesValues.containsKey(UPDATE)){
                long updateValue = Long.parseLong(quantitiesValues.get(UPDATE).trim());
                bgxConfiguration.setUpdateCandlesQuantity(updateValue);
            }
        }
    }

    void setRiskPerTrade(TradingStrategyConfiguration bgxConfiguration, HashMap<String, HashMap<String, String>> bgxSettings) {
        if(bgxSettings.containsKey(RISK)){
            Map<String, String> riskValue = bgxSettings.get(RISK);
            if(riskValue.containsKey(RISK_PER_TRADE)){
                BigDecimal riskPerTrade =  new BigDecimal(riskValue.get(RISK_PER_TRADE).trim());
                bgxConfiguration.setRiskPerTrade(riskPerTrade);
            }
        }
    }

    void setEntryStrategy(TradingStrategyConfiguration bgxConfiguration, HashMap<String, HashMap<String, String>> bgxSettings) {
        if(bgxSettings.containsKey(ENTRY)){
            Map<String, String> entryStrategy = bgxSettings.get(ENTRY);
            if(entryStrategy.containsKey(ENTRY_STRATEGY))
                bgxConfiguration.setEntryStrategy(entryStrategy.get(ENTRY_STRATEGY).trim());
            if(entryStrategy.containsKey(STOP_LOSS_FILTER)) {
                BigDecimal stopLossValue =  new BigDecimal(entryStrategy.get(STOP_LOSS_FILTER).trim());
                bgxConfiguration.setStopLossFilter(stopLossValue);
            }
            if(entryStrategy.containsKey(TARGET)) {
                BigDecimal stopLossValue =  new BigDecimal(entryStrategy.get(TARGET).trim());
                bgxConfiguration.setTarget(stopLossValue);
            }
            if(entryStrategy.containsKey(RSI_FILTER)) {
                BigDecimal stopLossValue =  new BigDecimal(entryStrategy.get(RSI_FILTER).trim());
                bgxConfiguration.setRsiFilter(stopLossValue);
            }
            if(entryStrategy.containsKey(ENTRY_FILTER)) {
                BigDecimal stopLossValue =  new BigDecimal(entryStrategy.get(ENTRY_FILTER).trim());
                bgxConfiguration.setEntryFilter(stopLossValue);
            }

        }
    }

    void setOrderStrategy(TradingStrategyConfiguration bgxConfiguration, HashMap<String, HashMap<String, String>> bgxSettings) {
        if(bgxSettings.containsKey(ORDER)){
            Map<String, String> orderValues = bgxSettings.get(ORDER);
            if(orderValues.containsKey(ORDER_STRATEGY)){
                bgxConfiguration.setOrderStrategy(orderValues.get(ORDER_STRATEGY).trim());
            }
        }
    }

    void setExitStrategy(TradingStrategyConfiguration bgxConfiguration, HashMap<String, HashMap<String, String>> bgxSettings) {
        if(bgxSettings.containsKey(EXIT)){
            Map<String, String> exitValues = bgxSettings.get(EXIT);
            if(exitValues.containsKey(EXIT_STRATEGY))
                bgxConfiguration.setExitStrategy(exitValues.get(EXIT_STRATEGY).trim());
            if(exitValues.containsKey("exitGranularity"))
                 bgxConfiguration.setExitGranularity(parseGranularity(exitValues.get("exitGranularity")));
        }
    }

    private CandleGranularity parseGranularity(String setting) {
        try {
            return CandleGranularity.valueOf(setting.trim().toUpperCase());
        } catch (Exception e) {
            throw new EmptyArgumentException();
        }
    }

}
