package com.generalrobotix.ui.view.graph;

/**
 * �ǡ�����ǥ�
 *
 * @author Kernel Inc.
 * @version 1.0 (2001/8/20)
 */
public class DataModel {

    public final DataItem dataItem;     // �ǡ��������ƥ�
    public final DataSeries dataSeries; // �ǡ�������

    // -----------------------------------------------------------------
    // ���󥹥ȥ饯��
    /**
     * ���󥹥ȥ饯��
     *
     * @param   dataItem    �ǡ��������ƥ�
     * @param   dataSeries  �ǡ�������
     */
    public DataModel(
        DataItem dataItem,
        DataSeries dataSeries
    ) {
        this.dataItem = dataItem;
        this.dataSeries = dataSeries;
    }
}
