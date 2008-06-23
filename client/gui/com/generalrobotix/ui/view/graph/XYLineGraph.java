package com.generalrobotix.ui.view.graph;

import javax.swing.*;
import java.util.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.text.*;

/**
 * �ޤ�������ե��饹
 *
 * @author Kernel Inc.
 * @version 1.0 (2001/8/20)
 */
public class XYLineGraph extends JPanel {

    // -----------------------------------------------------------------
    // ���
    // �����
    public static final int AXIS_LEFT   = 0;    // ����
    public static final int AXIS_RIGHT  = 1;    // ����
    public static final int AXIS_TOP    = 2;    // �弴
    public static final int AXIS_BOTTOM = 3;    // ����
    // ����դκǾ�������
    private static final int MIN_HEIGHT = 30;   // ����դκǾ���
    private static final int MIN_WIDTH = 30;    // ����դκǾ���
    // �ƥ��å��ȥ�٥�η��
    private static final int LABEL_GAP_LEFT = 5;
    private static final int LABEL_GAP_RIGHT = 4;
    private static final int LABEL_GAP_TOP = 3;
    private static final int LABEL_GAP_BOTTOM = 0;
    // ����ե饰
    private static final int DRAW_AXIS = 1;     // ������
    private static final int DRAW_TICK = 2;     // �ƥ��å�����
    private static final int DRAW_LABEL = 4;    // ��٥�����
    private static final int DRAW_GRID = 8;     // ����å�����
    private static final int DRAW_MARKER = 16;  // �ޡ�������

    private static final int EPS_SCALE = 20;            // EPS����������
    //private static final double EPS_LINE_WIDTH = 0.3;   // EPS����

    // -----------------------------------------------------------------
    // ���󥹥����ѿ�
    // �ޡ�����
    private int leftMargin_;    // ���ޡ�����
    private int rightMargin_;   // ���ޡ�����
    private int topMargin_;     // ��ޡ�����
    private int bottomMargin_;  // ���ޡ�����
    // ��
    private AxisInfo[] axisInfo_;   // ������
    // �ǡ�������
    private ArrayList<DataSeries> dsList_;  // �ǡ�������ꥹ��
    private HashMap<DataSeries, DataSeriesInfo>   dsInfoMap_;   // �ǡ����������ޥå�
    // ����
    private LegendPanel legendPanel_;    // ����ѥͥ�
    // ��
    private Color backColor_;       // �طʿ�
    private Color borderColor_;     // ���տ�
//    private Color nullAxisColor_;   // ����̵�����ο�

    private boolean epsMode_;

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
    public XYLineGraph(
        int leftMargin,     // ���ޡ�����
        int rightMargin,    // ���ޡ�����
        int topMargin,      // ��ޡ�����
        int bottomMargin    // ���ޡ�����
    ) {
        // �ޡ���������
        leftMargin_   = leftMargin;
        rightMargin_  = rightMargin;
        topMargin_    = topMargin;
        bottomMargin_ = bottomMargin;

        // ����ѥͥ�����
        legendPanel_ = new LegendPanel(
            new Font("dialog", Font.PLAIN, 10),
            Color.black,
            Color.white
        );

        // �ǥե���ȿ�����
        backColor_ = Color.black;
        borderColor_ = Color.black;
//        nullAxisColor_ = Color.darkGray;

        // �����󥯥ꥢ
        axisInfo_ = new AxisInfo[4];
        axisInfo_[AXIS_LEFT]   = null;
        axisInfo_[AXIS_RIGHT]  = null;
        axisInfo_[AXIS_TOP]    = null;
        axisInfo_[AXIS_BOTTOM] = null;

        // �ǡ�����������ǡ��������
        dsList_ = new ArrayList<DataSeries>();  // �ǡ�������ꥹ��
        dsInfoMap_ = new HashMap<DataSeries, DataSeriesInfo>(); // �ǡ����������ޥå�

        // EPS�⡼��
        epsMode_ = false;
    }

    // -----------------------------------------------------------------
    // �᥽�å�
    /**
     * EPS�⡼������
     *
     * @param   flag    �ե饰
     */
    public void setEPSMode(
        boolean flag
    ) {
        epsMode_ = flag;
    }

