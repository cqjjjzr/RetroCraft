package index.alchemy.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR })
@Retention(RetentionPolicy.RUNTIME)
public @interface Unsafe {
	
	String UNSAFE_API = "sun.misc.Unsafe", ASM_API = "org.objectweb.asm", REFLECT_API = "java.lang.reflect", JAVAFX_API = "com.sun.javafx", TYPE_NOT_SAFE = "Type not safe";
	
	String value() default "unknown";

}