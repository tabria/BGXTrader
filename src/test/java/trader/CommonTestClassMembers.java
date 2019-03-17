package trader;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;

public class CommonTestClassMembers {

    public static final BigDecimal ASK = BigDecimal.ONE;
    public static final BigDecimal BID = BigDecimal.TEN;



    public Object changeFieldObject(Object object, String fieldName, Object value) {
        try {
            Field field = getField(object, fieldName);
            field.set(object, value);
            return field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Object extractFieldObject(Object object, String fieldName) {
        try {
            Field field = getField(object, fieldName);
            return field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void changePrivateFinalField(Object object, String fieldName, Object newValue) throws NoSuchFieldException, IllegalAccessException {
        Field accountIDField = getField(object, fieldName);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(accountIDField, accountIDField.getModifiers() & ~Modifier.FINAL);
        accountIDField.set(object, newValue);
    }

    private Field getField(Object object, String fieldName) throws NoSuchFieldException {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }

}