    /**
     * �طʿ�������
     *
     * @param   color   Color   ��
     */
    public void setBackColor(
        Color color
    ) {
        backColor_ = color;
    }

    /**
     * ���տ�������
     *
     * @param   color   Color   ��
     */
    public void setBorderColor(
        Color color
    ) {
        borderColor_ = color;
    }

    /**
     * �ǡ���������ɲ�
     *
     * @param   ds      DataSeries  �ǡ�������
     * @param   xai     AxisInfo    X������
     * @param   yai     AxisInfo    Y������
     * @param   color   Color       ��
     * @param   legend  String      ����ʸ����
     */
    public void addDataSeries(
        DataSeries ds,
        AxisInfo xai,
        AxisInfo yai,
        Color color, 
        String legend
    ) {
        DataSeriesInfo dsi = new DataSeriesInfo(
            xai, yai, color,
            new LegendInfo(color, legend)
        );
        legendPanel_.addLegend(dsi.legend);
        dsList_.add(ds);
        dsInfoMap_.put(ds, dsi);
    }

    /**
     * �ǡ�������κ��
     *
     * @param   ds      DataSeries  �ǡ�������
     */
    public void removeDataSeries(
        DataSeries ds
    ) {
        int ind = dsList_.indexOf(ds);
        dsList_.remove(ind);
        DataSeriesInfo dsi = (DataSeriesInfo)dsInfoMap_.get(ds);
        legendPanel_.removeLegend(dsi.legend);
        dsInfoMap_.remove(ds);
    }

    /**
     * ���ǡ�������μ���
     *
     * @return  Iterator    ���ǡ�������
     */
    public Iterator getDataSeries() {
        return (Iterator)dsList_.listIterator();
    }

    /**
     * �����������
     *      axis�ˤ�AXIS_LEFT,AXIS_RIGHT,AXIS_TOP,AXIS_BOTTOM����ꤹ��
     *      ai��null����ꤷ�����Ϥ��μ�����ɽ��
     *
     * @param   axis    int         ������
     * @param   ai      AxisInfo    ������
     */
    public void setAxisInfo(
        int axis,
        AxisInfo ai
    ) {
        axisInfo_[axis] = ai;
    }

    /**
     * ������μ���
     *      axis�ˤ�AXIS_LEFT,AXIS_RIGHT,AXIS_TOP,AXIS_BOTTOM����ꤹ��
     *
     * @param   axis    int         ������
     * @return  AxisInfo    ������
     */
    public AxisInfo getAxisInfo(
        int axis
    ) {
        return axisInfo_[axis];
    }

    /**
     * ����ե���Ȥ�����
     *
     * @param   font    Font    �ե����
     */
    public void setLegendFont(
        Font font
    ) {
        legendPanel_.setFont(font);
    }

    /**
     * �����٥뿧������
     *
     * @param   color   Color   ��
     */
    public void setLegendLabelColor(
        Color color
    ) {
        legendPanel_.setLabelColor(color);
    }

    /**
     * �����طʿ�������
     *
     * @param   color   Color   ��
     */
    public void setLegendBackColor(
        Color color
    ) {
        legendPanel_.setBackColor(color);
    }

    /**
     * �ǡ�������ο�������
     *
     * @param   ds      DataSeries  �ǡ�������
     * @param   color   Color       ��
     */
    public void setStyle(DataSeries ds, Color color) {
        DataSeriesInfo dsi = (DataSeriesInfo)dsInfoMap_.get(ds);
        dsi.color = color;
        dsi.legend.color = color;
    }

    /**
     * �ǡ�������ο��μ���
     *
     * @param   ds  �ǡ�������
     * @return  ��
     */
    public Color getStyle(DataSeries ds) {
        DataSeriesInfo dsi = (DataSeriesInfo)dsInfoMap_.get(ds);
        return dsi.color;
    }

