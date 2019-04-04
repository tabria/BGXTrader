package trader.interactor;

import org.yaml.snakeyaml.Yaml;
import trader.connector.BrokerConfiguration;
import trader.exception.BadRequestException;
import trader.exception.NullArgumentException;
import trader.requestor.Request;
import trader.responder.Response;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class AddBrokerConfigurationUseCase implements UseCase {


    private static final String URL = "url";
    private static final String TOKEN = "token";
    private static final String ID = "id";

    @Override
    public <T, E> Response<E> execute(Request<T> request) {
        if(request == null)
            throw new NullArgumentException();
        BrokerConfiguration brokerConfiguration = (BrokerConfiguration) request.getRequestDataStructure();
        setConfigurations(brokerConfiguration);
        return setResponse((E) brokerConfiguration);
    }

    private <E> Response<E> setResponse(E brokerConfiguration) {
        Response<E> response = new ResponseImpl<>();
        response.setResponseDataStructure(brokerConfiguration);
        return response;
    }

    private void setConfigurations(BrokerConfiguration brokerConfiguration) {
        String location = brokerConfiguration.getFileLocation();
        Yaml yaml = new Yaml();
        try(InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(location)){
            HashMap<String, String> brokerSettings = yaml.load(is);
            if(brokerSettings.containsKey(URL)){
                brokerConfiguration.setUrl(brokerSettings.get(URL));
            }
            if(brokerSettings.containsKey(TOKEN)){
                brokerConfiguration.setToken(brokerSettings.get(TOKEN));
            }
            if(brokerSettings.containsKey(ID)){
                brokerConfiguration.setAccountID(brokerSettings.get(ID));
            }
        } catch (IOException | RuntimeException e) {
            BadRequestException badRequestException = new BadRequestException();
            badRequestException.initCause(e);
            throw badRequestException;
        }
    }
}
