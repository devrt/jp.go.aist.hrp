/**
 *
 *  LogManager.java
 *
 * @author  Kernel Co.,Ltd.
 * @version 2.0 (Fri Nov 23 2001)
 */

package com.generalrobotix.ui.view.graph;

import java.util.*;
import java.io.*;
import java.util.zip.*;

import jp.go.aist.hrp.simulator.CollisionPoint;

/**
 * ���������饹
 *
 * @history 1.0 (2001/3/1)
 * @history 1.1 (2001/10/??)
 *    ����դΤ����getData()�᥽�åɤ��ɲá�
 * @history 2.0 (Fri Nov 23 2001)
 *    Ʊ���˽񤭹��ߤ��ɤ߹��ߤ��ǽ�ˤ�����
 */
public class LogManager {
    //--------------------------------------------------------------------
    // ���
    private static final String POSTFIX = ".tmp";
    private static String COLLISION_LOG = "CollisionData.col";
    private static final int COLLISION_DATA_SIZE = 6 * 4 + 1 * 8;
    private static final String NONAME_OBJECT = "_noname";

    //--------------------------------------------------------------------
    // ���饹�ѿ�
    private static LogManager this_;   // LogManager��ͣ��Υ��󥹥���

    //--------------------------------------------------------------------
    // ���󥹥����ѿ�
    private Hashtable<String, LogHeader> header_;
    private Hashtable<String, DataOutputStream> writeFile_;
    private Hashtable<String, RandomAccessFile> readFile_;
    private Map<String, Map<String, Integer> > indexMapMap_;
    private CollisionLogHeader collisionLog_;
    private Time time_;
    private DataOutputStream collisionOut_;
    private RandomAccessFile collisionIn_;

    private String fileName_;

	private String tmpdir;
    //--------------------------------------------------------------------
    // ���󥹥ȥ饯��

    /**
     * Singleton�ѥ�����
     */
//    private LogManager() {}

