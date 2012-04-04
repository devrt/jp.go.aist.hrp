/*
 * Copyright (c) 2008, AIST, the University of Tokyo and General Robotix Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * General Robotix Inc.
 * National Institute of Advanced Industrial Science and Technology (AIST) 
 */
/**
 * ObjectToolBar.java
 *
 * @author  Kernel Co.,Ltd.
 * @version 2.0 (Thu Nov 29 2001)
 */

package com.generalrobotix.ui.view.tdview;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import com.generalrobotix.ui.util.IconProperties;
import com.generalrobotix.ui.util.MessageBundle;



/**
 * @history 1.0 (2001/3/1)
 * @history 2.0 (Thu Nov 29 2001)
 */
public class ObjectToolBar extends JToolBar {
    // ���̥⡼��
    public static final int DISABLE_MODE    = 0;
    public static final int OBJECT_MODE     = 1;
    public static final int FITTING_MODE    = 2;
    public static final int FROM_JOINT_MODE = 3;
    public static final int INV_KINEMA_MODE = 4;

    public int mode_;

    ActionListener listener_;

    JButton addRobot_;
    JButton addEnv_;
    JButton remove_;
    JToggleButton objTrans_;
    JToggleButton objRot_;
    JToggleButton joint_;
    JToggleButton fitSrc_;
    JToggleButton fitDist_;
    JButton fit_;
    JToggleButton fromJoint_;
    JToggleButton invKinTrans_;
    JToggleButton invKinRot_;

    PopButtonGroup group_;

    /**
     * ���󥹥ȥ饯��
     *
     * @param   properties
     * @param   messages
     * @param   buttonGroup
     */
    public ObjectToolBar() {
        super(MessageBundle.get("tool.object.title"));
        Dimension size =
            new Dimension(IconProperties.WIDTH, IconProperties.HEIGHT);

        group_ = new PopButtonGroup();
        group_.addActionListener(GUIAction.OPERATION_DISABLE);

        // ��ܥå��ɲ�
        addRobot_ = new JButton(GUIAction.ADD_ROBOT);
        addRobot_.setPreferredSize(size);
        addRobot_.setMaximumSize(size);
        addRobot_.setMinimumSize(size);
//        add(addRobot_);

        // �Ķ��ɲ�
        addEnv_ = new JButton(GUIAction.ADD_ENV);
        addEnv_.setPreferredSize(size);
        addEnv_.setMaximumSize(size);
        addEnv_.setMinimumSize(size);
//        add(addEnv_);

        // ���֥������Ⱥ��
        remove_ = new JButton(GUIAction.REMOVE_OBJECT);
        remove_.setPreferredSize(size);
        remove_.setMaximumSize(size);
        remove_.setMinimumSize(size);
//        add(remove_);

        addSeparator();

        // ���֥��������¿ʰ�ư
        objTrans_ = new JToggleButton(GUIAction.OBJECT_TRANSLATION);
        objTrans_.setActionCommand(GUIAction.OBJECT_TRANSLATION.getActionCommand());
        objTrans_.setPreferredSize(size);
        objTrans_.setMaximumSize(size);
        objTrans_.setMinimumSize(size);
        add(objTrans_);
        group_.add(objTrans_);

        // ���֥������Ȳ�ž��ư
        objRot_ = new JToggleButton(GUIAction.OBJECT_ROTATION);
        objRot_.setPreferredSize(size);
        objRot_.setMaximumSize(size);
        objRot_.setMinimumSize(size);
        add(objRot_);
        group_.add(objRot_);

        // ���������
        joint_ = new JToggleButton(GUIAction.JOINT_ROTATION);
        joint_.setPreferredSize(size);
        joint_.setMaximumSize(size);
        joint_.setMinimumSize(size);
        add(joint_);
        group_.add(joint_);

        addSeparator();

        // �ե��åȸ�����
        fitSrc_ = new JToggleButton(GUIAction.FITTING_SRC);
        fitSrc_.setPreferredSize(size);
        fitSrc_.setMaximumSize(size);
        fitSrc_.setMinimumSize(size);
        add(fitSrc_);
        group_.add(fitSrc_);

        // �ե��å�������
        fitDist_ = new JToggleButton(GUIAction.FITTING_DEST);
        fitDist_.setPreferredSize(size);
        fitDist_.setMaximumSize(size);
        fitDist_.setMinimumSize(size);
        add(fitDist_);
        group_.add(fitDist_);

        // �ե��å�
        fit_ = new JButton(GUIAction.DO_FIT);
        fit_.setPreferredSize(size);
        fit_.setMaximumSize(size);
        fit_.setMinimumSize(size);
        add(fit_);

        addSeparator();

        // �������祤�������
        fromJoint_ = new JToggleButton(GUIAction.INV_KINEMA_FROM);
        fromJoint_.setPreferredSize(size);
        fromJoint_.setMaximumSize(size);
        fromJoint_.setMinimumSize(size);
        add(fromJoint_);
        group_.add(fromJoint_);

        // ��󥯲�ž��ư
        invKinRot_ = new JToggleButton(GUIAction.INV_KINEMA_ROT);
        invKinRot_.setPreferredSize(size);
        invKinRot_.setMaximumSize(size);
        invKinRot_.setMinimumSize(size);
        add(invKinRot_);
        group_.add(invKinRot_);

        // ����¿ʰ�ư
        invKinTrans_ = new JToggleButton(GUIAction.INV_KINEMA_TRANS);
        invKinTrans_.setPreferredSize(size);
        invKinTrans_.setMaximumSize(size);
        invKinTrans_.setMinimumSize(size);
        add(invKinTrans_);
        group_.add(invKinTrans_);

        //setMode(DISABLE_MODE);
        selectNone();
    }

