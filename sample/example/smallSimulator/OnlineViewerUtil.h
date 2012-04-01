#include <hrpCorba/OpenHRPCommon.hh>
#include <hrpModel/Body.h>
#include <hrpModel/World.h>

void setupCharacterPosition(OpenHRP::CharacterPosition &characterPosition, 
                             hrp::BodyPtr body); 
void updateCharacterPosition(OpenHRP::CharacterPosition &characterPosition, 
                             hrp::BodyPtr body);
void getWorldState(OpenHRP::WorldState& state,  hrp::WorldBase& world);
void initWorldState(OpenHRP::WorldState& state, hrp::WorldBase& world);

