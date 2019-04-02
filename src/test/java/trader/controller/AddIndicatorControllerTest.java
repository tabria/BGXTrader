package trader.controller;

import org.junit.Before;
import org.junit.Test;
import trader.entity.indicator.Indicator;
import trader.exception.BadRequestException;
import trader.exception.NullArgumentException;
import trader.interactor.UseCase;
import trader.requestor.Request;
import trader.requestor.RequestBuilder;
import trader.requestor.UseCaseFactory;
import trader.responder.Response;
import trader.strategy.observable.PriceObservable;
import java.util.HashMap;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AddIndicatorControllerTest {

    private static final String RSI_INDICATOR = "rsiIndicator";
    private AddIndicatorController addIndicatorController;
    private RequestBuilder requestBuilderMock;
    private UseCaseFactory useCaseFactoryMock;
    private PriceObservable priceObservableMock;
    private Indicator indicatorMock;
    private UseCase useCaseMock;
    private Request requestMock;
    private HashMap<String, String> settings;

    @Before
    public void setUp() {
        requestBuilderMock = mock(RequestBuilder.class);
        useCaseFactoryMock = mock(UseCaseFactory.class);
        priceObservableMock = mock(PriceObservable.class);
        indicatorMock = mock(Indicator.class);
        useCaseMock = mock(UseCase.class);
        requestMock = mock(Request.class);
        settings = new HashMap<>();
        addIndicatorController = new AddIndicatorController(requestBuilderMock, useCaseFactoryMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreateControllerWithNullRequestBuilder_Exception(){
        new AddIndicatorController(null, useCaseFactoryMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenTryToGetRequestWithNullIndicatorType_Exception(){
        addIndicatorController.getRequest(null, new HashMap<>());
    }

    @Test(expected = NullArgumentException.class)
    public void WhenTryToGetRequestWithNullSettings_Exception(){
        addIndicatorController.getRequest("Test", null);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreateControllerWithNullUserCaseFactory_Exception(){
        new AddIndicatorController(requestBuilderMock, null);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallMakeWithNullUseCaseName_Exception(){
        addIndicatorController.make(null);
    }

    @Test
    public void TestIfUseCaseNameIsCorrect(){
        String useCaseName = addIndicatorController.composeUseCaseName();

        assertEquals("AddIndicator", useCaseName);
    }

    @Test
    public void WhenCallGetRequestWithCorrectSettings_ReturnCorrectResult(){
        when(requestMock.getRequestDataStructure()).thenReturn(indicatorMock);
        when(requestBuilderMock.build(RSI_INDICATOR, settings)).thenReturn(requestMock);
        Request<?> rsiIndicatorRequest = addIndicatorController.getRequest(RSI_INDICATOR, settings);

        assertEquals(indicatorMock, rsiIndicatorRequest.getRequestDataStructure());
    }

    @Test
    public void WhenCallMakeWithCorrectSetting_CorrectResult(){
        String useCaseName = "AddIndicator";
        when(useCaseFactoryMock.make(useCaseName)).thenReturn(useCaseMock);
        UseCase useCase = addIndicatorController.make(useCaseName);

        assertEquals(useCaseMock, useCase);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallExecuteWithNullPriceObservable_Exception(){
        addIndicatorController.execute(RSI_INDICATOR, settings, null);
    }
    @Test(expected = BadRequestException.class)
    public void WhenCallExecuteWithCorrectSettings_CorrectResponse(){
        setExecuteSettings();

        addIndicatorController.execute(RSI_INDICATOR, settings, priceObservableMock);
    }

    private void setExecuteSettings() {
        Response responseMock = mock(Response.class);
        when(responseMock.getResponseDataStructure()).thenReturn(indicatorMock);
        when(useCaseFactoryMock.make(anyString())).thenReturn(useCaseMock);
        when(useCaseMock.execute(requestMock)).thenReturn(responseMock);
        when(requestBuilderMock.build(RSI_INDICATOR, settings)).thenReturn(requestMock);
        doThrow(BadRequestException.class).when(priceObservableMock).registerObserver(any(Observer.class));
    }
}
