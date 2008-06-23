package jp.go.aist.hrp.simulator;

import java.util.*;

import javax.vecmath.*;
import javax.media.j3d.Node;
import javax.media.j3d.Group;
import javax.media.j3d.SharedGroup;
import javax.media.j3d.*;
import com.sun.j3d.utils.geometry.*;


//���֥������Ȥξ�����ݻ����륯�饹
/**
 * ObjectStruct class
 * @author K Saito (Kernel Co.,Ltd.)
 * @version 1.0 (2002/01/25)
 */
public class ObjectStruct{
    public static final int MODE_ROBOT=0;
    public static final int MODE_ENVIROMENT=1;

    public static final String ENVIROMENT_BODY_NAME="rootjoint";
    public static final String ENVIROMENT_SEGMENT_NAME="rootsegment";

    private static final String NODE_TYPE_JOINT   = "Joint";
    private static final String NODE_TYPE_SEGMENT = "Segment";
    private static final String NODE_TYPE_HUMANOID = "Humanoid";

    private static final int MODEL_NODE_GROUP = 0;
    private static final int MODEL_NODE_BRANCHGROUP = 1;
    private static final int MODEL_NODE_TRANSFORMGROUP = 2;
    private static final int MODEL_NODE_PRIMITIVE = 3;
    
    //�������
    private static final String[] SENSOR_NAME = {
        "ForceSensor",
        "Gyro",
        "AccelerationSensor",
        "PressureSensor",
        "PhotoInterrupter",
	"VisionSensor",
	"TorqueSensor",
    };

    private static final SensorInfoFactory[] SENSOR_FACTORY = {
        new ForceSensorInfoFactory(),
        new GyroSensorInfoFactory(),
        new AccelerationSensorInfoFactory(),
        new PressureSensorInfoFactory(),
        new PhotoInterrupterSensorInfoFactory(),
	new VisionSensorInfoFactory(),
        new TorqueSensorInfoFactory(),
    };
    
    private Hashtable<String, BodyStruct> htBody_ = null;//BodyName-BodyStruct
    private BodyStruct rootBody_;  //root
    private int mode_;
    private int index_;
    private VrmlSceneEx scene_;// reference to the scene
    private VrmlLoaderEx loader_;

    private HumanoidInfo humanoidInfo_;//PROTO Humanoid

    //���󥹥ȥ饯��
    public ObjectStruct(String url){

        System.out.println("Loader: loadURL()");
        System.out.println("\tURL = " + url);

        // load
        System.out.print("loading ...");
        loader_ = new VrmlLoaderEx();
        try {
            java.net.URL u = new java.net.URL(url);
            scene_ = (VrmlSceneEx)loader_.load(u);
        } catch (java.net.MalformedURLException ex) {
            System.out.println("* MalformedURLException");
        } catch (java.io.IOException ex) {
            System.out.println("* IOException");
        }
        System.out.println(" finished.");
        
        System.out.print("Loader: _parse() ");
        
        Hashtable ht   = scene_.getNamedObjects();      // VrmlSceneEx����DEF̾��������롣
        Hashtable table = new Hashtable();             // DEF���б�����ϣ£ʤ��ݻ�
        java.lang.Object key = null;
        for (Enumeration keys = ht.keys(); keys.hasMoreElements();) {
            key = keys.nextElement();                  // 
            try {
                Node obj = (Node) ht.get(key);
                if (obj instanceof Link) {             // Link�λ��ϡ�SharedGroup���ݻ�����
                    Link lk = (Link)ht.get(key);
                    lk.setCapability(Link.ALLOW_SHARED_GROUP_READ);
                    lk.setCapability(Link.ALLOW_SHARED_GROUP_WRITE);
                    SharedGroup sg = lk.getSharedGroup();
                    sg.setCapability(SharedGroup.ALLOW_CHILDREN_READ);
                    sg.setCapability(SharedGroup.ALLOW_CHILDREN_WRITE);
                    for (int j = 0; j < sg.numChildren(); j++) {
                        Node sgNode = (Node) sg.getChild(j);
                        if (sgNode instanceof TransformGroup) {
                            TransformGroup tg = (TransformGroup) sgNode;
                            tg.setCapability(TransformGroup.ALLOW_CHILDREN_READ);         // Children Read/Write
                            tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);        // Transform Read/Write
                            tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
                            tg.setCapability(TransformGroup.ALLOW_LOCAL_TO_VWORLD_READ);  // LocalToVworld Read
                            tg.setCapability(TransformGroup.ENABLE_PICK_REPORTING);       // LocalToVworld Read
                            break;
                        }
                    }
                    table.put(key, sg);
                } else if (obj instanceof TransformGroup) {
                    table.put(key, obj);               // 
                } else {
                    table.put(key, obj);               // Link,TransformGroup�ʳ��λ�
                }
            }  catch(Exception e)  {
                ;
            }
        }
        
