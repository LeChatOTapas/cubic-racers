package me.jesuismister.mckart.entity.custom;

import me.jesuismister.mckart.MCKart;
import me.jesuismister.mckart.util.KeyBinds;
import net.minecraft.client.KeyMapping;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
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

import java.util.List;

public class Kart extends Entity implements GeoEntity {
    private static final EntityDataAccessor<Float> SPEED =
            SynchedEntityData.defineId(Kart.class, EntityDataSerializers.FLOAT);
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    //KEYS POUR LE KART
    private KeyMapping keyUp = KeyBinds.KART_UP_KEY;
    private KeyMapping keyDown = KeyBinds.KART_DOWN_KEY;
    private KeyMapping keyLeft = KeyBinds.KART_LEFT_KEY;
    private KeyMapping keyRight = KeyBinds.KART_RIGHT_KEY;
    private KeyMapping keyJump = KeyBinds.KART_JUMP_KEY;
    //ATTRIBUTS DE CONDUITE
    private boolean isDrifting = false;
    private boolean deltaOn = false;
    private boolean previousKeyJump = false;
    private float fallSpeed = -0.5f;
    //ATTRIBUTS GENERAUX DES KARTS
    private static final float MIN_SPEED = 0.075f;
    private static final float FREINAGE_SPEED = 1.04f;
    private static final float FALL_SPEED_LIMIT = -4.0f;
    private static final float COEFF_FROTTEMENT  = 0.85f;
    //ATTRIBUTS DU KART
    private final float MAX_SPEED = 1.0f;
    private final float ACCELERATION_BOOST = 0.05f;
    private final float MANIABILITE_COEEF = 8.0f;
    private final float PLAYER_POS = -0.5f;


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
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    public ResourceLocation getAnimationResource(Kart animatable) {
        return new ResourceLocation(MCKart.MODID, "animations/trash_kart.animation.json");
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState) {
        //ANIMATION: DELTA PLANE ON
        if(this.deltaOn) {
            tAnimationState.getController().setAnimation(RawAnimation.begin().then("delta_on", Animation.LoopType.HOLD_ON_LAST_FRAME));
        //ANIMATION : MARCHE AVANT
        }else if(this.getSpeed()>MIN_SPEED) {
            tAnimationState.getController().setAnimation(RawAnimation.begin().then("marche_avant", Animation.LoopType.LOOP));
        //ANIMATION : MARCHE ARRIERE
        }else if(this.getSpeed()<(-MIN_SPEED)){
            tAnimationState.getController().setAnimation(RawAnimation.begin().then("marche_arriere", Animation.LoopType.LOOP));
        //ANIMATION : ARRET
        }else{
            tAnimationState.getController().setAnimation(RawAnimation.begin().then("arret", Animation.LoopType.LOOP));
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
    /**
     * Est-ce qu'on peut le ramasser (en cliquant dessus ?)
     */
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
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();
        player.setPos(x, y + PLAYER_POS, z);
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
        //this.getStepHeight();

        //ON FAIT RIEN SI PAS DE CONDUCTEUR DE TYPE JOUEUR
        if (this.getFirstPassenger() == null || !(this.getFirstPassenger() instanceof Player)){
            //RALENTIT SI LE KART AVANCE TOUJOURS
            if (this.getSpeed() != 0){
                this.slowDownKart();
                this.move(MoverType.SELF, this.getDeltaMovement());
            }
            return;
        }

        //TEMPORAIRE : MESSAGE STATS
        Player player = (Player) this.getFirstPassenger();
        //if(this.getSpeed()!=0)
        //    player.sendSystemMessage(Component.literal(
        //        "VROOM (onGround = " + this.isOnGround() + " / noGravity = " + this.isNoGravity() + " / VertColl = " +
        //                this.verticalCollision + " - HorizColl = " + this.horizontalCollision + ")"));

        //ON INITIE LA ROTATION QUE SI LE VEHICULE EST EN MOUVEMENT
        if(this.getSpeed()!=0){
            //ROTATION GAUCHE
            if (keyLeft.isDown() && !keyRight.isDown())
                this.setYRot(this.getYRot()-MANIABILITE_COEEF);
            //ROTATION DROITE
            else if (keyRight.isDown() && !keyLeft.isDown())
                this.setYRot(this.getYRot()+MANIABILITE_COEEF);
        }

        //VECTEUR DE MOUVEMENT : ACCELERE !!!!!
        if (keyUp.isDown() && this.getSpeed() <= MAX_SPEED) {
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

        if(this.deltaOn==false && keyJumpOk()){
            deltaOn = true;
            this.fallSpeed = -0.2f;
        } else if(this.deltaOn==true && keyJumpOk()) deltaOn = false;
        this.previousKeyJump = keyJump.isDown();

        if(this.isOnGround() && this.deltaOn==false) fallSpeed = -0.5f;
        else if(this.deltaOn==false && fallSpeed<=FALL_SPEED_LIMIT) fallSpeed = fallSpeed * 1.1f;

        //INITIE LE MOUVEMENT
        this.move(MoverType.SELF, new Vec3(this.getDeltaMovement().x, fallSpeed, this.getDeltaMovement().z));

        ////////////////////
/*
        if(this.isDrifting() == true){
            this.drift();
        }

        List<Entity> list = this.level.getEntities(this, this.getBoundingBox().inflate((double) 0.2F,
                (double) -0.01F, (double) 0.2F), EntitySelector.pushableBy(this));
        if (!list.isEmpty()) {
            boolean flag = !this.level.isClientSide && !(this.getControllingPassenger() instanceof Player);

            for (int j = 0; j < list.size(); ++j) {
                Entity entity = list.get(j);
                if (!entity.hasPassenger(this)) {
                    if (flag && !entity.isPassenger() && entity instanceof LivingEntity && !(entity instanceof WaterAnimal)
                            && !(entity instanceof Player)) {
                        entity.startRiding(this);
                    } else {
                        this.push(entity);
                    }
                }
            }
        }
*/

        /*
        if (!this.isOnGround()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.08D, 0.0D));
        }

        this.getStepHeight();
        */
    }

    /**
     * ?????? Un vecteur de saut, genre le kart saute comme dans le jeu avec la gachette de drift ?
     */
    /*public void hop(){
        if(keyJump.consumeClick() && (this.isOnGround() || this.isUnderWater())){
            Vec3 jump = new Vec3(0, 1.0D, 0);
            this.setDeltaMovement(this.getDeltaMovement().add(jump));
        }
    }*/

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
     * Retourne vrai si le kart est en train de déraper
     * @return
     */
    /*public boolean isDrifting() {
        return this.isDrifting;
    }*/

    /**
     * Modifie l'état du dérapage
     * @param drifting
     */
    /*public void setDrifting(boolean drifting) {
        this.isDrifting = drifting;
    }*/

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

    public boolean keyJumpOk(){
        return !keyJump.isDown() && previousKeyJump==true;
    }

    public boolean getDeltaOn(){
        return this.deltaOn;
    }
}
