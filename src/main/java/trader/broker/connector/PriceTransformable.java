package trader.broker.connector;


import trader.price.Price;
import trader.responder.Response;

public interface PriceTransformable {

    <T> Price transformToPrice(Response<T> response);

}
