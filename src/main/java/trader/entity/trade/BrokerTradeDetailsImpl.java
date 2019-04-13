package trader.entity.trade;

import trader.exception.EmptyArgumentException;
import trader.exception.NegativeNumberException;
import trader.exception.NullArgumentException;

import java.math.BigDecimal;

public class BrokerTradeDetailsImpl implements BrokerTradeDetails {

    private static final BigDecimal DEFAULT_VALUE = BigDecimal.valueOf(0.0001);
    private static final String DEFAULT_ID = "0";

    ////getStopLossOrderID
////getPrice
////getCurrentUnits
////tradeID

    private String tradeID;
    private String stopLossOrderID;
    private BigDecimal openPrice;
    private BigDecimal stopLossPrice;
    private BigDecimal currentUnits;

    public BrokerTradeDetailsImpl() {
        tradeID = DEFAULT_ID;
        openPrice = DEFAULT_VALUE;
        stopLossPrice = DEFAULT_VALUE;
        currentUnits = DEFAULT_VALUE;
    }

    @Override
    public String getTradeID() {
        return tradeID;
    }

    @Override
    public void setTradeID(String tradeID) {
        validateInput(tradeID);
        this.tradeID = tradeID.trim();
    }

    @Override
    public String getStopLossOrderID() {
        return stopLossOrderID;
    }

    @Override
    public void setStopLossOrderID(String stopLossOrderID) {
        validateInput(stopLossOrderID);
        this.stopLossOrderID = stopLossOrderID.trim();
    }

    @Override
    public BigDecimal getOpenPrice() {
        return openPrice;
    }

    @Override
    public void setOpenPrice(String openPrice) {
        validateInput(openPrice);
        checkForNegativeNumbers(openPrice.trim());
        this.openPrice = new BigDecimal(openPrice.trim());
    }

    @Override
    public BigDecimal getStopLossPrice() {
        return stopLossPrice;
    }

    @Override
    public void setStopLossPrice(String stopLossPrice) {
        validateInput(stopLossPrice);
        checkForNegativeNumbers(stopLossPrice.trim());
        this.stopLossPrice = new BigDecimal(stopLossPrice.trim());
    }

    @Override
    public BigDecimal getCurrentUnits() {
        return currentUnits;
    }

    @Override
    public void setCurrentUnits(String currentUnits) {
        validateInput(currentUnits);
        this.currentUnits = new BigDecimal(currentUnits.trim());
    }

    private void validateInput(String inputStr) {
        if(inputStr == null)
            throw new NullArgumentException();
        if(inputStr.trim().isEmpty())
            throw new EmptyArgumentException();
    }

    private void checkForNegativeNumbers(String entryPrice) {
        BigDecimal price = new BigDecimal(entryPrice);
        if(price.compareTo(BigDecimal.ZERO)<0)
            throw new NegativeNumberException();
    }
}
