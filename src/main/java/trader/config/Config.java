package trader.config;

import com.oanda.v20.account.AccountID;
import com.oanda.v20.primitives.InstrumentName;
import trader.candle.CandleGranularity;

import java.math.BigDecimal;

/**
 *  This is a configuration class, providing configurations for Oanda fxTrade and fxPractice.
 */

public final class Config {

    private Config(){

    }


    /**
     * URL - The fxTrade or fxPractice API URL
     */
    public static final String URL = "https://api-fxtrade.oanda.com";
    //public static final String URL = "https://api-fxpractice.oanda.com";

    /**
     * TOKEN - The OANDA API Personal Access token, obtained from OANDA website.
     */
    //fxPractice token
    public static final String TOKEN = "7fc3b8a323e95d1c0f35b3e12dfb0a29-da034ca81b764fe499946a2e4b092f12";
    //public static final String TOKEN = "b27b10dc93cac78ef1e082dde240de89-11397123d4fd3b93ffd028e30fbc1a22";

    /**
     * ACCOUNTID - object storing OANDA's account ID, obtained from OANDA website
     */
    public static final AccountID ACCOUNTID = new AccountID("001-004-1942536-001");
    //public static final AccountID ACCOUNTID = new AccountID("101-004-8077015-004");

    /**
     * INSTRUMENT_NAME - object storing the main trading pair. This can be only a pair supported by OANDA
     */
    public static final InstrumentName INSTRUMENT  = new InstrumentName("EUR_USD");


    ////// above will be remove //////////
    public static final String INSTRUMEN_T = "EUR_USD";

    /**
     * Risk Per Trade - Default Value is 0.01 or 1% of the account
     */
    public static final BigDecimal RISK_PER_TRADE = BigDecimal.valueOf(0.01);

    /**
     * Spread for EUR/USD
     */
    public static final BigDecimal SPREAD = BigDecimal.valueOf(0.0002);

    /**
     * System default timeFrame (@code CandleStickGranularity)
     */
    public static final CandleGranularity TIME_FRAME = CandleGranularity.M30;



}
