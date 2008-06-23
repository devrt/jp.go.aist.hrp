package com.generalrobotix.ui.view.graph;

/**
 * �ǡ�������
 *
 * @author Kernel Inc.
 * @version 1.0 (2001/8/20)
 */
public class DataKind {

    public final String name;       // �ǡ�������̾
    public final String unitLabel;  // ñ�̥�٥�
    public final double base;       // �����
    public final double extent;     // ��
    public final double factor;     // �ץ�åȷ���

    // -----------------------------------------------------------------
    // ���󥹥ȥ饯��
    /**
     * ���󥹥ȥ饯��
     *
     * @param   name        �ǡ�������̾
     * @param   unitLabel   ñ�̥�٥�
     * @param   base        �����
     * @param   extent      ��
     * @param   factor      �ץ�åȷ���
     */
    public DataKind(
        String name,
        String unitLabel,
        double base,
        double extent,
        double factor
    ) {
        this.name = name;
        this.unitLabel = unitLabel;
        this.base = base;
        this.extent = extent;
        this.factor = factor;
    }
}
