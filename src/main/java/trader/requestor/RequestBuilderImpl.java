package trader.requestor;

import trader.broker.connector.BrokerConnector;
import trader.entity.indicator.Indicator;
import trader.entity.indicator.ma.MovingAverageBuilder;
import trader.entity.indicator.ma.enums.MAType;
import trader.entity.indicator.rsi.RSIBuilder;
import trader.exception.EmptyArgumentException;
import trader.exception.NoSuchDataStructureException;
import trader.exception.NullArgumentException;
import trader.exception.WrongIndicatorSettingsException;
import trader.interactor.enums.DataStructureType;
import trader.interactor.enums.IndicatorTypes;
import trader.interactor.RequestImpl;
import trader.configuration.TradingStrategyConfiguration;
import trader.configuration.BGXConfigurationImpl;
import java.util.HashMap;

public class RequestBuilderImpl implements RequestBuilder {


    private static final String LOCATION = "location";
    private static final String BROKER_NAME = "brokerName";

    @Override
    public Request<?> build(String controllerName, HashMap<String, String> settings) {
        verifyInput(controllerName, settings);
        controllerName = controllerName.trim().toLowerCase();
        if(controllerName.contains(type(DataStructureType.INDICATOR)))
            return buildIndicatorRequest(settings);
        if(controllerName.contains(type(DataStructureType.BGX_CONFIGURATION)))
            return buildBGXConfigurationRequest(settings);
        if(controllerName.contains(type(DataStructureType.BROKER_CONNECTOR)))
            return buildBrokerConnector(settings);

        throw new NoSuchDataStructureException();
    }

    private Request<?> buildBrokerConnector(HashMap<String, String> settings) {
        Request<BrokerConnector> request = new RequestImpl<>();
        BrokerConnector brokerConnector = BrokerConnector.create(settings.get(BROKER_NAME));
        brokerConnector.setFileLocation(settings.get(LOCATION));
        request.setRequestDataStructure(brokerConnector);
        return request;
    }

    private Request<?> buildBGXConfigurationRequest(HashMap<String, String> settings) {
        Request<TradingStrategyConfiguration> request = new RequestImpl<>();
        BGXConfigurationImpl bgxConfiguration = new BGXConfigurationImpl();
        bgxConfiguration.setFileLocation(settings.get(LOCATION));
        request.setRequestDataStructure(bgxConfiguration);
        return request;
    }

    private Request<?> buildIndicatorRequest(HashMap<String, String> settings) {
        Request<Indicator> request = new RequestImpl<>();
        String dataStructureType = getDataStructureType(settings).toLowerCase();
        if (dataStructureType.contains("rsi")) {
            request.setRequestDataStructure(new RSIBuilder().build(settings));
            return request;
        } else if(isMovingAverage(dataStructureType)) {
            request.setRequestDataStructure(new MovingAverageBuilder().build(settings));
            return request;
        }
        throw new NoSuchDataStructureException();
    }

    private String getDataStructureType(HashMap<String, String> settings) {
        if(settings.size() == 0)
            throw new EmptyArgumentException();
        if(!settings.containsKey("type"))
            throw new WrongIndicatorSettingsException();
        String dataStructureType = settings.get("type");
        if(dataStructureType == null || dataStructureType.isEmpty())
            throw new WrongIndicatorSettingsException();
        return dataStructureType;
    }

    private boolean isMovingAverage(String dataStructureName) {
        return dataStructureName.contains(indicator(MAType.SIMPLE)) ||
                dataStructureName.contains(indicator(MAType.WEIGHTED)) ||
                dataStructureName.contains(indicator(MAType.EXPONENTIAL));
    }

    private void verifyInput(String controllerName, HashMap<String, String> settings) {
        if(controllerName == null || settings == null)
            throw new NullArgumentException();
        if(controllerName.trim().isEmpty())
            throw new EmptyArgumentException();
    }

    private String indicator(MAType name){
        return name.toString().trim().toLowerCase();
    }

    private String type(DataStructureType type){
        return type.toString().trim().toLowerCase();
    }
}
