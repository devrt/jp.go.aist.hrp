if( DEBIANPACKAGE_DEVELOP )
# openhrp-aist-dev edition dependensies
  set(DEPENDENT_BASENAMES
    sun-java6-jdk
    sun-java6-fonts
    jython
    libreadline-java
    java-common
    g++
    gfortran-4.3
    libcos4-dev 
    libomnievents-dev 
    libomniorb4-dev
    libomnithread3-dev
    libomnithread3c2
    omnievents
    omniidl4
    omniidl4-python
    omniorb4
    omniorb4-idl
    omniorb4-nameserver
    python-omniorb2
    python-tk
#    tvmet1.7.1-1
    liblapack3gf
    liblapack-dev
    libatlas-base-dev
    libatlas-headers
    libblas3gf
    libblas-dev
    f2c
    libf2c2
    libf2c2-dev 
    libg2c0
    libgfortran3
    libgfortran3-dbg
    libboost-dev
    libboost-filesystem-dev 
    libboost-program-options-dev 
    libboost-regex-dev
    libboost-signals-dev
    libboost-thread-dev
    libc6-dev
    zlib1g-dev
    libace-dev
    libjpeg62-dev
    libpng12-dev
    uuid-dev
    openrtm-aist
    openrtm-aist-dev
    openrtm-aist-doc
    openrtm-aist-example
    python-yaml
  )
else()
# openhrp-aist edition dependensies
  set(DEPENDENT_BASENAMES
    sun-java6-jre
    sun-java6-fonts
    jython
    libreadline-java
    java-common
    libcos4-1
    libomnievents2
    libomniorb4-1
    libomnithread3c2
    omnievents
    omniorb4
    omniorb4-nameserver
    python-omniorb2
    python-tk
#    tvmet1.7.1-1
    tvmet\ \(>=1.7.2\)
    liblapack3gf
    libblas3gf
    f2c
    libf2c2
    libg2c0
    libgfortran3
    libboost-filesystem1.34.1
    libboost-program-options1.34.1
    libboost-regex1.34.1
    libboost-signals1.34.1
    libboost-thread1.34.1
    libc6
    zlib1g
    libace-5.6.3
    libjpeg62
    libpng12-0
    uuid-runtime
    openrtm-aist
    openrtm-aist-doc
    openrtm-aist-example
    python-yaml
  )
endif()
