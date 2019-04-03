package trader.interactor;

import org.yaml.snakeyaml.Yaml;
import trader.exception.BadRequestException;
import trader.exception.NullArgumentException;
import trader.requestor.Request;
import trader.responder.Response;
import trader.strategy.bgxstrategy.configuration.BGXConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class BGXConfigurationUseCase implements UseCase {


    @Override
    public <T, E> Response<E> execute(Request<T> request) {
        if(request == null)
            throw new NullArgumentException();
        BGXConfiguration bgxConfiguration = (BGXConfiguration) request.getRequestDataStructure();
        setConfigurations(bgxConfiguration);
        return setResponse((E) bgxConfiguration);
    }

    private <E> Response<E> setResponse(E bgxConfiguration) {
        Response<E> response = new ResponseImpl<>();
        response.setResponseDataStructure(bgxConfiguration);
        return response;
    }

    private void setConfigurations(BGXConfiguration bgxConfiguration) {
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

    private void setIndicators(BGXConfiguration bgxConfiguration, HashMap<String, HashMap<String, String>> bgxSettings) {
        for (HashMap.Entry<String, HashMap<String, String>> entry : bgxSettings.entrySet()) {
            String keyName = entry.getKey();
            if(keyName.toLowerCase().trim().contains("indicator")){
                bgxConfiguration.addIndicator(entry.getValue());
            }
        }
    }

    void setCandlesQuantities(BGXConfiguration bgxConfiguration, HashMap<String, HashMap<String, String>> bgxSettings) {
        if(bgxSettings.containsKey("candlesQuantity")){
            Map<String, String> quantitiesValues = bgxSettings.get("candlesQuantity");
            if(quantitiesValues.containsKey("initial")){
                long initialValue = Long.parseLong(quantitiesValues.get("initial").trim());
                bgxConfiguration.setInitialCandlesQuantity(initialValue);
            }
            if(quantitiesValues.containsKey("update")){
                long updateValue = Long.parseLong(quantitiesValues.get("update").trim());
                bgxConfiguration.setUpdateCandlesQuantity(updateValue);
            }
        }
    }

    void setRiskPerTrade(BGXConfiguration bgxConfiguration, HashMap<String, HashMap<String, String>> bgxSettings) {
        if(bgxSettings.containsKey("risk")){
            Map<String, String> quantitiesValues = bgxSettings.get("risk");
            if(quantitiesValues.containsKey("riskPerTrade")){
                BigDecimal riskPerTrade =  new BigDecimal(quantitiesValues.get("riskPerTrade").trim());
                bgxConfiguration.setRiskPerTrade(riskPerTrade);
            }
        }
    }

}
