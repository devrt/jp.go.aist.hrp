/**
 * SEString.java
 *
 * @author  Kernel Co.,Ltd.
 * @version 1.0 (2001/3/1)
 */

package com.generalrobotix.ui.view.graph;

public class SEString implements StringExchangeable {
    String value_;

    /**
     * ���󥹥ȥ饯��
     *
     * @param   value
     */
    public SEString(String value) {
        value_ = value;
    };

    /**
     * String�ͤ����
     *
     * @return   String��
     */
    public String toString() {
        return value_;
    }

    /**
     * String�ͤ���Object�ͤ����
     *
     * @param  str  String��
     * @return      Object��
     */
    public Object fromString(String str) {
        value_ = str;
        return (Object)value_;
    }

    /**
     * Object�ͤ�����
     *
     * @param  value  Object��
     */
    public void setValue(Object value) {
        value_ = (String)value;
    }

    /**
     * String�ͤ�����
     *
     * @param  str  String��
     */
    public void setValue(String str) {
        value_ = str;
    }

    /**
     * Object�ͤμ���
     *
     * @return    Object��
     */
    public Object getValue() {
        return (Object)value_;
    }
};
