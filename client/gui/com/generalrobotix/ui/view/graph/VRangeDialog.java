package com.generalrobotix.ui.view.graph;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.generalrobotix.ui.util.ErrorDialog;
import com.generalrobotix.ui.util.MessageBundle;

import java.text.DecimalFormat;

/**
 * ����տ�ľ��������������
 *
 * @author Kernel Inc.
 * @version 1.0 (2001/8/20)
 */
public class VRangeDialog extends JDialog {

    // -----------------------------------------------------------------
    // ���
    // ����
    private static final int BORDER_GAP = 12;   // �������ֳ�
    private static final int LABEL_GAP = 12;    // ��٥�����Ƥκ���ֳ�
    private static final int BUTTON_GAP = 5;    // �ܥ���֤δֳ�
    private static final int ITEM_GAP = 11;     // �Ԥδֳ�
    private static final int CONTENTS_GAP = 17; // ���Ƥȥܥ���δֳ�
    // ����¾
    private static final String FORMAT_STRING = "0.000";    // ��󥸿��ͥե����ޥå�ʸ����

    // -----------------------------------------------------------------
    // ���󥹥����ѿ�
    double base_;       // �����
    double extent_;     // ��
    boolean updated_;   // �����ե饰
    //String unit_;       // ñ��ʸ����
    JTextField minField_;   // �Ǿ��ͥե������
    JTextField maxField_;   // �����ͥե������
    JLabel minUnitLabel_;   // �Ǿ���ñ�̥�٥�
    JLabel maxUnitLabel_;   // ������ñ�̥�٥�
    DecimalFormat rangeFormat_; // ����ͤΥե����ޥå�

