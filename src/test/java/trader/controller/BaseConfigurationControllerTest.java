package trader.controller;

import org.junit.Before;
import trader.interactor.UseCase;
import trader.requestor.Request;
import trader.requestor.RequestBuilder;
import trader.requestor.UseCaseFactory;
import trader.responder.Response;

import java.util.HashMap;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class BaseConfigurationControllerTest<T> {
    Response responseMock;
    HashMap<String, String> settings;
    RequestBuilder requestBuilderMock;
    UseCaseFactory useCaseFactoryMock;
    Request requestMock;
    UseCase useCaseMock;
    T configurationMock;


    @Before
    public void setUp() throws Exception {
        requestBuilderMock = mock(RequestBuilder.class);
        useCaseFactoryMock = mock(UseCaseFactory.class);
        requestMock = mock(Request.class);
        useCaseMock = mock(UseCase.class);
        responseMock = mock(Response.class);
        settings = new HashMap<>();
    }

     void setConfigurationMock(T configuration){
        configurationMock = configuration;
    }

    protected void setExecuteSettings(String controllerName) {
        when(responseMock.getResponseDataStructure()).thenReturn(configurationMock);
        when(useCaseFactoryMock.make(anyString())).thenReturn(useCaseMock);
        when(useCaseMock.execute(requestMock)).thenReturn(responseMock);
        when(requestBuilderMock.build(controllerName, settings)).thenReturn(requestMock);
    }
}
