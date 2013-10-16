// This file is generated by omniidl (C++ backend)- omniORB_4_1. Do not edit.

#include "CollisionDetector.hh"
#include <omniORB4/IOP_S.h>
#include <omniORB4/IOP_C.h>
#include <omniORB4/callDescriptor.h>
#include <omniORB4/callHandle.h>
#include <omniORB4/objTracker.h>


OMNI_USING_NAMESPACE(omni)

static const char* _0RL_library_version = omniORB_4_1;



OpenHRP::CollisionDetector_ptr OpenHRP::CollisionDetector_Helper::_nil() {
  return ::OpenHRP::CollisionDetector::_nil();
}

::CORBA::Boolean OpenHRP::CollisionDetector_Helper::is_nil(::OpenHRP::CollisionDetector_ptr p) {
  return ::CORBA::is_nil(p);

}

void OpenHRP::CollisionDetector_Helper::release(::OpenHRP::CollisionDetector_ptr p) {
  ::CORBA::release(p);
}

void OpenHRP::CollisionDetector_Helper::marshalObjRef(::OpenHRP::CollisionDetector_ptr obj, cdrStream& s) {
  ::OpenHRP::CollisionDetector::_marshalObjRef(obj, s);
}

OpenHRP::CollisionDetector_ptr OpenHRP::CollisionDetector_Helper::unmarshalObjRef(cdrStream& s) {
  return ::OpenHRP::CollisionDetector::_unmarshalObjRef(s);
}

void OpenHRP::CollisionDetector_Helper::duplicate(::OpenHRP::CollisionDetector_ptr obj) {
  if( obj && !obj->_NP_is_nil() )  omni::duplicateObjRef(obj);
}

OpenHRP::CollisionDetector_ptr
OpenHRP::CollisionDetector::_duplicate(::OpenHRP::CollisionDetector_ptr obj)
{
  if( obj && !obj->_NP_is_nil() )  omni::duplicateObjRef(obj);
  return obj;
}

OpenHRP::CollisionDetector_ptr
OpenHRP::CollisionDetector::_narrow(::CORBA::Object_ptr obj)
{
  if( !obj || obj->_NP_is_nil() || obj->_NP_is_pseudo() ) return _nil();
  _ptr_type e = (_ptr_type) obj->_PR_getobj()->_realNarrow(_PD_repoId);
  return e ? e : _nil();
}


OpenHRP::CollisionDetector_ptr
OpenHRP::CollisionDetector::_unchecked_narrow(::CORBA::Object_ptr obj)
{
  if( !obj || obj->_NP_is_nil() || obj->_NP_is_pseudo() ) return _nil();
  _ptr_type e = (_ptr_type) obj->_PR_getobj()->_uncheckedNarrow(_PD_repoId);
  return e ? e : _nil();
}

OpenHRP::CollisionDetector_ptr
OpenHRP::CollisionDetector::_nil()
{
#ifdef OMNI_UNLOADABLE_STUBS
  static _objref_CollisionDetector _the_nil_obj;
  return &_the_nil_obj;
#else
  static _objref_CollisionDetector* _the_nil_ptr = 0;
  if( !_the_nil_ptr ) {
    omni::nilRefLock().lock();
    if( !_the_nil_ptr ) {
      _the_nil_ptr = new _objref_CollisionDetector;
      registerNilCorbaObject(_the_nil_ptr);
    }
    omni::nilRefLock().unlock();
  }
  return _the_nil_ptr;
#endif
}

const char* OpenHRP::CollisionDetector::_PD_repoId = "IDL:OpenHRP/CollisionDetector:1.0";


OpenHRP::_objref_CollisionDetector::~_objref_CollisionDetector() {
  
}


OpenHRP::_objref_CollisionDetector::_objref_CollisionDetector(omniIOR* ior, omniIdentity* id) :
   omniObjRef(::OpenHRP::CollisionDetector::_PD_repoId, ior, id, 1),
   _objref_World(ior, id)
   
{
  _PR_setobj(this);
}

void*
OpenHRP::_objref_CollisionDetector::_ptrToObjRef(const char* id)
{
  if( id == ::OpenHRP::CollisionDetector::_PD_repoId )
    return (::OpenHRP::CollisionDetector_ptr) this;
  if( id == ::OpenHRP::World::_PD_repoId )
    return (::OpenHRP::World_ptr) this;


  if( id == ::CORBA::Object::_PD_repoId )
    return (::CORBA::Object_ptr) this;

  if( omni::strMatch(id, ::OpenHRP::CollisionDetector::_PD_repoId) )
    return (::OpenHRP::CollisionDetector_ptr) this;
  if( omni::strMatch(id, ::OpenHRP::World::_PD_repoId) )
    return (::OpenHRP::World_ptr) this;


  if( omni::strMatch(id, ::CORBA::Object::_PD_repoId) )
    return (::CORBA::Object_ptr) this;

  return 0;
}

// Proxy call descriptor class. Mangled signature:
//  void
class _0RL_cd_04f4de350348fce7_00000000
  : public omniCallDescriptor
{
public:
  inline _0RL_cd_04f4de350348fce7_00000000(LocalCallFn lcfn,const char* op_,size_t oplen,_CORBA_Boolean upcall=0):
     omniCallDescriptor(lcfn, op_, oplen, 0, _user_exns, 0, upcall)
  {
    
  }
  
  
    
  
  static const char* const _user_exns[];

  
};

const char* const _0RL_cd_04f4de350348fce7_00000000::_user_exns[] = {
  0
};

// Local call call-back function.
static void
_0RL_lcfn_04f4de350348fce7_10000000(omniCallDescriptor*, omniServant* svnt)
{
  
  OpenHRP::_impl_CollisionDetector* impl = (OpenHRP::_impl_CollisionDetector*) svnt->_ptrToInterface(OpenHRP::CollisionDetector::_PD_repoId);
  impl->destroy();


}

void OpenHRP::_objref_CollisionDetector::destroy()
{
  _0RL_cd_04f4de350348fce7_00000000 _call_desc(_0RL_lcfn_04f4de350348fce7_10000000, "destroy", 8);


  _invoke(_call_desc);



}
// Proxy call descriptor class. Mangled signature:
//  void_i_cOpenHRP_mLinkPair
class _0RL_cd_04f4de350348fce7_20000000
  : public omniCallDescriptor
{
public:
  inline _0RL_cd_04f4de350348fce7_20000000(LocalCallFn lcfn,const char* op_,size_t oplen,_CORBA_Boolean upcall=0):
     omniCallDescriptor(lcfn, op_, oplen, 0, _user_exns, 0, upcall)
  {
    
  }
  
  void marshalArguments(cdrStream&);
  void unmarshalArguments(cdrStream&);

    
  
  static const char* const _user_exns[];

  OpenHRP::LinkPair_var arg_0_;
  const OpenHRP::LinkPair* arg_0;
};

void _0RL_cd_04f4de350348fce7_20000000::marshalArguments(cdrStream& _n)
{
  (const OpenHRP::LinkPair&) *arg_0 >>= _n;

}

