/**
 * TransformChangeEvent.java
 *
 * @author  Kernel Co.,Ltd.
 * @version 1.0 (2001/3/1)
 */

package com.generalrobotix.ui.view.tdview;

import java.util.EventObject;
import javax.media.j3d.*;

public class TransformChangeEvent extends EventObject
{

    /**
     */
    // �������ݻ����� TransformGroup
    private TransformGroup tgChanged = null;

    /**
     * ���󥹥ȥ饯��
     * @param  objSource
     * @param  tgChanged
     */
    public TransformChangeEvent(Object objSource,TransformGroup tgChanged)
    {
        super(objSource);
        this.tgChanged = tgChanged;
    }

    /**
     * �ȥ�󥹥ե����॰�롼�׼���
     * @return  �ȥ�󥹥ե����॰�롼��
     */
    public TransformGroup getTransformGroup()
    {
        return tgChanged;
    }
}
