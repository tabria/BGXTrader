package trader;

import java.lang.reflect.Field;
import java.math.BigDecimal;

public class CommonTestClassMembers {

    public static final BigDecimal ASK = BigDecimal.ONE;
    public static final BigDecimal BID = BigDecimal.TEN;



    public Object changeFieldObject(Object object, String fieldName, Object value) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, value);
            return field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Object extractFieldObject(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}