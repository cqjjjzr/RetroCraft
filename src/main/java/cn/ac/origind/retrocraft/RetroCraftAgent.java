package cn.ac.origind.retrocraft;

import index.alchemy.util.ModuleHelper;

import java.lang.instrument.Instrumentation;

public class RetroCraftAgent {
    public static void premain(
            String agentArgs, Instrumentation inst) {
        System.err.println("[RetroCraftAgent] In premain()");
        System.err.println("[RetroCraftAgent] Installing patches...");
        inst.addTransformer(new ReflectionTransformer(), true);
        try {
            inst.retransformClasses(Class.forName("jdk.internal.reflect.Reflection"));
        } catch (Throwable e) {
            System.err.println("[RetroCraftAgent] Retransforming failed!");
            e.printStackTrace();
            return;
        }
        inst.addTransformer(new ForgeTransformer(), true);
        System.err.println("[RetroCraftAgent] Installed.");
        System.err.println("[RetroCraftAgent] Opening all modules...");
        ModuleHelper.openAllModule();
    }
}
