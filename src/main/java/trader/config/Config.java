package trader.config;

import com.oanda.v20.account.AccountID;
import com.oanda.v20.instrument.CandlestickGranularity;
import com.oanda.v20.primitives.InstrumentName;

import java.math.BigDecimal;

/**
 *  This is a configuration class, providing configurations for Oanda fxTrade and fxPractice.
 */

public final class Config {

    private Config(){ };


    /**
     * URL - The fxTrade or fxPractice API URL
     */
    public static final String URL = "https://api-fxpractice.oanda.com";

    /**
     * TOKEN - The OANDA API Personal Access token, obtained from OANDA website.
     */
    //fxPractice token
    public static final String TOKEN = "3f74e7c9f6d6076442eacbaad10d0062-1405cb80e3f409f8865c1327a3fdde0d";


    /**
     * ACCOUNTID - object storing OANDA's account ID, obtained from OANDA website
     */
    public static final AccountID ACCOUNTID = new AccountID("101-004-8077015-001");

    /**
     * INSTRUMENT - object storing the main trading pair. This can be only a pair supported by OANDA
     */
    public static final InstrumentName INSTRUMENT  = new InstrumentName("EUR_USD");

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
    public static final CandlestickGranularity TIME_FRAME = CandlestickGranularity.M30;

}
