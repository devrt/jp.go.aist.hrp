package com.generalrobotix.ui.view.graph;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;

/**
 * EPS����ե��å���
 *
 * @author Kernel Inc.
 * @version 1.0 (2001/8/20)
 */
public class EPSGraphics extends Graphics {

    // -----------------------------------------------------------------
    // ���
    // �إå���
    private static final String HEADER = "%!PS-Adobe-3.0 EPSF-3.0";
    private static final String BOUNDING_BOX = "%%BoundingBox:";
    private static final String DEF = "/";
    private static final String BIND_DEF = " bind def";
    private static final String EOF = "%%EOF";
    // �����ڥ졼��
    private static final String SET_COLOR_WHITE     = "setColorWhite";
    private static final String SET_COLOR_LIGHTGRAY = "setColorLightGray";
    private static final String SET_COLOR_GRAY      = "setColorGray";
    private static final String SET_COLOR_DARKGRAY  = "setColorDarkGray";
    private static final String SET_COLOR_BLACK     = "setColorBlack";
    private static final String SET_COLOR_OTHERS    = "setColorOthers";
    private static final String SET_COLOR_GREEN     = "setColorGreen";
    private static final String SET_COLOR_YELLOW    = "setColorYellow";
    private static final String SET_COLOR_PINK      = "setColorPink";
    private static final String SET_COLOR_CYAN      = "setColorCyan";
    private static final String SET_COLOR_MAGENTA   = "setColorMagenta";
    private static final String SET_COLOR_RED       = "setColorRed";
    private static final String SET_COLOR_ORANGE    = "setColorOrange";
    private static final String SET_COLOR_BLUE      = "setColorBlue";
    // �����ڥ졼�����(�����)
    private static final String DEF_COLOR_WHITE
        = DEF + SET_COLOR_WHITE     + " {0.3 setlinewidth 0 setgray [] 0 setdash}" + BIND_DEF;
    private static final String DEF_COLOR_LIGHTGRAY
        = DEF + SET_COLOR_LIGHTGRAY + " {0.3 setlinewidth 0.2 setgray [] 0 setdash}" + BIND_DEF;
    private static final String DEF_COLOR_GRAY
        = DEF + SET_COLOR_GRAY      + " {0.3 setlinewidth 0.5 setgray [] 0 setdash}" + BIND_DEF;
    private static final String DEF_COLOR_DARKGRAY
        = DEF + SET_COLOR_DARKGRAY  + " {0.3 setlinewidth 0.7 setgray [] 0 setdash}" + BIND_DEF;
    private static final String DEF_COLOR_BLACK
        = DEF + SET_COLOR_BLACK     + " {0.3 setlinewidth 1 setgray [] 0 setdash}" + BIND_DEF;
    private static final String DEF_COLOR_OTHERS
        = DEF + SET_COLOR_OTHERS    + " {0.3 setlinewidth 0 setgray [] 0 setdash}" + BIND_DEF;
    private static final String DEF_COLOR_GREEN
        = DEF + SET_COLOR_GREEN     + " {0.3 setlinewidth 0 setgray [] 0 setdash}" + BIND_DEF;
    private static final String DEF_COLOR_YELLOW
        = DEF + SET_COLOR_YELLOW    + " {0.3 setlinewidth 0.4 setgray [] 0 setdash}" + BIND_DEF;
    private static final String DEF_COLOR_PINK
        = DEF + SET_COLOR_PINK      + " {0.3 setlinewidth 0.7 setgray [] 0 setdash}" + BIND_DEF;
    private static final String DEF_COLOR_CYAN
        = DEF + SET_COLOR_CYAN      + " {0.6 setlinewidth 0.7 setgray [] 0 setdash}" + BIND_DEF;
    private static final String DEF_COLOR_MAGENTA
        = DEF + SET_COLOR_MAGENTA   + " {0.9 setlinewidth 0 setgray [] 0 setdash}" + BIND_DEF;
    private static final String DEF_COLOR_RED
        = DEF + SET_COLOR_RED       + " {0.9 setlinewidth 0.7 setgray [] 0 setdash}" + BIND_DEF;
    private static final String DEF_COLOR_ORANGE
        = DEF + SET_COLOR_ORANGE    + " {0.9 setlinewidth 0 setgray [6 2 2 2] 0 setdash}" + BIND_DEF;
    private static final String DEF_COLOR_BLUE
        = DEF + SET_COLOR_BLUE      + " {0.9 setlinewidth 0.7 setgray [6 2 2 2] 0 setdash}" + BIND_DEF;

