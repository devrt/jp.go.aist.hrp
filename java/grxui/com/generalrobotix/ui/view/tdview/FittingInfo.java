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
 * FittingInfo.java
 *
 * ���֥������Ȥξ�������Ѥ�class
 * @author  Kernel Co.,Ltd.
 * @version 1.0 (2001/3/1)
 */
package com.generalrobotix.ui.view.tdview;
import com.sun.j3d.utils.picking.*;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Sphere;

import javax.media.j3d.*;
import javax.vecmath.*;

// Connect(Fitting) ��Ϣ�Υǡ���

public class FittingInfo {
    /** �򺹤��Ƥ��� point(report ���֤���Ǥ�ľ��� TG ��ɸ) */
    Point3f p3fIntersect;
    /** �򺹤��Ƥ��� point �� Normal(report ���֤���Ǥ�ľ��� TG ��ɸ) */
    Vector3f v3fNormal;
    /** �򺹤��Ƥ��� point(world ��ɸ) */
    Point3f p3fIntersectVW;
    /** �򺹤��Ƥ��� point �� Normal(world ��ɸ) */
    Vector3f v3fNormalVW;
    /** Pick ���줿 Primitive �� Points (report ���֤���Ǥ�ľ��� TG ��ɸ) */
    Point3d[] p3dPrimitivePoints;

    /** ���ξ�̤� TG */
    TransformGroup tgFittingTarget;
    TransformGroup tgFittingTargetBefore;
    // �桼���ؤλ��Ūɽ���Τ�������ʷ�
    public final static int iNumberOfSphere = 4;
    /** addChild ���뤿��� BG */
    BranchGroup bgAddTop = new BranchGroup();
    /** ĺ���ξ����ѹ����뤿��� TG ������ */
    TransformGroup[] sphTrans = new TransformGroup [iNumberOfSphere];
    TransformGroup tgArrowTranslate = new TransformGroup();
    TransformGroup tgArrowRotate = new TransformGroup();
    /** ����������� primitives (cone) */
    Cone cone;
    /** ����������� primitives (cylinder) */
    Cylinder cylinder;

    /**
     * ���󥹥ȥ饯��
     *
     * @param   c3fArrow
     * @param   c3fSphere
     * @param   bInvertArrow
     */
    public FittingInfo(
        Color3f c3fArrow,
        Color3f c3fSphere,
        boolean bInvertArrow
    ) {
        // ����ɲ�
        Sphere[] sph = new Sphere [iNumberOfSphere];
        bgAddTop.setCapability(BranchGroup.ALLOW_DETACH);
        tgArrowTranslate.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        tgArrowTranslate.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        tgArrowTranslate.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
        tgArrowTranslate.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        tgArrowTranslate.setCapability(
            TransformGroup.ALLOW_LOCAL_TO_VWORLD_READ
        );
        tgArrowRotate.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        tgArrowRotate.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        float fScaleFactor = 0.1f;
        Appearance spherelook = new Appearance();
        Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
        Color3f white = new Color3f(1.0f, 1.0f, 1.0f);
        spherelook.setMaterial(
            new Material(c3fSphere, black, c3fSphere, white, 50.0f)
        );

        // ���Ѥ�����򤹤ޤ�
        for (int i = 0 ;i < iNumberOfSphere ; i++) {
            sph[i] = new Sphere(0.25f * fScaleFactor, spherelook);
            sph[i].setPickable(false);
            sphTrans[i] = new TransformGroup ();
            sphTrans[i].setCapability (TransformGroup.ALLOW_TRANSFORM_READ);
            sphTrans[i].setCapability (TransformGroup.ALLOW_TRANSFORM_WRITE);

            // Add sphere, transform
            bgAddTop.addChild (sphTrans[i]);
            sphTrans[i].addChild (sph[i]);
        }

        // ������ɲ�
        Appearance arrowlook = new Appearance();
        arrowlook.setMaterial(
            new Material(c3fArrow, black, c3fArrow, white, 50.0f)
        );

        cone =
            new Cone(
                0.5f * fScaleFactor,
                1.0f * fScaleFactor,
                Cone.GENERATE_NORMALS,
                arrowlook
            );
        cone.setCapability(Primitive.ENABLE_GEOMETRY_PICKING);
        for (int i = Cone.BODY; i <= Cone.CAP; i ++) {
            PickTool.setCapabilities(cone.getShape(i), PickTool.INTERSECT_FULL);
        }
        cone.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
        cone.setCapability(Primitive.ALLOW_PICKABLE_READ);
        cone.setCapability(Primitive.ALLOW_PICKABLE_WRITE);
        cylinder =
            new Cylinder(0.2f * fScaleFactor, 2.0f * fScaleFactor, arrowlook);
        cylinder.setCapability(Primitive.ENABLE_GEOMETRY_PICKING);
        cylinder.setCapability(Primitive.ALLOW_PICKABLE_READ);
        cylinder.setCapability(Primitive.ALLOW_PICKABLE_WRITE);
        for(int i = Cylinder.BODY; i <= Cylinder.BOTTOM; i ++) {
            PickTool.setCapabilities(
                cylinder.getShape(i),
                PickTool.INTERSECT_FULL
            );
        }
        cylinder.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
        // ��Ĥ����ʤ򤯤äĤ��뤿��˻��ѿ�Τۤ����ư���Ƥ���
        TransformGroup tgCone = new TransformGroup();
        Transform3D t3dCone = new Transform3D();
        TransformGroup tgCylinder = new TransformGroup();
        Transform3D t3dCylinder = new Transform3D();
        t3dCone.setTranslation(new Vector3f(0.0f,1.5f * fScaleFactor,0.0f));
        tgCone.setTransform(t3dCone);
        tgCone.addChild(cone);
        if (bInvertArrow) {
            t3dCylinder.setTranslation(
                new Vector3f(0.0f, 2.0f * fScaleFactor, 0.0f)
            );
            t3dCylinder.setRotation(
                new AxisAngle4f(new Vector3f(1.0f, 0.0f, 0.0f), (float)Math.PI)
            );
        } else {
            t3dCylinder.setTranslation(
                new Vector3f(0.0f, fScaleFactor, 0.0f)
            );
        }
        tgCylinder.addChild(tgCone);
        tgCylinder.setTransform(t3dCylinder);
        tgCylinder.addChild(cylinder);
        tgArrowRotate.addChild(tgCylinder);
        tgArrowTranslate.addChild(tgArrowRotate);
        bgAddTop.addChild(tgArrowTranslate);
    }

