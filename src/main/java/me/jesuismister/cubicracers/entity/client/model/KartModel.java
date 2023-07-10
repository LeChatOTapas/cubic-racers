package me.jesuismister.cubicracers.entity.client.model;

import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.custom.Kart;
import me.jesuismister.cubicracers.util.KeyBinds;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

import java.util.Optional;

public class KartModel extends GeoModel<Kart> {
    private static float WHEELS_ROTATION_DEGREE = 18; //WHEN THE KART IS MOVING (FORWARD OR BACKWARD)
    private static float WHEELS_TURN_DEGREE = 15; //WHEN THE KART IS TURNING (LEFT OR RIGHT)
    private static float ROTATE_KART_DEGREE = 5; //WHEN THE KART IS TURNING IN THE AIR (LEFT OR RIGHT WITH GLIDER)

    @Override
    public ResourceLocation getModelResource(Kart kart) {
        return new ResourceLocation(CubicRacers.MODID, kart.MODEL);
    }

    @Override
    public ResourceLocation getTextureResource(Kart kart) {
        return new ResourceLocation(CubicRacers.MODID, kart.TEXTURE);
    }

    @Override
    public ResourceLocation getAnimationResource(Kart kart) {
        return new ResourceLocation(CubicRacers.MODID, kart.ANIMATION);
    }

