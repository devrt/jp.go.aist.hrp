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

import java.awt.*;

/**
 * �����󥯥饹
 *
 * @author Kernel Inc.
 * @version 1.0 (2001/8/20)
 */
public class AxisInfo {

    // -----------------------------------------------------------------
    // ���󥹥����ѿ�
    public double base;             // ����ü(��ü)����
    public double extent;           // ����ü(��ü)���鼴��ü(��ü)�ޤǤ���
    public double max;              // ������(�����ͤ���礭���ƥ��å����٥�򿶤�ʤ�)
    public double min;              // �Ǿ���(������̤���Υƥ��å����٥�򿶤�ʤ�)
    public boolean maxLimitEnabled; // �ƥ��å����٥�κ��������¤�ͭ���ˤ���
    public boolean minLimitEnabled; // �ƥ��å����٥�κǾ������¤�ͭ���ˤ���
    public Color color;             // ���ο�
    public double factor;           // �ǡ����ˤ��η�����ݤ��ƥץ�åȤ���
    public double tickEvery;        // �ƥ��å��ֳ�
    public int tickLength;          // �ƥ��å���Ĺ��
    public double labelEvery;       // ��٥�ֳ�
    public String labelFormat;      // ��٥�ե����ޥå�
    public Font labelFont;          // ��٥�ե����
    public Color labelColor;        // ��٥뿧
    public Font unitFont;           // ñ�̥�٥�ե����
    public String unitLabel;        // ñ�̥�٥�
    public Color unitColor;         // ñ�̿�
    public int unitXOfs;
    public int unitYOfs;
    public double gridEvery;        // ����åɴֳ�
    public Color gridColor;         // ����åɤο�
    public boolean markerVisible;   // �ޡ�������ɽ���ե饰
    public double markerPos;        // �ޡ�������ɽ������
    public Color markerColor;       // �ޡ����ο�

    // -----------------------------------------------------------------
    // ���󥹥ȥ饯��
    /**
     * ���󥹥ȥ饯��
     *
     * @param   base    double  ����ü(��ü)����
     * @param   extent  double  ����ü(��ü)���鼴��ü(��ü)�ޤǤ���
     */
    public AxisInfo(
        double base,
        double extent
    ) {
        this.base = base;
        this.extent = extent;
        max = 0.0;
        min = 0.0;
        maxLimitEnabled = false;
        minLimitEnabled = false;
        color = Color.white;
        factor = 1.0;
        tickEvery = 0.0;
        tickLength = 3;
        labelEvery = 0.0;
        labelFormat = "0";
        labelFont = new Font("monospaced", Font.PLAIN, 10);
        labelColor = Color.white;
        unitFont = new Font("dialog", Font.PLAIN, 10);
        unitLabel = "";
        unitColor = Color.white;
        unitXOfs = 0;
        unitYOfs = 0;
        gridEvery = 0.0;
        gridColor = Color.darkGray;
        markerVisible = false;
        markerPos = 0.0;
        markerColor = Color.lightGray;
    }
}
