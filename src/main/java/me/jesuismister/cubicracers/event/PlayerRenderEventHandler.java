package me.jesuismister.cubicracers.event;

import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.custom.Kart;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Quaternionf;

@Mod.EventBusSubscriber(modid = CubicRacers.MODID, value = Dist.CLIENT)
public class PlayerRenderEventHandler {

    @SubscribeEvent
    public static void onPlayerRenderPre(RenderLivingEvent.Pre<Player, PlayerModel<Player>> event) {
        if(event.getEntity() instanceof Player player){
            if (player.isPassenger() && player.getVehicle() instanceof Kart) {
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
            if (player.isPassenger() && player.getVehicle() instanceof Kart) {
                event.getPoseStack().popPose();
            }
        }
    }
}
