/**
 * SimulationWorld.java
 *
 *  Canvas3D��add���뤳�Ȥ�3Dɽ������ǽ�Ǥ��뤳�Ȥ򼨤�
 *  ���󥿡��ե�������
 *  ���Υ��󥿡��ե�������DND�򥵥ݡ��Ȥ��뤳�Ȥ�����Ȥ���
 *  ���ᡢSerializable���󥿥ե�������Ѿ����롣
 *
 * @author  Kernel Co.,Ltd.
 * @version 1.0 (2001/3/1)
 */
package com.generalrobotix.ui.view.tdview;

import java.io.Serializable;
import javax.media.j3d.*;

public interface ThreeDDrawable extends Serializable {
    /**
     *
     * @param   bg
     */
    public String getName();
    public String getFullName();
    public void attach(BranchGroup bg);

    public void setViewMode(int mode);

    public void setDirection(int dir);

    public void setTransform(Transform3D transform);

    //public void addViewTransformListener(ViewTransformListener listener);

    //public void removeViewTransformListener(ViewTransformListener listener);

    public TransformGroup getTransformGroupRoot();

    public ViewInfo getViewInfo();

/*
    public void changeAttribute(String attrName, String value)
        throws
            NoSuchAttributeException,
            InvalidAttributeException,
            NotEditableAttributeException;
*/
}
