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
