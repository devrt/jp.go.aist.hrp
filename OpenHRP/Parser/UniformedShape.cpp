/*!
  @file UniformedShape.cpp
  @brief Implementation of Uniformed Shape class
  @author Y.TSUNODA
*/

#include <iostream>
#include <math.h>
#include "UniformedShape.h"

using namespace std;
using namespace boost;
using namespace OpenHRP;

static const double PI = 3.14159265358979323846;



//==================================================================================================
/*!
  @if jp

    @brief      �R���X�g���N�^

    @note       <BR>

    @date       2008-04-15 Y.TSUNODA <BR>

    @return     

  @endif
*/
//==================================================================================================
UniformedShape::UniformedShape()
{
	type_ = S_UNKNOWN_TYPE;
	vertexList_.clear();
	triangleList_.clear();
	flgUniformIndexedFaceSet_ = true;
	flgMessageOutput_ = true;
}




//==================================================================================================
/*!
  @if jp

    @brief      IndexedFaceSet���`�t���O�ݒ�

    @note       IndexedFaceSet(���̓f�[�^)���O�p���b�V���ł��邱�Ƃ��ۏႳ�ꂽ�f�[�^�̏ꍇ�C
				���`�������������̓f�[�^�����̂܂܏o�͂���B<BR>
				true : ���`����������Cfalse : ���`���������Ȃ�<BR>

    @date       2008-04-15 Y.TSUNODA <BR>

    @return     bool �t���O�ɐݒ肳�ꂽ�l

  @endif
*/
//==================================================================================================
bool
UniformedShape::setFlgUniformIndexedFaceSet(
	bool val )
{
	return( flgUniformIndexedFaceSet_ = val );
}




//==================================================================================================
/*!
  @if jp

    @brief      ���`���� (ModelNodeSet����Shape)

    @note       ���`�ΏۂƂ��Ĉ����ŗ^����ꂽModelNodeSet���́CShape(VRML�v���~�e�B�u�`��)��
                ���`�����ɂĎO�p�`���b�V���x�[�X�̓���I�Ȋ􉽌`��ɕϊ�����<BR>

    @date       2008-04-03 Y.TSUNODA <BR>

    @return     bool true:���� / false:���s

  @endif
*/
//==================================================================================================
bool
UniformedShape::uniform(
	ModelNodeSet& modelNodeSet )
{
	// JointNode�����擾����
    int numJointNodes = modelNodeSet.numJointNodes();
	
    if( 0 < numJointNodes )
	{
		int currentIndex = 0;

		// JointNode ���ċA�I�ɒH��CLinkInfo�𐶐�����
		JointNodeSetPtr rootJointNodeSet = modelNodeSet.rootJointNodeSet();
		_traverseJointNode( rootJointNodeSet, currentIndex, -1 );
    }

	return true;
}




//==================================================================================================
/*!
  @if jp

    @brief      ModelNodeSet����Shape�̐��`�����̉������֐�

    @note       ModelNodeSet����JointNode���ċA�I�ɒH��

    @date       2008-04-03 Y.TSUNODA <BR>

    @return     bool true:���� / false:���s

  @endif
*/
//==================================================================================================
int UniformedShape::_traverseJointNode(
	JointNodeSetPtr		jointNodeSet,	//!< �ΏۂƂȂ� JointNodeSet
	int&				currentIndex,	//!< ����JointNodeSet��index
	int					parentIndex )	//!< �eNode��index
{
	int index = currentIndex;
	currentIndex++;

	// �qJointNode�����擾����
    size_t numChildren = jointNodeSet->childJointNodeSets.size();

	// �qJointNode�����ɒH��
	for( size_t i = 0 ; i < numChildren ; ++i )
	{
		// �e�q�֌W�̃����N��H��
		JointNodeSetPtr childJointNodeSet = jointNodeSet->childJointNodeSets[i];
		_traverseJointNode( childJointNodeSet, currentIndex, index );
    }

	// JointNodeSet �� segmentNode
	_traverseShapeNodes( jointNodeSet->segmentNode->fields["children"].mfNode() );

	return index;
}




