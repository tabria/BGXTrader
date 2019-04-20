package trader.interactor.createbrokerconnector;
import trader.broker.connector.BrokerConnector;
import trader.exception.NoSuchConnectorException;
import trader.interactor.ResponseImpl;
import trader.interactor.createbrokerconnector.enums.Constants;
import trader.presenter.Presenter;
import trader.requestor.Request;
import trader.requestor.UseCase;
import trader.responder.Response;

import java.util.Map;

public class CreateBrokerConnectorUseCase implements UseCase {
    private Presenter presenter;

    public CreateBrokerConnectorUseCase(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public <T, E> Response<E> execute(Request<T> request) {
        Map<String, Object> settings = (Map<String, Object>) request.getBody();
        BrokerConnector brokerConnector = setConfigurations(settings);
        Response<E> response = setResponse((E) brokerConnector);
        presenter.execute(response);
        return response;
    }

    private <E> Response<E> setResponse(E brokerConnector) {
        Response<E> response = new ResponseImpl<>();
        response.setBody(brokerConnector);
        return response;
    }

    private BrokerConnector setConfigurations(Map<String, Object> inputSettings) {
        Map<String, String> settings = (Map<String, String>) inputSettings.get("settings");
        if(isNotConnector(settings))
            throw new NoSuchConnectorException();
        BrokerConnector  brokerConnector = BrokerConnector.create(settings.get("brokerName"));
        brokerConnector.setUrl(settings.get(Constants.URL.toString()));
        brokerConnector.setToken(settings.get(Constants.TOKEN.toString()));
        brokerConnector.setAccountID(settings.get(Constants.ID.toString()));
        brokerConnector.setLeverage(settings.get(Constants.LEVERAGE.toString()));

        return brokerConnector;
    }

    private boolean isNotConnector(Map<String, String> settings) {
        return !settings.containsKey(Constants.BROKER_NAME.toString()) ||
                !settings.containsKey(Constants.URL.toString()) ||
                !settings.containsKey(Constants.TOKEN.toString()) ||
                !settings.containsKey(Constants.ID.toString()) ||
                !settings.containsKey(Constants.LEVERAGE.toString());
    }
}
