package trader.entry;

import trader.controller.TraderController;
import trader.entity.indicator.Indicator;
import trader.entity.trade.Trade;
import trader.strategy.TradingStrategyConfiguration;

import java.util.List;

public interface EntryStrategy {


    void setConfiguration(TradingStrategyConfiguration configuration);

    void setCreateTradeController(TraderController<Trade> createTradeController);

    Trade generateTrade();

    void setIndicators(List<Indicator> indicators);
}