//==================================================================================================
/*!
  @if jp

    @brief      ModelNodeSet����Shape�̐��`�����̉������֐�

    @note       JointNodeSet����Node���ċA�I�ɒH��

    @date       2008-04-03 Y.TSUNODA <BR>

    @return     bool true:���� / false:���s

  @endif
*/
//==================================================================================================
void UniformedShape::_traverseShapeNodes(
	MFNode& childNodes )			//!< �qNode
{
	vector<SFNode>::iterator itr = childNodes.begin();

	while( itr != childNodes.end() )
	{
		VrmlNodePtr node = *itr;

		// Group�m�[�h�Ƃ�����p�������m�[�h�̏ꍇ���A�q�m�[�h��H���Ă���
		if( node->isCategoryOf( GROUPING_NODE ) )
		{
			VrmlGroupPtr group = static_pointer_cast<VrmlGroup>( node );
			_traverseShapeNodes( group->children );

			++itr;
		}
		// SHAPE�m�[�h�Ȃ��
		else if( node->isCategoryOf( SHAPE_NODE ) )
		{
			cout << "SHAPENODE " << node->defName << endl;

			// ���`����
			if( this->uniform( node ) )
			{
				VrmlIndexedFaceSetPtr uniformedNode( new VrmlIndexedFaceSet );
			
				// ���`������̒��_�z��� VrmlIndexedFaceSet�֑������
				VrmlCoordinatePtr coordinate( new VrmlCoordinate );
				vector<vector3d> vertexList = this->getVertexList();
				for( size_t i = 0 ; i < vertexList.size() ; ++i )
				{
					SFVec3f point;
					vector3d vertex = vertexList[i];
					point[0] = vertex[0];
					point[1] = vertex[1];
					point[2] = vertex[2];
					coordinate->point.push_back( point );
				}
				uniformedNode->coord = coordinate;

				// ���`������̎O�p���b�V���z��� VrmlIndexedFaceSet�֑������
				vector<vector3i> triangleList = this->getTriangleList();
				for( size_t i = 0 ; i < triangleList.size() ; ++i )
				{
					vector3i triangle = triangleList[i];
					uniformedNode->coordIndex.push_back( triangle[0] );
					uniformedNode->coordIndex.push_back( triangle[1] );
					uniformedNode->coordIndex.push_back( triangle[2] );
					uniformedNode->coordIndex.push_back( -1 );
				}

				// Geometry�m�[�h�����ւ���
				VrmlShapePtr shapeNode = static_pointer_cast<VrmlShape>( node );
				shapeNode->geometry = uniformedNode;

				++itr;
			}
			else
			{
				// ���`�Ɏ��s�����m�[�h�͍폜����
				itr = childNodes.erase( itr );
			}
		}
	}
}




//==================================================================================================
/*!
  @if jp

    @brief      ���`����

    @note       ���`�ΏۂƂ��Ĉ����ŗ^����ꂽnode�𔻒肵�C�K�؂Ȍ`��̐��`�����ɓn�� <BR>
                ���`�����ɂĎO�p�`���b�V���x�[�X�̓���I�Ȋ􉽌`��ŕ\�����ꂽShapeInfor��Ԃ�<BR>

    @date       2008-03-19 Y.TSUNODA <BR>

    @return     bool true:���� / false:���s

  @endif
*/
//==================================================================================================
bool
UniformedShape::uniform( 
	VrmlNodePtr node )		//!< ���`�Ώۂ�Node (ShapeNode)
{
	bool ret = false;

	// Shape �m�[�h�𒊏o���� (����node��Shape�m�[�h�Ɣ����Ă���̂�)
	VrmlShapePtr shapeNode = static_pointer_cast<VrmlShape>( node );

	VrmlGeometryPtr geometry = shapeNode->geometry;
	if( VrmlBoxPtr box = dynamic_pointer_cast<VrmlBox>( geometry ) )
	{
		ret = uniformBox( box );
	}
	else if( VrmlConePtr cone = dynamic_pointer_cast<VrmlCone>( geometry ) )
	{
		ret = uniformCone( cone );
	}
	else if( VrmlCylinderPtr cylinder = dynamic_pointer_cast<VrmlCylinder>( geometry ) )
	{
		ret = uniformCylinder( cylinder );
	}
	else if( VrmlSpherePtr sphere = dynamic_pointer_cast<VrmlSphere>( geometry ) )
	{
		ret = uniformSphere( sphere );
	}
	else if( VrmlIndexedFaceSetPtr faceSet = dynamic_pointer_cast<VrmlIndexedFaceSet>( geometry ) )
	{
		ret = uniformIndexedFaceSet( faceSet );
	}
	else if( VrmlElevationGridPtr elevationGrid = dynamic_pointer_cast<VrmlElevationGrid>( geometry ) )
	{
		ret = uniformElevationGrid( elevationGrid );
	}
	else if( VrmlExtrusionPtr extrusion = dynamic_pointer_cast<VrmlExtrusion>( geometry ) )
	{
		ret = uniformExtrusion( extrusion );
	}
	else
	{
		// ##### [TODO] #####
		;
	}

	return ret;
}




