/*
 * Copyright (c) 2008, AIST, the University of Tokyo and General Robotix Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * General Robotix Inc.
 * National Institute of Advanced Industrial Science and Technology (AIST) 
 */
package com.generalrobotix.ui.view.graph;

import java.awt.Color;

/**
 * �ǡ��������ƥ����
 *
 * @author Kernel Inc.
 * @version 1.0 (2001/8/20)
 */
public class DataItemInfo {

    public final DataItem dataItem; // �ǡ��������ƥ�
    public Color    color;      // ��
    public String   legend;     // ����ʸ����

    // -----------------------------------------------------------------
    // ���󥹥ȥ饯��
    /**
     * ���󥹥ȥ饯��
     *
     * @param   dataItem    �ǡ��������ƥ�
     * @param   color       ��
     * @param   legend      ����ʸ����
     */
    public DataItemInfo(
        DataItem dataItem,
        Color    color,
        String   legend
    ) {
        this.dataItem = dataItem;
        this.color = color;
        this.legend = legend;
    }
}
