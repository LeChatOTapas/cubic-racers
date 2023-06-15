package me.jesuismister.cubicracers.entity;

import me.jesuismister.cubicracers.entity.custom.Kart;
import me.jesuismister.cubicracers.init.KartInit;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;

public class KartSpawnItem extends Item {
    public String kartName;

    private static final Predicate<Entity> ENTITY_PREDICATE = EntitySelector.NO_SPECTATORS.and(Entity::isPickable);

    public KartSpawnItem(Properties properties, String name) {
        super(properties);
        kartName = name;
    }

    /**
     * Gère le spawn des karts grâce à leurs items respectifs
     *
     * @param level
     * @param player
     * @param hand
     * @return
     */
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        HitResult hitresult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);
        //SI LE TARGET DU CLIQUE DU JOUEUR EST PAS VALIDE, ON PASSE
        if (hitresult.getType() == HitResult.Type.MISS) {
            return InteractionResultHolder.pass(itemstack);
        }
        //SINON ON CONTINUE
        else {
            //RECUPERE ???? (MAXENCE, J'AI PAS COMPRIS)
            Vec3 vec3 = player.getViewVector(1.0F);
            List<Entity> list = level.getEntities(player, player.getBoundingBox().expandTowards(vec3.scale(5.0D)).inflate(1.0D), ENTITY_PREDICATE);

            //FAIT ??? (MAXENCE, J'AI PAS COMPRIS)
            Vec3 vec31 = player.getEyePosition();
            for (Entity entity : list) {
                AABB aabb = entity.getBoundingBox().inflate(entity.getPickRadius());
                if (aabb.contains(vec31)) {
                    return InteractionResultHolder.pass(itemstack);
                }
            }

            //SI LE TARGET DU CLIQUE DU JOUEUR EST UN BLOCK
            if (hitresult.getType() == HitResult.Type.BLOCK) {
                Kart kart = this.getKart(level, hitresult);
                kart.setYRot(player.getYRot());
                //SI LE KART N'A PAS ASSEZ DE PLACE POUR SPAWN, ON ARRETE
                if (!level.noCollision(kart, kart.getBoundingBox())) {
                    return InteractionResultHolder.fail(itemstack);
                }
                //SINON ON LE FAIT SPAWN
                else {
                    //COTES SERVEUR
                    if (!level.isClientSide) {
                        level.addFreshEntity(kart);
                        level.gameEvent(player, GameEvent.ENTITY_PLACE, hitresult.getLocation());
                        //ON ENLEVE L'ITEM DE L'INVENTAIRE SI NECESSAIRE
                        if (!player.getAbilities().instabuild) {
                            itemstack.shrink(1);
                        }
                    }
                    player.awardStat(Stats.ITEM_USED.get(this));
                    return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
                }
            }
            //SINON ON ARRETE
            else {
                return InteractionResultHolder.pass(itemstack);
            }
        }
    }

    /**
     * Récupère les datas du kart et créer un nouveau Kart
     *
     * @param level
     * @param hitResult
     * @return
     */
    private Kart getKart(Level level, HitResult hitResult) {
        KartData d = KartData.getKartData(KartInit.KARTS_DATA, this.kartName);
        assert d != null;
        return new Kart(level, hitResult.getLocation().x, hitResult.getLocation().y, hitResult.getLocation().z, d.name, d.texture, d.model, d.animation, d.maxSpeed, d.accelerationBoost, d.boost, d.maniabiliteCoeff, d.playerPosY);
    }
}