package trader.connectors.oanda;

import com.oanda.v20.Context;
import com.oanda.v20.ExecuteException;
import com.oanda.v20.RequestException;
import com.oanda.v20.pricing.PricingGetRequest;
import com.oanda.v20.pricing.PricingGetResponse;
import trader.prices.Price;

import java.util.ArrayList;
import java.util.List;
import static trader.config.Config.INSTRUMEN_T;


public class OandaPriceResponse {

    private OandaConnector oandaConnector;
    private PricingGetRequest pricingGetRequest;
    private OandaConfig oandaConfig;


    public OandaPriceResponse(OandaConnector connector){
        oandaConnector = connector;
        oandaConfig = new OandaConfig();
        createPricingRequest();
    }

    public Price getPrice(){

        PricingGetResponse pricingGetResponse = createPricingGetResponse();

        return null;
    }

    private PricingGetResponse createPricingGetResponse() {
        try {
            Context context = oandaConnector.getContext();
            return context.pricing.get(pricingGetRequest);
        } catch (ExecuteException | RequestException e) {
            //Connection.waitToConnect(Config.URL);
        }

//    } catch(ExecuteException ee){
//        Connection.waitToConnect(Config.URL);
//    } catch (RequestException  | InterruptedException e ) {
//        String message = e.getMessage();
//        //message is null
//        //HTTP 500 exception An internal server error has occurred
//        if (message == null){
//            this.sleepThread(THREAD_SLEEP_INTERVAL);
//
//        }else if (message.equalsIgnoreCase("Service unavailable, please try again later.")){
//            Connection.waitToConnect(Config.URL);
//
//        }else{
//            throw new RuntimeException(e);
//        }
//    }  catch (RuntimeException re){
//        //HTTP 503 exception "Unable to service request, please try again later.
//        String message = re.getMessage();
//        if (message == null || message.equalsIgnoreCase("Unable to service request, please try again later.")){
//            this.sleepThread(THREAD_SLEEP_INTERVAL);
//
//        } else {
//            throw new RuntimeException(re.getMessage());
//        }
//    }

        return null;
    }

    private void createPricingRequest(){
        List<String> instruments = new ArrayList<>();
        instruments.add(INSTRUMEN_T);
        pricingGetRequest = new PricingGetRequest(oandaConfig.getAccountID(), instruments);
    }

}
