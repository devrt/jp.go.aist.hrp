/*
 * Copyright (c) 2008, AIST, the University of Tokyo and General Robotix Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * General Robotix Inc.
 * National Institute of Advanced Industrial Science and Technology (AIST)
 */

/*
 *  GrxModelItem.java
 *
 *  @author Yuichiro Kawasumi (General Robotix, Inc.)
 *  @author Shin'ichiro Nakaoka (AIST)
 */

package com.generalrobotix.ui.item;


import java.io.File;

import javax.media.j3d.Geometry;
import javax.media.j3d.IndexedTriangleArray;
import javax.media.j3d.QuadArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Switch;
import javax.media.j3d.TransparencyAttributes;
import javax.media.j3d.TriangleFanArray;
import javax.vecmath.Point3f;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

import jp.go.aist.hrp.simulator.SensorInfo;
import jp.go.aist.hrp.simulator.CameraPackage.CameraParameter;
import jp.go.aist.hrp.simulator.CameraPackage.CameraType;

import com.generalrobotix.ui.grxui.GrxUIPerspectiveFactory;
import com.generalrobotix.ui.GrxPluginManager;
import com.generalrobotix.ui.util.MessageBundle;
import com.generalrobotix.ui.view.tdview.SceneGraphModifier;
import com.generalrobotix.ui.view.vsensor.Camera_impl;

/**
 * @brief sensor
 */
@SuppressWarnings({ "serial", "unchecked" }) //$NON-NLS-1$ //$NON-NLS-2$
public class GrxSensorItem extends GrxTransformItem implements  Comparable {

	SensorInfo info_;
	private Camera_impl camera_ = null;
	private Switch switchVisibleArea_ = null;


    /**
     * @brief get type of sensor
     * @return type of sensor
     */
    public String type(){
    	return info_.type;
    }

    /**
     * @brief get id of sensor
     * @return id of sensor
     */
    public int id(){
    	return info_.id;
    }

