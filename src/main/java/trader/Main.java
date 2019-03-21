package trader;


import com.oanda.v20.Context;
import com.oanda.v20.ContextBuilder;
import trader.config.Config;
import trader.connector.ApiConnector;
import trader.core.Observable;
import trader.core.Observer;
import trader.indicator.Indicator;
import trader.indicator.IndicatorObserver;
import trader.candle.CandlestickPriceType;
import trader.indicator.ma.MovingAverageBuilder;
import trader.indicator.ma.enums.MAType;
import trader.indicator.rsi.RSIBuilder;
import trader.price.PriceObservable;
import trader.price.PricePull;
import trader.trade.PositionManager;
import trader.strategy.BGXStrategy.BGXTradeGenerator;
import trader.trade.service.NewTradeService;
import trader.trade.service.OrderService;
import trader.trade.service.exit_strategie.ExitStrategy;
import trader.trade.service.exit_strategie.FullCloseStrategy;


/**
 * Moving Average CrossOver Trading Robot
 * <p> This is a trading robot based on BunnyGirl Bunny Cross(BGX). The application is using Oanda's v20 Java Wrapper API
 *
 * @see <a href="https://forexforays.com/BGX_Original.pdf"</a>
 *
 * The application can use Simple, Exponential and Weighted moving averages, and Relative Strength Index. For daily open is used SMA with period of 1. For Price is used also SMA with period of 1, applied on Close. The application can trade only on EUR accounts and only EUR/USD pair. Also application will open only one trade and if there is an open trade no other trade will be generated until current trade is closed.
 * @see Config
 *
 * Entries: The signals are generated from crosses between WMA(5) and WMA(20), priceSMA(1) and WMA(20), priceSMA(1) and WMA(100). All trade are in the direction of WMA(100). Entry signal is 25 pips away from the cross. Stop is on the cross. Entries are executed with Market If Touched Order. If there is an unfilled order and price move more than 5 pips below(for longs)/above(for shorts) cross point, then the waiting order will be canceled.
 *
 * There are 2 strategy for exiting:
 *  First: When price hit +32 pips, half of the position will be liquidated and stop will be set to break even. Then the stop will be trailed after each bar's extreme.
 *  Second: When price hit +32 pips, stop loss is moved to +16 pips for the hole trade and then will be trailed after significant extremes. For example for long, when price make high, then low and then higher high than the first one, the significant extreme is the low and the stop will be placed there. For shorts is reversed.
 *
 * </p>
 *
 */

//TODO log exception, remove active orders if price is equal or beyond break even point

public class Main {
    
