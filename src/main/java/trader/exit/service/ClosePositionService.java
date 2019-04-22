package trader.exit.service;

import trader.broker.BrokerGateway;
import trader.configuration.TradingStrategyConfiguration;
import trader.entity.trade.BrokerTradeDetails;

import java.math.BigDecimal;
import java.util.HashMap;

public class ClosePositionService {

    private static final String MARKET_ORDER = "marketOrder";
    private static final String INSTRUMENT = "instrument";
    private static final String UNITS_SIZE = "unitsSize";

    public void closePosition(BrokerTradeDetails tradeDetails, BrokerGateway brokerGateway, TradingStrategyConfiguration configuration, BigDecimal partsToClose){

        BigDecimal currentUnits = tradeDetails.getCurrentUnits();
        if (partsToClose == null || partsToClose.compareTo(BigDecimal.ONE)< 0)
            throw new IllegalArgumentException();
        brokerGateway.placeOrder(createCloseSettings(currentUnits, configuration, partsToClose), MARKET_ORDER);
    }

    private HashMap<String, String> createCloseSettings(BigDecimal currentUnits, TradingStrategyConfiguration configuration, BigDecimal partsToClose) {
        HashMap<String, String> settings = new HashMap<>();
        settings.put(INSTRUMENT, configuration.getInstrument());
        settings.put(UNITS_SIZE, reverseUnitsSizeSign(currentUnits, partsToClose).toString());
        return settings;
    }

    //multiply with -1 to reverse units size. This will open trade with opposite direction to the current trade
    private BigDecimal reverseUnitsSizeSign(BigDecimal currentUnits, BigDecimal partsToClose) {
        return currentUnits
                .divide(partsToClose, 0, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(-1)).setScale(0, BigDecimal.ROUND_HALF_UP);
    }
}
