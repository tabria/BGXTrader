package trader.requestor;

import org.junit.Before;
import org.junit.Test;
import trader.exception.EmptyArgumentException;
import trader.exception.NoSuchUseCaseException;
import trader.exception.NullArgumentException;
import trader.interactor.CreateIndicatorUseCase;
import trader.interactor.createbgxconfiguration.CreateBGXConfigurationUseCase;

import static org.junit.Assert.*;

public class UseCaseFactoryImplTest {

    private UseCaseFactoryImpl useCaseFactory;

    @Before
    public void setUp() {
        useCaseFactory = new UseCaseFactoryImpl();
    }

    @Test(expected = NullArgumentException.class)
    public void  givenNullUseCaseName_WhenCallMake_ThenException(){
        useCaseFactory.make(null);
    }

    @Test(expected = EmptyArgumentException.class)
    public void givenEmptyStringForUseCaseName_WhenCallMake_ThenException(){
        useCaseFactory.make("  ");
    }

    @Test(expected = NoSuchUseCaseException.class)
    public void givenBadNameForUseCase_WhenCallMake_ThenException(){
        useCaseFactory.make("BlqUseCase");
    }

    @Test
    public void givenCorrectUseCaseName_WhenCallMake_ReturnCorrectObject(){
        UseCase useCase = useCaseFactory.make("CreateBGXConfigurationController");
        UseCase spacedUseCase = useCaseFactory.make("   CreateBGXConfigurationController   ");

        assertEquals(CreateBGXConfigurationUseCase.class, useCase.getClass());
        assertEquals(CreateBGXConfigurationUseCase.class, spacedUseCase.getClass());
    }
}