    @Override
    public void setCustomAnimations(Kart kart, long instanceId, AnimationState<Kart> animationState) {
        super.setCustomAnimations(kart, instanceId, animationState);

        Optional<GeoBone> bonesKart = this.getBone("kart");
        if (!bonesKart.isPresent()) {
            return;
        }

        //INCLINAISON DES ROUES
        if (!kart.getDeltaOn()) {
            kart.setPourcentageInclinaison(0);

            //ON RECUPERE LES BONES DES ROUES
            Optional<GeoBone> bonesGaucheAvant = this.getBone("front_left");
            Optional<GeoBone> bonesDroiteAvant = this.getBone("front_right");
            Optional<GeoBone> bonesGaucheArriere = this.getBone("back_left");
            Optional<GeoBone> bonesDroiteArriere = this.getBone("back_right");

            //BONES DU PROPELLER
            Optional<GeoBone> bonesPropeller = this.getBone("propeller");

            //ON CALCUL L'ANGLE D'INCLINAISON EN Y
            float rotYGauche = 0;
            float rotYDroit = bonesDroiteArriere.get().getRotY();

            //DRIFT A GAUCHE
            if (kart.getIsDrifting() && kart.getDriftingSens().equals("Left")) {
                //PLUS MAINTIENT GAUCHE
                if (kart.getIsPressingKeyLeft() && !kart.getIsPressingKeyRight()) {
                    rotYGauche = 1.5f * WHEELS_TURN_DEGREE * Mth.DEG_TO_RAD;
                    rotYDroit = (1.5f * WHEELS_TURN_DEGREE + 180) * Mth.DEG_TO_RAD;
                }
                //PLUS MAINTIENT DROITE
                else if (kart.getIsPressingKeyRight() && !kart.getIsPressingKeyLeft()) {
                    rotYGauche = 0.45f * WHEELS_TURN_DEGREE * Mth.DEG_TO_RAD;
                    rotYDroit = (0.45f * WHEELS_TURN_DEGREE + 180) * Mth.DEG_TO_RAD;
                }
                //NE MAINTIENT RIEN
                else {
                    rotYGauche = 1.125f * WHEELS_TURN_DEGREE * Mth.DEG_TO_RAD;
                    rotYDroit = (1.125f * WHEELS_TURN_DEGREE + 180) * Mth.DEG_TO_RAD;
                }
            }
            //DRIFT A DROITE
            else if (kart.getIsDrifting() && kart.getDriftingSens().equals("Right")) {
                //PLUS MAINTIENT DROITE
                if (kart.getIsPressingKeyRight() && !kart.getIsPressingKeyLeft()) {
                    rotYGauche = -1.5f * WHEELS_TURN_DEGREE * Mth.DEG_TO_RAD;
                    rotYDroit = (-1.5f * WHEELS_TURN_DEGREE - 180) * Mth.DEG_TO_RAD;
                }
                //PLUS MAINTIENT GAUCHE
                else if (kart.getIsPressingKeyLeft() && !kart.getIsPressingKeyRight()) {
                    rotYGauche = -0.45f * WHEELS_TURN_DEGREE * Mth.DEG_TO_RAD;
                    rotYDroit = (-0.45f * WHEELS_TURN_DEGREE - 180) * Mth.DEG_TO_RAD;
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
                if (kart.getIsPressingKeyLeft() && !kart.getIsPressingKeyRight()) {
                    rotYGauche = WHEELS_TURN_DEGREE * Mth.DEG_TO_RAD;
                } else if (kart.getIsPressingKeyRight() && !kart.getIsPressingKeyLeft()) {
                    rotYGauche = -1 * WHEELS_TURN_DEGREE * Mth.DEG_TO_RAD;
                }
                //ROUES A DROITES
                if (kart.getIsPressingKeyLeft() && !kart.getIsPressingKeyRight()) {
                    rotYDroit = (WHEELS_TURN_DEGREE + 180) * Mth.DEG_TO_RAD;
                } else if (kart.getIsPressingKeyRight() && !kart.getIsPressingKeyLeft()) {
                    rotYDroit = (-WHEELS_TURN_DEGREE - 180) * Mth.DEG_TO_RAD;
                }
            }

            //ON CALCUL L'ANGLE DE ROTATION EN X
            kart.setActualRotationWheels((float) (kart.getActualRotationWheels() +
                    Math.toRadians(kart.getSpeed() * WHEELS_ROTATION_DEGREE)) % Math.abs(360));

            //UPDATE
            if(bonesGaucheAvant.isPresent()) bonesGaucheAvant.get().updateRotation(-kart.getActualRotationWheels(), rotYGauche, 0);
            if(bonesDroiteAvant.isPresent()) bonesDroiteAvant.get().updateRotation(-kart.getActualRotationWheels(), rotYDroit, 0);
            if(bonesGaucheArriere.isPresent()) bonesGaucheArriere.get().updateRotation(-kart.getActualRotationWheels(), 0, 0);
            if(bonesDroiteArriere.isPresent()) bonesDroiteArriere.get().updateRotation(-kart.getActualRotationWheels(), bonesDroiteArriere.get().getRotY(), 0);

            if(bonesPropeller.isPresent()) bonesPropeller.get().updateRotation(0, 0, -2* kart.getActualRotationWheels());
        }
        //INCLINAISON DU VEHICULE (PAS BESOIN DE BOUGER LES ROUES EN DELTA PLANE)
        else {
            //INCLINAISON DU VEHICULE A GAUCHE
            if (kart.getIsPressingKeyLeft() && !kart.getIsPressingKeyRight()) {
                if (kart.getPourcentageInclinaison() < 1.0f) kart.setPourcentageInclinaison(kart.getPourcentageInclinaison() + 0.02f);
            }
            //INCLINAISON DU VEHICULE A DROITE
            else if (kart.getIsPressingKeyRight() && !kart.getIsPressingKeyLeft()) {
                if (kart.getPourcentageInclinaison() > -1.0f) kart.setPourcentageInclinaison(kart.getPourcentageInclinaison() - 0.02f);
            }
            //ON RECENTRE LE VEHICULE
            else if (kart.getPourcentageInclinaison() != 0.0f) {
                kart.setPourcentageInclinaison((float) (kart.getPourcentageInclinaison() + (-(Math.abs(kart.getPourcentageInclinaison()) / kart.getPourcentageInclinaison()) * 0.02)));
            }

            //UPDATE
            bonesKart.get().updateRotation(0, 0, ROTATE_KART_DEGREE * Mth.DEG_TO_RAD * kart.getPourcentageInclinaison());
        }
    }
}
