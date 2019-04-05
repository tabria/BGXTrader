package trader.broker.connector;

public interface SettableBrokerConnector {

    String getFileLocation();

    void setFileLocation(String location);

    String getUrl();

    void setUrl(String url);

    String getToken();

    void setToken(String token);

    String getAccountID();

    void setAccountID(String accountID);

    void validateConnector();

    static SettableBrokerConnector create(String apiName) {
        return BaseConnector.create(apiName);
    }

}
