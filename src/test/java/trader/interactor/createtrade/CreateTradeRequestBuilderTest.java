package trader.interactor.createtrade;

import org.junit.Before;
import trader.interactor.BaseRequestBuilderTest;

public class CreateTradeRequestBuilderTest extends BaseRequestBuilderTest {

    private CreateTradeRequestBuilder requestBuilder;

    @Before
    public void setUp() throws Exception {
        requestBuilder = new CreateTradeRequestBuilder();
        super.setRequestBuilder(requestBuilder);
    }
}
