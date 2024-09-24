package me.jesuismister.cubicracers.entity.client.model;

import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.custom.TestKart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

import java.util.NoSuchElementException;
import java.util.Optional;

public class TestKartModel extends GeoModel<TestKart> {
    private static final float WHEELS_ROTATION_DEGREE = 18; //WHEN THE KART IS MOVING (FORWARD OR BACKWARD)
    private static final float WHEELS_TURN_DEGREE = 15; //WHEN THE KART IS TURNING (LEFT OR RIGHT)
    private static final float ROTATE_KART_DEGREE = 5; //WHEN THE KART IS TURNING IN THE AIR (LEFT OR RIGHT WITH GLIDER)

    @Override
    public ResourceLocation getModelResource(TestKart kart) {
        return new ResourceLocation(CubicRacers.MODID, kart.MODEL);
    }

    @Override
    public ResourceLocation getTextureResource(TestKart kart) {
        System.out.println("=> " + kart.isInvinsible());
        if(kart.isInvinsible()) return new ResourceLocation(CubicRacers.MODID, "textures/entity/star_model.png");
        else return new ResourceLocation(CubicRacers.MODID, kart.TEXTURE);
    }

    @Override
    public ResourceLocation getAnimationResource(TestKart kart) {
        return new ResourceLocation(CubicRacers.MODID, kart.ANIMATION);
    }

