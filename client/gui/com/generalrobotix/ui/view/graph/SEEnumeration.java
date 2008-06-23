/**
 * SEEnumeration.java
 *
 * @author  Kernel Co.,Ltd.
 * @version 1.0 (2001/3/1)
 */

package com.generalrobotix.ui.view.graph;

import java.util.StringTokenizer;

public class SEEnumeration implements StringExchangeable {
    String[] item_;
    int selection_;
    
    public SEEnumeration(String[] s, int selection) {
        item_ = s;
        selection_ = selection;
    }

    /**
     *  ����ޤǶ��ڤ�줿ʸ���󤫤顢item_����������
     */
    public SEEnumeration(String str) {
        StringTokenizer tokenizer = new StringTokenizer(str, ",");
        item_ = new String[tokenizer.countTokens()];
        for (int i = 0; tokenizer.hasMoreTokens(); i ++) {
            item_[i] = tokenizer.nextToken();
        }
        selection_ = 0;
    }

    /**
     * String�ͤ����
     *
     * @return   String��
     */
    public String toString() {
        return item_[selection_];
    }

    /**
     * String�ͤ���Object�����
     *
     * @param  str  String��
     * @return      Object��
     */
    public Object fromString(String str) {
        if (str == null) {
            selection_ = 0;
            return item_[selection_];
        }

        for(int i =0; i < item_.length; i ++) {
            if (str.equals(item_[i])) {
                selection_ = i;
                break;
            }
        }
        return item_[selection_];
    }

    /**
     * Object�ͤ�����
     *
     * @param  value  Object��
     */

    public void setValue(Object value) {
        fromString((String)value);
    }

    /**
     * String�ͤ�����
     *
     * @param  str  String��
     */
    public void setValue(String str) {
        fromString((String)str);
    }

    /**
     * Object�ͤμ���
     *
     * @return  Object��
     */
    public Object getValue() {
        return  item_[selection_];
    }

    /**
     * String[]�ͤμ���
     *
     * @return  String[]��
     */
    public String[] getSelectionNames() {
        return item_;
    }
    /**
     * Index�ͤμ���
     *
     * @return  String[]��
     */
    public int getSelectedIndex() {
        return selection_;
    }
}
