IF(CMAKE_CXX_COMPILER_ID AND CMAKE_CXX_COMPILER_ID STREQUAL GNU) # GCC
  EXECUTE_PROCESS(COMMAND ${CMAKE_CXX_COMPILER} --version
                  OUTPUT_VARIABLE CMAKE_HRP_GCC_VERSION_FULL
                  OUTPUT_STRIP_TRAILING_WHITESPACE)

  STRING(REGEX MATCH "[ \t\n][0-9].[0-9].[0-9][ \t\n]" CMAKE_GCC_REGEX_VERSION "${CMAKE_HRP_GCC_VERSION_FULL}")

  # Split the three parts:
  STRING(REGEX MATCHALL "[0-9]" CMAKE_HRP_GCC_VERSIONS "${CMAKE_GCC_REGEX_VERSION}")

  LIST(GET CMAKE_HRP_GCC_VERSIONS 0 CMAKE_HRP_GCC_VERSION_MAJOR)
  LIST(GET CMAKE_HRP_GCC_VERSIONS 1 CMAKE_HRP_GCC_VERSION_MINOR)
    
  set(CMAKE_HRP_GCC_VERSION ${CMAKE_HRP_GCC_VERSION_MAJOR}${CMAKE_HRP_GCC_VERSION_MINOR})
  MATH(EXPR CMAKE_HRP_GCC_VERSION_NUM "${CMAKE_HRP_GCC_VERSION_MAJOR}*100 + ${CMAKE_HRP_GCC_VERSION_MINOR}")
ENDIF(CMAKE_CXX_COMPILER_ID AND CMAKE_CXX_COMPILER_ID STREQUAL GNU)

IF(CMAKE_SYSTEM_NAME MATCHES "Linux")
  EXEC_PROGRAM(cat ARGS "/proc/cpuinfo" OUTPUT_VARIABLE CPUINFO)

  STRING(REGEX REPLACE ".*\nflags[ \t]*:[^\n]*[ \t]+(sse)[ \t]+.*" "\\1" SSE_THERE ${CPUINFO})
  STRING(COMPARE EQUAL "sse" "${SSE_THERE}" SSE_TRUE)
  IF (SSE_TRUE)
    set(OPTIMIZE_SSE_ENABLE true CACHE BOOL "SSE available on host")
  ELSE (SSE_TRUE)
    set(OPTIMIZE_SSE_ENABLE false CACHE BOOL "SSE available on host")
  ENDIF (SSE_TRUE)

  STRING(REGEX REPLACE ".*\nflags[ \t]*:[^\n]*[ \t]+(sse2)[ \t]+.*" "\\1" SSE_THERE ${CPUINFO})
  STRING(COMPARE EQUAL "sse2" "${SSE_THERE}" SSE2_TRUE)
  IF (SSE2_TRUE)
    set(OPTIMIZE_SSE2_ENABLE true CACHE BOOL "SSE2 available on host")
  ELSE (SSE2_TRUE)
    set(OPTIMIZE_SSE2_ENABLE false CACHE BOOL "SSE2 available on host")
  ENDIF (SSE2_TRUE)

  STRING(REGEX REPLACE ".*\nflags[ \t]*:[^\n]*[ \t]+(sse3)[ \t]+.*" "\\1" SSE_THERE ${CPUINFO})
  STRING(COMPARE EQUAL "sse3" "${SSE_THERE}" SSE3_TRUE)
  STRING(REGEX REPLACE ".*\nflags[ \t]*:[^\n]*[ \t]+(ssse3)[ \t]+.*" "\\1" SSE_THERE ${CPUINFO})
  STRING(COMPARE EQUAL "ssse3" "${SSE_THERE}" SSSE3_TRUE)
  IF (SSE3_TRUE OR SSSE3_TRUE)
    set(OPTIMIZE_SSE3_ENABLE true CACHE BOOL "SSE3 available on host")
  ELSE (SSE3_TRUE OR SSSE3_TRUE)
    set(OPTIMIZE_SSE3_ENABLE false CACHE BOOL "SSE3 available on host")
  ENDIF (SSE3_TRUE OR SSSE3_TRUE)

  IF(${CMAKE_HRP_GCC_VERSION_NUM} GREATER 402)
    IF (SSSE3_TRUE)
      set(OPTIMIZE_SSSE3_ENABLE true CACHE BOOL "SSSE3 available on host")
    ELSE (SSSE3_TRUE)
      set(OPTIMIZE_SSSE3_ENABLE false CACHE BOOL "SSSE3 available on host")
    ENDIF (SSSE3_TRUE)

    STRING(REGEX REPLACE ".*\nflags[ \t]*:[^\n]*[ \t]+(sse4_1)[ \t]+.*" "\\1" SSE_THERE ${CPUINFO})
    STRING(COMPARE EQUAL "sse4_1" "${SSE_THERE}" SSE41_TRUE)
    IF (SSE41_TRUE)
      set(OPTIMIZE_SSE41_ENABLE true CACHE BOOL "SSE4.1 available on host")
    ELSE (SSE41_TRUE)
      set(OPTIMIZE_SSE41_ENABLE false CACHE BOOL "SSE4.1 available on host")
    ENDIF (SSE41_TRUE)
  ENDIF(${CMAKE_HRP_GCC_VERSION_NUM} GREATER 402)

   mark_as_advanced(OPTIMIZE_SSE_ENABLE OPTIMIZE_SSE2_ENABLE OPTIMIZE_SSE3_ENABLE OPTIMIZE_SSSE3_ENABLE OPTIMIZE_SSE41_ENABLE)

