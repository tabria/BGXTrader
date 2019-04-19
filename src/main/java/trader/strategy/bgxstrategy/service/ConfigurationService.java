package trader.strategy.bgxstrategy.service;

import org.yaml.snakeyaml.Yaml;
import trader.configuration.TradingStrategyConfiguration;
import trader.controller.CreateBGXConfigurationController;
import trader.controller.TraderController;
import trader.exception.BadRequestException;
import trader.presenter.Presenter;
import trader.requestor.UseCaseFactory;
import trader.responder.Response;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ConfigurationService {

    private UseCaseFactory useCaseFactory;
    private Presenter presenter;

    public ConfigurationService(UseCaseFactory useCaseFactory, Presenter presenter) {
        this.useCaseFactory = useCaseFactory;
        this.presenter = presenter;
    }

    public TradingStrategyConfiguration createConfiguration(String configurationFileName) {
        Yaml yaml = new Yaml();
        Map<String, Map<String, String>> bgxSettings;
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(configurationFileName)) {
            bgxSettings = yaml.load(is);
        } catch (IOException | RuntimeException e) {
            throw new BadRequestException();
        }
        Map<String, Object> settings = new HashMap<>();
        settings.put("settings", bgxSettings);
        TraderController<TradingStrategyConfiguration> controller = new CreateBGXConfigurationController<>(useCaseFactory, presenter);
        Response<TradingStrategyConfiguration> configurationResponse = controller.execute(settings);
        return configurationResponse.getBody();
    }
}