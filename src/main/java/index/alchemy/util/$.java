package index.alchemy.util;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import index.alchemy.api.annotation.Unsafe;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nullable;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class $ {
    
    private static sun.misc.Unsafe unsafe;
    
    public static sun.misc.Unsafe unsafe() { return unsafe; }
    
    private static long overrideOffset;
    
    private static Void voidInstance;
    
    public static Void voidInstance() { return voidInstance; }
    
    protected static void markUnsafe(sun.misc.Unsafe unsafe) {
        if (unsafe != null) {
            $.unsafe = unsafe;
            try {
                voidInstance = (Void) unsafe.allocateInstance(Void.class);
                Field override = AccessibleObject.class.getDeclaredField("override");
                overrideOffset = unsafe.objectFieldOffset(override);
            } catch (Exception e) { throw new RuntimeException(e); }
        }
    }
    
    private static final Map<Class<?>, Class<?>> PRIMITIVE_MAPPING = Maps.newHashMap();
    
    static {
        PRIMITIVE_MAPPING.put(byte.class, Byte.class);
        PRIMITIVE_MAPPING.put(short.class, Short.class);
        PRIMITIVE_MAPPING.put(int.class, Integer.class);
        PRIMITIVE_MAPPING.put(long.class, Long.class);
        PRIMITIVE_MAPPING.put(float.class, Float.class);
        PRIMITIVE_MAPPING.put(double.class, Double.class);
        PRIMITIVE_MAPPING.put(boolean.class, Boolean.class);
        PRIMITIVE_MAPPING.put(char.class, Character.class);
        PRIMITIVE_MAPPING.put(void.class, Void.class);
        
        markUnsafe(UnsafeHelper.unsafe());
    }
    
    private static final Class<?>[] SIMPLE = new Class[]{Character.class, String.class, Boolean.class,
            Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class};
    
    public static Map<Class<?>, Class<?>> getPrimitiveMapping() {
        return PRIMITIVE_MAPPING;
    }
    
    public static Class<?> getPrimitiveMapping(Class<?> clazz) {
        return PRIMITIVE_MAPPING.get(clazz);
    }
    
    public static boolean isPacking(Class<?> clazz) {
        return ArrayUtils.contains(SIMPLE, clazz);
    }
    
    public static boolean isSimple(Class<?> clazz) {
        return clazz.isPrimitive() || isPacking(clazz);
    }
    
    public static boolean isBasics(Class<?> clazz) {
        return isSimple(clazz) || clazz == String.class;
    }
    
    public static boolean isSubclass(Class<?> supers, Class<?> clazz) {
        do
            if (supers == clazz)
                return true;
        while ((clazz = clazz.getSuperclass()) != null);
        return false;
    }
    
    public static <T extends AccessibleObject> T setAccessible(T accessible) {
        unsafe.putBoolean(accessible, overrideOffset, true);
        return accessible;
    }
    
    public static boolean isInstance(Class<?> supers, Class<?> clazz) {
        return supers.isAssignableFrom(clazz) || getPrimitiveMapping(supers) == clazz;
    }
    
    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> result = Lists.newArrayList();
        do
            result.addAll(Arrays.asList(clazz.getDeclaredFields()));
        while ((clazz = clazz.getSuperclass()) != null);
        return result;
    }
    
    public static Field searchField(Class<?> clazz, String name) throws NoSuchFieldException {
        for (Field field : getAllFields(clazz))
            if (field.getName().equals(name))
                return field;
        throw new NoSuchFieldException(clazz + ": " + name);
    }
    
    @Nullable
    public static Method searchMethod(Class<?> clazz, Class<?>... args) {
        method_forEach:
        for (Method method : clazz.getDeclaredMethods()) {
            Class<?> ca[] = method.getParameterTypes();
            if (ca.length == args.length) {
                for (int i = 0; i < ca.length; i++) {
                    if (!isInstance(ca[i], args[i] == null ? Object.class : args[i]))
                        continue method_forEach;
                }
                return setAccessible(method);
            }
        }
        return null;
    }
    
    @Nullable
    public static Method searchMethod(Class<?> clazz, String name, Object... args) {
        method_forEach:
        for (Method method : clazz.getDeclaredMethods()) {
            Class<?> now_args[] = method.getParameterTypes();
            if (method.getName().equals(name) && now_args.length == args.length) {
                for (int i = 0; i < args.length; i++)
                    if (!isInstance(now_args[i], args[i] == null ? Object.class : args[i].getClass()))
                        continue method_forEach;
                return setAccessible(method);
            }
        }
        return null;
    }
    
    public static List<Method> searchMethod(Class<?> clazz, String name) {
        List<Method> result = Lists.newArrayList();
        for (Method method : clazz.getDeclaredMethods())
            if (method.getName().equals(name))
                result.add(setAccessible(method));
        return result;
    }
    
    @Nullable
    public static Class<?> forName(String name) {
        return forName(name, false);
    }
    
    @Nullable
    public static Class<?> forName(String name, boolean init) {
        if (name == null || name.isEmpty())
            return null;
        try {
            return Class.forName(name, init, $.class.getClassLoader());
        } catch (ClassNotFoundException e) { return null; }
    }
    
    /**
     * JQuery for Java.
     * <red>You're not expected to understand this.</red>
     * First it will determine what the first argument is.
     * If it's a Class object or a String starts with 'L', it will be regarded as the class being operated, and you're assumed to access static field/method,
     * or it will be the object being operated, and the class will be the object's class.
     * Then: 1. If the 2nd arg does not exist, then the Class object is returned(just like a {@code java.lang.Class.forName()} invocation.);
     * 2. If the 2nd arg is *NOT* started with '>', it is considered as a field name.
     * (1) If the 3rd arg is absent, then the value in the field will be read and returned(if the 1st arg is a Class then the field must be static.);
     * (2) If the 3rd arg is present: if the 2nd arg is ends '<', the field search will be only performed in the class,
     * or if it is ends with '<<' then the field will be searched in the class's parent classes.
     * And the 3rd arg will be written to the field, and then the 3rd arg will be returned.
     * 3. If the 2nd arg is started with '>', or the field search failed, or the 2nd arg is neither started with > nor ends with <,
     * it will be considered as a method invocation. The 3rd and later args will be the arguments(if present), and the invocation result will be returned.
     *
     * @param args See above.
     * @param <T>  See above.
     * @return See above.
     */
    @Nullable
    @Unsafe(Unsafe.REFLECT_API)
    @SuppressWarnings("unchecked")
    public static <T> T $(Object... args) {
        try {
            if (args.length < 1)
                return null;
            Class<?> clazz = null;
            if (args[0].getClass() == String.class) {
                String str = (String) args[0];
                if (str.startsWith("L"))
                    clazz = forName(str.substring(1), false);
                if (clazz == null)
                    clazz = String.class;
            }
            else
                clazz = args[0].getClass() == Class.class ? (Class<?>) args[0] : args[0].getClass();
            unsafe.ensureClassInitialized(clazz);
            if (args.length == 1)
                return (T) clazz;
            String name = (String) args[1];
            Object object = args[0].getClass() == clazz ? args[0] : null;
            if (!name.startsWith(">")) {
                try {
                    if (args.length == 2)
                        return (T) setAccessible(searchField(clazz, (String) args[1])).get(object);
                    if (args.length == 3 && ((String) args[1]).endsWith("<")) {
                        Field field = setAccessible(((String) args[1]).endsWith("<<") ?
                                clazz.getDeclaredField(((String) args[1]).replace("<", "")) :
                                searchField(clazz, ((String) args[1]).replace("<", "")));
                        if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers()))
                            FinalFieldHelper.set(object, field, args[2]);
                        else
                            field.set(object, args[2]);
                        return (T) args[2];
                    }
                } catch (NoSuchFieldException e) { }
            }
            name = name.replace(">", "");
            args = ArrayUtils.subarray(args, 2, args.length);
            if (name.equals("new")) {
                method_forEach:
                for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
                    Class<?> now_args[] = constructor.getParameterTypes();
                    if (now_args.length == args.length) {
                        for (int i = 0; i < args.length; i++)
                            if (args[i] != null ? !isInstance(now_args[i], args[i].getClass()) : now_args[i].isPrimitive())
                                continue method_forEach;
                        setAccessible(constructor);
                        return (T) constructor.newInstance(args);
                    }
                }
            }
            do {
                Method method = searchMethod(clazz, name, args);
                if (method != null)
                    return (T) method.invoke(object, args);
            } while ((clazz = clazz.getSuperclass()) != null);
            throw new IllegalArgumentException();
        } catch (Exception e) {
            if (e instanceof InvocationTargetException)
                ((InvocationTargetException) e).getTargetException().printStackTrace();
            throw new RuntimeException("Can't invoke(" + args.length + "): " + Joiner.on(',').useForNull("null").join(args), e);
        }
    }
    
}
