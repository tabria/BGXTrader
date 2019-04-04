package trader.broker.connector.oanda;

import com.oanda.v20.account.AccountID;

public final class OandaConfig {

    static final String URL = "https://api-fxtrade.oanda.com";
    static final String TOKEN = "7fc3b8a323e95d1c0f35b3e12dfb0a29-da034ca81b764fe499946a2e4b092f12";
    static final AccountID ACCOUNT_ID = new AccountID("001-004-1942536-001");

   private OandaConfig(){}
}
