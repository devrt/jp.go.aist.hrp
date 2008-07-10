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
 * BadLinkStructureException.java
 *
 * ��󥯹�¤���������ʤ����Ȥ��������㳰
 *
 * @author  Kernel Co.,Ltd.
 * @version 1.0 (2001/3/1)
 */

package com.generalrobotix.ui.view.tdview;

public class BadLinkStructureException extends Exception {
    /**
     * ���󥹥ȥ饯��
     *
     * @param   String str    �ܺ�
     */
    public BadLinkStructureException() {
    super();
    }

    public BadLinkStructureException(String str) {
        super(str);
    }
}
