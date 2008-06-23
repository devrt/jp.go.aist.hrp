/**
 * JointRotationHandler.java
 *
 * @author  Kernel, Inc.
 * @version  (Mon Nov 12 2001)
 */

package com.generalrobotix.ui.view.tdview;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.media.j3d.*;
import javax.vecmath.*;


import com.generalrobotix.ui.item.GrxModelItem;
import com.generalrobotix.ui.item.GrxModelItem.LinkInfoLocal;
import com.sun.j3d.utils.picking.*;

class JointRotationHandler extends OperationHandler {
    //--------------------------------------------------------------------
    // ���
    private static final int MODE_NONE     = 0;
    private static final int DISK_MODE     = 1;
    private static final int CYLINDER_MODE = 2;
    private static final float FACTOR = 0.004f;

    // ʪ�Τ�Vector�Ȼ�����Vector�����γ��ٰʲ��λ�CYLINDER_MODE�˰ܹԤ���
    private static final double THRESHOLD = 0.262f;

    //--------------------------------------------------------------------
    // ���󥹥����ѿ�
    private int mode_;
    private TransformGroup tgTarget_;
    private Switch bbSwitch_;
    private Switch axisSwitch_;
    private Point prevPoint_ = new Point();
    private double angle_;
    private boolean isPicked_;
    private Vector3d vectorCylinder_;

    // DISK �⡼�ɤλ��˻��Ѥ���
    //protected Point3f point000 = new Point3f(0,0,0);
    private Point3d pointTarget_;

    //--------------------------------------------------------------------
    // BehaviorHandler�μ���
    public void processPicking(MouseEvent evt, BehaviorInfo info) {
        prevPoint_.x = evt.getPoint().x;
        prevPoint_.y = evt.getPoint().y;

        isPicked_ = false;

        try {
            info.pickCanvas.setShapeLocation(prevPoint_.x, prevPoint_.y);
            PickResult pickResult = info.pickCanvas.pickClosest();
            if (pickResult == null) {
                //_disableBoundingBox();
                return;
            }
            TransformGroup tg = (TransformGroup)pickResult.getNode(
                PickResult.TRANSFORM_GROUP
            );
            if (tg == null) {
                //_disableBoundingBox();
                return;
            }

            if (tg != tgTarget_) {
                if (!_enableBoundingBox(tg, info)) {
                    return;
                }
            }
           // Point3d startPoint = info.pickCanvas.getStartPosition();
           // PickIntersection intersection =
           //     pickResult.getClosestIntersection(startPoint);
        } catch (CapabilityNotSetException ex) {
            // �⤦�Ф뤳�ȤϤʤ��Ȼפ������ɤ߹����ǥ�ˤ�äƤ�
            // �Ф뤫�⤷��ʤ��Τǡ������å��ȥ졼����ɽ�����롣
            ex.printStackTrace();
            _disableBoundingBox();
        }

        isPicked_ = true;
        //evt.consume();
    }

