/**
 * ReplayManager.java
 *
 * @author  Kernel Co.,Ltd.
 * @version 1.0 (2001/3/1)
 */

package com.generalrobotix.ui.view.tdview;

import java.io.*;
import java.util.*;
import java.awt.image.BufferedImage;
import javax.media.Format;


public class RecordingManager{
    //--------------------------------------------------------------------
    // ���
    //��Ŭ���ѥ�᡼��
    private static int SLEEP_TIME=50;  //���꡼�פ������(ms)

    //���꡼�פ�����ޤǤκ�����ƥ��᡼�������å���
    private static int MAX_STACK_SIZE=0;
    
    //--------------------------------------------------------------------
    // ���饹�ѿ�
    private static RecordingManager this_;

    //--------------------------------------------------------------------
    // �����ѿ�
    private int width_,height_;
    private float frameRate_;
    private Hashtable<String, Format> htFormat_;   //String--Format
    
    private ImageToMovie movie_;
    
    //--------------------------------------------------------------------
    // ���󥹥ȥ饯��
    private RecordingManager() {
    }
    
    //--------------------------------------------------------------------
    // ���饹�᥽�å�
    public static RecordingManager getInstance() {
        if (this_ == null) {
            this_ = new RecordingManager();
        }

        return this_;
    }

    //--------------------------------------------------------------------
    // �����᥽�å�
    
    public void setImageSize(int w, int h) {
        width_ = w;
        height_ = h;
    }

    public int getWidth(){
        return width_;
    }

    public int getHeight(){
        return height_;
    }

    public void setFrameRate(float rate){
        frameRate_=rate;
    }

    public float getFrameRate(){
        return frameRate_;
    }

    public String[] getSuportedFormat(){
        htFormat_=new Hashtable<String, Format>();
        //�ƥ�ݥ����äƥե����ޥåȤ�����
        String fileName =
            "file:" +
            System.getProperty("user.dir") + 
            System.getProperty("file.separator") +
            _getUniqueName();
        ImageToMovie tempMovie =
            new ImageToMovie(
                width_,
                height_,
                frameRate_,
                fileName,
                ImageToMovie.QUICKTIME
            );
        Format[] formats=tempMovie.getSupportedFormats();
        String[] ret=new String[formats.length];
        for(int i=0;i<formats.length;i++){
            ret[i]=formats[i].toString();
            htFormat_.put(ret[i],formats[i]);
        }
        tempMovie.setFormat(formats[0]);
        //�ƥ�ݥ���ä�
        File file=new File(fileName);
        file.delete();
        
        return ret;
    }

    private String _getUniqueName() {
        Calendar cal=new GregorianCalendar();
        String str="`~$" + cal.getTime().hashCode() +".TMP";
        return str;
    }
    public String[] preRecord(String fileName, String fileType)
	{
        
        movie_=
            new ImageToMovie(
                width_,
                height_,
                frameRate_,
                fileName,
                fileType
            );
        htFormat_=new Hashtable<String, Format>();
        
        Format[] formats = movie_.getSupportedFormats();
        String[] ret=new String[formats.length];
        for(int i=0;i<formats.length;i++){
            ret[i]=formats[i].toString();
            htFormat_.put(ret[i],formats[i]);
        }
        return ret;
    }

    public void startRecord(String formatStr){
        Format format=(Format)htFormat_.get(formatStr);
        movie_.setFormat(format);

        movie_.startProcess();
    }

    public void endRecord() {
        movie_.endProcess();
    }

    public void pushImage(BufferedImage image)
	{
            movie_.pushImage(image);
            do {
                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } while (movie_.getImageStackSize() > MAX_STACK_SIZE);
    }
}
