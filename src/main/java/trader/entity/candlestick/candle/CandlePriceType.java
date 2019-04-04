package trader.entity.candlestick.candle;


import trader.entity.candlestick.Candlestick;
import java.math.BigDecimal;

public enum CandlePriceType {

    OPEN {
        public BigDecimal extractPrice(Candlestick candle) {
            return candle.getOpenPrice();
        }
    },

    CLOSE {
        public BigDecimal extractPrice(Candlestick candle){
            return candle.getClosePrice();
        }
    },

    HIGH {
        public BigDecimal extractPrice(Candlestick candle){
            return candle.getHighPrice();
        }
    },

    LOW {
        public BigDecimal extractPrice(Candlestick candle) {
            return candle.getLowPrice();
        }
    },

    MEDIAN {
        public BigDecimal extractPrice(Candlestick candle) {
            return medianPrice(candle.getHighPrice(), candle.getLowPrice());
        }

        private BigDecimal medianPrice(BigDecimal high, BigDecimal low) {
            return high.add(low)
                    .divide(BigDecimal.valueOf(2), 5,BigDecimal.ROUND_HALF_UP);
        }
    };


    public abstract BigDecimal extractPrice(Candlestick candle);
}
