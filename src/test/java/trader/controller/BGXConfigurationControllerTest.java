package trader.controller;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trader.exception.BadRequestException;
import trader.exception.NullArgumentException;
import trader.interactor.UseCase;
import trader.requestor.Request;
import trader.requestor.RequestBuilder;
import trader.requestor.UseCaseFactory;
import trader.responder.Response;
import trader.strategy.bgxstrategy.configuration.BGXConfiguration;
import java.util.HashMap;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BGXConfigurationControllerTest {

    private static final String BGX_CONFIGURATION = "BGXConfiguration";

    private RequestBuilder requestBuilderMock;
    private UseCaseFactory useCaseFactoryMock;
    private Request requestMock;
    private UseCase useCaseMock;
    private Response responseMock;
    private BGXConfiguration configurationMock;
    private BGXConfigurationController bgxConfigurationController;
    private HashMap<String, String> settings;

    @Before
    public void setUp() {
        requestBuilderMock = mock(RequestBuilder.class);
        useCaseFactoryMock = mock(UseCaseFactory.class);
        requestMock = mock(Request.class);
        useCaseMock = mock(UseCase.class);
        responseMock = mock(Response.class);
        configurationMock = mock(BGXConfiguration.class);
        settings = new HashMap<>();
        bgxConfigurationController = new BGXConfigurationController(requestBuilderMock, useCaseFactoryMock);

    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreateControllerWithNullRequestBuilder_Exception(){
        new BGXConfigurationController(null, useCaseFactoryMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreateControllerWithNullUserCaseFactory_Exception(){
        new BGXConfigurationController(requestBuilderMock, null);
    }

    @Test
    public void WhenCallGetRequestWithCorrectSettings_ReturnCorrectResult(){
        when(requestMock.getRequestDataStructure()).thenReturn(configurationMock);
        when(requestBuilderMock.build(BGX_CONFIGURATION, settings)).thenReturn(requestMock);
        Request<?> configurationRequest = bgxConfigurationController.getRequest(BGX_CONFIGURATION, settings);

        assertEquals(configurationMock, configurationRequest.getRequestDataStructure());
    }

    @Test
    public void WhenCallMakeWithCorrectSetting_CorrectResult(){
        String useCaseName = "BGXConfigurationController";
        when(useCaseFactoryMock.make(useCaseName)).thenReturn(useCaseMock);
        UseCase useCase = bgxConfigurationController.make(useCaseName);

        assertEquals(useCaseMock, useCase);
    }

    @Test
    public void WhenCallExecuteWithCorrectSettings_CorrectResponse(){
        setExecuteSettings();
        Response<BGXConfiguration> bgxConfigurationResponse = bgxConfigurationController.execute(BGX_CONFIGURATION, settings);

        assertEquals(configurationMock, bgxConfigurationResponse.getResponseDataStructure());
    }

    private void setExecuteSettings() {
        when(responseMock.getResponseDataStructure()).thenReturn(configurationMock);
        when(useCaseFactoryMock.make(anyString())).thenReturn(useCaseMock);
        when(useCaseMock.execute(requestMock)).thenReturn(responseMock);
        when(requestBuilderMock.build(BGX_CONFIGURATION, settings)).thenReturn(requestMock);
    }
}
