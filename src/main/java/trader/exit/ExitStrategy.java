package trader.exit;

import com.oanda.v20.account.Account;
import com.oanda.v20.primitives.DateTime;
import trader.broker.BrokerGateway;
import trader.configuration.TradingStrategyConfiguration;
import trader.entity.price.Price;
import trader.presenter.Presenter;

import java.math.BigDecimal;

public interface ExitStrategy {

    void execute(Price price);

    void setConfiguration(TradingStrategyConfiguration configuration);

    void setBrokerGateway(BrokerGateway brokerGateway);

    void setPresenter(Presenter presenter);
}
