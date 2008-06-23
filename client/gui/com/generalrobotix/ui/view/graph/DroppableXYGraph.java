package com.generalrobotix.ui.view.graph;

import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;

/**
 * �ɥ�åײ�ǽ�ޤ�������ե��饹
 *
 * @author Kernel Inc.
 * @version 1.0 (2001/8/20)
 */
public class DroppableXYGraph
    extends XYLineGraph
    implements DropTargetListener
{
    private Object droppedObject_;  // �ɥ�åפ��줿���֥�������
    DropTarget dropTarget_;         // �ɥ�åץ������å�
    ActionListener actionListener_; // ���������ꥹ��

    boolean dropSucceeded_;

    // -----------------------------------------------------------------
    // ���󥹥ȥ饯��
    /**
     * ���󥹥ȥ饯��
     *
     * @param   leftMargin      int ���ޡ�����
     * @param   rightMargin     int ���ޡ�����
     * @param   topMargin       int ��ޡ�����
     * @param   bottomMargin    int ���ޡ�����
     */
    public DroppableXYGraph(
        int leftMargin,     // ���ޡ�����
        int rightMargin,    // ���ޡ�����
        int topMargin,      // ��ޡ�����
        int bottomMargin    // ���ޡ�����
    ) {
        super(leftMargin, rightMargin, topMargin, bottomMargin);
        dropTarget_ = new DropTarget(
            this,
            DnDConstants.ACTION_COPY_OR_MOVE,
            this,
            true
        );
        droppedObject_ = null;
        dropSucceeded_ = true;
    }

    // -----------------------------------------------------------------
    // ���󥹥��󥹥᥽�å�
    /**
     * �ɥ�å׵����Ե�������
     *
     * @param   active  �ɥ�å׵����Ե��ĥե饰
     */
    public void setDropActive(
        boolean active
    ) {
        dropTarget_.setActive(active);
    }

    /**
     * �ɥ�åץ��֥������Ȥμ���
     *
     * @return  �ɥ�åפ��줿���֥�������
     */
    public Object getDroppedObject() {
        return droppedObject_;
    }

    public void setDropSucceeded(boolean flag) {
        dropSucceeded_ = flag;
    }

    // -----------------------------------------------------------------
    // ActionListener��Ͽ����Ӻ��
    public void addActionListener(ActionListener listener) {
        actionListener_ = AWTEventMulticaster.add(actionListener_, listener);
    }
    public void removeActionListener(ActionListener listener) {
        actionListener_ = AWTEventMulticaster.remove(actionListener_, listener);
    }

    // -----------------------------------------------------------------
    // DropTargetListener�μ���
    /**
     * �ɥ�åפ��줿
     *
     * @param   evt �ɥ�åץ��٥��
     */
    public void drop(DropTargetDropEvent evt) {
        if (
            evt.isDataFlavorSupported(AttributeInfo.dataFlavor)
            && (
                evt.getDropAction()
                & DnDConstants.ACTION_COPY_OR_MOVE
            ) != 0
        ) { // ���������?
            evt.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);   // �ɥ�å׼�������
            try {
                Transferable tr = evt.getTransferable();
                if (tr.isDataFlavorSupported(AttributeInfo.dataFlavor)){
                    droppedObject_ = tr.getTransferData(AttributeInfo.dataFlavor);
                    if(actionListener_ != null) {
                        actionListener_.actionPerformed(
                            new ActionEvent(
                                this,
                                ActionEvent.ACTION_PERFORMED,
                                "Dropped"
                            )
                        );
                    }
                    evt.dropComplete(dropSucceeded_);
                } else {
                    System.err.println("���ݡ��Ȥ��ʤ��ե졼��");
                    evt.dropComplete(false);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println("�ɥ�å׼���");
                evt.dropComplete(false);
            }
        } { // ���������Բ�?
            evt.rejectDrop();           // �ɥ�å��Ե�������
            evt.dropComplete(false);    // �ɥ�å׼�������
        }
    }

    /**
     * �ɥ�å��������äƤ���
     *
     * @param   evt �ɥ�å����٥��
     */
    public void dragEnter(DropTargetDragEvent evt) {
        if (
            evt.isDataFlavorSupported(AttributeInfo.dataFlavor)
            && (evt.getSourceActions() & DnDConstants.ACTION_COPY_OR_MOVE) != 0 // ���������?
        ) {
            evt.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);   // �ɥ�å�����������
        } else {
            evt.rejectDrag();   // �ɥ�å������������
        }
    }

    public void dragExit(DropTargetEvent evt) {}
    public void dragOver(DropTargetDragEvent evt) {}
    public void dropActionChanged(DropTargetDragEvent evt) {}
}
