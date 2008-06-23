/**
 * BehaviorInfo.java
 *
 * @author  Kernel, Inc.
 * @version  1.0 (Mon Nov 12 2001)
 */
 
package com.generalrobotix.ui.view.tdview;

import com.generalrobotix.ui.GrxPluginManager;
import com.generalrobotix.ui.item.GrxModelItem;
import com.sun.j3d.utils.picking.PickCanvas;

/**
 * BehaviorHandler�˾���������뤿��Υ��饹
 */
class BehaviorInfo {
    //--------------------------------------------------------------------
    // ���󥹥����ѿ�
    private boolean timerEnabled_;
    GrxPluginManager manager_;

    final PickCanvas pickCanvas;
    //final TransformGroup tgView;
    final ThreeDDrawable drawable;

    BehaviorInfo(
        GrxPluginManager manager,
        PickCanvas pickCanvas,
        ThreeDDrawable drawable
        //TransformGroup tgView
    ) {
        manager_ = manager;
        this.pickCanvas = pickCanvas;
        this.drawable = drawable;
        //this.tgView = tgView;
    }

    void setTimerEnabled(boolean enabled) {
        timerEnabled_ = enabled;
    }

    boolean isTimerEnabled() {
        return timerEnabled_;
    }

    Manipulatable getManipulatable(String name) {
    	return (Manipulatable) manager_.getSelectedItem(GrxModelItem.class, name);
        /*
        SimulationNode node = world_.getChild(name);
        if (node instanceof Manipulatable) {
            return (Manipulatable)node;
        } else {
            return null;
        }
        */
    }
}