    // ���襪�ڥ졼��
    private static final String DRAW_LINE     = "drawLine";
    private static final String SET_FONT      = "setFont";
    private static final String DRAW_STRING   = "drawString";
    private static final String SET_CLIP      = "setClip";
    private static final String SET_CLIP_NULL = "setClipNull";
    private static final String NEWPATH = "N";
    private static final String MOVETO = "M";
    private static final String LINETO = "L";
    private static final String STROKE = "S";
    // ���襪�ڥ졼�����
    private static final String DEF_DRAW_LINE
        = DEF + DRAW_LINE     + " {newpath moveto lineto stroke}" + BIND_DEF;
    private static final String DEF_SET_FONT
        = DEF + SET_FONT      + " {exch findfont exch scalefont setfont}" + BIND_DEF;
    private static final String DEF_DRAW_STRING
        = DEF + DRAW_STRING   + " {moveto show}" + BIND_DEF;
    private static final String DEF_SET_CLIP
        = DEF + SET_CLIP
        + " {gsave newpath 3 index 3 index moveto dup 0 exch"
        + " rlineto exch 0 rlineto 0 exch sub 0 exch"
        + " rlineto pop pop closepath clip}"
        + BIND_DEF;
    private static final String DEF_SET_CLIP_NULL
        = DEF + SET_CLIP_NULL + " {grestore}" + BIND_DEF;
    private static final String DEF_NEWPATH = DEF + NEWPATH + " {newpath}" + BIND_DEF;
    private static final String DEF_MOVETO  = DEF + MOVETO  + " {moveto}"  + BIND_DEF;
    private static final String DEF_LINETO  = DEF + LINETO  + " {lineto}"  + BIND_DEF;
    private static final String DEF_STROKE  = DEF + STROKE  + " {stroke}"  + BIND_DEF;
    // �極����
    private static final int PAGE_HEIGHT = 792;
    //private static final int PAGE_WIDTH = 612;

    // -----------------------------------------------------------------
    // ���󥹥����ѿ�
    private PrintWriter pw;         // �ץ��ȥ饤��
    private ArrayList<Color> colorList_;   // ������
    private ArrayList<String> colorOps_;    // �����ڥ졼������
    private double scale_;          // ��������
    private boolean inPath_;        // �ѥ���³��ե饰
    private int prevX_;             // ����X��ɸ
    private int prevY_;             // ����Y��ɸ
    private int xOfs_;              // X��ɸ���ե��å�
    private int yOfs_;              // Y��ɸ���ե��å�

