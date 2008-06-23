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
