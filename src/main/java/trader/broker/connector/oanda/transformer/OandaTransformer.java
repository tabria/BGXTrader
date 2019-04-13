package trader.broker.connector.oanda.transformer;


import trader.broker.connector.Transformable;
import trader.entity.candlestick.Candlestick;
import trader.entity.order.Order;
import trader.entity.price.Price;
import trader.entity.trade.BrokerTradeDetails;
import trader.responder.Response;

import java.util.List;

public class OandaTransformer implements Transformable {

    private OandaPriceTransformer oandaPriceTransformer;
    private OandaCandleTransformer oandaCandlesTransformer;
    private OandaOrderTransformer oandaOrderTransformer;
    private OandaTradeSummaryTransformer oandaTradeSummaryTransformer;

    public OandaTransformer() {
        oandaPriceTransformer = new OandaPriceTransformer();
        oandaCandlesTransformer = new OandaCandleTransformer();
        oandaOrderTransformer = new OandaOrderTransformer();
        oandaTradeSummaryTransformer = new OandaTradeSummaryTransformer();
    }

    @Override
    public <T> BrokerTradeDetails transformTradeSummary(T tradeSummary, List<com.oanda.v20.order.Order> orders){
       return oandaTradeSummaryTransformer.transformTradeSummary(tradeSummary, orders);
    };

    @Override
    public <T> Order transformOrder(T order){
        return oandaOrderTransformer.transformOrder(order);
    };

    @Override
    public <T> List<Candlestick> transformCandlesticks(Response<T> response){
        return  transformCandlesticks(response);
    };

    @Override
    public <T> Price transformToPrice(Response<T> response){
        return oandaPriceTransformer.transformToPrice(response);
    };

}