    // -----------------------------------------------------------------
    // ���󥹥ȥ饯��
    /**
     * ���󥹥ȥ饯��
     *
     * @param   writer  �饤��
     * @param   top     �Х���ǥ��󥰥ܥå������ɸ
     * @param   left    �Х���ǥ��󥰥ܥå�������ɸ
     * @param   width   �Х���ǥ��󥰥ܥå�����
     * @param   heigt   �Х���ǥ��󥰥ܥå����⤵
     * @param   color   ���顼�ե饰
     */
    public EPSGraphics(
        Writer writer,
        int top,
        int left,
        int width,
        int height,
        boolean color
    ) {
        super();
        // ������
        colorList_ = new ArrayList<Color>();
        colorList_.add(Color.white);
        colorList_.add(Color.lightGray);
        colorList_.add(Color.gray);
        colorList_.add(Color.darkGray);
        colorList_.add(Color.black);
        colorList_.add(Color.green);
        colorList_.add(Color.yellow);
        colorList_.add(Color.pink);
        colorList_.add(Color.cyan);
        colorList_.add(Color.magenta);
        colorList_.add(Color.red);
        colorList_.add(Color.orange);
        colorList_.add(Color.blue);
        colorOps_ = new ArrayList<String>();
        colorOps_.add(SET_COLOR_WHITE);
        colorOps_.add(SET_COLOR_LIGHTGRAY);
        colorOps_.add(SET_COLOR_GRAY);
        colorOps_.add(SET_COLOR_DARKGRAY);
        colorOps_.add(SET_COLOR_BLACK);
        colorOps_.add(SET_COLOR_GREEN);
        colorOps_.add(SET_COLOR_YELLOW);
        colorOps_.add(SET_COLOR_PINK);
        colorOps_.add(SET_COLOR_CYAN);
        colorOps_.add(SET_COLOR_MAGENTA);
        colorOps_.add(SET_COLOR_RED);
        colorOps_.add(SET_COLOR_ORANGE);
        colorOps_.add(SET_COLOR_BLUE);
        // ����¾������
        scale_ = 1;         // �������륯�ꥢ
        inPath_ = false;    // �ѥ���³��Ǥʤ�
        xOfs_ = 0;          // X���ե��åȥ��ꥢ
        yOfs_ = 0;          // Y���ե��åȥ��ꥢ
        // �إå�����
        pw = new PrintWriter(writer);   // �ץ��ȥ饤�������ץ�
        _writeHeader(top, left, width, height, color);   // �إå�����
    }

    // -----------------------------------------------------------------
    // �᥽�å�
    /**
     * X���ե��å�����
     *
     * @param   xofs    X���ե��å�
     */
    public void setXOffset(int xofs) {
        xOfs_ = xofs;
    }

    /**
     * Y���ե��å�����
     *
     * @param   yofs    Y���ե��å�
     */
    public void setYOffset(int yofs) {
        yOfs_ = yofs;
    }

    /**
     * ������������
     *
     * @param   scale   ��������
     */
    public void setScale(double scale) {
        _stroke();
        scale_ = scale;
    }

    /**
     * ��������
     *
     * @param   width   ����
     */
    public void setLineWidth(double width) {
        _stroke();
        pw.println("" + width + " setlinewidth");
    }

    /**
     * ���Ͻ�λ
     *
     */
    public void finishOutput() {
        _stroke();           // �ѥ�������
        pw.println(EOF);    // EOF�ޡ�������
        pw.close();         // �ץ��ȥ饤��������
    }

    // -----------------------------------------------------------------
    // Graphics�Υ᥽�åɥ����С��饤��
    /**
     * ������
     *
     * @param   color   ��
     */
    public void setColor(Color c) {
        _stroke();
        int ind = colorList_.indexOf(c);
        String col;
        if (ind >= 0) {
            col = (String)colorOps_.get(ind);
        } else {
            col = SET_COLOR_OTHERS;
        }
        pw.println(col);
    }

    /**
     * ����ɤ�Ĥ֤�
     *
     * @param   x       ����ɸ
     * @param   y       ���ɸ
     * @param   width   ��
     * @param   height  �⤵
     */
    public void fillRect(int x, int y, int width, int height) {
        // ̵����
    }

