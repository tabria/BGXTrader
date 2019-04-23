package trader.controller;


import org.junit.Before;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import trader.interactor.RequestBuilderCreator;
import trader.presenter.Presenter;
import trader.requestor.*;
import trader.responder.Response;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RequestBuilderCreator.class)
public abstract class BaseControllerTest {
    protected Map<String, Object> settings;
    protected UseCaseFactory useCaseFactoryMock;
    protected UseCase useCaseMock;
    protected RequestBuilder requestBuilderMock;
    protected Request requestMock;
    protected Response responseMock;
    protected Presenter presenterMock;


    @Before
    public void setUp() throws Exception {
        settings = new HashMap<>();
        useCaseFactoryMock = mock(UseCaseFactory.class);
        useCaseMock = mock(UseCase.class);
        requestMock = mock(Request.class);
        requestBuilderMock = mock(RequestBuilder.class);
        responseMock = mock(Response.class);
        presenterMock = mock(Presenter.class);
    }

    void setFakeRequestFactoryCreator(){
        PowerMockito.mockStatic(RequestBuilderCreator.class);
        PowerMockito.when(RequestBuilderCreator.create(any())).thenReturn(requestBuilderMock);
    }

    void setFakeRequestFactory(){
        when(requestBuilderMock.build(settings)).thenReturn(requestMock);
    }

    void setFakeUseCaseFactory(){
        when(useCaseFactoryMock.make(anyString(), any(Presenter.class))).thenReturn(useCaseMock);
    }

    void setFakeUseCase(){
        when(useCaseMock.execute(requestMock)).thenReturn(responseMock);
        doNothing().when(presenterMock).execute(responseMock);
    }

    protected void setFakes() {
        setFakeRequestFactoryCreator();
        setFakeRequestFactory();
        setFakeUseCaseFactory();
        setFakeUseCase();
    }
}
