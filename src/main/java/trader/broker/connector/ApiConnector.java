package trader.broker.connector;

import trader.entity.indicator.CandlesUpdaterConnector;
import trader.strategy.PriceConnector;
import trader.entity.trade.TradeImpl;

import java.util.List;

public interface ApiConnector extends CandlesUpdaterConnector, PriceConnector {

//    static ApiConnector create(String apiName) {
//        return BaseGateway.create(apiName);
//    }


    List<TradeImpl> getOpenTrades();
}
