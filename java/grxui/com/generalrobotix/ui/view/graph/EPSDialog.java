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

import com.generalrobotix.ui.util.MessageBundle;

/**
 * EPS���ϥ�������
 *
 * @author Kernel Inc.
 * @version 1.0 (2001/8/20)
 */
public class EPSDialog extends JDialog {

    // -----------------------------------------------------------------
    // ���
    // ����
    private static final int BORDER_GAP = 12;
    private static final int LABEL_GAP = 12;
    private static final int BUTTON_GAP = 5;
    private static final int ITEM_GAP = 11;
    private static final int CONTENTS_GAP = 17;

    // -----------------------------------------------------------------
    // ���󥹥����ѿ�
    String path_;           // �ѥ�
    boolean colorOutput_;   // ���顼���ϥե饰
    boolean graphOutput_;   // ����ս��ϥե饰
    boolean legendOutput_;  // ������ϥ����
    boolean updated_;       // �����ե饰
    JTextField pathField_;  // �ѥ����ϥե������
    JCheckBox colorCheck_;  // ���顼�����å��ܥå���
    JCheckBox graphCheck_;  // ����ս��ϥ����å��ܥå���
    JCheckBox legendCheck_; // ������ϥ����å��ܥå���
    JButton okButton_;      // OK�ܥ���
    JFileChooser chooser_;  // �ե����������������

