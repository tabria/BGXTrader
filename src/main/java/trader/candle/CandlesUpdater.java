package trader.candle;

import com.oanda.v20.Context;
import com.oanda.v20.instrument.InstrumentCandlesRequest;
import com.oanda.v20.primitives.DateTime;
import trader.connectors.ApiConnector;
import trader.core.Observer;
import trader.prices.Pricing;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;


public final class CandlesUpdater {

//    private static final DateTime DEFAULT_DATE_TIME = new DateTime("2018-01-01T01:01:01Z");

//
//    /////////////To be removed//////////////////////
//    private  Context context = null;
//    private  CandleGranularity candleTimeFrame;
//    private InstrumentCandlesRequest request;
//    private List<com.oanda.v20.instrument.Candlestick> candlestickListApi;
//
    public CandlesUpdater(Context context, InstrumentCandlesRequest request, CandleGranularity candleGranularity){
//        this.context = context;
//        this.candleTimeFrame = candleGranularity;
//        this.request = request;
//        this.candlestickList = new ArrayList<>();
//
//        this.requestCandles();
    }
//to check while for refactor
    /////////////////////////////////////////////////////////////////////
    public boolean updateCandles(DateTime candleDateTime){

        return false;
    }
    ///////////////////////////////////////////////
    private int sleepTimeMilliseconds = 1000;
    private ApiConnector apiConnector;
    private List<Candlestick> candlestickList;


    public CandlesUpdater(ApiConnector apiConnector){
        this.apiConnector = apiConnector;
        candlestickList = initialize();
    }

    public List<Candlestick> getCandles(){
        return Collections.unmodifiableList(this.candlestickList);
    }

    public Candlestick getUpdateCandle(){
        updateCandles();
        return candlestickList.get(candlestickList.size()-1);
    }

    private List<Candlestick> initialize() {
            return apiConnector.getInitialCandles();
    }

    private void updateCandles(){
        Candlestick updateCandle = apiConnector.getUpdateCandle();
        Candlestick lastCandlestick = getLastCandlestick();
        if(updateCandle.isComplete() && isDateTimeTradeable(updateCandle, lastCandlestick))
            candlestickList.add(updateCandle);
    }

    private boolean isDateTimeTradeable(Candlestick updateCandle, Candlestick lastCandlestick) {
        while(compareDateTimes(updateCandle, lastCandlestick)){
            updateCandle = apiConnector.getUpdateCandle();
            sleep(sleepTimeMilliseconds);
        }
        return true;
    }

    private void sleep(int sleepMillis) {
        try {
            Thread.sleep(sleepMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean compareDateTimes(Candlestick updateCandle, Candlestick lastCandlestick) {
        return updateCandle.getDateTime().compareTo(lastCandlestick.getDateTime()) <= 0;
    }

    private Candlestick getLastCandlestick() {
        return candlestickList.get(candlestickList.size() - 1);
    }
}
