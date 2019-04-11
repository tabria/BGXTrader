package trader.interactor.enums;

public enum DataStructureType {

    INDICATOR,
    BGX_CONFIGURATION {
        @Override
        public String toString() {
            return "bgxConfiguration";
        }
    },
    BROKER_CONNECTOR{
        @Override
        public String toString() {
            return "brokerConnector";
        }
    },
    CREATE_TRADE{
        @Override
        public String toString() {
            return "createTrade";
        }
    },
    ENTRY_STRATEGY{
        @Override
        public String toString() {
            return "entryStrategy";
        }
    },
    ORDER_STRATEGY{
        @Override
        public String toString() {
            return "orderStrategy";
        }
    }


}
