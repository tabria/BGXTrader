package trader;

import trader.exception.EmptyArgumentException;
import trader.exception.NoSuchDataStructureException;
import trader.exception.NullArgumentException;
import trader.indicator.Indicator;
import trader.indicator.ma.MovingAverageBuilder;
import trader.indicator.rsi.RSIBuilder;

import java.util.HashMap;

public class RequestBuilderImpl implements RequestBuilder {
    @Override
    public Request<?> build(String dataStructureName, HashMap<String, String> settings) {
        verifyInput(dataStructureName, settings);
        dataStructureName = dataStructureName.toLowerCase();
        if(dataStructureName.contains("indicator"))
            return buildIndicatorRequest(dataStructureName, settings);

        throw new NoSuchDataStructureException();
    }

    private Request<?> buildIndicatorRequest(String dataStructureName, HashMap<String, String> settings) {
        Request<Indicator> request = new Request<>();
        if(dataStructureName.contains("rsi")){
            request.dataStructure = new RSIBuilder().build(settings);
        } else if(dataStructureName.contains("sma")){
            request.dataStructure = new MovingAverageBuilder().build(settings);
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