    /**
     * �ǡ������������ʸ���������
     *
     * @param   ds      DataSeries  �ǡ�������
     * @param   legend  String      ����ʸ����
     */
    public void setLegendLabel(DataSeries ds, String legend) {
        DataSeriesInfo dsi = (DataSeriesInfo)dsInfoMap_.get(ds);
        dsi.legend.label = legend;
    }

    /**
     * �ǡ������������ʸ����μ���
     *
     * @param   ds  �ǡ�������
     * @return  ����ʸ����
     */
    public String getLegendLabel(DataSeries ds) {
        DataSeriesInfo dsi = (DataSeriesInfo)dsInfoMap_.get(ds);
        return dsi.legend.label;
    }

    /**
     * ����ѥͥ�μ���
     *
     * @return  JPanel  ����ѥͥ�
     */
    public JPanel getLegendPanel() {
        return legendPanel_;
    }

    /**
     * ����
     *
     * @param   g   Graphics    ����ե��å���
     */
    public void paint(
        Graphics g
    ) {
        //super.paint(g);

        // �������η���
        int width = getSize().width;
        int height = getSize().height;
        g.setColor(backColor_);
        g.fillRect(0, 0, width, height);
        int minWidth = leftMargin_ + MIN_WIDTH + rightMargin_;
        int minHeight = topMargin_ + MIN_HEIGHT + bottomMargin_;
        if (width < minWidth) {
            width = minWidth;
        }
        if (height < minHeight) {
            height = minHeight;
        }

        // �Ѻ�ɸ�η���
        int xl = leftMargin_;
        int xr = width - rightMargin_ - 1;
        int yt = topMargin_;
        int yb = height - bottomMargin_ - 1;

        // ����������ѹ�����
        if (epsMode_) {
            EPSGraphics eg = (EPSGraphics)g;
            eg.setScale(EPS_SCALE);
            //eg.setLineWidth(EPS_LINE_WIDTH);
            width *= EPS_SCALE;
            height *= EPS_SCALE;
            xl *= EPS_SCALE;
            xr *= EPS_SCALE;
            yt *= EPS_SCALE;
            yb *= EPS_SCALE;
        }

        // ����åɤ�����
        int flag = DRAW_GRID;
        drawAxis(g, xl, yt, xr, yb, /*width, height,*/ AXIS_LEFT,   flag);
        drawAxis(g, xl, yt, xr, yb, /*width, height,*/ AXIS_RIGHT,  flag);
        drawAxis(g, xl, yt, xr, yb, /*width, height,*/ AXIS_TOP,    flag);
        drawAxis(g, xl, yt, xr, yb, /*width, height,*/ AXIS_BOTTOM, flag);

        // ����åԥ�(EPS���Τ�)
        if (epsMode_) {
            g.setClip(xl, yt, xr - xl, yb - yt);
        }



        // �ǡ������������
        ListIterator li = dsList_.listIterator();
        while (li.hasNext()) {
            DataSeries ds = (DataSeries)li.next();

            DataSeriesInfo dsi = (DataSeriesInfo)dsInfoMap_.get(ds);

            double xbase = dsi.xAxisInfo.base;
            double ybase = dsi.yAxisInfo.base;
            double xscale = (xr - xl) / dsi.xAxisInfo.extent;
            double yscale = (yb - yt) / dsi.yAxisInfo.extent;
            double factor = dsi.yAxisInfo.factor;

            double xOffset = ds.getXOffset();
            double xStep = ds.getXStep();
            double[] data = ds.getData();
            int headPos = ds.getHeadPos();
            int length = data.length;
            int ox = 0, oy = 0;
            int nx = 0, ny = 0;
            boolean connect = false;
            g.setColor(dsi.color);
            int iofs = - headPos;
            //System.out.println("headPos=" + headPos);
            //System.out.println("headPos=" + headPos + " length=" + length);
            for (int i = headPos; i < length; i++) {
                if (Double.isNaN(data[i])) {    // �ǡ����ʤ�?
                    //if (i == headPos) {
                    //    System.out.println("data[i]=NaN");
                    //}
                    if (connect) {
                        //System.out.println("1 ox=" + ox + " oy=" + oy);
                        g.drawLine(ox, oy, ox, oy);
                        connect = false;
                    }
                } else {    // �ǡ�������?
                    nx = xl + (int)(((xStep * (i + iofs) + xOffset) - xbase) * xscale);
                    //if (i == headPos) {
                    //    System.out.println("iofs=" + iofs);
                    //    System.out.println("xOffset" + xOffset);
                    //    System.out.println("xbase" + xbase);
                    //    System.out.println("xscale" + xscale);
                    //}
                    ny = yb - (int)((data[i] * factor - ybase) * yscale);
                    if (connect) {
                        //System.out.println("2 ox=" + ox + " oy=" + oy + " nx=" + nx + " ny=" + ny);
                        g.drawLine(ox, oy, nx, ny);
                        ox = nx;
                        oy = ny;
                    } else {
                        ox = nx;
                        oy = ny;
                        connect = true;
                    }
                }
            }
            iofs = length - headPos;
            for (int i = 0; i < headPos; i++) {
                if (Double.isNaN(data[i])) {    // �ǡ����ʤ�?
                    if (connect) {
                        //System.out.println("3 ox=" + ox + " oy=" + oy);
                        g.drawLine(ox, oy, ox, oy);
                        connect = false;
                    }
                } else {    // �ǡ�������?
                    nx = xl + (int)(((xStep * (i + iofs) + xOffset) - xbase) * xscale);
                    ny = yb - (int)((data[i] * factor - ybase) * yscale);
                    if (connect) {
                        //System.out.println("4 ox=" + ox + " oy=" + oy + " nx=" + nx + " ny=" + ny);
                        g.drawLine(ox, oy, nx, ny);
                        ox = nx;
                        oy = ny;
                    } else {
                        ox = nx;
                        oy = ny;
                        connect = true;
                    }
                }
            }


        }


        if (epsMode_) {
            // ����åԥ󥰲��
            g.setClip(null);
        } else {
            // �ޥ�������
            g.setColor(borderColor_);
            g.fillRect(0,      0,      xl,     height);
            g.fillRect(xr + 1, 0,      width,  height);
            g.fillRect(0,      0,      width,  yt);
            g.fillRect(0,      yb + 1, width,  height);
        }

        // �Ƽ�������
        flag = DRAW_AXIS + DRAW_TICK + DRAW_LABEL + DRAW_MARKER;
        drawAxis(g, xl, yt, xr, yb, /*width, height,*/ AXIS_LEFT,   flag);
        drawAxis(g, xl, yt, xr, yb, /*width, height,*/ AXIS_RIGHT,  flag);
        drawAxis(g, xl, yt, xr, yb, /*width, height,*/ AXIS_TOP,    flag);
        drawAxis(g, xl, yt, xr, yb, /*width, height,*/ AXIS_BOTTOM, flag);

        /* ���¸� (ȾƩ���Υƥ���)
        width = getSize().width;
        height = getSize().height;
        g.setColor(new Color(0.5f, 0.5f, 0.5f, 0.5f));
        g.fillRect(0, 0, width, height);
        g.setColor(new Color(0.5f, 0.5f, 0.5f, 0.5f));
        g.fillRect(0, 0, width, height);
        */

        // ���������ꥻ�åȤ���
        if (epsMode_) {
            EPSGraphics eg = (EPSGraphics)g;
            eg.setScale(1);
        }


    }

