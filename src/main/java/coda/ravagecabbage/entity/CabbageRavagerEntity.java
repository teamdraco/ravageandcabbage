package coda.ravagecabbage.entity;

import coda.ravagecabbage.init.ItemInit;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.AbstractIllagerEntity;
import net.minecraft.entity.monster.AbstractRaiderEntity;
import net.minecraft.entity.monster.RavagerEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public class CabbageRavagerEntity extends AnimalEntity {
    private static final Predicate<Entity> field_213690_b = (p_213685_0_) -> {
        return p_213685_0_.isAlive() && !(p_213685_0_ instanceof RavagerEntity);
    };
    private static final DataParameter<Boolean> BABY = EntityDataManager.createKey(CabbageRavagerEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> TAMED = EntityDataManager.createKey(CabbageRavagerEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.createKey(CabbageRavagerEntity.class, DataSerializers.OPTIONAL_UNIQUE_ID);
    protected int growingAge;
    private int attackTick;
    private int stunTick;
    private int roarTick;

    public CabbageRavagerEntity(EntityType<? extends CabbageRavagerEntity> type, World world) {
        super(type, world);
        this.stepHeight = 1.0F;
        this.experienceValue = 20;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimGoal(this));
        if (!this.isChild()) this.goalSelector.addGoal(4, new CabbageRavagerEntity.AttackGoal());
        this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 0.4D));
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(10, new LookAtGoal(this, MobEntity.class, 8.0F));
        this.targetSelector.addGoal(2, (new HurtByTargetGoal(this, AbstractRaiderEntity.class)).setCallsForHelp());
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, AbstractVillagerEntity.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, true));
    }

    public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        this.setGrowingAge(-24000);
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Nullable
    @Override
    public AgeableEntity createChild(AgeableEntity ageable) {
        return null;
    }

    protected void registerData() {
        super.registerData();
        this.dataManager.register(BABY, false);
        this.dataManager.register(TAMED, false);
        this.dataManager.register(OWNER_UNIQUE_ID, Optional.empty());
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        IAttributeInstance attribute = getAttribute(SharedMonsterAttributes.MAX_HEALTH);
        attribute.setBaseValue(attribute.getBaseValue() * 2);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
        this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.5D);
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(12.0D);
        this.getAttribute(SharedMonsterAttributes.ATTACK_KNOCKBACK).setBaseValue(1.5D);
        this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
    }

    public int getGrowingAge() {
        if (this.world.isRemote) {
            return this.dataManager.get(BABY) ? -1 : 1;
        } else {
            return this.growingAge;
        }
    }

    public void setGrowingAge(int age) {
        int i = this.growingAge;
        this.growingAge = age;
        if (i < 0 && age >= 0 || i >= 0 && age < 0) {
            this.dataManager.set(BABY, age < 0);
        }
    }

    public void livingTick() {
        super.livingTick();
        if (this.isAlive()) {
            if (this.isMovementBlocked()) {
                this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
            } else {
                double d0 = this.getAttackTarget() != null ? 0.35D : 0.3D;
                double d1 = this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue();
                this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(MathHelper.lerp(0.1D, d1, d0));
            }

            if (this.collidedHorizontally && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, this)) {
                boolean flag = false;
                AxisAlignedBB axisalignedbb = this.getBoundingBox().grow(0.2D);

                for(BlockPos blockpos : BlockPos.getAllInBoxMutable(MathHelper.floor(axisalignedbb.minX), MathHelper.floor(axisalignedbb.minY), MathHelper.floor(axisalignedbb.minZ), MathHelper.floor(axisalignedbb.maxX), MathHelper.floor(axisalignedbb.maxY), MathHelper.floor(axisalignedbb.maxZ))) {
                    BlockState blockstate = this.world.getBlockState(blockpos);
                    Block block = blockstate.getBlock();
                    if (block instanceof LeavesBlock) {
                        flag = this.world.destroyBlock(blockpos, true, this) || flag;
                    }
                }

                if (!flag && this.onGround) {
                    this.jump();
                }
            }

            if (this.roarTick > 0) {
                --this.roarTick;
                if (this.roarTick == 10) {
                    this.roar();
                }
            }

            if (this.attackTick > 0) {
                --this.attackTick;
            }

            if (this.stunTick > 0) {
                --this.stunTick;
                this.func_213682_eh();
                if (this.stunTick == 0) {
                    this.playSound(SoundEvents.ENTITY_RAVAGER_ROAR, 1.0F, 1.0F);
                    this.roarTick = 20;
                }
            }
        }
        if (this.world.isRemote) {
            if (this.isAlive() && isTamed()) {
                int i = this.getGrowingAge();
                if (i < 0) {
                    ++i;
                    this.setGrowingAge(i);
                } else if (i > 0) {
                    --i;
                    this.setGrowingAge(i);
                }
            }
        }
    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_RAVAGER_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_RAVAGER_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_RAVAGER_DEATH;
    }

    private void func_213682_eh() {
        if (this.rand.nextInt(6) == 0) {
            double d0 = this.getPosX() - (double)this.getWidth() * Math.sin((double)(this.renderYawOffset * ((float)Math.PI / 180F))) + (this.rand.nextDouble() * 0.6D - 0.3D);
            double d1 = this.getPosY() + (double)this.getHeight() - 0.3D;
            double d2 = this.getPosZ() + (double)this.getWidth() * Math.cos((double)(this.renderYawOffset * ((float)Math.PI / 180F))) + (this.rand.nextDouble() * 0.6D - 0.3D);
            this.world.addParticle(ParticleTypes.ENTITY_EFFECT, d0, d1, d2, 0.4980392156862745D, 0.5137254901960784D, 0.5725490196078431D);
        }

    }

    protected boolean isMovementBlocked() {
        return super.isMovementBlocked() || this.attackTick > 0 || this.stunTick > 0 || this.roarTick > 0;
    }

    public boolean canEntityBeSeen(Entity entityIn) {
        return this.stunTick <= 0 && this.roarTick <= 0 ? super.canEntityBeSeen(entityIn) : false;
    }

    protected void constructKnockBackVector(LivingEntity entityIn) {
        if (this.roarTick == 0) {
            if (this.rand.nextDouble() < 0.5D) {
                this.stunTick = 40;
                this.playSound(SoundEvents.ENTITY_RAVAGER_STUNNED, 1.0F, 1.0F);
                this.world.setEntityState(this, (byte)39);
                entityIn.applyEntityCollision(this);
            } else {
                this.launch(entityIn);
            }

            entityIn.velocityChanged = true;
        }

    }

    private void roar() {
        if (this.isAlive()) {
            for(Entity entity : this.world.getEntitiesWithinAABB(LivingEntity.class, this.getBoundingBox().grow(4.0D), field_213690_b)) {
                if (!(entity instanceof AbstractIllagerEntity)) {
                    entity.attackEntityFrom(DamageSource.causeMobDamage(this), 6.0F);
                }

                this.launch(entity);
            }

            Vec3d vec3d = this.getBoundingBox().getCenter();

            for(int i = 0; i < 40; ++i) {
                double d0 = this.rand.nextGaussian() * 0.2D;
                double d1 = this.rand.nextGaussian() * 0.2D;
                double d2 = this.rand.nextGaussian() * 0.2D;
                this.world.addParticle(ParticleTypes.POOF, vec3d.x, vec3d.y, vec3d.z, d0, d1, d2);
            }
        }
    }

    private void launch(Entity p_213688_1_) {
        double d0 = p_213688_1_.getPosX() - this.getPosX();
        double d1 = p_213688_1_.getPosZ() - this.getPosZ();
        double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
        p_213688_1_.addVelocity(d0 / d2 * 4.0D, 0.2D, d1 / d2 * 4.0D);
    }

    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 4) {
            this.attackTick = 10;
            this.playSound(SoundEvents.ENTITY_RAVAGER_ATTACK, 1.0F, 1.0F);
        } else if (id == 39) {
            this.stunTick = 40;
        }

        super.handleStatusUpdate(id);
    }

    public Optional<UUID> getOwnerId() {
        return this.dataManager.get(OWNER_UNIQUE_ID);
    }

    public void setOwnerId(@Nullable UUID uniqueId) {
        this.dataManager.set(OWNER_UNIQUE_ID, Optional.ofNullable(uniqueId));
    }

    @Nullable
    public LivingEntity getOwner() {
        try {
            Optional<UUID> id = this.getOwnerId();
            return id.map(uuid -> this.world.getPlayerByUuid(uuid)).orElse(null);
        } catch (IllegalArgumentException var2) {
            return null;
        }
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("Age", this.getGrowingAge());
        this.getOwnerId().ifPresent(id -> compound.putUniqueId("OwnerUUID", id));
        compound.putInt("AttackTick", this.attackTick);
        compound.putInt("StunTick", this.stunTick);
        compound.putInt("RoarTick", this.roarTick);
    }

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setGrowingAge(compound.getInt("Age"));
        if (compound.hasUniqueId("OwnerUUID")) this.setOwnerId(compound.getUniqueId("OwnerUUID"));
        this.attackTick = compound.getInt("AttackTick");
        this.stunTick = compound.getInt("StunTick");
        this.roarTick = compound.getInt("RoarTick");
    }

    @OnlyIn(Dist.CLIENT)
    protected void playTameEffect() {
        IParticleData iparticledata = ParticleTypes.HEART;
        for(int i = 0; i < 7; ++i) {
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            this.world.addParticle(iparticledata, this.getPosXRandom(1.0D), this.getPosYRandom() + 0.5D, this.getPosZRandom(1.0D), d0, d1, d2);
        }
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.ENTITY_RAVAGER_STEP, 0.15F, 1.0F);
    }

    public boolean isNotColliding(IWorldReader worldIn) {
        return !worldIn.containsAnyLiquid(this.getBoundingBox());
    }

    public boolean attackEntityAsMob(Entity entityIn) {
        this.attackTick = 10;
        this.world.setEntityState(this, (byte)4);
        this.playSound(SoundEvents.ENTITY_RAVAGER_ATTACK, 1.0F, 1.0F);
        return super.attackEntityAsMob(entityIn);
    }

    public boolean isTamed() {
        return this.dataManager.get(TAMED);
    }

    public void setTamed(boolean tamed) {
        this.dataManager.set(TAMED, tamed);
    }

    @Override
    public boolean isChild() {
        return getGrowingAge() < 0;
    }

    @Override
    public boolean processInteract(PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getItem() == ItemInit.RAVAGER_MILK.get()) {
            if (!player.abilities.isCreativeMode) stack.shrink(1);
            playTameEffect();
            world.setEntityState(this, (byte) 7);
            return true;
        }
        return super.processInteract(player, hand);
    }

    public Team getTeam() {
        if (this.isTamed()) {
            LivingEntity livingentity = this.getOwner();
            if (livingentity != null) {
                return livingentity.getTeam();
            }
        }

        return super.getTeam();
    }

    public boolean isOnSameTeam(Entity entityIn) {
        if (this.isTamed()) {
            LivingEntity livingentity = this.getOwner();
            if (entityIn == livingentity) {
                return true;
            }

            if (livingentity != null) {
                return livingentity.isOnSameTeam(entityIn);
            }
        }

        return super.isOnSameTeam(entityIn);
    }

    public void onDeath(DamageSource cause) {
        LivingEntity owner = this.getOwner();

        if (!this.world.isRemote && this.world.getGameRules().getBoolean(GameRules.SHOW_DEATH_MESSAGES) && owner instanceof ServerPlayerEntity) {
            owner.sendMessage(this.getCombatTracker().getDeathMessage());
        }

        super.onDeath(cause);
    }

    @OnlyIn(Dist.CLIENT)
    public int func_213683_l() {
        return this.attackTick;
    }

    @OnlyIn(Dist.CLIENT)
    public int func_213684_dX() {
        return this.stunTick;
    }

    @OnlyIn(Dist.CLIENT)
    public int func_213687_eg() {
        return this.roarTick;
    }

    public double getMountedYOffset() {
        return 2.1D;
    }

    public boolean canBeSteered() {
        return !this.isAIDisabled() && this.getControllingPassenger() instanceof LivingEntity;
    }

    @Nullable
    public Entity getControllingPassenger() {
        return this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
    }

    class AttackGoal extends MeleeAttackGoal {
        public AttackGoal() {
            super(CabbageRavagerEntity.this, 1.0D, true);
        }

        protected double getAttackReachSqr(LivingEntity attackTarget) {
            float f = CabbageRavagerEntity.this.getWidth() - 0.1F;
            return (double)(f * 2.0F * f * 2.0F + attackTarget.getWidth());
        }
    }

    static class Navigator extends GroundPathNavigator {
        public Navigator(MobEntity p_i50754_1_, World p_i50754_2_) {
            super(p_i50754_1_, p_i50754_2_);
        }

        protected PathFinder getPathFinder(int p_179679_1_) {
            this.nodeProcessor = new CabbageRavagerEntity.Processor();
            return new PathFinder(this.nodeProcessor, p_179679_1_);
        }
    }

    static class Processor extends WalkNodeProcessor {
        private Processor() {
        }

        protected PathNodeType func_215744_a(IBlockReader p_215744_1_, boolean p_215744_2_, boolean p_215744_3_, BlockPos p_215744_4_, PathNodeType p_215744_5_) {
            return p_215744_5_ == PathNodeType.LEAVES ? PathNodeType.OPEN : super.func_215744_a(p_215744_1_, p_215744_2_, p_215744_3_, p_215744_4_, p_215744_5_);
        }
    }
}
