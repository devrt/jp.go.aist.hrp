/*! 
  @file BodyInfo_impl.cpp
  @author S.NAKAOKA
*/

#include "BodyInfo_impl.h"
#include "ViewSimulator.h"
#include "VrmlNodes.h"
#include "CalculateNormal.h"
#include "ImageConverter.h"

#include <iostream>
#include <map>
#include <vector>
#include <boost/bind.hpp>

using namespace std;
using namespace boost;
using namespace OpenHRP;



/*!
  @if jp
  shapeInfo���L�̂��߂̃}�b�v
  ���� shape_ �Ɋi�[����Ă��� node �ł���΁A�Ή�����C���f�b�N�X������
  @else
  Map for sharing shapeInfo
   if it is node that has already stored in shape_, it has the corresponding index.
  @endif
*/

// ShapeInfo��index�ƁC����shape���Z�o����transform�̃y�A
struct ShapeObject
{
	matrix44d	transform;
	short		index;
};

typedef map<OpenHRP::VrmlNodePtr, ShapeObject> SharedShapeInfoMap;
SharedShapeInfoMap sharedShapeInfoMap;

typedef map<string, string> SensorTypeMap;
SensorTypeMap sensorTypeMap;


bool operator != ( matrix44d& a, matrix44d& b )
{
	for( int i = 0 ; i < 4 ; i++ )
	{
		for( int j = 0 ; j < 4; j++ )
		{
			if( a( i, j ) != b( i, j ) )
				return false;
		}
	}
	return true;
}


BodyInfo_impl::BodyInfo_impl( PortableServer::POA_ptr poa ) :
    poa( PortableServer::POA::_duplicate( poa ) )
{
	lastUpdate_ = 0;
}


BodyInfo_impl::~BodyInfo_impl()
{
}


PortableServer::POA_ptr BodyInfo_impl::_default_POA()
{
    return PortableServer::POA::_duplicate( poa );
}


char* BodyInfo_impl::name()
{
    return CORBA::string_dup(name_.c_str());
}

char* BodyInfo_impl::url()
{
    return CORBA::string_dup(url_.c_str());
}

StringSequence* BodyInfo_impl::info()
{
    return new StringSequence(info_);
}

LinkInfoSequence* BodyInfo_impl::links()
{
    return new LinkInfoSequence(links_);
}

AllLinkShapeIndices* BodyInfo_impl::linkShapeIndices()
{
    return new AllLinkShapeIndices( linkShapeIndices_ );
}

ShapeInfoSequence* BodyInfo_impl::shapes()
{
    return new ShapeInfoSequence( shapes_ );
}

AppearanceInfoSequence* BodyInfo_impl::appearances()
{
    return new AppearanceInfoSequence( appearances_ );
}

MaterialInfoSequence* BodyInfo_impl::materials()
{
    return new MaterialInfoSequence( materials_ );
}

TextureInfoSequence* BodyInfo_impl::textures()
{
    return new TextureInfoSequence( textures_ );
}

void BodyInfo_impl::putMessage( const std::string& message )
{
  cout << message;
}




//==================================================================================================
/*!
  @if jp

    @brief		URL�X�L�[��(file:)��������폜

	@note       <BR>

	@return	    string URL�X�L�[�����������菜����������

  @endif
*/
//==================================================================================================
string
BodyInfo_impl::deleteURLScheme(
	string url )	//!< URL�p�X������
{
	// URL scheme ����菜��
    static const string fileProtocolHeader1("file:///");
	static const string fileProtocolHeader2("file://");
	static const string fileProtocolHeader3("file:");

    size_t pos = url.find( fileProtocolHeader1 );
    if( 0 == pos )
	{
		url.erase( 0, fileProtocolHeader1.size() );
    }
	else
	{
	    size_t pos = url.find( fileProtocolHeader2 );
    	if( 0 == pos )
		{
			url.erase( 0, fileProtocolHeader2.size() );
    	}
		else
		{
			size_t pos = url.find( fileProtocolHeader3 );
    		if( 0 == pos )
			{
				url.erase( 0, fileProtocolHeader3.size() );
    		}
		}
    }

	return url;
}

//==================================================================================================
/*!
  @if jp

    @brief		������u��

	@note       <BR>

	@return	    str ���� ���蕶����@sb �� �ʂ̕�����@sa �ɒu��


  @endif
*/
//==================================================================================================
string&
BodyInfo_impl::replace(string& str, const string sb, const string sa)
{
	string::size_type n, nb = 0;
	
	while ((n = str.find(sb,nb)) != string::npos)
	{
		str.replace(n,sb.size(),sa);
		nb = n + sa.size();
	}
	
	return str;
}




//==================================================================================================
/*!
  @if jp
	@brief		���f���t�@�C���̃��[�h
	@note		BodyInfo���\�z����B
	@return	    void
  @else
	@brief		load model file
	@note		Constructs a BodyInfo (a CORBA interface)
	@return	    void
  @endif
*/
//==================================================================================================
void
BodyInfo_impl::loadModelFile(
	const std::string& url )	//!< ���f���t�@�C���p�X������ (URL)
{
	// URL scheme ����菜�����t�@�C���p�X���Z�b�g����
    string filename( deleteURLScheme( url ) );

	// URL�������' \' ��؂�q��'/' �ɒu������  Windows �t�@�C���p�X�Ή� 
	string url2;
	url2 = filename;
	replace( url2, string("\\"), string("/") );
	filename = url2;

    ModelNodeSet modelNodeSet;
    modelNodeSet.signalOnStatusMessage.connect(bind(&BodyInfo_impl::putMessage, this, _1));
	modelNodeSet.setMessageOutput( true );


    try
	{
    	modelNodeSet.loadModelFile( filename );
		cout.flush();
    }
    catch(ModelNodeSet::Exception& ex)
	{
		throw ModelLoader::ModelLoaderException(ex.message.c_str());
    }

	// BodyInfo�����o�ɒl���Z�b�g����
    const string& humanoidName = modelNodeSet.humanoidNode()->defName;
    name_ = CORBA::string_dup(humanoidName.c_str());

    url_ = CORBA::string_dup(url2.c_str());

	// JointNode�����擾����
    int numJointNodes = modelNodeSet.numJointNodes();

	// links_, linkShapeIndices_ �z��T�C�Y���m�ۂ���
    links_.length(numJointNodes);
	linkShapeIndices_.length( numJointNodes );

    if( 0 < numJointNodes )
	{
		int currentIndex = 0;

		// JointNode ���ċA�I�ɒH��CLinkInfo�𐶐�����
		JointNodeSetPtr rootJointNodeSet = modelNodeSet.rootJointNodeSet();
		readJointNodeSet( rootJointNodeSet, currentIndex, -1 );

		// AllLinkShapeIndices ���\�z����
		// links_����linkInfo��H��C
		for( size_t i = 0 ; i < numJointNodes ; ++i )
		{
			// linkInfo�̃����oshapeIndices��linkShapeIndices�֑������
			linkShapeIndices_[i] = links_[i].shapeIndices;
		}
    }
}