    /**
     * Fitting �Ѥ�ɽ���⥸�塼���ɽ�����ޤ�
     *
     */
    public void addForDisplay() {
        if(tgFittingTarget == null) {
            System.err.println("ʪ�Τ�ɽ�������оݤ� TG �����ꤵ��Ƥ��ޤ���");
        }

        // world ��ɸ�ˤ�����ˡ������Ф�
        v3fNormalVW = new Vector3f();
        if (p3dPrimitivePoints.length >= 3) {
            Point3d pt1 = new Point3d();
            Point3d pt2 = new Point3d();
            pt1.sub(p3dPrimitivePoints[1],p3dPrimitivePoints[0]);
            pt2.sub(p3dPrimitivePoints[2],p3dPrimitivePoints[1]);
            Vector3f vecOne = new Vector3f(pt1);
            Vector3f vecTwo = new Vector3f(pt2);
            // ���Ѥ� currVec �٥��ȥ�������
            v3fNormalVW.cross(vecOne,vecTwo);
            double dScale =
                Math.sqrt(
                    v3fNormalVW.x * v3fNormalVW.x +
                    v3fNormalVW.y * v3fNormalVW.y +
                    v3fNormalVW.z * v3fNormalVW.z
                );
            v3fNormalVW.x /= dScale;
            v3fNormalVW.y /= dScale;
            v3fNormalVW.z /= dScale;
        }

        // LocalToVworld ����
        Transform3D t3dSph = new Transform3D();
        Transform3D t3dLocalToVworld = new Transform3D();
        Transform3D t3dCurrent = new Transform3D();
        this.tgFittingTarget.getLocalToVworld(t3dLocalToVworld);
        this.tgFittingTarget.getTransform(t3dCurrent);

        t3dLocalToVworld.mul(t3dCurrent);
        t3dLocalToVworld.invert();

        int iCoorsNum = p3dPrimitivePoints.length;
        // report ���֤���Ǥ�ľ��� TG ��ɸ���Ѵ�����
        for (int iCount = 0; iCount < iNumberOfSphere; iCount++) {
            if(iCoorsNum <= iCount) {
                t3dSph.set(new Vector3f(p3dPrimitivePoints[iCoorsNum - 1]));
            } else {
                t3dLocalToVworld.transform(this.p3dPrimitivePoints[iCount]);
                t3dSph.set(new Vector3f(this.p3dPrimitivePoints[iCount]));
            }

            sphTrans[iCount].setTransform(t3dSph);
        }

        // Vector �� intersect �� report ���֤���Ǥ�ľ��� TG ��ɸ���Ѵ�����
        v3fNormal = new Vector3f(this.v3fNormalVW);
        t3dLocalToVworld.transform(this.v3fNormal);
        p3fIntersect = new Point3f(this.p3fIntersectVW);
        t3dLocalToVworld.transform(this.p3fIntersect);

        Transform3D t3dTranslate = new Transform3D();
        Transform3D t3dRotate = new Transform3D();
        t3dTranslate.set(new Vector3f(p3fIntersect));
        tgArrowTranslate.setTransform(t3dTranslate);

        // ��ĤΥ٥��ȥ�γ��Ѥ򼴤ˤ��Ʋ�ž����
        Vector3f v3fLine = new Vector3f(0.0f,1.0f,0.0f);
        Vector3f v3fLineOther = new Vector3f(0.0f,0.0f,1.0f);
        Vector3f v3fCross = new Vector3f();

        if (v3fNormal.y != -1.0f) {
            v3fCross.cross(v3fLine,v3fNormal);
        } else {
            v3fCross.cross(v3fLineOther,v3fNormal);
        }
        t3dRotate.set(new AxisAngle4f(v3fCross,v3fLine.angle(v3fNormal)));
        tgArrowRotate.setTransform(t3dRotate);

        if (bgAddTop.isLive()) {
            // ���Ǥ� bgAddTop ���ɤ����� TG �ˤ��äĤ��Ƥ����饷���󥰥��
            // �����ڤ�Υ��
            for(int i= 0; i < tgFittingTargetBefore.numChildren(); i ++) {
                if (bgAddTop == tgFittingTargetBefore.getChild(i)) {
                    tgFittingTargetBefore.removeChild(i);
                }
            }
        }
        bgAddTop.detach();
        tgFittingTarget.addChild(bgAddTop);
        tgFittingTargetBefore = tgFittingTarget;
    }