void _0RL_cd_04f4de350348fce7_20000000::unmarshalArguments(cdrStream& _n)
{
  arg_0_ = new OpenHRP::LinkPair;
  (OpenHRP::LinkPair&)arg_0_ <<= _n;
  arg_0 = &arg_0_.in();

}

const char* const _0RL_cd_04f4de350348fce7_20000000::_user_exns[] = {
  0
};

// Local call call-back function.
static void
_0RL_lcfn_04f4de350348fce7_30000000(omniCallDescriptor* cd, omniServant* svnt)
{
  _0RL_cd_04f4de350348fce7_20000000* tcd = (_0RL_cd_04f4de350348fce7_20000000*)cd;
  OpenHRP::_impl_CollisionDetector* impl = (OpenHRP::_impl_CollisionDetector*) svnt->_ptrToInterface(OpenHRP::CollisionDetector::_PD_repoId);
  impl->addCollisionPair(*tcd->arg_0);


}

void OpenHRP::_objref_CollisionDetector::addCollisionPair(const ::OpenHRP::LinkPair& collisionPair)
{
  _0RL_cd_04f4de350348fce7_20000000 _call_desc(_0RL_lcfn_04f4de350348fce7_30000000, "addCollisionPair", 17);
  _call_desc.arg_0 = &(::OpenHRP::LinkPair&) collisionPair;

  _invoke(_call_desc);



}
// Proxy call descriptor class. Mangled signature:
//  _cboolean_i_cboolean_i_cOpenHRP_mCharacterPositionSequence_o_cOpenHRP_mLinkPairSequence
class _0RL_cd_04f4de350348fce7_40000000
  : public omniCallDescriptor
{
public:
  inline _0RL_cd_04f4de350348fce7_40000000(LocalCallFn lcfn,const char* op_,size_t oplen,_CORBA_Boolean upcall=0):
     omniCallDescriptor(lcfn, op_, oplen, 0, _user_exns, 0, upcall)
  {
    
  }
  
  void marshalArguments(cdrStream&);
  void unmarshalArguments(cdrStream&);

  void unmarshalReturnedValues(cdrStream&);
  void marshalReturnedValues(cdrStream&);
  
  
  static const char* const _user_exns[];

  ::CORBA::Boolean arg_0;
  OpenHRP::CharacterPositionSequence_var arg_1_;
  const OpenHRP::CharacterPositionSequence* arg_1;
  OpenHRP::LinkPairSequence_var arg_2;
  ::CORBA::Boolean result;
};

void _0RL_cd_04f4de350348fce7_40000000::marshalArguments(cdrStream& _n)
{
  _n.marshalBoolean(arg_0);
  (const OpenHRP::CharacterPositionSequence&) *arg_1 >>= _n;

}

void _0RL_cd_04f4de350348fce7_40000000::unmarshalArguments(cdrStream& _n)
{
  arg_0 = _n.unmarshalBoolean();
  arg_1_ = new OpenHRP::CharacterPositionSequence;
  (OpenHRP::CharacterPositionSequence&)arg_1_ <<= _n;
  arg_1 = &arg_1_.in();

}

void _0RL_cd_04f4de350348fce7_40000000::marshalReturnedValues(cdrStream& _n)
{
  _n.marshalBoolean(result);
  (const OpenHRP::LinkPairSequence&) arg_2 >>= _n;

}

void _0RL_cd_04f4de350348fce7_40000000::unmarshalReturnedValues(cdrStream& _n)
{
  result = _n.unmarshalBoolean();
  arg_2 = new OpenHRP::LinkPairSequence;
  (OpenHRP::LinkPairSequence&)arg_2 <<= _n;

}

const char* const _0RL_cd_04f4de350348fce7_40000000::_user_exns[] = {
  0
};

// Local call call-back function.
static void
_0RL_lcfn_04f4de350348fce7_50000000(omniCallDescriptor* cd, omniServant* svnt)
{
  _0RL_cd_04f4de350348fce7_40000000* tcd = (_0RL_cd_04f4de350348fce7_40000000*)cd;
  OpenHRP::_impl_CollisionDetector* impl = (OpenHRP::_impl_CollisionDetector*) svnt->_ptrToInterface(OpenHRP::CollisionDetector::_PD_repoId);
  tcd->result = impl->queryIntersectionForDefinedPairs(tcd->arg_0, *tcd->arg_1, tcd->arg_2.out());


}

::CORBA::Boolean OpenHRP::_objref_CollisionDetector::queryIntersectionForDefinedPairs(::CORBA::Boolean checkAll, const ::OpenHRP::CharacterPositionSequence& positions, ::OpenHRP::LinkPairSequence_out collidedPairs)
{
  _0RL_cd_04f4de350348fce7_40000000 _call_desc(_0RL_lcfn_04f4de350348fce7_50000000, "queryIntersectionForDefinedPairs", 33);
  _call_desc.arg_0 = checkAll;
  _call_desc.arg_1 = &(::OpenHRP::CharacterPositionSequence&) positions;

  _invoke(_call_desc);
  collidedPairs = _call_desc.arg_2._retn();
  return _call_desc.result;


}
// Proxy call descriptor class. Mangled signature:
//  _cboolean_i_cboolean_i_cOpenHRP_mLinkPairSequence_i_cOpenHRP_mCharacterPositionSequence_o_cOpenHRP_mLinkPairSequence
class _0RL_cd_04f4de350348fce7_60000000
  : public omniCallDescriptor
{
public:
  inline _0RL_cd_04f4de350348fce7_60000000(LocalCallFn lcfn,const char* op_,size_t oplen,_CORBA_Boolean upcall=0):
     omniCallDescriptor(lcfn, op_, oplen, 0, _user_exns, 0, upcall)
  {
    
  }
  
  void marshalArguments(cdrStream&);
  void unmarshalArguments(cdrStream&);

  void unmarshalReturnedValues(cdrStream&);
  void marshalReturnedValues(cdrStream&);
  
  
  static const char* const _user_exns[];

  ::CORBA::Boolean arg_0;
  OpenHRP::LinkPairSequence_var arg_1_;
  const OpenHRP::LinkPairSequence* arg_1;
  OpenHRP::CharacterPositionSequence_var arg_2_;
  const OpenHRP::CharacterPositionSequence* arg_2;
  OpenHRP::LinkPairSequence_var arg_3;
  ::CORBA::Boolean result;
};

void _0RL_cd_04f4de350348fce7_60000000::marshalArguments(cdrStream& _n)
{
  _n.marshalBoolean(arg_0);
  (const OpenHRP::LinkPairSequence&) *arg_1 >>= _n;
  (const OpenHRP::CharacterPositionSequence&) *arg_2 >>= _n;

}