    public void processStartDrag(MouseEvent evt, BehaviorInfo info) {
        if (bbSwitch_ == null || !isPicked_) {
            mode_ = MODE_NONE;
            return;
        }
        //Press ���줿�������ʪ�Τγ��٤���������ߤΥ⡼�ɤ�Ƚ�ꤹ��
         // �������åȺ�ɸ�Ϥ�����ɺ�ɸ�Ϥؤ��Ѵ�
        Transform3D target2vw = new Transform3D();
        Transform3D l2vw = new Transform3D();
        Transform3D tr = new Transform3D();
        tgTarget_.getLocalToVworld(l2vw);
        tgTarget_.getTransform(tr);
        target2vw.mul(l2vw, tr);

        // �������åȤθ����Υ��ɺ�ɸ�ϤǤκ�ɸ����롣
        pointTarget_ = new Point3d();
        target2vw.transform(pointTarget_);

        // ���ɺ�ɸ�Ϥ��������ɸ�Ϥؤ��Ѵ�
        Transform3D vw2view = new Transform3D();
        tr = new Transform3D();
        l2vw = new Transform3D();
        TransformGroup tgView = info.drawable.getTransformGroupRoot();
        tgView.getLocalToVworld(l2vw);
        tgView.getTransform(tr);
        vw2view.mul(l2vw, tr);
        vw2view.invert();
        
        // (0,0,0) (0,0,1) �� point �����������Ǻ�������
        // vw2view target2vw ��Ȥä��Ѵ�����
        Point3f point000 = new Point3f(0,0,0);
        //��������Ф�

        Hashtable ht = (Hashtable)tgTarget_.getUserData();
        LinkInfoLocal l = (LinkInfoLocal)ht.get("linkInfo");
  
        vw2view.mul(target2vw);
        vw2view.transform(point000);
        Vector3d vectorView = new Vector3d(point000);
        vectorCylinder_ = new Vector3d(l.jointAxis);
        vw2view.transform(vectorCylinder_);
        
        // ��Ĥ� Vector �� angle �γ��٤����� diskAngle ��
        // ��Ӥ��⡼�ɤ�����
        double angle = vectorView.angle(vectorCylinder_);
        
        if(angle == Double.NaN) {
            System.err.println("̵�����ͤ�����ޤ���");
        }
        // ���٤� 90 �ʾ�λ��Τ��Ȥ�ͤ��� if ��ʬ����
        if (angle > Math.PI / 2.0) {
            // ξü�� DISK_MODE �ξ��֤ˤʤ��ϰϤ�����Τ��礭��
            // �ۤ����ϰϤ򾮤����ۤ��ذ�ư����׻�
            angle = Math.PI - angle;
        }
        
        if (angle < THRESHOLD) {
            mode_ = DISK_MODE;
        } else {
            mode_ = CYLINDER_MODE;
        }
    }

    public void processDragOperation(MouseEvent evt, BehaviorInfo info) {
//        if (bbSwitch_ == null) {
//            return;
//        }

        Vector2d mouseMove = new Vector2d(
            FACTOR * (evt.getPoint().getX() - prevPoint_.getX()),
            FACTOR * (evt.getPoint().getY() - prevPoint_.getY())
        );

        angle_ = 0.0;  // ��ư�̤�������뤿��Υ饸����
        // ���ߤΥ⡼�ɤ˱����ƥޥ�����ư�����ž��ľ��
        switch (mode_) {
        case DISK_MODE:
            Point2d pointMouseOnPlane = new Point2d();
            //Point3d pointTemp = new Point3d(point000);
            Point3d pointTemp = new Point3d(pointTarget_);

            Canvas3D canvas = info.pickCanvas.getCanvas();
            Transform3D vw2imagePlate = new Transform3D();
            canvas.getVworldToImagePlate(vw2imagePlate);
            vw2imagePlate.transform(pointTemp);
            canvas.getPixelLocationFromImagePlate(pointTemp, pointMouseOnPlane);

            Vector2d prev = new Vector2d(
                prevPoint_.getX() - pointMouseOnPlane.x,
                prevPoint_.getY() - pointMouseOnPlane.y
            );

            Vector2d current = new Vector2d(
                evt.getPoint().getX() - pointMouseOnPlane.x,
                evt.getPoint().getY() - pointMouseOnPlane.y
            );

            // ���̾�κ�ɸ�ϲ��˹Ԥ��ۤ� y ���ͤ���ž���Ƥ����Τ�
            // y ���ͤ��ž�����Ƥ�����
            angle_ = prev.angle(current);

            Vector3d cross = new Vector3d();
            cross.cross(vectorCylinder_, new Vector3d(prev.x, prev.y, 0.0));

            if (mouseMove.dot(new Vector2d(cross.x, cross.y)) > 0.0) {
                angle_ = - angle_;
            }
            // ���̤Τɤ��餬�椬����������ɽ�̤�����Ƥ��뤫��
            // ư�ȿž����
/*
            if (vectorCylinder_.z < 0) {
                angle_ = - angle_;
            }
*/
            break;
        case CYLINDER_MODE:
            // x,y ʿ�̤ˤ����Ƽ�������ľ�Ԥ��������Υޥ�����
            // ư������򥷥������β�ž�ˤ���
            // fDotProduct <- ���� InnerProduct �Ȥ⤤��
            // Ⱦ���ײ��� 90 �ٲ�ž����Τ� 4x4 ���Ѵ������
            // ���ޤ��Ƥ�ä��ͤ�Ĥ��ä�ľ�Ԥ���٥��ȥ��Ф�
            // �ޥ������Ф����ž�̤� DISK_MODE �����ܤˤ���
            mouseMove.x *= -2.0;
            mouseMove.y *= 2.0;

            //vectorCylinder_.normalize();
            Vector2d vectorCylinder =
                new Vector2d(- vectorCylinder_.y, vectorCylinder_.x);

            // ���Υ٥��ȥ��ľ�Ԥ��� Vector �����
            vectorCylinder.normalize();
            angle_ = vectorCylinder.dot(mouseMove);
            break;
        case MODE_NONE:
            return;
        }

        prevPoint_.x = evt.getPoint().x;
        prevPoint_.y = evt.getPoint().y;
        _jointAngleChanged(info);
        
        evt.consume();
    }