    /**
     * @brief constructor
     * @param info SensorInfo retrieved through ModelLoader
     * @param parentLink link to which this sensor is attached
     */
    public GrxSensorItem(String name, GrxPluginManager manager, GrxModelItem model, SensorInfo info) {
    	super(name, manager, model);

		getMenu().clear();

		Action item;

		// rename
		item = new Action(){
			public String getText(){
				return MessageBundle.get("GrxSensorItem.menu.rename"); //$NON-NLS-1$
			}
			public void run(){
				InputDialog dialog = new InputDialog( null, getText(),
						MessageBundle.get("GrxSensorItem.dialog.message.newName"), getName(),null); //$NON-NLS-1$
				if ( dialog.open() == InputDialog.OK && dialog.getValue() != null)
					rename( dialog.getValue() );
			}
		};
		setMenuItem(item);

		// delete
		item = new Action(){
			public String getText(){
				return MessageBundle.get("GrxSensorItem.menu.delete"); //$NON-NLS-1$
			}
			public void run(){
				if( MessageDialog.openQuestion( null, MessageBundle.get("GrxSensorItem.dialog.title.delete"), //$NON-NLS-1$
						MessageBundle.get("GrxSensorItem.dialog.message.delete") + getName() + " ?") ) //$NON-NLS-1$ //$NON-NLS-2$
					delete();
			}
		};
		setMenuItem(item);

		/* disable copy and paste menus until they are implemented
        // menu item : copy
        item = new Action(){
            public String getText(){
                return "copy";
            }
            public void run(){
                GrxDebugUtil.println("GrxModelItem.GrxSensorItem copy Action");
                manager_.setSelectedGrxBaseItemList();

            }
        };
        setMenuItem(item);

        // menu item : paste
        item = new Action(){
            public String getText(){
                return "paste";
            }
            public void run(){
            }
        };
        setMenuItem(item);
        */
		
		// menu item : add shape
		item = new Action(){
			public String getText(){
				return MessageBundle.get("GrxSensorItem.menu.VRML97"); //$NON-NLS-1$
			}
			public void run(){
				FileDialog fdlg = new FileDialog( GrxUIPerspectiveFactory.getCurrentShell(), SWT.OPEN);
				fdlg.setFilterExtensions(new String[]{"*.wrl"});
				fdlg.setFilterPath(getDefaultDir().getAbsolutePath());
				String fPath = fdlg.open();
				System.out.println("fPath = "+fPath); //$NON-NLS-1$
				if(fPath!=null){
					addShape(fPath);
					setDefaultDirectory(new File(fPath).getParent());
				}
			}
		};
        setMenuItem(item);
        
//      menu item : add primitive shape
        MenuManager subMenu= new MenuManager(MessageBundle.get("GrxSensorItem.menu.primitiveShape")); //$NON-NLS-1$
        setSubMenu(subMenu);      
        item = new Action(){
			public String getText(){
				return "Box"; //$NON-NLS-1$
			}
			public void run(){
				addPrimitiveShape("Box"); //$NON-NLS-1$
			}
		};
		subMenu.add(item);
		item = new Action(){
			public String getText(){
				return "Cone"; //$NON-NLS-1$
			}
			public void run(){
				addPrimitiveShape("Cone"); //$NON-NLS-1$
			}
		};
		subMenu.add(item);
		item = new Action(){
			public String getText(){
				return "Cylinder"; //$NON-NLS-1$
			}
			public void run(){
				addPrimitiveShape("Cylinder"); //$NON-NLS-1$
			}
		};
		subMenu.add(item);
		item = new Action(){
			public String getText(){
				return "Sphere"; //$NON-NLS-1$
			}
			public void run(){
				addPrimitiveShape("Sphere"); //$NON-NLS-1$
			}
		};
		subMenu.add(item);
		setSubMenu(subMenu);

    	info_ = info;

    	setProperty("type", info_.type); //$NON-NLS-1$
    	setProperty("id", String.valueOf(info_.id)); //$NON-NLS-1$
    	translation(info_.translation);
    	rotation(info_.rotation);
    	setProperty("alwaysVisible", "false"); //$NON-NLS-1$ //$NON-NLS-2$

        setIcon("camera.png"); //$NON-NLS-1$

        if (info.type.equals("Vision")) { //$NON-NLS-1$
            CameraParameter prm = new CameraParameter();
            prm.defName = new String(info.name);
            prm.sensorName = new String(info.name);
            prm.sensorId = info.id;

            prm.frontClipDistance = (float)info.specValues[0];
            prm.backClipDistance = (float)info.specValues[1];
            prm.fieldOfView = (float)info.specValues[2];
            try {
                prm.type = CameraType.from_int((int)info.specValues[3]);
            } catch (Exception e) {
                prm.type = CameraType.NONE;
            }
            prm.width  = (int)info.specValues[4];
            prm.height = (int)info.specValues[5];
            prm.frameRate = (float)info.specValues[6];
            
            setDbl("frontClipDistance", prm.frontClipDistance, 4); //$NON-NLS-1$
            setDbl("backClipDistance",  prm.backClipDistance, 4); //$NON-NLS-1$
            setDbl("fieldOfView",       prm.fieldOfView, 6); //$NON-NLS-1$
            if (prm.type == CameraType.NONE){
            	setProperty("cameraType", "NONE"); //$NON-NLS-1$ //$NON-NLS-2$
            }else if (prm.type == CameraType.COLOR){
            	setProperty("cameraType", "COLOR"); //$NON-NLS-1$ //$NON-NLS-2$
            }else if (prm.type == CameraType.MONO){
            	setProperty("cameraType", "MONO"); //$NON-NLS-1$ //$NON-NLS-2$
            }else if (prm.type == CameraType.COLOR_DEPTH){
            	setProperty("cameraType", "COLOR_DEPTH"); //$NON-NLS-1$ //$NON-NLS-2$
            }else if (prm.type == CameraType.MONO_DEPTH){
            	setProperty("cameraType", "MONO_DEPTH"); //$NON-NLS-1$ //$NON-NLS-2$
            }else if (prm.type == CameraType.DEPTH){
            	setProperty("cameraType", "DEPTH"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            setInt("width",             prm.width); //$NON-NLS-1$
            setInt("height",            prm.height); //$NON-NLS-1$
            setDbl("frameRate",         prm.frameRate); //$NON-NLS-1$
            boolean offScreen = false;
            camera_ = new Camera_impl(prm, offScreen);

            tg_.addChild(camera_.getBranchGroup());
            switchVisibleArea_ = SceneGraphModifier._makeSwitchNode(_createShapeOfVisibleArea());
            tg_.addChild(switchVisibleArea_);
        }else if(info.type.equals("RateGyro")){ //$NON-NLS-1$
        	float[] max = new float[3];
        	max[0] = info.specValues[0];
        	max[1] = info.specValues[1];
        	max[2] = info.specValues[2];
        	setFltAry("maxAngularVelocity", max); //$NON-NLS-1$
        }else if(info.type.equals("Acceleration")){ //$NON-NLS-1$
        	float[] max = new float[3];
        	max[0] = info.specValues[0];
        	max[1] = info.specValues[1];
        	max[2] = info.specValues[2];
        	setFltAry("maxAcceleration", max); //$NON-NLS-1$
        }else if(info.type.equals("Force")){ //$NON-NLS-1$
        	float[] maxf = new float[3];
        	maxf[0] = info.specValues[0];
        	maxf[1] = info.specValues[1];
        	maxf[2] = info.specValues[2];
        	float[] maxt = new float[3];
        	maxt[0] = info.specValues[3];
        	maxt[1] = info.specValues[4];
        	maxt[2] = info.specValues[5];
        	setFltAry("maxForce", maxf); //$NON-NLS-1$
        	setFltAry("maxTorque", maxt); //$NON-NLS-1$
        }else if(info.type.equals("Range")){ //$NON-NLS-1$
        	setFlt("scanAngle", info.specValues[0]); //$NON-NLS-1$
        	setFlt("scanStep", info.specValues[1]); //$NON-NLS-1$
        	setFlt("scanRate", info.specValues[2]); //$NON-NLS-1$
        	setFlt("maxDistance", info.specValues[3]); //$NON-NLS-1$
        	switchVisibleArea_ = SceneGraphModifier._makeSwitchNode(_createShapeOfVisibleArea());
        	tg_.addChild(switchVisibleArea_);
        }

    }

    public int compareTo(Object o) {
        if (o instanceof GrxSensorItem) {
            GrxSensorItem s = (GrxSensorItem) o;
            if (getOrder(type()) < getOrder(s.type()))
                return -1;
            else{
                if (id() < s.id())
                    return -1;
            }
        }
        return 1;
    }

    /**
     * @brief get sensor type as integer value
     * @param type sensor type
     * @return
     */
    private int getOrder(String type) {
        if (type.equals("Force")) //$NON-NLS-1$
            return 0;
        else if (type.equals("RateGyro")) //$NON-NLS-1$
            return 1;
        else if (type.equals("Acceleration")) //$NON-NLS-1$
            return 2;
        else if (type.equals("Vision")) //$NON-NLS-1$
            return 3;
        else if (type.equals("Range")) //$NON-NLS-1$
            return 4;
        else
            return -1;

    }

    /**
     * @brief get translation relative to the parent link
     * @return translation
     */
	public double[] translation() {
		return info_.translation;
	}

    /**
     * @brief get rotation relative to the parent link
     * @return rotation
     */
	public double[] rotation() {
		return info_.rotation;
	}

	/**
	 * @brief set new translation
	 * @param pos new translation(length=3)
	 * @return true if set successfully, false otherwise
	 */
	public boolean translation(double[] pos){
		if (super.translation(pos)){
			info_.translation = pos;
			return true;
		}else{
			return false;
		}
	}

	/**
	 * @brief set new rotation
	 * @param rot new rotation(axis and angle, length=4)
	 * @return true if set successfully, false otherwise
	 */
	public boolean rotation(double[] rot){
		if (super.rotation(rot)){
			info_.rotation = rot;
			return true;
		}else{
			return false;
		}
	}

	/**
	 * set id from string
	 * @param value string 
	 */
	void id(String value){
		Short id = getShort(value);
		if (id != null && info_.id != id){
			info_.id = id;
			setShort("id", id); //$NON-NLS-1$
			if (model_ != null) model_.notifyModified();
		}
	}

	/**
     * @brief check validity of new value of property and update if valid
     * @param property name of property
     * @param value value of property
     * @return true if checked(even if value is not used), false otherwise
     */
    public boolean propertyChanged(String property, String value) {
    	if (super.propertyChanged(property, value)){
    	}else if(property.equals("translation")){ //$NON-NLS-1$
    		translation(value);
    	}else if(property.equals("rotation")){ //$NON-NLS-1$
    		rotation(value);
    	}else if(property.equals("id")){ //$NON-NLS-1$
    		id(value);
    	}else if(property.equals("type")){ //$NON-NLS-1$
    		type(value);
        }else if(property.equals("cameraType")){ //$NON-NLS-1$
    		cameraType(value);
    	}else if(property.equals("alwaysVisible")){ //$NON-NLS-1$
    		if (value.startsWith("true")){ //$NON-NLS-1$
    			setProperty("alwaysVisible", "true"); //$NON-NLS-1$ //$NON-NLS-2$
    			setVisibleArea(true);
    		}else{
    			setProperty("alwaysVisible", "false"); //$NON-NLS-1$ //$NON-NLS-2$
    			setVisibleArea(false);
    		}
    	}else{
    		return false;
    	}
    	return true;
    }

    public void type(String type){
    	if (type().equals(type)) return;
    	
    	if (type.equals("Vision")){ //$NON-NLS-1$
    		if (info_.specValues == null || info_.specValues.length != 7){
    			info_.specValues = new float[7];
    			_removeSensorSpecificProperties();
    			setProperty("frontClipDistance", "0.01"); //$NON-NLS-1$ //$NON-NLS-2$
    			setProperty("backClipDistance", "10.0"); //$NON-NLS-1$ //$NON-NLS-2$
    			setProperty("fieldOfView", "0.0785398"); //$NON-NLS-1$ //$NON-NLS-2$
    			setProperty("cameraType", "COLOR"); //$NON-NLS-1$ //$NON-NLS-2$
    			setProperty("width", "320"); //$NON-NLS-1$ //$NON-NLS-2$
    			setProperty("height", "240"); //$NON-NLS-1$ //$NON-NLS-2$
    			setProperty("frameRate", "30.0"); //$NON-NLS-1$ //$NON-NLS-2$
    		}
			info_.specValues[0] = getFlt("frontClipDistance", null); //$NON-NLS-1$
			info_.specValues[1] = getFlt("backClipDistance", null); //$NON-NLS-1$
			info_.specValues[2] = getFlt("fieldOfView", null); //$NON-NLS-1$
			if (getStr("cameraType", "NONE").equals("NONE")){ //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				info_.specValues[3] = CameraType._NONE;
			}else if(getStr("cameraType", "NONE").equals("COLOR")){ //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				info_.specValues[3] = CameraType._COLOR;
			}else if(getStr("cameraType", "NONE").equals("MONO")){ //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				info_.specValues[3] = CameraType._MONO;
			}else if(getStr("cameraType", "NONE").equals("DEPTH")){ //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				info_.specValues[3] = CameraType._DEPTH;
			}else if(getStr("cameraType", "NONE").equals("COLOR_DEPTH")){ //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				info_.specValues[3] = CameraType._COLOR_DEPTH;
			}else if(getStr("cameraType", "NONE").equals("MONO_DEPTH")){ //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				info_.specValues[3] = CameraType._MONO_DEPTH;
			}else{
				setProperty("cameraType", "NONE"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			info_.specValues[4] = getInt("width", null); //$NON-NLS-1$
			info_.specValues[5] = getInt("height", null); //$NON-NLS-1$
			info_.specValues[6] = getFlt("frameRate", null); //$NON-NLS-1$
			// TODO update shape of visible area
			// TODO update camera_
    	}else if(type.equals("RateGyro")){ //$NON-NLS-1$
    		if (info_.specValues == null || info_.specValues.length != 3 || getProperty("maxAngularVelocity")==null){ //$NON-NLS-1$
    			info_.specValues = new float[]{-1.0f, -1.0f, -1.0f};
    			_removeSensorSpecificProperties();
    			setFltAry("maxAngularVelocity", info_.specValues); //$NON-NLS-1$
    		}
    		info_.specValues = getFltAry("maxAngularVelocity", null); //$NON-NLS-1$
    	}else if(type.equals("Acceleration")){ //$NON-NLS-1$
    		if (info_.specValues == null || info_.specValues.length != 3 || getProperty("maxAcceleration")==null){ //$NON-NLS-1$
    			info_.specValues = new float[]{-1.0f, -1.0f, -1.0f};
    			_removeSensorSpecificProperties();
    			setFltAry("maxAcceleration", info_.specValues); //$NON-NLS-1$
    		}
    		info_.specValues = getFltAry("maxAcceleration", null); //$NON-NLS-1$
    	}else if(type.equals("Force")){ //$NON-NLS-1$
    		if (info_.specValues == null || info_.specValues.length != 6){
    			info_.specValues = new float[]{-1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f};
    			_removeSensorSpecificProperties();
    			setFltAry("maxForce", new float[]{-1.0f, -1.0f, -1.0f}); //$NON-NLS-1$
    			setFltAry("maxTorque", new float[]{-1.0f, -1.0f, -1.0f}); //$NON-NLS-1$
    		}
    		float[] maxf = getFltAry("maxForce", null); //$NON-NLS-1$
    		float[] maxt = getFltAry("maxTorque", null); //$NON-NLS-1$
    		info_.specValues[0] = maxf[0];
    		info_.specValues[1] = maxf[1];
    		info_.specValues[2] = maxf[2];
    		info_.specValues[3] = maxt[0];
    		info_.specValues[4] = maxt[1];
    		info_.specValues[5] = maxt[2];
    	}else if(type.equals("Range")){ //$NON-NLS-1$
    		if (info_.specValues == null || info_.specValues.length != 3){
    			info_.specValues = new float[]{3.14159f, 0.1f, 10.0f, 10.0f};
    			_removeSensorSpecificProperties();
    			setFlt("scanAngle", 3.14159f); //$NON-NLS-1$
    			setFlt("scanStep", 0.1f); //$NON-NLS-1$
    			setFlt("scanRate", 10.0f); //$NON-NLS-1$
    			setFlt("maxDistance", 10.0f); //$NON-NLS-1$
    		}
    		info_.specValues[0] = getFlt("scanAngle", 3.14159f); //$NON-NLS-1$
    		info_.specValues[1] = getFlt("scanStep", 0.1f); //$NON-NLS-1$
    		info_.specValues[2] = getFlt("scanRate", 10.0f); //$NON-NLS-1$
    	}else{
    		System.out.println("GrxSensorItem.propertyChanged() : unknown sensor type : "+info_.type); //$NON-NLS-1$
    		return;
    	}
    	info_.type = type;
    	setProperty("type", type); //$NON-NLS-1$
    	if (model_ != null) model_.notifyModified();
    }
    private void _removeSensorSpecificProperties() {
		// properties of ForceSensor
		remove("maxForce"); //$NON-NLS-1$
		remove("maxTorque"); //$NON-NLS-1$
		// property of Gyro
		remove("maxAngularVelocity"); //$NON-NLS-1$
		// property of AccelerationSensor
		remove("maxAcceleration"); //$NON-NLS-1$
		// property of VisionSensor
		remove("frontClipDistance"); //$NON-NLS-1$
		remove("backClipDistance"); //$NON-NLS-1$
		remove("fieldOfView"); //$NON-NLS-1$
		remove("width"); //$NON-NLS-1$
		remove("height"); //$NON-NLS-1$
		remove("frameRate"); //$NON-NLS-1$
		remove("cameraType"); //$NON-NLS-1$
		// property of RangeSensor
		remove("scanAngle"); //$NON-NLS-1$
		remove("scanStep"); //$NON-NLS-1$
		remove("scanRate"); //$NON-NLS-1$
		remove("maxDistance"); //$NON-NLS-1$
	}

	/**
     * @brief Override clone method
     * @return GrxSensorItem
     */
	public GrxSensorItem clone(){
		GrxSensorItem ret = (GrxSensorItem)super.clone();
/*    	
	Deep copy suspension list
*/
		
		return ret;
	}

	/**
	 * @brief convert array of distance into array of 3D point
	 * @param distances array of distance
	 * @return array of 3D point
	 */
	private Point3f[] _distances2points(double[] distances){
    	if (info_.type.equals("Range")){ //$NON-NLS-1$
    		float step = info_.specValues[1];
    		int half = distances.length/2;
    		Point3f[] p3f = new Point3f[half*2+1+1];
    		p3f[0] = new Point3f(0,0,0);
    		for (int i=-half; i<=half; i++){
    			double angle = step*i;
    			p3f[i+half+1] = new Point3f(
    					(float)(-distances[i+half]*Math.sin(angle)),
    					0.0f,
    					(float)(-distances[i+half]*Math.cos(angle)));
    		}
    		return p3f;
    	}else{
    		return null;
    	}
	}
	/**
	 * @brief update shape of visible area(only used for RangeSensor)
	 * @param distances array of distances
	 */
	public void updateShapeOfVisibleArea(double[] distances){
    	if (info_.type.equals("Range")){ //$NON-NLS-1$
    		Point3f[] p3f = _distances2points(distances);
    		if (p3f == null) return;
        	Shape3D shapeNode = (Shape3D)switchVisibleArea_.getChild(0);
        	Geometry gm = (Geometry)shapeNode.getGeometry(0);
        	if (gm instanceof TriangleFanArray){
        		TriangleFanArray tri = (TriangleFanArray)gm;
        		tri.setCoordinates(0, p3f);
        	}
    	}
	}
	/**
	 * @brief create shape of visible area
	 * @return shape 
	 */
    private Shape3D _createShapeOfVisibleArea() {
    	if (info_.type.equals("Range")){ //$NON-NLS-1$
    		double scanAngle = info_.specValues[0];
    		double step = info_.specValues[1];
    		float d = info_.specValues[3];
    		int half = (int)(scanAngle/2/step);
    		Point3f[] p3f = new Point3f[half*2+1+1];
    		p3f[0] = new Point3f(0,0,0);
    		for (int i=-half; i<=half; i++){
    			double angle = step*i;
    			p3f[i+half+1] = new Point3f(
    					(float)(-d*Math.sin(angle)),
    					0.0f,
    					(float)(-d*Math.cos(angle)));
    		}
    		int[] stripVertexCounts = { p3f.length };
    		TriangleFanArray tri = new TriangleFanArray(p3f.length,
    				TriangleFanArray.COORDINATES,
    				stripVertexCounts);
            tri.setCapability(QuadArray.ALLOW_COORDINATE_READ);
            tri.setCapability(QuadArray.ALLOW_COORDINATE_WRITE);
    		tri.setCoordinates(0, p3f);
    		javax.media.j3d.Appearance app  = new javax.media.j3d.Appearance();
    		app.setTransparencyAttributes(
    				new TransparencyAttributes(TransparencyAttributes.FASTEST, 0.5f)
    		);
    		Shape3D s3d = new Shape3D(tri, app);
            s3d.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
            s3d.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
    		return s3d;
    	}
    	if (camera_ == null) return null;
    	
    	CameraParameter prm = camera_.getCameraParameter();
        //box
        float enlarge = 0.001f; //[m] prevent this box is rendered
        float f = (float)prm.backClipDistance*(1.0f+enlarge);
        float n = (float)prm.frontClipDistance*(1.0f-enlarge);
        double theta = prm.fieldOfView;
        float aspect = ((float)prm.height)/prm.width;
        float nx = (float)Math.tan(theta/2)*n;
        float ny = nx*aspect;
        float fx = (float)Math.tan(theta/2)*f;
        float fy = fx*aspect;

        Point3f[] p3f = {
          new Point3f(nx,ny,-n),
          new Point3f(-nx,ny,-n),
          new Point3f(-nx,-ny,-n),
          new Point3f(nx,-ny,-n),
          new Point3f(fx,fy,-f),
          new Point3f(-fx,fy,-f),
          new Point3f(-fx,-fy,-f),
          new Point3f(fx,-fy,-f),
        };

        int vertIndices[] = {0,1,2,0,2,3,1,0,4,1,4,5,0,3,7,0,7,4,5,2,1,5,6,2,6,7,3,6,3,2,5,4,7,5,7,6};
        IndexedTriangleArray tri = 
          new IndexedTriangleArray(p3f.length, 
                                   IndexedTriangleArray.COORDINATES,
                                   vertIndices.length);
          
        tri.setCoordinates(0, p3f);
        tri.setCoordinateIndices(0,vertIndices);
        javax.media.j3d.Appearance app  = new javax.media.j3d.Appearance();
        app.setTransparencyAttributes(
            new TransparencyAttributes(TransparencyAttributes.FASTEST, 0.5f)
        );
        Shape3D s3d = new Shape3D(tri, app);
        return s3d;
    } 
    
	/**
	 * @brief make visible area visible/invisible
	 * @param b true to make visible, false otherwise
	 */
    public void setVisibleArea(boolean b) {
    	if (switchVisibleArea_ != null){
            switchVisibleArea_.setWhichChild(b? Switch.CHILD_ALL:Switch.CHILD_NONE);
    	}
    }
    
    public boolean isVisible(){
    	return switchVisibleArea_ != null && switchVisibleArea_.getWhichChild() == Switch.CHILD_ALL;
    }
    
    /**
     * @brief set/unset fucus on this item
     * 
     * When this item is focused, some geometries are displayed
     * @param b true to fucus, false to unfocus
     */
    public void setFocused(boolean b){
    	super.setFocused(b);
    	if (isFalse("alwaysVisible")) setVisibleArea(b); //$NON-NLS-1$
    }
    
    public boolean isCamera(){
    	if(camera_ != null)
    		return true;
    	else
    		return false;
    }
    
    public Camera_impl getCamera(){
    	if(isCamera())
    		return camera_;
    	else
    		return null;
    }

    private void cameraType(String cameraType){
        String old = getStr("cameraType", "NONE");
        if(!type().equals("Vision") || old.equals(cameraType)) return;

        if(cameraType.equals("NONE")){ //$NON-NLS-1$
            info_.specValues[3] = CameraType._NONE;
        }else if(cameraType.equals("COLOR")){ //$NON-NLS-1$
            info_.specValues[3] = CameraType._COLOR;
        }else if(cameraType.equals("MONO")){ //$NON-NLS-1$
            info_.specValues[3] = CameraType._MONO;
        }else if(cameraType.equals("DEPTH")){ //$NON-NLS-1$
            info_.specValues[3] = CameraType._DEPTH;
        }else if(cameraType.equals("COLOR_DEPTH")){ //$NON-NLS-1$
            info_.specValues[3] = CameraType._COLOR_DEPTH;
        }else if(cameraType.equals("MONO_DEPTH")){ //$NON-NLS-1$
            info_.specValues[3] = CameraType._MONO_DEPTH;
        }else{
            System.out.println("GrxSensorItem.propertyChanged() : unknown camera type : " + cameraType); //$NON-NLS-1$
            return;
        }
        setProperty("cameraType", cameraType); //$NON-NLS-1$
        if (model_ != null) model_.notifyModified();
    }
}