void _0RL_cd_04f4de350348fce7_60000000::unmarshalArguments(cdrStream& _n)
{
  arg_0 = _n.unmarshalBoolean();
  arg_1_ = new OpenHRP::LinkPairSequence;
  (OpenHRP::LinkPairSequence&)arg_1_ <<= _n;
  arg_1 = &arg_1_.in();
  arg_2_ = new OpenHRP::CharacterPositionSequence;
  (OpenHRP::CharacterPositionSequence&)arg_2_ <<= _n;
  arg_2 = &arg_2_.in();

}

void _0RL_cd_04f4de350348fce7_60000000::marshalReturnedValues(cdrStream& _n)
{
  _n.marshalBoolean(result);
  (const OpenHRP::LinkPairSequence&) arg_3 >>= _n;

}

void _0RL_cd_04f4de350348fce7_60000000::unmarshalReturnedValues(cdrStream& _n)
{
  result = _n.unmarshalBoolean();
  arg_3 = new OpenHRP::LinkPairSequence;
  (OpenHRP::LinkPairSequence&)arg_3 <<= _n;

}

const char* const _0RL_cd_04f4de350348fce7_60000000::_user_exns[] = {
  0
};

// Local call call-back function.
static void
_0RL_lcfn_04f4de350348fce7_70000000(omniCallDescriptor* cd, omniServant* svnt)
{
  _0RL_cd_04f4de350348fce7_60000000* tcd = (_0RL_cd_04f4de350348fce7_60000000*)cd;
  OpenHRP::_impl_CollisionDetector* impl = (OpenHRP::_impl_CollisionDetector*) svnt->_ptrToInterface(OpenHRP::CollisionDetector::_PD_repoId);
  tcd->result = impl->queryIntersectionForGivenPairs(tcd->arg_0, *tcd->arg_1, *tcd->arg_2, tcd->arg_3.out());


}

::CORBA::Boolean OpenHRP::_objref_CollisionDetector::queryIntersectionForGivenPairs(::CORBA::Boolean checkAll, const ::OpenHRP::LinkPairSequence& checkPairs, const ::OpenHRP::CharacterPositionSequence& positions, ::OpenHRP::LinkPairSequence_out collidedPairs)
{
  _0RL_cd_04f4de350348fce7_60000000 _call_desc(_0RL_lcfn_04f4de350348fce7_70000000, "queryIntersectionForGivenPairs", 31);
  _call_desc.arg_0 = checkAll;
  _call_desc.arg_1 = &(::OpenHRP::LinkPairSequence&) checkPairs;
  _call_desc.arg_2 = &(::OpenHRP::CharacterPositionSequence&) positions;

  _invoke(_call_desc);
  collidedPairs = _call_desc.arg_3._retn();
  return _call_desc.result;


}
// Proxy call descriptor class. Mangled signature:
//  _cboolean_i_cOpenHRP_mCharacterPositionSequence_o_cOpenHRP_mCollisionSequence
class _0RL_cd_04f4de350348fce7_80000000
  : public omniCallDescriptor
{
public:
  inline _0RL_cd_04f4de350348fce7_80000000(LocalCallFn lcfn,const char* op_,size_t oplen,_CORBA_Boolean upcall=0):
     omniCallDescriptor(lcfn, op_, oplen, 0, _user_exns, 0, upcall)
  {
    
  }
  
  void marshalArguments(cdrStream&);
  void unmarshalArguments(cdrStream&);

  void unmarshalReturnedValues(cdrStream&);
  void marshalReturnedValues(cdrStream&);
  
  
  static const char* const _user_exns[];

  OpenHRP::CharacterPositionSequence_var arg_0_;
  const OpenHRP::CharacterPositionSequence* arg_0;
  OpenHRP::CollisionSequence_var arg_1;
  ::CORBA::Boolean result;
};

void _0RL_cd_04f4de350348fce7_80000000::marshalArguments(cdrStream& _n)
{
  (const OpenHRP::CharacterPositionSequence&) *arg_0 >>= _n;

}

void _0RL_cd_04f4de350348fce7_80000000::unmarshalArguments(cdrStream& _n)
{
  arg_0_ = new OpenHRP::CharacterPositionSequence;
  (OpenHRP::CharacterPositionSequence&)arg_0_ <<= _n;
  arg_0 = &arg_0_.in();

}

void _0RL_cd_04f4de350348fce7_80000000::marshalReturnedValues(cdrStream& _n)
{
  _n.marshalBoolean(result);
  (const OpenHRP::CollisionSequence&) arg_1 >>= _n;

}

void _0RL_cd_04f4de350348fce7_80000000::unmarshalReturnedValues(cdrStream& _n)
{
  result = _n.unmarshalBoolean();
  arg_1 = new OpenHRP::CollisionSequence;
  (OpenHRP::CollisionSequence&)arg_1 <<= _n;

}

const char* const _0RL_cd_04f4de350348fce7_80000000::_user_exns[] = {
  0
};

// Local call call-back function.
static void
_0RL_lcfn_04f4de350348fce7_90000000(omniCallDescriptor* cd, omniServant* svnt)
{
  _0RL_cd_04f4de350348fce7_80000000* tcd = (_0RL_cd_04f4de350348fce7_80000000*)cd;
  OpenHRP::_impl_CollisionDetector* impl = (OpenHRP::_impl_CollisionDetector*) svnt->_ptrToInterface(OpenHRP::CollisionDetector::_PD_repoId);
  tcd->result = impl->queryContactDeterminationForDefinedPairs(*tcd->arg_0, tcd->arg_1.out());


}

::CORBA::Boolean OpenHRP::_objref_CollisionDetector::queryContactDeterminationForDefinedPairs(const ::OpenHRP::CharacterPositionSequence& positions, ::OpenHRP::CollisionSequence_out collisions)
{
  _0RL_cd_04f4de350348fce7_80000000 _call_desc(_0RL_lcfn_04f4de350348fce7_90000000, "queryContactDeterminationForDefinedPairs", 41);
  _call_desc.arg_0 = &(::OpenHRP::CharacterPositionSequence&) positions;

  _invoke(_call_desc);
  collisions = _call_desc.arg_1._retn();
  return _call_desc.result;


}
// Proxy call descriptor class. Mangled signature:
//  _cboolean_i_cOpenHRP_mLinkPairSequence_i_cOpenHRP_mCharacterPositionSequence_o_cOpenHRP_mCollisionSequence
class _0RL_cd_04f4de350348fce7_a0000000
  : public omniCallDescriptor
{
public:
  inline _0RL_cd_04f4de350348fce7_a0000000(LocalCallFn lcfn,const char* op_,size_t oplen,_CORBA_Boolean upcall=0):
     omniCallDescriptor(lcfn, op_, oplen, 0, _user_exns, 0, upcall)
  {
    
  }
  
  void marshalArguments(cdrStream&);
  void unmarshalArguments(cdrStream&);

  void unmarshalReturnedValues(cdrStream&);
  void marshalReturnedValues(cdrStream&);
  
  
  static const char* const _user_exns[];

  OpenHRP::LinkPairSequence_var arg_0_;
  const OpenHRP::LinkPairSequence* arg_0;
  OpenHRP::CharacterPositionSequence_var arg_1_;
  const OpenHRP::CharacterPositionSequence* arg_1;
  OpenHRP::CollisionSequence_var arg_2;
  ::CORBA::Boolean result;
};

