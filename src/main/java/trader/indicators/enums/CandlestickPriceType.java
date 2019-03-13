package trader.indicators.enums;


import com.oanda.v20.instrument.CandlestickData;
import trader.indicators.CandlestickPriceOperations;

import java.math.BigDecimal;


public enum CandlestickPriceType implements CandlestickPriceOperations {

    OPEN {
        @Override
        public BigDecimal extractPrice(CandlestickData candle) {
            return candle.getO().bigDecimalValue();
        }
    },

    CLOSE {
        @Override
        public BigDecimal extractPrice(CandlestickData candle){
            return candle.getC().bigDecimalValue();
        }
    },

    HIGH {
        @Override
        public BigDecimal extractPrice(CandlestickData candle){
            return candle.getH().bigDecimalValue();
        }
    },

    LOW {
        @Override
        public BigDecimal extractPrice(CandlestickData candle) {
            return candle.getL().bigDecimalValue();
        }
    },

    MEDIAN {
        @Override
        public BigDecimal extractPrice(CandlestickData candle) {
            return medianPrice(candle.getH().bigDecimalValue(), candle.getL().bigDecimalValue());
        }

        private BigDecimal medianPrice(BigDecimal high, BigDecimal low) {
            return high.add(low).divide(BigDecimal.valueOf(2), BigDecimal.ROUND_HALF_UP);
        }
    }
}
