package twittertrack.tests;

import java.lang.reflect.Field;

public class ReflectionUtil {

    public static <T> T setProperty(String name, Object value, T instance) {
        Class<?> cls = instance.getClass();
        Field field = null;
        try {
            field = cls.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            throw new WrapperException(e);
        }
        field.setAccessible(true);
        try {
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new WrapperException(e);
        }
        return instance;
    }

}