//==================================================================================================
/*!
  @if jp

    @brief      Box���`����

    @note       VRML�v���~�e�B�u�`��BOX���C�O�p�`���b�V���x�[�X�̓���I�Ȋ􉽌`��\���֕ϊ�����<BR>

    @date       2008-03-19 Y.TSUNODA <BR>
                2008-03-27 K.FUKUDA  ShapeInfo�p�~<BR>

    @return     bool true:���� / false:���s

  @endif
*/
//==================================================================================================
bool
UniformedShape::uniformBox(
	VrmlBoxPtr vrmlBox )	//!< ���`�Ώۂ� Box node
{
    // ���_���X�g�ƎO�p���b�V�����X�g���N���A����
    type_ = S_BOX;
    vertexList_.clear();
    triangleList_.clear();

	// BOX�T�C�Y�擾
	double width  = vrmlBox->size[0];
	double height = vrmlBox->size[1];
	double depth  = vrmlBox->size[2];

    // �G���[�`�F�b�N
    if( width < 0.0 || height < 0.0 || depth < 0.0 )
    {
		this->putMessage( "BOX : wrong value." );
        return false;
    }

	// BOX���_����
	vector3d vertex;

	vertex = -width/2.0, -height/2.0, -depth/2.0;	// ���_No.0
	_addVertexList( vertex );
	
	vertex = -width/2.0, -height/2.0, depth/2.0;	// ���_No.1
	_addVertexList( vertex );

	vertex = -width/2.0, height/2.0, -depth/2.0;	// ���_No.2
	_addVertexList( vertex );

	vertex = -width/2.0, height/2.0, depth/2.0;		// ���_No.3
	_addVertexList( vertex );

	vertex = width/2.0, -height/2.0, -depth/2.0;	// ���_No.4
	_addVertexList( vertex );

	vertex = width/2.0, -height/2.0, depth/2.0;		// ���_No.5
	_addVertexList( vertex );

	vertex = width/2.0, height/2.0, -depth/2.0;		// ���_No.6
	_addVertexList( vertex );

	vertex = width/2.0, height/2.0, depth/2.0;		// ���_No.7
	_addVertexList( vertex );


	// BOX �O�p���b�V���𐶐�����
	const int triangles[] =	{	5, 7, 3,	// Triangle No.0
								5, 3, 1,	// Triangle No.1
								0, 2, 6,	// Triangle No.2
								0, 6, 4,	// Triangle No.3
								4, 6, 7,	// Triangle No.4
								4, 7, 5,	// Triangle No.5
								1, 3, 2,	// Triangle No.6
								1, 2, 0,	// Triangle No.7
								7, 6, 2,	// Triangle No.8
								7, 2, 3,	// Triangle No.9
								4, 5, 1,	// Triangle No.10
								4, 1, 0,	// Triangle No.11
							};

	// triangleList_ �ɑ������
    const int triangleNumber = 12;
	for( int i = 0 ; i < triangleNumber ; i++ )
	{
		_addTriangleList( triangles[i*3+0], triangles[i*3+1], triangles[i*3+2] );
	}

	return true;
}




//==================================================================================================
/*!
  @if jp

    @brief      Cone���`����

    @note       VRML�v���~�e�B�u�`��CONE���C�O�p�`���b�V���x�[�X�̓���I�Ȋ􉽌`��\���֕ϊ�����<BR>

    @date       2008-03-19 Y.TSUNODA <BR>
                2008-03-27 K.FUKUDA  ShapeInfo�p�~<BR>

    @return     bool true:���� / false:���s

  @endif
*/
//==================================================================================================
bool
UniformedShape::uniformCone(
	VrmlConePtr vrmlShapeConeNode,	//!< ���`�Ώۂ� Cone node
	int divisionNumber )			//!< ������
{
    // ���_���X�g�ƎO�p���b�V�����X�g���N���A����
    type_ = S_CONE;
    vertexList_.clear();
    triangleList_.clear();

	// CONE�T�C�Y�擾
	double height = vrmlShapeConeNode->height;
	double radius = vrmlShapeConeNode->bottomRadius;

    // �G���[�`�F�b�N
    if( height < 0.0 || radius < 0.0 )
    {
		this->putMessage( "CONE : wrong value." );
        return false;
    }

	vector<int>			circularList;			// ��ʉ~����̒��_���i�[���郊�X�g(��Ɨp)
	vector3d			v;						// ���_
	vector3i			tr;						// �O�p���b�V��

	// CONE TOP ( = 0 �Ԗڂ̒��_ )
	v = 0.0, height, 0.0;
	_addVertexList( v );

	// CONE ��ʒ��S ( = 1 �Ԗڂ̒��_ )
	v = 0.0, 0.0, 0.0;
	_addVertexList( v );

	// Cone ��ʉ~����ɒ��_�𐶐�����
	for( int i = 0 ;  i < divisionNumber ; i++ )
	{
		v =  radius * cos( i * 2.0 * PI / divisionNumber ),	// X
			 0.0,											// Y
			-radius * sin( i * 2.0 * PI / divisionNumber );	// Z

		// ���_���X�g�ɒǉ�����C�ǉ��������_�ԍ��� vertexIndex
		size_t vertexIndex;		// ���_�ԍ�
		vertexIndex = _addVertexList( v );

		// ��ʉ~����̒��_���X�g�ɒǉ�����
		circularList.push_back( static_cast<int>( vertexIndex ) );
	}

	size_t cListSize = circularList.size();		// ��ʁE��ʂ̒��_��
												//   ���_��CdivisionNumber �Ɠ����ł��邪

	// �O�p���b�V���𐶐�����
	for( size_t i = 0 ; i < cListSize ; i++ )
	{
		// CONE ���ʂ̎O�p���b�V�� ( TOP - ��ʉ~����̒��_1 - ��ʉ~����̒��_2 )
		tr = 0,
			 circularList.at(   i       % cListSize ),
			 circularList.at( ( i + 1 ) % cListSize );
		_addTriangleList( tr );

		// CONE ��ʕ����̎O�p���b�V�� ( ��ʒ��S - ��ʉ~����̒��_2 - ��ʉ~����̒��_1 )
		tr = 1,
			 circularList.at( ( i + 1 ) % cListSize ),
			 circularList.at(   i       % cListSize );
		_addTriangleList( tr );
	}

	return true;
}




