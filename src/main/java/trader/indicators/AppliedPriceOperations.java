package trader.indicators;

import com.oanda.v20.instrument.CandlestickData;

import java.math.BigDecimal;

public interface AppliedPriceOperations {

    BigDecimal apply(CandlestickData candle);

}
