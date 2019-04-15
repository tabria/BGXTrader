package trader.broker.connector.oanda.transformer;

import com.oanda.v20.order.Order;
import com.oanda.v20.order.OrderID;
import com.oanda.v20.order.OrderType;
import com.oanda.v20.order.StopLossOrder;
import com.oanda.v20.trade.TradeSummary;
import trader.entity.trade.BrokerTradeDetails;
import trader.entity.trade.BrokerTradeDetailsImpl;
import trader.exception.NullArgumentException;

import java.util.List;

public class OandaTradeSummaryTransformer {

    private static final String DEFAULT_STOPLOSS_PRICE = "0";

    public <T> BrokerTradeDetails transformTradeSummary(T tradeSummary, List<Order> orders) {
        if(tradeSummary == null || orders == null)
            throw new NullArgumentException();
        TradeSummary summary = (TradeSummary) tradeSummary;

        BrokerTradeDetails tradeDetails = new BrokerTradeDetailsImpl();
        tradeDetails.setTradeID(summary.getId().toString());
        tradeDetails.setOpenPrice(summary.getPrice().toString());
        tradeDetails.setStopLossOrderID(summary.getStopLossOrderID().toString());
        tradeDetails.setInitialUnits(summary.getInitialUnits().toString());
        tradeDetails.setCurrentUnits(summary.getCurrentUnits().toString());
        tradeDetails.setStopLossPrice(getStopLossOrderPrice(orders, summary.getStopLossOrderID()));
        return tradeDetails;
    }

    private String getStopLossOrderPrice(List<Order> orders, OrderID stopLossOrderID) {
        for (Order order : orders) {
            OrderID id = order.getId();
            if (id.toString().equals(stopLossOrderID.toString()) &&
                    order.getType().equals(OrderType.STOP_LOSS))
                return ((StopLossOrder)order).getPrice().toString();
        }
        return DEFAULT_STOPLOSS_PRICE;
    }
}
