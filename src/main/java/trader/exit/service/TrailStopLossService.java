package trader.exit.service;

import trader.broker.BrokerGateway;
import trader.entity.candlestick.Candlestick;
import trader.entity.trade.BrokerTradeDetails;

import java.math.BigDecimal;

public class TrailStopLossService {

    private BigDecimal exitBarHigh;
    private BigDecimal exitBarLow;
    private String stopLossPrice;



    public boolean trailStopLoss(BrokerTradeDetails tradeDetails, Candlestick candlestick, BrokerGateway brokerGateway){
        setExitBarComponents(tradeDetails, candlestick);
        if (isReadyToSendTrailOrder(brokerGateway, tradeDetails) && isReadyToTrailStopLoss(tradeDetails, candlestick)){
            BigDecimal newStopLossPrice = getNewStopLossPrice(tradeDetails);
            brokerGateway.setTradeStopLossPrice(tradeDetails.getTradeID(), newStopLossPrice.toString());
            stopLossPrice = newStopLossPrice.toString();
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("Stop loss trailed @ %s", stopLossPrice);
    }

    void setExitBarComponents(BrokerTradeDetails tradeDetails, Candlestick candlestick) {
        if(this.exitBarHigh == null || this.exitBarLow == null){
            BigDecimal tradeOpenPrice = tradeDetails.getOpenPrice();
            this.exitBarHigh = tradeOpenPrice;
            this.exitBarLow = tradeOpenPrice;
        } else{
            this.exitBarLow = updateExitBarComponent(this.exitBarLow, candlestick.getLowPrice(), tradeDetails);
            this.exitBarHigh = updateExitBarComponent(this.exitBarHigh, candlestick.getHighPrice(), tradeDetails);
        }
    }

    boolean isReadyToSendTrailOrder(BrokerGateway brokerGateway, BrokerTradeDetails tradeDetails){

        BigDecimal stopLossPrice = brokerGateway.getTradeStopLossPrice(tradeDetails.getTradeID());

        boolean shortCondition = (isShort(tradeDetails.getCurrentUnits())) &&
                (stopLossPrice.compareTo(BigDecimal.ZERO) == 0 || stopLossPrice.compareTo(this.exitBarHigh) > 0);
        boolean longCondition = (isLong(tradeDetails.getCurrentUnits())) &&
                (stopLossPrice.compareTo(BigDecimal.ZERO) == 0 || stopLossPrice.compareTo(this.exitBarLow) < 0);
        return shortCondition || longCondition;
    }

    boolean isReadyToTrailStopLoss(BrokerTradeDetails tradeDetails,  Candlestick candlestick){
        return isShortTailable(tradeDetails, candlestick) || isLongTailable(tradeDetails, candlestick);
    }

    BigDecimal getNewStopLossPrice(BrokerTradeDetails tradeDetails){
        if (isShort(tradeDetails.getCurrentUnits()))
            return this.exitBarHigh;
        return this.exitBarLow;
    }

    BigDecimal getExitBarHigh() {
        return exitBarHigh;
    }

    BigDecimal getExitBarLow() {
        return exitBarLow;
    }

    private boolean isLong(BigDecimal currentUnits) {
        return currentUnits.compareTo(BigDecimal.ZERO) > 0;
    }

    private boolean isShort(BigDecimal currentUnits) {
        return currentUnits.compareTo(BigDecimal.ZERO) < 0;
    }

    private boolean isShortTailable(BrokerTradeDetails tradeDetails, Candlestick candlestick) {
        return isShort(tradeDetails.getCurrentUnits()) &&
                    candlestick.getClosePrice().compareTo(this.exitBarLow) < 0 &&
                    candlestick.getHighPrice().compareTo(this.exitBarHigh) < 0;
    }

    private boolean isLongTailable(BrokerTradeDetails tradeDetails, Candlestick candlestick) {
        return isLong(tradeDetails.getCurrentUnits()) &&
                candlestick.getClosePrice().compareTo(this.exitBarHigh) > 0 &&
                candlestick.getLowPrice().compareTo(this.exitBarLow) > 0;
    }

    private BigDecimal updateExitBarComponent(BigDecimal exitBarComponent, BigDecimal candlePrice, BrokerTradeDetails tradeDetails){
        if(isShort(tradeDetails.getCurrentUnits()))
            return candlePrice.compareTo(exitBarComponent) < 0 ? candlePrice : exitBarComponent;
         else
            return candlePrice.compareTo(exitBarComponent) > 0 ? candlePrice : exitBarComponent;
    }
}
