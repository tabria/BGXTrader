package trader.exit.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import trader.broker.BrokerGateway;
import trader.configuration.TradingStrategyConfiguration;
import trader.entity.trade.BrokerTradeDetails;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ClosePositionServiceTest {

    private BrokerTradeDetails tradeDetailsMock;
    private BrokerGateway brokerGatewayMock;
    private TradingStrategyConfiguration configurationMock;
    private ClosePositionService service;

    @Before
    public void setUp() throws Exception {
        tradeDetailsMock = mock(BrokerTradeDetails.class);
        brokerGatewayMock = mock(BrokerGateway.class);
        configurationMock = mock(TradingStrategyConfiguration.class);
        service = new ClosePositionService();
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenNullPartsToClose_WhenCallClosePosition_ThenThrowException(){
        service.closePosition(tradeDetailsMock, brokerGatewayMock, configurationMock, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenPartsToCloseAreLessThanOne_WhenCallClosePosition_ThenThrowException(){
        service.closePosition(tradeDetailsMock, brokerGatewayMock, configurationMock, BigDecimal.valueOf(0));
    }

    @Test
    public void givenCorrectSettings_WhenCallClosePosition_ThenCloseCorrectPartOfThePosition(){
        BigDecimal parts = BigDecimal.valueOf(3);
        double unitsSize = 100;
        setFakeBrokerTradeDetails(11, unitsSize);
        setFakeConfigurations("EUR_USD");
        ArgumentCaptor<HashMap> captor = ArgumentCaptor.forClass(HashMap.class);
        when(brokerGatewayMock.placeOrder(any(HashMap.class), anyString())).thenReturn("12");
        BigDecimal expectedUnitsSize = BigDecimal.valueOf(unitsSize * -1).divide(parts, 0, RoundingMode.HALF_UP);

        service.closePosition(tradeDetailsMock, brokerGatewayMock, configurationMock, parts);

        verify(brokerGatewayMock, times(1)).placeOrder(captor.capture(), anyString());
        HashMap<String, String> hashMap = captor.getAllValues().get(0);

        assertEquals("EUR_USD", hashMap.get("instrument"));
        assertEquals(expectedUnitsSize.toString(), hashMap.get("unitsSize"));
    }

    @Test
    public void givenService_WhenCallToString_ThenReturnCorrectString(){
        assertEquals("position closed @", service.toString());
    }

    private void setFakeBrokerTradeDetails(int tradeIndex , double units){
        when(brokerGatewayMock.getTradeDetails(tradeIndex)).thenReturn(tradeDetailsMock);
        when(tradeDetailsMock.getCurrentUnits()).thenReturn(BigDecimal.valueOf( units));
    }

    private void setFakeConfigurations(String instrument){
        when(configurationMock.getInstrument()).thenReturn(instrument);
    }

}
