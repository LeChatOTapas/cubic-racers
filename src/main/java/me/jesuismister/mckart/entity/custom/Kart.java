package me.jesuismister.mckart.entity.custom;

import me.jesuismister.mckart.util.KeyBinds;
import net.minecraft.client.KeyMapping;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.WaterAnimal;
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
    private static KeyMapping keyUp = KeyBinds.KART_UP_KEY;
    private static KeyMapping keyDown = KeyBinds.KART_DOWN_KEY;
    private static KeyMapping keyLeft = KeyBinds.KART_LEFT_KEY;
    private static KeyMapping keyRight = KeyBinds.KART_RIGHT_KEY;
    private static KeyMapping keyJump = KeyBinds.KART_JUMP_KEY;
    //ATTRIBUTS DE CONDUITE
    private boolean isDrifting = false;
    //ATTRIBUTS GENERAUX DES KARTS
    private static final float MIN_SPEED = 0.075f;
    private static final float FREINAGE_SPEED = 1.04f;
    //ATTRIBUTS DU KART
    private final float MAX_SPEED = 1.0f;
    private final float ACCELERATION_BOOST = 0.05f;



    /////////////////
    // OBLIGATOIRE //
    /////////////////
    public Kart(EntityType<?> entityType, Level level) {
        super(entityType, level);
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

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState) {
        if(this.getSpeed()>MIN_SPEED) {
            //MARCHE AVANT GAUCHE
            if (keyLeft.isDown() && !keyRight.isDown()) {

            //MARCHE AVANT DROITE
            } else if (!keyLeft.isDown() && keyRight.isDown()) {

            //MARCHE AVANT SIMPLE
            } else {
                tAnimationState.getController().setAnimation(RawAnimation.begin().then("roues_qui_tournent", Animation.LoopType.LOOP));
            }
        }else if(this.getSpeed()<-MIN_SPEED){
            //MARCHE ARRIERE GAUCHE
            if (keyLeft.isDown() && !keyRight.isDown()) {

            //MARCHE ARRIERE DROITE
            } else if (!keyLeft.isDown() && keyRight.isDown()) {

            //MARCHE ARRIERE SIMPLE
            } else {

            }
        //ARRET
        }else{
            tAnimationState.getController().setAnimation(RawAnimation.begin().then("roues_qui_tournent_pas", Animation.LoopType.LOOP));
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
        player.setPos(x, y + 0.8, z);
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
        //this.getStepHeight();

        //On return si le conducteur n'est pas un joueur
        if(this.getFirstPassenger() ==null || !(this.getFirstPassenger() instanceof Player))
            return;

        Player player = (Player) this.getFirstPassenger();
        if(this.getSpeed()!=0)
            player.sendSystemMessage(Component.literal(
                "VROOM (speed = " + this.getSpeed() + "/" + MAX_SPEED));

        //Fait tourner le kart en fonction des touches maintenus par le conducteur
        this.rotateKart();
        //Fait sauter le kart si le conducteur lache la touche de saut
        this.hop();

        if(this.getSpeed()!=0 && this.getPassengers() == null){
            if(this.getSpeed()>0) this.slowDownKart(null);
            else this.slowDownKart(null);
            return;
        }

        //ACCELERE !!!!!
        if (keyUp.isDown() && this.getSpeed() <= MAX_SPEED) {
            player.sendSystemMessage(Component.literal("=> ACCELERE"));
            this.setSpeed(this.getSpeed() + ACCELERATION_BOOST);
            this.setKartMovement();
        //MARCHE ARRIERE !!!
        }else if (keyDown.isDown() && this.getSpeed() >= -(MAX_SPEED/2)) {
            player.sendSystemMessage(Component.literal("=> MARCHE ARRIERE"));
            this.setSpeed((float) (this.getSpeed() - ACCELERATION_BOOST));
            this.setKartMovement();
        //RALENTISSEMENT AUTOMATIQUE
        }else {
            if(this.getSpeed()>0) this.slowDownKart(player);
            else if(this.getSpeed()<0) this.slowDownKart(player);
        }

        ////////////////////

        if(this.isDrifting() == true){
            this.drift();
        }


        this.move(MoverType.SELF, this.getDeltaMovement());

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


        /*
        if (!this.isOnGround()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.08D, 0.0D));
        }

        this.getStepHeight();
        */
    }

    /**
     * Gère la rotation du Kart
     */
    public void rotateKart() {
        if(!this.isDrifting && !keyJump.isDown()) {
            if (keyLeft.isDown()) {
                this.setRotation(-1.0f);
            } else if (keyRight.isDown()) {
                this.setRotation(1.0f);
            }
        }
    }

    /**
     * Applique la rotation
     * @param angle = angle a appliqué
     */
    public void setRotation(float angle) {
        float current_angle = this.getYRot();
        float new_angle = current_angle + angle;
        this.setYRot(new_angle);
    }

    /**
     * ?????? Un vecteur de saut, genre le kart saute comme dans le jeu avec la gachette de drift ?
     */
    public void hop(){
        if(keyJump.consumeClick() && (this.isOnGround() || this.isUnderWater())){
            Vec3 jump = new Vec3(0, 1.0D, 0);
            this.setDeltaMovement(this.getDeltaMovement().add(jump));
        }
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
     *Méthode pour accélèrer le véhicule
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
     *Méthode pour ralentir le véhicule
     */
    public void slowDownKart(Player player){
        double x,z;

        if(this.getSpeed()>-MIN_SPEED && this.getSpeed()<MIN_SPEED){
            //On arrête définitivement le véhicule
            this.setSpeed(0);
            x = 0;
            z = 0;

            if(player!=null)
                player.sendSystemMessage(Component.literal("=> STOP"));
        }else {
            //On ralentit progressivement
            double angle = Math.toRadians(this.getYRot());
            this.setSpeed(this.getSpeed() / (FREINAGE_SPEED));

            float clamped_speed = Mth.clamp(this.getSpeed(), -MAX_SPEED/2, MAX_SPEED);
            this.setSpeed(clamped_speed);

            x = Math.sin(-angle) * clamped_speed;
            z = Math.cos(-angle) * clamped_speed;
        }
        Vec3 vec3 = new Vec3(x, 0, z);
        this.setDeltaMovement(vec3);
    }

    /**
     * Retourne vrai si le kart est en train de déraper
     * @return
     */
    public boolean isDrifting() {
        return this.isDrifting;
    }

    /**
     * Modifie l'état du dérapage
     * @param drifting
     */
    public void setDrifting(boolean drifting) {
        this.isDrifting = drifting;
    }

    /**
     * Mets en place le dérapage du kart
     */
    public void drift(){
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
    }
}
