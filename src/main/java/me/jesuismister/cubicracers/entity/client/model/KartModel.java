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
    private static float WHEELS_ROTATION_DEGREE = 5;
    private static float WHEELS_TURN_DEGREE = 15;
    private static float ROTATE_KART_DEGREE = 5;

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

            if (!bonesGaucheArriere.isPresent() || !bonesDroiteArriere.isPresent() || !bonesGaucheAvant.isPresent() || !bonesDroiteAvant.isPresent())
                return;

            //ON CALCUL L'ANGLE D'INCLINAISON EN Y
            float rotYGauche = 0;
            float rotYDroit = bonesDroiteArriere.get().getRotY();

            //DRIFT A GAUCHE
            if (kart.getIsDrifting() && kart.getDriftingSens().equals("Left")) {
                //PLUS MAINTIENT GAUCHE
                if (kart.getIsPressingKeyLeft() && !kart.getIsPressingKeyRight()) {
                    rotYGauche = 3 * WHEELS_TURN_DEGREE * Mth.DEG_TO_RAD;
                    rotYDroit = (3 * WHEELS_TURN_DEGREE + 180) * Mth.DEG_TO_RAD;
                }
                //PLUS MAINTIENT DROITE
                else if (kart.getIsPressingKeyRight() && !kart.getIsPressingKeyLeft()) {
                    rotYGauche = 1.5f * WHEELS_TURN_DEGREE * Mth.DEG_TO_RAD;
                    rotYDroit = (1.5f * WHEELS_TURN_DEGREE + 180) * Mth.DEG_TO_RAD;
                }
                //NE MAINTIENT RIEN
                else {
                    rotYGauche = 2.25f * WHEELS_TURN_DEGREE * Mth.DEG_TO_RAD;
                    rotYDroit = (2.25f * WHEELS_TURN_DEGREE + 180) * Mth.DEG_TO_RAD;
                }
            }
            //DRIFT A DROITE
            else if (kart.getIsDrifting() && kart.getDriftingSens().equals("Right")) {
                //PLUS MAINTIENT DROITE
                if (kart.getIsPressingKeyRight() && !kart.getIsPressingKeyLeft()) {
                    rotYGauche = -3 * WHEELS_TURN_DEGREE * Mth.DEG_TO_RAD;
                    rotYDroit = (-3 * WHEELS_TURN_DEGREE - 180) * Mth.DEG_TO_RAD;
                }
                //PLUS MAINTIENT GAUCHE
                else if (kart.getIsPressingKeyLeft() && !kart.getIsPressingKeyRight()) {
                    rotYGauche = -1.5f * WHEELS_TURN_DEGREE * Mth.DEG_TO_RAD;
                    rotYDroit = (-1.5f * WHEELS_TURN_DEGREE - 180) * Mth.DEG_TO_RAD;
                }
                //NE MAINTIENT RIEN
                else {
                    rotYGauche = -2.25f * WHEELS_TURN_DEGREE * Mth.DEG_TO_RAD;
                    rotYDroit = (-2.25f * WHEELS_TURN_DEGREE - 180) * Mth.DEG_TO_RAD;
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
                    Math.toRadians(kart.getSpeed() * 10 * WHEELS_ROTATION_DEGREE)) % Math.abs(360));

            //UPDATE
            bonesGaucheAvant.get().updateRotation(-kart.getActualRotationWheels(), rotYGauche, 0);
            bonesDroiteAvant.get().updateRotation(-kart.getActualRotationWheels(), rotYDroit, 0);
            bonesGaucheArriere.get().updateRotation(-kart.getActualRotationWheels(), 0, 0);
            bonesDroiteArriere.get().updateRotation(-kart.getActualRotationWheels(), bonesDroiteArriere.get().getRotY(), 0);
        }
        //INCLINAISON DU VEHICULE (PAS BESOIN DE BOUGER LES ROUES CAR PAS DE ROUE EN DELTA PLANE)
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
