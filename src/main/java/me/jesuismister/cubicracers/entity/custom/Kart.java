package me.jesuismister.cubicracers.entity.custom;

import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.event.network.Network;
import me.jesuismister.cubicracers.event.network.message.*;
import me.jesuismister.cubicracers.event.network.message.use.*;
import me.jesuismister.cubicracers.init.KartInit;
import me.jesuismister.cubicracers.particles.ParticlesInit;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

@Mod.EventBusSubscriber(modid = CubicRacers.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class Kart extends Entity implements GeoEntity {
    private static final EntityDataAccessor<Float> SPEED = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.FLOAT);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    //KEYS POUR LE KART
    public boolean isPressingKeyUp = false;
    public boolean isPressingKeyDown = false;
    public boolean isPressingKeyLeft = false;
    public boolean isPressingKeyRight = false;
    public boolean isPressingKeyDrift = false;
    public boolean isPressingKeyItem = false;
    public boolean isPressingKeyDelta = false;
    public boolean previousPressingKeyDelta = false;

    //ATTRIBUTS GENERAUX DES KARTS
    private static final float MIN_SPEED = 0.075f;
    private static final float FREINAGE_SPEED = 1.05f;
    private static final float BASE_FALL_SPEED = -0.5f;
    private static final float REDUCED_FALL_SPEED = -0.2f;
    private static final float FALL_SPEED_LIMIT = -3.0f;
    private static final float FALL_SPEED_MULTIPLIER = 1.05f;
    private static final float COEFF_FROTTEMENT = 0.85f;

    //PATH
    public final String TEXTURE;
    public final String MODEL;
    public final String ANIMATION;

    //ATTRIBUTS DU KART
    public final float MAX_SPEED;
    private final float DELTA_SPEED;
    private final float ACCELERATION_BOOST;
    private final float BOOST;
    private final float MANIABILITE_COEEF;
    private final float PLAYER_POS_Y;

    //ATTRIBUTS DU DRIFT
    private static final float DRIFT_ANGLE = 1.5f;
    public boolean isDrifting = false;
    public String driftingSens = "None";
    public float driftingTime = 0.0f;
    public float driftTimeBoost = 0.0f;
    public float timeBoost = 0.0f;

    //ATTRIBUTS DE CONDUITE
    public boolean deltaOn = false;
    public int deltaAnimationState = 0;
    public int waterAnimationState = 0;
    private float fallSpeed = BASE_FALL_SPEED;
    public float pourcentage_inclinaison = 0;
    public float actual_rotation_wheels = 0;

    //ANIMATION DEGATS
    public static final float SPINNING_ANIMATION_TIME = 20.0f * 2.0f;
    public boolean canMove = true;
    public float stunRotation = 0;

    //KART ITEM
    public String kartItem = "Star";
    private boolean isInvinsible = false;
    private float starSpeedBoost = 1f; //COEFF DE BOOST / 1 PAR DEFAUT / 1.5 SOUS ETOILE
    private float timeStar = 0;

    /**
     * Constructeur de base
     */
    public Kart(EntityType<?> entityType, Level level, String texture, String model, String animation, float maxSpeed,
                float accelerationBoost, float boost, float maniabiliteCoeff, float playerPosY) {
        super(entityType, level);

        this.TEXTURE = texture;
        this.MODEL = model;
        this.ANIMATION = animation;

        this.MAX_SPEED = maxSpeed;
        this.DELTA_SPEED = MAX_SPEED;
        this.ACCELERATION_BOOST = accelerationBoost;
        this.BOOST = boost;
        this.MANIABILITE_COEEF = maniabiliteCoeff;

        this.PLAYER_POS_Y = playerPosY;
        this.setInvulnerable(false);
    }

    /**
     * Constructeur pour le spawn via item
     */
    public Kart(Level level, double x, double y, double z, String name, String texture, String model, String animation,
                float maxSpeed, float accelerationBoost, float boost, float maniabiliteCoeff, float playerPosY) {

        this(KartInit.KARTS.get(name).get(), level, texture, model, animation, maxSpeed, accelerationBoost, boost,
                maniabiliteCoeff, playerPosY);

        this.setPos(x, y, z);
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(SPEED, 0.0f);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag p_20052_) {
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag p_20139_) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean isNoGravity() {
        return false;
    }

    @Override
    public float getStepHeight() {
        return 1.2f;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    protected void positionRider(Entity player, MoveFunction p_19958_) {
        super.positionRider(player, p_19958_);
        double x = player.getX();
        double y = player.getY() + PLAYER_POS_Y;
        double z = player.getZ();
        player.setPos(x, y, z);
    }

    @Override
    /**
     * Gestion des interraction avec le kart
     */
    public @NotNull InteractionResult interact(Player player, @NotNull InteractionHand hand) {
        //SI LE JOUEUR N'EST PAS DANS LE KART
        if (this.getFirstPassenger() == null) {
            //ALORS LE JOUEUR MONTE DANS LE KART
            player.startRiding(this);
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }

    //////////////////
    // KART SECTION //
    //////////////////

    @Override
    /**
     * installe le joueur sur le kart
     */
    public boolean startRiding(@NotNull Entity rider) {
        Player player = (Player) rider;
        return super.startRiding(player);
    }

    @Override
    /**
     * Le conducteur peut interragir (??? - tester de voir à false ce que ça fait)
     */
    public boolean canRiderInteract() {
        return true;
    }

    @Override
    /**
     * Peut monter le kart
     */
    protected boolean canRide(@NotNull Entity rider) {
        return true;
    }

    /**
     * Récupère la vitesse du kart
     *
     * @return
     */
    public float getSpeed() {
        return this.entityData.get(SPEED);
    }

    /**
     * Modifie la vitesse du kart
     *
     * @param new_speed = nouvelle vitesse
     */
    public void setSpeed(float new_speed) {
        this.entityData.set(SPEED, new_speed);
    }

    /**
     * Méthode pour update le vecteur de vitesse du kart
     */
    public void setKartMovement() {
        double angle = Math.toRadians(this.getYRot());

        //SPAWN DES PARTICULES DE BOOST
        if (!this.isInvinsible && this.getSpeed() > 0 && (this.driftTimeBoost > 0 || this.timeBoost > 0)) {
            this.spawnBoostParticules(ParticleTypes.FLAME);
        }

        float clamped_speed = this.getSpeed();
        //BOOST LE JOUEUR S'IL EST SOUS BOOST DE DRIFT OU CHAMPIGNON
        if (this.driftTimeBoost > 0 || this.timeBoost > 0) {
            clamped_speed = Mth.clamp(this.getSpeed() + BOOST, 0, MAX_SPEED + BOOST);
            if (this.driftTimeBoost > 0) driftTimeBoost -= 0.1f;
            else if (this.timeBoost > 0) timeBoost -= 0.1f;
        }
        //ACCELERE LE TOUT SI SOUS ETOILE
        if (isInvinsible && !isPressingKeyDown) clamped_speed = clamped_speed * starSpeedBoost;
        if (this.timeStar > 0) timeStar -= 0.1f;

        this.setSpeed(clamped_speed);

        //CALCUL ET APPLICATION DU VECTEUR DE DEPLACEMENT
        double x = Math.sin(-angle) * clamped_speed;
        double z = Math.cos(-angle) * clamped_speed;
        Vec3 vec3 = new Vec3(x, 0, z);
        this.setDeltaMovement(vec3);
    }

    /**
     * Méthode pour ralentir le kart en updatant le vecteur de vitesse
     */
    public void slowDownKart() {
        double x, z;
        //SI LE KART EST COMPRIS ENTRE -MIN_SPEED ET MIN_SPEED ALORS IL S'ARRETE
        if (this.getSpeed() > -MIN_SPEED && this.getSpeed() < MIN_SPEED) {
            this.setSpeed(0);
            x = 0;
            z = 0;
        }
        //SINON LE KART RALENTIT PROGRESSIVEMENT
        else {
            double angle = Math.toRadians(this.getYRot());

            this.setSpeed(this.getSpeed() / (FREINAGE_SPEED));

            float clamped_speed = Mth.clamp(this.getSpeed(), -MAX_SPEED / 2, MAX_SPEED);
            this.setSpeed(clamped_speed);

            x = Math.sin(-angle) * clamped_speed;
            if (this.horizontalCollision) x *= COEFF_FROTTEMENT;
            z = Math.cos(-angle) * clamped_speed;
            if (this.horizontalCollision) z *= COEFF_FROTTEMENT;
        }

        //APPLICATION DU VECTEUR DE DEPLACEMENT
        Vec3 vec3 = new Vec3(x, 0, z);
        this.setDeltaMovement(vec3);
    }

    /**
     * FONCTION POUR DEBUG : ENVOIE UN MESSAGE AU CONDUCTEUR DU VEHICULE
     *
     * @param msg
     */
    public void sendConductorMessage(String msg) {
        try {
            if (this != null && this.getFirstPassenger() != null && this.getFirstPassenger() instanceof Player) {
                this.getFirstPassenger().sendSystemMessage(Component.literal(msg));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Reset les attributs de drift du kart
     */
    public void resetDrift() {
        if (driftingTime != 0) this.driftTimeBoost = (float) Math.floor(driftingTime);
        this.isDrifting = false;
        this.driftingTime = 0;
        this.driftingSens = "None";
    }

    /**
     * reset les attributs de drift du kart sans donner de boost
     */
    public void resetDriftWithNoBoost() {
        this.isDrifting = false;
        this.driftingTime = 0;
        this.driftingSens = "None";
    }

    /**
     * Set les attributs du drift en fonction du sens de rotation du kart
     *
     * @param sens
     */
    public void setDrifting(String sens) {
        this.isDrifting = true;
        this.driftingTime = 0.1f;
        this.driftingSens = sens;
    }

    /**
     * Fonction qui gère les particules lorsque le Kart reçoit un BOOST
     *
     * @param particle
     */
    public void spawnBoostParticules(SimpleParticleType particle) {
        if (!this.level().isClientSide()) return;

        float yaw = this.getYRot();
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
        if (!this.level().isClientSide()) return;

        float yaw = this.getYRot();
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
        if (!this.level().isClientSide()) return;

        Minecraft minecraft = Minecraft.getInstance();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();

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
                this.remove(RemovalReason.KILLED);
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
        if (kart.isInvinsible) return;

        kart.canMove = false;
        kart.stunRotation = 720;

        kart.deltaOn = false;
        kart.driftTimeBoost = 0;
        kart.resetDrift();
    }

    //////////////////
    // TICK SECTION //
    //////////////////

    @Override
    public void tick() {
        super.tick();
        Player player = (Player) this.getFirstPassenger();
        if (player == null) {
            isPressingKeyUp = isPressingKeyDown = isPressingKeyLeft = isPressingKeyRight = isPressingKeyDelta = isPressingKeyDrift = isPressingKeyItem = false;
            this.slowDownKart();
            this.setKartMovement();
        } else {
            if (this.level().isClientSide()) {
                collision(); // GERE LES COLLISIONS DU KART

                if (canMove && isPressingKeyItem) useItem(); // UTILISE L'ITEM SI LE JOUEUR LE VEUT

                deltaplane(player); // ACTIVE LE DELTA PLANE
                rotateOrDrift(player); // CALCUL LA ROTATION DU VEHCIULE

                isStun(); // ON VOIT SI LE KART EST STUN
                if (!this.canMove) applyStun(); // SI LE KART EST STUN, ON APPLIQUE LA PROCEDURE DE STUN
                else setVectorMovment(); // SINON ON CALCUL LE VECTEUR DE VITESSE

                this.fallSpeed = calculateFallSpeed(); // CALCUL LA VITESSE DE CHUTE

                this.move(MoverType.SELF, new Vec3(this.getDeltaMovement().x, fallSpeed, this.getDeltaMovement().z)); //ON APPLIQUE LE VECTEUR DE VITESSE
                moveCamera(player); // BOUGE LA CAMERA EN CONSEQUENCE DU MOUVEMENT

                Network.CHANNEL.sendToServer(new KartMessage(this.getX(), this.getY(), this.getZ(), this.getYRot()));
            }
        }
    }

    /**
     * Calcul la vitesse de chute du kart
     *
     * @return
     */
    private float calculateFallSpeed() {
        //VITESSE DE CHUTE : DANS LA MER OU EN DELTA
        if (this.isInWater() || this.deltaOn) {
            return REDUCED_FALL_SPEED;
        }
        //VITESSE DE CHUTE : EN CHUTE LIBRE
        else if (!this.onGround() && !this.deltaOn && fallSpeed >= FALL_SPEED_LIMIT) {
            return fallSpeed * FALL_SPEED_MULTIPLIER;
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
            player.setYRot(this.getYRot());
            player.setYBodyRot(this.getYRot());
        }
    }

    /**
     * Calcul est applique le vecteur de mouvement du kart
     */
    private void setVectorMovment() {
        //VECTEUR DE MOUVEMENT : BOOST
        if (this.timeBoost > 0 || (isInvinsible && !isPressingKeyDown)) {
            this.setSpeed(MAX_SPEED);
        }
        //VECTEUR DE MOUVEMENT : DELTA PLANE
        else if (deltaOn) {
            this.setSpeed(DELTA_SPEED);
        }
        //VECTEUR DE MOUVEMENT : MARCHE AVANT !!!
        else if (isPressingKeyUp) {
            this.setSpeed(Mth.clamp(this.getSpeed() + ACCELERATION_BOOST, -MAX_SPEED / 2, MAX_SPEED));
        }
        //VECTEUR DE MOUVEMENT : MARCHE ARRIERE !!!
        else if (isPressingKeyDown) {
            this.setSpeed(Mth.clamp(this.getSpeed() - ACCELERATION_BOOST, -MAX_SPEED / 2, MAX_SPEED));
            this.resetDriftWithNoBoost();
        }
        //VECTEUR DE MOUVEMENT : RALENTISSEMENT AUTOMATIQUE
        else {
            this.slowDownKart();
        }
        this.setKartMovement();
    }

    /**
     * Applique le stun au kart
     */
    private void applyStun() {
        isPressingKeyUp = isPressingKeyDown = isPressingKeyLeft = isPressingKeyRight = isPressingKeyDelta = isPressingKeyDrift = isPressingKeyItem = false;

        this.driftTimeBoost = 0;
        this.timeBoost = 0;

        this.setSpeed(Mth.clamp(this.getSpeed() - ACCELERATION_BOOST * 1.5f, 0, MAX_SPEED));
        this.setKartMovement();

        this.stunRotation = this.stunRotation - 720 / (3 * 20);
    }

    /**
     * Utilise l'objet contenu dans le kart
     */
    private void useItem() {
        //SI L'OBJET DANS LE KART EST UNE BANANE
        if (this.kartItem.equals("Banana")) {
            Network.CHANNEL.sendToServer(new BananaUseMessage());
            sendConductorMessage("BANANE !!!!!");
        } else if (this.kartItem.equals("Mushroom")) {
            this.timeBoost += 5.0f;
            setSpeed(MAX_SPEED);
            sendConductorMessage("MUSHROOM !!!!!");
        } else if (this.kartItem.equals("Star")) {
            this.timeStar += 20f;
            this.starSpeedBoost = 1.5f;
            this.isInvinsible = true;
            setSpeed(MAX_SPEED * starSpeedBoost);
            sendConductorMessage("STAR !!!!!");
        } else if (this.kartItem.equals("Thunder")) {
            Network.CHANNEL.sendToServer(new ThunderUseMessage());
            sendConductorMessage("THUNDER !!!!!");
        } else if (this.kartItem.equals("Klaxon")) {
            Network.CHANNEL.sendToServer(new KlaxonUseMessage());
            sendConductorMessage("KLAXON !!!!!");
        } else if (this.kartItem.equals("Bob_omb")) {
            Network.CHANNEL.sendToServer(new BobOmbUseMessage());
            sendConductorMessage("BOB_OMB !!!!!");
        } else if (this.kartItem.equals("Fake_box")) {
            Network.CHANNEL.sendToServer(new FakeBoxUseMessage());
            sendConductorMessage("FAKE_BOX !!!!!");
        } else if (this.kartItem.equals("Green_shell")) {
            Network.CHANNEL.sendToServer(new GreenShellUseMessage());
            sendConductorMessage("GREEN_SHELL !!!!!");
        }
        this.kartItem = "None";
    }

    /**
     * Applique la rotation du kart en fonction de s'il tourne, drift, ou rien
     *
     * @param player
     */
    private void rotateOrDrift(Player player) {
        //ON INITIE LA ROTATION QUE SI LE VEHICULE EST EN MOUVEMENT
        if (this.getSpeed() != 0 && this.canMove) {
            //SI LE JOUEUR APPUIE SUR LA TOUCHE DE DRIFT, QUE LE KART AVANCE ASSEZ VITE ET AUTRES CONDITIONS
            if (isPressingKeyDrift && !this.horizontalCollision && !this.deltaOn && this.getSpeed() > MAX_SPEED * 0.25) {
                //INIT DU DRIFT SI PAS ENCORE FAIT
                if (this.driftingTime == 0) {
                    if (isPressingKeyLeft && !isPressingKeyRight) {
                        this.setDrifting("Left");
                    } else if (isPressingKeyRight && !isPressingKeyLeft) {
                        this.setDrifting("Right");
                    }
                }

                //SI LE DRIFT EST INITIALISE
                if (this.level().isClientSide() && this.isDrifting) {
                    //SPAWN DES PARTICULES VIOLETTES
                    if (this.driftingTime >= 3) spawnDriftParticules(ParticlesInit.DRIFT_PURPLE_PARTICLES.get());
                        //SPAWN DES PARTICULES ROUGES
                    else if (this.driftingTime >= 2) spawnDriftParticules(ParticlesInit.DRIFT_ORANGE_PARTICLES.get());
                        //SPAWN DES PARTICULES BLEUES
                    else if (this.driftingTime >= 1) spawnDriftParticules(ParticlesInit.DRIFT_BLUE_PARTICLES.get());
                }

                //DRIFT INITIAL : DRIFT A GAUCHE
                if (this.isDrifting && this.driftingSens.equals("Left")) {
                    //LE JOUEUR MAINTIENT LA TOUCHE GAUCHE
                    if (isPressingKeyLeft && !isPressingKeyRight) {
                        if (isPressingKeyUp && this.driftingTime < 3.0f) driftingTime += 0.06f;
                        this.setYRot(this.getYRot() - MANIABILITE_COEEF * DRIFT_ANGLE);
                    }
                    //LE JOUEUR MAINTIENT LA TOUCHE DROITE
                    else if (isPressingKeyRight && !isPressingKeyLeft) {
                        this.setYRot(this.getYRot() - MANIABILITE_COEEF * (DRIFT_ANGLE * 0.3f));
                    }
                    //LE JOUEUR NE MAINTIENT RIEN
                    else {
                        if (isPressingKeyUp && this.driftingTime < 3.0f) driftingTime += 0.02f;
                        this.setYRot(this.getYRot() - MANIABILITE_COEEF * (DRIFT_ANGLE * 0.75f));
                    }
                }
                //DRIFT INITIAL : DRIFT A DROITE
                else if (this.isDrifting && this.driftingSens.equals("Right")) {
                    //LE JOUEUR MAINTIENT LA TOUCHE GAUCHE
                    if (isPressingKeyUp && isPressingKeyRight && !isPressingKeyLeft) {
                        if (this.driftingTime < 3.0f) driftingTime += 0.06f;
                        this.setYRot(this.getYRot() + MANIABILITE_COEEF * DRIFT_ANGLE);
                    }
                    //LE JOUEUR MAINTIENT LA TOUCHE DROITE
                    else if (isPressingKeyLeft && !isPressingKeyRight) {
                        this.setYRot(this.getYRot() + MANIABILITE_COEEF * (DRIFT_ANGLE * 0.5f));
                    }
                    //LE JOUEUR NE MAINTIENT RIEN
                    else {
                        if (isPressingKeyUp && this.driftingTime < 3.0f) driftingTime += 0.02f;
                        this.setYRot(this.getYRot() + MANIABILITE_COEEF * (DRIFT_ANGLE * 0.75f));
                    }
                }
            }
            //LE KART NE DRIFT PAS
            else {
                //ON RESET TOUS LES PARAMETRES EN RAPPORT AVEC LE DRIFT
                this.resetDrift();

                //SI LE JOUEUR MAINTIENT LE BOUTON GAUCHE : ROTATION GAUCHE
                if (isPressingKeyLeft && !isPressingKeyRight) {
                    if (this.getSpeed() > 0) this.setYRot(this.getYRot() - MANIABILITE_COEEF);
                    else this.setYRot(this.getYRot() + MANIABILITE_COEEF);
                }
                //SI LE JOUEUR MAINTIENT LE BOUTON DROIT : ROTATION DROITE
                else if (isPressingKeyRight && !isPressingKeyLeft) {
                    if (this.getSpeed() > 0) this.setYRot(this.getYRot() + MANIABILITE_COEEF);
                    else this.setYRot(this.getYRot() - MANIABILITE_COEEF);
                }
            }
        }
        //SI LE KART EST A L'ARRET
        else {
            //ON RESET TOUS LES PARAMETRES EN RAPPORT AVEC LE DRIFT
            this.resetDrift();
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
        if (!this.deltaOn && !this.onGround() && (isPressingKeyDelta && !previousPressingKeyDelta))
            deltaOn = true;
        else if (this.deltaOn && ((isPressingKeyDelta && !previousPressingKeyDelta) || this.onGround()))
            deltaOn = false;

        previousPressingKeyDelta = isPressingKeyDelta;
    }

    /**
     * Ajoute ou retire le kart de la liste des karts stun
     */
    private void isStun() {
        //DETECTE SI LE KART EST EN SITUATION DE "STUN"
        if (this.stunRotation <= 0) {
            canMove = true;
            this.stunRotation = 0;
        }
    }

    /**
     * Méthode qui gère tout ce qui touche aux collisions
     */
    private void collision() {
        //SI COLLISION, ON RESET LE BOOST DU DRIFT
        if (this.horizontalCollision) {
            this.driftingTime = 0;
        }

        //SI EN ETOILE, ALORS ON STUN LES GENS QU'ON PERCUTE
        if (this.isInvinsible) {
            List<Entity> nearbyEntities = level().getEntities(this, this.getBoundingBox().inflate(0.5f));
            for (Entity entity : nearbyEntities) {
                if (entity instanceof Kart kart) {
                    if (kart.canMove) {
                        Kart.stunKart(kart);
                    }
                }
            }
        }

        //ON VERIFIE QUE LE JOUEUR EST ENCORE EN ETOILE
        if (this.isInvinsible && this.timeStar <= 0) {
            this.isInvinsible = false;
            this.starSpeedBoost = 1f;
        }
    }

    ///////////////////////
    // ANIMATION SECTION //
    ///////////////////////

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
        if (!this.canMove) {
            //ANIMATION : ARRET SOUS L'EAU
            if (this.isInWater()) {
                waterAnimationState = 2;
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
        if (this.isInWater()) {
            //ANIMATION : MARCHE AVANT
            if (this.getSpeed() > MIN_SPEED) {
                //ANIMATION : MARCHE AVANT - ENTREE DANS L'EAU
                if (waterAnimationState == 0 || waterAnimationState == 3) {
                    waterAnimationState = 1;
                    tAnimationState.getController().setAnimation(RawAnimation.begin()
                            .then("propeller_on", Animation.LoopType.PLAY_ONCE)
                            .then("propeller_backwards", Animation.LoopType.LOOP)
                    );
                }
                //ANIMATION : MARCHE AVANT - SIMPLE
                else if (waterAnimationState == 2) {
                    tAnimationState.getController().setAnimation(RawAnimation.begin()
                            .then("propeller_backwards", Animation.LoopType.LOOP));
                }
            }
            //ANIMATION : MARCHE ARRIERE
            else if (this.getSpeed() < (-MIN_SPEED)) {
                waterAnimationState = 2;
                tAnimationState.getController().setAnimation(RawAnimation.begin()
                        .then("propeller_forwards", Animation.LoopType.LOOP));
            }
            //ANIMATION : ARRET
            else {
                waterAnimationState = 2;
                tAnimationState.getController().setAnimation(RawAnimation.begin()
                        .then("propeller_stopped", Animation.LoopType.HOLD_ON_LAST_FRAME));
            }
        }
        //ANIMATION : HORS DE L'EAU
        else {
            //ANIMATION : DANS LES AIRS
            if (this.deltaOn) {
                deltaAnimationState = 1;
                tAnimationState.getController().setAnimation(RawAnimation.begin()
                        .then("glider_on", Animation.LoopType.HOLD_ON_LAST_FRAME));
            }
            //ANIMATION : MARCHE AVANT
            else if (this.getSpeed() > MIN_SPEED) {
                //ANIMATION : MARCHE AVANT - ATTERISSAGE
                if (deltaAnimationState == 1) {
                    deltaAnimationState = 2;
                    tAnimationState.getController().setAnimation(RawAnimation.begin()
                            .then("glider_off", Animation.LoopType.PLAY_ONCE)
                            .then("engine", Animation.LoopType.LOOP));
                }
                //ANIMATION : MARCHE AVANT - SORTIE D'EAU
                else if (waterAnimationState > 0) {
                    waterAnimationState = 3;
                    tAnimationState.getController().setAnimation(RawAnimation.begin()
                            .then("propeller_off", Animation.LoopType.PLAY_ONCE)
                            .then("engine", Animation.LoopType.LOOP));
                }
                //ANIMATION : MARCHE AVANT
                else if (deltaAnimationState == 0 && waterAnimationState == 0) {
                    tAnimationState.getController().setAnimation(RawAnimation.begin()
                            .then("engine", Animation.LoopType.LOOP));
                }
            }
            //ANIMATION : MARCHE ARRIERE
            else if (this.getSpeed() < (-MIN_SPEED)) {
                deltaAnimationState = 0;
                waterAnimationState = 0;
                tAnimationState.getController().setAnimation(RawAnimation.begin()
                        .then("engine", Animation.LoopType.LOOP));
            }
            //ANIMATION : ARRET
            else {
                deltaAnimationState = 0;
                waterAnimationState = 0;
                tAnimationState.getController().setAnimation(RawAnimation.begin()
                        .then("engine", Animation.LoopType.HOLD_ON_LAST_FRAME));
            }
        }

        return PlayState.CONTINUE;
    }
}
