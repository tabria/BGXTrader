package trader.candle;


import java.math.BigDecimal;

import static trader.strategies.BGXStrategy.StrategyConfig.SCALE;


public enum CandlestickPriceType implements CandlestickPriceOperations {

    OPEN {
        @Override
        public BigDecimal extractPrice(Candlestick candle) {
            return candle.getOpenPrice();
        }
    },

    CLOSE {
        @Override
        public BigDecimal extractPrice(Candlestick candle){
            return candle.getClosePrice();
        }
    },

    HIGH {
        @Override
        public BigDecimal extractPrice(Candlestick candle){
            return candle.getHighPrice();
        }
    },

    LOW {
        @Override
        public BigDecimal extractPrice(Candlestick candle) {
            return candle.getLowPrice();
        }
    },

    MEDIAN {
        @Override
        public BigDecimal extractPrice(Candlestick candle) {
            return medianPrice(candle.getHighPrice(), candle.getLowPrice());
        }

        private BigDecimal medianPrice(BigDecimal high, BigDecimal low) {
            return high.add(low)
                    .divide(BigDecimal.valueOf(2), SCALE,BigDecimal.ROUND_HALF_UP);
        }
    }
}
