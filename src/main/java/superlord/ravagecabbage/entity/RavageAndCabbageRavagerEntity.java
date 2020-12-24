package superlord.ravagecabbage.entity;

import java.util.UUID;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IAngerable;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.OwnerHurtByTargetGoal;
import net.minecraft.entity.ai.goal.OwnerHurtTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.AbstractIllagerEntity;
import net.minecraft.entity.monster.AbstractRaiderEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.RangedInteger;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.TickRangeConverter;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import superlord.ravagecabbage.init.EntityInit;
import superlord.ravagecabbage.init.ItemInit;

public class RavageAndCabbageRavagerEntity extends TameableEntity implements IAngerable {
	private static final Predicate<Entity> field_213690_b = (p_213685_0_) -> {
		return p_213685_0_.isAlive() && !(p_213685_0_ instanceof RavageAndCabbageRavagerEntity);
	};   private static final DataParameter<Byte> STATUS = EntityDataManager.createKey(RavageAndCabbageRavagerEntity.class, DataSerializers.BYTE);

	protected Inventory horseChest;
	private static final DataParameter<Integer> field_234232_bz_ = EntityDataManager.createKey(RavageAndCabbageRavagerEntity.class, DataSerializers.VARINT);
	private static final RangedInteger field_234230_bG_ = TickRangeConverter.convertRange(20, 39);
	private UUID field_234231_bH_;

	private int attackTick;
	private int stunTick;
	private int roarTick;
	public float ridingXZ;
	public float ridingY = 1;

