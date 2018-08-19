package trader.indicators.enums;


import com.oanda.v20.instrument.CandlestickData;
import trader.indicators.AppliedPriceOperations;

import java.math.BigDecimal;

/**
 * The class defines available price value, based on candlestick structure
 *
 */

public enum AppliedPrice implements AppliedPriceOperations {

    OPEN {
        /**
         * @param candle complete candlestick
         * @return candlestick's open price
         * @see CandlestickData
         */
        @Override
        public BigDecimal apply(CandlestickData candle) {
            return candle.getO().bigDecimalValue();
        }
    },


    CLOSE {
        /**
         * @param candle  complete candlestick
         * @return candlestick's close price
         * @see CandlestickData
         */
        @Override
        public BigDecimal apply(CandlestickData candle){
            return candle.getC().bigDecimalValue();
        }
    },

    HIGH {
        /**
         * @param candle  complete candlestick
         * @return candlestick highest price
         * @see CandlestickData
         */
        @Override
        public BigDecimal apply(CandlestickData candle){
            return candle.getH().bigDecimalValue();
        }
    },

    LOW {
        /**
         * @param candle  complete candlestick
         * @return candlestick lowest price
         * @see CandlestickData
         */
        @Override
        public BigDecimal apply(CandlestickData candle) {
            return candle.getL().bigDecimalValue();
        }
    },

    MEDIAN {
        /**
         * @param candle  complete candlestick
         * @return candlestick median price {@code (high + low )/2}
         * @see CandlestickData
         */
        @Override
        public BigDecimal apply(CandlestickData candle) {
            BigDecimal high = candle.getH().bigDecimalValue();
            BigDecimal low = candle.getL().bigDecimalValue();
            return high.add(low).divide(BigDecimal.valueOf(2), BigDecimal.ROUND_HALF_UP);
        }
    }


}
