/**
 * BehaviorHandler.java
 *
 * @author  Kernel, Inc.
 * @version  1.0 (Mon Nov 12 2001)
 */

package com.generalrobotix.ui.view.tdview;

import java.awt.event.*;

/**
 * �ӥإ��ӥ��Τ���Υ��٥�ȥϥ�ɥ�Υ��󥿡��ե�����
 * IseBehavior���饹����ƤӽФ���롣
 */
public interface BehaviorHandler {
    public void processPicking(MouseEvent evt, BehaviorInfo info);
    public void processStartDrag(MouseEvent evt, BehaviorInfo info);
    public void processDragOperation(MouseEvent evt, BehaviorInfo info);
    public void processReleased(MouseEvent evt, BehaviorInfo info);
    public void processTimerOperation(BehaviorInfo info);
}

