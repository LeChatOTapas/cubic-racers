package me.jesuismister.cubicracers.entity.custom;

import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.event.network.Network;
import me.jesuismister.cubicracers.event.network.message.use.*;
import me.jesuismister.cubicracers.init.KartInit;
import me.jesuismister.cubicracers.itemKart.Klaxon;
import me.jesuismister.cubicracers.particles.ParticlesInit;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

@Mod.EventBusSubscriber(modid = CubicRacers.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class Kart extends KartAbstract implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    //PATH
    public final String TEXTURE;
    public final String MODEL;
    public final String ANIMATION;

    //ATTRIBUTS DU KART
    public final float MAX_SPEED;
    public final float DELTA_SPEED;
    public final float ACCELERATION_BOOST;
    public final float BOOST;
    public final float MANIABILITE_COEEF;
    public final float PLAYER_POS_Y;

    public Kart(EntityType<?> entityType, Level level, String texture, String model, String animation, float maxSpeed,
                float accelerationBoost, float boost, float maniabiliteCoeff, float playerPosY) {
        super(entityType, level);

        TEXTURE = texture;
        MODEL = model;
        ANIMATION = animation;

        MAX_SPEED = maxSpeed;
        DELTA_SPEED = MAX_SPEED;
        ACCELERATION_BOOST = accelerationBoost;
        BOOST = boost;
        MANIABILITE_COEEF = maniabiliteCoeff;

        PLAYER_POS_Y = playerPosY;
        setInvulnerable(false);
    }
    
    public Kart(Level level, double x, double y, double z, String name, String texture, String model, String animation,
                float maxSpeed, float accelerationBoost, float boost, float maniabiliteCoeff, float playerPosY) {

        this(KartInit.KARTS.get(name).get(), level, texture, model, animation, maxSpeed, accelerationBoost, boost,
                maniabiliteCoeff, playerPosY);

        setPos(x, y, z);
        xo = x;
        yo = y;
        zo = z;
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(SPEED, 0.0f);

        entityData.define(isPressingKeyUp, false);
        entityData.define(isPressingKeyDown, false);
        entityData.define(isPressingKeyLeft, false);
        entityData.define(isPressingKeyRight, false);
        entityData.define(isPressingKeyDrift, false);
        entityData.define(isPressingKeyItem, false);
        entityData.define(isPressingKeyDelta, false);
        entityData.define(previousPressingKeyDelta, false);

        entityData.define(isDrifting, false);
        entityData.define(driftingSens, "None");
        entityData.define(driftingTime, 0.0f);
        entityData.define(driftTimeBoost, 0.0f);
        entityData.define(timeBoost, 0.0f);

        entityData.define(deltaOn, false);
        entityData.define(deltaAnimationState, 0);
        entityData.define(waterAnimationState, 0);
        entityData.define(fallSpeed, BASE_FALL_SPEED);
        entityData.define(pourcentage_inclinaison, 0.f);
        entityData.define(actual_rotation_wheels, 0.f);

        entityData.define(canMove, true);
        entityData.define(stunRotation, 0.f);

        entityData.define(kartItem, "Green_shell");
        entityData.define(isInvinsible, false);
        entityData.define(starSpeedBoost, 1f); //COEFF DE BOOST / 1 PAR DEFAUT / 1.5 SOUS ETOILE
        entityData.define(timeStar, 0.f);
        entityData.define(isKlaxoning, false);
    }

    @Override
    protected void positionRider(Entity player, MoveFunction position) {
        super.positionRider(player, position);
        double x = player.getX();
        double y = player.getY() + PLAYER_POS_Y;
        double z = player.getZ();
        player.setPos(x, y, z);
    }

    //////////////////
    // TICK SECTION //
    //////////////////

    @Override
    public void tick() {
        super.tick();

        isStun(); // ON VOIT SI LE KART EST STUN
        if (!getCanMove()) applyStun(); // SI LE KART EST STUN, ON APPLIQUE LA PROCEDURE DE STUN

        Player player = (Player) getFirstPassenger();
        if (player == null) {
            setIsPressingKeyUp(false);
            setIsPressingKeyDown(false);
            setIsPressingKeyLeft(false);
            setIsPressingKeyRight(false);
            setIsPressingKeyDelta(false);
            setPreviousPressingKeyDelta(false);
            setIsPressingKeyDrift(false);
            setIsPressingKeyItem(false);
            if (getSpeed() > 0) {
                slowDownKart();
                setKartMovement();
                move(MoverType.SELF, new Vec3(getDeltaMovement().x, getFallSpeed(), getDeltaMovement().z));
            }
        } else {
            System.out.println(level() + " => speed: " + getSpeed() + " / Yrot: " + getYRot());
            collision(); // GERE LES COLLISIONS DU KART

            if (getCanMove() && getIsPressingKeyItem())
                useItem(); // UTILISE L'ITEM SI LE JOUEUR LE VEUT
            if (getIsKlaxoning()) {
                Klaxon.applyKlaxonToOthersKarts(this);
                setIsKlaxoning(false);
            }

            deltaplane(player); // ACTIVE LE DELTA PLANE
            rotateOrDrift(); // CALCUL LA ROTATION DU VEHCIULE

            if (getCanMove()) setVectorMovment(); // SINON ON CALCUL LE VECTEUR DE VITESSE

            setFallSpeed(calculateFallSpeed()); // CALCUL LA VITESSE DE CHUTE

            move(MoverType.SELF, new Vec3(getDeltaMovement().x, getFallSpeed(), getDeltaMovement().z)); //ON APPLIQUE LE VECTEUR DE VITESSE
            moveCamera(player); // BOUGE LA CAMERA EN CONSEQUENCE DU MOUVEMENT
        }
    }

    /**
     * Calcul la vitesse de chute du kart
     *
     * @return
     */
    private float calculateFallSpeed() {
        //VITESSE DE CHUTE : DANS LA MER OU EN DELTA
        if (isInWater() || getDeltaOn()) {
            return REDUCED_FALL_SPEED;
        }
        //VITESSE DE CHUTE : EN CHUTE LIBRE
        else if (!onGround() && !getDeltaOn() && getFallSpeed() >= FALL_SPEED_LIMIT) {
            return getFallSpeed() * FALL_SPEED_MULTIPLIER;
        }
        //VITESSE DE CHUTE : SUR TERRE
        return BASE_FALL_SPEED;
    }

    /**
     * Bouge la camera du joueur dans la direction où se dirige le kart
     *
     * @param player
     */
    private void moveCamera(Player player) {
        //ON BOUGE LA CAMERA DU CONDUCTEUR
        if (player != null) {
            player.setYRot(getYRot());
            player.setYBodyRot(getYRot());
        }
    }

    /**
     * Calcul est applique le vecteur de mouvement du kart
     */
    private void setVectorMovment() {
        //VECTEUR DE MOUVEMENT : BOOST
        if (getTimeBoost() > 0 || (getIsInvinsible() && !getIsPressingKeyDown())) {
            setSpeed(MAX_SPEED);
        }
        //VECTEUR DE MOUVEMENT : DELTA PLANE
        else if (getDeltaOn()) {
            setSpeed(DELTA_SPEED);
        }
        //VECTEUR DE MOUVEMENT : MARCHE AVANT !!!
        else if (getIsPressingKeyUp()) {
            setSpeed(Mth.clamp(getSpeed() + ACCELERATION_BOOST, -MAX_SPEED / 2, MAX_SPEED));
        }
        //VECTEUR DE MOUVEMENT : MARCHE ARRIERE !!!
        else if (getIsPressingKeyDown()) {
            setSpeed(Mth.clamp(getSpeed() - ACCELERATION_BOOST, -MAX_SPEED / 2, MAX_SPEED));
            resetDriftWithNoBoost();
        }
        //VECTEUR DE MOUVEMENT : RALENTISSEMENT AUTOMATIQUE
        else {
            slowDownKart();
        }
        setKartMovement();
    }

    /**
     * Applique le stun au kart
     */
    private void applyStun() {
        setIsPressingKeyUp(false);
        setIsPressingKeyDown(false);
        setIsPressingKeyLeft(false);
        setIsPressingKeyRight(false);
        setIsPressingKeyDelta(false);
        setPreviousPressingKeyDelta(false);
        setIsPressingKeyDrift(false);
        setIsPressingKeyItem(false);

        setDriftTimeBoost(0.f);
        setTimeBoost(0.f);

        setSpeed(Mth.clamp(getSpeed() - ACCELERATION_BOOST * 1.5f, 0, MAX_SPEED));
        setKartMovement();

        setStunRotation(getStunRotation() - 720 / (3 * 20));
    }

    /**
     * Utilise l'objet contenu dans le kart
     */
    private void useItem() {
        //SI L'OBJET DANS LE KART EST UNE BANANE
        if (getKartItem().equals("Banana")) {
            Network.CHANNEL.sendToServer(new BananaUseMessage());
            sendConductorMessage("BANANE !!!!!");
        } else if (getKartItem().equals("Mushroom")) {
            setTimeBoost(5.f);
            setSpeed(MAX_SPEED);
            sendConductorMessage("MUSHROOM !!!!!");
        } else if (getKartItem().equals("Star")) {
            setTimeStar(20f);
            setStarSpeedBoost(1.5f);
            setIsInvinsible(true);
            setSpeed(MAX_SPEED * getStarSpeedBoost());
            sendConductorMessage("STAR !!!!!");
        } else if (getKartItem().equals("Thunder")) {
            Network.CHANNEL.sendToServer(new ThunderUseMessage());
            sendConductorMessage("THUNDER !!!!!");
        } else if (getKartItem().equals("Klaxon")) {
            Network.CHANNEL.sendToServer(new KlaxonUseMessage());
            setIsKlaxoning(true);
            sendConductorMessage("KLAXON !!!!!");
        } else if (getKartItem().equals("Bob_omb")) {
            Network.CHANNEL.sendToServer(new BobOmbUseMessage());
            sendConductorMessage("BOB_OMB !!!!!");
        } else if (getKartItem().equals("Fake_box")) {
            Network.CHANNEL.sendToServer(new FakeBoxUseMessage());
            sendConductorMessage("FAKE_BOX !!!!!");
        } else if (getKartItem().equals("Green_shell")) {
            Network.CHANNEL.sendToServer(new GreenShellUseMessage());
            sendConductorMessage("GREEN_SHELL !!!!!");
        }
        setKartItem("None");
    }

    /**
     * Applique la rotation du kart en fonction de s'il tourne, drift, ou rien
     *
     */
    private void rotateOrDrift() {
        //ON INITIE LA ROTATION QUE SI LE VEHICULE EST EN MOUVEMENT
        if (getSpeed() != 0 && getCanMove()) {
            //SI LE JOUEUR APPUIE SUR LA TOUCHE DE DRIFT, QUE LE KART AVANCE ASSEZ VITE ET AUTRES CONDITIONS
            if (getIsPressingKeyDrift() && !horizontalCollision && !getDeltaOn() && getSpeed() > MAX_SPEED * 0.25) {
                //INIT DU DRIFT SI PAS ENCORE FAIT
                if (getDriftingTime() == 0) {
                    if (getIsPressingKeyLeft() && !getIsPressingKeyRight()) {
                        setDrifting("Left");
                    } else if (getIsPressingKeyRight() && !getIsPressingKeyLeft()) {
                        setDrifting("Right");
                    }
                }

                //SI LE DRIFT EST INITIALISE
                if (level().isClientSide() && getIsDrifting()) {
                    //SPAWN DES PARTICULES VIOLETTES
                    if (getDriftingTime() >= 3)
                        spawnDriftParticules(ParticlesInit.DRIFT_PURPLE_PARTICLES.get());
                        //SPAWN DES PARTICULES ROUGES
                    else if (getDriftingTime() >= 2)
                        spawnDriftParticules(ParticlesInit.DRIFT_ORANGE_PARTICLES.get());
                        //SPAWN DES PARTICULES BLEUES
                    else if (getDriftingTime() >= 1)
                        spawnDriftParticules(ParticlesInit.DRIFT_BLUE_PARTICLES.get());
                }

                //DRIFT INITIAL : DRIFT A GAUCHE
                if (getIsDrifting() && getDriftingSens().equals("Left")) {
                    //LE JOUEUR MAINTIENT LA TOUCHE GAUCHE
                    if (getIsPressingKeyLeft() && !getIsPressingKeyRight()) {
                        if (getIsPressingKeyUp() && getDriftingTime() < 3.0f)
                            setDriftingTime(getDriftingTime() + 0.06f);
                        setYRot(getYRot() - MANIABILITE_COEEF * DRIFT_ANGLE);
                    }
                    //LE JOUEUR MAINTIENT LA TOUCHE DROITE
                    else if (getIsPressingKeyRight() && !getIsPressingKeyLeft()) {
                        setYRot(getYRot() - MANIABILITE_COEEF * (DRIFT_ANGLE * 0.3f));
                    }
                    //LE JOUEUR NE MAINTIENT RIEN
                    else {
                        if (getIsPressingKeyUp() && getDriftingTime() < 3.0f)
                            setDriftingTime(getDriftingTime() + 0.02f);
                        setYRot(getYRot() - MANIABILITE_COEEF * (DRIFT_ANGLE * 0.75f));
                    }
                }
                //DRIFT INITIAL : DRIFT A DROITE
                else if (getIsDrifting() && getDriftingSens().equals("Right")) {
                    //LE JOUEUR MAINTIENT LA TOUCHE GAUCHE
                    if (getIsPressingKeyUp() && getIsPressingKeyRight() && !getIsPressingKeyLeft()) {
                        if (getDriftingTime() < 3.0f)
                            setDriftingTime(getDriftingTime() + 0.06f);
                        setYRot(getYRot() + MANIABILITE_COEEF * DRIFT_ANGLE);
                    }
                    //LE JOUEUR MAINTIENT LA TOUCHE DROITE
                    else if (getIsPressingKeyLeft() && !getIsPressingKeyRight()) {
                        setYRot(getYRot() + MANIABILITE_COEEF * (DRIFT_ANGLE * 0.5f));
                    }
                    //LE JOUEUR NE MAINTIENT RIEN
                    else {
                        if (getIsPressingKeyUp() && getDriftingTime() < 3.0f)
                            setDriftingTime(getDriftingTime() + 0.02f);
                        setYRot(getYRot() + MANIABILITE_COEEF * (DRIFT_ANGLE * 0.75f));
                    }
                }
            }
            //LE KART NE DRIFT PAS
            else {
                //ON RESET TOUS LES PARAMETRES EN RAPPORT AVEC LE DRIFT
                resetDrift();

                //SI LE JOUEUR MAINTIENT LE BOUTON GAUCHE : ROTATION GAUCHE
                if (getIsPressingKeyLeft() && !getIsPressingKeyRight()) {
                    if (getSpeed() > 0) setYRot(getYRot() - MANIABILITE_COEEF);
                    else setYRot(getYRot() + MANIABILITE_COEEF);
                }
                //SI LE JOUEUR MAINTIENT LE BOUTON DROIT : ROTATION DROITE
                else if (getIsPressingKeyRight() && !getIsPressingKeyLeft()) {
                    if (getSpeed() > 0) setYRot(getYRot() + MANIABILITE_COEEF);
                    else setYRot(getYRot() - MANIABILITE_COEEF);
                }
            }
        }
        //SI LE KART EST A L'ARRET
        else {
            //ON RESET TOUS LES PARAMETRES EN RAPPORT AVEC LE DRIFT
            resetDrift();
        }
    }

    /**
     * Active le deltaplane si demandé
     *
     * @param player
     */
    private void deltaplane(Player player) {
        if (player == null) return;

        //ACTIVATION DU DELTA PLANE
        if (!getDeltaOn() && !onGround() && (getIsPressingKeyDelta() && !getPreviousPressingKeyDelta()))
            setDeltaOn(true);
        else if (getDeltaOn() && ((getIsPressingKeyDelta() && !getPreviousPressingKeyDelta()) || onGround()))
            setDeltaOn(false);

        setPreviousPressingKeyDelta(getIsPressingKeyDelta());
    }

    /**
     * Ajoute ou retire le kart de la liste des karts stun
     */
    private void isStun() {
        //DETECTE SI LE KART EST EN SITUATION DE "STUN"
        if (getStunRotation() <= 0) {
            setCanMove(true);
            setStunRotation(0f);
        }
    }

    /**
     * Méthode qui gère tout ce qui touche aux collisions
     */
    private void collision() {
        //SI COLLISION, ON RESET LE BOOST DU DRIFT
        if (horizontalCollision) {
            setDriftingTime(0.f);
        }

        //SI EN ETOILE, ALORS ON STUN LES GENS QU'ON PERCUTE
        if (getIsInvinsible()) {
            List<Entity> nearbyEntities = level().getEntities(this, getBoundingBox().inflate(0.5f));
            for (Entity entity : nearbyEntities) {
                if (entity instanceof Kart kart) {
                    if (kart.getCanMove()) {
                        Kart.stunKart(kart);
                    }
                }
            }
        }

        //ON VERIFIE QUE LE JOUEUR EST ENCORE EN ETOILE
        if (getIsInvinsible() && getTimeStar() <= 0) {
            setIsInvinsible(false);
            setStarSpeedBoost(1f);
        }
    }

    //////////////////
    // KART SECTION //
    //////////////////

    /**
     * Méthode pour update le vecteur de vitesse du kart
     */
    public void setKartMovement() {
        double angle = Math.toRadians(getYRot());

        //SPAWN DES PARTICULES DE BOOST
        if (!getIsInvinsible() && getSpeed() > 0 && (getDriftTimeBoost() > 0 || getTimeBoost() > 0)) {
            spawnBoostParticules(ParticleTypes.FLAME);
        }

        float clamped_speed = getSpeed();
        //BOOST LE JOUEUR S'IL EST SOUS BOOST DE DRIFT OU CHAMPIGNON
        if (getDriftTimeBoost() > 0 || getTimeBoost() > 0) {
            clamped_speed = Mth.clamp(getSpeed() + BOOST, 0, MAX_SPEED + BOOST);
            if (getDriftTimeBoost() > 0)
                setDriftTimeBoost(getDriftTimeBoost() - 0.1f);
            else if (getTimeBoost() > 0)
                setTimeBoost(getTimeBoost() - 0.1f);
        }
        //ACCELERE LE TOUT SI SOUS ETOILE
        if (getIsInvinsible() && !getIsPressingKeyDown())
            clamped_speed = clamped_speed * getStarSpeedBoost();
        if (getTimeStar() > 0) setTimeStar(getTimeStar() - 0.1f);
        setSpeed(clamped_speed);

        //CALCUL ET APPLICATION DU VECTEUR DE DEPLACEMENT
        double x = Math.sin(-angle) * clamped_speed;
        double z = Math.cos(-angle) * clamped_speed;
        Vec3 vec3 = new Vec3(x, 0, z);
        setDeltaMovement(vec3);
    }

    /**
     * Méthode pour ralentir le kart en updatant le vecteur de vitesse
     */
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

            float clamped_speed = Mth.clamp(getSpeed(), -MAX_SPEED / 2, MAX_SPEED);
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

    /**
     * Reset les attributs de drift du kart
     */
    public void resetDrift() {
        if (getDriftingTime() != 0)
            setDriftTimeBoost((float) Math.floor(getDriftingTime()));
        setIsDrifting(false);
        setDriftingTime(0.f);
        setDriftingSens("None");
    }

    /**
     * reset les attributs de drift du kart sans donner de boost
     */
    public void resetDriftWithNoBoost() {
        setIsDrifting(false);
        setDriftingTime(0.f);
        setDriftingSens("None");
    }

    /**
     * Set les attributs du drift en fonction du sens de rotation du kart
     *
     * @param sens
     */
    public void setDrifting(String sens) {
        setIsDrifting(true);
        setDriftingTime(0.1f);
        setDriftingSens(sens);
    }

    /**
     * Fonction qui gère les particules lorsque le Kart reçoit un BOOST
     *
     * @param particle
     */
    public void spawnBoostParticules(SimpleParticleType particle) {
        if (!level().isClientSide()) return;

        float yaw = getYRot();
        double motionX = -Math.sin(Math.toRadians(yaw));
        double motionZ = Math.cos(Math.toRadians(yaw));
        spawnParticules(particle, 0.25 * Math.cos(Math.toRadians(yaw)), 0, 0.25 * Math.sin(Math.toRadians(yaw)),
                motionX * 0.5f, 0, motionZ * 0.5f);
    }

    /**
     * Fonction qui gère les particules du drift
     *
     * @param particle
     */
    public void spawnDriftParticules(SimpleParticleType particle) {
        if (!level().isClientSide()) return;

        float yaw = getYRot();
        spawnParticules(particle, 1 * Math.cos(Math.toRadians(yaw)), 0, 1 * Math.sin(Math.toRadians(yaw)),
                0, 0, 0);
    }

    /**
     * Fonction qui gère le spawn des particules en général
     *
     * @param particle
     * @param x1       = ajustement en x
     * @param y1       = ajustement en y
     * @param z1       = ajustement en z
     * @param x2       = vecteur de direction des particules en x
     * @param y2       = vecteur de direction des particules en y
     * @param z2       = vecteur de direction des particules en z
     */
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

    @Override
    /**
     * Méthode qui fait en sorte de détruire le kart quand il prend des dégats
     */
    public boolean hurt(DamageSource damage, float p_19947_) {
        if (damage.getEntity() instanceof Player player) {
            if (player.getVehicle() == null) {
                remove(RemovalReason.KILLED);
                return true;
            }
        }
        return false;
    }

    /**
     * Méthode static qui stun le kart
     *
     * @param kart
     */
    public static void stunKart(Kart kart) {
        if (kart.getIsInvinsible()) return;

        kart.setCanMove(false);
        kart.setStunRotation(720.f);

        kart.setDeltaOn(false);
        kart.setDriftTimeBoost(0.f);
        kart.resetDrift();
    }

    ///////////////////////
    // ANIMATION SECTION //
    ///////////////////////

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    /**
     * Gère les animations en fonction du statut du kart
     *
     * @return
     */
    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState) {
        //ANIMATION DE STUN
        if (!getCanMove()) {
            //ANIMATION : ARRET SOUS L'EAU
            if (isInWater()) {
                setWaterAnimationState(2);
                tAnimationState.getController().setAnimation(RawAnimation.begin()
                        .then("propeller_stopped", Animation.LoopType.HOLD_ON_LAST_FRAME));
            }
            //ANIMATION : ARRET
            else {
                tAnimationState.getController().setAnimation(RawAnimation.begin()
                        .then("engine", Animation.LoopType.HOLD_ON_LAST_FRAME));
            }
            return PlayState.CONTINUE;
        }

        //ANIMATION : DANS L'EAU
        if (isInWater()) {
            //ANIMATION : MARCHE AVANT
            if (getSpeed() > MIN_SPEED) {
                //ANIMATION : MARCHE AVANT - ENTREE DANS L'EAU
                if (getWaterAnimationState() == 0 || getWaterAnimationState() == 3) {
                    setWaterAnimationState(1);
                    tAnimationState.getController().setAnimation(RawAnimation.begin()
                            .then("propeller_on", Animation.LoopType.PLAY_ONCE)
                            .then("propeller_backwards", Animation.LoopType.LOOP)
                    );
                }
                //ANIMATION : MARCHE AVANT - SIMPLE
                else if (getWaterAnimationState() == 2) {
                    tAnimationState.getController().setAnimation(RawAnimation.begin()
                            .then("propeller_backwards", Animation.LoopType.LOOP));
                }
            }
            //ANIMATION : MARCHE ARRIERE
            else if (getSpeed() < (-MIN_SPEED)) {
                setWaterAnimationState(2);
                tAnimationState.getController().setAnimation(RawAnimation.begin()
                        .then("propeller_forwards", Animation.LoopType.LOOP));
            }
            //ANIMATION : ARRET
            else {
                setWaterAnimationState(2);
                tAnimationState.getController().setAnimation(RawAnimation.begin()
                        .then("propeller_stopped", Animation.LoopType.HOLD_ON_LAST_FRAME));
            }
        }
        //ANIMATION : HORS DE L'EAU
        else {
            //ANIMATION : DANS LES AIRS
            if (getDeltaOn()) {
                setDeltaAnimationState(1);
                tAnimationState.getController().setAnimation(RawAnimation.begin()
                        .then("glider_on", Animation.LoopType.HOLD_ON_LAST_FRAME));
            }
            //ANIMATION : MARCHE AVANT
            else if (getSpeed() > MIN_SPEED) {
                //ANIMATION : MARCHE AVANT - ATTERISSAGE
                if (getDeltaAnimationState() == 1) {
                    setDeltaAnimationState(2);
                    tAnimationState.getController().setAnimation(RawAnimation.begin()
                            .then("glider_off", Animation.LoopType.PLAY_ONCE)
                            .then("engine", Animation.LoopType.LOOP));
                }
                //ANIMATION : MARCHE AVANT - SORTIE D'EAU
                else if (getWaterAnimationState() > 0) {
                    setWaterAnimationState(3);
                    tAnimationState.getController().setAnimation(RawAnimation.begin()
                            .then("propeller_off", Animation.LoopType.PLAY_ONCE)
                            .then("engine", Animation.LoopType.LOOP));
                }
                //ANIMATION : MARCHE AVANT
                else if (getDeltaAnimationState() == 0 && getWaterAnimationState() == 0) {
                    tAnimationState.getController().setAnimation(RawAnimation.begin()
                            .then("engine", Animation.LoopType.LOOP));
                }
            }
            //ANIMATION : MARCHE ARRIERE
            else if (getSpeed() < (-MIN_SPEED)) {
                setDeltaAnimationState(0);
                setWaterAnimationState(0);
                tAnimationState.getController().setAnimation(RawAnimation.begin()
                        .then("engine", Animation.LoopType.LOOP));
            }
            //ANIMATION : ARRET
            else {
                setDeltaAnimationState(0);
                setWaterAnimationState(0);
                tAnimationState.getController().setAnimation(RawAnimation.begin()
                        .then("engine", Animation.LoopType.HOLD_ON_LAST_FRAME));
            }
        }

        return PlayState.CONTINUE;
    }
}
