package trader.broker.connector.oanda;

import com.oanda.v20.account.AccountID;
import com.oanda.v20.instrument.CandlestickGranularity;
import com.oanda.v20.instrument.InstrumentCandlesRequest;
import com.oanda.v20.pricing.PricingGetRequest;
import com.oanda.v20.primitives.InstrumentName;
import trader.entity.candlestick.candle.CandleGranularity;
import trader.exception.*;
import trader.interactor.RequestImpl;
import trader.requestor.Request;
import trader.requestor.RequestBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class OandaRequestBuilder implements RequestBuilder {

    private static final String ACCOUNT_ID = "accountID";
    private static final String INSTRUMENT = "instrument";
    private static final String QUANTITY = "quantity";
    private static final String GRANULARITY = "granularity";

    @Override
    public Request<?> build(String requestType, HashMap<String, String> settings) {
        initialInputValidation(requestType, settings);
        if(requestType.trim().equalsIgnoreCase("price")){
            validateRequestInput(settings, ACCOUNT_ID, INSTRUMENT);
            return buildPricingRequest(settings);
        }
        if(requestType.trim().equalsIgnoreCase("candle")){
            validateRequestInput(settings, QUANTITY, INSTRUMENT, GRANULARITY);
            validateGranularity(settings);
            long quantity = parseQuantity(settings);
            return buildCandlesRequest(settings.get(INSTRUMENT), quantity ,settings.get(GRANULARITY));
        }
        throw new NoSuchDataStructureException();
    }

    private Request<PricingGetRequest> buildPricingRequest(HashMap<String, String> settings) {
        List<String> instruments = new ArrayList<>();
        instruments.add(settings.get(INSTRUMENT));
        AccountID accountId = new AccountID(settings.get(ACCOUNT_ID));
        Request<PricingGetRequest> request = new RequestImpl<>();
        request.setRequestDataStructure(new PricingGetRequest(accountId, instruments));
        return request;
    }

    private Request<InstrumentCandlesRequest> buildCandlesRequest(String instrument, long candlesQuantity, String granularity){
        Request<InstrumentCandlesRequest> request = new RequestImpl<>();
        request.setRequestDataStructure(
                new InstrumentCandlesRequest(new InstrumentName(instrument))
                        .setCount(candlesQuantity)
                        .setGranularity(extractGranularity(granularity.toUpperCase()))
                        .setSmooth(false)
        );
        return request;
    }

    private void initialInputValidation(String requestType, HashMap<String, String> settings) {
        if(requestType == null || settings == null)
            throw new NullArgumentException();
        if(settings.size() == 0)
            throw new EmptyArgumentException();
    }

    private void validateRequestInput(HashMap<String, String> settings, String... options) {
        for (String option : options) {
            if(!settings.containsKey(option))
                throw new BadRequestException();
            if(settings.get(option) == null)
                throw new NullArgumentException();
            if(isEmpty(settings, option))
                throw new EmptyArgumentException();
        }
    }

    private boolean isEmpty(HashMap<String, String> settings, String element) {
        return settings.get(element).trim().isEmpty();
    }

    private long parseQuantity(HashMap<String, String> settings) {
        long candlesQuantity = 0L;
        try{
            candlesQuantity = Long.parseLong(settings.get(QUANTITY));
        } catch (RuntimeException e) {
            throw new BadRequestException();
        }
        checkForNegativeNumber(candlesQuantity);
        return candlesQuantity;
    }

    private void checkForNegativeNumber(long number) {
        if(number < 1)
            throw new OutOfBoundaryException();
    }

    private void validateGranularity(HashMap<String, String> settings) {
        try{
            CandleGranularity.valueOf(settings.get(GRANULARITY).trim().toUpperCase());
        } catch (RuntimeException e){
            throw new BadRequestException();
        }
    }



    private CandlestickGranularity extractGranularity(String granularity) {
        return CandlestickGranularity.valueOf(granularity);
    }
}