void _0RL_cd_04f4de350348fce7_a0000000::marshalArguments(cdrStream& _n)
{
  (const OpenHRP::LinkPairSequence&) *arg_0 >>= _n;
  (const OpenHRP::CharacterPositionSequence&) *arg_1 >>= _n;

}

void _0RL_cd_04f4de350348fce7_a0000000::unmarshalArguments(cdrStream& _n)
{
  arg_0_ = new OpenHRP::LinkPairSequence;
  (OpenHRP::LinkPairSequence&)arg_0_ <<= _n;
  arg_0 = &arg_0_.in();
  arg_1_ = new OpenHRP::CharacterPositionSequence;
  (OpenHRP::CharacterPositionSequence&)arg_1_ <<= _n;
  arg_1 = &arg_1_.in();

}

void _0RL_cd_04f4de350348fce7_a0000000::marshalReturnedValues(cdrStream& _n)
{
  _n.marshalBoolean(result);
  (const OpenHRP::CollisionSequence&) arg_2 >>= _n;

}

void _0RL_cd_04f4de350348fce7_a0000000::unmarshalReturnedValues(cdrStream& _n)
{
  result = _n.unmarshalBoolean();
  arg_2 = new OpenHRP::CollisionSequence;
  (OpenHRP::CollisionSequence&)arg_2 <<= _n;

}

const char* const _0RL_cd_04f4de350348fce7_a0000000::_user_exns[] = {
  0
};

// Local call call-back function.
static void
_0RL_lcfn_04f4de350348fce7_b0000000(omniCallDescriptor* cd, omniServant* svnt)
{
  _0RL_cd_04f4de350348fce7_a0000000* tcd = (_0RL_cd_04f4de350348fce7_a0000000*)cd;
  OpenHRP::_impl_CollisionDetector* impl = (OpenHRP::_impl_CollisionDetector*) svnt->_ptrToInterface(OpenHRP::CollisionDetector::_PD_repoId);
  tcd->result = impl->queryContactDeterminationForGivenPairs(*tcd->arg_0, *tcd->arg_1, tcd->arg_2.out());


}

::CORBA::Boolean OpenHRP::_objref_CollisionDetector::queryContactDeterminationForGivenPairs(const ::OpenHRP::LinkPairSequence& checkPairs, const ::OpenHRP::CharacterPositionSequence& positions, ::OpenHRP::CollisionSequence_out collisions)
{
  _0RL_cd_04f4de350348fce7_a0000000 _call_desc(_0RL_lcfn_04f4de350348fce7_b0000000, "queryContactDeterminationForGivenPairs", 39);
  _call_desc.arg_0 = &(::OpenHRP::LinkPairSequence&) checkPairs;
  _call_desc.arg_1 = &(::OpenHRP::CharacterPositionSequence&) positions;

  _invoke(_call_desc);
  collisions = _call_desc.arg_2._retn();
  return _call_desc.result;


}
// Proxy call descriptor class. Mangled signature:
//  void_i_cOpenHRP_mCharacterPositionSequence_o_cOpenHRP_mDistanceSequence
class _0RL_cd_04f4de350348fce7_c0000000
  : public omniCallDescriptor
{
public:
  inline _0RL_cd_04f4de350348fce7_c0000000(LocalCallFn lcfn,const char* op_,size_t oplen,_CORBA_Boolean upcall=0):
     omniCallDescriptor(lcfn, op_, oplen, 0, _user_exns, 0, upcall)
  {
    
  }
  
  void marshalArguments(cdrStream&);
  void unmarshalArguments(cdrStream&);

  void unmarshalReturnedValues(cdrStream&);
  void marshalReturnedValues(cdrStream&);
  
  
  static const char* const _user_exns[];

  OpenHRP::CharacterPositionSequence_var arg_0_;
  const OpenHRP::CharacterPositionSequence* arg_0;
  OpenHRP::DistanceSequence_var arg_1;
};

void _0RL_cd_04f4de350348fce7_c0000000::marshalArguments(cdrStream& _n)
{
  (const OpenHRP::CharacterPositionSequence&) *arg_0 >>= _n;

}

void _0RL_cd_04f4de350348fce7_c0000000::unmarshalArguments(cdrStream& _n)
{
  arg_0_ = new OpenHRP::CharacterPositionSequence;
  (OpenHRP::CharacterPositionSequence&)arg_0_ <<= _n;
  arg_0 = &arg_0_.in();

}

void _0RL_cd_04f4de350348fce7_c0000000::marshalReturnedValues(cdrStream& _n)
{
  (const OpenHRP::DistanceSequence&) arg_1 >>= _n;

}

void _0RL_cd_04f4de350348fce7_c0000000::unmarshalReturnedValues(cdrStream& _n)
{
  arg_1 = new OpenHRP::DistanceSequence;
  (OpenHRP::DistanceSequence&)arg_1 <<= _n;

}

const char* const _0RL_cd_04f4de350348fce7_c0000000::_user_exns[] = {
  0
};

// Local call call-back function.
static void
_0RL_lcfn_04f4de350348fce7_d0000000(omniCallDescriptor* cd, omniServant* svnt)
{
  _0RL_cd_04f4de350348fce7_c0000000* tcd = (_0RL_cd_04f4de350348fce7_c0000000*)cd;
  OpenHRP::_impl_CollisionDetector* impl = (OpenHRP::_impl_CollisionDetector*) svnt->_ptrToInterface(OpenHRP::CollisionDetector::_PD_repoId);
  impl->queryDistanceForDefinedPairs(*tcd->arg_0, tcd->arg_1.out());


}

void OpenHRP::_objref_CollisionDetector::queryDistanceForDefinedPairs(const ::OpenHRP::CharacterPositionSequence& positions, ::OpenHRP::DistanceSequence_out distances)
{
  _0RL_cd_04f4de350348fce7_c0000000 _call_desc(_0RL_lcfn_04f4de350348fce7_d0000000, "queryDistanceForDefinedPairs", 29);
  _call_desc.arg_0 = &(::OpenHRP::CharacterPositionSequence&) positions;

  _invoke(_call_desc);
  distances = _call_desc.arg_1._retn();


}
// Proxy call descriptor class. Mangled signature:
//  void_i_cOpenHRP_mLinkPairSequence_i_cOpenHRP_mCharacterPositionSequence_o_cOpenHRP_mDistanceSequence
class _0RL_cd_04f4de350348fce7_e0000000
  : public omniCallDescriptor
{
public:
  inline _0RL_cd_04f4de350348fce7_e0000000(LocalCallFn lcfn,const char* op_,size_t oplen,_CORBA_Boolean upcall=0):
     omniCallDescriptor(lcfn, op_, oplen, 0, _user_exns, 0, upcall)
  {
    
  }
  
  void marshalArguments(cdrStream&);
  void unmarshalArguments(cdrStream&);

  void unmarshalReturnedValues(cdrStream&);
  void marshalReturnedValues(cdrStream&);
  
  
  static const char* const _user_exns[];

  OpenHRP::LinkPairSequence_var arg_0_;
  const OpenHRP::LinkPairSequence* arg_0;
  OpenHRP::CharacterPositionSequence_var arg_1_;
  const OpenHRP::CharacterPositionSequence* arg_1;
  OpenHRP::DistanceSequence_var arg_2;
};

