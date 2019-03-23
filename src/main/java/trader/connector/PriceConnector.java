package trader.connector;

import trader.price.Pricing;

public interface PriceConnector {

    Pricing getPrice();

}
