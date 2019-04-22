package trader.entry;

import trader.controller.TraderController;
import trader.entity.indicator.Indicator;
import trader.entity.trade.Trade;

import java.util.List;

public interface EntryStrategy {


    void setCreateTradeController(TraderController<Trade> createTradeController);

    Trade generateTrade();

    void setIndicators(List<Indicator> indicators);
}