void _0RL_cd_04f4de350348fce7_e0000000::marshalArguments(cdrStream& _n)
{
  (const OpenHRP::LinkPairSequence&) *arg_0 >>= _n;
  (const OpenHRP::CharacterPositionSequence&) *arg_1 >>= _n;

}

void _0RL_cd_04f4de350348fce7_e0000000::unmarshalArguments(cdrStream& _n)
{
  arg_0_ = new OpenHRP::LinkPairSequence;
  (OpenHRP::LinkPairSequence&)arg_0_ <<= _n;
  arg_0 = &arg_0_.in();
  arg_1_ = new OpenHRP::CharacterPositionSequence;
  (OpenHRP::CharacterPositionSequence&)arg_1_ <<= _n;
  arg_1 = &arg_1_.in();

}

void _0RL_cd_04f4de350348fce7_e0000000::marshalReturnedValues(cdrStream& _n)
{
  (const OpenHRP::DistanceSequence&) arg_2 >>= _n;

}

void _0RL_cd_04f4de350348fce7_e0000000::unmarshalReturnedValues(cdrStream& _n)
{
  arg_2 = new OpenHRP::DistanceSequence;
  (OpenHRP::DistanceSequence&)arg_2 <<= _n;

}

const char* const _0RL_cd_04f4de350348fce7_e0000000::_user_exns[] = {
  0
};

// Local call call-back function.
static void
_0RL_lcfn_04f4de350348fce7_f0000000(omniCallDescriptor* cd, omniServant* svnt)
{
  _0RL_cd_04f4de350348fce7_e0000000* tcd = (_0RL_cd_04f4de350348fce7_e0000000*)cd;
  OpenHRP::_impl_CollisionDetector* impl = (OpenHRP::_impl_CollisionDetector*) svnt->_ptrToInterface(OpenHRP::CollisionDetector::_PD_repoId);
  impl->queryDistanceForGivenPairs(*tcd->arg_0, *tcd->arg_1, tcd->arg_2.out());


}

void OpenHRP::_objref_CollisionDetector::queryDistanceForGivenPairs(const ::OpenHRP::LinkPairSequence& checkPairs, const ::OpenHRP::CharacterPositionSequence& positions, ::OpenHRP::DistanceSequence_out distances)
{
  _0RL_cd_04f4de350348fce7_e0000000 _call_desc(_0RL_lcfn_04f4de350348fce7_f0000000, "queryDistanceForGivenPairs", 27);
  _call_desc.arg_0 = &(::OpenHRP::LinkPairSequence&) checkPairs;
  _call_desc.arg_1 = &(::OpenHRP::CharacterPositionSequence&) positions;

  _invoke(_call_desc);
  distances = _call_desc.arg_2._retn();


}
// Proxy call descriptor class. Mangled signature:
//  _cdouble_i_a3_cdouble_i_a3_cdouble
class _0RL_cd_04f4de350348fce7_01000000
  : public omniCallDescriptor
{
public:
  inline _0RL_cd_04f4de350348fce7_01000000(LocalCallFn lcfn,const char* op_,size_t oplen,_CORBA_Boolean upcall=0):
     omniCallDescriptor(lcfn, op_, oplen, 0, _user_exns, 0, upcall)
  {
    
  }
  
  void marshalArguments(cdrStream&);
  void unmarshalArguments(cdrStream&);

  void unmarshalReturnedValues(cdrStream&);
  void marshalReturnedValues(cdrStream&);
  
  
  static const char* const _user_exns[];

  OpenHRP::DblArray3 arg_0_;
  const OpenHRP::DblArray3_slice* arg_0;
  OpenHRP::DblArray3 arg_1_;
  const OpenHRP::DblArray3_slice* arg_1;
  ::CORBA::Double result;
};

void _0RL_cd_04f4de350348fce7_01000000::marshalArguments(cdrStream& _n)
{
  
#ifndef OMNI_MIXED_ENDIAN_DOUBLE
  if (! _n.marshal_byte_swap()) {
    _n.put_octet_array((_CORBA_Octet*)((::CORBA::Double*)arg_0),24,omni::ALIGN_8);
  }
  else 
#endif
  {
    _n.declareArrayLength(omni::ALIGN_8, 24);
    for (_CORBA_ULong _0i0 = 0; _0i0 < 3; _0i0++){
      arg_0[_0i0] >>= _n;
    }
  }

#ifndef OMNI_MIXED_ENDIAN_DOUBLE
  if (! _n.marshal_byte_swap()) {
    _n.put_octet_array((_CORBA_Octet*)((::CORBA::Double*)arg_1),24,omni::ALIGN_8);
  }
  else 
#endif
  {
    _n.declareArrayLength(omni::ALIGN_8, 24);
    for (_CORBA_ULong _0i0 = 0; _0i0 < 3; _0i0++){
      arg_1[_0i0] >>= _n;
    }
  }

}

void _0RL_cd_04f4de350348fce7_01000000::unmarshalArguments(cdrStream& _n)
{
  _n.unmarshalArrayDouble((_CORBA_Double*)((::CORBA::Double*)arg_0_), 3);
  arg_0 = &arg_0_[0];
  _n.unmarshalArrayDouble((_CORBA_Double*)((::CORBA::Double*)arg_1_), 3);
  arg_1 = &arg_1_[0];

}

void _0RL_cd_04f4de350348fce7_01000000::marshalReturnedValues(cdrStream& _n)
{
  result >>= _n;

}

void _0RL_cd_04f4de350348fce7_01000000::unmarshalReturnedValues(cdrStream& _n)
{
  (::CORBA::Double&)result <<= _n;

}

const char* const _0RL_cd_04f4de350348fce7_01000000::_user_exns[] = {
  0
};

// Local call call-back function.
static void
_0RL_lcfn_04f4de350348fce7_11000000(omniCallDescriptor* cd, omniServant* svnt)
{
  _0RL_cd_04f4de350348fce7_01000000* tcd = (_0RL_cd_04f4de350348fce7_01000000*)cd;
  OpenHRP::_impl_CollisionDetector* impl = (OpenHRP::_impl_CollisionDetector*) svnt->_ptrToInterface(OpenHRP::CollisionDetector::_PD_repoId);
  tcd->result = impl->queryDistanceWithRay(tcd->arg_0, tcd->arg_1);


}

