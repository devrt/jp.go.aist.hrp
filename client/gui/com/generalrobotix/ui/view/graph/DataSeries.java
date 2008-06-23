package com.generalrobotix.ui.view.graph;

/**
 * �ǡ������󥯥饹
 *
 * @author Kernel Inc.
 * @version 1.0 (2001/8/20)
 */
public class DataSeries {

    // -----------------------------------------------------------------
    // ���󥹥����ѿ�
    private int size_;                  // �ǡ�������
    private double[/*size_*/] data_;    // �ǡ�����Y��ɸ��(Double.NaN�ϥǡ����η���򼨤�)
    private int headPos_;               // �ǡ�����Ƭź��
    private double xOffset_;            // X��ɸ�ͥ��ե��å�
    private double xStep_;              // X��ɸ�͹����

    // -----------------------------------------------------------------
    // ���󥹥ȥ饯��
    /**
     * ���󥹥ȥ饯��
     *
     * @param   size    int     �ǡ�������
     * @param   xOffset double  X��ɸ�ͥ��ե��å�
     * @param   xStep   double  X��ɸ�͹����
     */
    public DataSeries(
        int size,
        double xOffset,
        double xStep
    ) {
        size_ = size;
        xOffset_ = xOffset;
        xStep_ = xStep;
        headPos_ = 0;
        data_ = new double[size];
        for (int i = 0; i < size; i++) {
            data_[i] = Double.NaN;
        }
    }

    // -----------------------------------------------------------------
    // ���󥹥��󥹥᥽�å�
    /**
     * �ǡ�������������
     *
     * @param   size    int �ǡ�������
     */
    public void setSize(
        int size
    ) {
        size_ = size;
        headPos_ = 0;
        data_ = new double[size];
        for (int i = 0; i < size; i++) {
            data_[i] = Double.NaN;
        }
    }

    /**
     * X��ɸ�ͥ��ե��å�����
     *
     * @param   xOffset double  X��ɸ�ͥ��ե��å�
     */
    public void setXOffset(
        double xOffset
    ) {
        xOffset_ = xOffset;
    }

    /**
     * X��ɸ�͹��������
     *
     * @param   xOffset double  X��ɸ�͹����
     */
    public void setXStep(
        double xStep
    ) {
        xStep_ = xStep;
    }

    /**
     * ����������
     *
     * @return  int ������
     */
    public int getSize() {
        return size_;
    }

    /**
     * X��ɸ�ͥ��ե��åȼ���
     *
     * @return  double  X��ɸ�ͥ��ե��å�
     */
    public double getXOffset() {
        return xOffset_;
    }

    /**
     * X��ɸ�͹��������
     *
     * @return  double  X��ɸ�͹����
     */
    public double getXStep() {
        return xStep_;
    }

    /**
     * �ǡ����������
     *
     * @return  double[]    �ǡ�������
     */
    public double[] getData() {
        return data_;
    }

    /**
     * �ǡ�����Ƭź��
     *
     * @return  int �ǡ�����Ƭź��
     */
    public int getHeadPos() {
        return headPos_;
    }

    /**
     * ��Ƭ�ǡ�������
     *     �ǡ�����Ƭ���֤���pos����Υ�줿�Ȥ�����data��񤭹���
     *     �ǡ���������Ķ������ʬ���ڤ�ΤƤ�
     *
     * @param   pos     int         �ǡ����������
     * @param   data    double[]    �ǡ���
     */
    public void setHead(
        int pos,
        double[] data
    ) {
        // ����Ĺ�����å�
        int len = data.length;
        if (len < 1) {  // ��������?
            return; // �ʤˤ⤷�ʤ�
        }

        // ���ϰ��֥����å�
        if (pos >= size_) { // ���ϰ��֤�����?
            return; // �ʤˤ⤷�ʤ�
        }
        int ofs = 0;
        if (pos < 0) {
            if (len <= -pos) {  // Ĺ����­��ʤ�(��Ƭ���Ϥ��ʤ�)?
                return; // �ʤˤ⤷�ʤ�
            }
            ofs = -pos; // ���ե��å�����
            pos = 0;    // ���ϰ��ֹ���
            len -= ofs; // ����Ĺ����
        }

        // ����Ĺ�����å�
        if (len > size_ - pos) {    // Ϳ����줿����Ĺ������?
            len = size_ - pos;      // Ĺ���򥫥å�
        }

        // �ǡ����Υ��ԡ�
        int former = size_ - headPos_;
        if (pos < former) {    // ���ϰ��֤���Ⱦ�ˤ���?
            int remain = former - pos;
            if (len <= remain) {    // ������Ⱦ�˼��ޤ�?
                System.arraycopy(
                    data, ofs,
                    data_, headPos_ + pos,
                    len
                );  // ������Ⱦ�˥��ԡ�
            } else {
                System.arraycopy(
                    data, ofs,
                    data_, headPos_ + pos,
                    remain
                );  // ��Ⱦ�˥��ԡ�
                System.arraycopy(
                    data, ofs + remain,
                    data_, 0,
                    len - remain
                );  // ��Ⱦ�˥��ԡ�
            }
        } else {    // ���ϰ��֤���Ⱦ�ˤ���?
            System.arraycopy(
                data, ofs,
                data_, pos - former,
                len
            );  // ���Ƹ�Ⱦ�˥��ԡ�
        }
    }

