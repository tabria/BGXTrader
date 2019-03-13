package trader.indicators;

import com.oanda.v20.instrument.CandlestickGranularity;

public interface CandleGranularityOperations {

    long MINUTE = 60L;
    long HOUR = 3_600L;
    long DAY = 86_400;
    long WEEK = 604_800;
    long MONTH = 2_629_746;

    long extractSeconds();
    CandlestickGranularity extractOANDAGranularity();
}
