package trader.interactor.createindicator;

import org.junit.Before;
import trader.interactor.BaseRequestBuilderTest;

public class CreateIndicatorRequestBuilderTest extends BaseRequestBuilderTest {


    private CreateIndicatorRequestBuilder requestBuilder;

    @Before
    public void setUp() throws Exception {
        requestBuilder = new CreateIndicatorRequestBuilder();
        super.setRequestBuilder(requestBuilder);
    }


}
