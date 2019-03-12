package trader.indicators;

import com.oanda.v20.instrument.CandlestickData;

import java.math.BigDecimal;

public interface CandlestickPriceOperations {

    BigDecimal extractPrice(CandlestickData candle);

}