    /**
     * ��������
     *
     * @param   g       Graphics    ����ե��å���
     * @param   width   int         ��
     * @param   height  int         �⤵
     * @param   axis    int         ������
     * @param   flag    int         ����ե饰
     */
    private void drawAxis(
        Graphics g,
        //int width,
        //int height,
        int xl,
        int yt,
        int xr,
        int yb,
        int axis,
        int flag
    ) {
        // �Ͷ��κ�ɸ�η���
        //int xl = leftMargin_;
        //int xr = width - rightMargin_ - 1;
        //int yt = topMargin_;
        //int yb = height - bottomMargin_ - 1;

        // ��������
        AxisInfo ai = axisInfo_[axis];  // ���������
        if (ai != null) {   // �����󤢤�?
            int tickLength = ai.tickLength;
            int unitXOfs = ai.unitXOfs;
            int unitYOfs = ai.unitYOfs;
            int ascale = 1;
            if (epsMode_) {
                tickLength *= EPS_SCALE;
                unitXOfs *= EPS_SCALE;
                unitYOfs *= EPS_SCALE;
                ascale = EPS_SCALE;
            }
            // ��
            if ((flag & DRAW_AXIS) != 0) {
                g.setColor(ai.color);
                switch (axis) {
                    case AXIS_LEFT:
                        g.drawLine(xl, yt, xl, yb);
                        break;
                    case AXIS_RIGHT:
                        g.drawLine(xr, yt, xr, yb);
                        break;
                    case AXIS_TOP:
                        g.drawLine(xl, yt, xr, yt);
                        break;
                    case AXIS_BOTTOM:
                        g.drawLine(xl, yb, xr, yb);
                        break;
                }
            }
            double base = ai.base;
            double extent = ai.extent;
            double scale = 1;
            switch (axis) {
                case AXIS_LEFT:
                case AXIS_RIGHT:
                    scale = (yb - yt) / extent;
                    break;
                case AXIS_TOP:
                case AXIS_BOTTOM:
                    scale = (xr - xl) / extent;
                    break;
            }
            double min = base;
            double max = base + extent;
            if (ai.minLimitEnabled && min < ai.min) {
                min = ai.min;
            }
            if (ai.maxLimitEnabled && max > ai.max) {
                max = ai.max;
            }
            // �ƥ��å�
            double every = ai.tickEvery;
            if (every > 0.0 && ((flag & DRAW_TICK) != 0)) {
                g.setColor(ai.color);
                int cfrom = (int)(Math.ceil(min / every));
                int cto = (int)(Math.floor(max / every));
                int pos;
                switch (axis) {
                    case AXIS_LEFT:
                        for (int i = cfrom; i <= cto; i ++) {
                            pos = yb - (int)((every * i - base) * scale);
                            g.drawLine(xl, pos, xl - tickLength, pos);
                        }
                        break;
                    case AXIS_RIGHT:
                        for (int i = cfrom; i <= cto; i ++) {
                            pos = yb - (int)((every * i - base) * scale);
                            g.drawLine(xr, pos, xr + tickLength, pos);
                        }
                        break;
                    case AXIS_TOP:
                        for (int i = cfrom; i <= cto; i ++) {
                            pos = xl + (int)((every * i - base) * scale);
                            g.drawLine(pos, yt, pos, yt - tickLength);
                        }
                        break;
                    case AXIS_BOTTOM:
                        for (int i = cfrom; i <= cto; i ++) {
                            pos = xl + (int)((every * i - base) * scale);
                            g.drawLine(pos, yb, pos, yb + tickLength);
                        }
                        break;
                }
            }
            // ����å�
            every = ai.gridEvery;
            if (every > 0.0 && ((flag & DRAW_GRID) != 0)) {
                g.setColor(ai.gridColor);
                int cfrom = (int)(Math.ceil(min / every));
                int cto = (int)(Math.floor(max / every));
                int pos;
                switch (axis) {
                    case AXIS_LEFT:
                    case AXIS_RIGHT:
                        for (int i = cfrom; i <= cto; i ++) {
                            pos = yb - (int)((every * i - base) * scale);
                            g.drawLine(xl + 1, pos, xr - 1, pos);
                        }
                        break;
                    case AXIS_TOP:
                    case AXIS_BOTTOM:
                        for (int i = cfrom; i <= cto; i ++) {
                            pos = xl + (int)((every * i - base) * scale);
                            g.drawLine(pos, yt + 1, pos, yb - 1);
                        }
                        break;
                }
            }
            // ��٥�
            every = ai.labelEvery;
            if (every > 0.0 && ((flag & DRAW_LABEL) != 0)) {
                DecimalFormat lformat = new DecimalFormat(ai.labelFormat);
                FontMetrics lmetrics = getFontMetrics(ai.labelFont);
                g.setColor(ai.labelColor);
                g.setFont(ai.labelFont);
                int cfrom = (int)(Math.ceil(min / every));
                int cto = (int)(Math.floor(max / every));
                int xpos, ypos;
                String lstr;
                switch (axis) {
                    case AXIS_LEFT:
                        for (int i = cfrom; i <= cto; i ++) {
                            lstr = lformat.format(every * i);
                            xpos = xl - tickLength - ascale * LABEL_GAP_LEFT
                                - ascale * lmetrics.stringWidth(lstr);
                            ypos = yb - (int)((every * i - base) * scale)
                                + (int)(ascale * lmetrics.getHeight() / 3.5);
                            g.drawString(lstr, xpos, ypos);
                        }
                        break;
                    case AXIS_RIGHT:
                        for (int i = cfrom; i <= cto; i ++) {
                            lstr = lformat.format(every * i);
                            xpos = xr + tickLength + ascale * LABEL_GAP_RIGHT;
                            ypos = yb - (int)((every * i - base) * scale)
                                + (int)(ascale * lmetrics.getHeight() / 3.5);
                            g.drawString(lstr, xpos, ypos);
                        }
                        break;
                    case AXIS_TOP:
                        ypos = yt - tickLength - ascale * LABEL_GAP_TOP;
                        for (int i = cfrom; i <= cto; i ++) {
                            lstr = lformat.format(every * i);
                            xpos = xl + (int)((every * i - base) * scale)
                                - ascale * lmetrics.stringWidth(lstr) / 2;
                            g.drawString(lstr, xpos, ypos);
                        }
                        break;
                    case AXIS_BOTTOM:
                        ypos = yb + tickLength
                            + ascale * LABEL_GAP_BOTTOM
                            + ascale * lmetrics.getHeight();
                        for (int i = cfrom; i <= cto; i ++) {
                            lstr = lformat.format(every * i);
                            xpos = xl + (int)((every * i - base) * scale)
                                - ascale * lmetrics.stringWidth(lstr) / 2;
                            g.drawString(lstr, xpos, ypos);
                        }
                        break;
                }
                // ñ��
                FontMetrics umetrics = getFontMetrics(ai.unitFont);
                int ux, uy;
                g.setColor(ai.unitColor);
                g.setFont(ai.unitFont);
                switch (axis) {
                    case AXIS_LEFT:
                        ux = xl - unitXOfs - ascale * umetrics.stringWidth(ai.unitLabel);
                        uy = yt - unitYOfs;
                        g.drawString(ai.unitLabel, ux, uy);
                        break;
                    case AXIS_RIGHT:
                        ux = xr + unitXOfs;
                        uy = yt - unitYOfs;
                        g.drawString(ai.unitLabel, ux, uy);
                        break;
                    case AXIS_TOP:
                        ux = xr + unitXOfs;
                        uy = yt - unitYOfs;
                        g.drawString(ai.unitLabel, ux, uy);
                        break;
                    case AXIS_BOTTOM:
                        ux = xr + unitXOfs;
                        uy = yb + unitYOfs + ascale * umetrics.getHeight();
                        g.drawString(ai.unitLabel, ux, uy);
                        break;
                }
            }
            // �ޡ���
            if (ai.markerVisible
                //&& ai.markerPos >= min && ai.markerPos <= max
                && ((flag & DRAW_MARKER) != 0)) {
                g.setColor(Color.white);
                g.setXORMode(ai.markerColor);
                int pos;
                switch (axis) {
                    case AXIS_LEFT:
                    case AXIS_RIGHT:
                        //pos = yb - (int)((ai.markerPos - base) * scale);
                        //pos = yb - (int)(ai.markerPos * scale);
                        pos = yb - (int)(ai.markerPos * (yb - yt));
                        g.drawLine(xl + 1, pos - 1, xr - 1, pos - 1);
                        g.drawLine(xl + 1, pos,     xr - 1, pos);
                        g.drawLine(xl + 1, pos + 1, xr - 1, pos + 1);
                        break;
                    case AXIS_TOP:
                    case AXIS_BOTTOM:
                        //pos = xl + (int)((ai.markerPos - base) * scale);
                        //pos = xl + (int)(ai.markerPos * scale);
                        pos = xl + (int)(ai.markerPos * (xr - xl));
                        g.drawLine(pos - 1, yt + 1, pos - 1, yb - 1);
                        g.drawLine(pos,     yt + 1, pos,     yb - 1);
                        g.drawLine(pos + 1, yt + 1, pos + 1, yb - 1);
                        break;
                }
                g.setPaintMode();
            }
        } /* else {    // ������ʤ�?
            if ((flag & DRAW_AXIS) != 0) {
                g.setColor(nullAxisColor_);
                switch (axis) {
                    case AXIS_LEFT:
                        g.drawLine(xl, yt + 1, xl, yb - 1);
                        break;
                    case AXIS_RIGHT:
                        g.drawLine(xr, yt + 1, xr, yb - 1);
                        break;
                    case AXIS_TOP:
                        g.drawLine(xl + 1, yt, xr - 1, yt);
                        break;
                    case AXIS_BOTTOM:
                        g.drawLine(xl + 1, yb, xr - 1, yb);
                        break;
                }
            }
        } */
    }

