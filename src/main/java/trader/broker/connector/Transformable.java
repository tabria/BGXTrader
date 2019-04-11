package trader.broker.connector;

import trader.entity.candlestick.Candlestick;
import trader.entity.order.Order;
import trader.price.Price;
import trader.responder.Response;

import java.util.List;

public interface Transformable {

    interface OrderTransformable{
        <T> Order transformOrder(T order);
    }

    interface CandleTransformable{
        <T> List<Candlestick> transformCandlesticks(Response<T> response);
    }

    interface PriceTransformable {
        <T> Price transformToPrice(Response<T> response);
    }

}
