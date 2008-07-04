/*!
  @file CalculateNormal.cpp
  @brief Implementation of Caluculate Normal class
  @author Y.TSUNODA
*/

#include <iostream>
#include <math.h>
#include "CalculateNormal.h"

using namespace std;
using namespace boost;
using namespace OpenHRP;

static const double PI = 3.14159265358979323846;


//==================================================================================================
/*!
  @if jp

    @brief      �S�Ă̒��_�̖@���x�N�g�����v�Z

    @note       �����ŗ^����ꂽ���_�Q�ɑΉ�����@���x�N�g�����v�Z����<BR>
				�v�Z�����@���� �����o�ϐ� _normalsOfVertex �Ɋi�[����<BR>
				_normalsOfVertex�̖@���́C����vertexList�̒��_�̏��ƑΉ����Ă���<BR><BR>
				�� ���̊֐����Ăяo�����ۂɁC�S�Ă̎O�p���b�V���̖@�� _normalsOfMesh ���v�Z�����
				   ���Ȃ��ƌ��􂵂��ꍇ�́C_normalsOfMesh���v�Z����B<BR>

    @date       2008-04-11 Y.TSUNODA <BR>

    @return     bool true:���� / false:���s

  @endif
*/
//==================================================================================================
bool
CalculateNormal::calculateNormalsOfVertex( 
	vector<vector3d> vertexList,		//!< ���_�Q
	vector<vector3i> triangleList,		//!< �O�p���b�V���Q
	double creaseAngle )				//!< �܂�ڊp�x
{
	size_t normalsOfMeshNum = _normalsOfMesh.size();
	size_t triangleNum = triangleList.size();

	// �O�p���b�V���̖ʂ̖@�������v�Z�Ȃ��
	//   (�܂�C�O�p���b�V���̖@������0�ȉ� ������ �O�p���b�V�����ƈ�v���Ă��Ȃ���΁C)
	if( ( normalsOfMeshNum <= 0 ) && ( normalsOfMeshNum != triangleNum ) )
	{
		// �S�Ă̎O�p���b�V���̖@�� _normalsOfMesh ���v�Z����
		calculateNormalsOfMesh( vertexList, triangleList );
	}

	vector<vector3d> tmpNormalsOfVertex;	// �ꎞ��Ɨp ���_�̖@���v�Z���ʂ��i�[
	vector3d normal;
	vector3d tmpnormal;
	vector3d sum;
	int count;

	// ���_�E�O�p���b�V���Ή����X�g�����ɒH��
	for( size_t vertexIndex = 0 ; vertexIndex < vertexContainedInMeshList_.size() ; ++vertexIndex )
	{
		sum = 0.0, 0.0, 0.0;
		count = 0;

		vector<long> meshIndexList = vertexContainedInMeshList_.at( vertexIndex );

		for( size_t i = 0 ; i < meshIndexList.size(); ++i )
		{
			long meshIndex = meshIndexList.at( i );
			sum += _normalsOfMesh[meshIndex];
			count++;
		}

		// ���_�̖@���x�N�g�����v�Z
		tmpnormal = sum[0] / count, sum[1] / count, sum[2] / count;
		normal= tvmet::normalize( tmpnormal );

		tmpNormalsOfVertex.push_back( normal );
	}


	// ���_�Q�̖@�����X�g���N���A����
	_normalsOfVertex.clear();
	_normalIndex.clear();

	// �@�����X�g�̃C���f�b�N�X (�@�����X�g�̉��Ԃ�������)
	int normalVertexIndex = 0;

	// ���b�V�������ɒH��
	for( size_t i = 0 ; i < triangleNum ; ++i )
	{
		// �O�p���b�V�����X�g i�Ԗڂ̃��b�V���̖ʖ@��
		vector3d nM = _normalsOfMesh.at( i );

		// �O�p���b�V�����X�g i�Ԗڂ̃��b�V��
		vector3i triangle = triangleList.at( i );
		
		vector3i normalIndex;		// �e���|������Ɨp

		// �O�p���b�V�����\�����钸�_�����ɒH��
		for( int j = 0 ; j < 3 ; ++j )
		{
			// ���_�C���f�b�N�X
			int vertexIndex = triangle[j];

			// ���̒��_�̖@���́C
			vector3d nV = tmpNormalsOfVertex.at( vertexIndex );

			// ���_�̖@���x�N�g���ƁC�ʂ̖@���x�N�g���̂Ȃ��p��
			double angle = acos( ( nM[0] * nV[0] + nM[1] * nV[1] + nM[2] * nV[2] ) 
								/ ( PRIVATE::_length( nM ) * ( PRIVATE::_length( nV ) ) ) );

			// �܂�ڊp�x�Ɣ�r����
			if( angle <= creaseAngle )
			{
				// ���_�̖@���x�N�g�����̗p����
				_normalsOfVertex.push_back( nV );
			}
			else
			{
				// ���b�V��(��)�̖@���x�N�g�����̗p����
				_normalsOfVertex.push_back( nM );
			}
		
			//
			normalIndex[j] = normalVertexIndex;

			//  
			normalVertexIndex++;
		}

		// 
		_normalIndex.push_back( normalIndex );
	}

	return true;
}




