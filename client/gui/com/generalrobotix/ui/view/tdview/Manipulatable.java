/**
 * @(#)Manipulatable.java
 *
 * @author  Kernel, Inc.
 * @version  1.0 (Wed Jul 13 2001)
 */

package com.generalrobotix.ui.view.tdview;
import javax.media.j3d.*;

public interface Manipulatable {
    /**
     * TransformGroup�λ��ȼ���
     *
     * @return       �����󥰥�դλ���
     */
    public TransformGroup getTransformGroupRoot();
    //public void changeAttribute(String name, String value);
}
