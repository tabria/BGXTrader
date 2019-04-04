//package trader.broker;
//
//import trader.exception.BadRequestException;
//import trader.exception.EmptyArgumentException;
//import trader.exception.NullArgumentException;
//
//public class BrokerConnectorImpl implements BrokerConnector {
//
//    ///will be removed
//
//    private String fileLocation;
//    private String url;
//    private String token;
//    private String accountID;
//    private String instrument;
//
//
//    public String getFileLocation() {
//        return fileLocation;
//    }
//
//
//    public void setFileLocation(String fileLocation) {
//        validateInputFileLocation(fileLocation);
//        this.fileLocation = fileLocation.trim();
//    }
//
//
//    public String getUrl() {
//        return url;
//    }
//
//
//    public void setUrl(String url) {
//        validateInput(url);
//        this.url = url.trim();
//    }
//
//
//    public String getToken() {
//        return token;
//    }
//
//
//    public void setToken(String token) {
//        validateInput(token);
//        this.token = token.trim();
//    }
//
//
//    public String getAccountID() {
//        return accountID;
//    }
//
//
//    public void setAccountID(String accountId) {
//        validateInput(accountId);
//        this.accountID = accountId.trim();
//    }
//
//
//    public String getInstrument() {
//        return instrument;
//    }
//
//    public void setInstrument(String instrument) {
//        validateInput(instrument);
//        this.instrument = instrument.trim();
//    }
//
//    private void validateInputFileLocation(String fileLocation) {
//        validateInput(fileLocation);
//        if(isNotYamlFile(fileLocation))
//            throw new BadRequestException();
//    }
//
//    private void validateInput(String input) {
//        if(input == null)
//            throw new NullArgumentException();
//        if(input.isEmpty())
//            throw new EmptyArgumentException();
//    }
//
//    private boolean isNotYamlFile(String fileLocation) {
//        if(fileLocation.contains(".yaml"))
//            return false;
//        return !fileLocation.contains(".yml");
//    }
//}