    /**
     * �����ǡ�������
     *     �ǡ����������֤���pos����Υ�줿�Ȥ�����data��񤭹���
     *     �ǡ���������Ķ������ʬ���ڤ�ΤƤ�
     *
     * @param   pos     int         �ǡ����������
     * @param   data    double[]    �ǡ���
     */
    public void setTail(
        int pos,
        double[] data
    ) {
        setHead(size_ - 1 - pos, data);
    }

    /**
     * �ǡ�����ư
     *     count�����ǡ������ư���롣
     *     count�����ξ�硢�ǡ���������count����NaN���ɲä���
     *     (�ǡ�����Ƭ���֤���ư������Ƭ�ǡ�������������)
     *     (xOffset += xStep * count)
     *     count����ξ�硢�ǡ�����Ƭ��-count����NaN���ɲä���
     *     (�ǡ�����Ƭ���֤���ư���������ǡ�������������)
     *     (xOffset -= xStep * count)
     *
     * @param   count   int �ǡ�����ư��
     */
    public void shift(
        int count
    ) {
        // ��ư�̥����å�
        if (count == 0) {   // ��ư���ʤ�?
            return; // �ʤˤ⤷�ʤ�
        }
        xOffset_ += xStep_ * count; // X��ɸ�ͥ��ե��åȹ���
        if (count >= size_ || count <= -size_) {    // ����Ĺ��Ķ�����ư?
            for (int i = 0; i < size_; i++) {   // NaN���ꥢ
                data_[i] = Double.NaN;
            }
            return;
        }

        // ���ե�
        int prevHead = headPos_;            // �Ť���Ƭ����
        int newHead = prevHead + count;     // ��������Ƭ����
        if (count > 0) {    // ���ΰ�ư?
            for (int i = prevHead; i < newHead; i++) {  // �����ˤǤ�������롼��
                data_[i % size_] = Double.NaN;  // NaN���ꥢ
            }
            headPos_ = newHead % size_; // ��Ƭ���ֹ���
        } else {            // ��ΰ�ư?
            int ind;
            for (int i = newHead; i < prevHead; i++) {  // �����ˤǤ�������롼��
                ind = i % size_;
                if (ind < 0) {
                    ind += size_;
                }
                data_[ind] = Double.NaN;  // NaN���ꥢ
            }
            // ��Ƭ���ֹ���
            headPos_ = newHead % size_;
            if (headPos_ < 0) {
                headPos_ += size_;
            }
        }
    }

    /**
     * �����˥ǡ����ɲ�
     *     1�ĥǡ������ư���ơ�value��񤭹���
     *     (xOffset += xStep)
     *
     * @param   count   int �ǡ�����ư��
     */
    public void addLast(
        double value
    ) {
        xOffset_ += xStep_; // X��ɸ�ͥ��ե��åȹ���
        data_[headPos_] = value;    // �ǡ��������
        headPos_ = (headPos_ + 1) % size_;  // ��Ƭ���ְ�ư
    }

    /**
     * �ǡ�������
     *     ��Ƭ����posΥ�줿�Ȥ���˥ǡ�����񤭹���
     *
     * @param   pos     �ǡ����������
     * @param   value   ��
     */
    public void set(
        int pos,
        double value
    ) {
        int setPos = (headPos_ + pos) % size_;
        data_[setPos] = value;
    }
}
