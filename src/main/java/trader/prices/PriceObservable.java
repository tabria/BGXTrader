package trader.prices;

import com.oanda.v20.Context;
import com.oanda.v20.ExecuteException;
import com.oanda.v20.RequestException;
import com.oanda.v20.pricing.ClientPrice;
import com.oanda.v20.pricing.PricingContext;
import com.oanda.v20.pricing.PricingGetRequest;
import com.oanda.v20.pricing.PricingGetResponse;
import com.oanda.v20.primitives.DateTime;
import trader.config.Config;
import trader.connection.Connection;
import trader.connectors.ApiConnector;
import trader.core.Observable;
import trader.core.Observer;
import trader.indicators.IndicatorObserver;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public final class PriceObservable implements Observable {

    private static final int THREAD_SLEEP_INTERVAL = 1000;

    private ApiConnector apiConnector;
    private Pricing oldPrice;


    private final Context context;
    private volatile boolean running = true;
    private CopyOnWriteArrayList<Observer> observers;
    private PricingGetRequest request;

    private PriceObservable(Context context){
        apiConnector = ApiConnector.create("Oanda");
        oldPrice = new Price.PriceBuilder().build();

        this.context = context;
        this.observers = new CopyOnWriteArrayList<>();
        this.request = createRequest();
    }

    public static PriceObservable create(Context context){
        return new PriceObservable(context);

    }

    @Override
    public void registerObserver(Observer observer) {
        if (observer == null)
            throw new NullArgumentException();
        this.observers.add(observer);
    }

    @Override
    public void unregisterObserver(Observer observer) {
        if (observer != null)
            this.observers.remove(observer);
    }

    @Override
    public void notifyObservers(DateTime dateTime, BigDecimal ask, BigDecimal bid) {
        for (Observer observer : this.observers)
            observer.updateObserver(dateTime, ask, bid);
    }

    @Override
    public void notifyObservers(Pricing price) {
        for (Observer observer : this.observers)
            observer.updateObserver(price);
    }

    @Override
    public void execute() {

        BigDecimal oldAsk = BigDecimal.ZERO;
        BigDecimal oldBid = BigDecimal.ZERO;


        while(this.running){

            Pricing newPrice = apiConnector.getPrice();
            notifyEveryone(newPrice);
            sleepThread(THREAD_SLEEP_INTERVAL);
//            try {
//
//
//                PricingContext pricing = this.context.pricing;
//                PricingGetResponse pricingGetResponse = pricing.get(this.request);
//                //response from the OANDA servers based on the request object
//                PricingGetResponse response = this.context.pricing.get(this.request);
//
//                //extracting new values of ask and bid prices
//                ClientPrice newClientPrice = response.getPrices().get(0);
//                BigDecimal newAsk = newClientPrice.getAsks().get(0).getPrice().bigDecimalValue();
//                BigDecimal newBid = newClientPrice.getBids().get(0).getPrice().bigDecimalValue();
//                Boolean tradeable = newClientPrice.getTradeable();
//                boolean b = oldAsk.compareTo(newAsk) != 0;
//                boolean b1 = oldBid.compareTo(newBid) != 0;
//                if (newClientPrice.getTradeable() && (oldAsk.compareTo(newAsk) != 0 || oldBid.compareTo(newBid) != 0)) {
//                    oldAsk = newAsk;
//                    oldBid = newBid;
//                    this.notifyObservers(response.getTime(), newAsk, newBid);
//                }
//
//                Thread.sleep(THREAD_SLEEP_INTERVAL);
//
//            } catch(ExecuteException ee){
//                Connection.waitToConnect(Config.URL);
//            } catch (RequestException  | InterruptedException | RuntimeException e ) {
//                String message = e.getMessage();
//                if (message == null){
//                    this.sleepThread(THREAD_SLEEP_INTERVAL);
//
//                }else if (message.equalsIgnoreCase("Service unavailable, please try again later.")){
//                    Connection.waitToConnect(Config.URL);
//
//                }else{
//                    throw new RuntimeException(e);
//                }
//            }
//            catch (RuntimeException re){
//                //HTTP 503 exception "Unable to service request, please try again later.
//                String message = re.getMessage();
//                if (message == null || message.equalsIgnoreCase("Unable to service request, please try again later.")){
//                    this.sleepThread(THREAD_SLEEP_INTERVAL);
//
//                } else {
//                    throw new RuntimeException(re.getMessage());
//                }
//            }
        }
    }

    private void notifyEveryone(Pricing newPrice) {
        if (newPrice.isTradable() && !newPrice.equals(oldPrice)) {
            oldPrice = newPrice;
            this.notifyObservers(newPrice);
        }
    }

    private boolean isNotOld(BigDecimal oldAsk, BigDecimal oldBid, Pricing price) {
        return oldAsk.compareTo(price.getAsk()) != 0 || oldBid.compareTo(price.getBid()) != 0;
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

    public class NullArgumentException extends RuntimeException{};

}
