package me.jesuismister.cubicracers.event;

import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.custom.TestKart;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import org.joml.Quaternionf;

@EventBusSubscriber(modid = CubicRacers.MODID, value = Dist.CLIENT)
public class PlayerRenderEventHandler {
/*
    @SubscribeEvent
    public static void onPlayerRenderPre(RenderLivingEvent.Pre<Player, PlayerModel<Player>> event) {
        if(event.getEntity() instanceof Player player){
            if (player.isPassenger() && player.getVehicle() instanceof TestKart) {
                //Kart kart = (Kart) player.getVehicle();

                //TO DO
                //JE VEUX INCLINER LE JOUEUR EN MÊME TEMPS QUE LE VEHICULE
                //QUAND IL EST EN DELTA PLANE, MAIS JE N'YN ARRIVE PAS
                Quaternionf q = new Quaternionf();
                q.rotateZ((float) Math.toRadians(0));
                q.rotateZ((float) Math.toRadians(0));
                q.rotateZ((float) Math.toRadians(0));

                //kart.sendConductorMessage("P | x = " + player.getXRot() + " / y = " + player.getYRot());
                //kart.sendConductorMessage("K | x = " + kart.getXRot() + " / y = " + kart.getYRot());
                //kart.sendConductorMessage("Q | x = " + q.x() + " / y = " + q.y() + " / z = " + q.z());
                //kart.sendConductorMessage("");

                event.getPoseStack().pushPose();
                event.getPoseStack().mulPose(q);
            }
        }
    }
    @SubscribeEvent
    public static void onPlayerRenderPost(RenderLivingEvent.Post<Player, PlayerModel<Player>> event) {
        if(event.getEntity() instanceof Player player) {
            if (player.isPassenger() && player.getVehicle() instanceof TestKart) {
                event.getPoseStack().popPose();
            }
        }
    }
*/
}
