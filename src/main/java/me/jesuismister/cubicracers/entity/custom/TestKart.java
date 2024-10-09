package me.jesuismister.cubicracers.entity.custom;

import me.jesuismister.cubicracers.block.BouncingMushroomBlock;
import me.jesuismister.cubicracers.config.KartConfig;
import me.jesuismister.cubicracers.init.BlockInit;
import me.jesuismister.cubicracers.init.KartInit;
import me.jesuismister.cubicracers.init.SoundsInit;
import me.jesuismister.cubicracers.network.Network;
import me.jesuismister.cubicracers.network.message.MessageSyncCarPosition;
import me.jesuismister.cubicracers.network.message.itemsKart.particles.KlaxonParticleMessage;
import me.jesuismister.cubicracers.network.message.itemsKart.use.*;
import me.jesuismister.cubicracers.sounds.*;
import me.jesuismister.cubicracers.util.ClientUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;
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
    public int deltaTime = 0;

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
    public void tick() {
        if(level().isClientSide) updateSounds();
        else{
            setMAX_SPEED(KartConfig.MAX_SPEED.get(id).get().floatValue());
            setDELTA_SPEED(KartConfig.MAX_SPEED.get(id).get().floatValue());
            setACCELERATION_BOOST(KartConfig.ACCELERATION_BOOST.get(id).get().floatValue());
            setBOOST(KartConfig.BOOST.get(id).get().floatValue());
            setMANIABILITE_COEEF(KartConfig.HANDLING.get(id).get().floatValue());
        }

        // ON UPDATE LES TIMERS
        if (getTimeStar() > 0) setTimeStar(getTimeStar() - 0.1f);
        if (getDriftTimeBoost() > 0) setDriftTimeBoost(getDriftTimeBoost() - 0.1f);
        if (getTimeBoost() > 0) setTimeBoost(getTimeBoost() - 0.1f);
        //ON VERIFIE QUE LE JOUEUR EST ENCORE EN ETOILE
        if (isInvinsible() && getTimeStar() <= 0) {
            setInvinsible(false);
            setStarSpeedBoost(1f);
        }

        Player player = (Player) getFirstPassenger();

        if(!level().isClientSide){
            xo = getX();
            yo = getY();
            zo = getZ();
        }

        super.baseTick();
        this.tickLerp();
        if (player!=null) {
            moveCamera(player);
            this.controlKart(player);
        } else {
            setDeltaOn(false);
            slowDownKart();
            setKartMovement();
        }
        move(MoverType.SELF, new Vec3(getDeltaMovement().x, calculateFallSpeed().y, getDeltaMovement().z));

        if (!level().isClientSide) {
            try {
                ServerPlayer driver = (ServerPlayer) this.getControllingPassenger();
                for (ServerPlayer sp : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                    if (sp != driver) {
                        Network.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sp),
                                new MessageSyncCarPosition(this.getId(), getX(), getY(), getZ(), getYRot()));
                    }
                }
            }catch (Exception e){}
        }
    }

    @Override
    public void lerpTo(double p_38299_, double p_38300_, double p_38301_, float p_38302_, float p_38303_, int p_38304_, boolean p_38305_) {
        super.lerpTo(p_38299_, p_38300_, p_38301_, p_38302_, p_38303_, p_38304_, p_38305_);
    }

    private void tickLerp() {
        if (this.isControlledByLocalInstance()) {
            this.lerpSteps = 0;
            this.syncPacketPositionCodec(this.getX(), this.getY(), this.getZ());
        }

        if (this.lerpSteps > 0) {
            double d0 = this.getX() + (this.lerpX - this.getX()) / (double)this.lerpSteps;
            double d1 = this.getY() + (this.lerpY - this.getY()) / (double)this.lerpSteps;
            double d2 = this.getZ() + (this.lerpZ - this.getZ()) / (double)this.lerpSteps;
            double d3 = Mth.wrapDegrees(this.lerpYRot - (double)this.getYRot());
            this.setYRot(this.getYRot() + (float)d3 / (float)this.lerpSteps);
            this.setXRot(this.getXRot() + (float)(this.lerpXRot - (double)this.getXRot()) / (float)this.lerpSteps);
            --this.lerpSteps;
            this.setPos(d0, d1, d2);
            this.setRot(this.getYRot(), this.getXRot());
        }

    }

    private void controlKart(Player player) {
        if(isOnKartController()){
            setSpeed(0);
            return;
        }

        isStun(); // ON VOIT SI LE KART EST STUN
        if (!(getCanMove())) applyStun(); // SI LE KART EST STUN, ON APPLIQUE LA PROCEDURE DE STUN

        // UTILISE L'ITEM SI LE JOUEUR LE VEUT
        if (getCanMove() && isPressingKeyItem()){
            useItem();
        }

        collision(); // GERE LES COLLISIONS DU KART

        deltaplane(player); // ACTIVE LE DELTA PLANE
        rotateOrDrift(); // CALCUL LA ROTATION DU VEHCIULE

        if (getCanMove()) {
            setVectorMovment(); // SI PAS STUN, CALCUL LE VECTEUR DE VITESSE
        }
    }

    private void useItem() {
        if (getKartItem().equals("Banana")) {
            if (level().isClientSide())
                Network.CHANNEL.sendToServer(new BananaUseMessage(isPressingKeyForward()));
        } else if (getKartItem().equals("Mushroom")) {
            setTimeBoost(5.f);
            setSpeed(getMAX_SPEED() + getBOOST());
        } else if (getKartItem().equals("Star")) {
            setTimeStar(20f);
            setStarSpeedBoost(1.5f);
            setInvinsible(true);
            setSpeed(getMAX_SPEED() * getStarSpeedBoost());
        } else if (getKartItem().equals("Thunder")) {
            if (level().isClientSide()) Network.CHANNEL.sendToServer(new ThunderUseMessage());
        } else if (getKartItem().equals("Klaxon")) {
            if (level().isClientSide) {
                Network.CHANNEL.sendToServer(new KlaxonParticleMessage(getX(), getY(), getZ()));
                Network.CHANNEL.sendToServer(new KlaxonUseMessage(getX(), getY(), getZ()));
            }
        } else if (getKartItem().equals("Bob_omb")) {
            if (level().isClientSide())
                Network.CHANNEL.sendToServer(new BobOmbUseMessage(isPressingKeyForward()));
        } else if (getKartItem().equals("Fake_box")) {
            if (level().isClientSide())
                Network.CHANNEL.sendToServer(new FakeBoxUseMessage(isPressingKeyForward()));
        } else if (getKartItem().equals("Green_shell")) {
            if (level().isClientSide())
                Network.CHANNEL.sendToServer(new GreenShellUseMessage(isPressingKeyBackward()));
        }

        setKartItem("None");
    }

    private void deltaplane(Player player) {
        if (player == null) {
            setDeltaOn(false);
            return;
        }

        int blockX = (int) Math.floor(getX());
        int blockY = (int) Math.floor(getY()-1);
        int blockZ = (int) Math.floor(getZ());
        BlockState blockState = getBlock(blockX, blockY, blockZ);

        //ACTIVATION DU DELTA PLANE
        if (!isDeltaOn() && blockState.getBlock().equals(BlockInit.GLIDE_TRIGGER_BLOCK.get())) {
            setDeltaOn(true);
        } else if (isDeltaOn() && !(blockState.getBlock().equals(BlockInit.GLIDE_TRIGGER_BLOCK.get()) || blockState.isAir())){
            setDeltaOn(false);
        }
    }


    private void applyStun() {
        resetBindValue();

        setDriftTimeBoost(0.f);
        setTimeBoost(0.f);

        setSpeed(Mth.clamp(getSpeed() - getACCELERATION_BOOST() * 1.5f, 0, getMAX_SPEED()));
        setKartMovement();

        setStunRotation(getStunRotation() - 720 / (3 * 20));
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        return super.getControllingPassenger();
    }

    public Vec3 calculateFallSpeed() {
        if(isDeltaOn()) return new Vec3(0, -TestKartAbstract.GRAVITY*2, 0);
        else if(getBouncingTime()!=0){
            if(!level().isClientSide() && getBouncingTime()== BouncingMushroomBlock.TIME_BOUNCING){
                ClientUtil.playSoundToAll(level(), getX(), getY(), getZ(), 8, SoundsInit.KART_BOUNCING.get(), SoundSource.RECORDS, 1f, 0.95f);
            }
            float value = TestKartAbstract.BOUNCING_COEFF*(getBouncingTime() / BouncingMushroomBlock.TIME_BOUNCING);
            setBouncingTime(getBouncingTime()-1);
            return new Vec3(0, value, 0);
        }

        double verticalSpeed = isInWater() ? TestKartAbstract.GRAVITY*3 : TestKartAbstract.GRAVITY * deltaTime;
        verticalSpeed = Math.min(verticalSpeed, TestKartAbstract.TERMINAL_VELOCITY);

        deltaTime = fallDistance!=0 ? deltaTime + 1 : 1;

        return new Vec3(0, -verticalSpeed, 0);
    }

    private void moveCamera(Player player) {
        //ON BOUGE LA CAMERA DU CONDUCTEUR
        if (player != null) {
            player.setYRot(getYRot());
            player.setYBodyRot(getYRot());
        }
    }

    private void isStun() {
        //DETECTE SI LE KART EST EN SITUATION DE "STUN"
        if (getStunRotation() <= 0) {
            setCanMove(true);
            setStunRotation(0f);
        }

        if(level().isClientSide && !getStunMotif().equals("None")){
            setCanMove(false);
            setStunRotation(720.0f);
            setStunMotif("None");
        }

    }


    private void setVectorMovment() {
        //VECTEUR DE MOUVEMENT : getBOOST()
        if (getTimeBoost() > 0 || (isInvinsible() && !isPressingKeyDeccelerate())) {
            setSpeed(getMAX_SPEED());
        }
        //VECTEUR DE MOUVEMENT : DELTA PLANE
        else if (isDeltaOn()) {
            setSpeed(getDELTA_SPEED());
        }
        //VECTEUR DE MOUVEMENT : MARCHE AVANT !!!
        else if ((!horizontalCollision || getSpeed() <= getMAX_SPEED() / 10) && isPressingKeyAccelerate()) {
            setSpeed(Mth.clamp(getSpeed() + getACCELERATION_BOOST(), -getMAX_SPEED() / 2, getMAX_SPEED()));
        }
        //VECTEUR DE MOUVEMENT : MARCHE ARRIERE !!!
        else if (isPressingKeyDeccelerate()) {
            setSpeed(Mth.clamp(getSpeed() - getACCELERATION_BOOST(), -getMAX_SPEED() / 2, getMAX_SPEED()));
            resetDriftWithNoBoost();
        }
        //VECTEUR DE MOUVEMENT : RALENTISSEMENT AUTOMATIQUE
        else {
            slowDownKart();
        }
        setKartMovement();
    }

    public void resetDriftWithNoBoost() {
        setDrifting(false);
        setDriftingTime(0.f);
        setDriftingSens("None");
    }

    private void collision() {
        //SI LE KART A UN PASSAGER ET QUE LE KART EST DANS UN BLOCK, ALORS ON LE REMONTE D'UN BLOC
        /*
        if (getFirstPassenger() != null) {
            if (isValidBlockCollision()){
                setPos(getX(), getY() + 1, getZ());
            }
        }*/

        //SI COLLISION, ON RESET LE getBOOST() DU DRIFT
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
        Vec3 vec3 = new Vec3(x, 0, z);
        setDeltaMovement(vec3);
    }
    public void setKartMovement() {
        double angle = Math.toRadians(getYRot());

        //SPAWN DES PARTICULES DE getBOOST()
        if (!isInvinsible() && getSpeed() > 0 && (getDriftTimeBoost() > 0 || getTimeBoost() > 0)) {
            spawnBoostParticules(ParticleTypes.FLAME);
        }

        float clamped_speed = getSpeed();
        //getBOOST() LE JOUEUR S'IL EST SOUS getBOOST() DE DRIFT OU CHAMPIGNON
        if (getDriftTimeBoost() > 0 || getTimeBoost() > 0) {
            clamped_speed = Mth.clamp(getSpeed() + getBOOST(), 0, getMAX_SPEED() + getBOOST());
        }
        //ACCELERE LE TOUT SI SOUS ETOILE
        if (isInvinsible() && !isPressingKeyDeccelerate())
            clamped_speed = clamped_speed * getStarSpeedBoost();

        //SI PAS SUR UN BLOC DE ROUTE (SANS getBOOST() OU ETOILE), ON SLOW LE VEHICULE
        if(!isOnRoadBlock() && (!isInvinsible() || getTimeBoost()<=0)){
            setSpeed(Mth.clamp(getSpeed(), -getMAX_SPEED()/2, getMAX_SPEED()/2));
        }

        //CALCUL ET APPLICATION DU VECTEUR DE DEPLACEMENT
        double x = Math.sin(-angle) * clamped_speed;
        double z = Math.cos(-angle) * clamped_speed;
        Vec3 vec3 = new Vec3(x, 0, z);
        speedToShow = clamped_speed;

        setDeltaMovement(vec3);
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
            //SI LE JOUEUR APPUIE SUR LA TOUCHE DE DRIFT, QUE LE KART AVANCE ASSEZ VITE ET AUTRES CONDITIONS
            if ((isOnRoadBlock() || getTimeBoost()>0 || isInvinsible()) && isPressingKeyDrift() && !horizontalCollision && !isDeltaOn() && getSpeed() > getMAX_SPEED() * 0.25) {
                //INIT DU DRIFT SI PAS ENCORE FAIT
                if (getDriftingTime() == 0) {
                    if (isPressingKeyLeft() && !isPressingKeyRight()) {
                        setDrifting("Left");
                    } else if (isPressingKeyRight() && !isPressingKeyLeft()) {
                        setDrifting("Right");
                    }
                }

                //DRIFT INITIAL : DRIFT A GAUCHE
                if (isDrifting() && getDriftingSens().equals("Left")) {
                    //LE JOUEUR MAINTIENT LA TOUCHE GAUCHE
                    if (isPressingKeyLeft() && !isPressingKeyRight()) {
                        if (getDriftingTime() < 3.0f)
                            setDriftingTime(getDriftingTime() + 0.06f);
                        setYRot(getYRot() - getMANIABILITE_COEEF() * DRIFT_ANGLE);
                    }
                    //LE JOUEUR MAINTIENT LA TOUCHE DROITE
                    else if (isPressingKeyRight() && !isPressingKeyLeft()) {
                        setYRot(getYRot() - getMANIABILITE_COEEF() * (DRIFT_ANGLE * 0.3f));
                    }
                    //LE JOUEUR NE MAINTIENT RIEN
                    else {
                        if (getDriftingTime() < 3.0f)
                            setDriftingTime(getDriftingTime() + 0.02f);
                        setYRot(getYRot() - getMANIABILITE_COEEF() * (DRIFT_ANGLE * 0.75f));
                    }
                }
                //DRIFT INITIAL : DRIFT A DROITE
                else if (isDrifting() && getDriftingSens().equals("Right")) {
                    //LE JOUEUR MAINTIENT LA TOUCHE GAUCHE
                    if (isPressingKeyAccelerate() && isPressingKeyRight() && !isPressingKeyLeft()) {
                        if (getDriftingTime() < 3.0f)
                            setDriftingTime(getDriftingTime() + 0.06f);
                        setYRot(getYRot() + getMANIABILITE_COEEF() * DRIFT_ANGLE);
                    }
                    //LE JOUEUR MAINTIENT LA TOUCHE DROITE
                    else if (isPressingKeyLeft() && !isPressingKeyRight()) {
                        setYRot(getYRot() + getMANIABILITE_COEEF() * (DRIFT_ANGLE * 0.5f));
                    }
                    //LE JOUEUR NE MAINTIENT RIEN
                    else {
                        if (getDriftingTime() < 3.0f)
                            setDriftingTime(getDriftingTime() + 0.02f);
                        setYRot(getYRot() + getMANIABILITE_COEEF() * (DRIFT_ANGLE * 0.75f));
                    }
                }
            }
            //LE KART NE DRIFT PAS
            else {
                //ON RESET TOUS LES PARAMETRES EN RAPPORT AVEC LE DRIFT
                resetDrift();

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

    ////////////
    // SOUNDS //
    ////////////

    public boolean boostFini = true;
    @OnlyIn(Dist.CLIENT)
    private SoundEngineIdle engineIdleLoop;
    @OnlyIn(Dist.CLIENT)
    private SoundEngineMax engineMaxLoop;
    @OnlyIn(Dist.CLIENT)
    private SoundStarMode starModeLoop;
    @OnlyIn(Dist.CLIENT)
    private SoundKartGliding kartGliding;
    @OnlyIn(Dist.CLIENT)
    private SoundKartDrifting kartDrifting;
    @OnlyIn(Dist.CLIENT)
    private SoundKartOffRoad kartOffRoad;

    @OnlyIn(Dist.CLIENT)
    public void updateSounds() {
        if(!level().isClientSide) return;

        if(isInvinsible()) {
            if (!isSoundPlaying(starModeLoop)) {
                starModeLoop = new SoundStarMode(this, SoundsInit.STAR_MODE.get(), SoundSource.RECORDS);
                SoundsInit.playSoundLoop(starModeLoop, level());
            }
        }else if(isDeltaOn()){
            if (!isSoundPlaying(kartGliding)) {
                kartGliding = new SoundKartGliding(this, SoundsInit.KART_GLIDING.get(), SoundSource.RECORDS);
                SoundsInit.playSoundLoop(kartGliding, level());
            }
        }else{
            //ARRET OU EN MOUVEMENT
            if (getSpeed() > -getMAX_SPEED()*0.2f && getSpeed() < getMAX_SPEED()*0.2f) {
                if (!isSoundPlaying(engineIdleLoop)) {
                    engineIdleLoop = new SoundEngineIdle(this, SoundsInit.ENGINE_IDLE.get(), SoundSource.RECORDS);
                    SoundsInit.playSoundLoop(engineIdleLoop, level());
                }
            }else if(!isOnRoadBlock()){
                if (!isSoundPlaying(kartOffRoad)) {
                    kartOffRoad = new SoundKartOffRoad(this, SoundsInit.KART_OFF_ROAD.get(), SoundSource.RECORDS);
                    SoundsInit.playSoundLoop(kartOffRoad, level());
                }
            } else if(getSpeed() != 0){
                if (!isSoundPlaying(engineMaxLoop)) {
                    engineMaxLoop = new SoundEngineMax(this, SoundsInit.ENGINE_MAX.get(), SoundSource.RECORDS);
                    SoundsInit.playSoundLoop(engineMaxLoop, level());
                }

                //DRIFTING OU PAS DRIFTING
                if(isDrifting()) {
                    if (!isSoundPlaying(kartDrifting)) {
                        kartDrifting = new SoundKartDrifting(this, SoundsInit.KART_DRIFTING.get(), SoundSource.RECORDS);
                        SoundsInit.playSoundLoop(kartDrifting, level());
                    }
                }
            }
        }

        //getBOOST() DE VITESSE
        if(boostFini && (getTimeBoost()>0 || getDriftTimeBoost()>0)){
            boostFini = false;
            if(getFirstPassenger()!=null && getFirstPassenger() instanceof Player player)
                SoundsInit.playSound(SoundsInit.KART_SPEED_BOOST.get(), level(), new BlockPos((int)getX(), (int)getY(), (int)getZ()), player, SoundSource.RECORDS, 1f);
        }else if(!boostFini && (getTimeBoost()<=0 && getDriftTimeBoost()<=0)){
            boostFini = true;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isSoundPlaying(SoundInstance sound) {
        if (sound == null) {
            return false;
        }
        return Minecraft.getInstance().getSoundManager().isActive(sound);
    }
}