    /**
     * �⡼������
     *
     * @param   mode    �⡼��
     */
    public void setMode(int mode) {
        mode_ = mode;
        switch (mode_) {
        case DISABLE_MODE:
            addRobot_.setEnabled(false);
            addEnv_.setEnabled(false);
            remove_.setEnabled(false);
            objTrans_.setEnabled(false);
            objRot_.setEnabled(false);
            joint_.setEnabled(false);
            fitSrc_.setEnabled(false);
            fitDist_.setEnabled(false);
            fit_.setEnabled(false);
            fromJoint_.setEnabled(false);
            invKinTrans_.setEnabled(false);
            invKinRot_.setEnabled(false);
            break;
        case OBJECT_MODE:
            addRobot_.setEnabled(true);
            addEnv_.setEnabled(true);
            remove_.setEnabled(true);
            objTrans_.setEnabled(true);
            objRot_.setEnabled(true);
            joint_.setEnabled(true);
            fitSrc_.setEnabled(true);
            fitDist_.setEnabled(true);
            fit_.setEnabled(false);
            fromJoint_.setEnabled(true);
            invKinTrans_.setEnabled(false);
            invKinRot_.setEnabled(false);
            break;
        case FITTING_MODE:
            addRobot_.setEnabled(false);
            addEnv_.setEnabled(false);
            remove_.setEnabled(false);
            objTrans_.setEnabled(false);
            objRot_.setEnabled(false);
            joint_.setEnabled(false);
            fitSrc_.setEnabled(true);
            fitDist_.setEnabled(true);
            fit_.setEnabled(true);
            fromJoint_.setEnabled(false);
            invKinTrans_.setEnabled(false);
            invKinRot_.setEnabled(false);
            break;
        case FROM_JOINT_MODE:
            addRobot_.setEnabled(false);
            addEnv_.setEnabled(false);
            remove_.setEnabled(false);
            objTrans_.setEnabled(false);
            objRot_.setEnabled(false);
            joint_.setEnabled(false);
            fitSrc_.setEnabled(false);
            fitDist_.setEnabled(false);
            fit_.setEnabled(false);
            fromJoint_.setEnabled(true);
            invKinTrans_.setEnabled(false);
            invKinRot_.setEnabled(false);
            break;
        case INV_KINEMA_MODE:
            addRobot_.setEnabled(false);
            addEnv_.setEnabled(false);
            remove_.setEnabled(false);
            objTrans_.setEnabled(false);
            objRot_.setEnabled(false);
            joint_.setEnabled(false);
            fitSrc_.setEnabled(false);
            fitDist_.setEnabled(false);
            fit_.setEnabled(false);
            fromJoint_.setEnabled(true);
            invKinTrans_.setEnabled(true);
            invKinRot_.setEnabled(true);
            break;
        }
    }

    public String getSelectedButton() {
         ButtonModel model = group_.getSelection();
         System.out.println("selectedButton=" + model.getActionCommand());
         return model.getActionCommand();
    }

    public void selectNone() {
        group_.selectNone();
    }
}
