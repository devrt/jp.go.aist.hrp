/** 
 * GUIStatus.java
 *
 * @author  Kernel, Inc.
 * @version  1.0 (Wed Nov 28 2001)
 */

package com.generalrobotix.ui.view.graph;

/**
 * GUI�Υ⡼�ɴ�����Ԥ����饹��
 *
 * @history  1.0 (Wed Nov 28 2001)
 */
public class GUIStatus {
    //--------------------------------------------------------------------
    // ���
    public static final int EDIT_MODE     = 1;
    public static final int EXEC_MODE     = 2;
    public static final int PLAYBACK_MODE = 3;

    private static int mode_;

    public static int getMode() {
        return mode_;
    }

    static void setMode(int mode) {
        mode_ = mode;
    }
}
