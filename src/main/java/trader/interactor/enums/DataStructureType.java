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
    CREATE_POINT{
        @Override
        public String toString() {
            return "createPoint";
        }
    }


}
