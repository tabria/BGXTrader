package trader.interactor;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import trader.exception.*;
import trader.requestor.Request;
import trader.strategy.bgxstrategy.configuration.BGXConfiguration;
import trader.strategy.bgxstrategy.configuration.BGXConfigurationImpl;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class BGXConfigurationBuilder {


    public static final String DEFAULT_BGX_CONFIG_FILE_LOCATION = "bgxStrategyConfig.yaml";
    public static final String LOCATION = "location";

    public Request<?> build(HashMap<String,String> settings) {
//        verifySettings(settings);
//        String location = verifyLocation(settings).trim();
//        Request<BGXConfiguration> request = new RequestImpl<>();
//       // BGXConfiguration configuration = new BGXConfigurationImpl();
//        Yaml yaml = new Yaml();
//        try(InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(location)){
//
//            Map<String, Map<String, Object>> bgxSettings = yaml.load(is);
//            for (Map.Entry<String, Map<String, Object>> entry : bgxSettings.entrySet()) {
//                String name = entry.getKey();
//                Map<String, Object> sett = entry.getValue();
//                for (Map.Entry<String, Object> value : sett.entrySet()) {
//                    String val = value.getKey();
//                    Object value1 =  value.getValue();
//                    String a ="";
//                }
//            }
//
//        } catch (IOException | RuntimeException e) {
//            throw new BadRequestException();
//        }

        return null;
    }

    private String verifyLocation(HashMap<String, String> settings) {
        if(!settings.containsKey(LOCATION))
            throw new UnableToExecuteRequest();
        String location = settings.get(LOCATION);
        if(location == null)
            throw new NullArgumentException();
        if(location.isEmpty())
            throw new EmptyArgumentException();
        return location;
    }

    private void verifySettings(HashMap<String, String> settings) {
        if(settings == null)
            throw new NullArgumentException();
        if(settings.size() != 1)
            throw new OutOfBoundaryException();
    }

}
