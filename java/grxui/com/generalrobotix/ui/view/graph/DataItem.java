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

/**
 * �ǡ��������ƥ�
 *
 * @author Kernel Inc.
 * @version 1.0 (2001/8/20)
 */
public class DataItem {

    public final String object;     // ���֥�������̾
    public final String node;       // �Ρ���̾
    public final String attribute;  // ���ȥ�ӥ塼��̾
    public final int index;         // ź��

    final String fullName_;         // ����̾
    final String attributePath_;    // ���ȥ�ӥ塼�ȥѥ�(ź���ʤ�)

    // -----------------------------------------------------------------
    // ���󥹥ȥ饯��
    /**
     * ���󥹥ȥ饯��
     *
     * @param   object      ���֥�������̾
     * @param   node        �Ρ���̾
     * @param   attribute   ���ȥ�ӥ塼��̾
     * @param   index       ź�� (ź�������׾���-1��Ϳ����)
     */
    public DataItem(
        String object,
        String node,
        String attribute,
        int index
    ) {
        this.object = object;
        this.node = node;
        this.attribute = attribute;
        this.index = index;

        StringBuffer sb;
        if (object == null) {   // ���֥�������̾�ʤ�?
            sb = new StringBuffer();    // ���֥�������̾����
        } else {    // ���֥�������̾����?
            sb = new StringBuffer(object);  // ���֥�������̾�ղ�
            sb.append(".");
        }
        sb.append(node);    // �Ρ���
        sb.append(".");
        sb.append(attribute);   // ���ȥ�ӥ塼��
        attributePath_ = sb.toString();
        if (index >= 0) {       // ¿����?
            sb.append(".");
            sb.append(index);   // ź��
        }
        fullName_ = sb.toString();
    }

    /**
     * ʸ����ɽ������
     *   ź���ޤǴޤ᤿������̾�����֤�
     *   (��: "rob1.LARM_JOINT2.absPos.2")
     *
     * @return  ʸ����ɽ��
     */
    public String toString() {
        return fullName_;
    }

    /**
     * ���ȥ�ӥ塼�ȥѥ�����
     *   ���ȥ�ӥ塼�ȥѥ����֤�(ź���ϴޤޤʤ�)
     *   (��: "rob1.LARM_JOINT2.absPos")
     *
     * @return  ���ȥ�ӥ塼�ȥѥ�
     */
    public String getAttributePath() {
        return attributePath_;
    }

    /**
     * �����ݤ�
     *
     * @return  �����ݤ�
     */
    public boolean isArray() {
        return (index >= 0);
    }
}