    /**
     * Fitting �Ѥ�ɽ���⥸�塼��������ޤ�
     *
     */
    public void removeForDisplay() {
        for (int i = 0; i < tgFittingTargetBefore.numChildren(); i ++) {
            if(bgAddTop == tgFittingTargetBefore.getChild(i)) {
                tgFittingTargetBefore.removeChild(i);
            }
        }
    }

    /**
     * setPickable
     *
     * @param   bPick
     */
    public void setPickable(boolean bPick) {
        this.cone.setPickable(bPick);
        this.cylinder.setPickable(bPick);
    }

    /**
     * setIntersectPoint
     *
     * @param   p3fIntersect
     */
    public void setIntersectPoint(Point3f p3fIntersect) {
        this.p3fIntersectVW = new Point3f(p3fIntersect);
    }

    /**
     * getIntersectPoint
     *
     * @return   Point3f
     */
    public Point3f getIntersectPoint() {
        return this.p3fIntersectVW;
    }

    /**
     * getNormalVector
     *
     * @return   Vector3f
     */
    public Vector3f getNormalVector() {
        return this.v3fNormalVW;
    }

    /**
     * setPrimitiveCoordinates
     *
     * @param   ptCoordinates
     */
    public void setPrimitiveCoordinates(Point3d[] ptCoordinates) {
        this.p3dPrimitivePoints = ptCoordinates;
    }

    /**
     * setTransformGroup
     *
     * @param   tgFittingTarget
     */
    public void setTransformGroup(TransformGroup tgFittingTarget) {
        this.tgFittingTarget = tgFittingTarget;
    }

    /**
     * �ºݤ��Ѵ��оݤ� TG ���֤��ޤ�
     *
     * @return   TransformGroup
     */
    public TransformGroup getTransformGroup() {
        return this.tgFittingTarget;
    }

    /**
     * ����� TG ���֤��ޤ�
     *
     * @return   TransformGroup
     */
    public TransformGroup getArrowTransformGroup() {
        return this.tgArrowTranslate;
    }
}
