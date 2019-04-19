package trader.interactor.createtrade.enums;

public enum Constants {

    TRADABLE {
        @Override
        public String toString() {
            return "tradable";
        }
    },
    DIRECTION{
        @Override
        public String toString() {
            return "direction";
        }
    },
    ENTRY_PRICE {
        @Override
        public String toString() {
            return "entryPrice";
        }
    },
    STOP_LOSS_PRICE {
        @Override
        public String toString() {
            return "stopLossPrice";
        }
    };
}
