package superlord.ravagecabbage.entity;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.OwnerHurtByTargetGoal;
import net.minecraft.entity.ai.goal.OwnerHurtTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.AbstractIllagerEntity;
import net.minecraft.entity.monster.AbstractRaiderEntity;
import net.minecraft.entity.monster.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
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
import net.minecraft.util.Direction;
import net.minecraft.util.DrinkHelper;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.TransportationHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import superlord.ravagecabbage.init.RCEntities;
import superlord.ravagecabbage.init.RCItems;

public class RavageAndCabbageRavagerEntity extends TameableEntity implements IJumpingMount, IEquipable  {
	private static final Predicate<Entity> field_213690_b = (p_213685_0_) -> {
		return p_213685_0_.isAlive() && !(p_213685_0_ instanceof RavageAndCabbageRavagerEntity);
	};
	private static final DataParameter<Boolean> SADDLED = EntityDataManager.createKey(RavageAndCabbageRavagerEntity.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> BOOST_TIME = EntityDataManager.createKey(RavageAndCabbageRavagerEntity.class, DataSerializers.VARINT);
	private final BoostHelper field_234214_bx_ = new BoostHelper(this.dataManager, BOOST_TIME, SADDLED);
	private int attackTick;
	private int stunTick;
	private int roarTick;
	protected int gallopTime;

	public RavageAndCabbageRavagerEntity(EntityType<? extends RavageAndCabbageRavagerEntity> p_i50250_1_, World p_i50250_2_) {
		super(p_i50250_1_, p_i50250_2_);
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
		this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
	}

	public static AttributeModifierMap.MutableAttribute func_234215_eI_() {
		return MobEntity.func_233666_p_().createMutableAttribute(Attributes.MAX_HEALTH, 10.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25D);
	}

	public void notifyDataManagerChange(DataParameter<?> key) {
		if (BOOST_TIME.equals(key) && this.world.isRemote) {
			this.field_234214_bx_.updateData();
		}

		super.notifyDataManagerChange(key);
	}

	protected void registerData() {
		super.registerData();
		this.dataManager.register(SADDLED, false);
		this.dataManager.register(BOOST_TIME, 0);
	}

	public boolean func_230264_L__() {
		return this.isAlive() && !this.isChild();
	}

	protected void dropInventory() {
		super.dropInventory();
		if (this.isHorseSaddled()) {
			this.entityDropItem(Items.SADDLE);
		}

	}

	public boolean isHorseSaddled() {
		return this.field_234214_bx_.getSaddled();
	}

	public Vector3d func_230268_c_(LivingEntity livingEntity) {
		Vector3d vector3d = func_233559_a_((double)this.getWidth(), (double)livingEntity.getWidth(), this.rotationYaw + (livingEntity.getPrimaryHand() == HandSide.RIGHT ? 90.0F : -90.0F));
		Vector3d vector3d1 = this.func_234236_a_(vector3d, livingEntity);
		if (vector3d1 != null) {
			return vector3d1;
		} else {
			Vector3d vector3d2 = func_233559_a_((double)this.getWidth(), (double)livingEntity.getWidth(), this.rotationYaw + (livingEntity.getPrimaryHand() == HandSide.LEFT ? 90.0F : -90.0F));
			Vector3d vector3d3 = this.func_234236_a_(vector3d2, livingEntity);
			return vector3d3 != null ? vector3d3 : this.getPositionVec();
		}
	}

	@Nullable
	private Vector3d func_234236_a_(Vector3d p_234236_1_, LivingEntity p_234236_2_) {
		double d0 = this.getPosX() + p_234236_1_.x;
		double d1 = this.getBoundingBox().minY;
		double d2 = this.getPosZ() + p_234236_1_.z;
		BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

		for(Pose pose : p_234236_2_.getAvailablePoses()) {
			blockpos$mutable.setPos(d0, d1, d2);
			double d3 = this.getBoundingBox().maxY + 0.75D;

			while(true) {
				double d4 = this.world.func_242403_h(blockpos$mutable);
				if ((double)blockpos$mutable.getY() + d4 > d3) {
					break;
				}

				if (TransportationHelper.func_234630_a_(d4)) {
					AxisAlignedBB axisalignedbb = p_234236_2_.getPoseAABB(pose);
					Vector3d vector3d = new Vector3d(d0, (double)blockpos$mutable.getY() + d4, d2);
					if (TransportationHelper.func_234631_a_(this.world, p_234236_2_, axisalignedbb.offset(vector3d))) {
						p_234236_2_.setPose(pose);
						return vector3d;
					}
				}

				blockpos$mutable.move(Direction.UP);
				if (!((double)blockpos$mutable.getY() < d3)) {
					break;
				}
			}
		}

		return null;
	}

	public void func_241841_a(ServerWorld p_241841_1_, LightningBoltEntity p_241841_2_) {
		if (p_241841_1_.getDifficulty() != Difficulty.PEACEFUL) {
			ZombifiedPiglinEntity zombifiedpiglinentity = EntityType.ZOMBIFIED_PIGLIN.create(p_241841_1_);
			zombifiedpiglinentity.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
			zombifiedpiglinentity.setLocationAndAngles(this.getPosX(), this.getPosY(), this.getPosZ(), this.rotationYaw, this.rotationPitch);
			zombifiedpiglinentity.setNoAI(this.isAIDisabled());
			zombifiedpiglinentity.setChild(this.isChild());
			if (this.hasCustomName()) {
				zombifiedpiglinentity.setCustomName(this.getCustomName());
				zombifiedpiglinentity.setCustomNameVisible(this.isCustomNameVisible());
			}

			zombifiedpiglinentity.enablePersistence();
			p_241841_1_.addEntity(zombifiedpiglinentity);
			this.remove();
		} else {
			super.func_241841_a(p_241841_1_, p_241841_2_);
		}

	}

	public float getMountedSpeed() {
		return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.225F;
	}

	public boolean canBeSteered() {
		return this.getControllingPassenger() instanceof LivingEntity;
	}

	@OnlyIn(Dist.CLIENT)
	public Vector3d func_241205_ce_() {
		return new Vector3d(0.0D, (0.6F * this.getEyeHeight()), (this.getWidth() * 0.4F));
	}

	@Override
	protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
		return isChild() ? 0.7F : 1.3F;
	}

	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		compound.putInt("Age", this.getGrowingAge());
		compound.putInt("AttackTick", this.attackTick);
		compound.putInt("StunTick", this.stunTick);
		compound.putInt("RoarTick", this.roarTick);
		this.field_234214_bx_.setSaddledToNBT(compound);

	}

	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		this.setGrowingAge(compound.getInt("Age"));
		this.attackTick = compound.getInt("AttackTick");
		this.stunTick = compound.getInt("StunTick");
		this.roarTick = compound.getInt("RoarTick");
		this.field_234214_bx_.setSaddledFromNBT(compound);
	}	   

	@Override
	public void setTamed(boolean tamed) {
		super.setTamed(tamed);
		if (tamed) this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(60);
		else this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(40);
	}

	@Override
	public void updatePassenger(Entity passenger) {
		if (this.isPassenger(passenger)) {
			float f;
			int i = this.getPassengers().indexOf(passenger);
			if (i == 0) f = 0.2F;
			else f = -0.6F;
			Vector3d vec3d = (new Vector3d(f, 0.0D, 0.0D)).rotateYaw(-this.rotationYaw * 0.017453292F - ((float) Math.PI / 2F));
			passenger.setPosition(this.getPosX() + vec3d.x, this.getPosY() + this.getMountedYOffset() + passenger.getYOffset(), this.getPosZ() + vec3d.z);
		}
	}

	@Override
	public ActionResultType func_230254_b_(PlayerEntity p_230254_1_, Hand p_230254_2_) {
		boolean flag = this.isBreedingItem(p_230254_1_.getHeldItem(p_230254_2_));
		int i = this.getGrowingAge();
		ItemStack itemstack = p_230254_1_.getHeldItem(p_230254_2_);
		if (!flag && this.isHorseSaddled() && !this.isBeingRidden() && !p_230254_1_.isSecondaryUseActive() && itemstack.getItem() != Items.BUCKET && this.isTamed()) {
			if (!this.world.isRemote) {
				p_230254_1_.startRiding(this);
			}

			return ActionResultType.func_233537_a_(this.world.isRemote);
		} else {
			ActionResultType actionresulttype = super.func_230254_b_(p_230254_1_, p_230254_2_);
			if (!actionresulttype.isSuccessOrConsume() && itemstack.getItem() == Items.SADDLE && this.isTamed()) {
				return ActionResultType.PASS;
			} else if(itemstack.getItem() == Items.BUCKET && !this.isChild()) {
				p_230254_1_.playSound(SoundEvents.ENTITY_COW_MILK, 1.0F, 1.0F);
				ItemStack itemstack1 = DrinkHelper.fill(itemstack, p_230254_1_, RCItems.RAVAGER_MILK.get().getDefaultInstance());
				p_230254_1_.setHeldItem(p_230254_2_, itemstack1);
				return ActionResultType.func_233537_a_(this.world.isRemote);
			}  else if (itemstack.getItem() == RCItems.RAVAGER_MILK.get() && !this.isTamed()) {
				if (!p_230254_1_.abilities.isCreativeMode) {
					itemstack.shrink(1);
					p_230254_1_.setHeldItem(p_230254_2_, Items.BUCKET.getDefaultInstance());
				}
				if (this.rand.nextInt(3) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, p_230254_1_)) {
					this.setTamedBy(p_230254_1_);
					this.navigator.clearPath();
					this.setAttackTarget((LivingEntity)null);
					this.func_233687_w_(true);
					this.world.setEntityState(this, (byte)7);
				} else if(flag && this.canFallInLove() && i == 0 && this.rand.nextInt(15) == 0) {
					this.consumeItemFromStack(p_230254_1_, itemstack);
					this.setInLove(p_230254_1_);
					return ActionResultType.SUCCESS;
				} else if (flag && this.isChild()) {
					this.consumeItemFromStack(p_230254_1_, itemstack);
					this.ageUp((int)((float)(-i / 20) * 0.1F), true);
					return ActionResultType.func_233537_a_(this.world.isRemote);
				}
				else {
					this.world.setEntityState(this, (byte)6);
				}

				return ActionResultType.SUCCESS;
			} else {
				return actionresulttype;
			}
		}
	}

	protected boolean canFitPassenger(Entity passenger) {
		return this.getPassengers().size() < 2;
	}

	@Nullable
	public Entity getControllingPassenger() {
		List<Entity> list = this.getPassengers();
		return list.isEmpty() ? null : list.get(0);
	}

	@Override
	public double getMountedYOffset() {
		return this.getHeight() - 0.2F;
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return stack.getItem() == RCItems.CABBAGE.get();
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

	public float getEyeHeight(Pose pose) {
		return this.isChild() ? this.getHeight() : 2.0F;
	}

	public static AttributeModifierMap.MutableAttribute func_234233_eS_() {
		return MobEntity.func_233666_p_().createMutableAttribute(Attributes.MAX_HEALTH, 100.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3D).createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 0.75D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 12.0D).createMutableAttribute(Attributes.ATTACK_KNOCKBACK, 1.5D).createMutableAttribute(Attributes.FOLLOW_RANGE, 32.0D);
	}

	public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
		this.setGrowingAge(-24000);
		return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
	}

	protected float getSoundVolume() {
		return 0.4F;
	}

	public boolean attackEntityAsMob(Entity entityIn) {
		this.attackTick = 10;
		this.world.setEntityState(this, (byte)4);
		this.playSound(SoundEvents.ENTITY_RAVAGER_ATTACK, 1.0F, 1.0F);
		return super.attackEntityAsMob(entityIn);
	}

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


	private void launch(Entity p_213688_1_) {
		double d0 = p_213688_1_.getPosX() - this.getPosX();
		double d1 = p_213688_1_.getPosZ() - this.getPosZ();
		double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
		p_213688_1_.addVelocity(d0 / d2 * 4.0D, 0.2D, d1 / d2 * 4.0D);
	}

	public RavageAndCabbageRavagerEntity func_241840_a(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
		RavageAndCabbageRavagerEntity ravagerentity = RCEntities.RAVAGER.get().create(p_241840_1_);
		UUID uuid = this.getOwnerId();
		if (uuid != null) {
			ravagerentity.setOwnerId(uuid);
			ravagerentity.setTamed(true);
		}

		return ravagerentity;
	}

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

	public void travel(Vector3d travelVector) {
		if (this.isAlive()) {
			if (this.isBeingRidden() && this.canBeSteered() && this.isHorseSaddled()) {
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
					this.gallopTime = 0;
				}



				if (this.canPassengerSteer()) {
					this.setAIMoveSpeed((float)this.getAttributeValue(Attributes.MOVEMENT_SPEED));
					super.travel(new Vector3d((double)f, travelVector.y, (double)f1));
				} else if (livingentity instanceof PlayerEntity) {
					this.setMotion(Vector3d.ZERO);
				}

				this.func_233629_a_(this, false);
			} else {
				this.jumpMovementFactor = 0.02F;
				super.travel(travelVector);
			}
		}
	}

	@Override
	public void func_230266_a_(@Nullable SoundCategory p_230266_1_) {
		this.field_234214_bx_.setSaddledFromBoolean(true);
		if (p_230266_1_ != null) {
			this.world.playMovingSound((PlayerEntity)null, this, SoundEvents.ENTITY_PIG_SADDLE, p_230266_1_, 0.5F, 1.0F);
		}
	}

	@Override
	public void setJumpPower(int jumpPowerIn) {

	}

	@Override
	public boolean canJump() {
		return false;
	}

	@Override
	public void handleStartJump(int jumpPower) {

	}

	@Override
	public void handleStopJump() {

	}

	@Override
	public ItemStack getPickedResult(RayTraceResult target) {
		return new ItemStack(RCItems.RAVAGER_SPAWN_EGG.get());
	}

}