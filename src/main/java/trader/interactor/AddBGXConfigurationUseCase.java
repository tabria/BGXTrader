package trader.interactor;

import org.yaml.snakeyaml.Yaml;
import trader.exception.BadRequestException;
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

public class AddBGXConfigurationUseCase implements UseCase {


    private static final String CANDLES_QUANTITY = "candlesQuantity";
    private static final String INITIAL = "initial";
    private static final String UPDATE = "update";
    private static final String RISK = "risk";
    private static final String RISK_PER_TRADE = "riskPerTrade";
    private static final String INDICATOR = "indicator";

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
            Map<String, String> quantitiesValues = bgxSettings.get(RISK);
            if(quantitiesValues.containsKey(RISK_PER_TRADE)){
                BigDecimal riskPerTrade =  new BigDecimal(quantitiesValues.get(RISK_PER_TRADE).trim());
                bgxConfiguration.setRiskPerTrade(riskPerTrade);
            }
        }
    }

}
