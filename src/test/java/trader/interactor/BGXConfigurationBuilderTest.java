package trader.interactor;

import org.junit.Before;
import org.junit.Test;
import trader.exception.*;
import trader.requestor.Request;

import java.util.HashMap;

public class BGXConfigurationBuilderTest {

    private BGXConfigurationBuilder bgxConfigurationBuilder;
    private HashMap<String, String> settings;

    @Before
    public void setUp() throws Exception {
        bgxConfigurationBuilder = new BGXConfigurationBuilder();
        settings = new HashMap<>();
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallBuildWithNullSettings_Exception(){
        bgxConfigurationBuilder.build(null);
    }

    @Test(expected = OutOfBoundaryException.class)
    public void SettingsMustHaveOnlyOneValue(){
        settings.put("a", "AA");
        settings.put("b", "BB");
        bgxConfigurationBuilder.build(settings);
    }

    @Test(expected = OutOfBoundaryException.class)
    public void WhenCallBuildWithEmptySettings_Exception(){
        bgxConfigurationBuilder.build(settings);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenLocationIsNull_Exception(){
        settings.put("location", null);
        bgxConfigurationBuilder.build(settings);
    }

    @Test(expected = EmptyArgumentException.class)
    public void WhenLocationIsEmpty_Exception(){
        settings.put("location", "");
        bgxConfigurationBuilder.build(settings);
    }

    @Test(expected = UnableToExecuteRequest.class)
    public void whenSettingsDoNotContainLocation_Exception(){
        settings.put("rock", "xxx.yyy");
        bgxConfigurationBuilder.build(settings);
    }

    @Test(expected = BadRequestException.class)
    public void WhenCannotParseSettingsFromFile_Exception(){
        settings.put("location", "xxx.yyy");
        bgxConfigurationBuilder.build(settings);
    }

    @Test
    public void WhenCallBuildWithCorrectFilePath_CorrectResult(){
        settings.put("location", "bgxStrategyConfig.yaml");
        Request<?> bgxConfigurationRequest = bgxConfigurationBuilder.build(settings);

    }

//    @Test(expected = BadRequestException.class)
//    public void ifSettingDoNotContaitFileWithYamlORYmlExtension_Exception(){
//        settings.put("location", "xx.exe");
//        bgxConfigurationBuilder.build(settings);
//    }

}
