package trader.requestor;

import org.junit.Before;
import org.junit.Test;
import trader.exception.EmptyArgumentException;
import trader.exception.NoSuchUseCaseException;
import trader.exception.NullArgumentException;
import trader.interactor.createbgxconfiguration.CreateBGXConfigurationUseCase;
import trader.presenter.Presenter;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class UseCaseFactoryImplTest {

    private UseCaseFactoryImpl useCaseFactory;
    private Presenter presenterMock;

    @Before
    public void setUp() {
        presenterMock = mock(Presenter.class);
        useCaseFactory = new UseCaseFactoryImpl();
    }

    @Test(expected = NullArgumentException.class)
    public void  givenNullUseCaseName_WhenCallMake_ThenException(){
        useCaseFactory.make(null, presenterMock);
    }

    @Test(expected = NullArgumentException.class)
    public void  givenNullPresenter_WhenCallMake_ThenException(){
        useCaseFactory.make("CreateBGXConfigurationController", null);
    }

    @Test(expected = EmptyArgumentException.class)
    public void givenEmptyStringForUseCaseName_WhenCallMake_ThenException(){
        useCaseFactory.make("  ", presenterMock);
    }

    @Test(expected = NoSuchUseCaseException.class)
    public void givenBadNameForUseCase_WhenCallMake_ThenException(){
        useCaseFactory.make("BlqUseCase", presenterMock);
    }

    @Test
    public void givenCorrectUseCaseName_WhenCallMake_ReturnCorrectObject(){
        UseCase useCase = useCaseFactory.make("CreateBGXConfigurationController", presenterMock);
        UseCase spacedUseCase = useCaseFactory.make("   CreateBGXConfigurationController   ", presenterMock);

        assertEquals(CreateBGXConfigurationUseCase.class, useCase.getClass());
        assertEquals(CreateBGXConfigurationUseCase.class, spacedUseCase.getClass());
    }
}
