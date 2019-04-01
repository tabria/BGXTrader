package trader.interactor;

import trader.exception.EmptyArgumentException;
import trader.exception.NoSuchDataStructureException;
import trader.exception.NullArgumentException;
import trader.entity.indicator.Indicator;
import trader.entity.indicator.ma.MovingAverageBuilder;
import trader.entity.indicator.rsi.RSIBuilder;
import trader.requestor.Request;
import trader.requestor.RequestBuilder;

import java.util.HashMap;

public class RequestBuilderImpl implements RequestBuilder {

    @Override
    public Request<?> build(String dataStructureName, HashMap<String, String> settings) {
        verifyInput(dataStructureName, settings);
        dataStructureName = dataStructureName.trim().toLowerCase();
        if(dataStructureName.contains("indicator"))
            return buildIndicatorRequest(dataStructureName, settings);

        throw new NoSuchDataStructureException();
    }

    private Request<?> buildIndicatorRequest(String dataStructureName, HashMap<String, String> settings) {
        Request<Indicator> request = new RequestImpl<>();
        if(dataStructureName.contains("rsi")){
            request.setRequestDataStructure(new RSIBuilder().build(settings));
        } else if(dataStructureName.contains("sma")){
            request.setRequestDataStructure(new MovingAverageBuilder().build(settings));
        }
        return request;
    }

    private void verifyInput(String dataStructureName, HashMap<String, String> settings) {
        if(dataStructureName == null || settings == null)
            throw new NullArgumentException();
        if(dataStructureName.trim().isEmpty())
            throw new EmptyArgumentException();
    }
}