//==================================================================================================
/*!
  @if jp

    @brief      �S�Ă̎O�p���b�V���̖@���x�N�g�����v�Z

    @note       �����ŗ^����ꂽ�O�p���b�V���Q�ɑΉ�����@���x�N�g�����v�Z����<BR>
				�v�Z�����@���� �����o�ϐ� _normalsOfMesh �Ɋi�[����<BR>
				_normalsOfMesh�̖@���́C����triangleList�̎O�p���b�V���̏��ƑΉ����Ă���<BR>

    @date       2008-04-11 Y.TSUNODA <BR>

    @return     bool true:���� / false:���s

  @endif
*/
//==================================================================================================
bool
CalculateNormal::calculateNormalsOfMesh( 
	vector<vector3d> vertexList,		//!< ���_�Q
	vector<vector3i> triangleList )		//!< �O�p���b�V���Q
{
	// �O�p���b�V���Q�̖@�����X�g�C���_�E�O�p���b�V���Ή����X�g���N���A����
	_normalsOfMesh.clear();
	vertexContainedInMeshList_.clear();

	// ���_�E�O�p���b�V���Ή����X�g�̃T�C�Y�� ���_�� �m�ۂ��� 
	vertexContainedInMeshList_.resize( vertexList.size() );

	// �O�p���b�V����
	size_t triangleNum = triangleList.size();

	// �O�p���b�V�������ɒH��
	for( size_t i = 0 ; i < triangleNum ; ++i )
	{
		vector3i triangle = triangleList.at( i );

		// �@�����v�Z����
		vector3d normal;
		normal = _calculateNormalOfTraiangleMesh( vertexList[triangle[0]],
												  vertexList[triangle[1]],
												  vertexList[triangle[2]] );

		// �O�p���b�V���Q�̖@�����X�g�ɒǉ�����
		_normalsOfMesh.push_back( normal );

		// ���_�E�O�p���b�V���Ή����X�g�ɒǉ�����
		//   ���_ triangle[j] �́Ci�Ԗڂ̎O�p���b�V���Ɋ܂܂��
		for( int j = 0 ; j < 3 ; ++j )
		{
			vertexContainedInMeshList_.at( triangle[j] ).push_back( i );
		}
	}

	return true;
}





//==================================================================================================
/*!
  @if jp

    @brief      �O�p���b�V���̖@���x�N�g���v�Z

    @note       �����ŗ^����ꂽ�O���_�ō\�������O�p���b�V���̖@���x�N�g�����v�Z����<BR>

    @date       2008-04-11 Y.TSUNODA <BR>

    @return     vector3d �@���x�N�g��

  @endif
*/
//==================================================================================================
vector3d
CalculateNormal::_calculateNormalOfTraiangleMesh(
	vector3d a,		//!< �O�p���b�V�����\�����钸�_1
	vector3d b,		//!< �O�p���b�V�����\�����钸�_2
	vector3d c )	//!< �O�p���b�V�����\�����钸�_3
{
	vector3d normal;
	normal = tvmet::normalize( tvmet::cross( b - a, c - a ) );

	return normal;
}



