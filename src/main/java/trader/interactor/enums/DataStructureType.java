package trader.interactor.enums;

public enum DataStructureType {

    INDICATOR,
    BGX_CONFIGURATION {
        @Override
        public String toString() {
            return "bgxConfiguration";
        }
    },
    BROKER_GATEWAY{
        @Override
        public String toString() {
            return "brokerGateway";
        }
    }

}
