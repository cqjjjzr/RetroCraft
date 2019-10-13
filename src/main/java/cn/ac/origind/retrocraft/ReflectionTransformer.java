package cn.ac.origind.retrocraft;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class ReflectionTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(
            ClassLoader loader,
            String className,
            Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain,
            byte[] classfileBuffer) {
        if (classBeingRedefined == null || !className.equals("jdk/internal/reflect/Reflection"))
            return null;
        System.err.println("[RetroCraftAgent] Patching jdk.internal.reflect.Reflection");

        return patchUsingCtClass(loader, classfileBuffer, ctClass -> {
            ctClass.getMethod(
                    "verifyMemberAccess",
                    "(Ljava/lang/Class;Ljava/lang/Class;Ljava/lang/Class;I)Z")
                    .setBody("return true;");
            ctClass.getMethod(
                    "filter",
                    "([Ljava/lang/reflect/Member;Ljava/util/Set;)[Ljava/lang/reflect/Member;")
                    .setBody("return $1;");
        });
    }

    private byte[] patchUsingCtClass(ClassLoader loader, byte[] classfileBuffer, ExceptionConsumer action) {
        try {
            ClassPool classPool = new ClassPool();
            classPool.appendClassPath(new LoaderClassPath(this.getClass().getClassLoader()));
            classPool.appendClassPath(new LoaderClassPath(loader));
            CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));

            action.apply(ctClass);

            return ctClass.toBytecode();
        } catch (Exception e) {
            // this should never happen
            e.printStackTrace();
            return null;
        }
    }

    private interface ExceptionConsumer {
        void apply(CtClass ctClass) throws Exception;
    }
}