::CORBA::Double OpenHRP::_objref_CollisionDetector::queryDistanceWithRay(const ::OpenHRP::DblArray3 point, const ::OpenHRP::DblArray3 dir)
{
  _0RL_cd_04f4de350348fce7_01000000 _call_desc(_0RL_lcfn_04f4de350348fce7_11000000, "queryDistanceWithRay", 21);
  _call_desc.arg_0 = &point[0];
  _call_desc.arg_1 = &dir[0];

  _invoke(_call_desc);
  return _call_desc.result;


}
// Proxy call descriptor class. Mangled signature:
//  _cOpenHRP_mDblSequence_i_a3_cdouble_i_a9_cdouble_i_cdouble_i_cdouble
class _0RL_cd_04f4de350348fce7_21000000
  : public omniCallDescriptor
{
public:
  inline _0RL_cd_04f4de350348fce7_21000000(LocalCallFn lcfn,const char* op_,size_t oplen,_CORBA_Boolean upcall=0):
     omniCallDescriptor(lcfn, op_, oplen, 0, _user_exns, 0, upcall)
  {
    
  }
  
  void marshalArguments(cdrStream&);
  void unmarshalArguments(cdrStream&);

  void unmarshalReturnedValues(cdrStream&);
  void marshalReturnedValues(cdrStream&);
  
  
  static const char* const _user_exns[];

  OpenHRP::DblArray3 arg_0_;
  const OpenHRP::DblArray3_slice* arg_0;
  OpenHRP::DblArray9 arg_1_;
  const OpenHRP::DblArray9_slice* arg_1;
  ::CORBA::Double arg_2;
  ::CORBA::Double arg_3;
  OpenHRP::DblSequence_var result;
};

void _0RL_cd_04f4de350348fce7_21000000::marshalArguments(cdrStream& _n)
{
  
#ifndef OMNI_MIXED_ENDIAN_DOUBLE
  if (! _n.marshal_byte_swap()) {
    _n.put_octet_array((_CORBA_Octet*)((::CORBA::Double*)arg_0),24,omni::ALIGN_8);
  }
  else 
#endif
  {
    _n.declareArrayLength(omni::ALIGN_8, 24);
    for (_CORBA_ULong _0i0 = 0; _0i0 < 3; _0i0++){
      arg_0[_0i0] >>= _n;
    }
  }

#ifndef OMNI_MIXED_ENDIAN_DOUBLE
  if (! _n.marshal_byte_swap()) {
    _n.put_octet_array((_CORBA_Octet*)((::CORBA::Double*)arg_1),72,omni::ALIGN_8);
  }
  else 
#endif
  {
    _n.declareArrayLength(omni::ALIGN_8, 72);
    for (_CORBA_ULong _0i0 = 0; _0i0 < 9; _0i0++){
      arg_1[_0i0] >>= _n;
    }
  }
  arg_2 >>= _n;
  arg_3 >>= _n;

}

void _0RL_cd_04f4de350348fce7_21000000::unmarshalArguments(cdrStream& _n)
{
  _n.unmarshalArrayDouble((_CORBA_Double*)((::CORBA::Double*)arg_0_), 3);
  arg_0 = &arg_0_[0];
  _n.unmarshalArrayDouble((_CORBA_Double*)((::CORBA::Double*)arg_1_), 9);
  arg_1 = &arg_1_[0];
  (::CORBA::Double&)arg_2 <<= _n;
  (::CORBA::Double&)arg_3 <<= _n;

}

void _0RL_cd_04f4de350348fce7_21000000::marshalReturnedValues(cdrStream& _n)
{
  (const OpenHRP::DblSequence&) result >>= _n;

}

void _0RL_cd_04f4de350348fce7_21000000::unmarshalReturnedValues(cdrStream& _n)
{
  result = new OpenHRP::DblSequence;
  (OpenHRP::DblSequence&)result <<= _n;

}

const char* const _0RL_cd_04f4de350348fce7_21000000::_user_exns[] = {
  0
};

// Local call call-back function.
static void
_0RL_lcfn_04f4de350348fce7_31000000(omniCallDescriptor* cd, omniServant* svnt)
{
  _0RL_cd_04f4de350348fce7_21000000* tcd = (_0RL_cd_04f4de350348fce7_21000000*)cd;
  OpenHRP::_impl_CollisionDetector* impl = (OpenHRP::_impl_CollisionDetector*) svnt->_ptrToInterface(OpenHRP::CollisionDetector::_PD_repoId);
  tcd->result = impl->scanDistanceWithRay(tcd->arg_0, tcd->arg_1, tcd->arg_2, tcd->arg_3);


}

OpenHRP::DblSequence* OpenHRP::_objref_CollisionDetector::scanDistanceWithRay(const ::OpenHRP::DblArray3 p, const ::OpenHRP::DblArray9 R, ::CORBA::Double step, ::CORBA::Double range)
{
  _0RL_cd_04f4de350348fce7_21000000 _call_desc(_0RL_lcfn_04f4de350348fce7_31000000, "scanDistanceWithRay", 20);
  _call_desc.arg_0 = &p[0];
  _call_desc.arg_1 = &R[0];
  _call_desc.arg_2 = step;
  _call_desc.arg_3 = range;

  _invoke(_call_desc);
  return _call_desc.result._retn();


}
OpenHRP::_pof_CollisionDetector::~_pof_CollisionDetector() {}


omniObjRef*
OpenHRP::_pof_CollisionDetector::newObjRef(omniIOR* ior, omniIdentity* id)
{
  return new ::OpenHRP::_objref_CollisionDetector(ior, id);
}


::CORBA::Boolean
OpenHRP::_pof_CollisionDetector::is_a(const char* id) const
{
  if( omni::ptrStrMatch(id, ::OpenHRP::CollisionDetector::_PD_repoId) )
    return 1;
  if( omni::ptrStrMatch(id, OpenHRP::World::_PD_repoId) )
    return 1;


  return 0;
}

const OpenHRP::_pof_CollisionDetector _the_pof_OpenHRP_mCollisionDetector;

OpenHRP::_impl_CollisionDetector::~_impl_CollisionDetector() {}


