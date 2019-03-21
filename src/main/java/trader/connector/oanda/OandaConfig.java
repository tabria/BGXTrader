package trader.connector.oanda;

import com.oanda.v20.account.AccountID;

public final class OandaConfig {

    private String url = "https://api-fxtrade.oanda.com";
    private String token = "7fc3b8a323e95d1c0f35b3e12dfb0a29-da034ca81b764fe499946a2e4b092f12";
    private AccountID accountID = new AccountID("001-004-1942536-001");

    OandaConfig(){}

    public AccountID getAccountID(){
        return accountID;
    }

    public String getToken(){
        return token;
    }

    public String getUrl(){
        return url;
    }
}
