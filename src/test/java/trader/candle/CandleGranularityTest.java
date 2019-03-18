package trader.candle;

import com.oanda.v20.instrument.CandlestickGranularity;
import org.junit.Test;
import trader.candle.CandleGranularity;

import static org.junit.Assert.*;

public class CandleGranularityTest {


    private static final long MINUTE = 60L;
    private static final long HOUR = 3_600L;
    private static final long DAY = 86_400;
    private static final long WEEK = 604_800;
    private static final long MONTH = 2_629_746;

    @Test
    public void testGranularityToSeconds(){
        for (CandleGranularity candle:CandleGranularity.values()) {
            long expected = calculateSeconds(candle);
            assertEquals(candle.extractSeconds(), expected);
        }
    }

    @Test
    public void testExtractOANDAGranularity(){
        for (CandleGranularity candle:CandleGranularity.values()) {
            assertEquals(oandaCandlestickGranularity(candle), candleGranularity(candle));
        }
    }

    private long calculateSeconds(CandleGranularity candle) {
        String timeFrameType = candle.toString().toLowerCase().substring(0, 1);
        String timeFrameNumber = candle.toString().toLowerCase().substring(1);

        switch (timeFrameType){
            case "s": return Long.parseLong(timeFrameNumber);
            case "m": return candle.toString().length() > 1 ? Long.parseLong(timeFrameNumber) * MINUTE : MONTH;
            case "h": return Long.parseLong(timeFrameNumber) * HOUR;
            case "d": return DAY;
            case "w": return WEEK;
            default: return 0;
        }
    }

    private CandlestickGranularity candleGranularity(CandleGranularity candle) {
        return candle.extractOANDAGranularity();
    }

    private CandlestickGranularity oandaCandlestickGranularity(CandleGranularity candle) {
        return CandlestickGranularity.valueOf(candle.toString());
    }
}