//==================================================================================================
/*!
  @if jp
	@brief		read JointNodeSet
	@note		Constructs a BodyInfo (a CORBA interface) <BR>
				During construction of the BodyInfo, LinkInfo, ShapeInfo, AppearanceInfo, 
				MaterialInfo, and TextureInfo structures are constructed.
	@return	    int
  @endif
*/
//==================================================================================================
int BodyInfo_impl::readJointNodeSet(
	JointNodeSetPtr		jointNodeSet,	//!< �ΏۂƂȂ� JointNodeSet
	int&				currentIndex,	//!< ����JointNodeSet��index
	int					parentIndex )	//!< �eNode��index
{
	int index = currentIndex;
	currentIndex++;

	LinkInfo_var linkInfo( new LinkInfo() );
	linkInfo->parentIndex = parentIndex;

	// �qJointNode�����擾����
    size_t numChildren = jointNodeSet->childJointNodeSets.size();

	// �qJointNode�����ɒH��
	for( size_t i = 0 ; i < numChildren ; ++i )
	{
		// �e�q�֌W�̃����N�𐶐�����
		JointNodeSetPtr childJointNodeSet = jointNodeSet->childJointNodeSets[i];
		int childIndex = readJointNodeSet( childJointNodeSet, currentIndex, index );

		// chidlIndices �� childIndex ��ǉ�����
		long childIndicesLength = linkInfo->childIndices.length();
		linkInfo->childIndices.length( childIndicesLength + 1 );
		linkInfo->childIndices[childIndicesLength] = childIndex;
    }

	// links_ �̓K�؂Ȉʒu(index)�֊i�[����
	links_[index] = linkInfo;

	try
	{
		matrix44d unit4d( tvmet::identity<matrix44d>() );

		// JointNodeSet �� segmentNode
		traverseShapeNodes( index, jointNodeSet->segmentNode->fields["children"].mfNode(), unit4d );

		setJointParameters( index, jointNodeSet->jointNode );
		setSegmentParameters( index, jointNodeSet->segmentNode );
		setSensors( index, jointNodeSet );
    }

	catch( ModelLoader::ModelLoaderException& ex )
	{
		//string name = linkInfo->name;
		CORBA::String_var cName = linkInfo->name;
		string name( cName );
		string error = name.empty() ? "Unnamed JoitNode" : name;
		error += ": ";
		error += ex.description;
		throw ModelLoader::ModelLoaderException( error.c_str() );
    }

    return index;
}




//==================================================================================================
/*!
  @if jp

    @brief		LinkInfo��JointNode�̃p�����[�^�ݒ�

	@note       <BR>

    @date       2008-03-11 Y.TSUNODA <BR>

    @return     void

  @endif
*/
//==================================================================================================
void
BodyInfo_impl::setJointParameters(
	int linkInfoIndex,					//!< LinkInfo�C���f�b�N�X (links_�̃C���f�b�N�X)
	VrmlProtoInstancePtr jointNode )	//!< JointNode�I�u�W�F�N�g�ւ̃|�C���^
{
	// �ΏۂƂȂ� linkInfo�C���X�^���X�ւ̎Q��
	LinkInfo& linkInfo = links_[linkInfoIndex];

	linkInfo.name =  CORBA::string_dup( jointNode->defName.c_str() );

    TProtoFieldMap& fmap = jointNode->fields;

	CORBA::Long jointId;
	copyVrmlField( fmap, "jointId", jointId );
	linkInfo.jointId = (CORBA::Short)jointId; 

	linkInfo.jointAxis[0] = 0.0;
	linkInfo.jointAxis[1] = 0.0;
	linkInfo.jointAxis[2] = 0.0;
    
    VrmlVariantField& fJointAxis = fmap["jointAxis"];

    switch( fJointAxis.typeId() )
	{

    case SFSTRING:
		{
			SFString& axisLabel = fJointAxis.sfString();
			if( axisLabel == "X" )		{ linkInfo.jointAxis[0] = 1.0; }
			else if( axisLabel == "Y" )	{ linkInfo.jointAxis[1] = 1.0; }
			else if( axisLabel == "Z" ) { linkInfo.jointAxis[2] = 1.0; }
		}
		break;
		
    case SFVEC3F:
		copyVrmlField( fmap, "jointAxis", linkInfo.jointAxis );
		break;

    default:
		break;
    }

	std::string jointType;
    copyVrmlField( fmap, "jointType", jointType );
	linkInfo.jointType = CORBA::string_dup( jointType.c_str() );

	copyVrmlField( fmap, "translation", linkInfo.translation );
    copyVrmlRotationFieldToDblArray4( fmap, "rotation", linkInfo.rotation );

    copyVrmlField( fmap, "ulimit",  linkInfo.ulimit );
    copyVrmlField( fmap, "llimit",  linkInfo.llimit );
    copyVrmlField( fmap, "uvlimit", linkInfo.uvlimit );
    copyVrmlField( fmap, "lvlimit", linkInfo.lvlimit );

    copyVrmlField( fmap, "gearRatio",     linkInfo.gearRatio );
    copyVrmlField( fmap, "rotorInertia",  linkInfo.rotorInertia );
	copyVrmlField( fmap, "rotorResistor", linkInfo.rotorResistor );
    copyVrmlField( fmap, "torqueConst",   linkInfo.torqueConst );
    copyVrmlField( fmap, "encoderPulse",  linkInfo.encoderPulse );
	copyVrmlField( fmap, "jointValue",    linkInfo.jointValue );

	// equivalentInertia �͔p�~
}





