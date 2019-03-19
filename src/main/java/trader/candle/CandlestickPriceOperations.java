package trader.candle;


import java.math.BigDecimal;

public interface CandlestickPriceOperations {

    BigDecimal extractPrice(Candlestick candle);

}