    // -----------------------------------------------------------------
    // ���󥹥ȥ饯��
    /**
     * ���󥹥ȥ饯��
     *
     * @param   owner   �ƥե졼��
     */
    public VRangeDialog(Frame owner) {
        super(owner, MessageBundle.get("dialog.graph.vrange.title"), true);

        // ��٥����Ĺ����
        JLabel label1 = new JLabel(MessageBundle.get("dialog.graph.vrange.min"));
        JLabel label2 = new JLabel(MessageBundle.get("dialog.graph.vrange.max"));
        int lwid1 = label1.getMinimumSize().width;
        int lwid2 = label2.getMinimumSize().width;
        int lwidmax = (lwid1 > lwid2 ? lwid1 : lwid2);
        int lgap1 = lwidmax - lwid1 + LABEL_GAP;
        int lgap2 = lwidmax - lwid2 + LABEL_GAP;

        // 1����(�Ǿ���)
        minField_ = new JTextField("", 8);
        minField_.setHorizontalAlignment(JTextField.RIGHT);
        minField_.setPreferredSize(new Dimension(100, 26));
        minField_.setMaximumSize(new Dimension(100, 26));
        minField_.addFocusListener(
            new FocusAdapter() {
                public void focusGained(FocusEvent evt) {
                    minField_.setSelectionStart(0);
                    minField_.setSelectionEnd(
                        minField_.getText().length()
                    );
                }
            }
        );
        minUnitLabel_ = new JLabel("");
        minUnitLabel_.setPreferredSize(new Dimension(50, 26));
        minUnitLabel_.setMaximumSize(new Dimension(50, 26));
        JPanel line1 = new JPanel();
        line1.setLayout(new BoxLayout(line1, BoxLayout.X_AXIS));
        line1.add(Box.createHorizontalStrut(BORDER_GAP));
        line1.add(label1);
        line1.add(Box.createHorizontalStrut(lgap1));
        line1.add(minField_);
        line1.add(Box.createHorizontalStrut(5));
        line1.add(minUnitLabel_);
        line1.add(Box.createHorizontalGlue());
        line1.add(Box.createHorizontalStrut(BORDER_GAP));
        line1.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 2����(������)
        maxField_ = new JTextField("", 8);
        maxField_.setHorizontalAlignment(JTextField.RIGHT);
        maxField_.setPreferredSize(new Dimension(100, 26));
        maxField_.setMaximumSize(new Dimension(100, 26));
        maxField_.addFocusListener(
            new FocusAdapter() {
                public void focusGained(FocusEvent evt) {
                    maxField_.setSelectionStart(0);
                    maxField_.setSelectionEnd(
                        maxField_.getText().length()
                    );
                }
            }
        );
        maxUnitLabel_ = new JLabel("");
        maxUnitLabel_.setPreferredSize(new Dimension(50, 26));
        maxUnitLabel_.setMaximumSize(new Dimension(50, 26));
        JPanel line2 = new JPanel();
        line2.setLayout(new BoxLayout(line2, BoxLayout.X_AXIS));
        line2.add(Box.createHorizontalStrut(BORDER_GAP));
        line2.add(label2);
        line2.add(Box.createHorizontalStrut(lgap2));
        line2.add(maxField_);
        line2.add(Box.createHorizontalStrut(5));
        line2.add(maxUnitLabel_);
        line2.add(Box.createHorizontalGlue());
        line2.add(Box.createHorizontalStrut(BORDER_GAP));
        line2.setAlignmentX(Component.LEFT_ALIGNMENT);

        // �ܥ����
        JButton okButton = new JButton(MessageBundle.get("dialog.okButton"));
        okButton.setDefaultCapable(true);
        this.getRootPane().setDefaultButton(okButton);
        okButton.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    // �������ϥ����å�
                    double min, max;
                    try {
                        min = Double.parseDouble(minField_.getText());
                        max = Double.parseDouble(maxField_.getText());
                    } catch (NumberFormatException ex) {
                        // ���顼ɽ��
/*
                        JOptionPane.showMessageDialog(
                            VRangeDialog.this,
                            MessageBundle.get("dialog.graph.vrange.invalidinput.message"),
                            MessageBundle.get("dialog.graph.vrange.invalidinput.title"),
                            JOptionPane.ERROR_MESSAGE
                        );
*/

                        new ErrorDialog(
                            VRangeDialog.this,
                            MessageBundle.get("dialog.graph.vrange.invalidinput.title"),
                            MessageBundle.get("dialog.graph.vrange.invalidinput.message")
                        ).showModalDialog();

                        minField_.requestFocus();   // �ե�����������
                        return;
                    }
                    // �����ͥ����å�
                    if (min == max) {
                        // ���顼ɽ��
/*
                        JOptionPane.showMessageDialog(
                            VRangeDialog.this,
                            MessageBundle.get("dialog.graph.vrange.invalidrange.message"),
                            MessageBundle.get("dialog.graph.vrange.invalidrange.title"),
                            JOptionPane.ERROR_MESSAGE
                        );
*/

                        new ErrorDialog(
                            VRangeDialog.this,
                            MessageBundle.get("dialog.graph.vrange.invalidrange.title"),
                            MessageBundle.get("dialog.graph.vrange.invalidrange.message")
                        ).showModalDialog();

                        minField_.requestFocus();   // �ե�����������
                        return;
                    }
                    // �͹���
                    if (min < max) {
                        base_ = min;
                        extent_ = max - min;
                    } else {
                        base_ = max;
                        extent_ = min - max;
                    }
                    updated_ = true;
                    VRangeDialog.this.setVisible(false);
                }
            }
        );
        // ����󥻥�ܥ���
        JButton cancelButton = new JButton(MessageBundle.get("dialog.cancelButton"));
        cancelButton.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    VRangeDialog.this.setVisible(false);    // ���������õ�
                }
            }
        );
        this.addKeyListener(
            new KeyAdapter() {
                public void keyPressed(KeyEvent evt) {
                    if (evt.getID() == KeyEvent.KEY_PRESSED
                        && evt.getKeyCode() == KeyEvent.VK_ESCAPE) {    // ���������ײ���?
                        VRangeDialog.this.setVisible(false);    // ���������õ�
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
            minField_.setText(rangeFormat_.format(base_));              // �Ǿ�������
            maxField_.setText(rangeFormat_.format(base_ + extent_));    // ����������
            minField_.requestFocus();   // ����ե�����������
            updated_ = false;   // �����ե饰���ꥢ
            pack(); // ������ɥ�����������
        }
        super.setVisible(visible);  // ɽ������
    }

    /**
     * ñ������
     *
     * @param   unit    ñ��ʸ����
     */
    public void setUnit(
        String unit
    ) {
        //unit_ = unit;
        minUnitLabel_.setText(unit);
        maxUnitLabel_.setText(unit);
    }

    /**
     * ���������
     *
     * @param   base    �����
     */
    public void setBase(
        double base
    ) {
        base_ = base;
    }

    /**
     * ������
     *
     * @param   extent  ��
     */
    public void setExtent(
        double extent
    ) {
        extent_ = extent;
    }

    /**
     * ����ͼ���
     *
     * @param   �����
     */
    public double getBase() {
        return base_;
    }

    /**
     * ������
     *
     * @param   ��
     */
    public double getExtent() {
        return extent_;
    }

    /**
     * �����ե饰����
     *
     * @param   �����ե饰
     */
    public boolean isUpdated() {
        return updated_;
    }
}
