package trader.broker.connector.oanda;

import com.oanda.v20.pricing.ClientPrice;
import com.oanda.v20.pricing.PricingGetResponse;
import com.oanda.v20.pricing_common.PriceBucket;
import com.oanda.v20.primitives.DateTime;
import trader.broker.connector.PriceTransformable;
import trader.price.Price;
import trader.price.PriceImpl;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class OandaPriceTransformer implements PriceTransformable {
    @Override
    public <T> Price transformToPrice(T response) {

        PricingGetResponse pricingGetResponse = (PricingGetResponse) response;
        if(isPriceTradeable(pricingGetResponse))
            return new PriceImpl.PriceBuilder().setIsTradable(false).build();

        ClientPrice clientPrice = pricingGetResponse.getPrices().get(0);
        BigDecimal ask = clientPrice.getAsks().get(0).getPrice().bigDecimalValue();
        BigDecimal bid = clientPrice.getBids().get(0).getPrice().bigDecimalValue();
        ZonedDateTime dateTime = convertDateTimeToZonedDateTime(clientPrice.getTime());
        boolean tradeable = clientPrice.getTradeable();
        BigDecimal availableUnits = clientPrice.getUnitsAvailable().getDefault().getLong().bigDecimalValue();
        return new PriceImpl.PriceBuilder()
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

    private ZonedDateTime convertDateTimeToZonedDateTime(DateTime dateTime){
        Instant instantDateTime = Instant.parse(dateTime.toString());
        ZoneId zoneId = ZoneId.of("UTC");
        return ZonedDateTime.ofInstant(instantDateTime, zoneId);
    }
}