    // -----------------------------------------------------------------
    // ���󥹥ȥ饯��
    /**
     * ���󥹥ȥ饯��
     *
     * @param   owner   �ƥե졼��
     */
    public EPSDialog(Frame owner) {
        super(owner, MessageBundle.get("dialog.graph.eps.title"), true);

        chooser_ = new JFileChooser(System.getProperty("user.dir"));

        // 1����(�ѥ�)
        JLabel outputLabel = new JLabel(MessageBundle.get("dialog.graph.eps.outputto"));
        int labelWidth = outputLabel.getMinimumSize().width;
        pathField_ = new JTextField("", 20);
        pathField_.setPreferredSize(new Dimension(400, 26));
        pathField_.setMaximumSize(new Dimension(400, 26));
        JButton browseButton = new JButton(MessageBundle.get("dialog.graph.eps.browse"));
        browseButton.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    //int result = chooser_.showSaveDialog(EPSDialog.this);
                    int result = chooser_.showDialog(EPSDialog.this, MessageBundle.get("dialog.okButton"));
                    if (result == JFileChooser.APPROVE_OPTION) {
                        pathField_.setText(
                            chooser_.getSelectedFile().getPath()
                        );
                    }
                }
            }
        );
        JPanel line1 = new JPanel();
        line1.setLayout(new BoxLayout(line1, BoxLayout.X_AXIS));
        line1.add(Box.createHorizontalStrut(BORDER_GAP));
        line1.add(outputLabel);
        //line1.add(Box.createHorizontalGlue());
        line1.add(Box.createHorizontalStrut(LABEL_GAP));
        line1.add(pathField_);
        line1.add(Box.createHorizontalStrut(5));
        line1.add(browseButton);
        //line1.add(Box.createHorizontalStrut(7));   // (��)Ĵ���ս�
        line1.add(Box.createHorizontalStrut(BORDER_GAP));
        line1.setAlignmentX(Component.LEFT_ALIGNMENT);

        // �����å��ܥå����ꥹ��
        ItemListener checkListener = new ItemListener() {
            public void itemStateChanged(ItemEvent evt) {
                okButton_.setEnabled(
                    graphCheck_.isSelected()
                    || legendCheck_.isSelected()
                );
            }
        };

        // 2����(����ե����å�)
        colorCheck_ = new JCheckBox(MessageBundle.get("dialog.graph.eps.color"));
        graphCheck_ = new JCheckBox(MessageBundle.get("dialog.graph.eps.graph"));
        graphCheck_.addItemListener(checkListener);
        legendCheck_ = new JCheckBox(MessageBundle.get("dialog.graph.eps.legend"));
        legendCheck_.addItemListener(checkListener);
        JPanel line2 = new JPanel();
        line2.setLayout(new BoxLayout(line2, BoxLayout.X_AXIS));
        line2.add(Box.createHorizontalStrut(BORDER_GAP));
        line2.add(Box.createHorizontalStrut(labelWidth));
        line2.add(Box.createHorizontalStrut(LABEL_GAP));
        line2.add(colorCheck_);
        line2.add(Box.createHorizontalStrut(LABEL_GAP));
        line2.add(graphCheck_);
        line2.add(Box.createHorizontalStrut(ITEM_GAP));
        line2.add(legendCheck_);
        line2.setAlignmentX(Component.LEFT_ALIGNMENT);

        // �ܥ����
        okButton_ = new JButton(MessageBundle.get("dialog.okButton"));
        this.getRootPane().setDefaultButton(okButton_);
        okButton_.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    // �ѥ��Υ����å�
                    String path = pathField_.getText().trim();
                    // (�����ʥѥ��ʤ饨�顼��������ɽ��)
                    // �͹���
                    if (!path.equals("") && !path.endsWith(".eps")) {
                        path += ".eps";
                    }
                    path_ = path;
                    colorOutput_ = colorCheck_.isSelected();
                    graphOutput_ = graphCheck_.isSelected();
                    legendOutput_ = legendCheck_.isSelected();
                    updated_ = true;
                    EPSDialog.this.setVisible(false);
                }
            }
        );
        JButton cancelButton = new JButton(MessageBundle.get("dialog.cancelButton"));
        cancelButton.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    EPSDialog.this.setVisible(false);
                }
            }
        );
        this.addKeyListener(
            new KeyAdapter() {
                public void keyPressed(KeyEvent evt) {
                    if (evt.getID() == KeyEvent.KEY_PRESSED
                        && evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        EPSDialog.this.setVisible(false);
                    }
                }
            }
        );
        JPanel bLine = new JPanel();
        bLine.setLayout(new BoxLayout(bLine, BoxLayout.X_AXIS));
        bLine.add(Box.createHorizontalGlue());
        bLine.add(okButton_);
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
        if (visible) {
            pathField_.setText(path_);
            colorCheck_.setSelected(colorOutput_);
            graphCheck_.setSelected(graphOutput_);
            legendCheck_.setSelected(legendOutput_);
            okButton_.setEnabled(graphOutput_ || legendOutput_);
            pathField_.requestFocus();   // ����ե�����������
            updated_ = false;
            pack();
        }
        super.setVisible(visible);
    }

    /**
     * �ѥ�����
     *
     * @param   path    �ѥ�
     */
    public void setPath(
        String path
    ) {
        path_ = path;
    }

    /**
     * ���顼���ϥե饰����
     *
     * @param   color   ���顼�ե饰
     */
    public void setColorOutput(
        boolean color
    ) {
        colorOutput_ = color;
    }


    /**
     * ����ս��ϥե饰����
     *
     * @param   output  ���ϥե饰
     */
    public void setGraphOutput(
        boolean output
    ) {
        graphOutput_ = output;
    }

    /**
     * ������ϥե饰����
     *
     * @param   output  ���ϥե饰
     */
    public void setLegendOutput(
        boolean output
    ) {
        legendOutput_ = output;
    }

    /**
     * �ѥ�����
     *
     * @return  �ѥ�
     */
    public String getPath() {
        return path_;
    }

    /**
     * ���顼���ϥե饰����
     *
     * @return  ���顼���ϥե饰
     */
    public boolean isColorOutput() {
        return colorOutput_;
    }

    /**
     * ����ս��ϥե饰����
     *
     * @return  ���ϥե饰
     */
    public boolean isGraphOutput() {
        return graphOutput_;
    }

    /**
     * ������ϥե饰����
     *
     * @return  ���ϥե饰
     */
    public boolean isLegendOutput() {
        return legendOutput_;
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
