package trader.broker.connector;

import trader.price.Price;

public interface PriceTransformable {

    <T> Price transformToPrice(T response);
}
