/**
 * FileOpenFailException.java
 *
 * @author  Kernel Co.,Ltd.
 * @version 1.0 (2001/3/1)
 */

package com.generalrobotix.ui.view.graph;

public class FileOpenFailException extends Exception {
    /**
     * ���󥹥ȥ饯��
     *
     * @param   String str    �ܺ�
     */
    public FileOpenFailException() {
    super();
    }

    public FileOpenFailException(String str) {
        super(str);
    }
}
