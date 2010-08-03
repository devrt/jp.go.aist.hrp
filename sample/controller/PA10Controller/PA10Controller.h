// -*- mode: c++; indent-tabs-mode: t; tab-width: 4; c-basic-offset: 4; -*-
/*
 * Copyright (c) 2008, AIST, the University of Tokyo and General Robotix Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * National Institute of Advanced Industrial Science and Technology (AIST)
 * General Robotix Inc. 
 */
/*!
 * @file  PA10Controller.h
 * @brief Sample PD component
 * @date  $Date$
 *
 * $Id$
 */

#ifndef PA10Controller_H
#define PA10Controller_H

#define DOF (9)

#include <rtm/Manager.h>
#include <rtm/DataFlowComponentBase.h>
#include <rtm/CorbaPort.h>
#include <rtm/DataInPort.h>
#include <rtm/DataOutPort.h>
#include <rtm/idl/BasicDataTypeSkel.h>

#include <vector>
#include <string>
#include <time.h>
#include <sstream>

#include <hrpModel/ModelLoaderUtil.h>
#include <hrpModel/Body.h>
#include <hrpUtil/Tvmet3d.h>
#include <hrpUtil/uBlasCommonTypes.h>
#include <hrpUtil/MatrixSolvers.h>
#include <hrpModel/Link.h>
#include <hrpModel/Sensor.h>
#include <hrpModel/JointPath.h>
#include <hrpCorba/DynamicsSimulator.hh>
#define vector3 Vector3
#define matrix33 Matrix33 

// Service implementation headers
// <rtc-template block="service_impl_h">

// </rtc-template>

// Service Consumer stub headers
// <rtc-template block="consumer_stub_h">

// </rtc-template>

using namespace std;
using namespace hrp;
using namespace RTC;

class PA10Controller
  : public RTC::DataFlowComponentBase
{
 public:
  PA10Controller(RTC::Manager* manager);
  ~PA10Controller();

  // The initialize action (on CREATED->ALIVE transition)
  // formaer rtc_init_entry() 
 virtual RTC::ReturnCode_t onInitialize();

  // The finalize action (on ALIVE->END transition)
  // formaer rtc_exiting_entry()
  // virtual RTC::ReturnCode_t onFinalize();

  // The startup action when ExecutionContext startup
  // former rtc_starting_entry()
  // virtual RTC::ReturnCode_t onStartup(RTC::UniqueId ec_id);

  // The shutdown action when ExecutionContext stop
  // former rtc_stopping_entry()
  // virtual RTC::ReturnCode_t onShutdown(RTC::UniqueId ec_id);

  // The activated action (Active state entry action)
  // former rtc_active_entry()
  virtual RTC::ReturnCode_t onActivated(RTC::UniqueId ec_id);

  // The deactivated action (Active state exit action)
  // former rtc_active_exit()
  virtual RTC::ReturnCode_t onDeactivated(RTC::UniqueId ec_id);

  // The execution action that is invoked periodically
  // former rtc_active_do()
  virtual RTC::ReturnCode_t onExecute(RTC::UniqueId ec_id);

  // The aborting action when main logic error occurred.
  // former rtc_aborting_entry()
  // virtual RTC::ReturnCode_t onAborting(RTC::UniqueId ec_id);

  // The error action in ERROR state
  // former rtc_error_do()
  // virtual RTC::ReturnCode_t onError(RTC::UniqueId ec_id);

  // The reset action that is invoked resetting
  // This is same but different the former rtc_init_entry()
  // virtual RTC::ReturnCode_t onReset(RTC::UniqueId ec_id);
  
  // The state update action that is invoked after onExecute() action
  // no corresponding operation exists in OpenRTm-aist-0.2.0
  // virtual RTC::ReturnCode_t onStateUpdate(RTC::UniqueId ec_id);

  // The action that is invoked when execution context's rate is changed
  // no corresponding operation exists in OpenRTm-aist-0.2.0
  // virtual RTC::ReturnCode_t onRateChanged(RTC::UniqueId ec_id);

 protected:
  // Configuration variable declaration
  // <rtc-template block="config_declare">
  
  // </rtc-template>

  // DataInPort declaration
  // <rtc-template block="inport_declare">
  TimedDoubleSeq m_angle;
  InPort<TimedDoubleSeq> m_angleIn;

  TimedDoubleSeq m_wristForce;
  InPort<TimedDoubleSeq> m_wristForceIn;

  // </rtc-template>

  // DataOutPort declaration
  // <rtc-template block="outport_declare">
  TimedDoubleSeq m_torque;
  OutPort<TimedDoubleSeq> m_torqueOut;
  
  // </rtc-template>

  // CORBA Port declaration
  // <rtc-template block="corbaport_declare">
  
  // </rtc-template>

  // Service declaration
  // <rtc-template block="service_declare">
  
  // </rtc-template>

  // Consumer declaration
  // <rtc-template block="consumer_declare">
  
  // </rtc-template>

 private:
  int dummy;
  std::ifstream angle, vel, gain, jac, cmp;
  std::ofstream res;
  double *Pgain;
  double *Dgain;
  std::vector<double> qold;
  double q_ref[DOF], dq_ref[DOF];
  void openFiles();
  void closeFiles();

  BodyPtr co;
  Link *wrist, *base, *lhand, *rhand;
  JointPathPtr arm_path, fing_path[2];
  dmatrix Jac, Kp, Kd, Gp, Gd;
  dvector off;
  int total_dof;

  double cur_time;
  vector<double> ex_time, x_pos, y_pos, z_pos, roll_angle, pitch_angle, yaw_angle, r_hand, l_hand;
  vector3 wrist_p_org, wrist_r_org;
  double rhand_org, lhand_org, hando[2], angle_o[7];

  void setRobot(BodyPtr _body);
  void setRobot();
  bool moveRobot();
  void calcGravityCompensation(dvector& mg);
};

extern "C"
{
DLL_EXPORT void PA10ControllerInit(RTC::Manager* manager);
};

#endif // PA10Controller_H
