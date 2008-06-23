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
