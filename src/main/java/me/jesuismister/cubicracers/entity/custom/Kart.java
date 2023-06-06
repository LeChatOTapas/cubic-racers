package me.jesuismister.cubicracers.entity.custom;

import me.jesuismister.cubicracers.particles.ParticlesInit;
import me.jesuismister.cubicracers.util.KeyBinds;
import net.minecraft.client.KeyMapping;
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
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;

public class Kart extends Entity implements GeoEntity {
    private static final EntityDataAccessor<Float> SPEED = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.FLOAT);
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    //KEYS POUR LE KART
    private final KeyMapping keyUp = KeyBinds.KART_UP_KEY;
    private final KeyMapping keyDown = KeyBinds.KART_DOWN_KEY;
    public final KeyMapping keyLeft = KeyBinds.KART_LEFT_KEY;
    public final KeyMapping keyRight = KeyBinds.KART_RIGHT_KEY;
    private final KeyMapping keyDelta = KeyBinds.KART_DELTA_KEY;
    private final KeyMapping keyDrift = KeyBinds.KART_DRIFT_KEY;
    private boolean previousKeyJump = false;
    //ATTRIBUTS GENERAUX DES KARTS
    private static final float MIN_SPEED = 0.075f;
    private static final float FREINAGE_SPEED = 1.1f;
    private static final float BASE_FALL_SPEED = -0.5f;
    private static final float DELTA_FALL_SPEED = -0.2f;
    private static final float FALL_SPEED_LIMIT = -3.0f;
    private static final float FALL_SPEED_MULTIPLIER = 1.05f;
    private static final float COEFF_FROTTEMENT  = 0.85f;
    //ATTRIBUTS DU KART
    private final float MAX_SPEED = 0.8f;
    private final float DELTA_SPEED = MAX_SPEED + 0.1f;
    private final float ACCELERATION_BOOST = 0.04f;
    private final float BOOST = 0.5f;
    private final float MANIABILITE_COEEF = 3.0f;
    private final float PLAYER_POS_X = 0;
    private final float PLAYER_POS_Y = -0.5f;
    private final float PLAYER_POS_Z = 0;
    //ATTRIBUTS DU DRIFT
    private static final float DRIFT_ANGLE = 1.5f;
    public boolean isDrifting = false;
    public String driftingSens = "None";
    public float driftingTime = 0.0f;
    public float driftingTimeBoost = 0.0f;

    //ATTRIBUTS DE CONDUITE
    public boolean deltaOn = false;
    public int deltaAnimationState = 0;
    private float fallSpeed = BASE_FALL_SPEED;

    /////////////////
    // OBLIGATOIRE //
    /////////////////
    public Kart(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = false;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(SPEED, 0.0f);
    }
    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag p_20052_) {}
    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag p_20139_) {}

    @Override
    /**
     * "Associe" le "processus" d'animation au kart
     */
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    /**
     * Gère les animations en fonction du statut du kart
     * @param tAnimationState
     * @return
     * @param <T>
     */
    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState) {
        //VITESSE GENERALE DES ANIMATIONS (test)
        tAnimationState.getController().setAnimationSpeed(1.5f);

        //ANIMATION: DELTA PLANE ON
        if(this.deltaOn) {
            deltaAnimationState = 1;
            tAnimationState.getController().setAnimation(RawAnimation.begin()
                    .then("delta_on", Animation.LoopType.HOLD_ON_LAST_FRAME));
        //ANIMATION : MARCHE AVANT
        }else if(this.getSpeed()>MIN_SPEED) {
            if(deltaAnimationState==1){
                deltaAnimationState = 2;
                tAnimationState.getController().setAnimation(RawAnimation.begin()
                        .then("delta_off", Animation.LoopType.PLAY_ONCE)
                        .then("marche_avant", Animation.LoopType.LOOP)
                );
            }else if(deltaAnimationState==0){
                tAnimationState.getController().setAnimation(RawAnimation.begin()
                        .then("marche_avant", Animation.LoopType.LOOP));
            }
        //ANIMATION : MARCHE ARRIERE
        }else if(this.getSpeed()<(-MIN_SPEED)){
            deltaAnimationState = 0;
            tAnimationState.getController().setAnimation(RawAnimation.begin()
                    .then("marche_arriere", Animation.LoopType.LOOP));
        //ANIMATION : ARRET
        }else{
            deltaAnimationState = 0;
            tAnimationState.getController().setAnimation(RawAnimation.begin()
                    .then("arret", Animation.LoopType.HOLD_ON_LAST_FRAME));
        }

        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    ////////////
    // AJOUTS //
    ////////////
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
        return 1.0f;
    }
    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    /**
     * Position du joueur dans le kart
     */
    public void positionRider(@NotNull Entity player) {
        super.positionRider(player);
        double x = player.getX() + PLAYER_POS_X;
        double y = player.getY() + PLAYER_POS_Y;
        double z = player.getZ() + PLAYER_POS_Z;
        player.setPos(x, y, z);
    }

    @Override
    /**
     * Gestion des interraction avec le kart
     */
    public @NotNull InteractionResult interact(Player player, @NotNull InteractionHand hand) {
        //Si le joueur interragis avec le kart + sneak en même temps (???)
        if (player.isShiftKeyDown()){
            //Si le joueur est passagé du kart
            if (this.hasPassenger(player)){
                //Alors le joueur sort du kart
                player.stopRiding();
                return InteractionResult.SUCCESS;
            }
            else{
                return InteractionResult.PASS;
            }
        }
        //Si le joueur interragis avec le kart
        else {
            //Si le joueur n'est pas déjà dans le kart
            if(!this.hasPassenger(player)){
                //Alors le joueur monte dans le kart
                player.startRiding(this);
                return InteractionResult.SUCCESS;
            }
            else{
                return InteractionResult.PASS;
            }
        }
    }

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

    @Override
    /**
     * La fonction sert à gérer ce que fait le kart
     */
    public void tick() {
        super.tick();
        this.noPhysics = false;

        //ON FAIT RIEN SI PAS DE CONDUCTEUR DE TYPE JOUEUR
        if (this.getFirstPassenger() == null || !(this.getFirstPassenger() instanceof Player)){
            //RALENTIT SI LE KART AVANCE TOUJOURS
            if (this.getSpeed() != 0){
                this.slowDownKart();
            }
        //SINON AU BOLOUT
        }else{
            //ACTIVATION DU DELTA PLANE
            if(!this.deltaOn && !this.isOnGround() && keyJumpOk())
                deltaOn = true;
            else if(this.deltaOn && (keyJumpOk() || this.isOnGround()))
                deltaOn = false;
            this.previousKeyJump = keyDelta.isDown();

            //ON INITIE LA ROTATION QUE SI LE VEHICULE EST EN MOUVEMENT
            if(this.getSpeed()!=0){
                //LE KART DRIFT ET AVANCE ASSEZ VITE
                if(keyDrift.isDown() && !this.horizontalCollision && !this.deltaOn && this.getSpeed()>MAX_SPEED*0.25){
                    //INIT DU DRIFT
                    if(this.driftingTime==0){
                        if(keyLeft.isDown() && !keyRight.isDown()){
                            this.setDrifting("Left");
                        }else if (keyRight.isDown() && !keyLeft.isDown()){
                            this.setDrifting("Right");
                        }
                    }

                    if(this.isDrifting){
                        if(this.driftingTime>=3) spawnDriftParticules(ParticlesInit.DRIFT_PURPLE_PARTICLES.get());
                        else if(this.driftingTime>=2) spawnDriftParticules(ParticlesInit.DRIFT_ORANGE_PARTICLES.get());
                        else if(this.driftingTime>=1) spawnDriftParticules(ParticlesInit.DRIFT_BLUE_PARTICLES.get());
                    }

                    //DRIFT A GAUCHE
                    if (this.isDrifting && this.driftingSens.equals("Left")) {
                        //PLUS MAINTIENT GAUCHE
                        if(keyLeft.isDown() && !keyRight.isDown()){
                            if(this.driftingTime<3.0f) driftingTime += 0.06f;
                            this.setYRot(this.getYRot() - MANIABILITE_COEEF * DRIFT_ANGLE);
                        //PLUS MAINTIENT DROITE
                        }else if (keyRight.isDown() && !keyLeft.isDown()){
                            this.setYRot(this.getYRot() - MANIABILITE_COEEF * (DRIFT_ANGLE*0.5f));
                        //NE MAINTIENT RIEN
                        }else{
                            if(this.driftingTime<3.0f) driftingTime += 0.02f;
                            this.setYRot(this.getYRot() - MANIABILITE_COEEF * (DRIFT_ANGLE*0.75f));
                        }
                    //DRIFT A DROITE
                    }else if (this.isDrifting && this.driftingSens.equals("Right")) {
                        //PLUS MAINTIENT DROITE
                        if(keyRight.isDown() && !keyLeft.isDown()){
                            if(this.driftingTime<3.0f) driftingTime += 0.06f;
                            this.setYRot(this.getYRot() + MANIABILITE_COEEF * DRIFT_ANGLE);
                        //PLUS MAINTIENT GAUCHE
                        }else if (keyLeft.isDown() && !keyRight.isDown()){
                            this.setYRot(this.getYRot() + MANIABILITE_COEEF * (DRIFT_ANGLE*0.5f));
                        //NE MAINTIENT RIEN
                        }else {
                            if(this.driftingTime<3.0f) driftingTime += 0.02f;
                            this.setYRot(this.getYRot() + MANIABILITE_COEEF * (DRIFT_ANGLE * 0.75f));
                        }
                    }
                //LE KART NE DRIFT PAS/PLUS
                }else{
                    this.resetDrift();

                    //ROTATION GAUCHE
                    if (keyLeft.isDown() && !keyRight.isDown()) {
                        if (this.getSpeed() > 0) this.setYRot(this.getYRot() - MANIABILITE_COEEF);
                        else this.setYRot(this.getYRot() + MANIABILITE_COEEF);
                    }
                    //ROTATION DROITE
                    else if (keyRight.isDown() && !keyLeft.isDown()) {
                        if (this.getSpeed() > 0) this.setYRot(this.getYRot() + MANIABILITE_COEEF);
                        else this.setYRot(this.getYRot() - MANIABILITE_COEEF);
                    }
                }
            }else{
                this.resetDrift();
            }

            //VECTEUR DE MOUVEMENT : DELTA PLANE
            if(deltaOn) {
                this.fallSpeed = DELTA_FALL_SPEED;
                this.setSpeed(DELTA_SPEED);
                this.setKartMovement();
            //VECTEUR DE MOUVEMENT : MARCHE AVANT !!!
            }else if (keyUp.isDown()) {
                this.setSpeed(this.getSpeed() + ACCELERATION_BOOST);
                this.setKartMovement();
            //VECTEUR DE MOUVEMENT : MARCHE ARRIERE !!!
            }else if (keyDown.isDown() && this.getSpeed() >= -(MAX_SPEED/2)) {
                this.setSpeed(this.getSpeed() - ACCELERATION_BOOST);
                this.setKartMovement();
            //VECTEUR DE MOUVEMENT : RALENTISSEMENT AUTOMATIQUE
            }else {
                if(this.driftingTimeBoost<=0){
                    if(this.getSpeed()>0) this.slowDownKart();
                    else if(this.getSpeed()<0) this.slowDownKart();
                }else{
                    this.setSpeed(this.getSpeed());
                    this.setKartMovement();
                }
            }

            //ON BOUGE LA CAMERA DU CONDUCTEUR
            this.getFirstPassenger().setYRot(this.getYRot());
        }

        //VITESSE DE CHUTE
        if(this.isOnGround()){
            fallSpeed = BASE_FALL_SPEED;
        }else if(!this.isOnGround() && !this.deltaOn && fallSpeed>=FALL_SPEED_LIMIT)
            fallSpeed = fallSpeed * FALL_SPEED_MULTIPLIER;

        //INITIE LE MOUVEMENT
        this.move(MoverType.SELF, new Vec3(this.getDeltaMovement().x, fallSpeed, this.getDeltaMovement().z));
    }

    /**
     * Récupère la vitesse du kart
     * @return
     */
    public float getSpeed() {
        return this.entityData.get(SPEED);
    }

    /**
     * Modifie la vitesse du kart
     * @param new_speed = nouvelle vitesse
     */
    public void setSpeed(float new_speed) {
        this.entityData.set(SPEED, new_speed);
    }

    /**
     *Méthode pour update le vecteur de vitesse du kart
     */
    public void setKartMovement(){
        double angle = Math.toRadians(this.getYRot());

        float clamped_speed;
        //BOOST LE JOUEUR S'IL A FINI DE DRIFT
        if(this.driftingTimeBoost>0 && !this.isDrifting){
            this.spawnBoostParticules(ParticleTypes.FLAME);
            clamped_speed = MAX_SPEED + BOOST;
            driftingTimeBoost -= 0.1f;
        //SINON LE FAIT AVANCE NORMALEMENT
        }else{
            clamped_speed = Mth.clamp(this.getSpeed(), -MAX_SPEED/2, MAX_SPEED);
        }

        this.setSpeed(clamped_speed);

        double x = Math.sin(-angle) * clamped_speed;
        double z = Math.cos(-angle) * clamped_speed;
        Vec3 vec3 = new Vec3(x, 0, z);
        this.setDeltaMovement(vec3);
    }

    /**
     *Méthode pour ralentir le kart en updatant le vecteur de vitesse
     */
    public void slowDownKart(){
        double x,z;
        if(this.getSpeed()>-MIN_SPEED && this.getSpeed()<MIN_SPEED){
            //On arrête définitivement le véhicule
            this.setSpeed(0);
            x = 0;
            z = 0;
        }else {
            //On ralentit progressivement
            double angle = Math.toRadians(this.getYRot());

            this.setSpeed(this.getSpeed() / (FREINAGE_SPEED));

            float clamped_speed = Mth.clamp(this.getSpeed(), -MAX_SPEED/2, MAX_SPEED);
            this.setSpeed(clamped_speed);

            x = Math.sin(-angle) * clamped_speed;
            if(this.horizontalCollision) x *= COEFF_FROTTEMENT;
            z = Math.cos(-angle) * clamped_speed;
            if(this.horizontalCollision) z *= COEFF_FROTTEMENT;
        }
        Vec3 vec3 = new Vec3(x, 0, z);
        this.setDeltaMovement(vec3);
    }

    /**
     * est-ce que la touche "gauche" est enfoncé
     * @return
     */
    public boolean keyLeftDown(){
        return this.keyLeft.isDown();
    }

    /**
     * est-ce que la touche "droite" est enfoncé
     * @return
     */
    public boolean keyRightDown(){
        return this.keyRight.isDown();
    }

    /**
     * On détecte si le joueur vient de relacher la touche du deltaplane
     * @return
     */
    public boolean keyJumpOk(){
        return !keyDelta.isDown() && previousKeyJump;
    }

    /**
     * FONCTION POUR DEBUG : ENVOIE UN MESSAGE AU CONDUCTEUR DU VEHICULE
     * @param msg
     */
    public void sendConductorMessage(String msg){
        if (this.getFirstPassenger() == null || !(this.getFirstPassenger() instanceof Player player))
            return;

        if(this.getSpeed()!=0)
            player.sendSystemMessage(Component.literal(msg));
    }

    /**
     * Reset les attributs de drift du kart
     */
    public void resetDrift(){
        if(driftingTime!=0) this.driftingTimeBoost = (float) Math.floor(driftingTime);
        this.isDrifting = false;
        this.driftingTime = 0;
        this.driftingSens = "None";
    }

    /**
     * Set les attributs du drift en fonction du sens de rotation du kart
     * @param sens
     */
    public void setDrifting(String sens){
        this.isDrifting = true;
        this.driftingTime = 0.1f;
        this.driftingSens = sens;
    }

    /**
     * Fonction qui gère les particules lorsque le Kart reçoit un BOOST
     * @param particle
     */
    public void spawnBoostParticules(SimpleParticleType particle) {
        float yaw = this.getYRot();
        double motionX = -Math.sin(Math.toRadians(yaw));
        double motionZ = Math.cos(Math.toRadians(yaw));
        spawnParticules(particle, 0.25 * Math.cos(Math.toRadians(yaw)), 0, 0.25 * Math.sin(Math.toRadians(yaw)),
                motionX*0.5f, 0.25f, motionZ*0.5f);
    }

    /**
     * Fonction qui gère les particules du drift
     * @param particle
     */
    public void spawnDriftParticules(SimpleParticleType particle){
        float yaw = this.getYRot();
        spawnParticules(particle, 1 * Math.cos(Math.toRadians(yaw)), 0, 1 * Math.sin(Math.toRadians(yaw)),
                0, 0, 0);
    }

    /**
     * Fonction qui gère le spawn des particules en général
     * @param particle
     * @param x1 = ajustement en x
     * @param y1 = ajustement en y
     * @param z1 = ajustement en z
     * @param x2 = vecteur de direction des particules en x
     * @param y2 = vecteur de direction des particules en y
     * @param z2 = vecteur de direction des particules en z
     */
    public void spawnParticules(SimpleParticleType particle, double x1, double y1, double z1, double x2, double y2, double z2){
        Minecraft minecraft = Minecraft.getInstance();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();

        //BOOSTER GAUCHE
        minecraft.particleEngine.createParticle(particle, x - x1, y - y1, z - z1,
                x2, y2, z2);
        //BOOSTER DROIT
        minecraft.particleEngine.createParticle(particle, x + x1, y + y1, z + z1,
                x2, y2, z2);
    }

}