	public RavageAndCabbageRavagerEntity(EntityType<? extends RavageAndCabbageRavagerEntity> type, World worldIn) {
		super(type, worldIn);
		this.setTamed(false);
	}

	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(0, new SwimGoal(this));
		if (!this.isChild()) this.goalSelector.addGoal(4, new RavageAndCabbageRavagerEntity.AttackGoal());
		this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 0.4D));
		this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.addGoal(10, new LookAtGoal(this, MobEntity.class, 8.0F));
		this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
		if (!this.isChild() && !this.isTamed()) {
			this.targetSelector.addGoal(2, (new HurtByTargetGoal(this, AbstractRaiderEntity.class)).setCallsForHelp());
			this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
			this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, AbstractVillagerEntity.class, true));
			this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, true));
		}
	}

	public static AttributeModifierMap.MutableAttribute func_234233_eS_() {
		return MobEntity.func_233666_p_().createMutableAttribute(Attributes.MAX_HEALTH, 100.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3D).createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 0.75D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 12.0D).createMutableAttribute(Attributes.ATTACK_KNOCKBACK, 1.5D).createMutableAttribute(Attributes.FOLLOW_RANGE, 32.0D);
	}

	protected void registerData() {
		super.registerData();
		this.dataManager.register(STATUS, (byte)0);
		this.dataManager.register(field_234232_bz_, 0);
	}

	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		compound.putInt("Age", this.getGrowingAge());
		compound.putInt("AttackTick", this.attackTick);
		compound.putInt("StunTick", this.stunTick);
		compound.putInt("RoarTick", this.roarTick);
	}

	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		this.setGrowingAge(compound.getInt("Age"));
		this.attackTick = compound.getInt("AttackTick");
		this.stunTick = compound.getInt("StunTick");
		this.roarTick = compound.getInt("RoarTick");
	}

	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_RAVAGER_AMBIENT;
	}

	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.ENTITY_RAVAGER_HURT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_RAVAGER_DEATH;
	}

	/**
	 * Returns the volume for the sounds this mob makes.
	 */
	protected float getSoundVolume() {
		return 0.4F;
	}

	public boolean attackEntityAsMob(Entity entityIn) {
		this.attackTick = 10;
		this.world.setEntityState(this, (byte)4);
		this.playSound(SoundEvents.ENTITY_RAVAGER_ATTACK, 1.0F, 1.0F);
		return super.attackEntityAsMob(entityIn);
	}

	/**
	 * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
	 * use this to react to sunlight and start to burn.
	 */
	public void livingTick() {
		super.livingTick();
		if (this.isAlive()) {
			if (this.isMovementBlocked()) {
				this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0D);
			} else {
				double d0 = this.getAttackTarget() != null ? 0.35D : 0.3D;
				double d1 = this.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue();
				this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(MathHelper.lerp(0.1D, d1, d0));
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

	public void onDeath(DamageSource cause) {
		super.onDeath(cause);
	}

	protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
		return sizeIn.height * 0.8F;
	}

	public int getVerticalFaceSpeed() {
		return this.isEntitySleeping() ? 20 : super.getVerticalFaceSpeed();
	}

	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else {
			Entity entity = source.getTrueSource();
			this.func_233687_w_(false);
			if (entity != null && !(entity instanceof PlayerEntity) && !(entity instanceof AbstractArrowEntity)) {
				amount = (amount + 1.0F) / 2.0F;
			}

			return super.attackEntityFrom(source, amount);
		}
	}

	protected void updateHorseSlots() {
		if (!this.world.isRemote) {
			this.setSaddled(!this.horseChest.getStackInSlot(0).isEmpty() && this.canBeSaddled());
		}
	}

	public boolean canEntityBeSeen(Entity entityIn) {
		return this.stunTick <= 0 && this.roarTick <= 0 ? super.canEntityBeSeen(entityIn) : false;
	}

	public boolean canBeSaddled() {
		return true;
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

			Vector3d vec3d = this.getBoundingBox().getCenter();

			for(int i = 0; i < 40; ++i) {
				double d0 = this.rand.nextGaussian() * 0.2D;
				double d1 = this.rand.nextGaussian() * 0.2D;
				double d2 = this.rand.nextGaussian() * 0.2D;
				this.world.addParticle(ParticleTypes.POOF, vec3d.x, vec3d.y, vec3d.z, d0, d1, d2);
			}
		}
	}

	private void func_213682_eh() {
		if (this.rand.nextInt(6) == 0) {
			double d0 = this.getPosX() - (double)this.getWidth() * Math.sin((double)(this.renderYawOffset * ((float)Math.PI / 180F))) + (this.rand.nextDouble() * 0.6D - 0.3D);
			double d1 = this.getPosY() + (double)this.getHeight() - 0.3D;
			double d2 = this.getPosZ() + (double)this.getWidth() * Math.cos((double)(this.renderYawOffset * ((float)Math.PI / 180F))) + (this.rand.nextDouble() * 0.6D - 0.3D);
			this.world.addParticle(ParticleTypes.ENTITY_EFFECT, d0, d1, d2, 0.4980392156862745D, 0.5137254901960784D, 0.5725490196078431D);
		}

	}

	public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
		this.setGrowingAge(-24000);
		return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
	}

	private void launch(Entity p_213688_1_) {
		double d0 = p_213688_1_.getPosX() - this.getPosX();
		double d1 = p_213688_1_.getPosZ() - this.getPosZ();
		double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
		p_213688_1_.addVelocity(d0 / d2 * 4.0D, 0.2D, d1 / d2 * 4.0D);
	}

	public void setTamed(boolean tamed) {
		super.setTamed(tamed);
		if (tamed) {
			this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(20.0D);
			this.setHealth(20.0F);
		} else {
			this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(8.0D);
		}

		this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(4.0D);
	}

	public ActionResultType func_230254_b_(PlayerEntity p_230254_1_, Hand p_230254_2_) {
		ItemStack itemstack = p_230254_1_.getHeldItem(p_230254_2_);
		Item item = itemstack.getItem();
		if (this.world.isRemote) {
			boolean flag = this.isOwner(p_230254_1_) || this.isTamed() || item == ItemInit.RAVAGER_MILK.get() && !this.isTamed() && !this.func_233678_J__();
			return flag ? ActionResultType.CONSUME : ActionResultType.PASS;
		}else if (item == Items.BUCKET && !this.isChild()) {
			itemstack.shrink(1);
			p_230254_1_.addItemStackToInventory(new ItemStack(ItemInit.RAVAGER_MILK.get()));
			return ActionResultType.SUCCESS;
		} else if(item == Items.SADDLE && !this.isSaddled() && this.isTamed()) {
			this.setSaddled(true);
			itemstack.shrink(1);
			return ActionResultType.SUCCESS;
		} else {
			if (item == ItemInit.RAVAGER_MILK.get() && !this.func_233678_J__()) {
				if (!p_230254_1_.abilities.isCreativeMode) {
					itemstack.shrink(1);
					p_230254_1_.addItemStackToInventory(new ItemStack(Items.BUCKET));
				}

				if (this.rand.nextInt(3) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, p_230254_1_)) {
					this.setTamedBy(p_230254_1_);
					this.navigator.clearPath();
					this.setAttackTarget((LivingEntity)null);
					this.func_233687_w_(true);
					this.world.setEntityState(this, (byte)7);
				} 
				else {
					this.world.setEntityState(this, (byte)6);
				}

				return ActionResultType.SUCCESS;
			} 

			return super.func_230254_b_(p_230254_1_, p_230254_2_);
		}
	}

	public boolean isBreedingItem(ItemStack stack) {
		Item item = stack.getItem();
		return item.isFood() && item.getFood().isMeat();
	}

	/**
	 * Will return how many at most can spawn in a chunk at once.
	 */
	public int getMaxSpawnedInChunk() {
		return 8;
	}

	public int getAngerTime() {
		return this.dataManager.get(field_234232_bz_);
	}

	public void setAngerTime(int time) {
		this.dataManager.set(field_234232_bz_, time);
	}

	public void func_230258_H__() {
		this.setAngerTime(field_234230_bG_.getRandomWithinRange(this.rand));
	}

	@Nullable
	public UUID getAngerTarget() {
		return this.field_234231_bH_;
	}

	public void setAngerTarget(@Nullable UUID target) {
		this.field_234231_bH_ = target;
	}

	public RavageAndCabbageRavagerEntity func_241840_a(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
		RavageAndCabbageRavagerEntity ravagerentity = EntityInit.RAVAGER.get().create(p_241840_1_);
		UUID uuid = this.getOwnerId();
		if (uuid != null) {
			ravagerentity.setOwnerId(uuid);
			ravagerentity.setTamed(true);
		}

		return ravagerentity;
	}

	public void setSaddled(boolean saddled) {
		this.setWatchableBoolean(4, saddled);
	}

	/**
	 * Returns true if the mob is currently able to mate with the specified mob.
	 */
	public boolean canMateWith(AnimalEntity otherAnimal) {
		if (otherAnimal == this) {
			return false;
		} else if (!this.isTamed()) {
			return false;
		} else if (!(otherAnimal instanceof RavageAndCabbageRavagerEntity)) {
			return false;
		} else {
			RavageAndCabbageRavagerEntity ravagerentity = (RavageAndCabbageRavagerEntity)otherAnimal;
			if (!ravagerentity.isTamed()) {
				return false;
			} else if (ravagerentity.isEntitySleeping()) {
				return false;
			} else {
				return this.isInLove() && ravagerentity.isInLove();
			}
		}
	}

	public boolean isSaddled() {
		return this.getWatchableBoolean(4);
	}

	protected boolean getWatchableBoolean(int p_110233_1_) {
		return (this.dataManager.get(STATUS) & p_110233_1_) != 0;
	}

	protected void setWatchableBoolean(int p_110208_1_, boolean p_110208_2_) {
		byte b0 = this.dataManager.get(STATUS);
		if (p_110208_2_) {
			this.dataManager.set(STATUS, (byte)(b0 | p_110208_1_));
		} else {
			this.dataManager.set(STATUS, (byte)(b0 & ~p_110208_1_));
		}

	}


	public boolean canBeLeashedTo(PlayerEntity player) {
		return !this.func_233678_J__() && super.canBeLeashedTo(player);
	}

	protected boolean isMovementBlocked() {
		return super.isMovementBlocked() || this.attackTick > 0 || this.stunTick > 0 || this.roarTick > 0;
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

	@OnlyIn(Dist.CLIENT)
	public Vector3d func_241205_ce_() {
		return new Vector3d(0.0D, (double)(0.6F * this.getEyeHeight()), (double)(this.getWidth() * 0.4F));
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

	class AttackGoal extends MeleeAttackGoal {
		public AttackGoal() {
			super(RavageAndCabbageRavagerEntity.this, 1.0D, true);
		}

		protected double getAttackReachSqr(LivingEntity attackTarget) {
			float f = RavageAndCabbageRavagerEntity.this.getWidth() - 0.1F;
			return (double)(f * 2.0F * f * 2.0F + attackTarget.getWidth());
		}
	}

	static class Navigator extends GroundPathNavigator {
		public Navigator(MobEntity p_i50754_1_, World p_i50754_2_) {
			super(p_i50754_1_, p_i50754_2_);
		}

		protected PathFinder getPathFinder(int p_179679_1_) {
			this.nodeProcessor = new RavageAndCabbageRavagerEntity.Processor();
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

	public double getMountedYOffset() {
		return 2.1D;
	}

	public PlayerEntity getRidingPlayer() {
		if (this.getControllingPassenger() instanceof PlayerEntity) {
			return (PlayerEntity) getControllingPassenger();
		} else {
			return null;
		}
	}

	@Override
	public void updatePassenger(Entity passenger) {
		super.updatePassenger(passenger);
		if (this.isPassenger(passenger)) {
			renderYawOffset = rotationYaw;
			this.rotationYaw = passenger.rotationYaw;
		}
		if (this.getRidingPlayer() != null && this.getRidingPlayer() instanceof PlayerEntity && this.getAttackTarget() != this.getRidingPlayer()) {
			rotationYaw = renderYawOffset;
			rotationYaw = this.getRidingPlayer().rotationYaw;
			rotationYawHead = this.getRidingPlayer().rotationYaw;
			float radius = ridingXZ * 0.7F * -3;
			float angle = (0.01745329251F * this.renderYawOffset);
			double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
			double extraZ = radius * MathHelper.cos(angle);
			double extraY = ridingY * 4;
			this.getRidingPlayer().setPosition(this.getPosX() + extraX, this.getPosY() + extraY - 1.75F, this.getPosZ() + extraZ);
		}
	}

	public void travel(Vector3d positionIn) {
		if (this.isAlive()) {
			if (this.isBeingRidden() && this.canBeSteered() && this.isSaddled()) {
				LivingEntity livingentity = (LivingEntity)this.getControllingPassenger();
				this.rotationYaw = livingentity.rotationYaw;
				this.prevRotationYaw = this.rotationYaw;
				this.rotationPitch = livingentity.rotationPitch * 0.5F;
				this.setRotation(this.rotationYaw, this.rotationPitch);
				this.renderYawOffset = this.rotationYaw;
				this.rotationYawHead = this.renderYawOffset;
				float f = livingentity.moveStrafing * 0.5F;
				float f1 = livingentity.moveForward;
				if (f1 <= 0.0F) {
					f1 *= 0.25F;
				}

				if (this.canPassengerSteer()) {
					this.setAIMoveSpeed((float)this.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
					super.travel(new Vector3d((double)f, positionIn.y, (double)f1));
				} else if (livingentity instanceof PlayerEntity) {
					this.setMotion(Vector3d.ZERO);
				}

			} else {
				super.travel(positionIn);
			}
		}
	}

}
