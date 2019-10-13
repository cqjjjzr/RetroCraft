package index.alchemy.util;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

public class UnsafeHelper {

    static {
        try {
            unsafe = UnsafeHelper.getUnsafe();
        } catch (Exception ex) {
            throw new Error("Can't get Unsafe!", ex);
        }
    }
    
    private static final Unsafe unsafe;
    
    private static Unsafe getUnsafe() throws PrivilegedActionException {
        return AccessController.doPrivileged((PrivilegedExceptionAction<Unsafe>) () -> {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            return (Unsafe) theUnsafe.get(null);
        });
    }
    
    public static Unsafe unsafe() {
        return unsafe;
    }
    
}
