package trader.broker.connector.oanda;

import trader.broker.connector.BaseConnector;
import trader.exception.BadRequestException;
import trader.exception.EmptyArgumentException;
import trader.exception.NullArgumentException;

public class OandaConnector extends BaseConnector {

    private String fileLocation;
    private String url;
    private String token;
    private String accountID;
    private String leverage;

    private OandaConnector(){ }

    @Override
    public String getFileLocation() {
        return fileLocation;
    }

    @Override
    public void setFileLocation(String fileLocation) {
        validateInputFileLocation(fileLocation);
        this.fileLocation = fileLocation.trim();
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public void setUrl(String url) {
        validateInput(url);
        this.url = url.trim();
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public void setToken(String token) {
        validateInput(token);
        this.token = token.trim();
    }

    @Override
    public String getAccountID() {
        return accountID;
    }

    @Override
    public void setAccountID(String accountId) {
        validateInput(accountId);
        this.accountID = accountId.trim();
    }

    @Override
    public String getLeverage() {
        return leverage;
    }

    @Override
    public void setLeverage(String leverage) {
        validateInput(leverage);
        this.leverage = leverage.trim();
    }

    @Override
    public String toString() {
        return "Connector: OANDA";
    }

    private void validateInputFileLocation(String fileLocation) {
        validateInput(fileLocation);
        if(isNotYamlFile(fileLocation))
            throw new BadRequestException();
    }

    private void validateInput(String input) {
        if(input == null)
            throw new NullArgumentException();
        if(input.isEmpty())
            throw new EmptyArgumentException();
    }

    private boolean isNotYamlFile(String fileLocation) {
        if(fileLocation.contains(".yaml"))
            return false;
        return !fileLocation.contains(".yml");
    }
}