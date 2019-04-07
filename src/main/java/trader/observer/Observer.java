package trader.observer;

import trader.price.Price;

public interface Observer {

    void updateObserver(Price price);
}
