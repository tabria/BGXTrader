package trader.broker.connector;

public interface BrokerConnector {

    String getFileLocation();

    void setFileLocation(String location);

    String getUrl();

    void setUrl(String url);

    String getToken();

    void setToken(String token);

    String getAccountID();

    void setAccountID(String accountID);

    static BaseConnector create(String apiName) {
        return BaseConnector.create(apiName);
    }

}
