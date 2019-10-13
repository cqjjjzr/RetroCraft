package index.alchemy.util;

import index.alchemy.api.annotation.Unsafe;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

public class FinalFieldHelperClass {
    static final sun.misc.Unsafe unsafe = $.unsafe();

    @Unsafe(Unsafe.UNSAFE_API)
    public static void set(@Nullable Object obj, Field field, @Nullable Object value) throws Exception {
        if (obj == null)
            setStatic(field, value);
        else
            unsafe.putObject(obj, unsafe.objectFieldOffset(field), value);
    }

    @Unsafe(Unsafe.UNSAFE_API)
    public static void setStatic(Field field, @Nullable Object value) throws Exception {
        unsafe.ensureClassInitialized(field.getDeclaringClass());
        unsafe.putObject(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field), value);
    }

    @Unsafe(Unsafe.UNSAFE_API)
    @SuppressWarnings("unchecked")
    public static <T> T get(@Nullable Object obj, Field field) throws Exception {
        if (obj == null)
            return getStatic(field);
        else
            return (T) unsafe.getObject(obj, unsafe.objectFieldOffset(field));
    }

    @Unsafe(Unsafe.UNSAFE_API)
    @SuppressWarnings("unchecked")
    public static <T> T getStatic(Field field) throws Exception {
        unsafe.ensureClassInitialized(field.getDeclaringClass());
        return (T) unsafe.getObject(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field));
    }
}