::CORBA::Boolean
OpenHRP::_impl_CollisionDetector::_dispatch(omniCallHandle& _handle)
{
  const char* op = _handle.operation_name();

  if( omni::strMatch(op, "destroy") ) {

    _0RL_cd_04f4de350348fce7_00000000 _call_desc(_0RL_lcfn_04f4de350348fce7_10000000, "destroy", 8, 1);
    
    _handle.upcall(this,_call_desc);
    return 1;
  }

  if( omni::strMatch(op, "addCollisionPair") ) {

    _0RL_cd_04f4de350348fce7_20000000 _call_desc(_0RL_lcfn_04f4de350348fce7_30000000, "addCollisionPair", 17, 1);
    
    _handle.upcall(this,_call_desc);
    return 1;
  }

  if( omni::strMatch(op, "queryIntersectionForDefinedPairs") ) {

    _0RL_cd_04f4de350348fce7_40000000 _call_desc(_0RL_lcfn_04f4de350348fce7_50000000, "queryIntersectionForDefinedPairs", 33, 1);
    
    _handle.upcall(this,_call_desc);
    return 1;
  }

  if( omni::strMatch(op, "queryIntersectionForGivenPairs") ) {

    _0RL_cd_04f4de350348fce7_60000000 _call_desc(_0RL_lcfn_04f4de350348fce7_70000000, "queryIntersectionForGivenPairs", 31, 1);
    
    _handle.upcall(this,_call_desc);
    return 1;
  }

  if( omni::strMatch(op, "queryContactDeterminationForDefinedPairs") ) {

    _0RL_cd_04f4de350348fce7_80000000 _call_desc(_0RL_lcfn_04f4de350348fce7_90000000, "queryContactDeterminationForDefinedPairs", 41, 1);
    
    _handle.upcall(this,_call_desc);
    return 1;
  }

  if( omni::strMatch(op, "queryContactDeterminationForGivenPairs") ) {

    _0RL_cd_04f4de350348fce7_a0000000 _call_desc(_0RL_lcfn_04f4de350348fce7_b0000000, "queryContactDeterminationForGivenPairs", 39, 1);
    
    _handle.upcall(this,_call_desc);
    return 1;
  }

  if( omni::strMatch(op, "queryDistanceForDefinedPairs") ) {

    _0RL_cd_04f4de350348fce7_c0000000 _call_desc(_0RL_lcfn_04f4de350348fce7_d0000000, "queryDistanceForDefinedPairs", 29, 1);
    
    _handle.upcall(this,_call_desc);
    return 1;
  }

  if( omni::strMatch(op, "queryDistanceForGivenPairs") ) {

    _0RL_cd_04f4de350348fce7_e0000000 _call_desc(_0RL_lcfn_04f4de350348fce7_f0000000, "queryDistanceForGivenPairs", 27, 1);
    
    _handle.upcall(this,_call_desc);
    return 1;
  }

  if( omni::strMatch(op, "queryDistanceWithRay") ) {

    _0RL_cd_04f4de350348fce7_01000000 _call_desc(_0RL_lcfn_04f4de350348fce7_11000000, "queryDistanceWithRay", 21, 1);
    
    _handle.upcall(this,_call_desc);
    return 1;
  }

  if( omni::strMatch(op, "scanDistanceWithRay") ) {

    _0RL_cd_04f4de350348fce7_21000000 _call_desc(_0RL_lcfn_04f4de350348fce7_31000000, "scanDistanceWithRay", 20, 1);
    
    _handle.upcall(this,_call_desc);
    return 1;
  }

  
#ifndef _MSC_VER
  if( _impl_World::_dispatch(_handle) ) {
    return 1;
  }
#else
  // Work-around for incorrect MSVC code generation.
  if( ((_impl_World*)this)->
      _impl_World::_dispatch(_handle) ) {
    return 1;
  }
#endif


  return 0;
}

void*
OpenHRP::_impl_CollisionDetector::_ptrToInterface(const char* id)
{
  if( id == ::OpenHRP::CollisionDetector::_PD_repoId )
    return (::OpenHRP::_impl_CollisionDetector*) this;
  if( id == ::OpenHRP::World::_PD_repoId )
    return (::OpenHRP::_impl_World*) this;


  if( id == ::CORBA::Object::_PD_repoId )
    return (void*) 1;

  if( omni::strMatch(id, ::OpenHRP::CollisionDetector::_PD_repoId) )
    return (::OpenHRP::_impl_CollisionDetector*) this;
  if( omni::strMatch(id, ::OpenHRP::World::_PD_repoId) )
    return (::OpenHRP::_impl_World*) this;


  if( omni::strMatch(id, ::CORBA::Object::_PD_repoId) )
    return (void*) 1;
  return 0;
}

const char*
OpenHRP::_impl_CollisionDetector::_mostDerivedRepoId()
{
  return ::OpenHRP::CollisionDetector::_PD_repoId;
}

OpenHRP::CollisionDetectorFactory_ptr OpenHRP::CollisionDetectorFactory_Helper::_nil() {
  return ::OpenHRP::CollisionDetectorFactory::_nil();
}

::CORBA::Boolean OpenHRP::CollisionDetectorFactory_Helper::is_nil(::OpenHRP::CollisionDetectorFactory_ptr p) {
  return ::CORBA::is_nil(p);

}

void OpenHRP::CollisionDetectorFactory_Helper::release(::OpenHRP::CollisionDetectorFactory_ptr p) {
  ::CORBA::release(p);
}

void OpenHRP::CollisionDetectorFactory_Helper::marshalObjRef(::OpenHRP::CollisionDetectorFactory_ptr obj, cdrStream& s) {
  ::OpenHRP::CollisionDetectorFactory::_marshalObjRef(obj, s);
}

OpenHRP::CollisionDetectorFactory_ptr OpenHRP::CollisionDetectorFactory_Helper::unmarshalObjRef(cdrStream& s) {
  return ::OpenHRP::CollisionDetectorFactory::_unmarshalObjRef(s);
}

void OpenHRP::CollisionDetectorFactory_Helper::duplicate(::OpenHRP::CollisionDetectorFactory_ptr obj) {
  if( obj && !obj->_NP_is_nil() )  omni::duplicateObjRef(obj);
}

OpenHRP::CollisionDetectorFactory_ptr
OpenHRP::CollisionDetectorFactory::_duplicate(::OpenHRP::CollisionDetectorFactory_ptr obj)
{
  if( obj && !obj->_NP_is_nil() )  omni::duplicateObjRef(obj);
  return obj;
}

OpenHRP::CollisionDetectorFactory_ptr
OpenHRP::CollisionDetectorFactory::_narrow(::CORBA::Object_ptr obj)
{
  if( !obj || obj->_NP_is_nil() || obj->_NP_is_pseudo() ) return _nil();
  _ptr_type e = (_ptr_type) obj->_PR_getobj()->_realNarrow(_PD_repoId);
  return e ? e : _nil();
}


OpenHRP::CollisionDetectorFactory_ptr
OpenHRP::CollisionDetectorFactory::_unchecked_narrow(::CORBA::Object_ptr obj)
{
  if( !obj || obj->_NP_is_nil() || obj->_NP_is_pseudo() ) return _nil();
  _ptr_type e = (_ptr_type) obj->_PR_getobj()->_uncheckedNarrow(_PD_repoId);
  return e ? e : _nil();
}

OpenHRP::CollisionDetectorFactory_ptr
OpenHRP::CollisionDetectorFactory::_nil()
{
#ifdef OMNI_UNLOADABLE_STUBS
  static _objref_CollisionDetectorFactory _the_nil_obj;
  return &_the_nil_obj;
#else
  static _objref_CollisionDetectorFactory* _the_nil_ptr = 0;
  if( !_the_nil_ptr ) {
    omni::nilRefLock().lock();
    if( !_the_nil_ptr ) {
      _the_nil_ptr = new _objref_CollisionDetectorFactory;
      registerNilCorbaObject(_the_nil_ptr);
    }
    omni::nilRefLock().unlock();
  }
  return _the_nil_ptr;
#endif
}

