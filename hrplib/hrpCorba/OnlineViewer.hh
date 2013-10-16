// This file is generated by omniidl (C++ backend)- omniORB_4_1. Do not edit.
#ifndef __OnlineViewer_hh__
#define __OnlineViewer_hh__

#ifndef __CORBA_H_EXTERNAL_GUARD__
#include <omniORB4/CORBA.h>
#endif

#ifndef  USE_stub_in_nt_dll
# define USE_stub_in_nt_dll_NOT_DEFINED_OnlineViewer
#endif
#ifndef  USE_core_stub_in_nt_dll
# define USE_core_stub_in_nt_dll_NOT_DEFINED_OnlineViewer
#endif
#ifndef  USE_dyn_stub_in_nt_dll
# define USE_dyn_stub_in_nt_dll_NOT_DEFINED_OnlineViewer
#endif



#ifndef __OpenHRPCommon_hh_EXTERNAL_GUARD__
#define __OpenHRPCommon_hh_EXTERNAL_GUARD__
#include "OpenHRPCommon.hh"
#endif



#ifdef USE_stub_in_nt_dll
# ifndef USE_core_stub_in_nt_dll
#  define USE_core_stub_in_nt_dll
# endif
# ifndef USE_dyn_stub_in_nt_dll
#  define USE_dyn_stub_in_nt_dll
# endif
#endif

#ifdef _core_attr
# error "A local CPP macro _core_attr has already been defined."
#else
# ifdef  USE_core_stub_in_nt_dll
#  define _core_attr _OMNIORB_NTDLL_IMPORT
# else
#  define _core_attr
# endif
#endif

#ifdef _dyn_attr
# error "A local CPP macro _dyn_attr has already been defined."
#else
# ifdef  USE_dyn_stub_in_nt_dll
#  define _dyn_attr _OMNIORB_NTDLL_IMPORT
# else
#  define _dyn_attr
# endif
#endif





_CORBA_MODULE OpenHRP

_CORBA_MODULE_BEG

#ifndef __OpenHRP_mOnlineViewer__
#define __OpenHRP_mOnlineViewer__

  class OnlineViewer;
  class _objref_OnlineViewer;
  class _impl_OnlineViewer;
  
  typedef _objref_OnlineViewer* OnlineViewer_ptr;
  typedef OnlineViewer_ptr OnlineViewerRef;

  class OnlineViewer_Helper {
  public:
    typedef OnlineViewer_ptr _ptr_type;

    static _ptr_type _nil();
    static _CORBA_Boolean is_nil(_ptr_type);
    static void release(_ptr_type);
    static void duplicate(_ptr_type);
    static void marshalObjRef(_ptr_type, cdrStream&);
    static _ptr_type unmarshalObjRef(cdrStream&);
  };

  typedef _CORBA_ObjRef_Var<_objref_OnlineViewer, OnlineViewer_Helper> OnlineViewer_var;
  typedef _CORBA_ObjRef_OUT_arg<_objref_OnlineViewer,OnlineViewer_Helper > OnlineViewer_out;

