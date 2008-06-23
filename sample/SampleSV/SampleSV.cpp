// -*- mode: c++; indent-tabs-mode: t; tab-width: 4; c-basic-offset: 4; -*-
/*!
 * @file  SampleSV.cpp
 * @brief Sample SV component
 * $Date$
 *
 * $Id$
 */

#include "SampleSV.h"

#include <iostream>

#define DOF (4)
#define STEERING_ID 0
#define WHEEL_ID 1

#define STEERING_FILE "etc/steer.dat"

#define STEERING_P_GAIN 100.0
#define STEERING_D_GAIN 1.0
#define WHEEL_P_GAIN 100.0
#define WHEEL_D_GAIN 0.5
#define WHEEL_REF_VEL 6.28  // [rad/s]

#define TIMESTEP 0.002

std::ofstream logfile("sv.log");

namespace {
  const bool CONTROLLER_BRIDGE_DEBUG = false;
}


// Module specification
// <rtc-template block="module_spec">
static const char* samplepd_spec[] =
  {
    "implementation_id", "SampleSV",
    "type_name",         "SampleSV",
    "description",       "Sample SV component",
    "version",           "0.1",
    "vendor",            "AIST",
    "category",          "Generic",
    "activity_type",     "DataFlowComponent",
    "max_instance",      "10",
    "language",          "C++",
    "lang_type",         "compile",
    // Configuration variables

    ""
  };
// </rtc-template>

SampleSV::SampleSV(RTC::Manager* manager)
  : RTC::DataFlowComponentBase(manager),
    // <rtc-template block="initializer">
    m_steerIn("steer", m_steer),
    m_velIn("vel", m_vel),
    m_torqueOut("torque", m_torque),
    
    // </rtc-template>
    wheel_ref(0.0)
{
  if( CONTROLLER_BRIDGE_DEBUG )
  {
    std::cout << "SampleSV::SampleSV" << std::endl;
  }
  // Registration: InPort/OutPort/Service
  // <rtc-template block="registration">
  // Set InPort buffers
  registerInPort("steer", m_steerIn);
  registerInPort("vel", m_velIn);

  // Set OutPort buffer
  registerOutPort("torque", m_torqueOut);
  // Set service provider to Ports
  
  // Set service consumers to Ports
  
  // Set CORBA Service Ports
  
  // </rtc-template>


	if (access( STEERING_FILE, 0))
    std::cerr << STEERING_FILE <<" not found" << std::endl;
	else
		steer.open( STEERING_FILE );

  m_torque.data.length(DOF);
  m_steer.data.length(2);
  m_vel.data.length(2);
}

SampleSV::~SampleSV()
{
	if (steer.is_open()) steer.close();
}


RTC::ReturnCode_t SampleSV::onInitialize()
{
  // <rtc-template block="bind_config">
  // Bind variables and configuration variable
  if( CONTROLLER_BRIDGE_DEBUG )
  {
    std::cout << "onInitialize" << std::endl;
  }

  // </rtc-template>
  return RTC::RTC_OK;
}



/*
RTC::ReturnCode_t SampleSV::onFinalize()
{
  return RTC::RTC_OK;
}
*/

/*
RTC::ReturnCode_t SampleSV::onStartup(RTC::UniqueId ec_id)
{
  return RTC::RTC_OK;
}
*/

/*
RTC::ReturnCode_t SampleSV::onShutdown(RTC::UniqueId ec_id)
{
  return RTC::RTC_OK;
}
*/

RTC::ReturnCode_t SampleSV::onActivated(RTC::UniqueId ec_id)
{

	std::cout << "on Activated" << std::endl;
	steer.seekg(0);
	
	return RTC::RTC_OK;
}

/*
RTC::ReturnCode_t SampleSV::onDeactivated(RTC::UniqueId ec_id)
{
  return RTC::RTC_OK;
}
*/


RTC::ReturnCode_t SampleSV::onExecute(RTC::UniqueId ec_id)
{
  if( CONTROLLER_BRIDGE_DEBUG )
  {
    std::cout << "SampleSV::onExecute" << std::endl;
  }

  // ���̊֐��̐U�镑����Controller_impl::control�̔h���扼�z�֐��ɑΉ�����
  m_steerIn.update();
  m_velIn.update();

  double q_ref, dq_ref;
  double dummy;
  steer >> dummy; // skip time
  int i;

  //�t�@�C������f�[�^����s�ǂݍ���Ń|�[�g�ɗ���
  double steer_ref;
  steer >> steer_ref;

  //m_torque�̃f�[�^���[���Ƀ��Z�b�g
  for(int i=0; i<DOF; i++) m_torque.data[i] = 0.0;
  
  m_torque.data[STEERING_ID] = (steer_ref - m_steer.data[STEERING_ID]) * STEERING_P_GAIN - m_vel.data[STEERING_ID] * STEERING_D_GAIN;
	m_torque.data[WHEEL_ID] = (wheel_ref - m_steer.data[WHEEL_ID]) * WHEEL_P_GAIN + (WHEEL_REF_VEL - m_vel.data[WHEEL_ID]) * WHEEL_D_GAIN;
	wheel_ref += WHEEL_REF_VEL * TIMESTEP;


  logfile << "--" << std::endl;
	logfile << "steer: ref=" << steer_ref << ", cur=" << m_steer.data[STEERING_ID] << ", u=" << m_torque.data[STEERING_ID] << std::endl;
	logfile << "wheel: ref=" << wheel_ref << ", cur=" << m_steer.data[WHEEL_ID] << ", u=" << m_torque.data[WHEEL_ID] << std::endl;

  m_torqueOut.write();
  
  return RTC::RTC_OK;
}


/*
  RTC::ReturnCode_t SampleSV::onAborting(RTC::UniqueId ec_id)
  {
  return RTC::RTC_OK;
  }
*/

/*
  RTC::ReturnCode_t SampleSV::onError(RTC::UniqueId ec_id)
  {
  return RTC::RTC_OK;
  }
*/

/*
  RTC::ReturnCode_t SampleSV::onReset(RTC::UniqueId ec_id)
  {
  return RTC::RTC_OK;
  }
*/

/*
  RTC::ReturnCode_t SampleSV::onStateUpdate(RTC::UniqueId ec_id)
  {
  return RTC::RTC_OK;
  }
*/

/*
  RTC::ReturnCode_t SampleSV::onRateChanged(RTC::UniqueId ec_id)
  {
  return RTC::RTC_OK;
  }
*/



extern "C"
{

	DllExport void SampleSVInit(RTC::Manager* manager)
	{
		RTC::Properties profile(samplepd_spec);
		manager->registerFactory(profile,
								 RTC::Create<SampleSV>,
								 RTC::Delete<SampleSV>);
	}

};