//==================================================================================================
/*!
  @if jp

    @brief		LinkInfo��SegmentNode�̃p�����[�^�ݒ�

	@note       <BR>

    @date       2008-03-11 Y.TSUNODA <BR>

    @return     void

  @endif
*/
//==================================================================================================
void BodyInfo_impl::setSegmentParameters(
	int linkInfoIndex,					//!< LinkInfo�C���f�b�N�X (links_�̃C���f�b�N�X)
	VrmlProtoInstancePtr segmentNode )	//!< SegmentNode�I�u�W�F�N�g�ւ̃|�C���^
{
	// �ΏۂƂȂ� linkInfo�C���X�^���X�ւ̎Q��
	LinkInfo& linkInfo = links_[linkInfoIndex];

	if( segmentNode )
	{
		TProtoFieldMap& fmap = segmentNode->fields;
		
		copyVrmlField( fmap, "centerOfMass",     linkInfo.centerOfMass );
		copyVrmlField( fmap, "mass",             linkInfo.mass );
		copyVrmlField( fmap, "momentsOfInertia", linkInfo.inertia );
	}
	else
	{
		linkInfo.mass = 0.0;
		// set zero to centerOfMass and inertia
		for( int i = 0 ; i < 3 ; ++i )
		{
			linkInfo.centerOfMass[i] = 0.0;
			for( int j = 0 ; j < 3 ; ++j )
			{
				linkInfo.inertia[i*3 + j] = 0.0;
			}
		}
	}
}





//==================================================================================================
/*!
  @if jp

    @brief		SensorInfo����

	@note       <BR>

    @date       2008-03-11 Y.TSUNODA <BR>

    @return     void

  @endif
*/
//==================================================================================================
void
BodyInfo_impl::setSensors(
	int linkInfoIndex,				//!< LinkInfo�C���f�b�N�X (links_�̃C���f�b�N�X)
	JointNodeSetPtr jointNodeSet )	//!< JointNodeSet�I�u�W�F�N�g�ւ̃|�C���^
{
	// �ΏۂƂȂ� linkInfo�C���X�^���X�ւ̎Q��
	LinkInfo& linkInfo = links_[linkInfoIndex];

	vector<VrmlProtoInstancePtr>& sensorNodes = jointNodeSet->sensorNodes;

	int numSensors = sensorNodes.size();
	linkInfo.sensors.length( numSensors );

	for( int i = 0 ; i < numSensors ; ++i )
	{
		SensorInfo_var sensorInfo( new SensorInfo() );

		readSensorNode( linkInfoIndex, sensorInfo, sensorNodes[i] );

		linkInfo.sensors[i] = sensorInfo;
	}
}





//==================================================================================================
/*!
  @if jp

    @brief		SensorInfo��SensorNode�̃p�����[�^�ݒ�

	@note       <BR>

    @date       2008-03-11 Y.TSUNODA <BR>

    @return     void

  @endif
*/
//==================================================================================================
void
BodyInfo_impl::readSensorNode(
	int linkInfoIndex,					//!< LinkInfo�C���f�b�N�X (links_�̃C���f�b�N�X)
	SensorInfo& sensorInfo,				//!< SensorInfo�I�u�W�F�N�g(�p�����[�^���������)
	VrmlProtoInstancePtr sensorNode )	//!< SensorNode�I�u�W�F�N�g�ւ̃|�C���^
{
	if( sensorTypeMap.empty() )
	{
		// initSensorTypeMap();
		sensorTypeMap["ForceSensor"]		= "Force";
		sensorTypeMap["Gyro"]				= "RateGyro";
		sensorTypeMap["AccelerationSensor"]	= "Acceleration";
		sensorTypeMap["PressureSensor"]		= "";
		sensorTypeMap["PhotoInterrupter"]	= "";
		sensorTypeMap["VisionSensor"]		= "Vision";
		sensorTypeMap["TorqueSensor"]		= "";
	}

	try
	{
		sensorInfo.name = CORBA::string_dup( sensorNode->defName.c_str() );

		TProtoFieldMap& fmap = sensorNode->fields;

		copyVrmlField( fmap, "sensorId", sensorInfo.id );

		copyVrmlField( fmap, "translation", sensorInfo.translation );
		copyVrmlRotationFieldToDblArray4( fmap, "rotation", sensorInfo.rotation );

		SensorTypeMap::iterator p = sensorTypeMap.find( sensorNode->proto->protoName );
		std::string sensorType;
		if( p != sensorTypeMap.end() )
		{
			sensorType = p->second;
			sensorInfo.type = CORBA::string_dup( sensorType.c_str() );
		}
		else
		{
			throw ModelLoader::ModelLoaderException("Unknown Sensor Node");
		}

		if( sensorType == "Force" )
		{
			sensorInfo.specValues.length( CORBA::ULong(6) );
			DblArray3 maxForce, maxTorque;
			copyVrmlField( fmap, "maxForce", maxForce );
			copyVrmlField( fmap, "maxTorque", maxTorque );
			sensorInfo.specValues[0] = maxForce[0];
			sensorInfo.specValues[1] = maxForce[1];
			sensorInfo.specValues[2] = maxForce[2];
			sensorInfo.specValues[3] = maxTorque[0];
			sensorInfo.specValues[4] = maxTorque[1];
			sensorInfo.specValues[5] = maxTorque[2];

		}
		else if( sensorType == "RateGyro" )
		{
			sensorInfo.specValues.length( CORBA::ULong(3) );
			DblArray3 maxAngularVelocity;
			copyVrmlField(fmap, "maxAngularVelocity", maxAngularVelocity);
			sensorInfo.specValues[0] = maxAngularVelocity[0];
			sensorInfo.specValues[1] = maxAngularVelocity[1];
			sensorInfo.specValues[2] = maxAngularVelocity[2];

		}
		else if( sensorType == "Acceleration" )
		{
			sensorInfo.specValues.length( CORBA::ULong(3) );
			DblArray3 maxAcceleration;
			copyVrmlField(fmap, "maxAcceleration", maxAcceleration);
			sensorInfo.specValues[0] = maxAcceleration[0];
			sensorInfo.specValues[1] = maxAcceleration[1];
			sensorInfo.specValues[2] = maxAcceleration[2];

		}
		else if( sensorType == "Vision" )
		{
			sensorInfo.specValues.length( CORBA::ULong(6) );

			CORBA::Double specValues[3];
			copyVrmlField( fmap, "frontClipDistance", specValues[0] );
			copyVrmlField( fmap, "backClipDistance", specValues[1] );
			copyVrmlField( fmap, "fieldOfView", specValues[2] );
			sensorInfo.specValues[0] = specValues[0];
			sensorInfo.specValues[1] = specValues[1];
			sensorInfo.specValues[2] = specValues[2];

			std::string sensorTypeString;
			copyVrmlField( fmap, "type", sensorTypeString );
		    
			if( sensorTypeString=="NONE" )				{ sensorInfo.specValues[3] = Camera::NONE;		}
			else if( sensorTypeString=="COLOR" )		{ sensorInfo.specValues[3] = Camera::COLOR;		}
			else if( sensorTypeString=="MONO" )			{ sensorInfo.specValues[3] = Camera::MONO;		}
			else if( sensorTypeString=="DEPTH" )		{ sensorInfo.specValues[3] = Camera::DEPTH;		}
			else if( sensorTypeString=="COLOR_DEPTH" )	{ sensorInfo.specValues[3] = Camera::COLOR_DEPTH; }
			else if( sensorTypeString=="MONO_DEPTH" )	{ sensorInfo.specValues[3] = Camera::MONO_DEPTH; }
			else
			{
				throw ModelLoader::ModelLoaderException("Sensor node has unkown type string");
			}

			CORBA::Long width, height;
			copyVrmlField( fmap, "width", width );
			copyVrmlField( fmap, "height", height );

			sensorInfo.specValues[4] = static_cast<CORBA::Double>(width);
			sensorInfo.specValues[5] = static_cast<CORBA::Double>(height);
		}


		// rotation���h���Q�X�̉�]��
		vector3d vRotation( sensorInfo.rotation[0], sensorInfo.rotation[1], sensorInfo.rotation[2] );

		// ���h���Q�Xrotation��3x3�s��ɕϊ�����
		matrix33d mRotation;
		PRIVATE::rodrigues( mRotation, vRotation, sensorInfo.rotation[3] );

		// rotation, translation ��4x4�s��ɑ������
		matrix44d mTransform( tvmet::identity<matrix44d>() );
		mTransform =
			mRotation(0,0), mRotation(0,1), mRotation(0,2), sensorInfo.translation[0],
			mRotation(1,0), mRotation(1,1), mRotation(1,2), sensorInfo.translation[1],
			mRotation(2,0), mRotation(2,1), mRotation(2,2), sensorInfo.translation[2],
			0.0,            0.0,            0.0,		    1.0;

		// 
		if( NULL != sensorNode->getField( "children" ) )
		{
			traverseShapeNodes( linkInfoIndex, sensorNode->fields["children"].mfNode(), mTransform );
		}
    }
    catch(ModelLoader::ModelLoaderException& ex)
	{
		string error = name_.empty() ? "Unnamed sensor node" : name_;
		error += ": ";
		error += ex.description;
		throw ModelLoader::ModelLoaderException( error.c_str() );
    }
}




