
#ifndef HRPMODEL_CONFIG_H_INCLUDED
#define HRPMODEL_CONFIG_H_INCLUDED

#define HRPMODEL_VERSION_MAJOR 3
#define HRPMODEL_VERSION_MINOR 1
#define HRPMODEL_VERSION_MICRO 4

#define OPENHRP_DIR "/usr/local"
#define OPENHRP_SHARE_DIR "/usr/local/share/OpenHRP-3.1"
#define OPENHRP_RELATIVE_SHARE_DIR "share/OpenHRP-3.1"

// for Windows DLL export 
#if defined(WIN32) || defined(_WIN32) || defined(__WIN32__) || defined(__NT__)
# ifdef HRPMODEL_MAKE_DLL
#   define HRPMODEL_API __declspec(dllexport)
# else 
#   define HRPMODEL_API __declspec(dllimport)
# endif
#else 
# define HRPMODEL_API
#endif /* Windows */

#endif
