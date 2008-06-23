/**
 * InvKinemaChangeListener.java
 *
 *   TransformGroup ���ѹ��� BehaviorManager �����Τ���
 *   ����˻Ȥ� Listener Interface
 *
 * @author  Kernel Co.,Ltd.
 * @version 1.0 (2001/3/1)
 */
package com.generalrobotix.ui.view.tdview;

import java.util.EventListener;

public interface InvKinemaChangeListener extends EventListener
{
    /**
     * ���󥹥ȥ饯��
     *
     * @param   event    ���٥��
     */
    public void invKinemaChanged(TransformChangeEvent event);
}
