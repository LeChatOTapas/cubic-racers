package me.jesuismister.cubicracers.entity.custom;

import me.jesuismister.cubicracers.config.KartConfig;
import me.jesuismister.cubicracers.init.BlockInit;
import me.jesuismister.cubicracers.init.KartInit;
import me.jesuismister.cubicracers.init.SoundsInit;
import me.jesuismister.cubicracers.network.message.clientToServer.KartSynchMessage;
import me.jesuismister.cubicracers.network.message.serverToClient.KartItemSynchMessage;
import me.jesuismister.cubicracers.sounds.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TestKart extends TestKartAbstract {
    public static float HITBOX_X;
    public static float HITBOX_Y;
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
        if (level().isClientSide) updateSoundsClient();
        else {
            setMAX_SPEED(KartConfig.MAX_SPEED.get(id).get().floatValue());
            setDELTA_SPEED(KartConfig.MAX_SPEED.get(id).get().floatValue());
            setACCELERATION_BOOST(KartConfig.ACCELERATION_BOOST.get(id).get().floatValue());
            setBOOST(KartConfig.BOOST.get(id).get().floatValue());
            setMANIABILITE_COEEF(KartConfig.HANDLING.get(id).get().floatValue());
        }

        // ON UPDATE LES TIMERS
        if (getDriftTimeBoost() > 0) setDriftTimeBoost(getDriftTimeBoost() - 0.1f);
        if (getTimeBoost() > 0) setTimeBoost(getTimeBoost() - 0.1f);
        if (getTimeStar() > 0) setTimeStar(getTimeStar() - 0.1f);
        if (isInvincible() && getTimeStar() <= 0) {
            setInvincible(false);
            setStarSpeedBoost(1f);
        }

        // PARTICULE DE BOOST
        if (getTimeBoost() > 0 || getDriftTimeBoost() > 0) spawnBoostParticules(ParticleTypes.FLAME);

        super.baseTick();
        this.tickLerp();

        if (!this.getPassengers().isEmpty() && this.getPassengers().get(0) instanceof Player player) {
            this.controlKart(player);

            if (!this.level().isClientSide()) {
                xo = getX();
                yo = getY();
                zo = getZ();
                // Synchro des items
                PacketDistributor.sendToAllPlayers(new KartItemSynchMessage(this.getKartItem()));
            } else {
                moveCamera(player);
                synchKart(player);
            }
        } else {
            setDeltaOn(false);
            slowDownKart();
        }
        move(MoverType.SELF, getDeltaMovement().add(0, calculateFallSpeed(), 0));
    }

    private void synchKart(Player player) {
        if (Minecraft.getInstance().player.getName().equals(player.getName())) {
            try {
                PacketDistributor.sendToAllPlayers(new KartSynchMessage(this.getId(), getX(), getY(), getZ(), getYRot()));
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void lerpHeadTo(float yaw, int pitch) {
        super.lerpHeadTo(yaw, pitch);
    }

    private void tickLerp() {
        if (this.isLocalInstanceAuthoritative()) {
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

    private void moveCamera(Player player) {
        //ON BOUGE LA CAMERA DU CONDUCTEUR
        if (player != null) {
            player.setYRot(getYRot());
            player.setYBodyRot(getYRot());
        }
    }

    public boolean isStun() {
        return getStunRotation() > 0;
    }

    private void calculateSpeed() {
        //VECTEUR DE MOUVEMENT : BOOST
        if (getTimeBoost() > 0 || (isInvincible() && !isPressingKeyDeccelerate())) {
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
        if (!isOnRoadBlock() && !isInvincible() && getTimeBoost() <= 0) {
            setSpeed(Mth.clamp(getSpeed(), -getMAX_SPEED() / 2.5f, getMAX_SPEED() / 2));
        }
    }

    private boolean applyBoostToSpeed() {
        if (isInvincible()) {
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
        if (isInvincible()) {
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
        if (!level().isClientSide()) return;

        float yaw = getYRot();
        double motionX = -Math.sin(Math.toRadians(yaw));
        double motionZ = Math.cos(Math.toRadians(yaw));
        spawnParticules(particle, 0.25 * Math.cos(Math.toRadians(yaw)), 0, 0.25 * Math.sin(Math.toRadians(yaw)),
                motionX * 0.5f, 0, motionZ * 0.5f);
    }

    public void spawnParticules(SimpleParticleType particle, double x1, double y1, double z1, double x2, double y2, double z2) {
        if (!level().isClientSide()) return;

        Minecraft minecraft = Minecraft.getInstance();
        double x = getX();
        double y = getY();
        double z = getZ();

        //SPAWN PARTICULES GAUCHES
        minecraft.particleEngine.createParticle(particle, x - x1, y - y1, z - z1, x2, y2, z2);
        //SPAWN PARTICULES DROITES
        minecraft.particleEngine.createParticle(particle, x + x1, y + y1, z + z1, x2, y2, z2);
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
            if (!getDriftingSens().equals("None") && (isOnRoadBlock() || getTimeBoost() > 0 || isInvincible()) && isPressingKeyDrift() && !horizontalCollision && !isDeltaOn() && getSpeed() > getMAX_SPEED() * 0.25) {
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
        if (kart.isInvincible()) return;

        kart.setStunMotif(motif);

        kart.setCanMove(false);
        kart.setStunRotation(720.f);

        kart.setDeltaOn(false);
        kart.setDriftTimeBoost(0.f);
        kart.resetDrift();
    }

    ////////////
    // SOUNDS //
    ////////////
    private transient SoundEngineIdle engineIdleLoop;
    private transient SoundEngineMax engineMaxLoop;
    private transient SoundStarMode starModeLoop;
    private transient SoundKartGliding kartGliding;
    private transient SoundKartDrifting kartDrifting;
    private transient SoundKartOffRoad kartOffRoad;
    public boolean boostFini = true;

    private void updateSoundsClient() {
        if (!level().isClientSide) return;

        Minecraft mc = Minecraft.getInstance();
        SoundManager sm = mc.getSoundManager();

        if (isInvincible()) {
            if (!isSoundPlaying(starModeLoop, sm)) {
                starModeLoop = new SoundStarMode(this, SoundsInit.STAR_MODE.get(), SoundSource.RECORDS);
                SoundsInit.playSoundLoop(starModeLoop, level());
            }
        } else if (isDeltaOn()) {
            if (!isSoundPlaying(kartGliding, sm)) {
                kartGliding = new SoundKartGliding(this, SoundsInit.KART_GLIDING.get(), SoundSource.RECORDS);
                SoundsInit.playSoundLoop(kartGliding, level());
            }
        } else {
            if (Math.abs(getSpeed()) <= 0.2f) {
                if (!isSoundPlaying(engineIdleLoop, sm)) {
                    engineIdleLoop = new SoundEngineIdle(this, SoundsInit.ENGINE_IDLE.get(), SoundSource.RECORDS);
                    SoundsInit.playSoundLoop(engineIdleLoop, level());
                }
            } else if (!isOnRoadBlock() && onGround() && Math.abs(getSpeed()) > 0.2f) {
                if (!isSoundPlaying(kartOffRoad, sm)) {
                    kartOffRoad = new SoundKartOffRoad(this, SoundsInit.KART_OFF_ROAD.get(), SoundSource.RECORDS);
                    SoundsInit.playSoundLoop(kartOffRoad, level());
                }
            } else if (isOnRoadBlock() && onGround()) {
                if (!isSoundPlaying(engineMaxLoop, sm)) {
                    engineMaxLoop = new SoundEngineMax(this, SoundsInit.ENGINE_MAX.get(), SoundSource.RECORDS);
                    SoundsInit.playSoundLoop(engineMaxLoop, level());
                }

                if (isDrifting()) {
                    if (!isSoundPlaying(kartDrifting, sm)) {
                        kartDrifting = new SoundKartDrifting(this, SoundsInit.KART_DRIFTING.get(), SoundSource.RECORDS);
                        SoundsInit.playSoundLoop(kartDrifting, level());
                    }
                }
            }
        }

        // BOOST
        if (boostFini && (getTimeBoost() > 0 || getDriftTimeBoost() > 0)) {
            boostFini = false;
            if (getFirstPassenger() instanceof Player player)
                SoundsInit.playSound(SoundsInit.KART_SPEED_BOOST.get(), level(), blockPosition(), player, SoundSource.RECORDS, 1f);
        } else if (!boostFini && (getTimeBoost() <= 0 && getDriftTimeBoost() <= 0)) {
            boostFini = true;
        }
    }

    private boolean isSoundPlaying(SoundInstance sound, SoundManager sm) {
        return sound != null && sm.isActive(sound);
    }

}
