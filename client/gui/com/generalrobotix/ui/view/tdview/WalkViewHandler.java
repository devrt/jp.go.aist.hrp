/**
 * WalkViewHandler.java
 *
 * @author  Kernel, Inc.
 * @version  1.0 (Mon Nov 12 2001)
 */

package com.generalrobotix.ui.view.tdview;

import java.awt.*;
import java.awt.event.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.picking.*;

class WalkViewHandler extends ViewHandler {
    //--------------------------------------------------------------------
    // ���
    private static final float ROTATION_FACTOR = 0.02f;
    private static final float TRANSLATION_FACTOR = 0.005f;
    private static final float ZOOM_FACTOR = 0.0005f;
    private static final float PAN_FACTOR = 0.0001f;

    //--------------------------------------------------------------------
    // ���󥹥����ѿ�
    private Point3f pickPoint_;
    private Point prevPoint_;
    private float zoom_;
    private float pan_;

    WalkViewHandler() {
        pickPoint_ = new Point3f();
    }

    //--------------------------------------------------------------------
    // BehaviorHandler�μ���
    public void processPicking(MouseEvent evt, BehaviorInfo info) {
        prevPoint_ = new Point(evt.getPoint());

        info.pickCanvas.setShapeLocation(prevPoint_.x, prevPoint_.y);
        try {
            PickResult pickResult = info.pickCanvas.pickClosest();
            if (pickResult != null) {
                Point3d startPoint = info.pickCanvas.getStartPosition();
                PickIntersection intersection =
                    pickResult.getClosestIntersection(startPoint);
                pickPoint_ = new Point3f(intersection.getPointCoordinatesVW());
            }
        } catch (CapabilityNotSetException ex) {
            return;
        }
    }

    public void processStartDrag(MouseEvent evt, BehaviorInfo info) {
        if (mode_[getMouseButtonMode(evt)] == ZOOM_MODE) {
            info.setTimerEnabled(true);
            System.out.println("timer start");
        }
    }

    public void processDragOperation(MouseEvent evt, BehaviorInfo info) {
        switch (mode_[getMouseButtonMode(evt)]) {
        case ROTATION_MODE:
            _rotation(evt, info);
            prevPoint_ = new Point(evt.getPoint());
            break;
        case TRANSLATION_MODE:
            _translation(evt, info);
            prevPoint_ = new Point(evt.getPoint());
            break;
        case ZOOM_MODE:
            _zoom(evt, info);
            break;
        }
    }

    public void processReleased(MouseEvent evt, BehaviorInfo info) {}

    public void processTimerOperation(BehaviorInfo info) {
        Transform3D tr = new Transform3D();
        Transform3D trView = new Transform3D();
        TransformGroup tgView = info.drawable.getTransformGroupRoot();
        tgView.getTransform(trView);

        tr.setRotation(
	    new AxisAngle4f(
	        new Vector3f(0.0f, 1.0f, 0.0f),
	        pan_
	    )
        );
        tr.setTranslation(new Vector3f(0.0f, 0.0f, zoom_));

        trView.mul(tr);
        info.drawable.setTransform(trView);
    }

    //--------------------------------------------------------------------
    // �ץ饤�١��ȥ᥽�å�
    private void _rotation(MouseEvent evt, BehaviorInfo info) {
        double dx =
            ROTATION_FACTOR * (prevPoint_.getX() - evt.getPoint().getX());
        double dy = 
            ROTATION_FACTOR * (prevPoint_.getY() - evt.getPoint().getY());

        // �����Х��ɸ���������ɸ�ؤ��Ѵ����Ȥ�롣
        TransformGroup tgView = info.drawable.getTransformGroupRoot();
        Transform3D trView = new Transform3D();   // �������Ѵ�
        tgView.getTransform(trView);

        // �����Х��ɸ�Ϥ��������ɸ�Ϥؤ��Ѵ�
        Transform3D vw2view = new Transform3D();
        tgView.getLocalToVworld(vw2view);
        vw2view.mul(trView);
        vw2view.invert();

        // �������鸫���������åȥݥ����
        Point3d pointTarget = new Point3d(pickPoint_);
        vw2view.transform(pointTarget);

        // ������ɸ�Ϥ򥿡����åȥݥ���Ȥޤǰ�ư
        Vector3d ray = new Vector3d(pointTarget);  // �����٥��ȥ�
        Transform3D trMove = new Transform3D();
        trMove.set(ray);
        trView.mul(trMove);

        // ������ɸ�Ϥ��ž
        Transform3D tr = new Transform3D();
        Vector3d zAxis = new Vector3d(0.0, 0.0, 1.0);
        vw2view.transform(zAxis);
        tr.set(new AxisAngle4d(zAxis, dx));
        trView.mul(tr);

        Vector3d xAxis = new Vector3d(1.0, 0.0, 0.0);
        tr.set(new AxisAngle4d(xAxis, dy));
        trView.mul(tr);

        // ������ɸ�Ϥ򥿡����åȥݥ���Ȥ���Υ��
        trView.mulInverse(trMove);

        info.drawable.setTransform(trView);
    }

    private void _translation(MouseEvent evt, BehaviorInfo info) {
        float fdx =
            TRANSLATION_FACTOR *
                (float)(evt.getPoint().getX() - prevPoint_.getX());
        float fdy =
            - TRANSLATION_FACTOR *
                (float)(evt.getPoint().getY() - prevPoint_.getY());

        Transform3D tr = new Transform3D();
        Transform3D trView = new Transform3D();
        TransformGroup tgView = info.drawable.getTransformGroupRoot();
        tgView.getTransform(trView);
        tr.set(new Vector3f(fdx, fdy, 0.0f));
        trView.mul(tr);
        info.drawable.setTransform(trView);
    }

    private void _zoom(MouseEvent evt, BehaviorInfo info) {
        pan_ =
            - PAN_FACTOR * (float)(evt.getPoint().getX() - prevPoint_.getX());
        zoom_ =
            - ZOOM_FACTOR * (float)(evt.getPoint().getY() - prevPoint_.getY());
    }
}
