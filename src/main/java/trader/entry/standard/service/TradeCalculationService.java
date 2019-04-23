package trader.entry.standard.service;

import trader.strategy.TradingStrategyConfiguration;
import trader.entity.trade.point.Point;
import trader.entity.trade.Direction;
import trader.exception.NullArgumentException;

import java.math.BigDecimal;

public class TradeCalculationService {

    private TradingStrategyConfiguration configuration;


    public TradeCalculationService() {
    }

    public BigDecimal calculateEntryPrice(Point intersectionPoint, Direction direction) {
        if (intersectionPoint == null || direction == null)
            throw new NullArgumentException();
        if (direction.equals(Direction.DOWN))
            return intersectionPoint.getPrice()
                    .subtract(configuration.getEntryFilter())
                    .setScale(5, BigDecimal.ROUND_HALF_UP);
        else
            return intersectionPoint.getPrice()
                    .add(configuration.getEntryFilter())
                    .add(configuration.getSpread())
                    .setScale(5, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal calculateStopLossPrice(Point intersectionPoint, Direction direction) {
        if (intersectionPoint == null || direction == null)
            throw new NullArgumentException();
        if (direction.equals(Direction.DOWN))
            return intersectionPoint.getPrice()
                    .add(configuration.getSpread())
                    .add(configuration.getStopLossFilter())
                    .setScale(5, BigDecimal.ROUND_HALF_UP);
        else
            return intersectionPoint.getPrice()
                    .subtract(configuration.getStopLossFilter())
                    .setScale(5, BigDecimal.ROUND_HALF_UP);
    }

    public boolean setTradable(Point intersectionPoint, Direction direction, BigDecimal dailyOpenPrice, BigDecimal entryPrice) {
        if (intersectionPoint == null || direction == null ||
                dailyOpenPrice == null || entryPrice == null)
            throw new NullArgumentException();
        if (direction.equals(Direction.DOWN))
            return getTradableForDownDirection(dailyOpenPrice, entryPrice, intersectionPoint.getPrice());
        else if (direction.equals(Direction.UP))
            return getTradableForUpDirection(dailyOpenPrice, entryPrice, intersectionPoint.getPrice());
        return false;
    }

    public void setConfiguration(TradingStrategyConfiguration configuration){
        this.configuration = configuration;
    }

    private boolean getTradableForUpDirection(BigDecimal dailyOpenPrice, BigDecimal entryPrice, BigDecimal intersectionPrice) {
        if (dailyOpenPriceAboveAll(dailyOpenPrice, entryPrice, intersectionPrice))
            return isTradable(entryPrice, dailyOpenPrice);
        else
            return (intersectionPrice.compareTo(dailyOpenPrice) <= 0 && entryPrice.compareTo(dailyOpenPrice) >= 0) ||
                    (dailyOpenPriceBelowAll(dailyOpenPrice, entryPrice, intersectionPrice));
    }

    private boolean getTradableForDownDirection(BigDecimal dailyOpenPrice, BigDecimal entryPrice, BigDecimal intersectionPrice) {
        if (dailyOpenPriceBelowAll(dailyOpenPrice, entryPrice, intersectionPrice))
            return isTradable(dailyOpenPrice, entryPrice);
        else
            return (intersectionPrice.compareTo(dailyOpenPrice) >= 0 && entryPrice.compareTo(dailyOpenPrice) <= 0)
                    || dailyOpenPriceAboveAll(dailyOpenPrice, entryPrice, intersectionPrice);
    }

    private boolean dailyOpenPriceBelowAll(BigDecimal dailyOpenPrice, BigDecimal entryPrice, BigDecimal intersectionPrice) {
        return intersectionPrice.compareTo(dailyOpenPrice) > 0 && entryPrice.compareTo(dailyOpenPrice) > 0;
    }

    private boolean dailyOpenPriceAboveAll(BigDecimal dailyOpenPrice, BigDecimal entryPrice, BigDecimal intersectionPrice) {
        return intersectionPrice.compareTo(dailyOpenPrice) < 0 && entryPrice.compareTo(dailyOpenPrice) < 0;
    }

    private boolean isTradable(BigDecimal dailyOpenPrice, BigDecimal entryPrice) {
        BigDecimal delta = entryPrice
                .subtract(dailyOpenPrice)
                .setScale(5, BigDecimal.ROUND_HALF_UP);
        return delta.compareTo(configuration.getTarget()) >= 0;
    }
}