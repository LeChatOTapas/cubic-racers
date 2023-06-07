package me.jesuismister.cubicracers.entity.client.model;

import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.custom.Kart;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector3d;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

import java.util.Optional;

public class KartModel extends GeoModel<Kart> {
    private static float ROTATE_WHEELS_DEGREE = 15;
    public static float ROTATE_KART_DEGREE = 5;

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

        Player player = (Player) kart.getFirstPassenger();
        if (player == null)
            return;

        if (!kart.deltaOn) {
            Optional<GeoBone> bonesGauches = this.getBone("roue_avant_gauche");
            Optional<GeoBone> bonesDroit = this.getBone("roue_avant_droite");
            kart.pourcentage_inclinaison = 0;

            //DRIFT A GAUCHE
            if (kart.isDrifting && kart.driftingSens.equals("Left")) {
                //PLUS MAINTIENT GAUCHE
                if(kart.keyLeft.isDown() && !kart.keyRight.isDown()){
                    bonesGauches.get().updateRotation(0, 3 * ROTATE_WHEELS_DEGREE * Mth.DEG_TO_RAD, 0);
                    bonesDroit.get().updateRotation(0, (3 * ROTATE_WHEELS_DEGREE + 180) * Mth.DEG_TO_RAD, 0);
                //PLUS MAINTIENT DROITE
                }else if (kart.keyRight.isDown() && !kart.keyLeft.isDown()){
                    bonesGauches.get().updateRotation(0, 1.5f * ROTATE_WHEELS_DEGREE * Mth.DEG_TO_RAD, 0);
                    bonesDroit.get().updateRotation(0, (1.5f * ROTATE_WHEELS_DEGREE + 180) * Mth.DEG_TO_RAD, 0);
                //NE MAINTIENT RIEN
                }else{
                    bonesGauches.get().updateRotation(0, 2.25f * ROTATE_WHEELS_DEGREE * Mth.DEG_TO_RAD, 0);
                    bonesDroit.get().updateRotation(0, (2.25f * ROTATE_WHEELS_DEGREE + 180) * Mth.DEG_TO_RAD, 0);
                }
            //DRIFT A DROITE
            }else if (kart.isDrifting && kart.driftingSens.equals("Right")) {
                //PLUS MAINTIENT DROITE
                if(kart.keyRight.isDown() && !kart.keyLeft.isDown()){
                    bonesGauches.get().updateRotation(0, -3 * ROTATE_WHEELS_DEGREE * Mth.DEG_TO_RAD, 0);
                    bonesDroit.get().updateRotation(0,  (-3 * ROTATE_WHEELS_DEGREE - 180) * Mth.DEG_TO_RAD, 0);
                //PLUS MAINTIENT GAUCHE
                }else if (kart.keyLeft.isDown() && !kart.keyRight.isDown()){
                    bonesGauches.get().updateRotation(0, -1.5f * ROTATE_WHEELS_DEGREE * Mth.DEG_TO_RAD, 0);
                    bonesDroit.get().updateRotation(0,  (-1.5f * ROTATE_WHEELS_DEGREE - 180) * Mth.DEG_TO_RAD, 0);
                //NE MAINTIENT RIEN
                }else {
                    bonesGauches.get().updateRotation(0, -2.25f * ROTATE_WHEELS_DEGREE * Mth.DEG_TO_RAD, 0);
                    bonesDroit.get().updateRotation(0,  (-2.25f * ROTATE_WHEELS_DEGREE - 180) * Mth.DEG_TO_RAD, 0);
                }
            //LE KART NE DRIFT PAS
            }else{
                //ROUES A GAUCHES
                if (bonesGauches.isPresent()) {
                    if (kart.keyLeftDown() && !kart.keyRightDown()) {
                        bonesGauches.get().updateRotation(0, ROTATE_WHEELS_DEGREE * Mth.DEG_TO_RAD, 0);
                    }else if (kart.keyRightDown() && !kart.keyLeftDown()) {
                        bonesGauches.get().updateRotation(0, -1 * ROTATE_WHEELS_DEGREE * Mth.DEG_TO_RAD, 0);
                    }
                }
                //ROUES A DROITES
                if (bonesDroit.isPresent()) {
                    if (kart.keyLeftDown() && !kart.keyRightDown()) {
                        bonesDroit.get().updateRotation(0, (ROTATE_WHEELS_DEGREE + 180) * Mth.DEG_TO_RAD, 0);
                    } else if (kart.keyRightDown() && !kart.keyLeftDown()) {
                        bonesDroit.get().updateRotation(0, (-ROTATE_WHEELS_DEGREE - 180) * Mth.DEG_TO_RAD, 0);
                    }
                }
            }
        } else {
            Optional<GeoBone> bonesKart = this.getBone("kart");

            if (bonesKart.isPresent()) {
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
                //APPLICATION DE L'INCLINAISON
                bonesKart.get().updateRotation(0, 0, ROTATE_KART_DEGREE * Mth.DEG_TO_RAD * kart.pourcentage_inclinaison);
            }
        }
    }
}