#endif

  // interface OnlineViewer
  class OnlineViewer {
  public:
    // Declarations for this interface type.
    typedef OnlineViewer_ptr _ptr_type;
    typedef OnlineViewer_var _var_type;

    static _ptr_type _duplicate(_ptr_type);
    static _ptr_type _narrow(::CORBA::Object_ptr);
    static _ptr_type _unchecked_narrow(::CORBA::Object_ptr);
    
    static _ptr_type _nil();

    static inline void _marshalObjRef(_ptr_type, cdrStream&);

    static inline _ptr_type _unmarshalObjRef(cdrStream& s) {
      omniObjRef* o = omniObjRef::_unMarshal(_PD_repoId,s);
      if (o)
        return (_ptr_type) o->_ptrToObjRef(_PD_repoId);
      else
        return _nil();
    }

    static _core_attr const char* _PD_repoId;

    // Other IDL defined within this scope.
    class OnlineViewerException : public ::CORBA::UserException {
    public:
      
      ::CORBA::String_member description;

    

      inline OnlineViewerException() {
        pd_insertToAnyFn    = insertToAnyFn;
        pd_insertToAnyFnNCP = insertToAnyFnNCP;
      }
      OnlineViewerException(const OnlineViewerException&);
      OnlineViewerException(const char* i_description);
      OnlineViewerException& operator=(const OnlineViewerException&);
      virtual ~OnlineViewerException();
      virtual void _raise() const;
      static OnlineViewerException* _downcast(::CORBA::Exception*);
      static const OnlineViewerException* _downcast(const ::CORBA::Exception*);
      static inline OnlineViewerException* _narrow(::CORBA::Exception* _e) {
        return _downcast(_e);
      }
      
      void operator>>=(cdrStream&) const ;
      void operator<<=(cdrStream&) ;

      static _core_attr insertExceptionToAny    insertToAnyFn;
      static _core_attr insertExceptionToAnyNCP insertToAnyFnNCP;

      virtual ::CORBA::Exception* _NP_duplicate() const;

      static _core_attr const char* _PD_repoId;
      static _core_attr const char* _PD_typeId;

    private:
      virtual const char* _NP_typeId() const;
      virtual const char* _NP_repoId(int*) const;
      virtual void _NP_marshal(cdrStream&) const;
    };

  
  };

  class _objref_OnlineViewer :
    public virtual ::CORBA::Object,
    public virtual omniObjRef
  {
  public:
    void update(const ::OpenHRP::WorldState& state);
    void load(const char* name, const char* url);
    void clearLog();
    void clearData();
    void drawScene(const ::OpenHRP::WorldState& state);
    void setLineWidth(::CORBA::Float width);
    void setLineScale(::CORBA::Float scale);
    ::CORBA::Boolean getPosture(const char* robotId, ::OpenHRP::DblSequence_out posture);
    void setLogName(const char* name);

    inline _objref_OnlineViewer()  { _PR_setobj(0); }  // nil
    _objref_OnlineViewer(omniIOR*, omniIdentity*);

  protected:
    virtual ~_objref_OnlineViewer();

    
  private:
    virtual void* _ptrToObjRef(const char*);

    _objref_OnlineViewer(const _objref_OnlineViewer&);
    _objref_OnlineViewer& operator = (const _objref_OnlineViewer&);
    // not implemented

    friend class OnlineViewer;
  };

  class _pof_OnlineViewer : public _OMNI_NS(proxyObjectFactory) {
  public:
    inline _pof_OnlineViewer() : _OMNI_NS(proxyObjectFactory)(OnlineViewer::_PD_repoId) {}
    virtual ~_pof_OnlineViewer();

    virtual omniObjRef* newObjRef(omniIOR*,omniIdentity*);
    virtual _CORBA_Boolean is_a(const char*) const;
  };

  class _impl_OnlineViewer :
    public virtual omniServant
  {
  public:
    virtual ~_impl_OnlineViewer();

    virtual void update(const ::OpenHRP::WorldState& state) = 0;
    virtual void load(const char* name, const char* url) = 0;
    virtual void clearLog() = 0;
    virtual void clearData() = 0;
    virtual void drawScene(const ::OpenHRP::WorldState& state) = 0;
    virtual void setLineWidth(::CORBA::Float width) = 0;
    virtual void setLineScale(::CORBA::Float scale) = 0;
    virtual ::CORBA::Boolean getPosture(const char* robotId, ::OpenHRP::DblSequence_out posture) = 0;
    virtual void setLogName(const char* name) = 0;
    
  public:  // Really protected, workaround for xlC
    virtual _CORBA_Boolean _dispatch(omniCallHandle&);

  private:
    virtual void* _ptrToInterface(const char*);
    virtual const char* _mostDerivedRepoId();
    
  };


_CORBA_MODULE_END



_CORBA_MODULE POA_OpenHRP
_CORBA_MODULE_BEG

  class OnlineViewer :
    public virtual OpenHRP::_impl_OnlineViewer,
    public virtual ::PortableServer::ServantBase
  {
  public:
    virtual ~OnlineViewer();

    inline ::OpenHRP::OnlineViewer_ptr _this() {
      return (::OpenHRP::OnlineViewer_ptr) _do_this(::OpenHRP::OnlineViewer::_PD_repoId);
    }
  };

_CORBA_MODULE_END



_CORBA_MODULE OBV_OpenHRP
_CORBA_MODULE_BEG

_CORBA_MODULE_END





#undef _core_attr
#undef _dyn_attr



inline void
OpenHRP::OnlineViewer::_marshalObjRef(::OpenHRP::OnlineViewer_ptr obj, cdrStream& s) {
  omniObjRef::_marshal(obj->_PR_getobj(),s);
}




#ifdef   USE_stub_in_nt_dll_NOT_DEFINED_OnlineViewer
# undef  USE_stub_in_nt_dll
# undef  USE_stub_in_nt_dll_NOT_DEFINED_OnlineViewer
#endif
#ifdef   USE_core_stub_in_nt_dll_NOT_DEFINED_OnlineViewer
# undef  USE_core_stub_in_nt_dll
# undef  USE_core_stub_in_nt_dll_NOT_DEFINED_OnlineViewer
#endif
#ifdef   USE_dyn_stub_in_nt_dll_NOT_DEFINED_OnlineViewer
# undef  USE_dyn_stub_in_nt_dll
# undef  USE_dyn_stub_in_nt_dll_NOT_DEFINED_OnlineViewer
#endif

#endif  // __OnlineViewer_hh__