    /**
     * ������
     *
     * @param   x1  ����X��ɸ
     * @param   y1  ����Y��ɸ
     * @param   x2  ����X��ɸ
     * @param   y2  ����Y��ɸ
     */
    public void drawLine(int x1, int y1, int x2, int y2) {
        StringBuffer sb;
        if (inPath_) {   // �ѥ���³��?
            if (prevX_ == x1 && prevY_ == y1) { // ����������ν����Ȱ���?
                // x2 y2 lineto
                sb = new StringBuffer();
                sb.append(x2 / scale_ + xOfs_);
                sb.append(' '); sb.append(PAGE_HEIGHT - y2 / scale_ - yOfs_);
                sb.append(' '); sb.append(LINETO);
                pw.println(sb.toString());
            } else {    // ����������ν����Ȱۤʤ�?
                // stroke
                // newpath
                // x1 y1 moveto
                // x2 y2 lineto
                sb = new StringBuffer(STROKE);
                sb.append(' '); sb.append(NEWPATH);
                sb.append(' '); sb.append(x1 / scale_ + xOfs_);
                sb.append(' '); sb.append(PAGE_HEIGHT - y1 / scale_ - yOfs_);
                sb.append(' '); sb.append(MOVETO);
                sb.append('\n'); sb.append(x2 / scale_ + xOfs_);
                sb.append(' '); sb.append(PAGE_HEIGHT - y2 / scale_ - yOfs_);
                sb.append(' '); sb.append(LINETO);
                pw.println(sb.toString());
            }
        } else {    // �ѥ���³��Ǥʤ�?
            // newpath
            // x1 y1 moveto
            // x2 y2 lineto
            sb = new StringBuffer(NEWPATH);
            sb.append(' '); sb.append(x1 / scale_ + xOfs_);
            sb.append(' '); sb.append(PAGE_HEIGHT - y1 / scale_ - yOfs_);
            sb.append(' '); sb.append(MOVETO);
            sb.append('\n'); sb.append(x2 / scale_ + xOfs_);
            sb.append(' '); sb.append(PAGE_HEIGHT - y2 / scale_ - yOfs_);
            sb.append(' '); sb.append(LINETO);
            pw.println(sb.toString());
            inPath_ = true;
        }
        prevX_ = x2; prevY_ = y2;   // �����򹹿�

        /* �����μ�����̵�̤�¿���ΤǼ����
        StringBuffer sb = new StringBuffer();
        sb.append(x1 / scale_ + xOfs_);
        sb.append(' '); sb.append(PAGE_HEIGHT - y1 / scale_ - yOfs_);
        sb.append(' '); sb.append(x2 / scale_ + xOfs_);
        sb.append(' '); sb.append(PAGE_HEIGHT - y2 / scale_ - yOfs_);
        sb.append(' '); sb.append(DRAW_LINE);
        pw.println(sb.toString());
        */
    }

    /**
     * �ե��������
     *
     * @param   font    �ե����
     */
    public void setFont(Font font) {
        _stroke();  // �����������������
        StringBuffer sb = new StringBuffer("/");

        // �ե���ȷ���
        //sb.append(font.getPSName()); <--- ������Ϥ�����ɤ��Ϥ�����...
        String fname = font.getName();
        String psf;
        if (fname.equals("dialog")) {
            psf = "Helvetica";
        } else if (fname.equals("monospaced")) {
            psf = "Courier";
        } else {
            psf = "Times-Roman";
        }
        sb.append(psf);

        sb.append(' '); sb.append(font.getSize());
        sb.append(' '); sb.append(SET_FONT);
        pw.println(sb.toString());
    }