    public void processReleased(MouseEvent evt, BehaviorInfo info) {
//        if (bbSwitch_ != null) {
            evt.consume();
//        }
    }

    public void processTimerOperation(BehaviorInfo info) {}

    //--------------------------------------------------------------------
    // OperationHandler�μ���
    public void disableHandler() {
        _disableBoundingBox();
    }

    public void setPickTarget(TransformGroup tg, BehaviorInfo info) {
        if (tg != tgTarget_) {
            _enableBoundingBox(tg, info);
        }
    }

    //--------------------------------------------------------------------
    // �ץ饤�١��ȥ᥽�å�
    private void _disableBoundingBox() {
        if (bbSwitch_ != null) {
            bbSwitch_.setWhichChild(Switch.CHILD_NONE);
            axisSwitch_.setWhichChild(Switch.CHILD_NONE);
            tgTarget_ = null;
            bbSwitch_ = null;
            axisSwitch_ = null;
        }
    }

    private boolean _enableBoundingBox(TransformGroup tg, BehaviorInfo info) {
        Hashtable ht = SceneGraphModifier.getHashtableFromTG(tg);
        String objectName = (String)ht.get("objectName");
        GrxModelItem robot = (GrxModelItem)info.getManipulatable(objectName);
        LinkInfoLocal l = (LinkInfoLocal)ht.get("linkInfo");
        if (l == null)
        	return false;
        
        if (l.jointType.equals("rotate") || l.jointType.equals("slide")) {
            _disableBoundingBox();
            robot.activeLinkInfo_ = l;
            tgTarget_ = l.tg;
            bbSwitch_ = (Switch)ht.get("boundingBoxSwitch");
            axisSwitch_ = (Switch)ht.get("axisLineSwitch");
            bbSwitch_.setWhichChild(Switch.CHILD_ALL);
            axisSwitch_.setWhichChild(Switch.CHILD_ALL);
            return true;
        }
        return false;               	
    }

    private void _jointAngleChanged(BehaviorInfo info) {
        try {
            Hashtable ht = SceneGraphModifier.getHashtableFromTG(tgTarget_);
            GrxModelItem model = (GrxModelItem)ht.get("object");
            String jname = (String)ht.get("controllableJoint");
            double a = model.getJointValue(jname) +angle_;
            model.setJointValue(jname, a);
            model.setJointValuesWithinLimit();
            model.updateInitialJointValue(jname);
            model.calcForwardKinematics();
        } catch (Exception e) { 
        	e.printStackTrace();
        }
    }
}
