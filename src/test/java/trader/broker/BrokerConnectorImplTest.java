//package trader.broker;
//
//import org.junit.Before;
//import org.junit.Test;
//import trader.broker.connector.SettableBrokerConnector;
//import trader.exception.BadRequestException;
//import trader.exception.EmptyArgumentException;
//import trader.exception.NullArgumentException;
//
//import static org.junit.Assert.assertEquals;
//
//public class BrokerConnectorImplTest {
//
//    private SettableBrokerConnector config;
//
//    @Before
//    public void setUp() throws Exception {
//        //config = new BrokerConnectorImpl();
//    }
//
//    @Test(expected = NullArgumentException.class)
//    public void WhenCallSetFileLocationWithNull_Exception(){
//        config.setFileLocation(null);
//    }
//
//    @Test(expected = EmptyArgumentException.class)
//    public void WhenCallSetFileLocationWithEmptyString_Exception(){
//        config.setFileLocation("");
//    }
//
//    @Test(expected = BadRequestException.class)
//    public void WhenCallSetFileLocationWithStringWithoutYamlOrYmlExtension_Exception(){
//        config.setFileLocation("gtre.exe");
//    }
//
//    @Test
//    public void WhenCallSetFileLocationWithCorrectStringContainingExtraSpaces_TrimAndSet(){
//        config.setFileLocation("   sss.yaml ");
//
//        assertEquals("sss.yaml", config.getFileLocation());
//    }
//
//    @Test(expected = NullArgumentException.class)
//    public void WhenCallSetUrlWithNull_Exception(){
//        config.setUrl(null);
//    }
//
//    @Test(expected = EmptyArgumentException.class)
//    public void WhenCallSetUrlWithEmptyString_Exception(){
//        config.setUrl("");
//    }
//
//    @Test
//    public void WhenCallSetUrlWithCorrectUrlContainingSpaces_TrimAndSet(){
//        String url = "  https://www.www.con ";
//        config.setUrl(url);
//        assertEquals(url.trim(),config.getUrl());
//    }
//
//    @Test(expected = NullArgumentException.class)
//    public void WhenCallSetTokenWithNull_Exception(){
//        config.setToken(null);
//    }
//
//    @Test(expected = EmptyArgumentException.class)
//    public void WhenCallSetTokenWithEmptyString_Exception(){
//        config.setToken("");
//    }
//
//    @Test
//    public void WhenCallSetTokenWithCorrectUrlContainingSpaces_TrimAndSet(){
//        String token = "  dfsawewq32wsD!@#Dsa321sds1 ";
//        config.setToken(token);
//        assertEquals(token.trim(),config.getToken());
//    }
//
//    @Test(expected = NullArgumentException.class)
//    public void WhenCallSetAccountIDWithNull_Exception(){
//        config.setAccountID(null);
//    }
//
//    @Test(expected = EmptyArgumentException.class)
//    public void WhenCallSetAccountIDWithEmptyString_Exception(){
//        config.setAccountID("");
//    }
//
//    @Test
//    public void WhenCallSetAccountIDWithCorrectUrlContainingSpaces_TrimAndSet(){
//        String accountID = "  123-453-432-321 ";
//        config.setAccountID(accountID);
//        assertEquals(accountID.trim(),config.getAccountID());
//    }
//
//    @Test(expected = NullArgumentException.class)
//    public void WhenCallSetInstrumentWithNull_Exception(){
//        config.setInstrument(null);
//    }
//
//    @Test(expected = EmptyArgumentException.class)
//    public void WhenCallSetInstrumentWithEmptyString_Exception(){
//        config.setInstrument("");
//    }
//
//    @Test
//    public void WhenCallSetInstrumentWithCorrectUrlContainingSpaces_TrimAndSet(){
//        String instrument = "  EUR_USD ";
//        config.setInstrument(instrument);
//        assertEquals(instrument.trim(),config.getInstrument());
//    }
//}
