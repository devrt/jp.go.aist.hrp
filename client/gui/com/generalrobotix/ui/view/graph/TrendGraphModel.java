package com.generalrobotix.ui.view.graph;

import java.util.*;
import java.awt.*;

/**
 * �ȥ��ɥ���ե�ǥ륯�饹
 *
 * @author Kernel Inc.
 * @version 1.0 (2001/8/20)
 */
public class TrendGraphModel
  //  implements WorldTimeListener, WorldReplaceListener
{
    public  static final double TIME_SCALE = 1000000;   // �����५����Ȥ���Ψ(1��sec)
    private static final double MAX_DIV = 10;   // ���ּ��κ���ʬ���
    private static final double LOG10 = Math.log(10);

    private long stepTimeCount_;    // ���ֹ����(�������)
    private long totalTimeCount_;   // �����(�������)
    private long currentTimeCount_; // ���߻���(�������)
    private long dataTermCount_;    // �ǡ����ν�ü�λ���(�������)

    private double stepTime_;       // ���ֹ����(��)
    private double totalTime_;      // �����(��)
    private double currentTime_;    // ���߻���(��)

    private double timeRange_;      // ���֥��(��)
    private double markerPos_;      // ���߻���ޡ�������(0.0����1.0�ǻ���)

    private double baseTime_;       // ����պ�ü����(��)

    public int sampleCount_;   // ����ե���ץ��
    public long baseCount_;    // �ǡ������ϰ���

    private AxisInfo timeAxisInfo_; // ���ּ�����

    //private HashMap dataSeriesMap_; // �ǡ����������
    private HashMap<String, ArrayList<String> > dataItemCount_; // �ǡ��������ƥ५����
                                    // (�ɤΥǡ�������ˤ����ĤΥǡ��������ƥब������Ƥ��Ƥ��뤫)

    private HashMap<String, DataModel> dataModelMap_;   // �ǡ�����ǥ����
    private DataModel[] dataModelArray_;    // �ǡ�����ǥ����

    //private DummyDataSource dumSource_; // �����ߡ��ǡ���������
    private LogManager logManager_; // ���ޥ͡�����

    private boolean markerFixed_;   // ���߻���ޡ�������ե饰
    private double fixedMarkerPos_; // ����ޡ�������

    private int mode_;  // �⡼��

  //  private SimulationWorld world_;
    /**
     * ���󥹥ȥ饯��
     *
     * @param   logManager  ���ޥ͡�����
     */
    public TrendGraphModel() {
        stepTimeCount_ = 1000;  // ���ֹ����   ���������ե����뤫���ɤ�?
        totalTimeCount_ = 10000000;  // �����   ���������ե����뤫���ɤ�?
        currentTimeCount_ = 0;  //1000000;
        stepTime_ = stepTimeCount_ / TIME_SCALE;
        totalTime_ = totalTimeCount_ / TIME_SCALE;
        currentTime_ = currentTimeCount_ / TIME_SCALE;

        timeRange_ = 1;     // ���֥��   ���������ե����뤫���ɤ�?

        fixedMarkerPos_ = 0.8;   // ���߻���ޡ�������   ���������ե����뤫���ɤ�?
        markerFixed_ = (timeRange_ < totalTime_);
        if (markerFixed_) { // �ޡ�������?
            markerPos_ = fixedMarkerPos_;
            baseTime_ = currentTime_ - timeRange_ * markerPos_; // ����պ�ü����
            sampleCount_ = (int)Math.floor(timeRange_ / stepTime_) + 2;  // ����ץ��
            baseCount_ = Math.round(baseTime_ / stepTime_); // - 1; // �ǡ������ϰ���
        } else {
            markerPos_ = currentTime_ / timeRange_;
            baseTime_ = 0;
            sampleCount_ = (int)Math.floor(timeRange_ / stepTime_) + 2;  // ����ץ��
            baseCount_ = 0;
        }

        // ����������
        timeAxisInfo_ = new AxisInfo(baseTime_, timeRange_);
        _updateDiv();    // ��ʬ�乹��
        timeAxisInfo_.min = 0;
        timeAxisInfo_.max = totalTime_;
        timeAxisInfo_.minLimitEnabled = true;
        timeAxisInfo_.maxLimitEnabled = true;
        timeAxisInfo_.unitFont = new Font("dialog", Font.PLAIN, 12);
        timeAxisInfo_.unitXOfs = 12;
        timeAxisInfo_.unitYOfs = 0;
        timeAxisInfo_.unitLabel = "(sec)";
        timeAxisInfo_.markerPos = markerPos_;
        timeAxisInfo_.markerColor = Color.cyan;  //new Color(255, 128, 128);
        timeAxisInfo_.markerVisible = true;

        //dataSeriesMap_ = new HashMap();
        dataItemCount_ = new HashMap<String, ArrayList<String> >();
        dataModelMap_ = new HashMap<String, DataModel>();
        dataModelArray_ = null;

        mode_ = GUIStatus.EDIT_MODE;

      //  logManager_ = LogManager.getInstance();

        // �����ߡ��ǡ���������
        /*
        dumSource_ = new DummyDataSource(10000);
        dumSource_.addDataItem(
            new DataItem("rob1", "LARM_JOINT3", "angle", -1),
            -3.14, 3.14
        );

        dumSource_.addDataItem(
            new DataItem("rob1", "LARM_JOINT3", "absPos", 0),
            -10, 10
        );
        dumSource_.addDataItem(
            new DataItem("rob1", "LARM_JOINT3", "absPos", 1),
            -10, 10
        );
        dumSource_.addDataItem(
            new DataItem("rob1", "LARM_JOINT3", "absPos", 2),
            -10, 10
        );

        dumSource_.addDataItem(
            new DataItem(null, "VLINK1", "contactForce", 0),
            0, 1000
        );
        dumSource_.addDataItem(
            new DataItem(null, "VLINK1", "contactForce", 1),
            0, 1000
        );
        dumSource_.addDataItem(
            new DataItem(null, "VLINK1", "contactForce", 2),
            0, 1000
        );

        dumSource_.addDataItem(
            new DataItem("rob2", "RLEG_JOINT2", "absComPos", 0),
            -10, 10
        );
        dumSource_.addDataItem(
            new DataItem("rob2", "RLEG_JOINT2", "absComPos", 1),
            -10, 10
        );
        dumSource_.addDataItem(
            new DataItem("rob2", "RLEG_JOINT2", "absComPos", 2),
            -10, 10
        );

        dumSource_.createData();
        */
    }

    // -----------------------------------------------------------------
    // �᥽�å�
    /**
     * �⡼�ɤ�����
     *   GUIManager.GUI_STATUS_EDIT:   �Խ��⡼��
     *   GUIManager.GUI_STATUS_EXEC:   ���ߥ�졼�����¹ԥ⡼��
     *   GUIManager.GUI_STATUS_PLAY:   �����⡼��
     * 
     * @param   mode    �⡼��
     */
    public void setMode(int mode) {
        mode_ = mode;
    }

    /**
     * ���ּ��������
     *
     * @return  AxisInfo    ���ּ�����
     */
    public AxisInfo getTimeAxisInfo() {
        return timeAxisInfo_;
    }

    /**
     * ���ֹ��������
     *
     * @param   stepTime    long    ���ֹ����(�������)
     */
    public void setStepTime(
        long stepTime
    ) {
        stepTimeCount_ = stepTime;
        stepTime_ = stepTimeCount_ / TIME_SCALE;

        sampleCount_ = (int)Math.floor(timeRange_ / stepTime_) + 2;  // ����ץ��(����2����ץ���ɲ�)
        baseCount_ = Math.round(baseTime_ / stepTime_); //- 1; // �ǡ������ϰ���

        // ���ǡ�������ι���
        Iterator itr = dataModelMap_.values().iterator();
        while (itr.hasNext()) {
            DataModel dm = (DataModel)itr.next();
            dm.dataSeries.setSize(sampleCount_);
            dm.dataSeries.setXStep(stepTime_);
        }

        // �ǡ������ɤ�ľ��ɬ�פϤʤ�(���ֹ���������ꤵ���Τ��Խ��⡼�ɤ�����)
    }

    /**
     * ���������
     *
     * @param   totalTime   long    �����(�������)
     */
    public void setTotalTime(long totalTime) {
        totalTimeCount_ = totalTime;
        totalTime_ = totalTimeCount_ / TIME_SCALE;
        timeAxisInfo_.max = totalTime_; // �������ͤι���

        //System.out.println("totalTime="+totalTime);

        if (markerFixed_) {
            markerFixed_ = (timeRange_ < totalTime_);
            if (markerFixed_) { // fixed -> fixed ?
                return;
            } else {    // fixed -> not fixed ?
                markerPos_ = currentTime_ / timeRange_;
                baseTime_ = 0;
                sampleCount_ = (int)Math.floor(timeRange_ / stepTime_) + 2;  // ����ץ��
                baseCount_ = 0;
            }
        } else {
            markerFixed_ = (timeRange_ < totalTime_);
            if (markerFixed_) { // non fixed -> fixed ?
                markerPos_ = fixedMarkerPos_;
                baseTime_ = currentTime_ - timeRange_ * markerPos_; // ����պ�ü����
                sampleCount_ = (int)Math.floor(timeRange_ / stepTime_) + 2;  // ����ץ��
                baseCount_ = Math.round(baseTime_ / stepTime_); // - 1; // �ǡ������ϰ���
            } else {    // not fixed -> not fixed ?
                markerPos_ = currentTime_ / timeRange_;
                baseTime_ = 0;
                sampleCount_ = (int)Math.floor(timeRange_ / stepTime_) + 2;  // ����ץ��
                baseCount_ = 0;
            }
        }

        timeAxisInfo_.base = baseTime_;
        timeAxisInfo_.extent = timeRange_;
        timeAxisInfo_.markerPos = markerPos_;
        _updateDiv();

        // ���ǡ�������ι���
        Iterator itr = dataModelMap_.values().iterator();
        while (itr.hasNext()) {
            DataModel dm = (DataModel)itr.next();
            dm.dataSeries.setSize(sampleCount_);
            //dm.dataSeries.setXOffset(baseTime_);  ������Ǥϥ���
            dm.dataSeries.setXOffset(baseCount_ * stepTime_);
        }

        // �ǡ������ɤ�ľ��
        //   ���֥�󥸤��ѹ��Ǥ���Τ��Խ��⡼�ɡ������⡼�ɤΤ�
        //   �����⡼�ɤξ��Τߥǡ������ɤ�ľ��
        if (dataModelArray_ == null) {
            return;
        }
        if (mode_ == GUIStatus.EDIT_MODE) {
            return;
        }
        // ��
        //System.out.println("baseCount_=" + baseCount_);
        logManager_.getData(baseCount_, 0, sampleCount_, dataModelArray_);
    }

    /**
     * ����֤���ӻ��ֹ��������
     *
     * @param   totalTime   �����(�������)
     * @param   stepTime    ���ֹ����(�������)
     */
    /*
    public void setTotalAndStepTime(
        long totalTime,
        int stepTime
    ) {
    }
    */

    /**
     * ���֥�󥸤���ӥޡ�����������
     *
     * @param   timeRange   double  ���֥��(��)
     * @param   markerPos   double  �ޡ�������
     */
    public void setRangeAndPos(
        double timeRange,
        double markerPos
    ) {
        if (fixedMarkerPos_ == markerPos
            && timeRange_ == timeRange) {
            return;
        }
        _setTimeRange(timeRange, markerPos);
    }

    /**
     * ���֥������
     *
     * @param   timeRange   double  ���֥��(��)
     */
    public void setTimeRange(
        double timeRange
    ) {
        if (timeRange_ == timeRange) {
            return;
        }
        _setTimeRange(timeRange, fixedMarkerPos_);
    }

    /**
     * ���֥������
     *
     * @param   timeRange   double  ���֥��(��)
     */
    private void _setTimeRange(
        double timeRange,
        double markerPos
    ) {
        timeRange_ = timeRange;
        fixedMarkerPos_ = markerPos;

        markerFixed_ = (timeRange_ < totalTime_);
        if (markerFixed_) { // �ޡ�������?
            markerPos_ = fixedMarkerPos_;
            baseTime_ = currentTime_ - timeRange_ * markerPos_; // ����պ�ü����
            sampleCount_ = (int)Math.floor(timeRange_ / stepTime_) + 2;  // ����ץ��
            baseCount_ = Math.round(baseTime_ / stepTime_); // - 1; // �ǡ������ϰ���
        } else {
            markerPos_ = currentTime_ / timeRange_;
            baseTime_ = 0;
            sampleCount_ = (int)Math.floor(timeRange_ / stepTime_) + 2;  // ����ץ��
            baseCount_ = 0;
        }
        //System.out.println("*** base= " + (baseTime_ / stepTime_));
        //System.out.println("*** cnt = " + baseCount_);

        timeAxisInfo_.base = baseTime_;
        timeAxisInfo_.extent = timeRange_;
        timeAxisInfo_.markerPos = markerPos_;
        _updateDiv();

        // ���ǡ�������ι���
        Iterator itr = dataModelMap_.values().iterator();
        while (itr.hasNext()) {
            DataModel dm = (DataModel)itr.next();
            dm.dataSeries.setSize(sampleCount_);
            //dm.dataSeries.setXOffset(baseTime_);  ������Ǥϥ���
            dm.dataSeries.setXOffset(baseCount_ * stepTime_);
        }

        // �ǡ������ɤ�ľ��
        //   ���֥�󥸤��ѹ��Ǥ���Τ��Խ��⡼�ɡ������⡼�ɤΤ�
        //   �����⡼�ɤξ��Τߥǡ������ɤ�ľ��
        if (dataModelArray_ == null) {
            return;
        }
        if (mode_ == GUIStatus.EDIT_MODE) {
            return;
        }
        // ��
        //System.out.println("baseCount_=" + baseCount_);
        logManager_.getData(baseCount_, 0, sampleCount_, dataModelArray_);
    }

    /**
     * ���֥�󥸼���
     *
     * @param   double  ���֥��(��)
     */
    public double getTimeRange() {
        return timeRange_;
    }

    /**
     * �ޡ�����������
     *
     * @param   markerPos   double  �ޡ�������
     */
    public void setMarkerPos(double markerPos) {
        if (markerPos == fixedMarkerPos_) {
            return;
        }
        fixedMarkerPos_ = markerPos;

        if (!markerFixed_) { // �ޡ�������Ǥʤ�?
            return; // �ʤˤ⤷�ʤ�
        }

        markerPos_ = fixedMarkerPos_;
        baseTime_ = currentTime_ - timeRange_ * markerPos_; // ����պ�ü����
        long oldBaseCount = baseCount_;
        baseCount_ = Math.round(baseTime_ / stepTime_); // - 1; // �ǡ������ϰ���

        timeAxisInfo_.base = baseTime_;
        timeAxisInfo_.markerPos = markerPos_;
        //_updateDiv();

        int diff = (int)(baseCount_ - oldBaseCount);

        // ���ǡ�������ΰ�ư
        Iterator itr = dataModelMap_.values().iterator();
        while (itr.hasNext()) {
            DataSeries ds = ((DataModel)itr.next()).dataSeries;
            ds.shift(diff);
            //System.out.println("shift=" + diff);
        }

        // �ǡ������ɤ�ľ��
        //   �ޡ������֤��ѹ��Ǥ���Τ��Խ��⡼�ɡ������⡼�ɤΤ�
        //   �����⡼�ɤξ��Τߺ�ʬ�ǡ������ɤ߽Ф�
        if (diff == 0 || dataModelArray_ == null) {
            return;
        }
        // ��
        if (mode_ == GUIStatus.EDIT_MODE) {
            return;
        }
        if (diff > 0) {
            if (diff >= sampleCount_) {
                logManager_.getData(baseCount_, 0, sampleCount_, dataModelArray_);
            } else {
                logManager_.getData(baseCount_, sampleCount_ - diff, diff, dataModelArray_);
            }
        } else {
            if (-diff >= sampleCount_) {
                logManager_.getData(baseCount_, 0, sampleCount_, dataModelArray_);
            } else {
                logManager_.getData(baseCount_, 0, -diff, dataModelArray_);
            }
        }
    }

    /**
     * �ޡ������ּ���
     *
     * @param   double  �ޡ�������
     */
    public double getMarkerPos() {
        return fixedMarkerPos_;
    }

    /**
     * ���ƥå׻��ּ���
     *
     * @param   double  ���ƥå׻���
     */
    public double getStepTime() {
        return stepTime_;
    }

    /**
     * �ȡ�������ּ���
     *
     * @param   double  �ȡ��������
     */
    public double getTotalTime() {
        return totalTime_;
    }

    /**
     * ���߻�������
     *
     * @param   long    currentTime ���߻���(�������)
     */
    public void setCurrentTime(long currentTime) {
        //System.out.println("setCurrentTime(): crrentTime=" + currentTime);

        currentTimeCount_ = currentTime;
        currentTime_ = currentTimeCount_ / TIME_SCALE;

        if (!markerFixed_) {    // �ޡ�������Ǥʤ�?
            markerPos_ = currentTime_ / timeRange_;
            timeAxisInfo_.markerPos = markerPos_;
            return;
        }

        baseTime_ = currentTime_ - timeRange_ * markerPos_; // ����պ�ü����
        long oldBaseCount = baseCount_;
        baseCount_ = Math.round(baseTime_ / stepTime_); // - 1; // �ǡ������ϰ���
        timeAxisInfo_.base = baseTime_;

        int diff = (int)(baseCount_ - oldBaseCount);

        // ���ǡ�������ΰ�ư
        Iterator itr = dataModelMap_.values().iterator();
        while (itr.hasNext()) {
            DataSeries ds = ((DataModel)itr.next()).dataSeries;
            ds.shift(diff);
        }

        // �ǡ������ɤ�ľ��
        //   �ޡ������֤��ѹ��Ǥ���Τ��Խ��⡼�ɡ������⡼�ɤΤ�
        //   �����⡼�ɤξ��Τߺ�ʬ�ǡ������ɤ߽Ф�
        if (diff == 0 || dataModelArray_ == null) {
            //System.out.println("diff=" + diff);
            return;
        }
        // ��
        if (mode_ == GUIStatus.EDIT_MODE) {
            System.out.println("GUIMODE_EDIT");
            return;
        }
        if (diff > 0) {
            if (diff >= sampleCount_) {
                //System.out.println("getData(): patern 1 in");
                logManager_.getData(baseCount_, 0, sampleCount_, dataModelArray_);
                //System.out.println("getData(): patern 1 out");
            } else {
                //System.out.println("base=" + baseCount_ + ", ofs=" + (sampleCount_ - diff) + ", count=" + diff);
                //System.out.println("getData(): patern 2 in");
                logManager_.getData(baseCount_, sampleCount_ - diff, diff, dataModelArray_);
                //System.out.println("getData(): patern 2 out");
            }
        } else {
            if (-diff >= sampleCount_) {
                //System.out.println("getData(): patern 3 in");
                logManager_.getData(baseCount_, 0, sampleCount_, dataModelArray_);
                //System.out.println("getData(): patern 3 out");
            } else {
                //System.out.println("base=" + baseCount_ + ", ofs=0, count=" + (-diff));
                //System.out.println("getData(): patern 4 in");
                logManager_.getData(baseCount_, 0, -diff, dataModelArray_);
                //System.out.println("getData(): patern 4 out");
            }
        }
    }

    public void setDataTermTime(long dataTermTime) {
        long prevDataTermCount = dataTermCount_;
        dataTermCount_ = dataTermTime / stepTimeCount_;
        if (dataModelArray_ == null) {
            return;
        }
        if (prevDataTermCount < baseCount_) {
            //System.out.println("case1");
            if (dataTermCount_ < baseCount_ + sampleCount_) {
                logManager_.getData(
                    baseCount_,
                    0,
                    (int)(dataTermCount_ - baseCount_), 
                    dataModelArray_
                );
            } else {
                logManager_.getData(
                    baseCount_,
                    0,
                    sampleCount_,
                    dataModelArray_
                );
                dataTermCount_ = baseCount_ + sampleCount_;
            }
        } else if (prevDataTermCount < baseCount_ + sampleCount_) {
            //System.out.println("case2");
            if (dataTermCount_ < baseCount_ + sampleCount_) {
                logManager_.getData(
                    baseCount_,
                    (int)(prevDataTermCount - baseCount_),
                    (int)(dataTermCount_ - prevDataTermCount),
                    dataModelArray_
                );
            } else {
                logManager_.getData(
                    baseCount_,
                    (int)(prevDataTermCount - baseCount_),
                    (int)(baseCount_ + sampleCount_ - prevDataTermCount),
                    dataModelArray_
                );
                dataTermCount_ = baseCount_ + sampleCount_;
            }
        } else {
            //System.out.println("case3: prevDataTermCount=" + prevDataTermCount + " baseCount=" + baseCount_ + " sampleCount=" + sampleCount_);
        }
    }


    /**
     * ���߻����1���ƥåװ�ư
     *
     */
    public void shiftCurrentTime() {
        // ���߻���ΰ�ư
        //currentTimeCount_ += stepTimeCount_; //�������ǲû�����ΤϤ�������
        //System.out.println("shiftCurrentTime(): currentTimeCount=" + currentTimeCount_);
        currentTime_ = currentTimeCount_ / TIME_SCALE;
//        int setPos;
        if (markerFixed_) { // �ޡ�������?
            baseTime_ = currentTime_ - timeRange_ * markerPos_; // ����պ�ü����
            baseCount_ = Math.round(baseTime_ / stepTime_); // �ǡ������ϰ���
            timeAxisInfo_.base = baseTime_;
//            setPos = (int)Math.round(timeRange_ * markerPos_ / stepTime_);  // �ǡ����������
        } else {    // �ޡ�����ư?
            markerPos_ = currentTime_ / timeRange_;
            timeAxisInfo_.markerPos = markerPos_;
//            setPos = (int)(currentTimeCount_ / stepTimeCount_);
        }
        // �����ͤμ���
//        Iterator itr = dataModelMap_.values().iterator();
        //if (!itr.hasNext()) { System.out.println("dataModelMap is empty!!!"); }
 /*       while (itr.hasNext()) { // ���ǡ�����ǥ��롼��
            DataModel dm = (DataModel)itr.next();
            DataItem di = dm.dataItem;
            DataSeries ds = dm.dataSeries;

            StringExchangeable se = world_.getAttributeFromPath(    // ���ߤΥ��ȥ�ӥ塼�Ȥ����
                di.getAttributePath()
            );
            
            // ���ȥ�ӥ塼���ͼ���
            double val;
            if (di.isArray()) {
                if (se instanceof SETranslation) {
                    SETranslation st = (SETranslation)se;
                    if (di.index == 0) {
                        val = st.getX();
                    } else if (di.index == 1) {
                        val = st.getY();
                    } else {
                        val = st.getZ();
                    }
                } else {
                    val = ((SEDoubleArray)se).doubleValue(di.index);
                }
            } else {
                val = ((SEDouble)se).doubleValue();
            }
            //System.out.println("value = " + val);
            ds.set(setPos, val);
            if (markerFixed_) {
                ds.shift(1);
            }
            //ds.addLast(val);    // ���ǡ��������������ɲ� <--- ����ϴְ㤤
        }
*/
        if (currentTimeCount_ < totalTimeCount_) {
            currentTimeCount_ += stepTimeCount_;
            //System.out.println("currentTimeCount=" + currentTimeCount_);
        }
    }

    /**
     * �ǡ��������ƥ��ɲ�
     *
     * @param   DataItem    dataItem    �ǡ��������ƥ�
     * @return  DataSeries  �ǡ�������
     */
    public DataSeries addDataItem(
        DataItem dataItem
    ) {
        DataSeries ds;
        DataModel dm;

    /*    if (!world_.hasAttributeByPath(dataItem.getAttributePath())) {
            return null;
        }*/

        String key = dataItem.toString();
        ArrayList<String> l = dataItemCount_.get(key);
        if (l == null) {    // ���ƤΥǡ��������ƥ�?
            l = new ArrayList<String>();
            l.add(key);
            dataItemCount_.put(key, l);
            ds = new DataSeries(
                sampleCount_,
                baseCount_ * stepTime_, // baseTime_, ������Ǥϥ���
                stepTime_
            );
            dm = new DataModel(dataItem, ds);
            dataModelMap_.put(key, dm);
            dataModelArray_ = new DataModel[dataModelMap_.size()];
            dataModelMap_.values().toArray(dataModelArray_);
            // �ǡ������ɤ߹���
            //   �ǡ��������ƥ���ɲäǤ���Τ��Խ��⡼�ɡ������⡼�ɤΤ�
            //   �����⡼�ɤξ��Τߥǡ������ɤ߽Ф�
            // ��
// commented by grx
//            if (mode_ != GUIStatus.EDIT_MODE) {
//                logManager_.getData(baseCount_, 0, sampleCount_, new DataModel[]{dm});
//            }

            // ���ȥ�ӥ塼�ȥե饰���ѹ�
            //System.out.println("###### locked (" + dataItem.getAttributePath() + ")");
       /*     world_.setAttributeFlagFromPath(
                dataItem.getAttributePath(),
                Attribute.RECORD_REQUIRED | Attribute.RECORD_FLAG_LOCKED,   // ��MUST_RECORD�ʤ�Τ�RECORD_REQUIRED�򥻥åȤ��Ƥ�����פ�?
                true
            );*/

        } else {
            l.add(key);
            ds = ((DataModel)dataModelMap_.get(key)).dataSeries;
        }

        return ds;
    }

    /**
     * �ǡ��������ƥ���
     *
     * @param   dataItem    �ǡ��������ƥ�
     * @param   setFlag     �ե饰����򤹤뤫�ݤ�
     */
    public void removeDataItem(
        DataItem dataItem,
        boolean setFlag
    ) {
        String key = dataItem.toString();
        ArrayList l = (ArrayList)dataItemCount_.get(key);   // ������ȼ���
        l.remove(0);    // ������Ȥ򸺤餹
        if (l.size() <= 0) {    // �ǡ���������б�����ǡ��������ƥब�ʤ��ʤä�?
            dataItemCount_.remove(key); // ������Ƚ���
            dataModelMap_.remove(key); // �ǡ����������
            int size = dataModelMap_.size();
            if (size <= 0) {
                dataModelArray_ = null;
            } else {
                dataModelArray_ = new DataModel[size];
                dataModelMap_.values().toArray(dataModelArray_);
            }

            // ���ȥ�ӥ塼�ȥե饰���ѹ�
            if (!setFlag) { // �ե饰�ѹ��򤷤ʤ�?
                return; // �ʤˤ⤷�ʤ�
            }
            String apath = dataItem.getAttributePath(); // ���ȥ�ӥ塼�ȥѥ�̾�μ���
            //if (world_.checkAttributeFlagFromPath(apath, Attribute.MUST_RECORD)) { // ��Ͽɬ��?
            //    return; // �ե饰�ѹ��Ϥ��ʤ�
            //}
            Iterator itr = dataItemCount_.keySet().iterator();  // ���٤ƤΥǡ��������ƥ�̾�����
//           boolean found = false;  // ȯ���ե饰�ꥻ�å�
            while (itr.hasNext()) { // ���٤ƤΥǡ��������ƥ�̾�ˤĤ���
                String k = (String)itr.next();
                if (k.startsWith(apath)) {  // Ʊ�����ȥ�ӥ塼��?
//                  found = true;   // ȯ���ե饰ON
                    break;  // �롼��æ��
                }
            }
            //System.out.println("###### unlocked?");
 /*           if (!found) {   // ���Υ��ȥ�ӥ塼�ȤϤ⤦�ʤ�?
                //System.out.println("###### unlocked (" + apath + ")");
                world_.setAttributeFlagFromPath(
                    apath,
                    Attribute.RECORD_FLAG_LOCKED,
                    false
                );
            }
*/
        }
    }

    // -----------------------------------------------------------------
    // WorldTimeListener�μ���
    /**
     * �����Ѳ�
     * 
     * @param   time    ����
     */
    public void worldTimeChanged(Time time) {
        //System.out.println("@@@ TimeChanged @@@ " + time.getUtime());
        /*
        if (mode_ == GUIManager.GUI_STATUS_EXEC) {
            shiftCurrentTime();
        } else if (mode_ == GUIManager.GUI_STATUS_PLAY) {
            setCurrentTime(time.getUtime());
        }
        */
    }

    // -----------------------------------------------------------------
    // �ץ饤�١��ȥ᥽�å�
    /**
     * ��ʬ�乹��
     *
     */
    private void _updateDiv() {
        double sMin = timeAxisInfo_.extent / MAX_DIV;
        int eMin = (int)Math.floor(Math.log(sMin) / LOG10);
        double step = 0;
        String format = "0";
        int e = eMin;
        boolean found = false;
        while (!found) {
            int m = 1;
            for (int i = 1; i <= 3; i++) {
                step = m * Math.pow(10.0, e);
                if (sMin <= step) { // && step <= sMax) {
                    if (e < 0) {
                        char[] c = new char[-e + 2];
                        c[0] = '0';
                        c[1] = '.';
                        for (int j = 0; j < -e; j++) {
                            c[j + 2] = '0';
                        }
                        format = new String(c);
                    }
                    found = true;
                    break;
                }
                m += (2 * i - 1);
            }
            e++;
        }
        timeAxisInfo_.tickEvery = step;
        timeAxisInfo_.labelEvery = step;
        timeAxisInfo_.gridEvery = step;
        timeAxisInfo_.labelFormat = format;
    }

    // -----------------------------------------------------------------
    // WorldReplaceListener�μ���
    /**
     * �����ι���
     *
     * @param   world   ����
     */
/*    public void replaceWorld(SimulationWorld world) {
        world_ = world;
        //world_.addTimeListener(this);
    }
*/
    /**
     * ������
     *
     * @param   totalTime   �����(�ޥ�������)
     * @param   stepTime    ��ʬ����(�ޥ�������)
     * @param   currentTime ���߻���(�ޥ�������)
     * @param   timeRange   ���֥��(��)
     * @param   markerPos   �ޡ�������
     */
    public void setup(
        long    totalTime,
        long    stepTime,
        long    currentTime,
        double  timeRange,
        double  markerPos
    ) {
        /*
        System.out.println("totalTime=" + totalTime);
        System.out.println("stepTime=" + stepTime);
        System.out.println("currentTime=" + currentTime);
        System.out.println("timeRange=" + timeRange);
        */
        totalTimeCount_ = totalTime;
        stepTimeCount_ = stepTime;
        currentTimeCount_ = currentTime;
        timeRange_ = timeRange;
        fixedMarkerPos_ = markerPos;

        dataTermCount_ = 0L;

        totalTime_ = totalTimeCount_ / TIME_SCALE;
        stepTime_ = stepTimeCount_ / TIME_SCALE;
        currentTime_ = currentTimeCount_ / TIME_SCALE;

        markerFixed_ = (timeRange_ < totalTime_);
        if (markerFixed_) {
            markerPos_ = fixedMarkerPos_;
            baseTime_ = currentTime_ - timeRange_ * markerPos_; // ����պ�ü����
            sampleCount_ = (int)Math.floor(timeRange_ / stepTime_) + 2;  // ����ץ��
            baseCount_ = Math.round(baseTime_ / stepTime_); // - 1; // �ǡ������ϰ���
            //System.out.println("(fixed) sampleCount=" + sampleCount_);
            //System.out.println("(fixed) baseCount=" + baseCount_);
        } else {
            markerPos_ = currentTime_ / timeRange_;
            baseTime_ = 0;
            sampleCount_ = (int)Math.floor(timeRange_ / stepTime_) + 2;  // ����ץ��
            baseCount_ = 0;
            //System.out.println("(not fixed) sampleCount=" + sampleCount_);
            //System.out.println("(not fixed) baseCount=" + baseCount_);
        }

        timeAxisInfo_.max = totalTime_; // �������ͤι���
        timeAxisInfo_.base = baseTime_;
        timeAxisInfo_.extent = timeRange_;
        timeAxisInfo_.markerPos = markerPos_;
        _updateDiv();

        // ���ǡ�������ι���
        Iterator itr = dataModelMap_.values().iterator();
        while (itr.hasNext()) {
            DataModel dm = (DataModel)itr.next();
            dm.dataSeries.setSize(sampleCount_);
            dm.dataSeries.setXOffset(baseCount_ * stepTime_);
            dm.dataSeries.setXStep(stepTime_);
        }

//        world_.updateAttribute("Graph0.hRange=" + Double.toString(timeRange_));
//        world_.updateAttribute("Graph0.markerPos=" + Double.toString(fixedMarkerPos_));
    }

    public void setup(
        long    totalTime,
        long    currentTime
    ) {
        setup(
            totalTime,
            stepTimeCount_,
            currentTime,
            (
                timeRange_ >= totalTime / TIME_SCALE
                ? totalTime / TIME_SCALE
                : timeRange_
            ),
            fixedMarkerPos_
        );
    }

    public void setup(
        long    totalTime,
        long    stepTime,
        long    currentTime
    ) {
        setup(
            totalTime,
            stepTime,
            currentTime,
            (
                timeRange_ >= totalTime / TIME_SCALE
                ? totalTime / TIME_SCALE
                : timeRange_
            ),
            fixedMarkerPos_
        );
    }

    public void reread() {
        if (dataModelArray_ == null) {
            return;
        }
        logManager_.getData(baseCount_, 0, sampleCount_, dataModelArray_);
    }
    
    public void setLogManager(LogManager logManager) {
    	logManager_ = logManager;
    }
}
