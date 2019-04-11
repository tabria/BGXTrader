package trader.broker.connector.oanda;

import com.oanda.v20.order.MarketIfTouchedOrder;
import trader.broker.connector.Transformable;
import trader.entity.order.Order;
import trader.entity.order.OrderImpl;
import trader.entity.order.enums.OrderType;

import java.math.BigDecimal;

public class OandaOrderTransformer implements Transformable.OrderTransformable {

    @Override
    public <T> Order transformOrder(T order) {

        if(order != null && order.getClass() == MarketIfTouchedOrder.class)
            return transformMarketIfTouchedOrderToOrder((MarketIfTouchedOrder) order);
        return null;
    }

    private <T> Order transformMarketIfTouchedOrderToOrder(MarketIfTouchedOrder order) {
        String orderID = order.getId().toString();
        String orderType = order.getType().toString();
        String orderInstrument = order.getInstrument().toString();
        BigDecimal orderUnits = order.getUnits().bigDecimalValue();
        BigDecimal orderStopLossPrice = order.getStopLossOnFill().getPrice().bigDecimalValue();

        return new OrderImpl(orderID, OrderType.valueOf(orderType), orderInstrument, orderUnits, orderStopLossPrice);
    }
}
