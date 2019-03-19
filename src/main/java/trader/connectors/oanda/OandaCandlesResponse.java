package trader.connectors.oanda;

import com.oanda.v20.ExecuteException;
import com.oanda.v20.RequestException;
import com.oanda.v20.instrument.*;
import com.oanda.v20.primitives.DateTime;
import com.oanda.v20.primitives.InstrumentName;
import trader.candle.Candle;
import trader.candle.Candlestick;
import trader.exceptions.BadRequestException;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static trader.strategies.BGXStrategy.StrategyConfig.*;

public class OandaCandlesResponse {

    private OandaConnector oandaConnector;
    private InstrumentCandlesRequest initialCandlesRequest;
    private InstrumentCandlesRequest updateCandlesRequest;

    OandaCandlesResponse(OandaConnector connector){
        oandaConnector = connector;
        initialCandlesRequest = createCandlesRequest(INITIAL_CANDLES_QUANTITY);
        updateCandlesRequest = createCandlesRequest(UPDATE_CANDLES_QUANTITY);
    }

    List<Candlestick> getInitialCandles(){
        List<com.oanda.v20.instrument.Candlestick> oandaCandles =
                getOandaCandles(initialCandlesRequest);
        return transformToTradeCandlestickList(oandaCandles);
    }

    Candlestick getUpdateCandle(){
        List<com.oanda.v20.instrument.Candlestick> oandaCandles =
                getOandaCandles(updateCandlesRequest);
        return transformToTradeCandlestickList(oandaCandles).get(0);
    }

    private List<Candlestick> transformToTradeCandlestickList(List<com.oanda.v20.instrument.Candlestick> oandaCadles) {
        List<Candlestick> traderCandlestickList = new ArrayList<>(oandaCadles.size());
        for (com.oanda.v20.instrument.Candlestick oandaCandlestick : oandaCadles) {
            if (oandaCandlestick.getComplete()){
                traderCandlestickList.add(convertToTradeCandlestick(oandaCandlestick));
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

    private List<com.oanda.v20.instrument.Candlestick> getOandaCandles(InstrumentCandlesRequest request) {
        InstrumentCandlesResponse response = candlesResponse(request);
        return response.getCandles();
    }

    private InstrumentCandlesResponse candlesResponse(InstrumentCandlesRequest request) {
        try {
            return getInstrumentContext().candles(request);
        } catch (RequestException | ExecuteException e) {
            throw new BadRequestException();
        }
    }

    private InstrumentContext getInstrumentContext() {
        return oandaConnector.getContext().instrument;
    }

    private InstrumentCandlesRequest createCandlesRequest(long candlesQuantity){
        return new InstrumentCandlesRequest(new InstrumentName(INSTRUMENT_NAME))
                .setCount(candlesQuantity)
                .setGranularity(extractGranularity())
                .setSmooth(false);
    }

    private CandlestickGranularity extractGranularity() {
        return CandlestickGranularity.valueOf(CANDLE_GRANULARITY.toString());
    }
}