    // -----------------------------------------------------------------
    // �������饹
    /**
     * �ǡ���������󥯥饹
     *
     */
    private class DataSeriesInfo {

        // -----------------------------------------------------------------
        // ���󥹥����ѿ�
        public AxisInfo   xAxisInfo;    // X������
        public AxisInfo   yAxisInfo;    // Y������
        public Color      color;        // ���迧
        public LegendInfo legend;       // �������

        // -----------------------------------------------------------------
        // ���󥹥ȥ饯��
        /**
         * ���󥹥ȥ饯��
         *
         * @param   xAxisInfo   AxisInfo    X������
         * @param   yAxisInfo   AxisInfo    Y������
         * @param   color       Color       ���迧
         * @param   legend      LegendInfo  �������
         */
        public DataSeriesInfo(
            AxisInfo xAxisInfo,
            AxisInfo yAxisInfo,
            Color    color,
            LegendInfo   legend
        ) {
            this.xAxisInfo = xAxisInfo;
            this.yAxisInfo = yAxisInfo;
            this.color     = color;
            this.legend    = legend;
        }
    }

    /**
     * ����ѥͥ륯�饹
     *
     */
    public class LegendPanel extends JPanel {

        // -----------------------------------------------------------------
        // ���
        private static final int MARGIN_X = 15;
        private static final int MARGIN_Y = 15;
        private static final int GAP_X = 10;
        private static final int GAP_Y = 5;
        private static final int LEN_LINE = 20;

