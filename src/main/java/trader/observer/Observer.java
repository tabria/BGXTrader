package trader.observer;

import trader.entity.price.Price;

public interface Observer {

    void updateObserver(Price price);
}