const char* OpenHRP::CollisionDetectorFactory::_PD_repoId = "IDL:OpenHRP/CollisionDetectorFactory:1.0";


OpenHRP::_objref_CollisionDetectorFactory::~_objref_CollisionDetectorFactory() {
  
}


OpenHRP::_objref_CollisionDetectorFactory::_objref_CollisionDetectorFactory(omniIOR* ior, omniIdentity* id) :
   omniObjRef(::OpenHRP::CollisionDetectorFactory::_PD_repoId, ior, id, 1),
   _objref_ServerObject(ior, id)
   
{
  _PR_setobj(this);
}

void*
OpenHRP::_objref_CollisionDetectorFactory::_ptrToObjRef(const char* id)
{
  if( id == ::OpenHRP::CollisionDetectorFactory::_PD_repoId )
    return (::OpenHRP::CollisionDetectorFactory_ptr) this;
  if( id == ::OpenHRP::ServerObject::_PD_repoId )
    return (::OpenHRP::ServerObject_ptr) this;


  if( id == ::CORBA::Object::_PD_repoId )
    return (::CORBA::Object_ptr) this;

  if( omni::strMatch(id, ::OpenHRP::CollisionDetectorFactory::_PD_repoId) )
    return (::OpenHRP::CollisionDetectorFactory_ptr) this;
  if( omni::strMatch(id, ::OpenHRP::ServerObject::_PD_repoId) )
    return (::OpenHRP::ServerObject_ptr) this;


  if( omni::strMatch(id, ::CORBA::Object::_PD_repoId) )
    return (::CORBA::Object_ptr) this;

  return 0;
}

// Proxy call descriptor class. Mangled signature:
//  _cOpenHRP_mCollisionDetector
class _0RL_cd_04f4de350348fce7_41000000
  : public omniCallDescriptor
{
public:
  inline _0RL_cd_04f4de350348fce7_41000000(LocalCallFn lcfn,const char* op_,size_t oplen,_CORBA_Boolean upcall=0):
     omniCallDescriptor(lcfn, op_, oplen, 0, _user_exns, 0, upcall)
  {
    
  }
  
  
  void unmarshalReturnedValues(cdrStream&);
  void marshalReturnedValues(cdrStream&);
  
  
  static const char* const _user_exns[];

  OpenHRP::CollisionDetector_var result;
};

void _0RL_cd_04f4de350348fce7_41000000::marshalReturnedValues(cdrStream& _n)
{
  OpenHRP::CollisionDetector::_marshalObjRef(result,_n);

}

void _0RL_cd_04f4de350348fce7_41000000::unmarshalReturnedValues(cdrStream& _n)
{
  result = OpenHRP::CollisionDetector::_unmarshalObjRef(_n);

}

const char* const _0RL_cd_04f4de350348fce7_41000000::_user_exns[] = {
  0
};

// Local call call-back function.
static void
_0RL_lcfn_04f4de350348fce7_51000000(omniCallDescriptor* cd, omniServant* svnt)
{
  _0RL_cd_04f4de350348fce7_41000000* tcd = (_0RL_cd_04f4de350348fce7_41000000*)cd;
  OpenHRP::_impl_CollisionDetectorFactory* impl = (OpenHRP::_impl_CollisionDetectorFactory*) svnt->_ptrToInterface(OpenHRP::CollisionDetectorFactory::_PD_repoId);
  tcd->result = impl->create();


}

OpenHRP::CollisionDetector_ptr OpenHRP::_objref_CollisionDetectorFactory::create()
{
  _0RL_cd_04f4de350348fce7_41000000 _call_desc(_0RL_lcfn_04f4de350348fce7_51000000, "create", 7);


  _invoke(_call_desc);
  return _call_desc.result._retn();


}
OpenHRP::_pof_CollisionDetectorFactory::~_pof_CollisionDetectorFactory() {}


omniObjRef*
OpenHRP::_pof_CollisionDetectorFactory::newObjRef(omniIOR* ior, omniIdentity* id)
{
  return new ::OpenHRP::_objref_CollisionDetectorFactory(ior, id);
}


::CORBA::Boolean
OpenHRP::_pof_CollisionDetectorFactory::is_a(const char* id) const
{
  if( omni::ptrStrMatch(id, ::OpenHRP::CollisionDetectorFactory::_PD_repoId) )
    return 1;
  if( omni::ptrStrMatch(id, OpenHRP::ServerObject::_PD_repoId) )
    return 1;


  return 0;
}

const OpenHRP::_pof_CollisionDetectorFactory _the_pof_OpenHRP_mCollisionDetectorFactory;

OpenHRP::_impl_CollisionDetectorFactory::~_impl_CollisionDetectorFactory() {}


::CORBA::Boolean
OpenHRP::_impl_CollisionDetectorFactory::_dispatch(omniCallHandle& _handle)
{
  const char* op = _handle.operation_name();

  if( omni::strMatch(op, "create") ) {

    _0RL_cd_04f4de350348fce7_41000000 _call_desc(_0RL_lcfn_04f4de350348fce7_51000000, "create", 7, 1);
    
    _handle.upcall(this,_call_desc);
    return 1;
  }

  
#ifndef _MSC_VER
  if( _impl_ServerObject::_dispatch(_handle) ) {
    return 1;
  }
#else
  // Work-around for incorrect MSVC code generation.
  if( ((_impl_ServerObject*)this)->
      _impl_ServerObject::_dispatch(_handle) ) {
    return 1;
  }
#endif


  return 0;
}

void*
OpenHRP::_impl_CollisionDetectorFactory::_ptrToInterface(const char* id)
{
  if( id == ::OpenHRP::CollisionDetectorFactory::_PD_repoId )
    return (::OpenHRP::_impl_CollisionDetectorFactory*) this;
  if( id == ::OpenHRP::ServerObject::_PD_repoId )
    return (::OpenHRP::_impl_ServerObject*) this;


  if( id == ::CORBA::Object::_PD_repoId )
    return (void*) 1;

  if( omni::strMatch(id, ::OpenHRP::CollisionDetectorFactory::_PD_repoId) )
    return (::OpenHRP::_impl_CollisionDetectorFactory*) this;
  if( omni::strMatch(id, ::OpenHRP::ServerObject::_PD_repoId) )
    return (::OpenHRP::_impl_ServerObject*) this;


  if( omni::strMatch(id, ::CORBA::Object::_PD_repoId) )
    return (void*) 1;
  return 0;
}

const char*
OpenHRP::_impl_CollisionDetectorFactory::_mostDerivedRepoId()
{
  return ::OpenHRP::CollisionDetectorFactory::_PD_repoId;
}

POA_OpenHRP::CollisionDetector::~CollisionDetector() {}

POA_OpenHRP::CollisionDetectorFactory::~CollisionDetectorFactory() {}

