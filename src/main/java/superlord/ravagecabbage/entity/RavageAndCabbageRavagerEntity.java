package superlord.ravagecabbage.entity;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
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
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
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
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
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
import net.minecraftforge.event.ForgeEventFactory;
import superlord.ravagecabbage.common.inventory.RavagerContainer;
import superlord.ravagecabbage.init.EntityInit;
import superlord.ravagecabbage.init.ItemInit;

public class RavageAndCabbageRavagerEntity extends TameableEntity implements IInventoryChangedListener, INamedContainerProvider {
	private static final Predicate<Entity> field_213690_b = (p_213685_0_) -> {
		return p_213685_0_.isAlive() && !(p_213685_0_ instanceof RavageAndCabbageRavagerEntity);
	};
	private static final DataParameter<Boolean> SADDLE = EntityDataManager.createKey(RavageAndCabbageRavagerEntity.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> DATA_ID_CHEST = EntityDataManager.createKey(RavageAndCabbageRavagerEntity.class, DataSerializers.BOOLEAN);
	private int attackTick;
	private int stunTick;
	private int roarTick;
	public Inventory inventory;

	public RavageAndCabbageRavagerEntity(EntityType<? extends RavageAndCabbageRavagerEntity> type, World worldIn) {
		super(type, worldIn);
		this.initChest();
	}

	@Override
	protected void registerData() {
		super.registerData();
		this.dataManager.register(SADDLE, false);
		this.dataManager.register(DATA_ID_CHEST, false);
	}

	public boolean hasChest() {
		return this.dataManager.get(DATA_ID_CHEST);
	}

	public void setChested(boolean chested) {
		this.dataManager.set(DATA_ID_CHEST, chested);
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

	public void onDeath(DamageSource cause) {
		super.onDeath(cause);

		if (this.hasChest()) {
			if (!this.world.isRemote) {
				this.entityDropItem(new ItemStack(Blocks.CHEST, 1));
			}

			this.setChested(false);
		}
	}

	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		compound.putBoolean("Chest", this.hasChest());
		compound.putInt("Age", this.getGrowingAge());
		compound.putInt("AttackTick", this.attackTick);
		compound.putInt("StunTick", this.stunTick);
		compound.putInt("RoarTick", this.roarTick);
		if (this.hasChest()) {
			ListNBT nbttaglist = new ListNBT();

			for (int i = 2; i < this.inventory.getSizeInventory(); ++i) {
				ItemStack itemstack = this.inventory.getStackInSlot(i);

				if (!itemstack.isEmpty()) {
					CompoundNBT nbttagcompound = new CompoundNBT();
					nbttagcompound.putByte("Slot", (byte) i);
					itemstack.write(nbttagcompound);
					nbttaglist.add(nbttagcompound);
				}
			}

			compound.put("Items", nbttaglist);
		}
	}

	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		this.setChested(compound.getBoolean("Chest"));
		this.setGrowingAge(compound.getInt("Age"));
		this.attackTick = compound.getInt("AttackTick");
		this.stunTick = compound.getInt("StunTick");
		this.roarTick = compound.getInt("RoarTick");
		if (this.hasChest()) {
			ListNBT nbttaglist = compound.getList("Items", 10);
			this.initChest();

			for (int i = 0; i < nbttaglist.size(); ++i) {
				CompoundNBT nbttagcompound = nbttaglist.getCompound(i);
				int j = nbttagcompound.getByte("Slot") & 255;

				if (j >= 2 && j < this.inventory.getSizeInventory()) {
					this.inventory.setInventorySlotContents(j, ItemStack.read(nbttagcompound));
				}
			}
		}

		if (!this.world.isRemote) this.dataManager.set(SADDLE, !this.inventory.getStackInSlot(0).isEmpty());
	}

	protected void initChest() {
		Inventory inv = this.inventory;
		this.inventory = new Inventory(this.hasChest() ? 17 : 1);

		if (inv != null) {
			inv.removeListener(this);
			int i = Math.min(inv.getSizeInventory(), this.inventory.getSizeInventory());

			for (int j = 0; j < i; ++j) {
				ItemStack itemstack = inv.getStackInSlot(j);

				if (!itemstack.isEmpty()) {
					this.inventory.setInventorySlotContents(j, itemstack.copy());
				}
			}
		}

		this.inventory.addListener(this);
		if (!this.world.isRemote) this.dataManager.set(SADDLE, !this.inventory.getStackInSlot(0).isEmpty());
	}
	
	public boolean isSaddled() {
		return this.dataManager.get(SADDLE);
	}

	@Override
	public void setTamed(boolean tamed) {
		super.setTamed(tamed);
		if (tamed) this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(60);
		else this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(40);
	}

	@Override
	public boolean canBeSteered() {
		return !inventory.getStackInSlot(0).isEmpty();
	}

