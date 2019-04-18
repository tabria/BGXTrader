package trader.interactor.createbrokerconnector.enums;

public enum Constants {

    BROKER_NAME {
        @Override
        public String toString() { return "brokerName"; }
    },
    URL {
        @Override
        public String toString() {
            return "url";
        }
    },
    TOKEN {
        @Override
        public String toString() {
            return "token";
        }
    },
    ID {
        @Override
        public String toString() {
            return "id";
        }
    },
    LEVERAGE {
        @Override
        public String toString() {
            return "leverage";
        }
    };
}
