package trader.broker.connector.oanda;

import com.oanda.v20.Context;
import com.oanda.v20.ExecuteException;
import com.oanda.v20.RequestException;
import com.oanda.v20.pricing.*;
import com.oanda.v20.primitives.DateTime;
import trader.config.Config;
import trader.connection.Connection;
import trader.price.Price;
import trader.price.Pricing;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import static trader.config.Config.INSTRUMEN_T;
import static trader.broker.connector.oanda.OandaConfig.ACCOUNT_ID;


public class OandaPriceResponse {

    private static final int THREAD_SLEEP_INTERVAL = 1000;

    private OandaConnector oandaConnector;
    private PricingGetRequest pricingGetRequest;

    OandaPriceResponse(OandaConnector connector){
        oandaConnector = connector;
        createPricingRequest();
    }

    public Pricing getPrice(){

        PricingGetResponse pricingGetResponse = createPricingGetResponse();
        if(isPriceTradeable(pricingGetResponse))
            return new Price.PriceBuilder().setIsTradable(false).build();

        ClientPrice clientPrice = pricingGetResponse.getPrices().get(0);
        BigDecimal ask = clientPrice.getAsks().get(0).getPrice().bigDecimalValue();
        BigDecimal bid = clientPrice.getBids().get(0).getPrice().bigDecimalValue();
        ZonedDateTime dateTime = convertDateTimeToZonedDateTime(clientPrice.getTime());
        boolean tradeable = clientPrice.getTradeable();
        BigDecimal availableUnits = clientPrice.getUnitsAvailable().getDefault().getLong().bigDecimalValue();
        return new Price.PriceBuilder()
                .setAsk(ask)
                .setBid(bid)
                .setDateTime(dateTime)
                .setIsTradable(tradeable)
                .setAvailableUnits(availableUnits)
                .build();
    }

    private boolean isPriceTradeable(PricingGetResponse pricingGetResponse) {
        return pricingGetResponse == null || pricingGetResponse.getPrices() == null || pricingGetResponse.getPrices().size() == 0;
    }


    private PricingGetResponse createPricingGetResponse() {
//        try {
//            Context context = oandaConnector.getContext();
//            return context.pricing.get(pricingGetRequest);
//        } catch (ExecuteException | RequestException e) {
//            Connection.waitToConnect(Config.URL);
//        } catch (RuntimeException e) {
//            throw new RuntimeException(e);
//        }
        return null;
    }

    private void createPricingRequest(){
        List<String> instruments = new ArrayList<>();
        instruments.add(INSTRUMEN_T);
        pricingGetRequest = new PricingGetRequest(ACCOUNT_ID, instruments);
    }

    private ZonedDateTime convertDateTimeToZonedDateTime(DateTime dateTime){
        Instant instantDateTime = Instant.parse(dateTime.toString());
        ZoneId zoneId = ZoneId.of("UTC");
        return ZonedDateTime.ofInstant(instantDateTime, zoneId);
    }

}
