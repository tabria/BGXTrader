package trader.exit.service;

import trader.broker.BrokerGateway;
import trader.configuration.TradingStrategyConfiguration;
import trader.entity.trade.BrokerTradeDetails;

import java.math.BigDecimal;
import java.util.HashMap;

public class ClosePositionService {

    public void closePosition(BrokerTradeDetails tradeDetails, BrokerGateway brokerGateway, TradingStrategyConfiguration configuration, BigDecimal partsToClose){

        BigDecimal currentUnits = tradeDetails.getCurrentUnits();


//        if (initialUnits.compareTo(currentUnits) != 0)
//            return;

//        BigDecimal firstTargetPrice = getFirstTarget(tradeDetails, FIRST_TARGET_DISTANCE);

//        if(isAbleToSetStopLoss(currentUnits, firstTargetPrice, price)){
            if (partsToClose == null || partsToClose.compareTo(BigDecimal.ONE)< 0)
                throw new IllegalArgumentException();
            brokerGateway.placeMarketOrder(createCloseSettings(currentUnits, configuration, partsToClose));
    }

    private HashMap<String, String> createCloseSettings(BigDecimal currentUnits, TradingStrategyConfiguration configuration, BigDecimal partsToClose) {
        HashMap<String, String> settings = new HashMap<>();
        settings.put("instrument", configuration.getInstrument());
        settings.put("unitsSize", reverseUnitsSizeSign(currentUnits, partsToClose).toString());
        return settings;
    }

    //multiply with -1 to reverse units size. This will open trade with opposite direction to the current trade
    private BigDecimal reverseUnitsSizeSign(BigDecimal currentUnits, BigDecimal partsToClose) {
        return currentUnits
                .divide(partsToClose, 0, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(-1)).setScale(0, BigDecimal.ROUND_HALF_UP);
    }
}
