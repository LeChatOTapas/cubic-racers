package me.jesuismister.cubicracers.entity.custom;

import me.jesuismister.cubicracers.config.KartConfig;
import me.jesuismister.cubicracers.init.BlockInit;
import me.jesuismister.cubicracers.init.KartInit;
import me.jesuismister.cubicracers.network.Network;
import me.jesuismister.cubicracers.network.message.serverToClient.KartItemSynchMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class TestKart extends TestKartAbstract {
    public static float HITBOX_X;
    public static float HITBOX_Y;
    //CACHE
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    //PATH
    public final String TEXTURE;
    public final String MODEL;
    public final String ANIMATION;
    //OTHERS
    public final float PLAYER_POS_Y;
    public float speedToShow = 0;
    public int fallingTime = 0;

    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private double lerpYRot;
    private double lerpXRot;
    private float deltaRotation;

    public TestKart(EntityType<TestKart> entityType, Level level, int id, String texture, String model, String animation, float playerPosY, float hitboxX, float hitboxY) {
        super(entityType, level);
        this.id = id;
        TEXTURE = texture;
        MODEL = model;
        ANIMATION = animation;
        PLAYER_POS_Y = playerPosY;
        HITBOX_X = hitboxX;
        HITBOX_Y = hitboxY;
        setInvulnerable(false);
    }

    public TestKart(Level level, int id, double x, double y, double z, String name, String texture, String model, String animation,
                    float playerPosY, float hitboxX, float hitboxY) {
        this(KartInit.KARTS.get(name).get(), level, id, texture, model, animation, playerPosY, hitboxX, hitboxY);
        setPos(x, y, z);
        xo = x;
        yo = y;
        zo = z;
        setKartItem("None");
    }

    @Override
    protected void positionRider(Entity player, MoveFunction position) {
        super.positionRider(player, position);
        double x = player.getX();
        double y = player.getY() + PLAYER_POS_Y;
        double z = player.getZ();
        player.setPos(x, y, z);
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public void tick() {
        // Timers
        if (getDriftTimeBoost() > 0) setDriftTimeBoost(getDriftTimeBoost() - 0.1f);
        if (getTimeBoost() > 0) setTimeBoost(getTimeBoost() - 0.1f);
        if (getTimeStar() > 0) setTimeStar(getTimeStar() - 0.1f);
        if (isInvinsible() && getTimeStar() <= 0) {
            setInvinsible(false);
            setStarSpeedBoost(1f);
        }

        // Traitement spécifique au client
        if (level().isClientSide) {
            // Utilisation de DistExecutor pour exécuter le code client uniquement côté client
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                clientTick();

                // PARTICULE DE BOOST (seulement côté client)
                if (getTimeBoost() > 0 || getDriftTimeBoost() > 0) {
                    clientSpawnBoostParticules();
                }
            });
        }
        // Traitement spécifique au serveur
        else {
            setMAX_SPEED(KartConfig.MAX_SPEED.get(id).get().floatValue());
            setDELTA_SPEED(KartConfig.MAX_SPEED.get(id).get().floatValue());
            setACCELERATION_BOOST(KartConfig.ACCELERATION_BOOST.get(id).get().floatValue());
            setBOOST(KartConfig.BOOST.get(id).get().floatValue());
            setMANIABILITE_COEEF(KartConfig.HANDLING.get(id).get().floatValue());

            // SYNCHRONISATION DE POSITION - Béton Armé 💪
            // Envoie la position toutes les 2 ticks (~100ms) pour une synchro parfaite
            if (this.tickCount % 2 == 0 && !this.getPassengers().isEmpty()) {
                Network.sendKartPositionSync(this);
            }
        }

        super.baseTick();
        this.tickLerp();

        if (!this.getPassengers().isEmpty() && this.getPassengers().get(0) instanceof Player player) {
            this.controlKart(player);

            if (!this.level().isClientSide()) {
                xo = getX();
                yo = getY();
                zo = getZ();
                // Synchro des items - utilisation de la méthode sécurisée
                if (player instanceof ServerPlayer serverPlayer) {
                    Network.sendKartItemSynchMessage(serverPlayer, this.getKartItem());
                }
            } else {
                // Traitement côté client - utilisation de DistExecutor pour sécuriser l'appel
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> clientHandlePlayerControl(player));
            }
        } else {
            setDeltaOn(false);
            slowDownKart();
        }
        move(MoverType.SELF, getDeltaMovement().add(0, calculateFallSpeed(), 0));
    }

    // Ces méthodes sont vides côté serveur et seront "overridden" par le code client
    @OnlyIn(Dist.CLIENT)
    protected void clientTick() {
        // Implémentation côté client uniquement
    }

    @OnlyIn(Dist.CLIENT)
    protected void clientSpawnBoostParticules() {
        // Implémentation côté client uniquement
    }

    @OnlyIn(Dist.CLIENT)
    protected void clientHandlePlayerControl(Player player) {
        // Implémentation côté client uniquement
    }

    private void tickLerp() {
        if (this.isControlledByLocalInstance()) {
            this.lerpSteps = 0;
            this.syncPacketPositionCodec(this.getX(), this.getY(), this.getZ());
        }

        if (this.lerpSteps > 0) {
            double d0 = this.getX() + (this.lerpX - this.getX()) / (double) this.lerpSteps;
            double d1 = this.getY() + (this.lerpY - this.getY()) / (double) this.lerpSteps;
            double d2 = this.getZ() + (this.lerpZ - this.getZ()) / (double) this.lerpSteps;
            double d3 = Mth.wrapDegrees(this.lerpYRot - (double) this.getYRot());
            this.setYRot(this.getYRot() + (float) d3 / (float) this.lerpSteps);
            this.setXRot(this.getXRot() + (float) (this.lerpXRot - (double) this.getXRot()) / (float) this.lerpSteps);
            --this.lerpSteps;
            this.setPos(d0, d1, d2);
            this.setRot(this.getYRot(), this.getXRot());
        }
    }

    private void controlKart(Player player) {
        // Est sur un kart controller
        if (isOnKartController()) {
            setSpeed(0);
            return;
        }

        if (isStun()) {
            // APPLIQUE LE STUN
            setCanMove(false);
            applyStun();
        } else {
            // DESACTIVE LE STUN
            setCanMove(true);
            setStunMotif("None");

            // GERE LES COLLISIONS DU KART
            applyCollision();

            // ACTIVE LE DELTA PLANE SI NECESSAIRE
            useDeltaplane(player);

            // CALCUL LA ROTATION DU VEHCIULE
            rotateOrDrift();

            // CALCUL LE VECTEUR DE VITESSE
            setVectorMovment();
        }
    }

    private void useDeltaplane(Player player) {
        if (player == null) {
            setDeltaOn(false);
            return;
        }

        int blockX = (int) Math.floor(getX());
        int blockY = (int) Math.floor(getY() - 1);
        int blockZ = (int) Math.floor(getZ());
        BlockState blockState = getBlock(blockX, blockY, blockZ);

        //ACTIVATION DU DELTA PLANE
        if (!isDeltaOn() && blockState.getBlock().equals(BlockInit.GLIDE_TRIGGER_BLOCK.get())) {
            setDeltaOn(true);
        } else if (isDeltaOn() && !getBlock(blockX, blockY, blockZ).isAir() && !(blockState.getBlock().equals(BlockInit.GLIDE_TRIGGER_BLOCK.get()))) {
            setDeltaOn(false);
        }
    }


    private void applyStun() {
        setDriftTimeBoost(0.f);
        setTimeBoost(0.f);

        setSpeed(Mth.clamp(getSpeed() - getACCELERATION_BOOST() * 1.25f, 0, getMAX_SPEED()));
        setKartDeltaMovement();

        setStunRotation(getStunRotation() - 720 / (2 * 20));
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        return super.getControllingPassenger();
    }

    public double calculateFallSpeed() {
        if (onGround()) fallingTime = 0;
        else fallingTime++;

        if (isDeltaOn()) return -TestKartAbstract.GRAVITY * 2;

        double verticalSpeed = isInWater() ? TestKartAbstract.GRAVITY * 3 : TestKartAbstract.GRAVITY * fallingTime;
        verticalSpeed = Math.min(verticalSpeed, TestKartAbstract.TERMINAL_VELOCITY);

        return -verticalSpeed;
    }

    public boolean isStun() {
        return getStunRotation() > 0;
    }

    private void calculateSpeed() {
        //VECTEUR DE MOUVEMENT : BOOST
        if (getTimeBoost() > 0 || (isInvinsible() && !isPressingKeyDeccelerate())) {
            setSpeed(getMAX_SPEED());
        }
        //VECTEUR DE MOUVEMENT : DELTAPLANE
        else if (isDeltaOn()) {
            setSpeed(getDELTA_SPEED());
        }
        //VECTEUR DE MOUVEMENT : MARCHE AVANT
        else if (isPressingKeyAccelerate()) {
            setSpeed(Mth.clamp(getSpeed() + getACCELERATION_BOOST(), -getMAX_SPEED() / 2, getMAX_SPEED()));
        }
        //VECTEUR DE MOUVEMENT : MARCHE ARRIERE
        else if (isPressingKeyDeccelerate()) {
            setSpeed(Mth.clamp(getSpeed() - getACCELERATION_BOOST(), -getMAX_SPEED() / 2, getMAX_SPEED()));
            resetDriftWithNoBoost();
        }
        //VECTEUR DE MOUVEMENT : RALENTISSEMENT AUTOMATIQUE
        else {
            slowDownKart();
        }

        // REDUIT LA VITESSE SI COLLISION AVEC MUR
        if (horizontalCollision) setSpeed(Mth.clamp(getSpeed(), -getMAX_SPEED() / 2.5f, getMAX_SPEED() / 2));

        //SI PAS SUR UN BLOC DE ROUTE (SANS BOOST OU ETOILE), ON SLOW LE VEHICULE
        if (!isOnRoadBlock() && !isInvinsible() && getTimeBoost() <= 0) {
            setSpeed(Mth.clamp(getSpeed(), -getMAX_SPEED() / 2.5f, getMAX_SPEED() / 2));
        }
    }

    private boolean applyBoostToSpeed() {
        if (isInvinsible()) {
            if (isPressingKeyBackward()) {
                setSpeed(Mth.clamp(getSpeed() - getBOOST(), getMAX_SPEED() / 2 - getBOOST(), getMAX_SPEED() + getBOOST()));
            } else {
                setSpeed(Mth.clamp(getSpeed() + getBOOST(), getMAX_SPEED() / 2 - getBOOST(), getMAX_SPEED() + getBOOST()));
            }
            return true;
        } else if (getDriftTimeBoost() > 0 || getTimeBoost() > 0) {
            setSpeed(Mth.clamp(getSpeed() + getBOOST(), 0, getMAX_SPEED() + getBOOST()));
            return true;
        }
        return false;
    }

    private void setVectorMovment() {
        if (!applyBoostToSpeed()) {
            calculateSpeed();
        }
        setKartDeltaMovement();
    }

    public void resetDriftWithNoBoost() {
        setDrifting(false);
        setDriftingTime(0.f);
        setDriftingSens("None");
    }

    private void applyCollision() {
        //SI COLLISION, ON RESET LE DRIFT
        if (horizontalCollision) {
            setDriftingTime(0.f);
        }

        //SI EN ETOILE, ALORS ON STUN LES GENS QU'ON PERCUTE
        if (isInvinsible()) {
            List<Entity> nearbyEntities = level().getEntities(this, getBoundingBox().inflate(0.5f));
            for (Entity entity : nearbyEntities) {
                if (entity instanceof TestKart kart) {
                    if (kart.getCanMove()) {
                        TestKart.stunKart(kart, "Star");
                    }
                }
            }
        }
    }

    public void slowDownKart() {
        double x, z;
        //SI LE KART EST COMPRIS ENTRE -MIN_SPEED ET MIN_SPEED ALORS IL S'ARRETE
        if (getSpeed() > -MIN_SPEED && getSpeed() < MIN_SPEED) {
            setSpeed(0);
            x = 0;
            z = 0;
        }
        //SINON LE KART RALENTIT PROGRESSIVEMENT
        else {
            double angle = Math.toRadians(getYRot());

            setSpeed(getSpeed() / (FREINAGE_SPEED));

            float clamped_speed = Mth.clamp(getSpeed(), -getMAX_SPEED() / 2, getMAX_SPEED());
            setSpeed(clamped_speed);

            x = Math.sin(-angle) * clamped_speed;
            if (horizontalCollision) x *= COEFF_FROTTEMENT;
            z = Math.cos(-angle) * clamped_speed;
            if (horizontalCollision) z *= COEFF_FROTTEMENT;
        }

        //APPLICATION DU VECTEUR DE DEPLACEMENT
        Vec3 vec3 = new Vec3(x, getDeltaMovement().y, z);
        setDeltaMovement(vec3);
    }

    public void setKartDeltaMovement() {
        double angle = Math.toRadians(getYRot());
        float clamped_speed = getSpeed();

        //CALCUL ET APPLICATION DU VECTEUR DE DEPLACEMENT
        double x = Math.sin(-angle) * clamped_speed;
        double z = Math.cos(-angle) * clamped_speed;

        //APPLICATION DU VECTEUR DE DEPLACEMENT
        Vec3 vec3 = new Vec3(x, getDeltaMovement().y, z);
        setDeltaMovement(vec3);

        speedToShow = clamped_speed;
    }

    public void spawnBoostParticules(SimpleParticleType particle) {
        // Cette méthode ne fait rien côté serveur
        // Implémentation uniquement côté client
    }

    private void rotateOrDrift() {
        //ON INITIE LA ROTATION QUE SI LE VEHICULE EST EN MOUVEMENT
        if (getSpeed() != 0 && getCanMove()) {
            //INIT DU DRIFT
            if (isPressingKeyDrift() && onGround() && getDriftingTime() == 0.f) {
                if (isPressingKeyLeft() && !isPressingKeyRight()) {
                    setDrifting("Left");
                } else if (isPressingKeyRight() && !isPressingKeyLeft()) {
                    setDrifting("Right");
                }
            }
            // ANALYSE DES ACTIONS SI LE JOUEUR DRIFT
            if (!getDriftingSens().equals("None") && (isOnRoadBlock() || getTimeBoost() > 0 || isInvinsible()) && isPressingKeyDrift() && !horizontalCollision && !isDeltaOn() && getSpeed() > getMAX_SPEED() * 0.25) {
                //DRIFT INITIAL : DRIFT A GAUCHE
                if (isDrifting() && getDriftingSens().equals("Left")) {
                    //LE JOUEUR MAINTIENT LA TOUCHE GAUCHE
                    if (isPressingKeyLeft() && !isPressingKeyRight()) {
                        if (getDriftingTime() < 3.0f && onGround())
                            setDriftingTime(getDriftingTime() + 0.06f);
                        setYRot(getYRot() - getMANIABILITE_COEEF() * DRIFT_ANGLE);
                    }
                    //LE JOUEUR MAINTIENT LA TOUCHE DROITE
                    else if (isPressingKeyRight() && !isPressingKeyLeft()) {
                        setYRot(getYRot() - getMANIABILITE_COEEF() * (DRIFT_ANGLE * 0.3f));
                    }
                    //LE JOUEUR NE MAINTIENT RIEN
                    else {
                        if (getDriftingTime() < 3.0f && onGround())
                            setDriftingTime(getDriftingTime() + 0.02f);
                        setYRot(getYRot() - getMANIABILITE_COEEF() * (DRIFT_ANGLE * 0.75f));
                    }
                }
                //DRIFT INITIAL : DRIFT A DROITE
                else if (isDrifting() && getDriftingSens().equals("Right")) {
                    //LE JOUEUR MAINTIENT LA TOUCHE GAUCHE
                    if (isPressingKeyAccelerate() && isPressingKeyRight() && !isPressingKeyLeft()) {
                        if (getDriftingTime() < 3.0f && onGround())
                            setDriftingTime(getDriftingTime() + 0.06f);
                        setYRot(getYRot() + getMANIABILITE_COEEF() * DRIFT_ANGLE);
                    }
                    //LE JOUEUR MAINTIENT LA TOUCHE DROITE
                    else if (isPressingKeyLeft() && !isPressingKeyRight()) {
                        setYRot(getYRot() + getMANIABILITE_COEEF() * (DRIFT_ANGLE * 0.5f));
                    }
                    //LE JOUEUR NE MAINTIENT RIEN
                    else {
                        if (getDriftingTime() < 3.0f && onGround())
                            setDriftingTime(getDriftingTime() + 0.02f);
                        setYRot(getYRot() + getMANIABILITE_COEEF() * (DRIFT_ANGLE * 0.75f));
                    }
                }
            }
            //LE KART NE DRIFT PAS
            else {
                //ON RESET TOUS LES PARAMETRES EN RAPPORT AVEC LE DRIFT
                if(!isPressingKeyDrift()) resetDrift();
                else resetDriftWithNoBoost();

                //SI LE JOUEUR MAINTIENT LE BOUTON GAUCHE : ROTATION GAUCHE
                if (isPressingKeyLeft() && !isPressingKeyRight()) {
                    if (getSpeed() > 0) setYRot(getYRot() - getMANIABILITE_COEEF());
                    else setYRot(getYRot() + getMANIABILITE_COEEF());
                }
                //SI LE JOUEUR MAINTIENT LE BOUTON DROIT : ROTATION DROITE
                else if (isPressingKeyRight() && !isPressingKeyLeft()) {
                    if (getSpeed() > 0) setYRot(getYRot() + getMANIABILITE_COEEF());
                    else setYRot(getYRot() - getMANIABILITE_COEEF());
                }
            }
        }
        //SI LE KART EST A L'ARRET
        else {
            //ON RESET TOUS LES PARAMETRES EN RAPPORT AVEC LE DRIFT
            resetDrift();
        }
    }

    public void resetDrift() {
        if (getDriftingTime() != 0)
            setDriftTimeBoost((float) Math.floor(getDriftingTime()));
        setDrifting(false);
        setDriftingTime(0.f);
        setDriftingSens("None");
    }

    public void setDrifting(String sens) {
        setDrifting(true);
        setDriftingTime(0.1f);
        setDriftingSens(sens);
    }

    private void resetBindValue() {
        setPressingKeyAccelerate(false);
        setPressingKeyDeccelerate(false);

        setPressingKeyForward(false);
        setPressingKeyBackward(false);
        setPressingKeyLeft(false);
        setPressingKeyRight(false);

        setPressingKeyDelta(false);
        //setPreviousPressingKeyDelta(false);
        setPressingKeyDrift(false);
        setPressingKeyItem(false);
    }

    public static void stunKart(TestKart kart, String motif) {
        if (kart.isInvinsible()) return;

        kart.setStunMotif(motif);

        kart.setCanMove(false);
        kart.setStunRotation(720.f);

        kart.setDeltaOn(false);
        kart.setDriftTimeBoost(0.f);
        kart.resetDrift();
    }

    // Diverses méthodes utilitaires accessibles au client
    public boolean boostFini = true;
}
