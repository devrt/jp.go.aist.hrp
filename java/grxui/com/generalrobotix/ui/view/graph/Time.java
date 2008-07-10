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
 * Time.java
 *
 * @author  Kernel Co.,Ltd.
 * @version 1.0 (2001/3/1)
 */

package com.generalrobotix.ui.view.graph;

import java.text.DecimalFormat;

public class Time {
    int msec_;
    int usec_;
    DecimalFormat format_ = new DecimalFormat("000");

    /**
     * ���󥹥ȥ饯��
     */
    public Time() {
        msec_ = 0;
        usec_ = 0;
    }

    /**
     * ���󥹥ȥ饯��
     * @param  m  �ߥ���
     * @param  u  �ޥ�������
     */
    public Time(int m, int u) {
        msec_ = m;
        usec_ = u;
    }

    /**
     * ���󥹥ȥ饯��
     * @param  time  ������
     */
    public Time(double time) {
        set(time);
    }

    public Time(float time) {
        set(time);
    }

    public Time(Time time) {
        set(time);
    }

    /**
     * ���󥹥ȥ饯��
     * @param  time  ������
     */
    public Time(long time) {
        setUtime(time);
    }

    /**
     * ��������
     * @param  time  Time���֥�������
     */
    public void set(Time time) {
        msec_ = time.msec_;
        usec_ = time.usec_;
    }

    /**
     * ��������
     * @param  time  ���֡��á�
     */
    public void set(double time) {
        long utime = (int)Math.round(time * 1000000.0);
        usec_ = (int)(utime % 1000);
        msec_ = (int)(utime / 1000);
    }

    public void set(float time) {
        long utime = (int)Math.round(time * 1000000.0f);
        usec_ = (int)(utime % 1000);
        msec_ = (int)(utime / 1000);
    }

    /**
     * ��������
     * @param  m  �ߥ���
     * @param  m  �ޥ�������
     */
    public void set(int m, int u) {
        msec_ = m;
        usec_ = u;
    }

    /**
     * ���ֲû�
     * @param  time  Time���֥�������
     */
    public void add(Time time) {
        msec_ += time.msec_;
        usec_ += time.usec_;
        if (usec_ >= 1000) {
            msec_ += usec_ / 1000;
            usec_ = usec_ % 1000;
        }
    }
    /**
     * ���ָ���
     * @param  time  Time���֥�������
     */
    public void sub(Time time) {
        msec_ -= time.msec_;
        usec_ -= time.usec_;
        if (usec_ < 0) {
            msec_ += usec_ / 1000 - 1 ;
            usec_ = usec_ % 1000;
        }
    }

    public void addUtime(int utime) {
        usec_ += utime;
        if (usec_ >= 1000) {
            msec_ += usec_ / 1000;
            usec_ = usec_ % 1000;
        }
    }

    /**
     * �ô������֤μ���
     * @return  �ô�����
     */
    public double getDouble() {
         return (double)msec_ * 0.001 + (double)usec_ * 0.000001;
    }

    /**
     *
     */
    public float getFloat() {
        return (float)msec_ * 0.001f + (float)usec_ * 0.000001f;
    }

    public boolean compare(Time time) {
        if ((msec_ == time.msec_) && (usec_ == time.usec_)) return true;
        else return false;
    }

    public long getUtime() {
        return msec_ * 1000 + usec_;
    }

    public void setUtime(long utime) {
        msec_ = (int)(utime / 1000);
        usec_ = (int)utime % 1000;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(String.valueOf(msec_ / 1000));
        buf.append('.');
        buf.append(format_.format(msec_ % 1000));
        buf.append(format_.format(usec_ % 1000));
        return buf.toString();
    }
}
