package me.jesuismister.cubicracers.entity.client.model;

import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.custom.Kart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

import java.util.Optional;

public class KartModel extends GeoModel<Kart> {
    private static float WHEELS_ROTATION_DEGREE = 30;
    private static float WHEELS_TURN_DEGREE = 15;
    private static float ROTATE_KART_DEGREE = 5;

    @Override
    public ResourceLocation getModelResource(Kart kart) {
        return new ResourceLocation(CubicRacers.MODID, "geo/trash_kart.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Kart kart) {
        return new ResourceLocation(CubicRacers.MODID, "textures/entity/trash_kart.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Kart kart) {
        return new ResourceLocation(CubicRacers.MODID, "animations/trash_kart.animation.json");
    }

    @Override
    public void setCustomAnimations(Kart kart, long instanceId, AnimationState<Kart> animationState) {
        super.setCustomAnimations(kart, instanceId, animationState);

        //INCLINAISON DES ROUES
        if (!kart.deltaOn) {
            kart.pourcentage_inclinaison = 0;

            //ON RECUPERE LES BONES DES ROUES
            Optional<GeoBone> bonesGaucheAvant = this.getBone("roue_avant_gauche");
            Optional<GeoBone> bonesDroiteAvant = this.getBone("roue_avant_droite");
            Optional<GeoBone> bonesGaucheArriere = this.getBone("roue_arriere_gauche");
            Optional<GeoBone> bonesDroiteArriere= this.getBone("roue_arriere_droite");

            if(!bonesGaucheArriere.isPresent() || !bonesDroiteArriere.isPresent() || !bonesGaucheAvant.isPresent() || !bonesDroiteAvant.isPresent())
                return;

            //ON CALCUL L'ANGLE D'INCLINAISON EN Y
            float rotYGauche = 0;
            float rotYDroit = bonesDroiteArriere.get().getRotY();

            //DRIFT A GAUCHE
            if (kart.isDrifting && kart.driftingSens.equals("Left")) {
                //PLUS MAINTIENT GAUCHE
                if(kart.keyLeft.isDown() && !kart.keyRight.isDown()){
                    rotYGauche = 3 * WHEELS_TURN_DEGREE * Mth.DEG_TO_RAD;
                    rotYDroit = (3 * WHEELS_TURN_DEGREE + 180) * Mth.DEG_TO_RAD;
                //PLUS MAINTIENT DROITE
                }else if (kart.keyRight.isDown() && !kart.keyLeft.isDown()){
                    rotYGauche = 1.5f * WHEELS_TURN_DEGREE * Mth.DEG_TO_RAD;
                    rotYDroit = (1.5f * WHEELS_TURN_DEGREE + 180) * Mth.DEG_TO_RAD;
                //NE MAINTIENT RIEN
                }else{
                    rotYGauche = 2.25f * WHEELS_TURN_DEGREE * Mth.DEG_TO_RAD;
                    rotYDroit = (2.25f * WHEELS_TURN_DEGREE + 180) * Mth.DEG_TO_RAD;
                }
            //DRIFT A DROITE
            }else if (kart.isDrifting && kart.driftingSens.equals("Right")) {
                //PLUS MAINTIENT DROITE
                if(kart.keyRight.isDown() && !kart.keyLeft.isDown()){
                    rotYGauche = -3 * WHEELS_TURN_DEGREE * Mth.DEG_TO_RAD;
                    rotYDroit =  (-3 * WHEELS_TURN_DEGREE - 180) * Mth.DEG_TO_RAD;
                //PLUS MAINTIENT GAUCHE
                }else if (kart.keyLeft.isDown() && !kart.keyRight.isDown()){
                    rotYGauche = -1.5f * WHEELS_TURN_DEGREE * Mth.DEG_TO_RAD;
                    rotYDroit =  (-1.5f * WHEELS_TURN_DEGREE - 180) * Mth.DEG_TO_RAD;
                //NE MAINTIENT RIEN
                }else {
                    rotYGauche = -2.25f * WHEELS_TURN_DEGREE * Mth.DEG_TO_RAD;
                    rotYDroit =  (-2.25f * WHEELS_TURN_DEGREE - 180) * Mth.DEG_TO_RAD;
                }
            //LE KART NE DRIFT PAS
            }else{
                //ROUES A GAUCHES
                if (kart.keyLeftDown() && !kart.keyRightDown()) {
                    rotYGauche = WHEELS_TURN_DEGREE * Mth.DEG_TO_RAD;
                }else if (kart.keyRightDown() && !kart.keyLeftDown()) {
                    rotYGauche = -1 * WHEELS_TURN_DEGREE * Mth.DEG_TO_RAD;
                }
                //ROUES A DROITES
                if (kart.keyLeftDown() && !kart.keyRightDown()) {
                    rotYDroit = (WHEELS_TURN_DEGREE + 180) * Mth.DEG_TO_RAD;
                } else if (kart.keyRightDown() && !kart.keyLeftDown()) {
                    rotYDroit = (-WHEELS_TURN_DEGREE - 180) * Mth.DEG_TO_RAD;
                }
            }

            //ON CALCUL L'ANGLE DE ROTATION EN X
            kart.actual_rotation_wheels += Math.toRadians((kart.getSpeed()/kart.MAX_SPEED) * WHEELS_ROTATION_DEGREE);

            //UPDATE
            bonesGaucheAvant.get().updateRotation(-kart.actual_rotation_wheels, rotYGauche, 0);
            bonesDroiteAvant.get().updateRotation(kart.actual_rotation_wheels, rotYDroit, 0);
            bonesGaucheArriere.get().updateRotation(-kart.actual_rotation_wheels, 0, 0);
            bonesDroiteArriere.get().updateRotation(kart.actual_rotation_wheels, bonesDroiteArriere.get().getRotY(), 0);

        //INCLINAISON DU VEHICULE (PAS BESOIN DE BOUGER LES ROUES CAR PAS DE ROUE EN DELTA PLANE)
        } else {
            Optional<GeoBone> bonesKart = this.getBone("kart");
            if(!bonesKart.isPresent()) return;

            //INCLINAISON DU VEHICULE A GAUCHE
            if(kart.keyLeftDown() && !kart.keyRightDown()){
                if(kart.pourcentage_inclinaison < 1.0f) kart.pourcentage_inclinaison += 0.02f;
            }
            //INCLINAISON DU VEHICULE A DROITE
            else if(kart.keyRightDown() && !kart.keyLeftDown()){
                if(kart.pourcentage_inclinaison > -1.0f) kart.pourcentage_inclinaison -= 0.02f;
            }
            //ON RECENTRE LE VEHICULE
            else if(kart.pourcentage_inclinaison!=0.0f){
                kart.pourcentage_inclinaison = (float) (kart.pourcentage_inclinaison + (-(Math.abs(kart.pourcentage_inclinaison)/kart.pourcentage_inclinaison) * 0.02));
            }

            //UPDATE
            bonesKart.get().updateRotation(0, 0, ROTATE_KART_DEGREE * Mth.DEG_TO_RAD * kart.pourcentage_inclinaison);
        }
    }
}