//==================================================================================================
/*!
  @if jp

    @brief      Cylinder���`����

    @note       VRML�v���~�e�B�u�`��CYLINDER���C�O�p�`���b�V���x�[�X�̓���I�Ȋ􉽌`��\���֕ϊ�����<BR>

    @date       2008-03-20 Y.TSUNODA <BR>
                2008-03-27 K.FUKUDA  ShapeInfo�p�~<BR>

    @return     bool true:���� / false:���s

  @endif
*/
//==================================================================================================
bool
UniformedShape::uniformCylinder(
	VrmlCylinderPtr vrmlShapeCylinderNode,	//!< ���`�Ώۂ� Cylinder node
	int divisionNumber )					//!< ������
{
    // ���_���X�g�ƎO�p���b�V�����X�g���N���A����
    type_ = S_CYLINDER;
    vertexList_.clear();
    triangleList_.clear();

	// CYLINDER�T�C�Y�擾
	double height = vrmlShapeCylinderNode->height;
	double radius = vrmlShapeCylinderNode->radius;

    // �G���[�`�F�b�N
    if( height < 0.0 || radius < 0.0 )
    {
		this->putMessage( "CYLINDER : wrong value." );
        return false;
    }

	vector<int>			uCircularList;		// ��ʉ~����̒��_���i�[���郊�X�g(��Ɨp)
	vector<int>			lCircularList;		// ��ʉ~����̒��_���i�[���郊�X�g(��Ɨp)

	vector3d			v;					// ���_
	vector3i			tr;					// �O�p���b�V��

	// CYLINDER ��ʒ��S ( = 0 �Ԗڂ̒��_ )
	v =  0.0, height / 2.0, 0.0;
	_addVertexList( v );

	// CYLINDER ��ʒ��S ( = 1 �Ԗڂ̒��_ )
	v = 0.0, -height / 2.0, 0.0;
	_addVertexList( v );


	// CYLINDER ��ʁE��ʉ~����ɒ��_�𐶐�����
	for( int i = 0 ; i < divisionNumber ; i++ )
	{
		size_t uVertexIndex;	// ��ʉ~����̒��_�ԍ�
		size_t lVertexIndex;	// ��ʉ~����̒��_�ԍ�

		// ��ʉ~�ʏ�̒��_
		v =  radius * cos( i * 2.0 * PI / divisionNumber ),
			 height / 2.0,
			-radius * sin( i * 2.0 * PI / divisionNumber );
		uVertexIndex = _addVertexList( v );

		// ��ʉ~����̒��_���X�g�ɒǉ�����
		uCircularList.push_back( static_cast<int>( uVertexIndex ) );
		
		// ��ʉ~�ʏ�̒��_
		v =  radius * cos( i * 2.0 * PI / divisionNumber ),
			-height / 2.0,
			-radius * sin( i * 2.0 * PI/ divisionNumber );
		lVertexIndex = _addVertexList( v );

		// ��ʉ~����̒��_���X�g�ɒǉ�����
		lCircularList.push_back( static_cast<int>( lVertexIndex ) );
	}

	size_t cListSize = uCircularList.size();	// ��ʁE��ʂ̒��_��
												//   ���_��CdivisionNumber �Ɠ����ł��邪

	// �O�p���b�V���𐶐�����
	for( size_t i = 0 ; i < cListSize ; i++ )
	{
		// ��ʂ̎O�p���b�V��
		tr = 0,
			 uCircularList.at(   i       % cListSize ),
			 uCircularList.at( ( i + 1 ) % cListSize );
		_addTriangleList( tr );

		// ��ʂ̎O�p���b�V��
		tr = 1,
			 lCircularList.at( ( i + 1 ) % cListSize ),
			 lCircularList.at(   i       % cListSize );
		_addTriangleList( tr );

		// ���ʂ�(���)�O�p���b�V��
		tr = uCircularList.at(   i       % cListSize ),
			 lCircularList.at(   i       % cListSize ),
			 lCircularList.at( ( i + 1 ) % cListSize );
		_addTriangleList( tr );

		// ���ʂ�(���)�O�p���b�V��
		tr = uCircularList.at(   i       % cListSize ),
			 lCircularList.at( ( i + 1 ) % cListSize ),
			 uCircularList.at( ( i + 1 ) % cListSize );
		_addTriangleList( tr );
	}

	return true;
}