    @Override
    public void setCustomAnimations(TestKart kart, long instanceId, AnimationState<TestKart> animationState) {
        super.setCustomAnimations(kart, instanceId, animationState);

        Optional<GeoBone> bonesKart = this.getBone("kart");
        if (!bonesKart.isPresent()) {
            return;
        }

        //INCLINAISON DES ROUES
        if (!kart.isDeltaOn()) {
            kart.setPourcentage_inclinaison(0);

            //ON RECUPERE LES BONES DES ROUES
            Optional<GeoBone> bonesGaucheAvant = this.getBone("front_left");
            Optional<GeoBone> bonesDroiteAvant = this.getBone("front_right");
            Optional<GeoBone> bonesGaucheArriere = this.getBone("back_left");
            Optional<GeoBone> bonesDroiteArriere = this.getBone("back_right");

            //BONES DU PROPELLER
            Optional<GeoBone> bonesPropeller = this.getBone("propeller");

            //ON CALCUL L'ANGLE D'INCLINAISON EN Y
            float rotYGauche = 0;
            float rotYDroit;
            try{
                rotYDroit = bonesDroiteArriere.get().getRotY();
            }catch(NoSuchElementException e){
                System.out.println("INVALID KART FORMAT");
                return;
            }

            //DRIFT A GAUCHE
            if (kart.isDrifting() && kart.getDriftingSens().equals("Left")) {
                //PLUS MAINTIENT GAUCHE
                if (kart.isPressingKeyLeft() && !kart.isPressingKeyRight()) {
                    rotYGauche = 1.75f * WHEELS_TURN_DEGREE * Mth.DEG_TO_RAD;
                    rotYDroit = (1.75f * WHEELS_TURN_DEGREE) * Mth.DEG_TO_RAD;
                }
                //PLUS MAINTIENT DROITE
                else if (kart.isPressingKeyRight() && !kart.isPressingKeyLeft()) {
                    rotYGauche = -1.125f * WHEELS_TURN_DEGREE * Mth.DEG_TO_RAD;
                    rotYDroit = (-1.125f * WHEELS_TURN_DEGREE) * Mth.DEG_TO_RAD;
                }
                //NE MAINTIENT RIEN
                else {
                    rotYGauche = 1.125f * WHEELS_TURN_DEGREE * Mth.DEG_TO_RAD;
                    rotYDroit = (1.125f * WHEELS_TURN_DEGREE) * Mth.DEG_TO_RAD;
                }
            }
            //DRIFT A DROITE
            else if (kart.isDrifting() && kart.getDriftingSens().equals("Right")) {
                //PLUS MAINTIENT DROITE
                if (kart.isPressingKeyRight() && !kart.isPressingKeyLeft()) {
                    rotYGauche = -1.75f * WHEELS_TURN_DEGREE * Mth.DEG_TO_RAD;
                    rotYDroit = (-1.75f * WHEELS_TURN_DEGREE - 180) * Mth.DEG_TO_RAD;
                }
                //PLUS MAINTIENT GAUCHE
                else if (kart.isPressingKeyLeft() && !kart.isPressingKeyRight()) {
                    rotYGauche = 1.125f * WHEELS_TURN_DEGREE * Mth.DEG_TO_RAD;
                    rotYDroit = (1.125f * WHEELS_TURN_DEGREE - 180) * Mth.DEG_TO_RAD;
                }
                //NE MAINTIENT RIEN
                else {
                    rotYGauche = -1.125f * WHEELS_TURN_DEGREE * Mth.DEG_TO_RAD;
                    rotYDroit = (-1.125f * WHEELS_TURN_DEGREE - 180) * Mth.DEG_TO_RAD;
                }
            }
            //LE KART NE DRIFT PAS
            else {
                //ROUES A GAUCHES
                if (kart.isPressingKeyLeft() && !kart.isPressingKeyRight()) {
                    rotYGauche = WHEELS_TURN_DEGREE * Mth.DEG_TO_RAD;
                } else if (kart.isPressingKeyRight() && !kart.isPressingKeyLeft()) {
                    rotYGauche = -1 * WHEELS_TURN_DEGREE * Mth.DEG_TO_RAD;
                }
                //ROUES A DROITES
                if (kart.isPressingKeyLeft() && !kart.isPressingKeyRight()) {
                    rotYDroit = (WHEELS_TURN_DEGREE) * Mth.DEG_TO_RAD;
                } else if (kart.isPressingKeyRight() && !kart.isPressingKeyLeft()) {
                    rotYDroit = (-WHEELS_TURN_DEGREE) * Mth.DEG_TO_RAD;
                }
            }

            //ON CALCUL L'ANGLE DE ROTATION EN X
            kart.setActual_rotation_wheels((float) (kart.getActual_rotation_wheels() +
                    Math.toRadians(kart.getSpeed() * WHEELS_ROTATION_DEGREE)) % Math.abs(360));

            //UPDATE
            if(bonesGaucheAvant.isPresent()) bonesGaucheAvant.get().updateRotation(-kart.getActual_rotation_wheels(), rotYGauche, 0);
            if(bonesDroiteAvant.isPresent()) bonesDroiteAvant.get().updateRotation(-kart.getActual_rotation_wheels(), rotYDroit, 0);
            if(bonesGaucheArriere.isPresent()) bonesGaucheArriere.get().updateRotation(-kart.getActual_rotation_wheels(), 0, 0);
            if(bonesDroiteArriere.isPresent()) bonesDroiteArriere.get().updateRotation(-kart.getActual_rotation_wheels(), bonesDroiteArriere.get().getRotY(), 0);

            if(bonesPropeller.isPresent()) bonesPropeller.get().updateRotation(0, 0, -2* kart.getActual_rotation_wheels());
        }
        //INCLINAISON DU VEHICULE (PAS BESOIN DE BOUGER LES ROUES EN DELTA PLANE)
        else {
            //INCLINAISON DU VEHICULE A GAUCHE
            if (kart.isPressingKeyLeft() && !kart.isPressingKeyRight()) {
                if (kart.getPourcentage_inclinaison() < 1.0f) kart.setPourcentage_inclinaison(kart.getPourcentage_inclinaison() + 0.02f);
            }
            //INCLINAISON DU VEHICULE A DROITE
            else if (kart.isPressingKeyRight() && !kart.isPressingKeyLeft()) {
                if (kart.getPourcentage_inclinaison() > -1.0f) kart.setPourcentage_inclinaison(kart.getPourcentage_inclinaison() - 0.02f);
            }
            //ON RECENTRE LE VEHICULE
            else if (kart.getPourcentage_inclinaison() != 0.0f) {
                kart.setPourcentage_inclinaison((float) (kart.getPourcentage_inclinaison() + (-(Math.abs(kart.getPourcentage_inclinaison()) / kart.getPourcentage_inclinaison()) * 0.02)));
            }

            //UPDATE
            bonesKart.get().updateRotation(0, 0, ROTATE_KART_DEGREE * Mth.DEG_TO_RAD * kart.getPourcentage_inclinaison());
        }

    }
}
