package superlord.ravagecabbage.entity;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.BoostHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEquipable;
import net.minecraft.entity.IRideable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
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
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.AbstractIllagerEntity;
import net.minecraft.entity.monster.AbstractRaiderEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
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
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import superlord.ravagecabbage.init.RCEntities;
import superlord.ravagecabbage.init.RCItems;
import superlord.ravagecabbage.items.IRavagerHornArmorItem;

import static net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent;

public class RCRavagerEntity extends TameableEntity implements IRideable, IEquipable {
	private static final Predicate<Entity> field_213690_b = (p_213685_0_) -> {
		return p_213685_0_.isAlive() && !(p_213685_0_ instanceof RCRavagerEntity);
	};
	private static final DataParameter<Boolean> SADDLED = EntityDataManager.createKey(RCRavagerEntity.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> HORN_ARMOR = EntityDataManager.createKey(RCRavagerEntity.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> BOOST_TIME = EntityDataManager.createKey(RCRavagerEntity.class, DataSerializers.VARINT);
	public int attackTick;
	private int stunTick;
	private int roarTick;
	private final BoostHelper field_234214_bx_ = new BoostHelper(this.dataManager, BOOST_TIME, SADDLED);

	public RCRavagerEntity(EntityType<? extends RCRavagerEntity> p_i50250_1_, World p_i50250_2_) {
		super(p_i50250_1_, p_i50250_2_);
		this.stepHeight = 1.0F;
		this.navigator = new Navigator(this, world);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(1, new SwimGoal(this));
		this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
		this.goalSelector.addGoal(3, new RCRavagerEntity.AttackGoal());
		this.goalSelector.addGoal(4, new WaterAvoidingRandomWalkingGoal(this, 0.4D));
		this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.addGoal(6, new LookAtGoal(this, MobEntity.class, 8.0F));
		this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
		this.targetSelector.addGoal(3, new HurtByTargetGoal(this, AbstractRaiderEntity.class, RCRavagerEntity.class).setCallsForHelp());
		this.targetSelector.addGoal(4, new UntamedAttackGoal<>(this, PlayerEntity.class, true));
		this.targetSelector.addGoal(5, new UntamedAttackGoal<>(this, AbstractVillagerEntity.class, true));
		this.targetSelector.addGoal(5, new UntamedAttackGoal<>(this, IronGolemEntity.class, true));
	}

	public boolean isSaddled() {
		return this.dataManager.get(SADDLED);
	}

	private void setSaddled(boolean isSaddled) {
		this.dataManager.set(SADDLED, isSaddled);
	}

	public boolean hasHornArmor() {
		return this.dataManager.get(HORN_ARMOR);
	}

	public void setHasHornArmor(boolean hasHornArmor) {
		this.dataManager.set(HORN_ARMOR, hasHornArmor);
	}

	@Override
	protected void registerData() {
		super.registerData();
		this.dataManager.register(BOOST_TIME, 0);
		this.dataManager.register(SADDLED, false);
		this.dataManager.register(HORN_ARMOR, false);
	}

	@Override
	protected void dropInventory() {
		super.dropInventory();
		if (this.isSaddled()) {
			this.entityDropItem(Items.SADDLE);
		}
		if (this.hasHornArmor()) {
			ItemStack hornArmor = this.getItemStackFromSlot(EquipmentSlotType.HEAD);
			this.entityDropItem(hornArmor);
		}
	}

	@Override
	public Vector3d func_230268_c_(LivingEntity livingEntity) {
		Vector3d vector3d = func_233559_a_(this.getWidth(), livingEntity.getWidth(), this.rotationYaw + (livingEntity.getPrimaryHand() == HandSide.RIGHT ? 90.0F : -90.0F));
		Vector3d vector3d1 = this.func_234236_a_(vector3d, livingEntity);
		if (vector3d1 != null) {
			return vector3d1;
		} else {
			Vector3d vector3d2 = func_233559_a_(this.getWidth(), livingEntity.getWidth(), this.rotationYaw + (livingEntity.getPrimaryHand() == HandSide.LEFT ? 90.0F : -90.0F));
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

	@Override
	public float getMountedSpeed() {
		return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED);
	}

	@Override
	public boolean canBeSteered() {
		return this.getControllingPassenger() instanceof LivingEntity;
	}

	@Override
	protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
		return isChild() ? 0.7F : 1.3F;
	}

	@Override
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		compound.putInt("AttackTick", this.attackTick);
		compound.putInt("StunTick", this.stunTick);
		compound.putInt("RoarTick", this.roarTick);
		compound.putBoolean("IsSaddled", this.isSaddled());


		ItemStack itemStackHead = this.getItemStackFromSlot(EquipmentSlotType.HEAD);
		if(!itemStackHead.isEmpty()) {
			CompoundNBT headCompoundNBT = new CompoundNBT();
			itemStackHead.write(headCompoundNBT);
			compound.put("HeadSlot", headCompoundNBT);
		}

	}

	@Override
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		this.attackTick = compound.getInt("AttackTick");
		this.stunTick = compound.getInt("StunTick");
		this.roarTick = compound.getInt("RoarTick");
		this.setSaddled(compound.getBoolean("IsSaddled"));

		CompoundNBT compoundNBT = compound.getCompound("HeadSlot");
		boolean hasHornArmor = !compoundNBT.isEmpty();
		if(hasHornArmor) {
			this.setItemStackToSlot(EquipmentSlotType.HEAD, ItemStack.read(compoundNBT));
		}
		this.setHasHornArmor(hasHornArmor);
	}

