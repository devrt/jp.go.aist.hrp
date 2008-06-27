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
 * StringExchangeException.java
 *
 * StringExchangeable������������饹���Ѵ��Ǥ��ʤ�ʸ�����Ϳ����줿����
 * ȯ�������㳰
 *
 * @author  Kernel Co.,Ltd.
 * @version 1.0 (2001/3/1)
 */

package com.generalrobotix.ui.view.graph;

import java.lang.RuntimeException;

public class StringExchangeException extends RuntimeException {
    /**
     * ���󥹥ȥ饯��
     *
     * @param   str    �ܺ�
     */
    public StringExchangeException() {
        super();
    }

    public StringExchangeException(String str) {
        super(str);
    }
};