        htBody_=new Hashtable<String, BodyStruct>();
        humanoidInfo_ = new HumanoidInfo();
        mode_=MODE_ROBOT;
        if (mode_==MODE_ROBOT){
                        //index_��htBody���ǿ�-1��
            index_=-1;

            _analyze(table,scene_.getSceneGroup(),null,null,new Transform3D(),0);// �����󥰥�դ�é�롣
            
            //rootBody��õ��(���ä���뤤�����Ŷ�)
            key = null;                    // ������������
            for (Enumeration keys = htBody_.keys(); keys.hasMoreElements();) {
                key = keys.nextElement();
                BodyStruct body=htBody_.get(key);
                if (body.getId()==0) {               //root?
                    rootBody_=body;
                    break;
                }
            }
            
        }else if (mode_==MODE_ENVIROMENT){
            index_=0;
            rootBody_=new BodyStruct(scene_.getSceneGroup(),null,index_);
            rootBody_.setParamForEnviroment(ENVIROMENT_BODY_NAME,ENVIROMENT_SEGMENT_NAME);        //�������󥻥å�
            htBody_.put(ENVIROMENT_BODY_NAME,rootBody_);                //�ơ��֥���ɲ�
            _analyze(table,scene_.getSceneGroup(),rootBody_,null,new Transform3D(),0);// �����󥰥�դ�é�롣
        }
    }
    
    public BodyStruct rootBody(){
        return rootBody_;
    }

    public BodyStruct getBody(String name){
        return htBody_.get(name);
    }

    public String[] getBodyList(){
         String[] s=new String[htBody_.size()];
         int c=0;
         for (Enumeration e = htBody_.keys() ; e.hasMoreElements() ;) {
            s[c]=(String)e.nextElement();
            c++;
        }
        return s;

    }

    public BodyStruct[] getBodys(){
         BodyStruct[] b=new BodyStruct[htBody_.size()];
         int c=0;
         for (Enumeration e = htBody_.keys() ; e.hasMoreElements() ;) {
            String name =(String)e.nextElement();
            b[c]=htBody_.get(name);
            c++;
        }
        return b;
    }

    public String[] getSegmentList(){
        Vector<String> vec= new Vector<String>();
        for (Enumeration e = htBody_.keys() ; e.hasMoreElements() ;) {
            String name =(String)e.nextElement();
            BodyStruct body=htBody_.get(name);
            String sName=body.getSegmentName();
            if (sName!=null){
                vec.add(sName);
            }
        }
        
         String[] s=new String[vec.size()];
         for (int i=0;i<vec.size();i++){
            s[i]=(String)vec.get(i);
         }
         return s;

    }

    public LinkInfo_impl[] getLinkInfos(){
        BodyStruct[] bs=getBodys();
        LinkInfo_impl[] mos=new LinkInfo_impl[bs.length];
        for(int i=0;i<bs.length;i++){
            mos[bs[i].getId()]=bs[i].getLinkInfo();
        }
        return mos;
    }
    
    public HumanoidInfo getHumanoidInfo(){
        return humanoidInfo_;
    }
    /**
     * �Ƶ�Ū��Node����Ϥ���BodyStruct����htBody_�˳�Ǽ
     *
     * @param  table         defName-Node�Υơ��֥�
     * @param  nd            ���ߤ�Node
     * @param  motherBody    ��Body
     * @param  sisterBody    ��Body
     * @param  tfBody,       �ܥǥ������ΰ��ֻ���
     * @param  depth
     * @return  nd��Joint���ä���礽��Body,�����Ǥʤ����null;
     */
    private BodyStruct _analyze(Hashtable table, Node nd,BodyStruct motherBody, BodyStruct sisterBody,
                                Transform3D tfBody,int depth)
    {

        Node node = nd;
        Node nameNode = nd;
        Group group = null;
        BodyStruct myBody=null;
        
        if (node instanceof Link) {                         // Link�Ρ��ɤλ���SharedGroup��
            Link lk = (Link) node;                          // DEF���б������ݻ�����Ƥ���
            SharedGroup sg = lk.getSharedGroup();
            nameNode = (Node) sg;
            //Joint��
            if(sg.numChildren() == 1){
                node = (Node) sg.getChild(0);
            }else{
                node = (Node) sg;
            }
        }

        //System.out.println("trf = " + tfBody);

                                                             // �ɤ�ʥΡ��ɤ�Ĵ�٤�
        if (node instanceof Group) {
            int nodetype = MODEL_NODE_GROUP;
            try {
                BranchGroup bg = (BranchGroup)node;
                nodetype = MODEL_NODE_BRANCHGROUP;
            } catch (ClassCastException exbg) {
                try {
                    TransformGroup tg = (TransformGroup)node;
                    nodetype = MODEL_NODE_TRANSFORMGROUP;
                } catch (ClassCastException extg) {
                    try {
                        Primitive pr = (Primitive)node;
                        nodetype = MODEL_NODE_GROUP;                  // Primitive��Group_Node
                        //nodetype = MODEL_NODE_PRIMITIVE;
                    } catch (ClassCastException expr) {
                    }
                }
            }
    
            //---------------------------------------------------------------------------
            // ����nameNode�ξ������(MODE_ROBOT�ΤȤ�Enviroment�ΤȤ���Joint�������ʤ������̵��)
            //
            if (table.containsValue(nameNode) && mode_==MODE_ROBOT) {                    // �Ρ��ɤϡ�DEF����Ƥ���
                
                                                                //node����def̾�����
                java.lang.Object key = null;                    // ������������
                for (Enumeration keys = table.keys(); keys.hasMoreElements();) {
                    key = keys.nextElement();
                    if (table.get(key) == nameNode) {               // �Ρ��ɤ��������
                        break;
                    }
                }
                                                           // NodeType�����
                String defName = key.toString();
                String nodeType = scene_.getNodeTypeString( defName );
        
                //System.out.println("def:" + nd);
                if (nodeType.equals( NODE_TYPE_JOINT )) {       // NodeType == Joint
                    for (int i=0; i<depth; i++) System.out.print(" ");
                    System.out.println("Joint("+defName+")");
                
                    index_++;                                   //����С���ʥ���ǥå��������󥿤򥤥󥯥����
                                                                       //���Υ��祤��Ȥξ����ʤ����ܥǥ���¤������
                                                                       //���ΤȤ�,MO�������Ȥ���ɬ�פˤʤ�index������
                    myBody=new BodyStruct((Group)node,motherBody,index_); //����ˡ��������֥ޥޡ���
                    myBody.setJointParam(defName,scene_);        //�������󥻥å�
                    htBody_.put(defName,myBody);                //�ơ��֥���ɲ�

                    if(sisterBody!=null){                        //�Ф�����ʤ�ֻ����͡�
                                                           //�Ф�sister�˻�򥻥å�
                                                           //����ϻ�衪��
                        sisterBody.setSister(myBody);
                
                    }else{                                      //�Ф����ʤ��ʤ�ֻ��Ĺ���͡�
            
                        if(motherBody!=null){                            //�Ƥ�����ʤ�
                                                                //�Ƥ�̼����򹹿�
                            motherBody.setDaughter(myBody);              //��Ĺ���Ϥ錄���衪��
                        }
                    }

                } else if (nodeType.equals( NODE_TYPE_SEGMENT )) {   // NodeType == Segment
                    for (int i=0; i<depth; i++) System.out.print(" ");
                    System.out.println("Segment("+defName+")");
                    //motherBody�˥ǡ�������
                    if(motherBody!=null){
                        motherBody.setSegmentParam(defName,scene_);
                    }
                } else if (nodeType.equals( NODE_TYPE_HUMANOID )) {   // NodeType == Segment
                    for (int i=0; i<depth; i++) System.out.print(" ");
                    System.out.println("Humanoid("+defName+")");
                    //humanoidInfo_�˥ǡ�������
                    humanoidInfo_.setParam(defName,scene_);
                                                        
                } else {                                        // NodeType != Joint Segment

                    //���󥵡�    kokok
                    for(int i = 0 ; i < SENSOR_NAME.length ; i++){
                        if (nodeType.equals(SENSOR_NAME[i])){
                            for (int j=0; j<depth; j++) System.out.print(" ");
                            System.out.println(SENSOR_NAME[i]+"("+defName+")");
                            motherBody.addSensor(
                                SENSOR_FACTORY[i].createSensorInfo( 
                                    defName,
                                    scene_
                                )
                            );
                        }
                    }
                 }
            } else {                                            // DEF����Ƥ��ʤ���
                //
                //System.out.println("no def:" + nd);
            } 
    

            //---------------------------------------------------------------------------
            // �ҥΡ��ɤ�
            //

            if(myBody!=null){                                //Joint�ʤ��
                                                             //Joint�ʤ��Group�ʤϤ�
                group = (Group)node;
            
                //�Ҥ�
                BodyStruct lastDaughterBody=null;                         // �Х���ǥå�����Ĺ����null
                BodyStruct tempBody;                                      //����ͳ�Ǽ��
                for (int i = 0; i < group.numChildren(); i++) {
                    //�ҥΡ��ɲ���
                    tempBody=_analyze(table, group.getChild(i), myBody, lastDaughterBody,
                                      (new Transform3D()),depth+1);
                    
                    if(tempBody!=null){                      //��Node��Joint���ä���
                        lastDaughterBody=tempBody;              //���λҤΥ���ǥå�����ФȤ����ݻ�
                    }
                }
            }else{
                //Joint�ʳ��ʤ�
                // ���ΥΡ��ɤ�̵�뤷�ƻҥΡ��ɤ�
                //��ա�
                //  Segment�ϻҤ�Joint��Segment������ʤ��Ϥ�
                //  �Ǥ⡢����ҥΡ��ɤعԤ�
                //  (�����ΥΡ��ɤ�Segment�ʤ饨�顼�ˤʤ�)
                //  (�����ΥΡ��ɤ�Joint�ʤ餳��Segment����Joint��ƤȤ���)
                
                if (nodetype == MODEL_NODE_GROUP||nodetype == MODEL_NODE_BRANCHGROUP) {         //Group or BranchGroup�Ρ��ɤǤ���
                    group = (Group)node;

                    //�Ҥ�
                    BodyStruct lastDaughterBody=null;                         // �Х���ǥå�����Ĺ����null
                    BodyStruct tempBody;                                      //����ͳ�Ǽ��
                    for (int i = 0; i < group.numChildren(); i++) {
                        //�ҥΡ��ɲ���
                        tempBody=_analyze(table, group.getChild(i), motherBody, lastDaughterBody,
                                          tfBody,depth+1);
                    
                        if(tempBody!=null){                      //��Node��Joint���ä���
                            lastDaughterBody=tempBody;              //���λҤΥ���ǥå�����ФȤ����ݻ�
                        }
                    }
                }else if (nodetype == MODEL_NODE_TRANSFORMGROUP) {         //�Ρ��ɤ�TransformGroup
                    TransformGroup tg = (TransformGroup)node;

                    // �������ɸ�ϤǤΰ��֤Ȼ��������
                    Transform3D t3dLocal = new Transform3D();
                    tg.getTransform(t3dLocal);
                    // ��°����ܥǥ������Ǥΰ��֤Ȼ��������
                    Transform3D newtfBody = new Transform3D(tfBody);
                    newtfBody.mul(t3dLocal);

                    //�Ҥ�
                    BodyStruct lastDaughterBody=null;                         // �Х���ǥå�����Ĺ����null
                    BodyStruct tempBody;                                      //����ͳ�Ǽ��
                    for (int i = 0; i < tg.numChildren(); i++) {
                        //�ҥΡ��ɲ���
                        tempBody=_analyze(table, tg.getChild(i), motherBody, lastDaughterBody,
                                          newtfBody,depth+1);
                    
                        if(tempBody!=null){                      //��Node��Joint���ä���
                            lastDaughterBody=tempBody;              //���λҤΥ���ǥå�����ФȤ����ݻ�
                        }
                    }

                }else{
                    //
                }
            } // if(joint){}else{}
        }else if(node instanceof Shape3D) { // Group�Ρ��ɤ��������ʤ��ʤ顢�Ρ��ɤ�Shape3D��
            Shape3D s3d = (Shape3D)node;
            //���ѷ��������Ͽ
            motherBody.addShape3D(s3d,tfBody);
        }else{                              // �Ρ��ɤϤ���¾��
            //Link�Ρ��ɤϤ��ꤨ�ʤ���

            System.out.println("* The node " + node.toString() + " is not supported.");
        }
        return myBody;
    }
    

}



