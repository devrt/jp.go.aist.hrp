/*
 * Copyright (c) 2008, AIST, the University of Tokyo and General Robotix Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * National Institute of Advanced Industrial Science and Technology (AIST)
 */
/**
   \file
   \author Shin'ichiro Nakaoka
*/

#ifndef HRPMODEL_LINK_H_INCLUDED
#define HRPMODEL_LINK_H_INCLUDED

#include <string>
#include <ostream>
#include <vector>
#include <boost/intrusive_ptr.hpp>
#include <hrpUtil/Tvmet3d.h>
#include "exportdef.h"

namespace hrp {
    class Link;
}

HRPMODEL_API std::ostream& operator<<(std::ostream &out, hrp::Link& link);

namespace hrp {

    class Body;

    class ColdetModel;
    typedef boost::intrusive_ptr<ColdetModel> ColdetModelPtr;

    class HRPMODEL_API Link {

      public:

        Link();
        Link(const Link& link);
        ~Link();

        void addChild(Link* link);
        bool detachChild(Link* link);
        bool isRoot() { return !parent; }

        void setAttitude(const Matrix33& R) { this->R = R * trans(Rs); }
        Matrix33 attitude() { return Matrix33(this->R * Rs); }

        /**
           @deprecated use setAttitude().
        */
        void setSegmentAttitude(const Matrix33& R) { this->R = R * trans(Rs); }

        /**
           @deprecated use attitude().
        */
        Matrix33 segmentAttitude() { return Matrix33(this->R * Rs); }

        Body* body;

        int index; 
        int jointId;  ///< jointId value written in a model file
		
        std::string name;

        enum JointType {
            FREE_JOINT,   /// 6-DOF root link
            FIXED_JOINT,        ///< fixed joint(0 dof)
            ROTATIONAL_JOINT,	///< rotational joint (1 dof)
            SLIDE_JOINT	///< translational joint (1 dof)
        };
		
        JointType jointType;

        Link* parent;
        Link* sibling;
        Link* child;

        Vector3	p;      ///< position

        /**
           Internal world attitude.
           In the model computation, it corresponds to the identity matrix
           when all the joint angles of a robot are zero so that multiplication of
           local attitdue matrices can be omitted to simplify the computation.
           If you want to use the original coordinate in the model file,
           use setSegmentAttitude() and segmentAttitude() to access R.
        */
        Matrix33 R;

        Vector3	v;      ///< linear velocity 
        Vector3	w;      ///< angular velocity, omega
        Vector3	dv;     ///< linear acceleration
        Vector3	dw;     ///< derivative of omega

        double q;       ///< joint value
        double dq;      ///< joint velocity
        double ddq;     ///< joint acceleration
        double u;       ///< joint torque

        Vector3 a;      ///< rotational joint axis (self local)
        Vector3 d;      ///< translation joint axis (self local)
        Vector3 b;      ///< relative position (parent local)

        Matrix33 Rs;    ///< relative attitude of the link segment (self local)

        double m;       ///< mass
        Matrix33 I;     ///< inertia tensor (self local, around c)
        Vector3 c;      ///< center of mass (self local)
        Vector3 wc;     ///< R * c + p
        
        Vector3 vo;     ///< translation elements of spacial velocity
        Vector3 dvo;    ///< derivative of vo

        /** A unit vector of angular velocity (the world coordinate) generated by the joint 
            The value is parent->R * a when the joint is the rotational type. */
        Vector3 sw;
		
        /** A unit vector of spatial velocity (the world coordinate) generated by the joint.
            The value is parent->R * d when the joint is the translation type. */
        Vector3 sv;
		
        Vector3 cv;     ///< dsv * dq (cross velocity term)
        Vector3 cw;     ///< dsw * dq (cross velocity term)

        Vector3 fext;   ///< external force 
        Vector3 tauext; ///< external torque (around the world origin)

        // needed ?
        //Vector3			f;      ///< force from the parent link 
        //Vector3			tau;    ///< torque from the parent link
		
        Matrix33 Iww;   ///< bottm right block of the articulated inertia
        Matrix33 Iwv;   ///< bottom left block (transpose of top right block) of the articulated inertia
        Matrix33 Ivv;   ///< top left block of the articulated inertia
        Vector3 pf;     ///< bias force (linear element)
        Vector3 ptau;   ///< bias force (torque element)
        Vector3 hhv;
        Vector3 hhw;
        double uu;
        double dd;
		
        double Jm2;    ///< Equivalent rotor inertia: n^2*Jm [kg.m^2]

        double ulimit;  ///< the upper limit of joint values
        double llimit;  ///< the lower limit of joint values
        double uvlimit;  ///< the upper limit of joint velocities
        double lvlimit; ///< the lower limit of joint velocities

        double defaultJointValue;
        double torqueConst;
        double encoderPulse;
        double Ir;      ///< rotor inertia [kg.m^2]
        double gearRatio;
        double gearEfficiency;
        double rotorResistor;

        bool isHighGainMode;

        ColdetModelPtr coldetModel;

        struct ConstraintForce {
            Vector3 point;
            Vector3 force;
        };

        typedef std::vector<ConstraintForce> ConstraintForceArray;
        ConstraintForceArray constraintForces;

      private:

        Link& operator=(const Link& link); // no implementation is given to disable the copy operator

        void setBodyIter(Body* body);
        
        friend std::ostream& ::operator<<(std::ostream &out, hrp::Link& link);
        void putInformation(std::ostream& out); // for the iostream output
    };

};
	

#endif
