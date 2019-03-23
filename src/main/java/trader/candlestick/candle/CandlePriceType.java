package trader.candlestick.candle;


import trader.candlestick.Candlestick;
import java.math.BigDecimal;

import static trader.strategy.BGXStrategy.configuration.StrategyConfig.SCALE;


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
                    .divide(BigDecimal.valueOf(2), SCALE,BigDecimal.ROUND_HALF_UP);
        }
    };

    public abstract BigDecimal extractPrice(Candlestick candle);
}
