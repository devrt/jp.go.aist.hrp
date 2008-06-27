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
 * InvalidClassException.java
 *
 * Ϳ����줿���饹���ְ�äƤ��뤳�Ȥ��������㳰
 *  SimulationNode�Υ��֥��饹��addChild()����Ȥ����ҥΡ��ɤȤ���
 *  ��뤳�Ȥν���ʤ����饹��Ϳ����줿���������㳰��ȯ�����롣
 *
 * @author  Kernel Co.,Ltd.
 * @version 1.0 (2001/3/1)
 */
package com.generalrobotix.ui.view.graph;

import java.lang.RuntimeException;

public class InvalidClassException extends RuntimeException {
    /**
     * ���󥹥ȥ饯��
     *
     * @param   s    �ܺ�
     */
    public InvalidClassException() {
        super();
    }

    public InvalidClassException(String s) {
        super(s);
    }
};
