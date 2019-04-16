package trader.controller;

import org.junit.Before;
import trader.requestor.*;
import trader.responder.Response;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class BaseControllerTest<T> {
////////////to be deleted///////////////
    RequestOLDBuilder requestOLDBuilderMock;
    HashMap<String, String> settings2 = new HashMap<>();
/////////////// to be deleted/////////////////
    Response responseMock;
    Map<String, Object> settings;
    RequestBuilder requestBuilderMock;
    Request requestMock;
    UseCaseFactory useCaseFactoryMock;

    UseCase useCaseMock;
    T configurationMock;


    @Before
    public void setUp() throws Exception {
        ////////////// to be deleted////////////
        requestOLDBuilderMock = mock(RequestOLDBuilder.class);
        requestMock = mock(Request.class);
        ////////////// to be deleted////////////////////

;
        requestBuilderMock = mock(RequestBuilder.class);
        requestMock = mock(Request.class);
        useCaseFactoryMock = mock(UseCaseFactory.class);
        useCaseMock = mock(UseCase.class);
        responseMock = mock(Response.class);
        settings = new HashMap<>();
    }

     void setConfigurationMock(T configuration){
        configurationMock = configuration;
    }

    protected void setExecuteSettings(String controllerName) {
        when(responseMock.getBody()).thenReturn(configurationMock);
        when(useCaseFactoryMock.make(anyString())).thenReturn(useCaseMock);
        when(useCaseMock.execute(requestMock)).thenReturn(responseMock);


        //////////// to be removed///////////////
        when(requestOLDBuilderMock.build(controllerName, settings2)).thenReturn(requestMock);
        ///////////to be removed/////////////////
    }
}
