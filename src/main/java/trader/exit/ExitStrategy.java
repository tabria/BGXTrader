package trader.exit;

import trader.broker.BrokerGateway;
import trader.strategy.TradingStrategyConfiguration;
import trader.entity.price.Price;
import trader.presenter.Presenter;


public interface ExitStrategy {

    void execute(Price price);

    void setConfiguration(TradingStrategyConfiguration configuration);

    void setBrokerGateway(BrokerGateway brokerGateway);

    void setPresenter(Presenter presenter);
}
