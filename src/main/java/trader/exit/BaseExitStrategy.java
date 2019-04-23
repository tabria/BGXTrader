package trader.exit;

import trader.broker.BrokerGateway;
import trader.strategy.TradingStrategyConfiguration;
import trader.exception.NullArgumentException;
import trader.exit.service.UpdateCandlesService;
import trader.presenter.Presenter;

import java.math.BigDecimal;

public abstract class BaseExitStrategy implements ExitStrategy {
    protected UpdateCandlesService updateCandlesService;
    protected TradingStrategyConfiguration configuration;
    protected BrokerGateway brokerGateway;
    protected Presenter presenter;

    public BaseExitStrategy() {
        updateCandlesService = new UpdateCandlesService();
    }

    @Override
    public void setConfiguration(TradingStrategyConfiguration configuration) {
        if(configuration == null)
            throw new NullArgumentException();
        this.configuration = configuration;
    }

    @Override
    public void setBrokerGateway(BrokerGateway brokerGateway) {
        if(brokerGateway == null)
            throw new NullArgumentException();
        this.brokerGateway = brokerGateway;
    }

    @Override
    public  void setPresenter(Presenter presenter){
        if(presenter == null)
            throw new NullArgumentException();
        this.presenter = presenter;
    }

    protected boolean isAbove(BigDecimal priceA, BigDecimal priceB) {
        return priceA.compareTo(priceB) >= 0;
    }

    protected boolean isBelow(BigDecimal priceA, BigDecimal priceB) {
        return priceA.compareTo(priceB) <= 0;
    }

    protected boolean isShortTrade(BigDecimal currentUnits) {
        return currentUnits.compareTo(BigDecimal.ZERO) < 0;
    }

    protected BigDecimal subtract(BigDecimal NumberA, BigDecimal NumberB) {
        return NumberA.subtract(NumberB)
                .setScale(5, BigDecimal.ROUND_HALF_UP);
    }

    protected BigDecimal add(BigDecimal NumberA, BigDecimal NumberB) {
        return NumberA.add(NumberB)
                .setScale(5, BigDecimal.ROUND_HALF_UP);
    }


}