    public static void main(String[] args)  {


        ApiConnector apiConnector = ApiConnector.create("Oanda");




        Context context = new ContextBuilder(Config.URL)
                .setToken(Config.TOKEN)
                .setApplication("Context")
                .build();

//Start transaction for bgxtrader
//        TransactionSinceResponse since = context.transaction.since(Config.ACCOUNTID, new TransactionID("156"));

 //       validateAccount(context);


        //simple ma with period of 1 representing the price
        Indicator smaPrice = new MovingAverageBuilder(apiConnector)
                .setPeriod(1)
                .setCandlestickPriceType(CandlestickPriceType.CLOSE)
//                .setCandleTimeFrame(Config.TIME_FRAME)
                .setMAType(MAType.SIMPLE)
                .build();

        Indicator dailyPrice = new MovingAverageBuilder(apiConnector)
                .setPeriod(1)
                .setCandlestickPriceType(CandlestickPriceType.OPEN)
 //               .setCandleTimeFrame(CandleGranularity.D)
                .setMAType(MAType.SIMPLE)
                .build();

        Indicator wmaFast = new MovingAverageBuilder(apiConnector)
                .setPeriod(5)
                .setCandlestickPriceType(CandlestickPriceType.CLOSE)
 //               .setCandleTimeFrame(Config.TIME_FRAME)
                .setMAType(MAType.WEIGHTED)
                .build();

        Indicator wmaMiddle = new MovingAverageBuilder(apiConnector)
                .setPeriod(20)
                .setCandlestickPriceType(CandlestickPriceType.CLOSE)
 //               .setCandleTimeFrame(Config.TIME_FRAME)
                .setMAType(MAType.WEIGHTED)
                .build();

        Indicator wmaSlow = new MovingAverageBuilder(apiConnector)
                .setPeriod(100)
                .setCandlestickPriceType(CandlestickPriceType.CLOSE)
 //               .setCandleTimeFrame(Config.TIME_FRAME)
                .setMAType(MAType.WEIGHTED)
                .build();

        Indicator rsi = new RSIBuilder(apiConnector)
                .setPeriod(14)
                .setCandlestickPriceType(CandlestickPriceType.CLOSE)
 //               .setCandleGranularity(Config.TIME_FRAME)
                .build();


        BGXTradeGenerator signalGenerator = new BGXTradeGenerator(wmaFast, wmaMiddle, wmaSlow, smaPrice, dailyPrice, rsi);

        Observable priceObserver = PriceObservable.create(context);

        //create observers
        Observer smaPriceObserver = IndicatorObserver.create(smaPrice);
        Observer dailyPriceObserver = IndicatorObserver.create(dailyPrice);
        Observer wmaFastObserver = IndicatorObserver.create(wmaFast);
        Observer wmaMiddleObserver = IndicatorObserver.create(wmaMiddle);
        Observer wmaSlowObserver = IndicatorObserver.create(wmaSlow);
        Observer rsiObserver = IndicatorObserver.create(rsi);

        //create trade service
        NewTradeService newTradeService = new NewTradeService(context, signalGenerator);
        ExitStrategy exitStrategy = new FullCloseStrategy(context, Config.TIME_FRAME);
        OrderService orderService = new OrderService(context);

        //create position manager
        Observer tradeManager = new PositionManager(context, newTradeService, exitStrategy, orderService);

        priceObserver.registerObserver(smaPriceObserver);
        priceObserver.registerObserver(dailyPriceObserver);
        priceObserver.registerObserver(wmaFastObserver);
        priceObserver.registerObserver(wmaMiddleObserver);
        priceObserver.registerObserver(wmaSlowObserver);
        priceObserver.registerObserver(rsiObserver);
        priceObserver.registerObserver(tradeManager);

        System.out.println("Start ");

        PricePull pricePull = new PricePull("PricePull", priceObserver);
    }

//    //Check for valid account
//    private static void validateAccount(Context context){
//
//        Connection.waitToConnect(Config.URL);
//
//        try {
//
//            AccountListResponse response = context.account.list();
//            List<AccountProperties> accountProperties;
//            accountProperties = response.getAccounts();
//
//            boolean hasAccount = false;
//            for (AccountProperties account : accountProperties) {
//                if (account.getId().equals(Config.ACCOUNTID))
//                    hasAccount = true;
//            }
//            if (!hasAccount)
//                throw new IllegalArgumentException("Account "+Config.ACCOUNTID+" not found");
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//        //Check if account balance is not zero
//        try {
//
//            AccountGetResponse response = context.account.get(Config.ACCOUNTID);
//            Account account;
//            account = response.getAccount();
//
//            // Check the balance
//            if (account.getBalance().doubleValue() <= 0.0) {
//                throw new IllegalArgumentException("Account "+Config.ACCOUNTID+" balance "+account.getBalance()+" <= 0");
//            }
//
//            if (!Config.INSTRUMENT.equals(new InstrumentName("EUR_USD"))){
//                throw new IllegalArgumentException("Robot must be used only on EUR/USD pair");
//            }
//
//            if(!account.getCurrency().toString().equalsIgnoreCase("EUR")){
//                throw  new IllegalArgumentException("Robot must be used only on EURO based accounts");
//            }
//        } catch(RequestException re){
//            if (re.getStatus() == 504 || re.getStatus() == 503){
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    throw  new RuntimeException(e);
//                }
//            }
//        }
//        catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

}