        // -----------------------------------------------------------------
        // ���󥹥����ѿ�
        private ArrayList<LegendInfo> legendList_;  // �������ꥹ��
        private Font font_;             // ��٥�ե����
        private Color backColor_;       // �طʿ�
        private Color labelColor_;      // ��٥뿧
        private Dimension size_;    // �ѥͥ륵����

        // -----------------------------------------------------------------
        // ���󥹥ȥ饯��
        /**
         * ���󥹥ȥ饯��
         *
         * @param   font        Font    ��٥�ե����
         * @param   backColor   Color   �طʿ�
         * @param   labelColor  Color   ��٥뿧
         */
        public LegendPanel(
            Font font,
            Color backColor,
            Color labelColor
        ) {
            font_ = font;
            backColor_ = backColor;
            labelColor_ = labelColor;
            size_ = new Dimension(0, 0);
            //setPreferredSize(size_);
            legendList_ = new ArrayList<LegendInfo>();
        }

        /**
         * �����ɲ�
         *
         * @param   legend  LegendInfo  �������
         */
        public void addLegend(
            LegendInfo legend
        ) {
            legendList_.add(legend);
            updateSize();
        }

        /**
         * ������
         *
         * @param   legend  LegendInfo  �������
         */
        public void removeLegend(
            LegendInfo legend
        ) {
            int ind = legendList_.indexOf(legend);
            legendList_.remove(ind);
            updateSize();
        }

