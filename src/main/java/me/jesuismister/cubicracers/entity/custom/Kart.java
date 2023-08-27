package me.jesuismister.cubicracers.entity.custom;

import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.block.BoosterBlock;
import me.jesuismister.cubicracers.block.KartController;
import me.jesuismister.cubicracers.block.RoadBlock;
import me.jesuismister.cubicracers.init.BlockInit;
import me.jesuismister.cubicracers.init.KartInit;
import me.jesuismister.cubicracers.init.SoundsInit;
import me.jesuismister.cubicracers.network.Network;
import me.jesuismister.cubicracers.network.message.KartPositionMessage;
import me.jesuismister.cubicracers.network.message.itemsKart.use.*;
import me.jesuismister.cubicracers.sounds.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
    public static float HITBOX_X;
    public static float HITBOX_Y;
    //CACHE
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
    //OTHERS
    public float speedToShow = 0;
    public String stunMotif = "None";

    public Kart(EntityType<?> entityType, Level level, String texture, String model, String animation, float maxSpeed,
                float accelerationBoost, float boost, float maniabiliteCoeff, float playerPosY, float hitboxX, float hitboxY) {
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
        HITBOX_X = hitboxX;
        HITBOX_Y = hitboxY;
        setInvulnerable(false);
        //useItem(); //je sais pas pourquoi mais si je fais pas ça, ça veut pas mettre bien l'item à vide
    }

    public Kart(Level level, double x, double y, double z, String name, String texture, String model, String animation,
                float maxSpeed, float accelerationBoost, float boost, float maniabiliteCoeff, float playerPosY, float hitboxX, float hitboxY) {
        this(KartInit.KARTS.get(name).get(), level, texture, model, animation, maxSpeed, accelerationBoost, boost,
                maniabiliteCoeff, playerPosY, hitboxX, hitboxY);
        setPos(x, y, z);
        xo = x;
        yo = y;
        zo = z;
        setKartItem("Banana");
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
        updateSounds();

        // ON MET LES BINDS A FALSE SI PAS DE JOUEUR DANS LE VEHICULE
        Player player = (Player) getFirstPassenger();
        if (player == null) {
            resetBindValue();
        }

        if(isOnKartController()){
            setSpeed(0);
            return;
        }

        collision(); // GERE LES COLLISIONS DU KART
        isStun(); // ON VOIT SI LE KART EST STUN
        if (!getCanMove()) applyStun(); // SI LE KART EST STUN, ON APPLIQUE LA PROCEDURE DE STUN

        // ON UPDATE LES TIMERS
        if (getTimeStar() > 0) setTimeStar(getTimeStar() - 0.1f);
        if (getDriftTimeBoost() > 0) setDriftTimeBoost(getDriftTimeBoost() - 0.1f);
        if (getTimeBoost() > 0) setTimeBoost(getTimeBoost() - 0.1f);

        // UTILISE L'ITEM SI LE JOUEUR LE VEUT
        if (getCanMove() && getIsPressingKeyItem())
            useItem();

        deltaplane(player); // ACTIVE LE DELTA PLANE
        rotateOrDrift(); // CALCUL LA ROTATION DU VEHCIULE

        if (getCanMove()) setVectorMovment(); // SI PAS STUN, CALCUL LE VECTEUR DE VITESSE

        moveCamera(player); // BOUGE LA CAMERA EN CONSEQUENCE DU MOUVEMENT

        //ENVOIE LES POSITIONS AU SERVEUR
        if (level().isClientSide() && player!=null && player.getVehicle()!=null && player.getVehicle().equals(this)) {
            Network.CHANNEL.sendToServer(new KartPositionMessage(getX(), getY(), getZ()));
        }

        move(MoverType.SELF, new Vec3(getDeltaMovement().x, calculateFallSpeed(), getDeltaMovement().z)); //ON APPLIQUE LE VECTEUR DE VITESSE
    }

    private void resetBindValue() {
        setIsPressingKeyAccelerate(false);
        setIsPressingKeyDeccelerate(false);

        setIsPressingKeyFoward(false);
        setIsPressingKeyBackward(false);
        setIsPressingKeyLeft(false);
        setIsPressingKeyRight(false);

        setIsPressingKeyDelta(false);
        setPreviousPressingKeyDelta(false);
        setIsPressingKeyDrift(false);
        setIsPressingKeyItem(false);
    }

    /**
     * Calcul la vitesse de chute du kart
     *
     * @return
     */
    private float calculateFallSpeed() {
        //VITESSE DE CHUTE : EN DELTA
        if (getDeltaOn()) {
            return REDUCED_FALL_SPEED;
        }
        //VITESSE DE CHUTE : DANS l4EAU
        else if(isInWater()){
            return REDUCED_FALL_SPEED*2;
        }
        //VITESSE DE CHUTE : EN CHUTE LIBRE
        else if (!onGround() && !getDeltaOn()) {
            return BASE_FALL_SPEED;
        }
        //VITESSE DE CHUTE : SUR TERRE
        return 0;
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
        if (getTimeBoost() > 0 || (getIsInvinsible() && !getIsPressingKeyDeccelerate())) {
            setSpeed(MAX_SPEED);
        }
        //VECTEUR DE MOUVEMENT : DELTA PLANE
        else if (getDeltaOn()) {
            setSpeed(DELTA_SPEED);
        }
        //VECTEUR DE MOUVEMENT : MARCHE AVANT !!!
        else if ((!horizontalCollision || getSpeed() <= MAX_SPEED / 10) && getIsPressingKeyAccelerate()) {
            setSpeed(Mth.clamp(getSpeed() + ACCELERATION_BOOST, -MAX_SPEED / 2, MAX_SPEED));
        }
        //VECTEUR DE MOUVEMENT : MARCHE ARRIERE !!!
        else if (getIsPressingKeyDeccelerate()) {
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
        resetBindValue();

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
        if (getKartItem().equals("Banana")) {
            if (level().isClientSide())
                Network.CHANNEL.sendToServer(new BananaUseMessage(getIsPressingKeyForward()));
            if(!getIsPressingKeyForward()){
                if(getFirstPassenger()!=null && getFirstPassenger() instanceof Player player)
                    SoundsInit.playSound(SoundsInit.SPAWN_ITEM_BELOW.get(), level(), new BlockPos((int)getX(), (int)getY(), (int)getZ()), player, SoundSource.RECORDS, 1f);
            }else{
                if(getFirstPassenger()!=null && getFirstPassenger() instanceof Player player)
                    SoundsInit.playSound(SoundsInit.THROWING_ITEM.get(), level(), new BlockPos((int)getX(), (int)getY(), (int)getZ()), player, SoundSource.RECORDS, 1f);
            }
        } else if (getKartItem().equals("Mushroom")) {
            setTimeBoost(5.f);
            setSpeed(MAX_SPEED + BOOST);
        } else if (getKartItem().equals("Star")) {
            setTimeStar(20f);
            setStarSpeedBoost(1.5f);
            setIsInvinsible(true);
            setSpeed(MAX_SPEED * getStarSpeedBoost());
        } else if (getKartItem().equals("Thunder")) {
            if (level().isClientSide()) Network.CHANNEL.sendToServer(new ThunderUseMessage());
        } else if (getKartItem().equals("Klaxon")) {
            if (level().isClientSide()) Network.CHANNEL.sendToServer(new KlaxonUseMessage(getX(), getY(), getZ()));
        } else if (getKartItem().equals("Bob_omb")) {
            if (level().isClientSide())
                Network.CHANNEL.sendToServer(new BobOmbUseMessage(getIsPressingKeyForward()));
            if(!getIsPressingKeyForward()){
                if(getFirstPassenger()!=null && getFirstPassenger() instanceof Player player)
                    SoundsInit.playSound(SoundsInit.SPAWN_ITEM_BELOW.get(), level(), new BlockPos((int)getX(), (int)getY(), (int)getZ()), player, SoundSource.RECORDS, 1f);
            }else{
                if(getFirstPassenger()!=null && getFirstPassenger() instanceof Player player)
                    SoundsInit.playSound(SoundsInit.THROWING_ITEM.get(), level(), new BlockPos((int)getX(), (int)getY(), (int)getZ()), player, SoundSource.RECORDS, 1f);
            }
        } else if (getKartItem().equals("Fake_box")) {
            if (level().isClientSide())
                Network.CHANNEL.sendToServer(new FakeBoxUseMessage(getIsPressingKeyForward()));
            if(!getIsPressingKeyForward()){
                if(getFirstPassenger()!=null && getFirstPassenger() instanceof Player player)
                    SoundsInit.playSound(SoundsInit.SPAWN_ITEM_BELOW.get(), level(), new BlockPos((int)getX(), (int)getY(), (int)getZ()), player, SoundSource.RECORDS, 1f);
            }else{
                if(getFirstPassenger()!=null && getFirstPassenger() instanceof Player player)
                    SoundsInit.playSound(SoundsInit.THROWING_ITEM.get(), level(), new BlockPos((int)getX(), (int)getY(), (int)getZ()), player, SoundSource.RECORDS, 1f);
            }
        } else if (getKartItem().equals("Green_shell")) {
            if (level().isClientSide())
                Network.CHANNEL.sendToServer(new GreenShellUseMessage(getIsPressingKeyBackward()));
        }
        setKartItem("None");
    }

    /**
     * Applique la rotation du kart en fonction de s'il tourne, drift, ou rien
     */
    private void rotateOrDrift() {
        //ON INITIE LA ROTATION QUE SI LE VEHICULE EST EN MOUVEMENT
        if (getSpeed() != 0 && getCanMove()) {
            //SI LE JOUEUR APPUIE SUR LA TOUCHE DE DRIFT, QUE LE KART AVANCE ASSEZ VITE ET AUTRES CONDITIONS
            if ((isOnRoadBlock() || getTimeBoost()>0 || getDriftTimeBoost()>0 || getIsInvinsible()) && getIsPressingKeyDrift() && !horizontalCollision && !getDeltaOn() && getSpeed() > MAX_SPEED * 0.25) {
                //INIT DU DRIFT SI PAS ENCORE FAIT
                if (getDriftingTime() == 0) {
                    if (getIsPressingKeyLeft() && !getIsPressingKeyRight()) {
                        setDrifting("Left");
                    } else if (getIsPressingKeyRight() && !getIsPressingKeyLeft()) {
                        setDrifting("Right");
                    }
                }

                //DRIFT INITIAL : DRIFT A GAUCHE
                if (getIsDrifting() && getDriftingSens().equals("Left")) {
                    //LE JOUEUR MAINTIENT LA TOUCHE GAUCHE
                    if (getIsPressingKeyLeft() && !getIsPressingKeyRight()) {
                        if (getDriftingTime() < 3.0f)
                            setDriftingTime(getDriftingTime() + 0.06f);
                        setYRot(getYRot() - MANIABILITE_COEEF * DRIFT_ANGLE);
                    }
                    //LE JOUEUR MAINTIENT LA TOUCHE DROITE
                    else if (getIsPressingKeyRight() && !getIsPressingKeyLeft()) {
                        setYRot(getYRot() - MANIABILITE_COEEF * (DRIFT_ANGLE * 0.3f));
                    }
                    //LE JOUEUR NE MAINTIENT RIEN
                    else {
                        if (getDriftingTime() < 3.0f)
                            setDriftingTime(getDriftingTime() + 0.02f);
                        setYRot(getYRot() - MANIABILITE_COEEF * (DRIFT_ANGLE * 0.75f));
                    }
                }
                //DRIFT INITIAL : DRIFT A DROITE
                else if (getIsDrifting() && getDriftingSens().equals("Right")) {
                    //LE JOUEUR MAINTIENT LA TOUCHE GAUCHE
                    if (getIsPressingKeyAccelerate() && getIsPressingKeyRight() && !getIsPressingKeyLeft()) {
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
                        if (getDriftingTime() < 3.0f)
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

    private boolean isValidBlockCollision(){
        int blockX = (int) Math.floor(getX());
        int blockY = (int) Math.floor(getY());
        int blockZ = (int) Math.floor(getZ());

        if(this.getCommandSenderWorld().getBlockState(new BlockPos(blockX, blockY, blockZ)).is(Blocks.AIR)){
            return false;
        }else if(this.getCommandSenderWorld().getBlockState(new BlockPos(blockX, blockY, blockZ)).is(Blocks.WATER)){
            return false;
        }else if(this.getCommandSenderWorld().getBlockState(new BlockPos(blockX, blockY, blockZ)).is(Blocks.LAVA)){
            return false;
        }else if(this.getCommandSenderWorld().getBlockState(new BlockPos(blockX, blockY, blockZ)).getBlock().getName().toString().contains("slab")){
            return false;
        }
        return true;
    }

    private boolean isOnKartController(){
        int blockX = (int) Math.floor(getX());
        int blockY = (int) Math.floor(getY()) - 1;
        int blockZ = (int) Math.floor(getZ());
        BlockState blockState = this.getCommandSenderWorld().getBlockState(new BlockPos(blockX, blockY, blockZ));

        if(blockState.is(BlockInit.KART_CONTROLLER.get())){
            if(blockState.getValue(KartController.LIT)){
                setSpeed(0);
                if (blockState.getValue(KartController.FACING).getOpposite().equals(Direction.NORTH)) {
                    setYRot(180);
                } else if (blockState.getValue(KartController.FACING).getOpposite().equals(Direction.WEST)) {
                    setYRot(90);
                } else if (blockState.getValue(KartController.FACING).getOpposite().equals(Direction.EAST)) {
                    setYRot(-90);
                } else if (blockState.getValue(KartController.FACING).getOpposite().equals(Direction.SOUTH)) {
                    setYRot(0);
                }
                setPos(blockX + 0.5d, blockY + 1d, blockZ + 0.5d);
                return true;
            }
        }
        return false;
    }

    public boolean isOnRoadBlock(){
        int blockX = (int) Math.floor(getX());
        int blockY = (int) Math.floor(getY()) - 1;
        int blockZ = (int) Math.floor(getZ());

        if(this.getCommandSenderWorld().getBlockState(new BlockPos(blockX, blockY, blockZ)).is(Blocks.AIR)){
            return true;
        } else if(this.getCommandSenderWorld().getBlockState(new BlockPos(blockX, blockY, blockZ)).is(Blocks.WATER)){
            return true;
        } else if(this.getCommandSenderWorld().getBlockState(new BlockPos(blockX, blockY, blockZ)).is(Blocks.LAVA)){
            return true;
        } else if(this.getCommandSenderWorld().getBlockState(new BlockPos(blockX, blockY, blockZ)).getBlock() instanceof RoadBlock){
            return true;
        } else if(this.getCommandSenderWorld().getBlockState(new BlockPos(blockX, blockY, blockZ)).getBlock() instanceof BoosterBlock){
            return true;
        }
        return false;
    }

    /**
     * Méthode qui gère tout ce qui touche aux collisions
     */
    private void collision() {
        //SI LE KART A UN PASSAGER ET QUE LE KART EST DANS UN BLOCK, ALORS ON LE REMONTE D'UN BLOC
        if (getFirstPassenger() != null) {
            if (isValidBlockCollision()){
                setPos(getX(), getY() + 1, getZ());
            }
        }

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
                        Kart.stunKart(kart, "Star");
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
        }
        //ACCELERE LE TOUT SI SOUS ETOILE
        if (getIsInvinsible() && !getIsPressingKeyDeccelerate())
            clamped_speed = clamped_speed * getStarSpeedBoost();

        //SI PAS SUR UN BLOC DE ROUTE (SANS BOOST OU ETOILE), ON SLOW LE VEHICULE
        if(!isOnRoadBlock() && (!getIsInvinsible() || getTimeBoost()<=0)){
            setSpeed(Mth.clamp(getSpeed(), -MAX_SPEED/2, MAX_SPEED/2));
        }

        //CALCUL ET APPLICATION DU VECTEUR DE DEPLACEMENT
        double x = Math.sin(-angle) * clamped_speed;
        double z = Math.cos(-angle) * clamped_speed;
        Vec3 vec3 = new Vec3(x, 0, z);
        speedToShow = clamped_speed;

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
    public static void stunKart(Kart kart, String motif) {
        if (kart.getIsInvinsible()) return;

        kart.stunMotif = motif;

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
        controllerRegistrar.add(new AnimationController<>(this, "controller_engine", 0, this::predicate_engine));
        controllerRegistrar.add(new AnimationController<>(this, "controller_propeller", 0, this::predicate_propeller));
        controllerRegistrar.add(new AnimationController<>(this, "controller_glider", 0, this::predicade_glider));
        controllerRegistrar.add(new AnimationController<>(this, "controller_drift", 0, this::predicate_drift));
    }

    private <T extends GeoAnimatable> PlayState predicate_engine(AnimationState<T> tAnimationState) {
        tAnimationState.getController().setAnimation(RawAnimation.begin()
                .then("engine", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState predicate_propeller(AnimationState<T> tAnimationState) {
        if (isInWater()) {
            tAnimationState.getController().setAnimation(RawAnimation.begin()
                    .then("propeller_on", Animation.LoopType.HOLD_ON_LAST_FRAME));
        } else {
            tAnimationState.getController().setAnimation(RawAnimation.begin()
                    .then("propeller_off", Animation.LoopType.HOLD_ON_LAST_FRAME));
        }
        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState predicade_glider(AnimationState<T> tAnimationState) {
        if (getDeltaOn()) {
            tAnimationState.getController().setAnimation(RawAnimation.begin()
                    .then("glider_on", Animation.LoopType.HOLD_ON_LAST_FRAME));
        } else {
            tAnimationState.getController().setAnimation(RawAnimation.begin()
                    .then("glider_off", Animation.LoopType.HOLD_ON_LAST_FRAME));
        }
        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState predicate_drift(AnimationState<T> tAnimationState) {
        //ANIMATION DES PARTICULES VIOLETTES
        if (getDriftingTime() >= 3) {
            tAnimationState.getController().setAnimation(RawAnimation.begin()
                    .then("drift_v", Animation.LoopType.LOOP));
        }
        //ANIMATION DES PARTICULES ORANGE
        else if (getDriftingTime() >= 2) {
            tAnimationState.getController().setAnimation(RawAnimation.begin()
                    .then("drift_o", Animation.LoopType.LOOP));
        }
        //ANIMATION DES PARTICULES BLEUES
        else if (getDriftingTime() >= 1) {
            tAnimationState.getController().setAnimation(RawAnimation.begin()
                    .then("drift_b", Animation.LoopType.LOOP));
        }
        //PAS D'ANIMATION DE DRIFT
        else {
            tAnimationState.getController().setAnimation(RawAnimation.begin()
                    .then("drift_off", Animation.LoopType.HOLD_ON_LAST_FRAME));
        }
        return PlayState.CONTINUE;
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

        if(getIsInvinsible()) {
            if (!isSoundPlaying(starModeLoop)) {
                starModeLoop = new SoundStarMode(this, SoundsInit.STAR_MODE.get(), SoundSource.RECORDS);
                SoundsInit.playSoundLoop(starModeLoop, level());
            }
        }else if(getDeltaOn()){
            if (!isSoundPlaying(kartGliding)) {
                kartGliding = new SoundKartGliding(this, SoundsInit.KART_GLIDING.get(), SoundSource.RECORDS);
                SoundsInit.playSoundLoop(kartGliding, level());
            }
        }else{
            //ARRET OU EN MOUVEMENT
            }if (getSpeed() > -MAX_SPEED*0.2f && getSpeed() < MAX_SPEED*0.2f) {
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
            if(getIsDrifting()){
                if (!isSoundPlaying(kartDrifting)) {
                    kartDrifting = new SoundKartDrifting(this, SoundsInit.KART_DRIFTING.get(), SoundSource.RECORDS);
                    SoundsInit.playSoundLoop(kartDrifting, level());
                }
            }
        }

        //BOOST DE VITESSE
        if(boostFini && getTimeBoost()>0){
            boostFini = false;
            if(getFirstPassenger()!=null && getFirstPassenger() instanceof Player player)
                SoundsInit.playSound(SoundsInit.KART_SPEED_BOOST.get(), level(), new BlockPos((int)getX(), (int)getY(), (int)getZ()), player, SoundSource.RECORDS, 1f);
        }else if(!boostFini && getTimeBoost()<=0){
            boostFini = true;
        }

        //STUN MOTIF
        if(!stunMotif.equals("None")){
            if(stunMotif.equals("Green_shell")){
                if(getFirstPassenger()!=null && getFirstPassenger() instanceof Player player)
                    SoundsInit.playSound(SoundsInit.GREEN_SHELL_HIT_KART.get(), level(), new BlockPos((int)getX(), (int)getY(), (int)getZ()), player, SoundSource.RECORDS, 1f);
            }else if(getFirstPassenger()!=null && getFirstPassenger() instanceof Player player){
                SoundsInit.playSound(SoundsInit.BANANA_HIT_KART.get(), level(), new BlockPos((int)getX(), (int)getY(), (int)getZ()), player, SoundSource.RECORDS, 0.7f);
            }
            stunMotif = "None";
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
