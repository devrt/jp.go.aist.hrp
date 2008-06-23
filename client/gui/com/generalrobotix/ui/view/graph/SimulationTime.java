/**
 * SimulationTime.java
 *
 * SimulationTime���饹�ϡ����ߥ�졼�����˻��Ѥ�����־����������ޤ���
 * ���ƥå׻��֤ϥޥ�������
 *
 * @author  Kernel Co.,Ltd.
 * @version 1.0 (2001/3/1)
 */

package com.generalrobotix.ui.view.graph;
public class SimulationTime {
    protected Time totalTime_;
    protected Time currentTime_;
    protected Time startTime_;
    protected Time timeStep_;
    protected Time viewUpdateStep_;

    /**
     * ���󥹥ȥ饯��
     *
     * @param   totalTime    ��׻��֡ʥߥ��á�
     * @param   timeStep     ���ƥå׻��֡ʥޥ������á�
     */
    public SimulationTime() {
        totalTime_ = new Time();
        timeStep_ = new Time();
        viewUpdateStep_ = new Time();
        currentTime_ = new Time();
        startTime_ = new Time();
    }

    public SimulationTime(
        Time totalTime, // [msec]
        Time timeStep,   // [usec]
        Time viewUpdateStep   // [usec]
    ) {
        totalTime_ = new Time(totalTime.getDouble());
        timeStep_ = new Time(timeStep.getDouble());
        viewUpdateStep_ = new Time(viewUpdateStep.getDouble());
        currentTime_ = new Time(0,0);
        startTime_ = new Time(0,0);
    }

    public SimulationTime(
        double totalTime, // [msec]
        double timeStep,   // [usec]
        double viewUpdateStep   // [usec]
    ) {
        totalTime_ = new Time(totalTime);
        timeStep_ = new Time(timeStep);
        viewUpdateStep_ = new Time(viewUpdateStep);
        currentTime_ = new Time(0,0);
        startTime_ = new Time(0,0);
    }

    public void set(SimulationTime time) { 
        totalTime_.set(time.totalTime_);
        timeStep_.set(time.timeStep_);
        viewUpdateStep_.set(time.viewUpdateStep_);
        currentTime_.set(time.currentTime_);
        startTime_.set(time.startTime_);
    }

    /**
     * ���ƥå׻��֤βû��ʸ��߻��ֹ�����
     *   @return   ��׻��֤�ã����ޤ�True
     */
    public boolean inc() {
        currentTime_.add(timeStep_);
        if (currentTime_.msec_ > totalTime_.msec_) {
            currentTime_.set(totalTime_);
            return false;
        } else if (currentTime_.msec_ == totalTime_.msec_) {
            if (currentTime_.usec_ > totalTime_.usec_) {
                currentTime_.set(totalTime_);
                return false;
            }
        }

        return true;
    }

    /**
     * ���ϻ��֤�����
     *   @return time �� ���֡��á�
     */
    public void setStartTime(double time) {
        startTime_.set(time);
    }

    public void setCurrentTime(double time) {
        currentTime_.set(time);
    }

    public void setTotalTime(double time) {
        totalTime_.set(time);
    }

    public void setTimeStep(double time) {
        timeStep_.set(time);
    }

    public void setViewUpdateStep(double time) {
        viewUpdateStep_.set(time);
    }


    /**
     * ���ϻ��֤μ���
     *   @return ���ϻ��֡��á�
     */
    public double getStartTime() {
        return startTime_.getDouble();
    }

    /**
     * ���߻��֤μ���
     *   @return ���߻��֡��á�
     */
    public double getCurrentTime() {
        return currentTime_.getDouble();
    }

    /**
     * ��׻��֤μ���
     *   @return ��׻��֡��á�
     */
    public double getTotalTime() {
        return totalTime_.getDouble();
    }

    public double getTimeStep() {
        return timeStep_.getDouble();
    }

    public double getViewUpdateStep() {
        return viewUpdateStep_.getDouble();
    }
}