//==================================================================================================
/*!
  @if jp

    @brief      IndexedFacedSet���`����

    @note       VRML�v���~�e�B�u�`��INDEXEDFACESET���C�O�p�`���b�V���x�[�X�̓���I�Ȋ􉽌`��\����
                �ϊ�����<BR>

    @date       2008-03-20 Y.TSUNODA <BR>
                2008-03-27 K.FUKUDA  ShapeInfo�p�~<BR>

    @return     bool true:���� / false:���s

  @endif
*/
//==================================================================================================
bool
UniformedShape::uniformIndexedFaceSet(
	VrmlIndexedFaceSetPtr vrmlIndexedFaceSetNode )	//!< ���`�Ώۂ� IndexedFacedSet node
{
    // ���_���X�g�ƎO�p���b�V�����X�g���N���A����
    type_ = S_INDEXED_FACE_SET;
    vertexList_.clear();
    triangleList_.clear();

	// coord field ���_�Q
	VrmlCoordinatePtr coodinate = vrmlIndexedFaceSetNode->coord;
	MFVec3f points = coodinate->point;		// ���_���W�Q���i�[���� std::vector�B���g�� boost::array<SFFloat,3>

	// ���_�Q points(MFVec3f)�� vertexList_�ɓ���� (�R�s�[)
	for( size_t i = 0 ; i < points.size() ; i++ )
	{
		vector3d v;
		v = ( points.at( i ) )[0], ( points.at( i ) )[1], ( points.at( i ) )[2];
		_addVertexList( v );
	}

	// ���`����������ꍇ
	if( flgUniformIndexedFaceSet_ )
	{

		// ���b�V���z����擾����(coordList�ɑ������)
		MFInt32 coordList = vrmlIndexedFaceSetNode->coordIndex;

		vector<int>			mesh;				// ��Ɨp�e���|����
												//   ���f���t�@�C���Ŏw�肳�ꂽ coordIndex���珇�ɁC
												//    ��̃��b�V�����\�����钸�_�ԍ����i�[����vector

		for( size_t i = 0 ; i < vrmlIndexedFaceSetNode->coordIndex.size() ; i++ )
		{
			// [MEMO] �܂��C-1���o������܂ł́C1�̃��b�V���̒��_�Q��mesh�ɓ����
			//        -1 ���o��������C�O�p���b�V���𐶐����� traiangleList�ɒǉ�����
			//        �������I���s�v�ɂȂ���(�e���|������)���_�Q mesh���N���A����

			int		index = vrmlIndexedFaceSetNode->coordIndex.at( i );
			if( index == -1 )
			{
				// �O�p���b�V���𐶐����CtriangleList_�ɒǉ�����
				//   �������s��( ���_����5�ȏ�̏ꍇ ) �������f�� false��Ԃ�
				if( _createTriangleMesh( mesh, vrmlIndexedFaceSetNode->ccw ) < 0 )	return false;

				// ���b�V�����N���A
				mesh.clear();
			}
			else
			{
				// ���_�ԍ������b�V���ɒǉ�
				mesh.push_back( index );
			}
		}
		
		// mesh�Ƀf�[�^���c���Ă���΁C
		//   �� �i�[�o���Ă��Ȃ��ꍇ���l������̂ŁC(coordinateIndex��-1�ŏI����Ă��Ȃ��ꍇ)
		if( 0 < mesh.size() )
		{
			// �O�p���b�V���𐶐����CtriangleList_�ɒǉ�����
			//   �������s��( ���_����5�ȏ�̏ꍇ ) �������f�� false��Ԃ�
			if( _createTriangleMesh( mesh, vrmlIndexedFaceSetNode->ccw ) < 0 ) return false;

			// ���b�V�����N���A
			mesh.clear();
		}
	}
	// ���`���������Ȃ�(�O�p���b�V���ł��邱�Ƃ��ۏ؂���Ă���)�ꍇ
	else
	{
		// ���b�V�������v�Z����
		//   coordIndex�� -1����؂�q�Ƃ��Ă���̂� size() / 4 �����b�V�����ƂȂ�
		//   �܂��CcoordIndex�̍Ō��-1�������\��������̂� size() + 1 �Ƃ��Ă��� 
		size_t meshNum = static_cast<size_t>( ( vrmlIndexedFaceSetNode->coordIndex.size() + 1 ) / 4 );

		for( size_t i = 0 ; i < meshNum ; i++ )
		{
			// [MEMO]
			// �O�̂��� vrmlIndexedFaceSetNode->coordIndex.at( # ) ���Ó��Ȑ��l�ł��邩
			// �`�F�b�N���ׂ���������Ȃ����C�u�O�p���b�V���ł��邱�Ƃ��ۏ؂���Ă���v
			// �Ƃ������ƂŁC��؃`�F�b�N�͂��Ă��Ȃ��B

			// triangleList�ɒǉ�����
			_addTriangleList( vrmlIndexedFaceSetNode->coordIndex.at( 4*i+0 ),
							  vrmlIndexedFaceSetNode->coordIndex.at( 4*i+1 ),
							  vrmlIndexedFaceSetNode->coordIndex.at( 4*i+2 ) );
		}
	}

	return true;
}




