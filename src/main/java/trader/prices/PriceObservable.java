package trader.prices;

import com.oanda.v20.Context;
import com.oanda.v20.ExecuteException;
import com.oanda.v20.RequestException;
import com.oanda.v20.pricing.ClientPrice;
import com.oanda.v20.pricing.PricingGetRequest;
import com.oanda.v20.pricing.PricingGetResponse;
import com.oanda.v20.primitives.DateTime;
import trader.config.Config;
import trader.connection.Connection;
import trader.core.Observable;
import trader.core.Observer;
import trader.indicators.IndicatorObserver;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * This class is requesting new prices every second
 */

public final class PriceObservable implements Observable {

    private static final int THREAD_SLEEP_INTERVAL = 1000;

    private final Context context;
    private volatile boolean running = true;
    private CopyOnWriteArrayList<Observer> observers;
//    private final Set<Observer> observers;
    private PricingGetRequest request;

    /**
     * Constructor
     * @param context oanda contex
     * @see Context
     */
    private PriceObservable(Context context){
        this.context = context;
        this.observers = new CopyOnWriteArrayList<>();
        this.request = createRequest();
    }

    /**
     *
     * @param context oanda context
     * @return {@link PriceObservable}
     */
    public static PriceObservable create(Context context){
        return new PriceObservable(context);

    }

    /**
     * Register observers
     *
     * @param observer current observer
     * @see Observer
     * @see IndicatorObserver
     */
    @Override
    public void registerObserver(Observer observer) {
        if (observer == null){
            return;
        }
        this.observers.add(observer);
    }

    /**
     * Unregister observer
     * @param observer current observer
     * @see Observer
     * @see IndicatorObserver
     */
    @Override
    public void unregisterObserver(Observer observer) {
        if (observer != null){
            this.observers.remove(observer);
        }
    }

    /**
     * Notify observers
     * @param dateTime dateTime of the last fetched candle
     * @see DateTime
     */

    @Override
    public void notifyObservers(DateTime dateTime, BigDecimal ask, BigDecimal bid) {
        for (Observer observer : this.observers) {
            observer.updateObserver(dateTime, ask, bid);
        }
    }

    /**
     * This method compare old prices to new prices and notify observers if needed
     */
    @Override
    public void execute() {

        //old values of the ask and bid prices
        BigDecimal oldAsk = BigDecimal.ZERO;
        BigDecimal oldBid = BigDecimal.ZERO;

        while(this.running){

            try {
                //response from the OANDA servers based on the request object
                PricingGetResponse response = this.context.pricing.get(this.request);

                //extracting new values of ask and bid prices
                ClientPrice newClientPrice = response.getPrices().get(0);
                BigDecimal newAsk = newClientPrice.getAsks().get(0).getPrice().bigDecimalValue();
                BigDecimal newBid = newClientPrice.getBids().get(0).getPrice().bigDecimalValue();

                if (newClientPrice.getTradeable() && (oldAsk.compareTo(newAsk) != 0 || oldBid.compareTo(newBid) != 0)) {
                    oldAsk = newAsk;
                    oldBid = newBid;
                    this.notifyObservers(response.getTime(), newAsk, newBid);
                }

                Thread.sleep(THREAD_SLEEP_INTERVAL);

            } catch(ExecuteException ee){
                Connection.waitToConnect(Config.URL);
            } catch (RequestException  | InterruptedException e ) {
                String message = e.getMessage();
                //message is null
                //HTTP 500 exception An internal server error has occurred
                if (message == null){
                    this.sleepThread(THREAD_SLEEP_INTERVAL);

                }else if (message.equalsIgnoreCase("Service unavailable, please try again later.")){
                    Connection.waitToConnect(Config.URL);

                }else{
                    throw new RuntimeException(e);
                }
            }  catch (RuntimeException re){
                //HTTP 503 exception "Unable to service request, please try again later.
                String message = re.getMessage();
                if (message == null || message.equalsIgnoreCase("Unable to service request, please try again later.")){
                    this.sleepThread(THREAD_SLEEP_INTERVAL);

                } else {
                    throw new RuntimeException(re.getMessage());
                }
            }
        }
    }

    private void sleepThread(int sleepInterval){
        try {
            Thread.sleep(sleepInterval);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * create price request object
     * @return new PricingRequestObject with data from the config file
     * @see Config
     * @see PricingGetRequest
     */
    private PricingGetRequest createRequest(){

        List<String> instruments = new ArrayList<>();
        instruments.add(Config.INSTRUMENT.toString());

        return  new PricingGetRequest(Config.ACCOUNTID, instruments);
    }

}
