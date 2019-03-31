package trader.strategy;

import trader.price.Pricing;

public interface PriceConnector {

    Pricing getPrice();

}