//==================================================================================================
/*!
  @if jp

    @brief		Shape �m�[�h�T���̂��߂̍ċA�֐�

	@note       �q�m�[�h�I�u�W�F�N�g��H�� ShapeInfo�𐶐�����B<BR>
                ��������ShapeInfo��BodyInfo��shapes_�ɒǉ�����B<BR>
                shapes_�ɒǉ������ʒu(index)�� LinkInfo��shapeIndices�ɒǉ�����B<BR>

    @date       2008-03-11 Y.TSUNODA <BR>

	@return	    void

  @endif
*/
//==================================================================================================
void
BodyInfo_impl::traverseShapeNodes(
	int linkInfoIndex,				//!< links_ ��index (����linkInfo�ɊY������ShapeInfo�ł���)
	MFNode& childNodes,				//!< �qNode
	matrix44d mTransform )			//!< ��]�E���i 4x4�s��
{
	// �ΏۂƂȂ� linkInfo�C���X�^���X�ւ̎Q��
	LinkInfo& linkInfo = links_[linkInfoIndex];

	for( size_t i = 0 ; i < childNodes.size() ; ++i )
	{
		VrmlNodePtr node = childNodes[i];

		// Group�m�[�h�Ƃ�����p�������m�[�h�̏ꍇ���A�q�m�[�h��H���Ă���
		if( node->isCategoryOf( GROUPING_NODE ) )
		{
			VrmlGroupPtr group = static_pointer_cast<VrmlGroup>( node );

			matrix44d mCurrentTransform( tvmet::identity<matrix44d>() );	// Transform�Őݒ肳�ꂽ��]�E���i���������������s��

			// Transform�m�[�h�ł��邩�̔���B
			//   GROUPING_NODE�ȂǁA�m�[�h�̊�{�ƂȂ�J�e�S���� isCategoryOf() �Ŕ���ł���悤�ɂ��Ă��邪�A
			//   ���̂Ƃ���Transform�ł��邩�ǂ����͂��̂悤�Ȋ�{�J�e�S���Ƃ��Ă��Ȃ�
			if( VrmlTransformPtr transform = dynamic_pointer_cast<VrmlTransform>( group ) )
			{
				// ���̃m�[�h�Őݒ肳�ꂽ transform (scale���܂�)���v�Z���C4x4�̍s��ɑ������
				matrix44d mThisTransform;
				_calcTransform( transform, mThisTransform );

				// �e�m�[�h�Őݒ肳�ꂽ��]�E���i�����ƍ�������
				mCurrentTransform = mTransform * mThisTransform;
			}
			
			// �q�m�[�h�̒T��
			traverseShapeNodes( linkInfoIndex, group->children, mCurrentTransform );
		}
		// Shape�m�[�h�ł��邩�̔���
		else if( node->isCategoryOf( SHAPE_NODE ) )
		{
			short shapeInfoIndex;		// shapeInfoVec(shape_)����index

			// shapeInfo���L�}�b�v�ɂ���node���o�^����Ă��邩��������
			SharedShapeInfoMap::iterator itr = sharedShapeInfoMap.find( node );

			// ����node�ŁCtransform���������̂��o�^����Ă����
			if( sharedShapeInfoMap.end() != itr 
			&& ( itr->second.transform != mTransform ) )
			{
				// �C���f�b�N�X���擾����
				shapeInfoIndex = itr->second.index;
			}
			// �o�^����Ă��Ȃ���΁C
			else
			{
				// ���`�����CShapeInfo �𐶐�����
				UniformedShape uniformShape;
				uniformShape.signalOnStatusMessage.connect( bind( &BodyInfo_impl::putMessage, this, _1 ) );
				uniformShape.setMessageOutput( true );

				if( !uniformShape.uniform( node ) )
                {
                    // ���`�����Ɏ��s�����̂�ShapeInfo�͐������Ȃ�
                    continue;
                };

                // ���`�������ʂ��i�[
                ShapeInfo_var   shapeInfo( new ShapeInfo );

				// ���_�E���b�V����������
                _setVertices( shapeInfo, uniformShape.getVertexList(), mTransform );
				_setTriangles( shapeInfo, uniformShape.getTriangleList() );

				// PrimitiveType��������
				_setShapeInfoType( shapeInfo, uniformShape.getShapeType() );

				// AppearanceInfo
				{
					VrmlShapePtr shapeNode = static_pointer_cast<VrmlShape>( node );
					VrmlAppearancePtr appearanceNode = shapeNode->appearance;
					if( NULL != appearanceNode )
					{
						AppearanceInfo_var appearance( new AppearanceInfo() );
						//appearance->creaseAngle = 0.0;
						appearance->creaseAngle = 3.14;		// 2008.05.11 Changed. �v���~�e�B�u�`�� CreaseAngle�f�t�H���g�l

						// IndexedFaceSet�̏ꍇ
						if( UniformedShape::S_INDEXED_FACE_SET == uniformShape.getShapeType() )
						{
							VrmlIndexedFaceSetPtr faceSet = static_pointer_cast<VrmlIndexedFaceSet>( shapeNode->geometry );

							appearance->coloerPerVertex = faceSet->colorPerVertex;
							
							if( NULL != faceSet->color )
							{
								size_t colorNum = faceSet->color->color.size();
								appearance->colors.length( colorNum * 3 );
								for( size_t i = 0 ; i < colorNum ; ++i )
								{
									SFColor color = faceSet->color->color[i];
									appearance->colors[3*i+0] = color[0];
									appearance->colors[3*i+1] = color[1];
									appearance->colors[3*i+2] = color[2];
								}
							}

							size_t colorIndexNum = faceSet->colorIndex.size();
							appearance->colorIndices.length( colorIndexNum );
							for( size_t i = 0 ; i < colorIndexNum ; ++i )
							{
								appearance->colorIndices[i] = faceSet->colorIndex[i];
							}

							appearance->normalPerVertex = faceSet->normalPerVertex;
							appearance->solid = faceSet->solid;
							appearance->creaseAngle = faceSet->creaseAngle;

							// ##### [TODO] #####
							//appearance->textureCoordinate = faceSet->texCood;

							_setNormals( appearance, uniformShape.getVertexList(), uniformShape.getTriangleList(), mTransform );

						}
						// ElevationGrid�̏ꍇ
						else if( UniformedShape::S_ELEVATION_GRID == uniformShape.getShapeType() )
						{
							VrmlElevationGridPtr elevationGrid = static_pointer_cast<VrmlElevationGrid>( shapeNode->geometry );

							appearance->coloerPerVertex = elevationGrid->colorPerVertex;
							
							if( NULL != elevationGrid->color )
							{
								size_t colorNum = elevationGrid->color->color.size();
								appearance->colors.length( colorNum * 3 );
								for( size_t i = 0 ; i < colorNum ; ++i )
								{
									SFColor color = elevationGrid->color->color[i];
									appearance->colors[3*i+0] = color[0];
									appearance->colors[3*i+1] = color[1];
									appearance->colors[3*i+2] = color[2];
								}
							}

							// appearance->colorIndices // ElevationGrid �̃����o�ɂ͖���

							appearance->normalPerVertex = elevationGrid->normalPerVertex;
							appearance->solid = elevationGrid->solid;
							appearance->creaseAngle = elevationGrid->creaseAngle;

							// ##### [TODO] #####
							//appearance->textureCoordinate = elevationGrid->texCood;

							_setNormals( appearance, uniformShape.getVertexList(), uniformShape.getTriangleList(), mTransform );
						}
						// Box�̏ꍇ
						else if( UniformedShape::S_BOX == uniformShape.getShapeType() )
						{
							appearance->creaseAngle = (float)(3.14 / 2);
						}
						// Cone�̏ꍇ
						else if( UniformedShape::S_CONE == uniformShape.getShapeType() )
						{
							appearance->creaseAngle = (float)(3.14 / 2);
						}
						// Cylinder�̏ꍇ
						else if( UniformedShape::S_CYLINDER == uniformShape.getShapeType() )
						{
							appearance->creaseAngle = (float)(3.14 / 2);
						}
						// Sphere�̏ꍇ
						else if( UniformedShape::S_SPHERE == uniformShape.getShapeType() )
						{
							appearance->creaseAngle = (float)(3.14 / 2);
						}
						// Extrusion�̏ꍇ
						else if( UniformedShape::S_EXTRUSION == uniformShape.getShapeType() )
						{
							appearance->creaseAngle = 3.14;
						}


						// MaterialInfo 
						//   material�m�[�h�����݂���΁CMaterialInfo�𐶐��Cmaterials_�Ɋi�[����
						appearance->materialIndex = _createMaterialInfo( appearanceNode->material );


                        // TextureInfo
						//   texture�m�[�h�����݂���΁CTextureInfo�𐶐��Ctextures_�Ɋi�[����
						appearance->textureIndex = _createTextureInfo( appearanceNode->texture );


						long appearancesLength	= appearances_.length();
						appearances_.length( appearancesLength + 1 );
						appearances_[appearancesLength] = appearance;

						// ShapeInfo��appearanceIndex�ɃC���f�b�N�X����
						shapeInfo->appearanceIndex = appearancesLength;
					}
					else
					{
						shapeInfo->appearanceIndex = -1;
					}
				}

				// shapes_�̍Ō�ɒǉ�����
				int shapesLength = shapes_.length();
				shapes_.length( shapesLength + 1 );
				shapes_[shapesLength] = shapeInfo;

				// shapes_����index ��
				shapeInfoIndex = shapesLength;

				// shapeInfo���L�}�b�v�� ����shapeInfo(node)��index,transform�̏�����o�^(�}��)����
				ShapeObject shapeObject;
				shapeObject.index = shapeInfoIndex;
				shapeObject.transform = mTransform;
				sharedShapeInfoMap.insert( pair<OpenHRP::VrmlNodePtr, ShapeObject>( node, shapeObject ) );
			}

			// index�� LinkInfo �� shapeIndices �ɒǉ�����
			long shapeIndicesLength = linkInfo.shapeIndices.length();
			linkInfo.shapeIndices.length( shapeIndicesLength + 1 );
			linkInfo.shapeIndices[shapeIndicesLength] = shapeInfoIndex;
		}
	}
}




