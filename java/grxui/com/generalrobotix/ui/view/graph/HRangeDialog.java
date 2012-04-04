/*
 * Copyright (c) 2008, AIST, the University of Tokyo and General Robotix Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * General Robotix Inc.
 * National Institute of Advanced Industrial Science and Technology (AIST) 
 */
package com.generalrobotix.ui.view.graph;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.generalrobotix.ui.util.ErrorDialog;
import com.generalrobotix.ui.util.MessageBundle;

import java.text.DecimalFormat;

/**
 * ����տ�ʿ��������������
 *
 * @author Kernel Inc.
 * @version 1.0 (2001/8/20)
 */
public class HRangeDialog extends JDialog {

    // -----------------------------------------------------------------
    // ���
    // �����ե饰
    public static final int RANGE_UPDATED = 1;  // ����ѹ�
    public static final int POS_UPDATED = 2;    // �ޡ��������ѹ�
    // ����
    private static final int BORDER_GAP = 12;   // �������ֳ�
    private static final int LABEL_GAP = 12;    // ��٥�����Ƥκ���ֳ�
    private static final int BUTTON_GAP = 5;    // �ܥ���֤δֳ�
    private static final int ITEM_GAP = 11;     // �Ԥδֳ�
    private static final int CONTENTS_GAP = 17; // ���Ƥȥܥ���δֳ�
    // ����¾
    private static final String FORMAT_STRING = "0.000";    // ��󥸿��ͥե����ޥå�ʸ����
    private static final int MARKER_POS_STEPS = 10;         // �ޡ��������ʳ���

    // -----------------------------------------------------------------
    // ���󥹥����ѿ�
    int updateFlag_;    // �����ե饰
    double hRange_;     // �����
    double maxHRange_;  // ������
    double minHRange_;  // �Ǿ����
    double markerPos_;  // �ޡ�������
    //boolean updated_; // �����ե饰
    JTextField hRangeField_;    // ������ϥե������
    JSlider markerSlider_;      // �ޡ����������ꥹ�饤��
    DecimalFormat rangeFormat_; // ����ͤΥե����ޥå�

