package me.jesuismister.cubicracers.entity.custom;

import me.jesuismister.cubicracers.util.KeyBinds;
import net.minecraft.client.KeyMapping;
import net.minecraft.nbt.CompoundTag;
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
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;

public class Kart extends Entity implements GeoEntity {
    private static final EntityDataAccessor<Float> SPEED = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.FLOAT);
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    //KEYS POUR LE KART
    private KeyMapping keyUp = KeyBinds.KART_UP_KEY;
    private KeyMapping keyDown = KeyBinds.KART_DOWN_KEY;
    private KeyMapping keyLeft = KeyBinds.KART_LEFT_KEY;
    private KeyMapping keyRight = KeyBinds.KART_RIGHT_KEY;
    private KeyMapping keyDelta = KeyBinds.KART_DELTA_KEY;
    private KeyMapping keyDrift = KeyBinds.KART_DRIFT_KEY;
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
    private final float MANIABILITE_COEEF = 3.0f;
    private final float PLAYER_POS_X = 0;
    private final float PLAYER_POS_Y = -0.5f;
    private final float PLAYER_POS_Z = 0;
    public final float DRIFT_ANGLE = 1.5f;
    //ATTRIBUTS DE CONDUITE
    public boolean deltaOn = false;
    public boolean isDrifting = false;
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

    @Override //TO DO ?????????????
    protected void readAdditionalSaveData(CompoundTag p_20052_) {}

    @Override //TO DO ?????????????
    protected void addAdditionalSaveData(CompoundTag p_20139_) {}

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
    /**
     * Gravite on
     */
    public boolean isNoGravity() {
        return false;
    }

        @Override
    /**
     * Jusqu'à quel hauteur il peut monter sans sauter (ici 1 bloc de haut)
     */
    public float getStepHeight() {
        return 1.0f;
    }

    @Override
    /**
     * Peut-on la pousser (les collisions en générale)
     */
    public boolean isPushable() {
        return false;
    }

    @Override
    /**
     * Position du joueur dans le kart
     */
    public void positionRider(Entity player) {
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
    public InteractionResult interact(Player player, InteractionHand hand) {
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
    public boolean startRiding(Entity rider) {
        Player player = (Player) rider;
        return super.startRiding(player);
    }

    @Override
    /**
     * Le conducteur peut interragir (??? - tester de voir à false ce que sa fait)
     */
    public boolean canRiderInteract() {
        return true;
    }

    @Override
    /**
     * Peut monter le kart
     */
    protected boolean canRide(Entity rider) {
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
            //TEMPORAIRE : MESSAGE STATS
            Player player = (Player) this.getFirstPassenger();
            //if(this.getSpeed()!=0)
            //player.sendSystemMessage(Component.literal(
            //        "speed = " + ((float)Math.round(this.getSpeed()*1000))/1000.0f + "/" + this.MAX_SPEED +
            //                " - delta " + this.deltaOn + ")"));

            //ACTIVATION DU DELTA PLANE
            if(this.deltaOn==false && !this.isOnGround() && keyJumpOk())
                deltaOn = true;
            else if(this.deltaOn==true && (keyJumpOk() || this.isOnGround()))
                deltaOn = false;
            this.previousKeyJump = keyDelta.isDown();

            //ON INITIE LA ROTATION QUE SI LE VEHICULE EST EN MOUVEMENT
            if(this.getSpeed()!=0){
                //LE KART DRIFT ET AVANCE
                if(keyDrift.isDown() && this.getSpeed()>0){
                    //DRIFT A GAUCHE
                    if (keyLeft.isDown() && !keyRight.isDown()) {
                        this.isDrifting = true;
                        this.setYRot(this.getYRot() - MANIABILITE_COEEF * DRIFT_ANGLE);
                    //DRIFT A DROITE
                    }else if (keyRight.isDown() && !keyLeft.isDown()) {
                        this.isDrifting = true;
                        this.setYRot(this.getYRot() + MANIABILITE_COEEF * DRIFT_ANGLE);
                    }
                //LE KART NE DRIFT PAS
                }else{
                    this.isDrifting = false;
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
                this.isDrifting = false;
            }

            //VECTEUR DE MOUVEMENT : DELTA PLANE
            if(deltaOn==true) {
                this.fallSpeed = DELTA_FALL_SPEED;
                this.setSpeed(DELTA_SPEED);
                this.setKartMovement();
            //VECTEUR DE MOUVEMENT : MARCHE AVANT !!!
            }else if (keyUp.isDown() && this.getSpeed() <= MAX_SPEED) {
                this.setSpeed(this.getSpeed() + ACCELERATION_BOOST);
                this.setKartMovement();
            //VECTEUR DE MOUVEMENT : MARCHE ARRIERE !!!
            }else if (keyDown.isDown() && this.getSpeed() >= -(MAX_SPEED/2)) {
                this.setSpeed((float) (this.getSpeed() - ACCELERATION_BOOST));
                this.setKartMovement();
            //VECTEUR DE MOUVEMENT : RALENTISSEMENT AUTOMATIQUE
            }else {
                if(this.getSpeed()>0) this.slowDownKart();
                else if(this.getSpeed()<0) this.slowDownKart();
            }

            //ON BOUGE LA CAMERA DU CONDUCTEUR
            player.setYRot(this.getYRot());
        }

        //VITESSE DE CHUTE
        if(this.isOnGround()){
            fallSpeed = BASE_FALL_SPEED;
        }else if(!this.isOnGround() && this.deltaOn==false && fallSpeed>=FALL_SPEED_LIMIT)
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

        //La méthode Math.clamp permet de restreindre une valeur à un intervalle spécifié.
        float clamped_speed = Mth.clamp(this.getSpeed(), -MAX_SPEED/2, MAX_SPEED);
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
     * Mets en place le dérapage du kart
     */
    /*public void drift(){
        boolean passengerCheck = this.getFirstPassenger() instanceof Player;
        this.setDrifting(true);

        if(keyJump.isDown() && keyLeft.isDown() && passengerCheck && this.isDrifting()){
            double r = 1.0f;
            double angle = Math.toRadians(this.getYRot());
            double x = (this.xo + r * Mth.cos((float) -angle));
            double z = (this.zo + r * Mth.cos((float) -angle));
            this.setSpeed(this.getSpeed());
            Vec3 circle = new Vec3(x, 0, z);
            this.setDeltaMovement(circle);
        }
        if(keyJump.isDown() && keyRight.isDown() && passengerCheck && this.isDrifting()){
            double r = 1.0f;
            double angle = Math.toRadians(this.getYRot());
            double x = (this.xo + r * Mth.cos((float) -angle));
            double z = (this.zo + r * Mth.cos((float) -angle));
            this.setSpeed(this.getSpeed());
            Vec3 circle = new Vec3(x, 0, z);
            this.setDeltaMovement(circle);
        }
    }*/

    public boolean keyLeftDown(){
        return this.keyLeft.isDown();
    }

    public boolean keyRightDown(){
        return this.keyRight.isDown();
    }

    /**
     * On détecte si le joueur vient de relacher la touche du deltaplane
     * @return
     */
    public boolean keyJumpOk(){
        return !keyDelta.isDown() && previousKeyJump==true;
    }
}