//==================================================================================================
/*!
  @if jp

    @brief      ���_���W��ShapeInfo.vertices�ɑ��

    @note       ���_���X�g�Ɋi�[����Ă��钸�_���W��ShapeInfo.vertices�ɑ������ <BR>
                mTransform�Ƃ��ė^����ꂽ��]�E���i������S�Ă̒��_�ɔ��f���� <BR>

    @date       2008-03-10 Y.TSUNODA <BR>
	            2008-04-11 Y.TSUNODA ���_���W��4�����x�N�g���ɂ��Čv�Z <BR>

    @return     void

  @endif
*/
//==================================================================================================
void
BodyInfo_impl::_setVertices(
    ShapeInfo_var& shape,           //!< �l�ߍ��ݑΏ�
	vector<vector3d> vList,			//!< ���_���W���X�g
	matrix44d mTransform )			//!< ��]�E���i����
{
	// ���_�����擾����
	size_t vertexNumber = vList.size();

	// ���_���W���i�[����z��T�C�Y���w�肷��
	shape->vertices.length( vertexNumber * 3 );

	int i = 0;
	for( size_t v = 0 ; v < vertexNumber ; v++ )
	{
		vector4d vertex4;			// ��]�E���i�O�̃x�N�g��(���W)	
		vector4d transformed;		// ��]�E���i��̃x�N�g��(���W)
		
		// ���_���W��4�����x�N�g���ɑ������
		vertex4 = vList.at( v )[0], vList.at( v )[1], vList.at( v )[2], 1; 
		
		// ��]�E���i�v�Z
		transformed = mTransform * vertex4;

		// ShapeInfo��vertices�ɑ������
		shape->vertices[i] = transformed[0]; i++;
		shape->vertices[i] = transformed[1]; i++;
		shape->vertices[i] = transformed[2]; i++;
	}
}