//==================================================================================================
/*!
  @if jp

    @brief      Sphere���`����

    @note       VRML�v���~�e�B�u�`��SPHERE���C�O�p�`���b�V���x�[�X�̓���I�Ȋ􉽌`��\���֕ϊ�����<BR>

    @date       2008-03-20 Y.TSUNODA <BR>
                2008-03-27 K.FUKUDA  ShapeInfo�p�~<BR>

    @return     bool true:���� / false:���s

  @endif
*/
//==================================================================================================
bool
UniformedShape::uniformSphere(
	VrmlSpherePtr vrmlShapeSphereNode,		//!< ���`�Ώۂ� Sphere node
	int vDivisionNumber,					//!< ������ �ܓx����
	int hDivisionNumber )					//!< ������ �o�x����
{
    // ���_���X�g�ƎO�p���b�V�����X�g���N���A����
    type_ = S_SPHERE;
    vertexList_.clear();
    triangleList_.clear();

	// CYLINDER�T�C�Y�擾
	double radius = vrmlShapeSphereNode->radius;

    // �G���[�`�F�b�N
    if( radius < 0.0 )
    {
        this->putMessage( "SPHERE : wrong value." );
        return false;
    }

    vector3d			v;					// ���_
	vector3i			tr;					// �O�p���b�V��

	// SPHERE �V�����W ( = 0 �Ԗڂ̒��_ )
	v =  0.0, radius, 0.0;
	_addVertexList( v );

	// SPHERE �V����W ( = 1 �Ԗڂ̒��_ )
	v = 0.0, -radius, 0.0;
	_addVertexList( v );

	vector< vector<int> >	vertexIndexMatrix;	// ���_�C���f�b�N�X���i�[�����}�g���N�X(�ꎞ��Ɨp)
												//   ���V���E�V����������_���i�[����}�g���N�X
												//     ��������ɂ��ĎO�p���b�V�����X�gtraiangleList�𐶐�����
	vector<int>			circularList;			// �~����̒��_���X�g(�ꎞ��Ɨp)
												//   ������Y���ɐ����ȕ��ʂŃX���C�X���ďo����ʁE�~����̒��_���X�g

	// �ܓx(�c)�����̃��[�v
	for( int i = 1 ; i < vDivisionNumber ; i++ )
	{
		double radVDivison = i * PI / vDivisionNumber;				// �ܓx�����̕����p(���W�A��)

		// ��~����̒��_(index)���X�g���N���A����
		circularList.clear();

		// �o�x(��)�����̃��[�v
		for( int j = 0 ; j < hDivisionNumber ; j++ )
		{
			double radHDivision = j * 2.0 * PI / hDivisionNumber;	// �o�x�����̕����p(���W�A��)

			// ���_���W���v�Z����
			v =  radius * sin( radVDivison ) * cos( radHDivision ),
				 radius * cos( radVDivison ),
				-radius * sin( radVDivison ) * sin( radHDivision );

			// ���_���X�g�ɒǉ�����
			size_t vertexIndex;		// ���_�C���f�b�N�X
			vertexIndex = _addVertexList( v );

			// ��~����̒��_(index)���X�g�ɒǉ�����
			circularList.push_back( static_cast<int>( vertexIndex ) );
		}

		// ��~����̒��_(index)���X�g���C���_(index)�}�g���N�X�ɒǉ�����
		vertexIndexMatrix.push_back( circularList );
	}


	// �ŏ�E�ŉ��т̃��b�V����ǉ�����
	vector<int> uCircularList = vertexIndexMatrix.at( 0 );
	vector<int> lCircularList = vertexIndexMatrix.at( vertexIndexMatrix.size() - 1 );

	size_t cListSize = uCircularList.size();	// ��~����̒��_��
	for( size_t i = 0 ; i < cListSize ; i++ )
	{
		// �V����
		tr = 0,
			 uCircularList.at( i         % cListSize ),
			 uCircularList.at( ( i + 1 ) % cListSize );
		_addTriangleList( tr );

		// �V�ꑤ
		tr = 1,
			 lCircularList.at( ( i + 1 ) % cListSize ),
			 lCircularList.at( i         % cListSize );
		_addTriangleList( tr );
	}

	// ���ʑт̃��b�V����ǉ�����
	for( int i = 0 ; i < static_cast<int>( vertexIndexMatrix.size() ) - 1 ; i++ )
	{
		// ���鑤�ʑт̏㉺�~���̒��_���X�g���擾����
		uCircularList = vertexIndexMatrix.at( i );
		lCircularList = vertexIndexMatrix.at( i + 1 );

		cListSize = uCircularList.size();
		for( size_t j = 0 ; j < cListSize ; j++ )
		{
			// ���O�p���b�V��
			tr = uCircularList.at(   j       % cListSize ),
				 lCircularList.at(   j       % cListSize ),
				 lCircularList.at( ( j + 1 ) % cListSize );
			_addTriangleList( tr );

			// ����O�p���b�V��
			tr = uCircularList.at(   j       % cListSize ),
				 lCircularList.at( ( j + 1 ) % cListSize ),
				 uCircularList.at( ( j + 1 ) % cListSize );
			_addTriangleList( tr );
		}
	}

	return true;
}