	@Override
	public void updatePassenger(Entity passenger) {
		if (this.isPassenger(passenger)) {
			if (world.isRemote && passenger instanceof PlayerEntity && Minecraft.getInstance().player.isSneaking()) {
				removePassenger(passenger);
				return;
			}
			float f;
			int i = this.getPassengers().indexOf(passenger);
			if (i == 0) f = 0.2F;
			else f = -0.6F;
			Vector3d vec3d = (new Vector3d(f, 0.0D, 0.0D)).rotateYaw(-this.rotationYaw * 0.017453292F - ((float) Math.PI / 2F));
			passenger.setPosition(this.getPosX() + vec3d.x, this.getPosY() + this.getMountedYOffset() + passenger.getYOffset(), this.getPosZ() + vec3d.z);
		}
	}

	public void travel(Vector3d motion) {
		if (this.getControllingPassenger() != null && this.canBeSteered() && !inventory.getStackInSlot(0).isEmpty()) {
			LivingEntity entitylivingbase = (LivingEntity) this.getControllingPassenger();
			this.rotationYaw = entitylivingbase.rotationYaw;
			this.prevRotationYaw = this.rotationYaw;
			this.rotationPitch = entitylivingbase.rotationPitch * 0.5F;
			this.setRotation(this.rotationYaw, this.rotationPitch);
			this.renderYawOffset = this.rotationYaw;
			this.rotationYawHead = this.renderYawOffset;
			motion = new Vector3d(entitylivingbase.moveStrafing * 0.5F, motion.y, entitylivingbase.moveForward);
			this.stepHeight = 1.0F;
			if (motion.z <= 0.0F) motion = motion.mul(0, 0, 0.25F);
			if (this.canPassengerSteer()) {
				this.setAIMoveSpeed((float) this.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
				super.travel(motion);
			} else if (entitylivingbase instanceof PlayerEntity) {
				setMotion(0, 0, 0);
			}

			this.prevLimbSwingAmount = this.limbSwingAmount;
			double d1 = this.getPosX() - this.prevPosX;
			double d0 = this.getPosZ() - this.prevPosZ;
			float f2 = MathHelper.sqrt(d1 * d1 + d0 * d0) * 4.0F;

			if (f2 > 1.0F) f2 = 1.0F;
			this.limbSwingAmount += (f2 - this.limbSwingAmount) * 0.4F;
			this.limbSwing += this.limbSwingAmount;
		} else super.travel(motion);
	}

	@Override
	public ActionResultType func_230254_b_(PlayerEntity p_230254_1_, Hand p_230254_2_) {
		ItemStack stack = p_230254_1_.getHeldItem(p_230254_2_);
		if (!this.hasChest() && stack.getItem() == Item.getItemFromBlock(Blocks.CHEST)) {
			this.setChested(true);
			this.playSound(SoundEvents.ENTITY_DONKEY_CHEST, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
			this.initChest();
			return ActionResultType.SUCCESS;
		}
		if (!this.isChild() && this.isTamed() && (this.isOwner(p_230254_1_) || this.getControllingPassenger() != null)) {
			if (p_230254_1_.isSneaking()) {
				p_230254_1_.openContainer(this);
				return ActionResultType.SUCCESS;
			}
			if (!this.inventory.getStackInSlot(0).isEmpty() && !p_230254_1_.isPassenger(this) && this.getPassengers().size() < 2) {
				p_230254_1_.startRiding(this);
				return ActionResultType.SUCCESS;
			}
		} else if (!this.isTamed() && stack.getItem() == ItemInit.RAVAGER_MILK.get()) {
			if (!p_230254_1_.abilities.isCreativeMode) stack.shrink(1);
			if (!p_230254_1_.abilities.isCreativeMode) p_230254_1_.addItemStackToInventory(new ItemStack(Items.BUCKET));
			if (!this.world.isRemote) {
				if (this.rand.nextInt(3) == 0 && !ForgeEventFactory.onAnimalTame(this, p_230254_1_)) {
					this.setTamedBy(p_230254_1_);
					this.isJumping = false;
					this.navigator.clearPath();
					this.playTameEffect(true);
					this.world.setEntityState(this, (byte) 7);
				} else {
					this.playTameEffect(false);
					this.world.setEntityState(this, (byte) 6);
				}
			}
			return ActionResultType.SUCCESS;
		}
		return super.func_230254_b_(p_230254_1_, p_230254_2_);
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
		return this.getHeight() + 0.1F;
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return stack.getItem() == ItemInit.CABBAGE.get();
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

	@Override
	public void onInventoryChanged(IInventory invBasic) {
		boolean flag = this.dataManager.get(SADDLE);
		if (!this.world.isRemote) this.dataManager.set(SADDLE, !this.inventory.getStackInSlot(0).isEmpty());
		if (this.ticksExisted > 20 && !flag && this.dataManager.get(SADDLE))
			this.playSound(SoundEvents.ENTITY_HORSE_SADDLE, 0.5F, 1.0F);
	}

	@Nullable
	@Override
	public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
		return new RavagerContainer(p_createMenu_1_, this, p_createMenu_3_);
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
		RavageAndCabbageRavagerEntity ravagerentity = EntityInit.RAVAGER.get().create(p_241840_1_);
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

	@OnlyIn(Dist.CLIENT)
	public Vector3d func_241205_ce_() {
		return new Vector3d(0.0D, (double)(0.6F * this.getEyeHeight()), (double)(this.getWidth() * 0.4F));
	}

}