//==================================================================================================
/*!
  @if jp

    @brief      �O�p���b�V������ShapeInfo.triangles�ɑ��

    @note       �O�p���b�V�����X�g�Ɋi�[����Ă���O�p���b�V������ShapeInfo.triangles�ɑ������ <BR>

    @date       2008-03-10 Y.TSUNODA <BR>

    @return     void

  @endif
*/
//==================================================================================================
void
BodyInfo_impl::_setTriangles(
	ShapeInfo_var& shape,				//!< �Ώۂ�ShapeInfo
	vector<vector3i> tList )			//!< ���b�V�����X�g
{
	// ���b�V�������擾����
	size_t triangleNumber = tList.size();

	// ���b�V�����i�[����z��T�C�Y���w�肷��
	shape->triangles.length( triangleNumber * 3 );
	
	int i = 0;
	for( size_t t = 0 ; t < triangleNumber ; t++ )
	{
		shape->triangles[i] = ( tList.at( t ) )[0]; i++;
		shape->triangles[i] = ( tList.at( t ) )[1]; i++;
		shape->triangles[i] = ( tList.at( t ) )[2]; i++;
	}
}




//==================================================================================================
/*!
  @if jp

    @brief      �@�����v�Z��AppearanceInfo�ɑ��

    @note       ���_���X�g�E�O�p���b�V�����X�g����@�����v�Z���CAppearanceInfo�ɑ������<BR>

    @date       2008-04-11 Y.TSUNODA <BR>

    @return     void

  @endif
*/
//==================================================================================================
void
BodyInfo_impl::_setNormals(
	AppearanceInfo_var& appearance,		//!< �v�Z���ʂ̖@����������AppearanceInfo
	vector<vector3d> vertexList,		//!< ���_���X�g
	vector<vector3i> traiangleList,		//!< �O�p���b�V�����X�g
	matrix44d mTransform )				//!< ��]�E���i����
{
	// ���_���X�g���̒��_���W���ꂼ��ɁC��]�E���i�������|����
	vector<vector3d> transformedVertexList;	// ��]�E���i�v�Z��̒��_���X�g
	vector4d vertex4;						// ��]�E���i�O�̃x�N�g��(���W)	
	vector4d transformed4;					// ��]�E���i��̃x�N�g��(���W)
	vector3d transformed;					//    �V

	for( size_t v = 0 ; v < vertexList.size() ; ++v )
	{
		// ���_���W��4�����x�N�g���ɑ������
		vertex4 = vertexList.at( v )[0], vertexList.at( v )[1], vertexList.at( v )[2], 1; 
		
		// ��]�E���i�v�Z
		transformed4 = mTransform * vertex4;

		transformed = transformed4[0], transformed4[1], transformed4[2];
		transformedVertexList.push_back( transformed );
	}

	CalculateNormal calculateNormal;

	// ���b�V���̖@��(�ʂ̖@��)���v�Z����
	calculateNormal.calculateNormalsOfMesh( transformedVertexList, traiangleList );


	// normalPerVertex == TRUE �Ȃ̂ŁC���_�̖@��
	if( true == appearance->normalPerVertex )
	{
		calculateNormal.calculateNormalsOfVertex( transformedVertexList, traiangleList, appearance->creaseAngle );

		vector<vector3d> normalsVertex = calculateNormal.getNormalsOfVertex();
		vector<vector3i> normalIndex = calculateNormal.getNormalIndex();

		// �@���f�[�^��������
		size_t normalsVertexNum = normalsVertex.size();
		appearance->normals.length( normalsVertexNum * 3 );

		for( size_t i = 0 ; i < normalsVertexNum ; ++i )
		{
			// �@���x�N�g���𐳋K������
			vector3d normal = static_cast<vector3d>( tvmet::normalize( normalsVertex.at( i ) ) );

			// AppearanceInfo �̃����o�ɑ������
			appearance->normals[3*i+0] = normal[0];
			appearance->normals[3*i+1] = normal[1];
			appearance->normals[3*i+2] = normal[2];
		}

		// �@���Ή��t���f�[�^(�C���f�b�N�X��)��������
		size_t normalIndexNum = normalIndex.size();
		appearance->normalIndices.length( normalIndexNum * 4 );

		for( size_t i = 0 ; i < normalIndexNum ; ++i )
		{
			appearance->normalIndices[4*i+0] = normalIndex.at( i )[0];
			appearance->normalIndices[4*i+1] = normalIndex.at( i )[1];
			appearance->normalIndices[4*i+2] = normalIndex.at( i )[2];
			appearance->normalIndices[4*i+3] = -1;
		}
	}
	// �ʂ̖@��
	else
	{
		// �Z�o�����ʂ̖@��(��vector:�z��)���擾����
		vector<vector3d> normalsMesh = calculateNormal.getNormalsOfMesh();

		// �ʂ̖@���f�[�^�����擾����
		size_t normalsMeshNum = normalsMesh.size();

		// �������@���C�@���C���f�b�N�X��vector(�z��)�T�C�Y���w�肷��
		appearance->normals.length( normalsMeshNum * 3 );
		appearance->normalIndices.length( normalsMeshNum );

		for( size_t i = 0 ; i < normalsMeshNum ; ++i )
		{
			// �@���x�N�g���𐳋K������
			vector3d normal = static_cast<vector3d>( tvmet::normalize( normalsMesh.at( i ) ) );

			// AppearanceInfo �̃����o�ɑ������
			appearance->normals[3*i+0] = normal[0];
			appearance->normals[3*i+1] = normal[1];
			appearance->normals[3*i+2] = normal[2];

			appearance->normalIndices[i] = i;
		}
	}
}




