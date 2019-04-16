package trader.broker.connector.oanda.transformer;

import com.oanda.v20.instrument.CandlestickData;
import com.oanda.v20.instrument.InstrumentCandlesResponse;
import com.oanda.v20.primitives.DateTime;
import trader.entity.candlestick.Candlestick;
import trader.entity.candlestick.candle.Candle;
import trader.responder.Response;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class OandaCandleTransformer{

    public <T> List<Candlestick> transformCandlesticks(Response<T> response) {
        List<Candlestick> traderCandlestickList = new ArrayList<>();
        if(response != null){
            InstrumentCandlesResponse responseDataStructure = (InstrumentCandlesResponse) response.getBody();
            List<com.oanda.v20.instrument.Candlestick> oandaCandles = responseDataStructure.getCandles();
            for (com.oanda.v20.instrument.Candlestick oandaCandlestick : oandaCandles) {
                if (oandaCandlestick.getComplete()){
                    traderCandlestickList.add(convertToTradeCandlestick(oandaCandlestick));
                }
            }
        }
        return traderCandlestickList;
    }

    private Candlestick convertToTradeCandlestick(com.oanda.v20.instrument.Candlestick oandaCandlestick) {
        CandlestickData mid = oandaCandlestick.getMid();
        return new Candle.CandleBuilder()
                .setClosePrice(mid.getC().bigDecimalValue())
                .setHighPrice(mid.getH().bigDecimalValue())
                .setLowPrice(mid.getL().bigDecimalValue())
                .setOpenPrice(mid.getO().bigDecimalValue())
                .setDateTime(convertDateTimeToZonedDateTime(oandaCandlestick.getTime()))
                .setVolume(oandaCandlestick.getVolume())
                .setComplete(oandaCandlestick.getComplete())
                .build();
    }

    private ZonedDateTime convertDateTimeToZonedDateTime(DateTime dateTime){
        Instant instantDateTime = Instant.parse(dateTime.toString());
        ZoneId zoneId = ZoneId.of("UTC");
        return ZonedDateTime.ofInstant(instantDateTime, zoneId);
    }

}
