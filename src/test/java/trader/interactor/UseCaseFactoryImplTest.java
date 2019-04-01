package trader.interactor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trader.exception.EmptyArgumentException;
import trader.exception.NoSuchUseCaseException;
import trader.exception.NullArgumentException;

import static org.junit.Assert.*;

public class UseCaseFactoryImplTest {

    private UseCaseFactoryImpl useCaseFactory;

    @Before
    public void setUp() throws Exception {
        useCaseFactory = new UseCaseFactoryImpl();
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallMakeWithNull_Exception(){
        useCaseFactory.make(null);
    }

    @Test(expected = EmptyArgumentException.class)
    public void WhenCallMakeWithEmptyString_Exception(){
        useCaseFactory.make("");
    }

    @Test(expected = EmptyArgumentException.class)
    public void WhenCallMakeWithStringComposedOfSpaces_Exception(){
        useCaseFactory.make("     ");
    }

    @Test(expected = NoSuchUseCaseException.class)
    public void WhenCallWithBadUseCaseName_Exception(){
        useCaseFactory.make("BlqUseCase");
    }

    @Test
    public void WhenCallWithCorrectUseCaseName_ReturnCorrectObject(){
        UseCase useCase = useCaseFactory.make("addIndicator");
        UseCase spacedUseCase = useCaseFactory.make("   addIndicator   ");

        assertEquals(AddIndicatorUseCase.class, useCase.getClass());
        assertEquals(AddIndicatorUseCase.class, spacedUseCase.getClass());
    }

}