        /**
         * ��٥�ե��������
         *
         * @param   font        Font    ��٥�ե����
         */
        public void setFont(
            Font font
        ) {
            font_ = font;
        }

        /**
         * �طʿ�
         *
         * @param   backColor   Color   �طʿ�
         */
        public void setBackColor(
            Color color
        ) {
            backColor_ = color;
        }

        /**
         * ��٥뿧����
         *
         * @param   labelColor  Color   ��٥뿧
         */
        public void setLabelColor(
            Color color
        ) {
            labelColor_ = color;
        }

        /**
         * ����
         *
         * @param   g   Graphics    ����ե��å���
         */
        public void paint(
            Graphics g
        ) {
            // �ط�
            int width = getSize().width;
            int height = getSize().height;
            g.setColor(backColor_);
            g.fillRect(0, 0, width, height);
            // ����
            g.setFont(font_);
            FontMetrics metrics = getFontMetrics(font_);    // �ե���ȥ�ȥꥯ��
            int yofs = (int)(metrics.getHeight() / 3.5);    // ��٥�Y���ե��å�
            int ygap = metrics.getHeight() + GAP_Y;         // Y�ֳ�
            ListIterator li = legendList_.listIterator();
            int ypos = MARGIN_Y;    // Y���ֽ����
            while (li.hasNext()) {  // �������롼��
                LegendInfo legend = (LegendInfo)li.next();  // ��������
                g.setColor(legend.color);   // ��������
                //g.drawLine(MARGIN_X, ypos - 1, MARGIN_X + LEN_LINE, ypos - 1);
                g.drawLine(MARGIN_X, ypos, MARGIN_X + LEN_LINE, ypos);  // ��������
                //g.drawLine(MARGIN_X, ypos + 1, MARGIN_X + LEN_LINE, ypos + 1);
                g.setColor(labelColor_);    // ��٥뿧
                g.drawString(   // ��٥������
                    legend.label,
                    MARGIN_X + LEN_LINE + GAP_X,
                    ypos + yofs
                );
                ypos += ygap;   // Y���֤ι���
            }
        }

