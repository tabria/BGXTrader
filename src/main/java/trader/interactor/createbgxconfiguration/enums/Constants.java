package trader.interactor.createbgxconfiguration.enums;

public enum Constants {
    CANDLES_QUANTITY {
        @Override
        public String toString() {
            return "candlesQuantity";
        }
    },
    INITIAL {
        @Override
        public String toString() {
            return "initial";
        }
    },
    UPDATE {
        @Override
        public String toString() {
            return "update";
        }
    },
    RISK {
        @Override
        public String toString() {
            return "risk";
        }
    },
    RISK_PER_TRADE {
        @Override
        public String toString() {
            return "riskPerTrade";
        }
    },
    INDICATOR {
        @Override
        public String toString() {
            return "indicator";
        }
    },
    ENTRY_STRATEGY {
        @Override
        public String toString() {
            return "entryStrategy";
        }
    },
    ORDER_STRATEGY {
        @Override
        public String toString() {
            return "orderStrategy";
        }
    },
    EXIT_STRATEGY {
        @Override
        public String toString() {
            return "exitStrategy";
        }
    },
    ENTRY {
        @Override
        public String toString() {
            return "entry";
        }
    },
    ORDER {
        @Override
        public String toString() {
            return "order";
        }
    },
    EXIT {
        @Override
        public String toString() {
            return "exit";
        }
    },
    EXIT_GRANULARITY{
        @Override
        public String toString() {
            return "exitGranularity";
        }
    },
    STOP_LOSS_FILTER {
        @Override
        public String toString() {
            return "stopLossFilter";
        }
    },
    TARGET {
        @Override
        public String toString() {
            return "target";
        }
    },
    RSI_FILTER {
        @Override
        public String toString() {
            return "rsiFilter";
        }
    },
    ENTRY_FILTER {
        @Override
        public String toString() {
            return "entryFilter";
        }
    };
}
