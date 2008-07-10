/*
 * Copyright (c) 2008, AIST, the University of Tokyo and General Robotix Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * General Robotix Inc.
 * National Institute of Advanced Industrial Science and Technology (AIST) 
 */
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
