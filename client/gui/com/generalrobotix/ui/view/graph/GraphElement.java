package com.generalrobotix.ui.view.graph;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.generalrobotix.ui.util.ErrorDialog;
import com.generalrobotix.ui.util.MessageBundle;

/**
 * ����ե������
 *   ����դ���������ܤ���ѥͥ�
 *
 * @author Kernel Inc.
 * @version 1.0 (2001/8/20)
 */
public class GraphElement
    extends JPanel
    implements MouseListener, ActionListener
{
    JSplitPane graphPane_;  // ʬ��ڥ���
    JComponent graph_;      // �����
    JComponent legend_;     // ����
    TrendGraph tg_;         // �ȥ��ɥ����

    ActionListener actionListener_; // ���������ꥹ����
    private static final String CMD_CLICKED = "clicked";    // ��������󥳥ޥ��̾

    // -----------------------------------------------------------------
    // ���󥹥ȥ饯��
    /**
     * ���󥹥ȥ饯��
     *
     * @param   tg      �ȥ��ɥ����
     * @param   graph   ����եѥͥ�
     * @param   legend  ����ѥͥ�
     */
    public GraphElement(
        TrendGraph tg,
        JComponent graph,
        JComponent legend
    ) {
        super();

        // ������¸
        tg_ = tg;   // �ȥ��ɥ����
        graph_ = graph; // �����
        legend_ = legend;   // ����

        // ���ץ�åȥڥ���
        graphPane_ = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            true,
            graph,
            legend
        );
        graphPane_.setResizeWeight(0.8);
        graphPane_.setDividerSize(6);
        setLayout(new BorderLayout());
        add(graphPane_, BorderLayout.CENTER);

        // �ꥹ������
        ((DroppableXYGraph)graph_).addActionListener(this); // �ɥ�åץ��������ꥹ��
        graph.addMouseListener(this);
        legend.addMouseListener(this);
        graphPane_.addMouseListener(this);
        addMouseListener(this);
    }

    // -----------------------------------------------------------------
    // �᥽�å�
    /**
     * �ȥ��ɥ���ռ���
     *
     * @return  �ȥ��ɥ����
     */
    public TrendGraph getTrendGraph() {
        return tg_;
    }

    /**
     * ����եѥͥ����
     *
     * @return  ����եѥͥ�
     */
    public JComponent getGraph() {
        return graph_;
    }

    /**
     * ����ѥͥ����
     *
     * @return  ����ѥͥ�
     */
    public JComponent getLegend() {
        return legend_;
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
    // MouseListener�μ���
    public void mousePressed(MouseEvent evt) {
        //System.out.println("Clicked");
        raiseActionEvent();
    }
    public void mouseClicked(MouseEvent evt){}
    public void mouseEntered(MouseEvent evt){}
    public void mouseExited(MouseEvent evt) {}
    public void mouseReleased(MouseEvent evt){}

    // -----------------------------------------------------------------
    // ActionListener�μ���
    public void actionPerformed(ActionEvent evt) {
        int result = tg_.addDataItem(
            (AttributeInfo)(((DroppableXYGraph)graph_).getDroppedObject())
        );
        if (result == TrendGraph.SUCCEEDED) {   // ����?
            ((DroppableXYGraph)graph_).setDropSucceeded(true);
            raiseActionEvent();
            repaint();
        } else if (result == TrendGraph.NOT_MATCHED) {  // �ǡ��������԰���?
            ((DroppableXYGraph)graph_).setDropSucceeded(false);
            new ErrorDialog(
                (Frame)null,
                MessageBundle.get("dialog.graph.mismatch.title"),
                MessageBundle.get("dialog.graph.mismatch.message")
            ).showModalDialog();
        } else {    // �ǡ��������󥵥ݡ���
            ((DroppableXYGraph)graph_).setDropSucceeded(false);
            new ErrorDialog(
                (Frame)null,
                MessageBundle.get("dialog.graph.unsupported.title"),
                MessageBundle.get("dialog.graph.unsupported.message")
            ).showModalDialog();
        }
    }

    /**
     * ��������󥤥٥��ȯ��(����å���)
     *
     * @return  ����ѥͥ�
     */
    private void raiseActionEvent() {
        if(actionListener_ != null) {
            actionListener_.actionPerformed(
                new ActionEvent(
                    this,
                    ActionEvent.ACTION_PERFORMED,
                    CMD_CLICKED
                )
            );
        }
    }
}
