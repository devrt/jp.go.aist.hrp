
set(program openhrp-controller-bridge)

set(sources 
  BridgeConf.cpp
  VirtualRobotPortHandler.cpp
  VirtualRobotRTC.cpp
  Controller_impl.cpp
  main.cpp)

add_executable(${program} ${sources})

if( NOT ADDITIONAL_SYMBOL STREQUAL "")
  add_definitions(-D${ADDITIONAL_SYMBOL})
endif()
if( OPENRTM_VERSION STREQUAL OPENRTM_VERSION110 )
  add_definitions(-DOPENRTM_VERISON110)
endif()

if(UNIX)
  target_link_libraries(
    ${program}
    hrpCorbaStubSkel-${OPENHRP_LIBRARY_VERSION}
    hrpUtil-${OPENHRP_LIBRARY_VERSION}
    ${OPENRTM_LIBRARIES}
    boost_filesystem-mt boost_program_options-mt boost_regex-mt
    boost_system-mt
    )
elseif(WIN32)
  add_definitions(${OPENRTM_DEFINITIONS})
  set_target_properties(${program} PROPERTIES DEBUG_POSTFIX d )
  target_link_libraries(${program}
    optimized hrpCorbaStubSkel-${OPENHRP_LIBRARY_VERSION}
    optimized hrpUtil-${OPENHRP_LIBRARY_VERSION}
    debug hrpCorbaStubSkel-${OPENHRP_LIBRARY_VERSION}d 
    debug hrpUtil-${OPENHRP_LIBRARY_VERSION}d
    ${OPENRTM_LIBRARIES})
endif()

if(WIN32)
  install(TARGETS ${program} DESTINATION ${PROJECT_BINARY_DIR}/bin CONFIGURATIONS Release )
endif()

install(TARGETS ${program} DESTINATION bin CONFIGURATIONS Release Debug)