//==================================================================================================
/*!
  @if jp

    @brief      ElevationGrid���`����

    @note       VRML�v���~�e�B�u�`��ELEVATIONGRID���C�O�p�`���b�V���x�[�X�̓���I�Ȋ􉽌`��\����
                �ϊ�����<BR>

    @date       2008-03-20 Y.TSUNODA <BR>
                2008-03-27 K.FUKUDA  ShapeInfo�p�~<BR>

    @return     bool true:���� / false:���s

  @endif
*/
//==================================================================================================
bool
UniformedShape::uniformElevationGrid(
	VrmlElevationGridPtr elevationGrid )	//!< ���`�Ώۂ� ElevationGrid node
{
    type_ = S_ELEVATION_GRID;

	// �i�q���ƍ��x�w��̌�����v���Ă��Ȃ���΁C 
	if( elevationGrid->xDimension * elevationGrid->zDimension
		!= static_cast<SFInt32>( elevationGrid->height.size() ) )
	{	
        this->putMessage( "ELEVATIONGRID : wrong value." );
        return false;
	}

	vector3d			v;						// ���_
	vector3i			tr;						// �O�p���b�V��

	vector< vector<int> >	vertexIndexMatrix;	// ���_�C���f�b�N�X���i�[�����}�g���N�X(�ꎞ��Ɨp)
	vector<int>			lineList;				// �i�q���̒��_���X�g(�ꎞ��Ɨp)

	for( int z = 0 ; z < elevationGrid->zDimension ; z++ )
	{
		for( int x = 0 ; x < elevationGrid->xDimension ; x++ )
		{
			v = x * elevationGrid->xSpacing,
				elevationGrid->height[z * elevationGrid->xDimension + x],
				z * elevationGrid->zSpacing;

			// ���_���X�g�ɒǉ�����
			size_t vertexIndex;		// ���_�C���f�b�N�X
			vertexIndex = _addVertexList( v );

			lineList.push_back( static_cast<int>( vertexIndex ) );
		}
		
		vertexIndexMatrix.push_back( lineList );
		lineList.clear();
	}


	// ���b�V����������
	for( int z = 0 ; z < static_cast<int>( vertexIndexMatrix.size() ) - 1 ; z++ )
	{
		vector<int> currentLine	= vertexIndexMatrix.at( z );
		vector<int> nextLine	= vertexIndexMatrix.at( z + 1 );

		for( int x = 0 ; x < static_cast<int>( currentLine.size() ) - 1 ; x++ )
		{
			// ���O�p�`
			_addTriangleList( currentLine.at( x ), nextLine.at( x ), nextLine.at( x + 1 ), elevationGrid->ccw );

			// ����O�p�`
			_addTriangleList( currentLine.at( x ), nextLine.at( x + 1 ), currentLine.at( x + 1 ), elevationGrid->ccw );
		}
	}

	return true;
}



//==================================================================================================
/*!
  @if jp

    @brief      Extrusion���`����

    @note       VRML�v���~�e�B�u�`��EXTRUSION���C�O�p�`���b�V���x�[�X�̓���I�Ȋ􉽌`��\���֕ϊ�
                ����<BR>

    @date       2008-03-20 Y.TSUNODA <BR>
                2008-03-27 K.FUKUDA  ShapeInfo�p�~<BR>

    @return     bool true:���� / false:���s

  @endif
*/
//==================================================================================================
bool
UniformedShape::uniformExtrusion(
	VrmlExtrusionPtr extrusion )	//!< ���`�Ώۂ� Extrusion node
{
    // ���_���X�g�ƎO�p���b�V�����X�g���N���A����
    type_ = S_EXTRUSION;
    vertexList_.clear();
    triangleList_.clear();

	return true;
}





//==================================================================================================
/*!
  @if jp

    @brief      �O�p���b�V������

    @note       n�p�`�̃��b�V�����O�p�`���b�V���ɕ�������<BR>
                �� �����_�ł́C�����Ώۂ̃��b�V���͎O�p�`�E�l�p�`�̃��b�V���݂̂ɑΉ�<BR>

    @date       2008-03-14 Y.TSUNODA <BR>

    @return     int ���������O�p�`�̐� �G���[���͕��̐���

  @endif
*/
//==================================================================================================
int
UniformedShape::_createTriangleMesh(
	vector<int> mesh,					//!< 1�̃��b�V�����\�����钸�_�Q���X�g
	bool ccw )							//!< CounterClockWise (�����v�܂��)�w��
{
	int triangleCount = 0;				// ���������O�p�`�̐�
	size_t vertexNumber = mesh.size();	// ���b�V�����\�����钸�_��

	if( vertexNumber == 3 )
	{
		triangleCount = 1;

		// ���̂܂� triangleList�ɒǉ�����
		_addTriangleList( mesh[0], mesh[1], mesh[2], ccw );
	}
	else if( vertexNumber == 4 )
	{
		triangleCount = 2;

		// �Ίp���̒������Z�����ŕ�������
		if( PRIVATE::_distance( vertexList_.at( mesh[0] ), vertexList_.at( mesh[2] ) )
		  <	PRIVATE::_distance( vertexList_.at( mesh[1] ), vertexList_.at( mesh[3] ) ) )
		{
			_addTriangleList( mesh[0], mesh[1], mesh[2], ccw );
			_addTriangleList( mesh[0], mesh[2], mesh[3], ccw );
		}
		else
		{
			_addTriangleList( mesh[0], mesh[1], mesh[3], ccw );
			_addTriangleList( mesh[1], mesh[2], mesh[3], ccw );
		}
	}
	else
	{
        this->putMessage( "The number of vertex is 5 or more." );
		return -1;
	}

	return triangleCount;
}




//==================================================================================================
/*!
  @if jp

    @brief      ���_���X�g�ɒ��_��ǉ�

    @note       ���_���X�g�ɒ��_��ǉ����C�ǉ������ʒu(index)��Ԃ� <BR>

    @date       2008-03-10 Y.TSUNODA <BR>
                2008-03-27 K.FUKUDA  vertexList_ �����o�ϐ����ɔ����ύX<BR>

    @return     size_t ���_�𒸓_���X�g�ɒǉ������ʒu(index)

  @endif
*/
//==================================================================================================
size_t UniformedShape::_addVertexList(
	vector3d v )				//!< ���_
{
	vertexList_.push_back( v );

	return( vertexList_.size() - 1 );
}




