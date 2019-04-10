package trader.interactor;

import org.yaml.snakeyaml.Yaml;
import trader.broker.connector.BrokerConnector;
import trader.exception.BadRequestException;
import trader.exception.EmptyArgumentException;
import trader.exception.NullArgumentException;
import trader.requestor.Request;
import trader.requestor.UseCase;
import trader.responder.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class AddBrokerConnectorUseCase extends BaseUseCase {

    private static final String URL = "url";
    private static final String TOKEN = "token";
    private static final String ID = "id";
    private static final String LEVERAGE = "leverage";

    @Override
    public <T, E> Response<E> execute(Request<T> request) {
        if(request == null)
            throw new NullArgumentException();
        BrokerConnector brokerConnector = (BrokerConnector) request.getRequestDataStructure();
        setConfigurations(brokerConnector);
        return setResponse((E) brokerConnector);
    }

    private <E> Response<E> setResponse(E brokerConnector) {
        Response<E> response = new ResponseImpl<>();
        response.setResponseDataStructure(brokerConnector);
        return response;
    }

    private void setConfigurations(BrokerConnector brokerConnector) {
        String location = brokerConnector.getFileLocation();
        Yaml yaml = new Yaml();
        try(InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(location)){
            HashMap<String, String> brokerSettings = yaml.load(is);
            if(brokerSettings.containsKey(URL)){
                brokerConnector.setUrl(brokerSettings.get(URL));
            }
            if(brokerSettings.containsKey(TOKEN)){
                brokerConnector.setToken(brokerSettings.get(TOKEN));
            }
            if(brokerSettings.containsKey(ID)){
                brokerConnector.setAccountID(brokerSettings.get(ID));
            }
            if(brokerSettings.containsKey(LEVERAGE)){
                brokerConnector.setLeverage(brokerSettings.get(LEVERAGE));
            }
        } catch (IOException | RuntimeException e) {
            BadRequestException badRequestException = new BadRequestException();
            badRequestException.initCause(e);
            throw badRequestException;
        }
    }
}