    // -----------------------------------------------------------------
    // ���󥹥ȥ饯��
    /**
     * ���󥹥ȥ饯��
     *
     * @param   owner   �ƥե졼��
     */
    public HRangeDialog(Frame owner) {
        super(owner, MessageBundle.get("dialog.graph.hrange.title"), true);

        // ��٥����Ĺ����
        JLabel label1 = new JLabel(MessageBundle.get("dialog.graph.hrange.hrange"));
        JLabel label2 = new JLabel(MessageBundle.get("dialog.graph.hrange.markerpos"));
        int lwid1 = label1.getMinimumSize().width;
        int lwid2 = label2.getMinimumSize().width;
        int lwidmax = (lwid1 > lwid2 ? lwid1 : lwid2);
        int lgap1 = lwidmax - lwid1 + LABEL_GAP;
        int lgap2 = lwidmax - lwid2 + LABEL_GAP;

        // 1����(��ʿ���)
        hRangeField_ = new JTextField("", 8);
        hRangeField_.setHorizontalAlignment(JTextField.RIGHT);
        hRangeField_.setPreferredSize(new Dimension(100, 26));
        hRangeField_.setMaximumSize(new Dimension(100, 26));
        hRangeField_.addFocusListener(
            new FocusAdapter() {
                public void focusGained(FocusEvent evt) {
                    hRangeField_.setSelectionStart(0);
                    hRangeField_.setSelectionEnd(
                        hRangeField_.getText().length()
                    );
                }
            }
        );
        JPanel line1 = new JPanel();
        line1.setLayout(new BoxLayout(line1, BoxLayout.X_AXIS));
        line1.add(Box.createHorizontalStrut(BORDER_GAP));
        line1.add(label1);
        line1.add(Box.createHorizontalStrut(lgap1));
        line1.add(hRangeField_);
        line1.add(Box.createHorizontalStrut(5));
        line1.add(new JLabel(MessageBundle.get("dialog.graph.hrange.unit")));
        line1.add(Box.createHorizontalGlue());
        line1.add(Box.createHorizontalStrut(BORDER_GAP));
        line1.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 2����(�ޡ�������)
        markerSlider_ = new JSlider(0, MARKER_POS_STEPS, 0);
        markerSlider_.setPreferredSize(new Dimension(200, 30));
        markerSlider_.setPaintTicks(true);
        markerSlider_.setMajorTickSpacing(1);
        markerSlider_.setSnapToTicks(true);
        JPanel line2 = new JPanel();
        line2.setLayout(new BoxLayout(line2, BoxLayout.X_AXIS));
        line2.add(Box.createHorizontalStrut(BORDER_GAP));
        line2.add(label2);
        line2.add(Box.createHorizontalStrut(lgap2));
        line2.add(markerSlider_);
        line2.add(Box.createHorizontalGlue());
        line2.add(Box.createHorizontalStrut(BORDER_GAP));
        line2.setAlignmentX(Component.LEFT_ALIGNMENT);

        // �ܥ����
        // OK�ܥ���
        JButton okButton = new JButton(MessageBundle.get("dialog.okButton"));
        okButton.setDefaultCapable(true);
        this.getRootPane().setDefaultButton(okButton);
        okButton.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    // �������ϥ����å�
                    double range;
                    try {
                        range = Double.parseDouble(hRangeField_.getText());
                    } catch (NumberFormatException ex) {
                        // ���顼ɽ��
                        new ErrorDialog(
                            HRangeDialog.this,
                            MessageBundle.get("dialog.graph.hrange.invalidinput.title"),
                            MessageBundle.get("dialog.graph.hrange.invalidinput.message")
                        ).showModalDialog();
                        hRangeField_.requestFocus();    // �ե�����������
                        return;
                    }
                    // �����ͥ����å�
                    if (range < minHRange_ || range > maxHRange_) {
                        // ���顼ɽ��
                        new ErrorDialog(
                            HRangeDialog.this,
                            MessageBundle.get("dialog.graph.hrange.invalidrange.title"),
                            MessageBundle.get("dialog.graph.hrange.invalidrange.message")
                                + "\n(" + minHRange_ + "  -  " + maxHRange_ + ")"
                        ).showModalDialog();
                        hRangeField_.requestFocus();    // �ե�����������
                        return;
                    }
                    double pos = markerSlider_.getValue() / (double)MARKER_POS_STEPS;
                    // ���������å�
                    updateFlag_ = 0;
                    if (range != hRange_) { // ��󥸹���?
                        hRange_ = range;
                        updateFlag_ += RANGE_UPDATED;
                    }
                    if (pos != markerPos_) {    // �ޡ�������?
                        markerPos_ = pos;
                        updateFlag_ += POS_UPDATED;
                    }
                    //hRange_ = range;
                    //markerPos_ = markerSlider_.getValue() / (double)MARKER_POS_STEPS;
                    //updated_ = true;
                    HRangeDialog.this.setVisible(false);    // ���������õ�
                }
            }
        );
        // ����󥻥�ܥ���
        JButton cancelButton = new JButton(MessageBundle.get("dialog.cancelButton"));
        cancelButton.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    HRangeDialog.this.setVisible(false);    // ���������õ�
                }
            }
        );
        this.addKeyListener(
            new KeyAdapter() {
                public void keyPressed(KeyEvent evt) {
                    if (evt.getID() == KeyEvent.KEY_PRESSED
                        && evt.getKeyCode() == KeyEvent.VK_ESCAPE) {    // ���������ײ���?
                        HRangeDialog.this.setVisible(false);    // ���������õ�
                    }
                }
            }
        );
        JPanel bLine = new JPanel();
        bLine.setLayout(new BoxLayout(bLine, BoxLayout.X_AXIS));
        bLine.add(Box.createHorizontalGlue());
        bLine.add(okButton);
        bLine.add(Box.createHorizontalStrut(BUTTON_GAP));
        bLine.add(cancelButton);
        bLine.add(Box.createHorizontalStrut(BORDER_GAP));
        bLine.setAlignmentX(Component.LEFT_ALIGNMENT);

        // �ѥͥ빽��
        Container pane = getContentPane();
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        pane.add(Box.createVerticalStrut(BORDER_GAP));
        pane.add(line1);
        pane.add(Box.createVerticalStrut(ITEM_GAP));
        pane.add(line2);
        pane.add(Box.createVerticalStrut(CONTENTS_GAP));
        pane.add(bLine);
        pane.add(Box.createVerticalStrut(BORDER_GAP));

        // ����¾
        rangeFormat_ = new DecimalFormat(FORMAT_STRING);    // ���ͥե����ޥå�����
        setResizable(false);  // �ꥵ�����Բ�
    }

    // -----------------------------------------------------------------
    // �᥽�å�
    /**
     * ɽ����ɽ������
     *
     * @param   visible ɽ����ɽ���ե饰
     */
    public void setVisible(
        boolean visible
    ) {
        if (visible) {  // ɽ������?
            hRangeField_.setText(rangeFormat_.format(hRange_)); // �������
            markerSlider_.setValue( // ���饤����������
                (int)(markerPos_ * MARKER_POS_STEPS)
            );
            hRangeField_.requestFocus();    // ����ե�����������
            //updated_ = false;
            updateFlag_ = 0;    // �����ե饰���ꥢ
            pack(); // ������ɥ�����������
        }
        super.setVisible(visible);  // ɽ������
    }

    /**
     * ��ʿ�������
     *
     * @param   hRange  ��ʿ���
     */
    public void setHRange(
        double hRange
    ) {
        hRange_ = hRange;
    }

    /**
     * ��ʿ��󥸺���������
     *
     * @param   maxHRange   ��ʿ��󥸺�����
     */
    public void setMaxHRange(
        double maxHRange
    ) {
        maxHRange_ = maxHRange;
    }

    /**
     * ��ʿ��󥸺Ǿ�������
     *
     * @param   minHRange   ��ʿ��󥸺Ǿ���
     */
    public void setMinHRange(
        double minHRange
    ) {
        minHRange_ = minHRange;
    }

    /**
     * �ޡ�����������
     *
     * @param   markerPos   �ޡ�������
     */
    public void setMarkerPos(
        double markerPos
    ) {
        markerPos_ = markerPos;
    }

    /**
     * ��ʿ��󥸼���
     *
     * @param   ��ʿ���
     */
    public double getHRange() {
        return hRange_;
    }

    /**
     * �ޡ������ּ���
     *
     * @param   �ޡ�������
     */
    public double getMarkerPos() {
        return markerPos_;
    }

    /**
     * �����ե饰����
     *
     * @param   �����ե饰
     */
    public int getUpdateFlag() {
        return updateFlag_;
    }

    /*
    public boolean isUpdated() {
        return updated_;
    }
    */
}
