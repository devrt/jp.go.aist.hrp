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