//==================================================================================================
/*!
  @if jp

    @brief      �O�p���b�V�����X�g�ɎO�p���b�V����ǉ�����(�\�����钸�_index�w��)

	@note		�O�p���b�V�����X�g�ɒ��_��ǉ����C�ǉ������ʒu(index)��Ԃ�<BR>
				�A���Cccw�t���O��false�̏ꍇ�C���v���(����)�̎O�p���b�V������������<BR>
				����� 2�̃��b�V���������Ő����E�o�^����邱�ƂɂȂ�B<BR>
				�߂�l�́C�ʏ��(�����v���)�̎O�p���b�V�����i�[���ꂽ�ʒu��index<BR>
				ccw�t���O��false�̏ꍇ�C���v���̎O�p���b�V�����i�[���ꂽ�ʒu��index+1�œ�����<BR>

    @date       2008-03-10 Y.TSUNODA <BR>
                2008-03-27 K.FUKUDA  triangleList_ �����o�ϐ����ɔ����ύX<BR>

	@return		size_t �O�p���b�V�����O�p���b�V�����X�g�ɒǉ������ʒu(index)

  @endif
*/
//==================================================================================================
size_t UniformedShape::_addTriangleList(
	int v1,						//!< ���b�V�����\�����钸�_�C���f�b�N�X1
	int v2,						//!< ���b�V�����\�����钸�_�C���f�b�N�X2
	int v3,						//!< ���b�V�����\�����钸�_�C���f�b�N�X3
	bool ccw )					//!< CounterClockWise (�����v�܂��)�w��
{
	vector3i triangle;
	size_t	index;

	// �ʏ�́C�w�肳�ꂽ���_���ɎO�p���b�V���𐶐�����
	triangle = v1, v2, v3;
	index = _addTriangleList( triangle );

	// ccw�t���O�� false �Ȃ�΁C
	if( false == ccw )
	{
		// ���v���(������)�̎O�p���b�V���𐶐�����
		triangle = v1, v3, v2;
		_addTriangleList( triangle );
	}

	return( index );
}




//==================================================================================================
/*!
  @if jp

    @brief      �O�p���b�V�����X�g�ɎO�p���b�V����ǉ�

	@note		�O�p���b�V�����X�g�ɎO�p���b�V����ǉ����C�ǉ������ʒu(index)��Ԃ�<BR>

    @date       2008-03-10 Y.TSUNODA <BR>
                2008-03-27 K.FUKUDA  triangleList_ �����o�ϐ����ɔ����ύX<BR>

	@return		size_t �O�p���b�V�����O�p���b�V�����X�g�ɒǉ������ʒu(index)

  @endif
*/
//==================================================================================================
size_t UniformedShape::_addTriangleList(
	vector3i t )				//!< �O�p���b�V��
{
	triangleList_.push_back( t );

	return( triangleList_.size() - 1 );
}





//==================================================================================================
/*!
  @if jp

    @brief      ���b�Z�[�W�o��

	@note		<BR>

    @date       2008-04-18 Y.TSUNODA <BR>

	@return		void

  @endif
*/
//==================================================================================================
void UniformedShape::putMessage(
	const std::string& message )
{
	if( flgMessageOutput_ )
	{
		signalOnStatusMessage( message + "\n" );
	}
}



//==================================================================================================
/*!
  @if jp

    @brief      3x3�s������h���Q�X�ɕϊ�

	@note		<BR>

    @date       2008-03-27 K.FUKUDA <BR>

	@return		vector3d

  @endif
*/
//==================================================================================================
vector3d
OpenHRP::PRIVATE::omegaFromRot(
    const matrix33d& r )    //!< 3x3�̉�]�s��
{
    using ::std::numeric_limits;

    double alpha = (r(0,0) + r(1,1) + r(2,2) - 1.0) / 2.0;

    if(fabs(alpha - 1.0) < 1.0e-6) {
        return vector3d(0.0);

    } else {
        double th = acos(alpha);
        double s = sin(th);

        if (s < numeric_limits<double>::epsilon()) {
            return vector3d(0.0);
        }

        double k = -0.5 * th / s;

        return vector3d( (r(1,2) - r(2,1)) * k,
			(r(2,0) - r(0,2)) * k,
			(r(0,1) - r(1,0)) * k );
    }
}




//==================================================================================================
/*!
  @if jp

    @brief      ��_�Ԃ̋���(���[�N���b�h�̋���)�v�Z

    @note       <BR>

    @date       2008-03-14 Y.TSUNODA <BR>

    @return     double ����

  @endif
*/
//==================================================================================================
double
OpenHRP::PRIVATE::_distance( vector3d a, vector3d b )
{
	double distance = sqrt( pow( a[0] - b[0], 2 ) + pow( a[1] - b[1], 2 ) + pow( a[2] - b[2], 2 ) );

	return distance;
}




//==================================================================================================
/*!
  @if jp

    @brief      �x�N�g���̑傫���v�Z

    @note       <BR>

    @date       2008-04-11 Y.TSUNODA <BR>

    @return     double �傫��

  @endif
*/
//==================================================================================================
double
OpenHRP::PRIVATE::_length( vector3d a )
{
	double length = sqrt( pow( a[0], 2 ) + pow( a[1], 2 ) + pow( a[2], 2 ) );

	return length;
}