//==================================================================================================
/*!
  @if jp

    @brief      ShapeInfo��PrimitiveType����

    @note       <BR>

    @date       2008-04-11 Y.TSUNODA <BR>

    @return     void

  @endif
*/
//==================================================================================================
void
BodyInfo_impl::_setShapeInfoType(
	ShapeInfo_var& shapeInfo,					//!< �Ώۂ�ShapeInfo
	UniformedShape::ShapePrimitiveType type )	//!< ShapeType
{
    switch( type )
    {
    case UniformedShape::S_BOX:
        shapeInfo->type = BOX;
        break;
    case UniformedShape::S_CONE:
        shapeInfo->type = CONE;
        break;
    case UniformedShape::S_CYLINDER:
        shapeInfo->type = CYLINDER;
        break;
    case UniformedShape::S_SPHERE:
        shapeInfo->type = SPHERE;
        break;
    case UniformedShape::S_INDEXED_FACE_SET:
    case UniformedShape::S_ELEVATION_GRID:
    case UniformedShape::S_EXTRUSION:
        shapeInfo->type = MESH;
        break;
    }
}




//==================================================================================================
/*!
  @if jp

    @brief		TextureInfo����

	@note       texture�m�[�h�����݂���΁CTextureInfo�𐶐��Ctextures_ �ɒǉ�����B<BR>
				�Ȃ��CImageTexture�m�[�h�̏ꍇ�́CPixelTexture�ɕϊ��� TextureInfo�𐶐�����B<BR>
                textures_�ɒǉ������ʒu(�C���f�b�N�X)��߂�l�Ƃ��ĕԂ��B<BR>

    @date       2008-04-18 Y.TSUNODA <BR>

	@return	    long  TextureInfo(textures_)�̃C���f�b�N�X�Ctexture�m�[�h�����݂��Ȃ��ꍇ�� -1

  @endif
*/
//==================================================================================================
long
BodyInfo_impl::_createTextureInfo( 
	VrmlTexturePtr textureNode )
{
	if( ! textureNode )	return -1;

	VrmlPixelTexturePtr pixelTextureNode = NULL;
	VrmlImageTexturePtr imageTextureNode = dynamic_pointer_cast<VrmlImageTexture>( textureNode );

	// ImageTexture���ǂ����̔��f
	if( imageTextureNode )
	{
		ImageConverter  converter;

		VrmlPixelTexture* tempTexture = new VrmlPixelTexture;

		// PixelTexture�ɕϊ�����
		if( converter.convert( *imageTextureNode, *tempTexture, _getModelFileDirPath() ) )
		{
			pixelTextureNode = tempTexture;
		}
	}
	// ImageTexture�łȂ����PixelTexture���ǂ���
	else
	{
		pixelTextureNode = dynamic_pointer_cast<VrmlPixelTexture>( textureNode );
	}

	if( pixelTextureNode )
	{
		TextureInfo_var texture( new TextureInfo() );

		texture->height = pixelTextureNode->image.height;
		texture->width =pixelTextureNode->image.width;
		texture->numComponents = pixelTextureNode->image.numComponents;
		
		size_t pixelsLength =  pixelTextureNode->image.pixels.size();
		texture->image.length( pixelsLength );
		for( size_t j = 0 ; j < pixelsLength ; j++ )
		{
			texture->image[j] = pixelTextureNode->image.pixels[j];
		}
		texture->repeatS = pixelTextureNode->repeatS;
		texture->repeatT = pixelTextureNode->repeatT;

		long texturesLength = textures_.length();
		textures_.length( texturesLength + 1 );
		textures_[texturesLength] = texture;

		return texturesLength;
	}
	else
	{
		return -1;
	}
}




