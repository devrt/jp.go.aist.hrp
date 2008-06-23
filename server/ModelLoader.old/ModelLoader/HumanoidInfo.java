package jp.go.aist.hrp.simulator;

/**
 * HumanoidInfo class
 * PROTO Humanoid�ξ�����ݻ����륯�饹��
 * @author K Saito (Kernel Co.,Ltd.)
 * @version 1.0 (2001/01/25)
 */
public class HumanoidInfo
{
    public String name_ = "";
    public String[] info_ = new String[0];
    public HumanoidInfo(){}
    public void setParam(String defName, VrmlSceneEx scene){
        name_ = defName;
        info_ = ProtoFieldGettor.getStringArray(defName,scene,"info",info_);
    }
}