        /**
         * ɬ�׽�ʬ����������
         *   �����ɽ������Τ�ɬ�׽�ʬ�ʥ��������������
         *      (�������Ǥϻ��Ѥ��Ƥ��ʤ�)
         *
         */
        public Dimension getMinimalSize() {
            return size_;
        }

        /**
         * ����������
         *   ����ο���Ĺ���˱����ƥѥͥ�Υ���������ꤹ��
         *
         */
        private void updateSize() {
            FontMetrics metrics = getFontMetrics(font_);    // �ե���ȥ�ȥꥯ��
            int ygap = metrics.getHeight() + GAP_Y; // Y�ֳ�
            ListIterator li = legendList_.listIterator();
            int ysize = MARGIN_Y;   // �⤵
            int max = 0;    // ��٥����Ĺ
            while (li.hasNext()) {  // �������롼��
                LegendInfo legend = (LegendInfo)li.next();
                int len = metrics.stringWidth(legend.label);    // ��٥��Ĺ�������
                if (len > max) {    // ����Ĺ?
                    max = len;  // ����Ĺ�򹹿�
                }
                if (li.hasNext()) { // �Ǹ������Ǥʤ�?
                    ysize += ygap;  // �⤵�򹹿�
                }
            }
            ysize += MARGIN_Y;  // ���ޡ�����
            size_.width = MARGIN_X + LEN_LINE + GAP_X + max + MARGIN_X; // ������
            size_.height = ysize;   // �⤵����
            //System.out.println("ygap = " + ygap);
            //System.out.println("(" + size_.width + ", " + size_.height + ")");
        }
    }

    /**
     * ������󥯥饹
     *
     */
    private class LegendInfo {

        // -----------------------------------------------------------------
        // ���󥹥����ѿ�
        public Color color;     // ��
        public String label;    // ��٥�

        // -----------------------------------------------------------------
        // ���󥹥ȥ饯��
        /**
         * ���󥹥ȥ饯��
         *
         * @param   color   Color   ���迧
         * @param   label   String  ��٥�
         */
        public LegendInfo(
            Color color,
            String label
        ) {
            this.color = color;
            this.label = label;
        }
    }
}
