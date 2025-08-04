package me.jesuismister.cubicracers.items;

import me.jesuismister.cubicracers.entity.custom.ItemBox;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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

public class ItemBoxSpawnItem extends Item {
    private static final Predicate<Entity> ENTITY_PREDICATE = EntitySelector.NO_SPECTATORS.and(Entity::isPickable);

    public ItemBoxSpawnItem(Properties properties) {
        super(properties);
    }

    public InteractionResult use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        HitResult hitresult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);
        //SI LE TARGET DU CLIQUE DU JOUEUR EST PAS VALIDE, ON PASSE
        if (hitresult.getType() == HitResult.Type.MISS) {
            return InteractionResult.PASS;
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
                    return InteractionResult.PASS;
                }
            }

            //SI LE TARGET DU CLIQUE DU JOUEUR EST UN BLOCK
            if (hitresult.getType() == HitResult.Type.BLOCK) {
                ItemBox itemBox = new ItemBox(level, hitresult.getLocation().x, hitresult.getLocation().y, hitresult.getLocation().z);
                itemBox.setYRot(player.getYRot());
                //SI LE CUBE N'A PAS ASSEZ DE PLACE POUR SPAWN, ON ARRETE
                if (!level.noCollision(itemBox, itemBox.getBoundingBox())) {
                    return InteractionResult.FAIL;
                }
                //SINON ON LE FAIT SPAWN
                else {
                    //COTES SERVEUR
                    if (!level.isClientSide) {
                        level.addFreshEntity(itemBox);
                        level.gameEvent(player, GameEvent.ENTITY_PLACE, hitresult.getLocation());
                        //ON ENLEVE L'ITEM DE L'INVENTAIRE SI NECESSAIRE
                        if (!player.getAbilities().instabuild) {
                            itemstack.shrink(1);
                        }
                    }
                    player.awardStat(Stats.ITEM_USED.get(this));
                    return InteractionResult.SUCCESS;
                }
            }
            //SINON ON ARRETE
            else {
                return InteractionResult.PASS;
            }
        }
    }
}