	@Override
	public void setTamed(boolean tamed) {
		super.setTamed(tamed);
		this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(160);
		this.setHealth(160);
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

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_RAVAGER_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.ENTITY_RAVAGER_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_RAVAGER_DEATH;
	}

	@Override
	public float getEyeHeight(Pose pose) {
		return this.isChild() ? this.getHeight() : 2.0F;
	}

	public static AttributeModifierMap.MutableAttribute func_234233_eS_() {
		return MobEntity.func_233666_p_().createMutableAttribute(Attributes.MAX_HEALTH, 100.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3D).createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 0.75D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 6.0D).createMutableAttribute(Attributes.ATTACK_KNOCKBACK, 1.5D).createMutableAttribute(Attributes.FOLLOW_RANGE, 32.0D).createMutableAttribute(Attributes.ARMOR, 0D);
	}

	@Override
	protected float getSoundVolume() {
		return 0.4F;
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		this.world.setEntityState(this, (byte)4);
		return super.attackEntityAsMob(entityIn);
	}

	@Override
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

			if (!world.isRemote && getMobGriefingEvent(this.world, this)) {
				boolean flag = false;
				AxisAlignedBB axisalignedbb = this.getBoundingBox().grow(0.2D);

				for(BlockPos blockpos : BlockPos.getAllInBoxMutable(MathHelper.floor(axisalignedbb.minX), MathHelper.floor(axisalignedbb.minY), MathHelper.floor(axisalignedbb.minZ), MathHelper.floor(axisalignedbb.maxX), MathHelper.floor(axisalignedbb.maxY), MathHelper.floor(axisalignedbb.maxZ))) {
					BlockState blockstate = this.world.getBlockState(blockpos);
					Block block = blockstate.getBlock();
					if (block instanceof LeavesBlock) {
						flag = this.world.destroyBlock(blockpos, true, this) || flag;
					}
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
	}

	@Override
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

	@Override
	public boolean canEntityBeSeen(Entity entityIn) {
		return this.stunTick <= 0 && this.roarTick <= 0 && super.canEntityBeSeen(entityIn);
	}

	@Override
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

	public void roar() {
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
			double d0 = this.getPosX() - (double)this.getWidth() * Math.sin((this.renderYawOffset * ((float)Math.PI / 180F))) + (this.rand.nextDouble() * 0.6D - 0.3D);
			double d1 = this.getPosY() + (double)this.getHeight() - 0.3D;
			double d2 = this.getPosZ() + (double)this.getWidth() * Math.cos((this.renderYawOffset * ((float)Math.PI / 180F))) + (this.rand.nextDouble() * 0.6D - 0.3D);
			this.world.addParticle(ParticleTypes.ENTITY_EFFECT, d0, d1, d2, 0.4980392156862745D, 0.5137254901960784D, 0.5725490196078431D);
		}

	}

	private void launch(Entity p_213688_1_) {
		double d0 = p_213688_1_.getPosX() - this.getPosX();
		double d1 = p_213688_1_.getPosZ() - this.getPosZ();
		double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
		p_213688_1_.addVelocity(d0 / d2 * 4.0D, 0.2D, d1 / d2 * 4.0D);
	}

	@Override
	public RCRavagerEntity func_241840_a(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
		RCRavagerEntity ravagerentity = RCEntities.RAVAGER.get().create(p_241840_1_);
		UUID uuid = this.getOwnerId();
		if (uuid != null) {
			ravagerentity.setOwnerId(uuid);
			ravagerentity.setTamed(true);
		}
		return ravagerentity;
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

	@Override
	public void travel(Vector3d travelVector) {
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
					f1 *= 0.5F;
				}

				if (this.canPassengerSteer()) {
					this.setAIMoveSpeed((float)this.getAttributeValue(Attributes.MOVEMENT_SPEED) / 2);
					super.travel(new Vector3d(f, travelVector.y, f1));
				} else if (livingentity instanceof PlayerEntity) {
					this.setMotion(Vector3d.ZERO);
				}

				this.func_233629_a_(this, false);
			} else {
				super.travel(travelVector);
			}
		}
	}


	public void setItemStackToSlot(EquipmentSlotType slotIn, ItemStack itemStack) {
		super.setItemStackToSlot(slotIn, itemStack);

		if(slotIn == EquipmentSlotType.HEAD && itemStack != null && !itemStack.isEmpty()) {
			Item item = itemStack.getItem();
			if(item instanceof IRavagerHornArmorItem) {
				IRavagerHornArmorItem hornArmor = (IRavagerHornArmorItem)item;

				// and update this Entity's Atttributes.ARMOR base
				this.getAttribute(Attributes.ARMOR).setBaseValue((double) hornArmor.getArmorValue());
			}
		}
	}

	@Override
	public ItemStack getPickedResult(RayTraceResult target) {
		return new ItemStack(RCItems.RAVAGER_SPAWN_EGG.get());
	}

	@Override
	public ActionResultType func_230254_b_(PlayerEntity player, Hand hand) {
		ItemStack itemstack = player.getHeldItem(hand);
		Item item = itemstack.getItem();
		if (item == RCItems.RAVAGER_MILK.get() && !this.isTamed()) {
			if (!player.abilities.isCreativeMode) {
				itemstack.shrink(1);
				ItemStack itemstack1 = DrinkHelper.fill(itemstack, player, Items.BUCKET.getDefaultInstance());
				player.setHeldItem(hand, itemstack1);
			}
			if (this.rand.nextInt(3) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, player)) {
				this.setTamedBy(player);
				this.navigator.clearPath();
				this.setAttackTarget(null);
				this.func_233687_w_(true);
				this.world.setEntityState(this, (byte)7);
			} else {
				this.world.setEntityState(this, (byte)6);
			}
			return ActionResultType.SUCCESS;
		} else if (this.isBreedingItem(itemstack) && this.getHealth() < this.getMaxHealth()) {
			if (!player.abilities.isCreativeMode) {
				itemstack.shrink(1);
			}
			this.heal(3F);
			return ActionResultType.SUCCESS;
		} else if (!this.isSaddled() && this.isTamed() && !this.isChild() && item == Items.SADDLE) {
			if (!player.abilities.isCreativeMode) {
				itemstack.shrink(1);
			}
			this.setSaddled(true);
			world.playMovingSound(player, this, SoundEvents.ENTITY_PIG_SADDLE, SoundCategory.AMBIENT, 0.5F, 1.0F);
			return ActionResultType.CONSUME;


		} else if ((!this.hasHornArmor() || this.getItemStackFromSlot(EquipmentSlotType.HEAD).isEmpty()) && this.isTamed() && !this.isChild() && item instanceof IRavagerHornArmorItem) {

			this.setItemStackToSlot(EquipmentSlotType.HEAD, itemstack.copy());
			if (!player.abilities.isCreativeMode) {
				itemstack.shrink(1);
			}

			IRavagerHornArmorItem hornArmorItem = (IRavagerHornArmorItem)item;
			world.playMovingSound(player, this, hornArmorItem.getArmorMaterial().getSoundEvent(), SoundCategory.AMBIENT, 0.5F, 1.0F);

			return ActionResultType.CONSUME;
		} else if (this.isSaddled() && item == Items.AIR) {
			player.startRiding(this);
			return ActionResultType.SUCCESS;
		} else if (this.isTamed() && item == Items.BUCKET && !this.isChild()) {
			player.playSound(SoundEvents.ENTITY_COW_MILK, 1.0F, 1.0F);
			ItemStack itemstack1 = DrinkHelper.fill(itemstack, player, RCItems.RAVAGER_MILK.get().getDefaultInstance());
			player.setHeldItem(hand, itemstack1);
			return ActionResultType.func_233537_a_(this.world.isRemote);
		}
		return super.func_230254_b_(player, hand);
	}

	@Override
	public boolean func_230264_L__() {
		return this.isAlive() && !this.isChild();
	}

	@Override
	public void func_230266_a_(SoundCategory p_230266_1_) {

	}

	@Override
	public boolean isHorseSaddled() {
		return this.isSaddled();
	}

	@Override
	public boolean boost() {
		return this.field_234214_bx_.boost(this.getRNG());
	}

	@Override
	public void travelTowards(Vector3d travelVec) {
		super.travel(travelVec);		
	}

