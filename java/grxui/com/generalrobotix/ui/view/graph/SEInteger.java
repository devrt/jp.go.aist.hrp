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
 * SEInteger.java
 *
 * @author  Kernel Co.,Ltd.
 * @version 1.0 (2001/3/1)
 */

package com.generalrobotix.ui.view.graph;

public class SEInteger implements StringExchangeable {
    Integer value_;

    /**
     * ���󥹥ȥ饯��
     *
     * @param   value
     */
    public SEInteger(int value) {
        value_ = new Integer(value);
    };

    public SEInteger(String value) {
        fromString(value);
    }

    /**
     * String�ͤ����
     *
     * @return   String��
     */
    public String toString() {
        return value_.toString();
    }

    /**
     * String�ͤ���Object�����
     *
     * @param  str  String��
     * @return      Object��
     */
    public Object fromString(String str) {
        value_ = Integer.decode(str);
        return (Object)value_;
    }

    /**
     * Object�ͤ�����
     *
     * @param  value  Object��
     */
    public void setValue(Object value) {
        value_ = (Integer)value;
    }

    /**
     * String�ͤ�����
     *
     * @param  str  String��
     */
    public void setValue(String str) {
        value_ = Integer.decode(str);
    }

    /**
     * Object�ͤμ���
     *
     * @return  Object��
     */
    public Object getValue() {
        return (Object)value_;
    }

    /**
     * int�ͤμ���
     *
     * @return  int��
     */
    public int intValue() {
        return value_.intValue();
    }
};