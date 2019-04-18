package trader.interactor.createindicator;


import trader.entity.indicator.Indicator;
import trader.entity.indicator.ma.MovingAverageBuilder;
import trader.entity.indicator.ma.enums.MAType;
import trader.entity.indicator.rsi.RSIBuilder;
import trader.exception.EmptyArgumentException;
import trader.exception.NoSuchDataStructureException;
import trader.exception.WrongIndicatorSettingsException;
import trader.interactor.RequestImpl;
import trader.interactor.ResponseImpl;
import trader.interactor.createindicator.enums.Constants;
import trader.requestor.Request;
import trader.requestor.UseCase;
import trader.responder.Response;

import java.util.Map;

public class CreateIndicatorUseCase implements UseCase {

    @Override
    public <T, E> Response<E> execute(Request<T> request) {
        Map<String, Object> settings = (Map<String, Object>) request.getBody();
        Indicator indicator = setIndicator(settings);
        return setResponse((E) indicator);
    }

    private Indicator setIndicator(Map<String,Object> inputSettings) {
        Map<String, String> settings = (Map<String, String>) inputSettings.get("settings");
        String dataStructureType = getDataStructureType(settings);
        if(dataStructureType.contains(Constants.RSI.toString()))
            return new RSIBuilder().build(settings);
        if(isMovingAverage(dataStructureType))
            return new MovingAverageBuilder().build(settings);
        throw new NoSuchDataStructureException();
    }

    private <E> Response<E> setResponse(E indicator) {
        Response<E> response = new ResponseImpl<>();
        response.setBody(indicator);
        return response;
    }

    private String getDataStructureType(Map<String, String> settings) {
        if(settings.size() == 0)
            throw new EmptyArgumentException();
        if(!settings.containsKey("type"))
            throw new WrongIndicatorSettingsException();
        String dataStructureType = settings.get("type");
        if(dataStructureType == null || dataStructureType.trim().isEmpty())
            throw new WrongIndicatorSettingsException();
        return dataStructureType.trim();
    }

    private boolean isMovingAverage(String dataStructureName) {
        return dataStructureName.contains(indicator(MAType.SIMPLE)) ||
                dataStructureName.contains(indicator(MAType.WEIGHTED)) ||
                dataStructureName.contains(indicator(MAType.EXPONENTIAL));
    }

    private String indicator(MAType name){
        return name.toString().trim().toLowerCase();
    }


}