/*
	public boolean isHornArmor(ItemStack stack) {
		return stack.getItem() instanceof RavagerHornArmorItem;
	}
*/

	@Override
	public boolean shouldAttackEntity(LivingEntity target, LivingEntity owner) {
		if (!(target instanceof CreeperEntity) && !(target instanceof GhastEntity)) {
			if (target instanceof RCRavagerEntity) {
				RCRavagerEntity ravager = (RCRavagerEntity)target;
				return !ravager.isTamed() || ravager.getOwner() != owner;
			} else if (target instanceof PlayerEntity && owner instanceof PlayerEntity && !((PlayerEntity)owner).canAttackPlayer((PlayerEntity)target)) {
				return false;
			} else if (target instanceof AbstractHorseEntity && ((AbstractHorseEntity)target).isTame()) {
				return false;
			} else {
				return !(target instanceof TameableEntity) || !((TameableEntity)target).isTamed();
			}
		} else {
			return false;
		}
	}

	class AttackGoal extends MeleeAttackGoal {
		public AttackGoal() {
			super(RCRavagerEntity.this, 1.0D, true);
		}

		protected double getAttackReachSqr(LivingEntity attackTarget) {
			float f = RCRavagerEntity.this.getWidth() - 0.1F;
			return (f * 2.0F * f * 2.0F + attackTarget.getWidth());
		}
	}

	static class Navigator extends GroundPathNavigator {
		public Navigator(MobEntity p_i50754_1_, World p_i50754_2_) {
			super(p_i50754_1_, p_i50754_2_);
		}

		protected PathFinder getPathFinder(int p_179679_1_) {
			this.nodeProcessor = new RCRavagerEntity.Processor();
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

	class UntamedAttackGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {

		RCRavagerEntity goalOwner;

		public UntamedAttackGoal(RCRavagerEntity goalOwnerIn, Class<T> targetClassIn, boolean checkSight) {
			this(goalOwnerIn, targetClassIn, checkSight, false);
		}

		public UntamedAttackGoal(RCRavagerEntity goalOwnerIn, Class<T> targetClassIn, boolean checkSight, boolean nearbyOnlyIn) {
			this(goalOwnerIn, targetClassIn, 10, checkSight, nearbyOnlyIn, (Predicate<LivingEntity>)null);
		}

		public UntamedAttackGoal(RCRavagerEntity goalOwnerIn, Class<T> targetClassIn, int targetChanceIn, boolean checkSight, boolean nearbyOnlyIn, @Nullable Predicate<LivingEntity> targetPredicate) {
			super(goalOwnerIn, targetClassIn, targetChanceIn, checkSight, nearbyOnlyIn, targetPredicate);
			this.goalOwner = goalOwnerIn;
		}

		public boolean shouldExecute() {
			if (super.shouldExecute() && !goalOwner.isChild() && !goalOwner.isTamed()) {
				return true;
			} else {
				return false;
			}
		}

		public void resetTask() {
			super.resetTask();
		}

		public boolean shouldContinueExecuting() {
			if (super.shouldContinueExecuting()) {
				return true;
			} else {
				return false;
			}
		}

	}
}