    //--------------------------------------------------------------------
    // �����᥽�å�
    public static void main(String[] args) {
        LogManager log = new LogManager();
        log.init();
        try {
            log.addLogObject(
                "test",
                new String[] {
                    "test1",
                    "float", 
                    "test2", 
                    "float[3]"
                }
            );
        } catch (LogFileFormatException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * ���󥹥��󥹤μ�����Singleton�ѥ�����)
     */
    public static LogManager getInstance() {
        if (this_ == null) {
            this_ = new LogManager();
	    String tmpdir = System.getProperty("TEMP");
	    if (tmpdir != null){ 
		COLLISION_LOG = tmpdir+File.separator+COLLISION_LOG;
	    }
        }

        return this_;
    }

    /**
     * �����
     */
    public void init() {
        header_ = new Hashtable<String, LogHeader>();
        indexMapMap_ = new HashMap<String, Map<String, Integer> >();
        time_ = new Time();
        fileName_ = null;
    }
    
    //�񤭹��߽�λ���
    //�ե졼��������롣
    //start����<�ޤ�> end����<�ޤ�>
    //@return ���ä�����
    public Time deleteFrame(Time startTime, Time endTime)
        throws IOException
    {
        System.out.println("deleteFrame() " + startTime + "-" + endTime);
        int stFrame = _timeToFrame(startTime);
        int edFrame = _timeToFrame(endTime);
        if(stFrame < 0){
            stFrame = 0;
        }
        if(!existRecord(edFrame)){
            throw new RuntimeException("UNA UNA!!");
        }
        //�����޶ػ�
        if(!existRecord(edFrame + 1) && stFrame == 0){
            throw new RuntimeException("Total 0 frame Nattimau!!");
        }
        
        //<�ޤ�>�ʤΤ�+1
        int len = edFrame - stFrame + 1;
        deleteFrame(stFrame,len);
        
        Time time = new Time(len * getStepTime().getUtime());
        return time;
    }
    
    private Time getStepTime(){
        Enumeration elements = header_.elements();
        if (elements.hasMoreElements()) {
            LogHeader header = (LogHeader)elements.nextElement();
            return new Time(header.timeStep_);
        }else{
            throw new RuntimeException("konoyarou");
        }
    }
    private int _timeToFrame(Time time){
        Enumeration elements = header_.elements();
        if (elements.hasMoreElements()) {
            LogHeader header = (LogHeader)elements.nextElement();
         
            long recordNum =
                time.getUtime() / header.timeStep_
                + (((time.getUtime() % header.timeStep_) == 0) ? 0 : 1);
            return (int)recordNum;
        }else{
            throw new RuntimeException("konoyarou");
        }
    }
    //�񤭹��߽�λ���
    //�ե졼��������롣
    //start,length
    public void deleteFrame(int startFrame, int length)
        throws IOException
    {
        System.out.println("deleteFrame() " + startFrame + "-" + length);
        synchronized(this){
            for(Enumeration elements = header_.elements();elements.hasMoreElements();){
                //�إå��������
                LogHeader header = (LogHeader)elements.nextElement();
                _deleteFrameRobot(header,startFrame,length);
            }
            
            //���ꥸ���
            //�إå��������
            collisionLog_ = _deleteFrameCollision(collisionLog_,startFrame,length);
        }
        System.out.println("sore,OK");
        
    }
    //@return newHeader
    private CollisionLogHeader _deleteFrameCollision(CollisionLogHeader oldHeader,int startFrame, int length)
        throws IOException
    {
        System.out.println("_deleteFrameCollision()");
        
        //�������إå�
        CollisionLogHeader newHeader = new CollisionLogHeader();
        //�Ť��ǡ�����Ѿ�
        DataInputStream in =
            new DataInputStream(
                new FileInputStream(COLLISION_LOG)
            );
        try{
            newHeader.input(in);
        }catch(LogFileFormatException ex){
            System.out.println("@@damedame");
            throw new IOException();
        }
        
        in.close();
        //�������إå������񤭴���
        newHeader.numRecords_ -= length;
        newHeader.totalTime_ -= length * newHeader.timeStep_;
        newHeader.endTime_ -= length * newHeader.timeStep_;
        newHeader.position_ = new int[newHeader.numRecords_ + 1];//int*length
        
        //���������
        //newHeader.headerSize_ -= CollisionLogHeader.INT_DATA_SIZE * length;//int*length
        //�ʤ��ʤ顢�ǽ�ϥ��ߥ�졼�����λͽ��������˥إå����������������Ƥ��뤫�顣
        //������Ф�������ϥ��ߥ�졼���������֤ϴ��ΤʤΤǡ������ǲ���ƥإå�����������׻����ʤ���Фʤ�ʤ��Τ���
        newHeader.headerSize_ = CollisionLogHeader.FIXED_PART_SIZE + (newHeader.numRecords_ + 1) * CollisionLogHeader.INT_DATA_SIZE;
        
        System.out.println("new.numRecords_ = " + newHeader.numRecords_ );
        System.out.println("new.totalTime_ = " + newHeader.totalTime_ );
        System.out.println("new.endTime_ = " + newHeader.endTime_ );
        System.out.println("new.position_ = " + newHeader.position_ );
        System.out.println("new.headerSize_ = " + newHeader.headerSize_ );
        
        //��ˡ����ʥե���������
        File tempFile = File.createTempFile("col","r",new  File("."));
        System.out.println("create temp file : " + tempFile);
        DataOutputStream out = new DataOutputStream(
            new BufferedOutputStream(
                new FileOutputStream(tempFile)
            )
        );
        
        //�������إå��񤭹���
        newHeader.output(out);
        newHeader.currentPos_ = newHeader.headerSize_;
        newHeader.position_[0] = newHeader.currentPos_;
        
        //�ե���������
        //�������ե�����
        RandomAccessFile source = collisionIn_;
        source.seek(oldHeader.headerSize_);
        byte[] buffer = new byte[1024];
        //�ǡ������ԡ�
        for(int i=0 ; i<startFrame; i++){
            int size =  oldHeader.position_[i+1] - oldHeader.position_[i];
            for(int j = 0; j < size / buffer.length;j++){
                source.read(buffer);
                out.write(buffer);
            }
            source.read(buffer,0,size % buffer.length);
            out.write(buffer,0,size % buffer.length);
            newHeader.position_[i+1] = newHeader.position_[i] + size;
            //System.out.println("wrote; " + i + " size = " + size + "pos[+1] = " + newHeader.position_[i+1]);
            
        }
        
        source.seek(oldHeader.position_[startFrame + length]);
        for(int i = startFrame ; i < newHeader.numRecords_; i++){
            int size =  oldHeader.position_[i+length +1] - oldHeader.position_[ i+ length];
            //System.out.println("wrote2; " + i + " size = " + size);
            for(int j = 0; j < size / buffer.length;j++){
                source.read(buffer);
                out.write(buffer);
            }
            source.read(buffer,0,size % buffer.length);
            out.write(buffer,0,size % buffer.length);
            newHeader.position_[i+1] = newHeader.position_[i] + size;
            //System.out.println("wrote; " + i + " size = " + size + "pos[+1] = " + newHeader.position_[i+1]);
        }
        
        //�ե����륯����
        out.flush();
        out.close();
        source.close();//�Ť��ե�����򥯥���
        
        
        //rename
        File logFile = new File(COLLISION_LOG);
        //ǰ�Τ���ä�
        System.out.println("delete : " + logFile);
        if(!logFile.delete()){
            System.out.println("Failed to delete!");
            throw new IOException();
        }
        System.out.println("renameTo : " + logFile);
        if(!tempFile.renameTo(logFile)){
            System.out.println("Failed to rename!");
            throw new IOException();
        }
        
        //�إå��񤭹���
        RandomAccessFile tempfile = new RandomAccessFile(COLLISION_LOG, "rw");
        newHeader.outPositions(tempfile);
        tempfile.close();
        
        //�ƥ����ץ�
        RandomAccessFile file =
            new RandomAccessFile(COLLISION_LOG, "r");
        
        //���
        collisionIn_ = file;
        
        System.out.println("owari2");
        return newHeader;
    }
    
    //�إå��Ͻ񤭴����
    private void _deleteFrameRobot(LogHeader header,int startFrame, int length)
        throws IOException
    {
        System.out.println("_deleteFrameRobot()" + header.objectName_);
        
        //�إå�����񤭴���
        header.numRecords_ -= length;
        header.totalTime_ -= length * header.timeStep_;
        header.endTime_ -= length * header.timeStep_;
        
        //��ˡ����ʥե���������
        File tempFile = File.createTempFile(header.objectName_,"r",
					    new File("."));
        System.out.println("create temp file : " + tempFile);
        DataOutputStream out = new DataOutputStream(
            new BufferedOutputStream(
                new FileOutputStream(tempFile)
            )
        );
        
        //�ե���������
        //�������ե�����
        RandomAccessFile source = readFile_.get(header.objectName_);
        
        //�إå��񤭹���
        header.output(out);
        
        //�ǡ������ԡ�
        //���쥳���ɤΥ�������header.recordSize_
        //�ǽ�
        source.seek(header.headerSize_);
        byte[] buffer = new byte[header.recordSize_];
        for(int i=0 ; i<startFrame; i++){
            source.read(buffer);
            out.write(buffer);
        }
        //�Ǹ�
        source.seek(header.headerSize_ + header.recordSize_ * (startFrame + length));
        for(int i=startFrame ; i<header.numRecords_; i++){
            source.read(buffer);
            out.write(buffer);
        }
        
        //�ե����륯����
        out.flush();
        out.close();
        source.close();

        //rename
        File logFile = new File(getTempFilePath(header.objectName_));
        //ǰ�Τ���ä�
        System.out.println("delete : " + logFile);
        if(!logFile.delete()){
            System.out.println("Failed to delete!");
            throw new IOException();
        }
	System.out.println("renameTo : " + logFile);
	if(!tempFile.renameTo(logFile)){
	    System.out.println("Failed to rename!");
	    throw new IOException();
	}

        //�ƥ����ץ�
        RandomAccessFile file =
            new RandomAccessFile(getTempFilePath(header.objectName_), "r");

        //���
        readFile_.put(header.objectName_, file);
        
        System.out.println("owari");
    }
    
    public String getTempFilePath(String objectName){
	//String tmpdir = System.getProperty("TEMP");
	if (tmpdir != null){
	    return tmpdir+File.separator+objectName+POSTFIX;
	}else{
	    return objectName+POSTFIX;
	}
    }
    /**
     * �إå����󤫤�SimulationTime��Ϳ����
     */
    public void getSimulationTime(SimulationTime time) {
        // �إå��Υ��ߥ�졼�������֤˴ؤ�����ܤϤ��٤ƤΥ��֥������Ȥ�
        // ���̤ΤϤ������顢���Ĥ����إå��������Ф���SimulationTime
        // ���Ѵ�
        Enumeration elements = header_.elements();
        if (!elements.hasMoreElements()) return;
        LogHeader header = (LogHeader)elements.nextElement();
        //time.totalTime_.setUtime(header.totalTime_);
        time.totalTime_.setUtime(header.endTime_ - header.startTime_);
        time.startTime_.setUtime(header.startTime_);
        time.timeStep_.setUtime(header.timeStep_);
    }

    /**
     * �����륪�֥������Ȥ��ɲä���
     *
     */
    public void addLogObject(String objectName, String[] format)
        throws LogFileFormatException
    {
        LogHeader header = new LogHeader(objectName, format);
        header_.put(objectName, header);
        _makeIndexMapMap(header);
    }

    /**
     * ���ĥ����å�����Υ��Τ���ν����
     */
    public void initCollisionLog(SimulationTime time) {
        collisionLog_ = new CollisionLogHeader(time);
    }

    /**
     * ���񤭹��ߤΤ���˥ե�����򥪡��ץ�
     *
     *  ���ե������ʣ������Τǡ����ȥ꡼���ϥå���ơ��֥�(file_)��
     *  ��¸
     */
    public void openAsWrite(SimulationTime time, int method)
        throws IOException
    {
        writeFile_ = new Hashtable<String, DataOutputStream>();
        for (
            Enumeration elements = header_.elements();
            elements.hasMoreElements();
        ) {
            try {
                LogHeader header = (LogHeader)elements.nextElement();
                header.totalTime_ = time.totalTime_.getUtime();
                header.startTime_ = time.startTime_.getUtime();
                header.timeStep_  = time.timeStep_.getUtime();
                header.endTime_ = 0;
                header.method_ = method;
             
                // �إå��ν����
                DataOutputStream out =
                    new DataOutputStream(
                        new BufferedOutputStream(
                            new FileOutputStream(getTempFilePath(header.objectName_))
                        )
                    );

                header.output(out);
                writeFile_.put(header.objectName_, out);
            } catch (IOException ex) {
                for (
                    Enumeration elms = writeFile_.elements();
                    elms.hasMoreElements();
                ) {
                    DataOutputStream out =
                        (DataOutputStream)elms.nextElement();
                    out.close();
                }
                throw ex;
            }
        }
    }

    /**
     * �񤭹��ߤȤ��ƥ����ץ󤷤��ե�����򥯥�������
     */
    public double closeAsWrite() throws IOException {
        for (
            Enumeration elements = header_.elements();
            elements.hasMoreElements();
        ) {
            LogHeader header = (LogHeader)elements.nextElement();
            DataOutputStream out =
                (DataOutputStream)writeFile_.get(header.objectName_);
            out.close();

            // �إå��˽�λ���֤�񤭹���
            header.endTime_ = time_.getUtime();
            RandomAccessFile file =
                new RandomAccessFile(getTempFilePath(header.objectName_), "rw");
            header.outEndTime(file);    // ��λ���֤ޤǥ�����
            file.close();
        }
        writeFile_ = null;
        return time_.getDouble();
    }

    public void openAsRead() throws IOException {
        readFile_ = new Hashtable<String, RandomAccessFile>();
        for (
            Enumeration elements = header_.elements();
            elements.hasMoreElements();
        ) {
            LogHeader header = (LogHeader)elements.nextElement();
            RandomAccessFile file =
                new RandomAccessFile(getTempFilePath(header.objectName_), "r");
            readFile_.put(header.objectName_, file);
        }
    }

    public void closeAsRead() throws IOException {
        if (readFile_ == null) return;
        for (
            Enumeration elements = readFile_.elements();
            elements.hasMoreElements();
        ) {
            RandomAccessFile file =
                (RandomAccessFile)elements.nextElement();
            file.close();
       }
       readFile_ = null;
    }

    public void openCollisionLogAsWrite() throws IOException {
        collisionOut_ =
            new DataOutputStream(
                new BufferedOutputStream(
                    new FileOutputStream(COLLISION_LOG)
                )
            );

        collisionLog_.output(collisionOut_);
        collisionLog_.currentPos_ = collisionLog_.headerSize_;
        collisionLog_.position_[0] = collisionLog_.currentPos_;
        collisionLog_.numRecords_ = 0;
    }

    public void openCollisionLogAsRead()
        throws IOException, FileNotFoundException
    {
        collisionIn_ = new RandomAccessFile(COLLISION_LOG, "r");
    }

    public void closeCollisionLogAsRead() throws IOException {
        collisionIn_.close();
    }

    public void closeCollisionLogAsWrite() throws IOException {
        collisionOut_.close();

        // recordSize_��񤭹���
        collisionLog_.endTime_ = time_.getUtime();
        try {
            RandomAccessFile file = new RandomAccessFile(COLLISION_LOG, "rw");
            collisionLog_.outEndTime(file);
            collisionLog_.outPositions(file);
            file.close();
        } catch (FileNotFoundException ex) {
            throw new IOException();
        }
    }

    public String[] getDataFormat(String objectName) {
        LogHeader header = (LogHeader)header_.get(objectName);
        if (header == null) return null;

        int size = header.dataFormat_.length / 2;
        String[] format = new String[size];
        for (int i = 0; i < size; i ++) {
            format[i] = header.dataFormat_[i * 2];
        }
        return format;
    }

    public void setTime(Time time) {
        time_.set(time);
    }

    public void put(String objectName, float[] data)
        throws LogFileOutputException, IOException
    {
        LogHeader header = (LogHeader)header_.get(objectName);

        if (data.length == (header.recordSize_ / LogHeader.FLOAT_DATA_SIZE)) {
            try {
                DataOutputStream out = (DataOutputStream)writeFile_.get(objectName);
                for (int i = 0; i < data.length; i ++) {
                    out.writeFloat(data[i]);
                }
                out.flush();
            } catch (IOException ex) {
                closeAsWrite();
                throw ex;
            }
            header.numRecords_ ++;
        } else {
            throw new LogFileOutputException("data length error.");
        }
    }

    public float[] get(String objectName, Time time) throws IOException {
        if (readFile_ == null) return null;

        LogHeader header = (LogHeader)header_.get(objectName);
        if (header == null) return null;

        RandomAccessFile file = (RandomAccessFile)readFile_.get(objectName);

        float[] data = new float[header.recordSize_ / 4];

        try {
            synchronized (file) {
                // �쥳���ɤ��ֹ�
                // ������ / ���ƥåץ�����
                long record = time.getUtime() / header.timeStep_;
                //System.out.println("record = " + record);
                //System.out.println("numRecords = " + header.numRecords_);
             
                file.seek((long)header.headerSize_ + header.recordSize_ * record);
             
                for (int i = 0; i < data.length; i ++) {
                    data[i] = file.readFloat();
                    //System.out.println("data[" + i + "]=" + data[i]);
                }
            }
        } catch (EOFException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            closeAsRead();
            throw ex;
        }
        return data;
    }

    public void putCollisionPointData(CollisionPoint[] data) throws IOException {
        //int frameNum = (int)(time_.getUtime() / collisionLog_.timeStep_);
        //System.out.println("putCollisionPointData(): frameNum=" + frameNum+":"+time_.getUtime()+":"+collisionLog_.timeStep_);

    	collisionLog_.currentPos_ += data.length * COLLISION_DATA_SIZE;
        collisionLog_.position_[collisionLog_.numRecords_ + 1] =
            collisionLog_.currentPos_;

        for (int i = 0; i < data.length; i ++) {
            collisionOut_.writeFloat((float)data[i].normal[0]);
            collisionOut_.writeFloat((float)data[i].normal[1]);
            collisionOut_.writeFloat((float)data[i].normal[2]);
            collisionOut_.writeFloat((float)data[i].position[0]);
            collisionOut_.writeFloat((float)data[i].position[1]);
            collisionOut_.writeFloat((float)data[i].position[2]);
	    collisionOut_.writeDouble(data[i].idepth);
            collisionOut_.flush();
        }

        collisionLog_.numRecords_ ++;
    }

    public CollisionPoint[] getCollisionPointData(Time time) throws IOException {
        int frameNum = (int)(time.getUtime() / collisionLog_.timeStep_);
        //System.out.println("getCollisionPointData(): frameNum=" + frameNum +" time = " + time.getUtime());

        int size =
            collisionLog_.position_[frameNum + 1]
            - collisionLog_.position_[frameNum];

	int data_size=0;
	Enumeration elements = header_.elements();
	LogHeader header = (LogHeader)elements.nextElement();
	int version = header.getVersion();
	if (version <= 110){
	  data_size = 6 * 4;
	}else{
	  data_size = COLLISION_DATA_SIZE;
	}
        if ((size % data_size) != 0 || size <= 0) return null;
        size /= data_size;

        collisionIn_.seek(collisionLog_.position_[frameNum]);
        CollisionPoint[] data = new CollisionPoint[size];
        for (int i = 0; i < size; i ++) {
            data[i] = new CollisionPoint();
            data[i].normal = new double[3];
            data[i].normal[0] = collisionIn_.readFloat();
            data[i].normal[1] = collisionIn_.readFloat();
            data[i].normal[2] = collisionIn_.readFloat();
            data[i].position = new double[3];
            data[i].position[0] = collisionIn_.readFloat();
            data[i].position[1] = collisionIn_.readFloat();
            data[i].position[2] = collisionIn_.readFloat();
	    if (version <= 110){
	      data[i].idepth = 0.01;
	    }else{
	      data[i].idepth = collisionIn_.readDouble();
	    }
        }
        return data;
    }

    /**
     * �ӣ��֣Ž���
     *     @param    String  fileName  �ӣ��֣ťե�����̾
     */
    public void save(String fileName, String prjFileName) throws IOException {
        try {
            ZipOutputStream zip  =
                new ZipOutputStream(
                    new FileOutputStream(new File(fileName))
                );

            // �ƥ��ե�������ɲ�
            for (
                 Enumeration elements = header_.elements();
                 elements.hasMoreElements();
            ) {
                LogHeader header = (LogHeader)elements.nextElement();
                String logFileName = getTempFilePath(header.objectName_);
                File logFile = new File(logFileName);
                FileInputStream log = new FileInputStream(logFile);

                byte[] buffer = new byte[1024 * 1024];

                ZipEntry zipEntry = new ZipEntry(logFileName);
                zip.putNextEntry(zipEntry);
                long leftSize = logFile.length();
                while (leftSize > 0)  {
                    int readSize = log.read(buffer);
                    zip.write(buffer, 0, readSize);
                    leftSize -= readSize;
                }

                zip.closeEntry();
                log.close();
                //logFile.delete();
            }

            // �ץ������ȥե�������ɲ�
            File prjFile = new File(prjFileName);
            if (prjFile.exists()) {
                FileInputStream prj = new FileInputStream(prjFile);
             
                byte[] buffer = new byte[1024 * 1024];
             
                ZipEntry zipEntry = new ZipEntry(prjFileName);
                zip.putNextEntry(zipEntry);
             
                long leftSize = prjFile.length();
                while (leftSize > 0)  {
                    int readSize = prj.read(buffer);
                    zip.write(buffer, 0, readSize);
                    leftSize -= readSize;
                }
                prj.close();
                //prjFile.delete();
            }

            // ���ľ�������ɲ�
            File collisionFile = new File(COLLISION_LOG);
            if (collisionFile.exists()) {
                FileInputStream collision = new FileInputStream(collisionFile);
             
                byte[] buffer = new byte[1024 * 1024];

                ZipEntry zipEntry = new ZipEntry(COLLISION_LOG);
                zip.putNextEntry(zipEntry);
             
                long leftSize = collisionFile.length();
                while (leftSize > 0)  {
                    int readSize = collision.read(buffer);
                    zip.write(buffer, 0, readSize);
                    leftSize -= readSize;
                }
                collision.close();
                //collisionFile.delete();
            }


            zip.flush();
            zip.closeEntry();
            zip.close();
            fileName_ = fileName;
        } catch (IOException ex) {
            throw ex;
        }
    }

    public void save(String prjFile) throws IOException {
        if (fileName_ == null) return;
        save(fileName_, prjFile);
    }

    public void load(String fileName, String prjFile)
        throws FileOpenFailException, LogFileFormatException
    {
        init();

        // zip�ե�����Υ���ȥ����������ץ������ȥե����뤬���뤫�ɤ���
        // �����å����롣
        try {
   			ZipFile zipFile = new ZipFile(fileName);
            int entrySize = zipFile.size();
// TODO  temporary comment for GRXUI
/* 
            if (zipFile.getEntry(prjFile) == null) {
                throw new LogFileFormatException();
            }
            zipFile.close();
*/
            // zip��Ÿ������
            ZipInputStream zip =
                new ZipInputStream(
                    new FileInputStream(fileName)
                );
            byte[] buffer = new byte[1024];
            for (int i = 0; i < entrySize; i ++) {
                String entry = zip.getNextEntry().getName();
                FileOutputStream out = new FileOutputStream(entry);
                while (zip.available() == 1) {
                    int readSize = zip.read(buffer, 0, 1024);
                    if (readSize < 0) break;
                    out.write(buffer, 0, readSize);
                }
                out.close();
         
                if (entry.equals(prjFile)) { continue; }

                DataInputStream in;

                if (entry.equals(COLLISION_LOG)) {
                    try {
                        in = new DataInputStream(new FileInputStream(entry));
                        collisionLog_ = new CollisionLogHeader();
                        collisionLog_.input(in);
                        in.close();
                    } catch (LogFileFormatException ex) {
                        zip.close();
                        throw ex;
                    }
                } else {
                    try {
                        in = new DataInputStream(new FileInputStream(entry));
                        LogHeader header = new LogHeader();
                        header.input(in);
                        header_.put(header.objectName_, header);
                        in.close();
                        if (header.getVersion() <= 100) {
                            File file = new File(entry);
                            header.setFileSize(file.length());
                        }
                        header.calcUnitSize();
                        _makeIndexMapMap(header);
                    } catch (LogFileFormatException ex) {
                        zip.close();
                        throw ex;
                    }
                }
            }
            zip.close();
        } catch (IOException ex) {
			ex.printStackTrace();
            throw new FileOpenFailException();
        }
    }

    public void saveCSV(String fileName, String ObjectName)
        throws FileOpenFailException
    {
        try{
            LogHeader header = (LogHeader)header_.get(ObjectName);
            if (header == null) {
                throw new FileOpenFailException();
            }

            DataInputStream in =
                new DataInputStream(
                    new FileInputStream(getTempFilePath(header.objectName_))
                );
            PrintWriter out = new PrintWriter(new FileWriter(fileName));
            
            out.println(
                "Software Version, " +
                String.valueOf(header.version_[0]) + "." +
                String.valueOf(header.version_[1]) + "." +
                String.valueOf(header.version_[2]) + "." +
                String.valueOf(header.version_[3])
            );
            out.println("Header Size[byte], " + header.headerSize_);
            out.println("Simulation Total Time[s], " 
			+ (double)header.totalTime_/1000000.0);
            out.println("Simulation Start Time[s], " 
			+ (double)header.startTime_/1000000.0);
            out.println("Simulation End Time[s], " 
			+ (double)header.endTime_/1000000.0);
            out.println("TimeStep[s], " 
			+ (double)header.timeStep_/1000000.0);
            switch (header.method_) {
            case 0:
                out.println("Integration Method, EULER");
                break;
            case 1:
                out.println("Integration Method, RUNGE_KUTTA");
                break;
            default:
                out.println("Integration Method, " + header.method_);
                break;
            }
            out.println("Record Size[byte], " + header.recordSize_);

	    for (int i=0; i<header.dataFormat_.length/2; i++){
	      String fmt = header.dataFormat_[i*2 + 1];
	  	  int start = fmt.indexOf('[') + 1;
		  int end =  fmt.indexOf(']');
		  int len = 1;
		  if (start > 0)
	        len = Integer.parseInt(fmt.substring(start,end));
	      if (len == 1){
		    out.print(header.dataFormat_[i*2]);
		  if (i != header.dataFormat_.length/2-1) 
			out.print(",");
	      }else{
	        for (int j=0; j<len; j++){
		      out.print(header.dataFormat_[i*2]+"["+j+"]");
		      if (!(i == header.dataFormat_.length/2-1 && j == len-1))
		        out.print(",");
		    }
	      }
	    }
	    out.println();
            
            in.skip(header.headerSize_);
            int nLine =
                (int)(
                    (header.endTime_ - header.startTime_) /
                    (double)header.timeStep_
                );
            for (int i = 0; i < nLine; i ++) {
                for (
                    int j = 0;
                    j < header.recordSize_ / LogHeader.FLOAT_DATA_SIZE - 1;
                    j ++
                ){
                    out.print(in.readFloat() + ",");
                }
                //�Ǹ�ΰ��
                out.println(in.readFloat());
            }
            out.close();
            in.close();
            
        } catch (IOException ex) {
	  ex.printStackTrace();
            throw new FileOpenFailException();
        }
    }

    public boolean existRecord(int recordNum) {
        Enumeration elements = header_.elements();
        while (elements.hasMoreElements()) {
            LogHeader header = (LogHeader)elements.nextElement();
            if (header.numRecords_ <= recordNum) {
                //System.out.println("object=" + header.objectName_ + " numRecords=" + header.numRecords_);
                return false;
            }
        }

        if (collisionLog_.numRecords_ <= recordNum) {
            //System.out.println("recordNum=" + recordNum);
            //System.out.println("numRecords=" + collisionLog_.numRecords_);
            return false;
            //return true;
        } else {
            return true;
        }
    }

    public boolean existRecord(Time time) {
        Enumeration elements = header_.elements();
        if (elements.hasMoreElements()) {
            LogHeader header = (LogHeader)elements.nextElement();
         
            long recordNum =
                time.getUtime() / header.timeStep_
                + (((time.getUtime() % header.timeStep_) == 0) ? 0 : 1);
            return existRecord((int)recordNum);
        } else {
            return false;
        }
    }

    public int getLogObjectNum() {
        return header_.size();
    }

    public int getDataLength(String objectName) {
        LogHeader header = (LogHeader)header_.get(objectName);
        return header.recordSize_ / LogHeader.FLOAT_DATA_SIZE;
    }

    /**
     * �ǡ����ɤ߽Ф�
     *   dataModelArray�ǻ��ꤵ�줿�ǡ��������ƥ��origin����
     *   offset�ʤ���Ȥ�����count�����ɤ߽Ф�
     *
     * @param   origin          �ɤ߽Ф����ϥ쥳����
     * @param   offset          �쥳���ɥ��ե��å�
     * @param   count           �ɤ߽Ф��쥳���ɿ�
     * @param   dataModelArray  �ǡ�����ǥ�����
     */
    public void getData(
        long origin,
        int  offset,
        int  count,
        DataModel[] dataModelArray
    ) {
        if (readFile_ == null) { return; }

        // �ɤ߽Ф�����
        int numItems = dataModelArray.length;   // �����ƥ������
        ArrayList<String> objList = new ArrayList<String>();    // ���֥�������̾�Υꥹ��
        HashMap<String, ArrayList<DataSeries> > dsListMap = new HashMap<String, ArrayList<DataSeries> >();      // �ǡ�������ꥹ�ȤΥޥå�
        HashMap<String, ArrayList<Object> > indexListMap = new HashMap<String, ArrayList<Object> >();   // ź���ꥹ�ȤΥޥå�
        HashMap<String, ArrayList<Integer> > posListMap = new HashMap<String, ArrayList<Integer> >();     // ���������֥ꥹ�ȤΥޥå�
        HashMap<String, ArrayList<Integer> > sizeListMap = new HashMap<String, ArrayList<Integer> >();    // ���󥵥����ꥹ�ȤΥޥå�
        for (int i = 0; i < numItems; i ++) {    // �����ƥ��ʬ�롼��
            DataItem di = dataModelArray[i].dataItem;       // �ǡ��������ƥ�
            DataSeries ds = dataModelArray[i].dataSeries;   // �ǡ�������
            String obj = di.object;     // ���֥�������̾
            if (obj == null || obj.equals("")) {  // ���֥�������̾�ʤ�?
                obj = NONAME_OBJECT;    // ̵̾���֥�������
            }

	    // ź������(node.attribute.index)
			//System.out.println(i+":"+obj+":"+di.node+":"+di.attribute+" :: "+indexMapMap_);
            Object ind = ((Map)indexMapMap_.get(obj)).get(
                di.node + "." + di.attribute
                + (di.index >= 0 ? "." + di.index : "")
            );

            // �ǡ�������ꥹ�ȼ���
            ArrayList<DataSeries> dsList = dsListMap.get(obj);
            // ź���ꥹ�ȼ���
            ArrayList<Object> indList = indexListMap.get(obj);
            // ���������֥ꥹ�ȼ���
            ArrayList<Integer> posList = posListMap.get(obj);
            // ����Ĺ�ꥹ�ȼ���
            ArrayList<Integer> sizeList = sizeListMap.get(obj);
            if (dsList == null) {                 // ���ƤΥ��֥�������?
                objList.add(obj);                 // ���֥������ȥꥹ�Ȥ��ɲ�
                dsList = new ArrayList<DataSeries>();         // �ǡ�������ꥹ������
                indList = new ArrayList<Object>();        // ź���ꥹ������
                posList = new ArrayList<Integer>();        // ���������֥ꥹ������
                sizeList = new ArrayList<Integer>();       // ����Ĺ�ꥹ������
                dsListMap.put(obj, dsList);       // �ǡ�������ꥹ�ȥޥåפ��ɲ�
                indexListMap.put(obj, indList);   // ź���ꥹ�ȥޥåפ��ɲ�
                posListMap.put(obj, posList);     // ���������֥ꥹ�ȥޥåפ��ɲ�
                sizeListMap.put(obj, sizeList);   // ����Ĺ�ꥹ�ȥޥåפ��ɲ�
            }
            int size = ds.getSize();              // �ǡ������󥵥�������
            int pos = (ds.getHeadPos() + offset) % size;    // ���������ַ���
            dsList.add(ds);                       // �ǡ�������ꥹ�Ȥ��ɲ�
            indList.add(ind);                     // ź���ꥹ�Ȥ��ɲ�
            posList.add(new Integer(pos));        // ���������֥ꥹ�Ȥ��ɲ�
            sizeList.add(new Integer(size));      // ����Ĺ�ꥹ�Ȥ��ɲ�
        }
        // �ǡ����ɤ߽Ф�
        int numObjs = indexListMap.size();  // ���֥������ȿ�����
        for (int i = 0; i < numObjs; i++) { // �����֥������ȥ롼��
            String obj = (String)objList.get(i);    // ���֥�������̾
            LogHeader header = (LogHeader)header_.get(obj); // �إå�
            int itemsPerRec = header.recordSize_ / 4; // �쥳���ɤ�����Υ����ƥ��
            double[] record = new double[itemsPerRec];    // �쥳���ɥХåե�
            double[] data;  // �ǡ����Хåե�
            long recNo = origin + offset;   // �쥳�����ֹ�
            // �ꥹ��
            ArrayList dsList = (ArrayList)dsListMap.get(obj);   // �ǡ�������ꥹ��
            ArrayList indList = (ArrayList)indexListMap.get(obj);   // ź���ꥹ��
            ArrayList posList = (ArrayList)posListMap.get(obj);// ���������֥ꥹ��
            ArrayList sizeList = (ArrayList)sizeListMap.get(obj); // ����Ĺ�ꥹ��
            int itemCount = dsList.size();   // �����ƥ��
            int[] posArray = new int[itemCount]; // ��������������
            // ���������֥ꥹ�Ȥ�����˥��ԡ�
            for (int j = 0; j < itemCount; j++) {
                posArray[j] = ((Integer)posList.get(j)).intValue();
            }
            // �ե�����
            RandomAccessFile file = (RandomAccessFile)readFile_.get(obj);
            //System.out.println("obj=" + obj);
            synchronized (file) {
                try {
                    // ���ϥ쥳���ɤ���Ƭ�쥳���ɤ���������
                    if (recNo < 0) {
                        // ��Ƭ�쥳���ɤޤǥ�����
                        file.seek((long)header.headerSize_);
                    } else if (recNo < header.numRecords_) {
                        // �����쥳���ɤޤǥ�����
                        file.seek(
                            (long)header.headerSize_ + header.recordSize_ * recNo
                        );
                    }

                    // �쥳���ɿ�ʬ�롼��
                    for (int rec = 0; rec < count; rec++) {
                        // �쥳�����ɤ߽Ф�

                        // �쥳�����ϰϳ�?
                        if (recNo < 0 || recNo >= header.numRecords_) {
                            //System.out.println("record = NaN, numRecords=" + header.numRecords_);
                            for (int k = 0; k < itemsPerRec; k++) {
                                record[k] = Double.NaN;
                            }
                        } else  {
                            for (int k = 0; k < itemsPerRec; k++) {
                                record[k] = file.readFloat();
                            }
                        }

                        // �����ƥ��ʬ�롼��
                        for (int item = 0; item < itemCount; item++) {
                            //data = ((DataSeries)dsList.get(item)).getData();
                            DataSeries ds = (DataSeries)dsList.get(item);
                            data = ds.getData();
                            data[posArray[item]] = record[((Integer)indList.get(item)).intValue()];
                            if (posArray[item] < (((Integer)sizeList.get(item)).intValue() - 1)) {
                                posArray[item]++;
                            } else {
                                posArray[item] = 0;
                            }
                        }
                        recNo++;
                    }
                } catch (IOException ex) { ex.printStackTrace(); }
            }
        }
    }

    private void _makeIndexMapMap(LogHeader header) {
        String[] format = header.dataFormat_;
        Map<String, Integer> indexMap = new HashMap<String, Integer>();
        int index = 0;
        for (int i = 0; i < format.length / 2; i ++) {
            if (header.getUnitSize(i) == 0) {
                indexMap.put(format[i * 2], new Integer(index));
                index ++;
            } else {
                for (int j = 0; j < header.getUnitSize(i); j ++) {
                    StringBuffer attrName = new StringBuffer(format[i * 2]);
                    attrName.append('.');
                    attrName.append(j);
                    indexMap.put(attrName.toString(), new Integer(index));
                    index ++;
                }
            }
        }
        indexMapMap_.put(header.objectName_, indexMap);
    }

    //--------------------------------------------------------------------
    // Inner Class
    /**
     * ���إå������饹
     *
     *  version 1.1.0 ����totalTime_, startTime_, endTime_, timeStep_
     * ��float����long���ѹ����줿��
     *  version 1.1.1 ���鴳�Ŀ�������¸�����褦���ѹ�
     */
    class LogHeader {
        // ����Ĺ�إå���
        public byte[]   version_;     // ���եȥ������С������
        public int      headerSize_;  // �إå�������
        public long     totalTime_;   // ���ߥ�졼����������
        public long     startTime_;   // ���ߥ�졼����󳫻ϻ���
        public long     endTime_;     // ���ߥ�졼�����λ����
        public long     timeStep_;    // ���ƥåץ����� (usec)
        public int      method_;      // ��ʬˡ
        public int      recordSize_;  // 1�쥳��������Υǡ�����(byte��)
        public int      numRecords_;   // ��쥳���ɿ�
        public byte[]   reserved_;    // �ꥶ���֥�
        public byte[]   reserved_v1_0_;    // �ꥶ���֥�(version 1.0)

        // ����Ĺ�إå���
        public String   objectName_;  // ���֥�������̾
        public String[] dataFormat_;  // �ǡ����ե����ޥå�

        public int[]    unitSize_;    //

        private static final int VERSION_DATA_SIZE = 4;
        private static final int INT_DATA_SIZE = 4;       
        private static final int LONG_DATA_SIZE = 8;
        private static final int FLOAT_DATA_SIZE = 4;
        private static final int FIXED_PART_SIZE = 64;
        private static final int RESERVED_DATA_SIZE =
            FIXED_PART_SIZE - (
                VERSION_DATA_SIZE +
                INT_DATA_SIZE * 4 +
                LONG_DATA_SIZE * 4
            );

        private static final int RESERVED_DATA_SIZE_V1_0 =
            FIXED_PART_SIZE - (
                VERSION_DATA_SIZE +
                INT_DATA_SIZE * 4 +
                FLOAT_DATA_SIZE * 3
            );

        private static final int END_TIME_SEEK_POINT =
            (VERSION_DATA_SIZE + INT_DATA_SIZE + LONG_DATA_SIZE * 2);

        private static final int NUM_RECORDS_SEEK_POINT =
            (VERSION_DATA_SIZE + INT_DATA_SIZE * 3 + LONG_DATA_SIZE * 4);

        LogHeader() {
            reserved_ = new byte[RESERVED_DATA_SIZE];
        }

        LogHeader(String objectName, String[] format)
            throws LogFileFormatException
        {
            reserved_ = new byte[RESERVED_DATA_SIZE];
            objectName_ = objectName;
            dataFormat_ = format;

            // �إå��������η׻�
            headerSize_ = FIXED_PART_SIZE;
            headerSize_ += objectName_.length() + 1;
            for (int i = 0; i < dataFormat_.length; i ++) {
                headerSize_ += dataFormat_[i].length() + 1;
            }

            unitSize_ = new int[dataFormat_.length / 2];

            // 1�쥳��������Υǡ����̤�׻�
            recordSize_ = 0;
            for (int i = 0; i < dataFormat_.length / 2; i ++) {
                if (!dataFormat_[i * 2 + 1].startsWith("float")) {
                    throw new LogFileFormatException();
                }
                if (dataFormat_[i * 2 + 1].equals("float")) {
                    unitSize_[i] = 0;
                    recordSize_ ++;
                } else {
                    try {
                        unitSize_[i] = Integer.parseInt(
                            dataFormat_[i * 2 + 1].substring(
                                dataFormat_[i * 2 + 1].indexOf('[') + 1,
                                dataFormat_[i * 2 + 1].indexOf(']')
                            )
                        );

                        recordSize_ += unitSize_[i];
                    } catch (NumberFormatException ex) {
                        throw new LogFileFormatException();
                    } catch (StringIndexOutOfBoundsException ex) {
                        throw new LogFileFormatException();
                    }
                }
            }
            recordSize_ *= FLOAT_DATA_SIZE;
        }

        public int getVersion() {
            return (
                version_[0] * 1000 +
                version_[1] * 100 +
                version_[2] * 10 + 
                version_[3]
            );
        }

        public void output(DataOutputStream out)
            throws IOException
        {
            // for Debug
            /*
            System.out.println("Log header for export");
            System.out.println("Header Size: " + headerSize_);
            System.out.println("Total Time[us]: " + totalTime_);
            System.out.println("Start Time: " + startTime_);
            System.out.println("End Time: " + endTime_);
            System.out.println("method: " + method_);
            System.out.println("recordSize[byte]: " + recordSize_);
            System.out.println("numRecords: " + numRecords_);
            */

            version_ = new byte[] {0, 1, 1, 1};  // version 1.1.0
            out.write(version_, 0, VERSION_DATA_SIZE);
            out.writeInt(headerSize_);
            out.writeLong(totalTime_);
            out.writeLong(startTime_);
            out.writeLong(endTime_);
            out.writeLong(timeStep_);
            out.writeInt(method_);
            out.writeInt(recordSize_);
            out.writeInt(numRecords_);
            out.write(reserved_, 0, RESERVED_DATA_SIZE);
            out.writeBytes(objectName_);
            out.writeByte(0);
            for (int i = 0; i < dataFormat_.length; i ++) {
                //if (i % 2 == 0) System.out.print("   " + dataFormat_[i]);
                out.writeBytes(dataFormat_[i]);
                out.writeByte(0);
            }
            //System.out.println("");
        }

        public void input(DataInputStream in)
            throws LogFileFormatException, IOException
        {
            version_ = new byte[4];
            in.readFully(version_);
            if (getVersion() <= 100) {
                 reserved_v1_0_ = new byte[RESERVED_DATA_SIZE_V1_0];
            }

            headerSize_ = in.readInt();

            if (getVersion() <= 100) {
                totalTime_ = new Time(in.readDouble()).getUtime();
                startTime_ = new Time(in.readDouble()).getUtime();
                endTime_ = new Time(in.readDouble()).getUtime();
                timeStep_ = in.readInt();
            } else {
                totalTime_ = in.readLong();
                startTime_ = in.readLong();
                endTime_ = in.readLong();
                timeStep_ = in.readLong();
            }
            method_ = in.readInt();
            recordSize_ = in.readInt();

            if (getVersion() <= 100) {
                 in.readFully(reserved_v1_0_);
            } else {
                 numRecords_ = in.readInt();
                 in.readFully(reserved_);
            }
         
            // for Debug
            /*
            System.out.println("Log header");
            System.out.println("Header Size: " + headerSize_);
            System.out.println("Total Time[us]: " + totalTime_);
            System.out.println("Start Time: " + startTime_);
            System.out.println("End Time: " + endTime_);
            System.out.println("method: " + method_);
            System.out.println("recordSize[byte]: " + recordSize_);
            System.out.println("numRecords: " + numRecords_);
            */
         
            byte[] readBuffer = new byte[headerSize_ - FIXED_PART_SIZE];
            in.readFully(readBuffer);
         
            // ���֥�������̾�����
            int ptr;
            for (ptr = 0; ptr < readBuffer.length; ptr ++) {
                if (readBuffer[ptr] == 0) {
                    objectName_ = new String(readBuffer, 0, ptr);
                    ptr ++;
                    break;
                }
            }
            
            // �ե����ޥåȥ��ȥ�󥰤ο��򥫥����
            int counter = 0;
            for (int j = ptr; j < readBuffer.length; j ++) {
                if (readBuffer[j] == 0) counter ++;
            }
            
            // �ե����ޥåȥ��ȥ�󥰤μ���
            dataFormat_ = new String[counter];
            counter = 0;
            for (int j = ptr; j < readBuffer.length; j ++) {
                if (readBuffer[j] == 0) {
                    dataFormat_[counter] = new String(readBuffer, ptr, j - ptr);
                    counter ++;
                    ptr = j + 1;
                }
            }
        }

        public void calcUnitSize() throws LogFileFormatException {
            unitSize_ = new int[dataFormat_.length / 2];

            // 1�쥳��������Υǡ����̤�׻�
            for (int i = 0; i < dataFormat_.length / 2; i ++) {
                if (!dataFormat_[i * 2 + 1].startsWith("float")) {
                    throw new LogFileFormatException();
                }
                if (dataFormat_[i * 2 + 1].equals("float")) {
                    unitSize_[i] = 0;
                } else {
                    try {
                        unitSize_[i] = Integer.parseInt(
                            dataFormat_[i * 2 + 1].substring(
                                dataFormat_[i * 2 + 1].indexOf('[') + 1,
                                dataFormat_[i * 2 + 1].indexOf(']')
                            )
                        );
                    } catch (NumberFormatException ex) {
                        throw new LogFileFormatException();
                    } catch (StringIndexOutOfBoundsException ex) {
                        throw new LogFileFormatException();
                    }
                }
            }
        }

        int getUnitSize(int index) { return unitSize_[index]; }

        public void outEndTime(RandomAccessFile file) throws IOException {
            file.seek(END_TIME_SEEK_POINT);    // ��λ���֤ޤǥ�����
            file.writeLong(endTime_);
            file.seek(NUM_RECORDS_SEEK_POINT);    // ��λ���֤ޤǥ�����
            file.writeInt(numRecords_);
            //System.out.println("outEndTime(): numRecords="+numRecords_);
        }

        /**
         * version 1.0�ϥե������Ĺ������numRecords_�򻻽�
         */
        void setFileSize(long length) {
            if (recordSize_ == 0) {
                numRecords_ = 0;
            } else {
                numRecords_ = (int)((length - headerSize_) / recordSize_);
            }
        }
    }

    /**
     * ���ꥸ�����إå������饹
     */
    class CollisionLogHeader {
        // ����Ĺ�إå���
        public byte[]   version_;     // ���եȥ������С������
        public int      headerSize_;  // �إå�������
        public long     totalTime_;   // ���ߥ�졼����������
        public long     startTime_;   // ���ߥ�졼����󳫻ϻ���
        public long     endTime_;     // ���ߥ�졼�����λ����
        public long     timeStep_;    // ���ƥåץ����� (usec)
        public byte[]   reserved_;    // �ꥶ���֥�
        public byte[]   reserved_v1_0_;    // �ꥶ���֥�(version 1.0)

        // ����Ĺ�إå���
        public int[] position_;    // �ե�����ݥ���

        public int currentPos_;
        public int numRecords_;   // ��쥳���ɿ�

        private static final int VERSION_DATA_SIZE = 4;
        private static final int INT_DATA_SIZE = 4;       
        private static final int LONG_DATA_SIZE = 8;
        private static final int FLOAT_DATA_SIZE = 4;
        private static final int FIXED_PART_SIZE = 64;
        private static final int RESERVED_DATA_SIZE =
            FIXED_PART_SIZE -
            (
                VERSION_DATA_SIZE +
                INT_DATA_SIZE +
                LONG_DATA_SIZE * 4
            );

        private static final int RESERVED_DATA_SIZE_V1_0 =
            FIXED_PART_SIZE -
            (
                VERSION_DATA_SIZE +
                INT_DATA_SIZE * 2 +
                FLOAT_DATA_SIZE * 3
            );

        private static final int END_TIME_SEEK_POINT =
            (VERSION_DATA_SIZE + INT_DATA_SIZE + LONG_DATA_SIZE * 2);

        public CollisionLogHeader() {
            reserved_ = new byte[RESERVED_DATA_SIZE];
        }

        public CollisionLogHeader(SimulationTime time) {
            reserved_ = new byte[RESERVED_DATA_SIZE];

            totalTime_ = time.totalTime_.getUtime();
            startTime_ = time.startTime_.getUtime();
            endTime_ = 0;
            timeStep_ = time.timeStep_.getUtime();

            int frameSize =
                (int)(totalTime_ / timeStep_) +
                    ((totalTime_ % timeStep_) > 0 ? 1 : 0) + 1;

            // �Ǹ�Υե졼��Υǡ�����������׻����뤿���
            // �ե졼��� + 1 �����ե�����ݥ��󥿤��ݻ�����
            position_ = new int[frameSize + 1];
            headerSize_ = FIXED_PART_SIZE + (frameSize + 1) * INT_DATA_SIZE;
        }

        public int getVersion() {
            return (
                version_[0] * 1000 +
                version_[1] * 100 +
                version_[2] * 10 + 
                version_[3]
            );
        }

        public void input(DataInputStream in)
            throws LogFileFormatException, IOException
        {
            version_ = new byte[4];
            in.readFully(version_);
            if (getVersion() <= 100) {
                 reserved_v1_0_ = new byte[RESERVED_DATA_SIZE_V1_0];
            }

            headerSize_ = in.readInt();

            if (getVersion() <= 100) {
                totalTime_ = new Time(in.readDouble()).getUtime();
                startTime_ = new Time(in.readDouble()).getUtime();
                endTime_ = new Time(in.readDouble()).getUtime();
                timeStep_ = in.readInt();
            } else {
                totalTime_ = in.readLong();
                startTime_ = in.readLong();
                endTime_ = in.readLong();
                timeStep_ = in.readLong();
            }

            if (getVersion() <= 100) {
                 in.readFully(reserved_v1_0_);
            } else {
                 in.readFully(reserved_);
            }

            // for Debug
            /*
            System.out.println("Collision data log header");
            System.out.println("Header Size: " + headerSize_);
            System.out.println("Total Time : " + totalTime_);
            System.out.println("Start Time: " + startTime_);
            System.out.println("End Time: " + endTime_);
            */

            int frameSize =
                (int)((endTime_ - startTime_) / timeStep_) +
                    (((endTime_ - startTime_) % timeStep_) > 0 ? 1 : 0) + 1;
             position_ = new int[frameSize + 1];
             for (int i = 0; i < frameSize + 1; i ++) {
                 position_[i] = in.readInt();
             }
             numRecords_ = frameSize;
             //System.out.println("numRecords=" + numRecords_);
        }

        public void output(DataOutputStream out) throws IOException {
            //System.out.println("StartTime=" + startTime_ + ", endTime=" + endTime_);
            version_ = new byte[] {0, 1, 1, 0};  // version 1.0.0
            out.write(version_, 0, VERSION_DATA_SIZE);
            out.writeInt(headerSize_);
            out.writeLong(totalTime_);
            out.writeLong(startTime_);
            out.writeLong(endTime_);
            out.writeLong(timeStep_);
            out.write(reserved_, 0, RESERVED_DATA_SIZE);
            for (int i = 0; i < position_.length; i ++) {
                out.writeInt(position_[i]);
            }
        }

        public void outEndTime(RandomAccessFile file) throws IOException {
            file.seek(END_TIME_SEEK_POINT);    // ��λ���֤ޤǥ�����
            file.writeLong(endTime_);
        }

        public void outPositions(RandomAccessFile file) throws IOException {
            file.seek(FIXED_PART_SIZE);
            for (int i = 0; i < position_.length; i ++) {
                file.writeInt(position_[i]);
            }
        }
    }
// added by GRX 20070207
    public float[] get(String objectName, long record) throws IOException {
        if (readFile_ == null) return null;

        LogHeader header = (LogHeader)header_.get(objectName);
        if (header == null) return null;

        RandomAccessFile file = (RandomAccessFile)readFile_.get(objectName);

        float[] data = new float[header.recordSize_ / 4];

        try {
            synchronized (file) {
                file.seek((long)header.headerSize_ + header.recordSize_ * record);
                for (int i = 0; i < data.length; i ++)
                    data[i] = file.readFloat();
            }
        } catch (EOFException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            closeAsRead();
            throw ex;
        }
        return data;
    }

    public CollisionPoint[] getCollisionPointData(int frameNum) throws IOException {
	    if (collisionLog_.position_.length < frameNum + 2)  {
				return null;
		}
        int size = collisionLog_.position_[frameNum + 1] - collisionLog_.position_[frameNum];
		int data_size=0;
		Enumeration elements = header_.elements();
		LogHeader header = (LogHeader)elements.nextElement();
		int version = header.getVersion();
		if (version <= 110){
	  		data_size = 6 * 4;
		}else{
	  		data_size = COLLISION_DATA_SIZE;
		}
        if ((size % data_size) != 0 || size <= 0) 
        	return null;
        size /= data_size;
	
        collisionIn_.seek(collisionLog_.position_[frameNum]);
        CollisionPoint[] data = new CollisionPoint[size];
        for (int i = 0; i < size; i ++) {
           	data[i] = new CollisionPoint();
           	data[i].normal = new double[3];
           	data[i].normal[0] = collisionIn_.readFloat();
           	data[i].normal[1] = collisionIn_.readFloat();
           	data[i].normal[2] = collisionIn_.readFloat();
           	data[i].position = new double[3];
           	data[i].position[0] = collisionIn_.readFloat();
           	data[i].position[1] = collisionIn_.readFloat();
           	data[i].position[2] = collisionIn_.readFloat();
	    	if (version <= 110){
	      		data[i].idepth = 0.01;
	    	}else{
	      		data[i].idepth = collisionIn_.readDouble();
	    	}
        }
        return data;
    }

    public int getRecordNum(String objectName) {
        LogHeader header = (LogHeader)header_.get(objectName);
        return header.numRecords_;
    }

// added by GRX 20070416
	public void setTempDir(String tmp) {
		tmpdir = tmp;
		COLLISION_LOG = tmpdir+File.separator+"CollisionData.col";
	}
}