#  STRING(REGEX REPLACE ".*\nflags[ \t]*:[^\n]*[ \t]+(sse4_2)[ \t]+.*" "\\1" SSE_THERE ${CPUINFO})
#  STRING(COMPARE EQUAL "sse4_2" "${SSE_THERE}" SSE42_TRUE)
#  IF (SSE42_TRUE)
#    set(OPTIMIZE_SSE42_ENABLE true CACHE BOOL "SSE4.2 available on host")
#  ELSE (SSE41_TRUE)
#    set(OPTIMIZE_SSE42_ENABLE false CACHE BOOL "SSE4.2 available on host")
#  ENDIF (SSE42_TRUE)
#  mark_as_advanced(OPTIMIZE_SSE_ENABLE OPTIMIZE_SSE2_ENABLE OPTIMIZE_SSE3_ENABLE OPTIMIZE_SSSE3_ENABLE OPTIMIZE_SSE41_ENABLE OPTIMIZE_SSE42_ENABLE)

ELSEIF(CMAKE_SYSTEM_NAME STREQUAL "Windows")
  # TODO
  set(OPTIMIZE_SSE2_ENABLE  true    CACHE BOOL "SSE2 available on host")
ELSE(CMAKE_SYSTEM_NAME MATCHES "Linux")
  set(OPTIMIZE_SSE_ENABLE    true   CACHE BOOL "SSE available on host")
  set(OPTIMIZE_SSE2_ENABLE   true   CACHE BOOL "SSE2 available on host")
  set(OPTIMIZE_SSE3_ENABLE   false  CACHE BOOL "SSE3 available on host")
  set(OPTIMIZE_SSSE3_ENABLE  false  CACHE BOOL "SSSE3 available on host")
  set(OPTIMIZE_SSE41_ENABLE  false  CACHE BOOL "SSE4.1 available on host")
  set(OPTIMIZE_SSE42_ENABLE  false  CACHE BOOL "SSE4.1 available on host")
ENDIF(CMAKE_SYSTEM_NAME MATCHES "Linux")

IF(NOT OPTIMIZE_SSE2_ENABLE)
  MESSAGE(STATUS "Could not find hardware support for SSE2 on this machine.")
ENDIF(NOT OPTIMIZE_SSE2_ENABLE)

set(EXTRA_CXX_FLAGS_RELEASE "")
IF(CMAKE_CXX_COMPILER_ID AND CMAKE_CXX_COMPILER_ID STREQUAL GNU) # gcc
  IF(OPTIMIZE_SSE_ENABLE)
    set(EXTRA_CXX_FLAGS_RELEASE "${EXTRA_CXX_FLAGS_RELEASE} -mfpmath=sse -msse")
  ENDIF()
  IF(OPTIMIZE_SSE2_ENABLE)
    set(EXTRA_CXX_FLAGS_RELEASE "${EXTRA_CXX_FLAGS_RELEASE} -msse2")
  ENDIF()
  IF(OPTIMIZE_SSE3_ENABLE)
    set(EXTRA_CXX_FLAGS_RELEASE "${EXTRA_CXX_FLAGS_RELEASE} -msse3")
  ENDIF()
  
  IF(${CMAKE_HRP_GCC_VERSION_NUM} GREATER 402)
    IF(OPTIMIZE_SSSE3_ENABLE)
      set(EXTRA_CXX_FLAGS_RELEASE "${EXTRA_CXX_FLAGS_RELEASE} -mssse3")
    ENDIF()
    IF(OPTIMIZE_SSE41_ENABLE)
      set(EXTRA_CXX_FLAGS_RELEASE "${EXTRA_CXX_FLAGS_RELEASE} -msse4.1")
    ENDIF()
#    IF(OPTIMIZE_SSE42_ENABLE)
#      set(EXTRA_CXX_FLAGS_RELEASE "${EXTRA_CXX_FLAGS_RELEASE} -msse4.2")
#    ENDIF()
  ENDIF(${CMAKE_HRP_GCC_VERSION_NUM} GREATER 402)
ELSEIF(CMAKE_C_COMPILER MATCHES "cl(.exe)?$") # MSVC
  IF(OPTIMIZE_SSE2_ENABLE)
    IF(NOT CMAKE_CL_64)
      set(EXTRA_CXX_FLAGS_RELEASE "${EXTRA_CXX_FLAGS_RELEASE} /arch:SSE2")
    ENDIF(NOT CMAKE_CL_64)
  ENDIF()
ENDIF(CMAKE_CXX_COMPILER_ID AND CMAKE_CXX_COMPILER_ID STREQUAL GNU)

