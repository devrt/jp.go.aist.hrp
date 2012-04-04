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
 * ProjectErrorHandler.java
 *
 * @author  Kernel, Inc.
 * @version  1.0
 */

package com.generalrobotix.ui.view.tdview;

/**
 * �ץ������ȥ��ɻ��˵��������顼����館�ơ����顼�����򤹤뤿��Υϥ�ɥ�
 */
public interface ProjectErrorHandler {
    /**
     * ��ǥ�ե������ɤ߹��߻��˵����륨�顼��ϥ�ɥ롣
     *
     * @param  errorNo    ���顼�ֹ档
     *                    ProjectManager.FILE_NOT_FOUND,
     *                    ProjectManager.FILE_IO_ERROR,
     *                    ProjectManager.SERVER_ERROR,
     *                    ProjectManager.FILE_FORMAT_ERROR
     *                    �Τ����줫��
     * @param  objectType ���֥������Ȥμ��̡�
     *                    ProjectManager.ROBOT_NODE,ProjectManager.ENVIRONMENT_NODE
     *                    �Τ����줫��
     * @param  objectName ���֥�������̾
     * @param  url        ���ɤ˼��Ԥ���url
     *
     * @return ���顼�ϥ�ɥ餫��ȴ������ɤ����뤫��ProjectManager.RELOAD,
     *         ProjectManager.SKIP_LOADING, ProjectManager.ABORT�Τ����줫��
     */
    public int loadVRMLFailed(
        int errorNo, 
        int objectType,
        String objectName,
        String url
    );

    /**
     * @return �ɤ�ľ������ο�����url
     */
    public String getURL();

    /**
     * ���ȥ�ӥ塼�Ȥ�ȿ�Ǥ���Ȥ��˵����륨�顼��ϥ�ɥ롣
     *
     * @param errorNo    ���顼�ֹ档
     *                   ProjectManager.COLLISION_PAIR_LINK_FAILURE,
     *                   ProjectManager.VLINK_FAILURE
     *                   �Τ����줫��
     * @param objectName ���顼�ε����ä����֥������Ȥ�̾��
     */
    public void reflectAttributeError(int errorNo, String objectName);
}