//==================================================================================================
/*!
  @if jp

    @brief		MaterialInfo����

	@note       material�m�[�h�����݂���΁CMaterialInfo�𐶐��Cmaterials_�ɒǉ�����B<BR>
                materials_�ɒǉ������ʒu(�C���f�b�N�X)��߂�l�Ƃ��ĕԂ��B<BR>

    @date       2008-04-18 Y.TSUNODA <BR>

	@return	    long  MaterialInfo (materials_)�̃C���f�b�N�X�Cmaterial�m�[�h�����݂��Ȃ��ꍇ�� -1

  @endif
*/
//==================================================================================================
long
BodyInfo_impl::_createMaterialInfo(
	VrmlMaterialPtr materialNode )		//!< MaterialNode�ւ̃|�C���^
{
	// material�m�[�h�����݂����
	if( materialNode )
	{
		MaterialInfo_var material( new MaterialInfo() );

		material->ambientIntensity = materialNode->ambientIntensity;
		material->shininess = materialNode->shininess;
		material->transparency = materialNode->transparency;
		for( int j = 0 ; j < 3 ; j++ )
		{
			material->diffuseColor[j] = materialNode->diffuseColor[j];
			material->emissiveColor[j] = materialNode->emissiveColor[j];
			material->specularColor[j] = materialNode->specularColor[j];
		}

		// materials_�ɒǉ�����
		long materialsLength = materials_.length();
		materials_.length( materialsLength + 1 );
		materials_[materialsLength] = material;

		// �ǉ������ʒu(materials_)�̃C���f�b�N�X��Ԃ�
		return materialsLength;
	}
	else
	{
		return -1;
	}
}




//==================================================================================================
/*!
  @if jp

    @brief      transform�v�Z

    @note       transform�m�[�h�Ŏw�肳�ꂽrotation,translation,scale���v�Z���C4x4�s��ɑ������<BR>
				�v�Z���ʂ͑�2�����ɑ������<BR>

    @date       2008-04-07 Y.TSUNODA <BR>

    @return     bool true:���� / false:���s

  @endif
*/
//==================================================================================================
bool
BodyInfo_impl::_calcTransform(
	VrmlTransformPtr transform,		//!< transform�m�[�h
	matrix44d&		mOutput )		//!< �v�Z���ʂ�������4x4�̍s��
{
	// rotation���h���Q�X�̉�]��
	vector3d vRotation( transform->rotation[0], transform->rotation[1], transform->rotation[2] );

	// ���h���Q�Xrotation��3x3�s��ɕϊ�����
	matrix33d mRotation;
	PRIVATE::rodrigues( mRotation, vRotation, transform->rotation[3] );

	// rotation, translation ��4x4�s��ɑ������
	matrix44d mTransform;
	mTransform =
		mRotation(0,0), mRotation(0,1), mRotation(0,2), transform->translation[0],
		mRotation(1,0), mRotation(1,1), mRotation(1,2), transform->translation[1],
		mRotation(2,0), mRotation(2,1), mRotation(2,2), transform->translation[2],
		0.0,            0.0,            0.0,		    1.0;


	// ScaleOrientation
	vector3d scaleOrientation;
	scaleOrientation =	transform->scaleOrientation[0],
						transform->scaleOrientation[1],
						transform->scaleOrientation[2];

	// ScaleOrientation��3x3�s��ɕϊ�����
	matrix33d mSO;
	PRIVATE::rodrigues( mSO, scaleOrientation, transform->scaleOrientation[3] );

	// �X�P�[�����O���S ���s�ړ�
	matrix44d mTranslation;
	mTranslation = 1.0, 0.0, 0.0, transform->center[0],
				   0.0, 1.0, 0.0, transform->center[1],
				   0.0, 0.0, 1.0, transform->center[2],
				   0.0, 0.0, 0.0, 1.0;

	// �X�P�[�����O���S �t���s�ړ�
	matrix44d mTranslationInv;
	mTranslationInv = 1.0, 0.0, 0.0, -transform->center[0],
					  0.0, 1.0, 0.0, -transform->center[1],
				  	  0.0, 0.0, 1.0, -transform->center[2],
					  0.0, 0.0, 0.0, 1.0;

	// ScaleOrientation ��]
	matrix44d mScaleOrientation;
	mScaleOrientation =	mSO(0,0), mSO(0,1), mSO(0,2), 0,
						mSO(1,0), mSO(1,1), mSO(1,2), 0,
						mSO(2,0), mSO(2,1), mSO(2,2), 0,
						0,        0,        0,        1;

	// �X�P�[��(�g��E�k����)
	matrix44d mScale;
	mScale = transform->scale[0],                 0.0,                 0.0, 0.0,
		 	                 0.0, transform->scale[1],                 0.0, 0.0,
			                 0.0,                 0.0, transform->scale[2], 0.0,
			                 0.0,                 0.0,                 0.0, 1.0;

	// ScaleOrientation �t��]
	matrix44d mScaleOrientationInv;
	mScaleOrientationInv =	mSO(0,0), mSO(1,0), mSO(2,0), 0,
							mSO(0,1), mSO(1,1), mSO(2,1), 0,
							mSO(0,2), mSO(1,2), mSO(2,2), 0,
							0,        0,        0,        1; 

	// transform, scale, scaleOrientation �Őݒ肳�ꂽ��]�E���i��������������
	mOutput = mTransform
			* mScaleOrientation * mTranslationInv * mScale * mTranslation * mScaleOrientationInv;

	return true;
}




//==================================================================================================
/*!
  @if jp

    @brief      ModelFile(.wrl)�̃f�B���N�g���p�X���擾

    @note       url_�̃p�X����URL�X�L�[���C�t�@�C���������������f�B���N�g���p�X�������Ԃ�<BR>

    @date       2008-04-19 Y.TSUNODA <BR>

    @return     string ModelFile(.wrl)�̃f�B���N�g���p�X������

  @endif
*/
//==================================================================================================
string
BodyInfo_impl::_getModelFileDirPath()
{
	// BodyInfo::url_ ���� URL�X�L�[�����폜����
	string filepath = deleteURLScheme( url_ );

	// '/' �܂��� '\' �̍Ō�̈ʒu���擾����
	size_t pos = filepath.find_last_of( "/\\" );

	string dirPath = "";

	// ���݂���΁C
	if( pos != string::npos )
	{
		// �f�B���N�g���p�X������
		dirPath = filepath;
		dirPath.resize( pos + 1 );
	}

	return dirPath;
}

