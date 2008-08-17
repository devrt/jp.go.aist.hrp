
/**
   @author Shin'ichiro Nakaoka
*/

#ifndef HRP_COLLISION_COLDET_MODEL_PAIR_H_INCLUDED
#define HRP_COLLISION_COLDET_MODEL_PAIR_H_INCLUDED

#include "config.h"
#include "CollisionData.h"
#include "ColdetModel.h"

namespace hrp {

    class HRP_COLLISION_EXPORT ColdetModelPair
    {
      public:
        ColdetModelPair();
        ColdetModelPair(ColdetModelPtr model0, ColdetModelPtr model1);
        ColdetModelPair(const ColdetModelPair& org);
        virtual ~ColdetModelPair();

        void set(ColdetModelPtr model0, ColdetModelPtr model1);
        ColdetModelPtr model0() { return model0_; }
        ColdetModelPtr model1() { return model1_; }
        
        collision_data* detectCollisions();

        bool checkCollision();

      private:
        collision_data* detectCollisionsSub(bool detectAllContacts);
        
        ColdetModelPtr model0_;
        ColdetModelPtr model1_;
    };
}


#endif
