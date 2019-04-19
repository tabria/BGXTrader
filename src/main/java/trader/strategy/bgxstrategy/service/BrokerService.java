package trader.strategy.bgxstrategy.service;

import org.yaml.snakeyaml.Yaml;
import trader.broker.BrokerGateway;
import trader.broker.connector.BaseGateway;
import trader.broker.connector.BrokerConnector;
import trader.controller.CreateBrokerConnectorController;
import trader.controller.TraderController;
import trader.exception.BadRequestException;
import trader.presenter.Presenter;
import trader.requestor.UseCaseFactory;
import trader.responder.Response;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class BrokerService {

    private UseCaseFactory useCaseFactory;
    private Presenter presenter;

    public BrokerService(UseCaseFactory useCaseFactory, Presenter presenter) {
        this.useCaseFactory = useCaseFactory;
        this.presenter = presenter;
    }

    public BrokerGateway createBrokerGateway(String brokerName, String brokerConfigurationFileName) {
        Yaml yaml = new Yaml();
        Map<String, String> brokerSettings;
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(brokerConfigurationFileName)) {
            brokerSettings = yaml.load(is);
        } catch (IOException | RuntimeException e) {
            throw new BadRequestException();
        }
        brokerSettings.put("brokerName", brokerName);
        Map<String, Object> settings = new HashMap<>();
        settings.put("settings", brokerSettings);
        TraderController<BrokerConnector> controller = new CreateBrokerConnectorController<>(useCaseFactory, presenter);
        Response<BrokerConnector> brokerResponse = controller.execute(settings);
        BrokerConnector connector = brokerResponse.getBody();
        return BaseGateway.create(brokerName, connector);
    }
}
