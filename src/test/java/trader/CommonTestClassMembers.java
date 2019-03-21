package trader;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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

    public Object changeFieldInSuperObject(Object object, String fieldName, Object value) {
        try {
            Field field = getSuperField(object, fieldName);
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
        Field field = getField(object, fieldName);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(object, newValue);
    }

    private Field getField(Object object, String fieldName) throws NoSuchFieldException {
        Field field = null;
        try {
            field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
        } catch (Exception e){
            field = object.getClass().getSuperclass().getDeclaredField(fieldName);
            field.setAccessible(true);
        }
        return field;
    }

    private Field getSuperField(Object object, String fieldName) throws NoSuchFieldException {
        Field field = object.getClass().getSuperclass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }

    public Method getPrivateMethodForTest(Object object, String methodName, Class<?>...params) throws NoSuchMethodException {
        Method declaredMethod = object.getClass().getDeclaredMethod(methodName, params);
        declaredMethod.setAccessible(true);
        return declaredMethod;
    }

    public Method getPrivateMethodForTest(Class<?> objectClass, String methodName, Class<?>...params) throws NoSuchMethodException {
        Method declaredMethod = objectClass.getDeclaredMethod(methodName, params);
        declaredMethod.setAccessible(true);
        return declaredMethod;
    }

}