    /**
     * ʸ��������
     *
     * @param   str ʸ����
     * @param   x   X��ɸ
     * @param   y   Y��ɸ
     */
    public void drawString(String str, int x, int y) {
        _stroke();  // �����������������
        StringBuffer sb = new StringBuffer("(");
        int len = str.length();
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            if (c == '(' || c == ')') {
                sb.append('\\');
            }
            sb.append(c);
        }
        sb.append(") "); sb.append(x / scale_ + xOfs_);
        sb.append(' '); sb.append(PAGE_HEIGHT - y / scale_ - yOfs_);
        sb.append(' '); sb.append(DRAW_STRING);
        pw.println(sb.toString());
    }

    /**
     * ����å�����
     *
     * @param   clip    ����å׷���
     */
    public void setClip(Shape clip) {
        _stroke();  // �����������������
        if (clip == null) { // ����åײ��?
            pw.println(SET_CLIP_NULL);  // ����åײ��
        }
    }

    /**
     * �������å�����
     *
     * @param   x       ����ɸ
     * @param   y       ���ɸ
     * @param   width   ��
     * @param   height  �⤵
     */
    public void setClip(int x, int y, int width, int height) {
        _stroke();  // �����������������
        StringBuffer sb = new StringBuffer();
        sb.append(x / scale_ + xOfs_);
        sb.append(' '); sb.append(PAGE_HEIGHT - (y + height) / scale_ - yOfs_);
        sb.append(' '); sb.append(width / scale_);
        sb.append(' '); sb.append(height / scale_);
        sb.append(' '); sb.append(SET_CLIP);
        pw.println(sb.toString());  // ����å�
    }

    /**
     * XOR�⡼�ɤ�����
     *
     * @param   color   ��
     */
    public void setXORMode(Color c) {
        // ̵����
    }

    /**
     * �ڥ���ȥ⡼�ɤ�����
     *
     */
    public void setPaintMode() {
        // ̵����
    }

    // -----------------------------------------------------------------
    // Graphics�Υ᥽�åɥ����С��饤��(̤������)
    public Graphics create() {
        return null;
    }
    public void translate(int x, int y) { }
    public Color getColor() {
        return null;
    }
    public Font getFont() {
        return null;
    }
    public FontMetrics getFontMetrics(Font f) {
        return null;
    }
    public Rectangle getClipBounds() {
        return null;
    }
    public void clipRect(int x, int y, int width, int height) { }
    public Shape getClip() {
        return null;
    }
    public void copyArea(
        int x, int y, int width, int height, int dx, int dy
    ) { }
    public void clearRect(int x, int y, int width, int height) { }
    public void drawRoundRect(
        int x, int y, int width, int height, int arcWidth, int arcHeight
    ) { }
    public void fillRoundRect(
        int x, int y, int width, int height, int arcWidth, int arcHeight
    ) { }
    public void drawOval(int x, int y, int width, int height) { }
    public void fillOval(int x, int y, int width, int height) { }
    public void drawArc(
        int x, int y, int width, int height, int startAngle, int arcAngle
    ) { }
    public void fillArc(
        int x, int y, int width, int height, int startAngle, int arcAngle
    ) { }
    public void drawPolyline(int xPoints[], int yPoints[], int nPoints) { }
    public void drawPolygon(int xPoints[], int yPoints[], int nPoints) { }
    public void fillPolygon(int xPoints[], int yPoints[], int nPoints) { }
    public void drawString(
        AttributedCharacterIterator iterator, int x, int y
    ) { }
    public boolean drawImage(
        Image img, int x, int y, ImageObserver observer
    ) {
        return false;
    }
    public boolean drawImage(
        Image img, int x, int y, int width, int height, ImageObserver observer
    ) {
        return false;
    }
    public boolean drawImage(
        Image img, int x, int y, Color bgcolor, ImageObserver observer
    ) {
        return false;
    }
    public boolean drawImage(
        Image img, int x, int y, int width, int height,
        Color bgcolor, ImageObserver observer
    ) {
        return false;
    }
    public boolean drawImage(
        Image img, int dx1, int dy1, int dx2, int dy2,
        int sx1, int sy1, int sx2, int sy2, ImageObserver observer
    ) {
        return false;
    }
    public boolean drawImage(
        Image img, int dx1, int dy1, int dx2, int dy2,
        int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer
    ) {
        return false;
    }
    public void dispose() { }


    // -----------------------------------------------------------------
    // �ץ饤�١��ȥ᥽�å�
    /**
     * �إå�����
     *
     * @param   top     �Х���ǥ��󥰥ܥå������ɸ
     * @param   left    �Х���ǥ��󥰥ܥå�������ɸ
     * @param   width   �Х���ǥ��󥰥ܥå�����
     * @param   heigt   �Х���ǥ��󥰥ܥå����⤵
     * @param   color   ���顼�ե饰
     */
    private void _writeHeader(
        int top,
        int left,
        int width,
        int height,
        boolean color
    ) {
        // BoundingBox�׻�
        int bl = left;
        int bb = PAGE_HEIGHT - (top + height);
        int br = left + width;
        int bt = PAGE_HEIGHT - top;

        // Header
        pw.println(HEADER);
        StringBuffer sb = new StringBuffer(BOUNDING_BOX);
        sb.append(' '); sb.append(bl);
        sb.append(' '); sb.append(bb);
        sb.append(' '); sb.append(br);
        sb.append(' '); sb.append(bt);
        pw.println(sb.toString());
        pw.println();

        // �����ڥ졼�����
        pw.println("% Color definition");
        if (color) {
            for (int i = 0; i < colorList_.size(); i++) {
                Color col = (Color)colorList_.get(i);
                StringBuffer sbuf = new StringBuffer(DEF);
                sbuf.append((String)colorOps_.get(i));
                sbuf.append(" {0.3 setlinewidth 0 setgray [] 0 setdash ");
                sbuf.append((255 - col.getRed()) / 255.0f);
                sbuf.append(' ');
                sbuf.append((255 - col.getGreen()) / 255.0f);
                sbuf.append(' ');
                sbuf.append((255 - col.getBlue()) / 255.0f);
                sbuf.append(" setrgbcolor}");
                sbuf.append(BIND_DEF);
                pw.println(sbuf.toString());
            }
            pw.println(DEF_COLOR_OTHERS);
        } else {
            pw.println(DEF_COLOR_WHITE);
            pw.println(DEF_COLOR_LIGHTGRAY);
            pw.println(DEF_COLOR_GRAY);
            pw.println(DEF_COLOR_DARKGRAY);
            pw.println(DEF_COLOR_BLACK);
            pw.println(DEF_COLOR_OTHERS);
            pw.println(DEF_COLOR_GREEN);
            pw.println(DEF_COLOR_YELLOW);
            pw.println(DEF_COLOR_PINK);
            pw.println(DEF_COLOR_CYAN);
            pw.println(DEF_COLOR_MAGENTA);
            pw.println(DEF_COLOR_RED);
            pw.println(DEF_COLOR_ORANGE);
            pw.println(DEF_COLOR_BLUE);
        }
        pw.println();

        // ���襪�ڥ졼�����
        pw.println("% Method definition");
        pw.println(DEF_DRAW_LINE);
        pw.println(DEF_SET_FONT);
        pw.println(DEF_DRAW_STRING);
        pw.println(DEF_SET_CLIP);
        pw.println(DEF_SET_CLIP_NULL);
        pw.println(DEF_NEWPATH);
        pw.println(DEF_MOVETO);
        pw.println(DEF_LINETO);
        pw.println(DEF_STROKE);
        pw.println();

        // �إå���λ
        pw.println("% end of header");
        pw.println();

        // ���ǥХ���(�Х���ǥ��󥰥ܥå�������)
        pw.println("newpath");
        pw.println("" + bl + " " + bb + " moveto");
        pw.println("" + br + " " + bb + " lineto");
        pw.println("" + br + " " + bt + " lineto");
        pw.println("" + bl + " " + bt + " lineto");
        pw.println("closepath");
        pw.println("stroke");
    }

    /**
     * ���ȥ���
     *
     */
    private void _stroke() {
        if (inPath_) {  // �ѥ���³��?
            pw.println(STROKE); // ���ȥ���
            inPath_ = false;    // �ѥ���λ
        }
    }
}
