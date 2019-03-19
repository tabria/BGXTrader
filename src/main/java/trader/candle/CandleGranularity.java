package trader.candle;

import com.oanda.v20.instrument.CandlestickGranularity;

//Remove oanda stuffs

public enum CandleGranularity implements CandleGranularityOperations {

    S5(5L, CandlestickGranularity.S5),
    S10(10L, CandlestickGranularity.S10),
    S15(15L,CandlestickGranularity.S15),
    S30(30L, CandlestickGranularity.S30),
    M1(MINUTE, CandlestickGranularity.M1),
    M2(2*MINUTE, CandlestickGranularity.M2),
    M4(4*MINUTE, CandlestickGranularity.M4),
    M5(5*MINUTE, CandlestickGranularity.M5),
    M10(10*MINUTE, CandlestickGranularity.M10),
    M15(15*MINUTE, CandlestickGranularity.M15),
    M30(30*MINUTE, CandlestickGranularity.M30),
    H1(HOUR, CandlestickGranularity.H1),
    H2(2*HOUR, CandlestickGranularity.H2),
    H3(3*HOUR, CandlestickGranularity.H3),
    H4(4*HOUR, CandlestickGranularity.H4),
    H6(6*HOUR, CandlestickGranularity.H6),
    H8(8*HOUR, CandlestickGranularity.H8),
    H12(12*HOUR, CandlestickGranularity.H12),
    D(DAY, CandlestickGranularity.D),
    W(WEEK, CandlestickGranularity.W),
    M(MONTH, CandlestickGranularity.M);

    private long granularityInSeconds;
    private CandlestickGranularity candlestickGranularityOANDA;

    CandleGranularity(long seconds, CandlestickGranularity originalGranularity){
        granularityInSeconds = seconds;
        candlestickGranularityOANDA = originalGranularity;
    }

    @Override
        public long extractSeconds() {
            return granularityInSeconds;
        }

    @Override
    public CandlestickGranularity extractOANDAGranularity() {
        return candlestickGranularityOANDA;
    }

}
