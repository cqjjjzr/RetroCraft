package cn.ac.origind.retrocraft;

import index.alchemy.util.FinalFieldHelper;

/**
 * @author Charlie Jiang
 * @since rv1
 */
public class Test {
    public final int x = 5;
    public void x() throws Exception {
        FinalFieldHelper.set(null, Test.class.getField("x"), 10);
    }
}
