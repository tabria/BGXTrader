package trader;

import trader.strategy.bgxstrategy.BGXStrategyMain;
import trader.strategy.Strategy;


/**
 * Moving Average CrossOver Trading Robot
 * <p> This is a trading robot based on BunnyGirl Bunny Cross(BGX). The application is using Oanda's v20 Java Wrapper API
 *
 * @see <a href="https://forexforays.com/BGX_Original.pdf"</a>
 *
 * The application can use Simple, Exponential and Weighted moving averages, and Relative Strength Index. For daily open is used SMA with period of 1. For PriceImpl is used also SMA with period of 1, applied on Close. The application can trade only on EUR accounts and only EUR/USD pair. Also application will open only one trade and if there is an open trade no other trade will be generated until current trade is closed.
 *
 * Entries: The signals are generated from crosses between WMA(5) and WMA(20), priceSMA(1) and WMA(20), priceSMA(1) and WMA(100). All trade are in the direction of WMA(100). Entry signal is 25 pips away from the cross. Stop is on the cross. Entries are executed with Market If Touched Order. If there is an unfilled order and price move more than 5 pips below(for longs)/above(for shorts) cross point, then the waiting order will be canceled.
 *
 * There are 2 exit strategies
 *
 * </p>
 *
 */

public class Main {



    public static void main(String[] args) {

        Strategy strategy = new BGXStrategyMain("Oanda", "bgxStrategyConfig.yaml", "oandaBrokerConfig.yaml");
        ThreadedStrategy threadedStrategy = new ThreadedStrategy(strategy);
    }
}
