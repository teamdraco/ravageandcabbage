package teamdraco.ravagecabbage.common.entities;

import static net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.ItemBasedSteering;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PlayerRideable;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import teamdraco.ravagecabbage.common.items.IRavagerHornArmorItem;
import teamdraco.ravagecabbage.registry.RCEntities;
import teamdraco.ravagecabbage.registry.RCItems;

public class RCRavagerEntity extends TamableAnimal implements PlayerRideable, Saddleable {
	private static final Predicate<Entity> NO_RAVAGER_AND_ALIVE = (p_213685_0_) -> p_213685_0_.isAlive() && !(p_213685_0_ instanceof RCRavagerEntity);
	private static final EntityDataAccessor<Boolean> SADDLED = SynchedEntityData.defineId(RCRavagerEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> BOOST_TIME = SynchedEntityData.defineId(RCRavagerEntity.class, EntityDataSerializers.INT);
	private final ItemBasedSteering steering = new ItemBasedSteering(this.entityData, BOOST_TIME, SADDLED);
	private int stunTick;
	private int roarTick;
	public int attackTick;

	@SuppressWarnings("deprecation")
	public RCRavagerEntity(EntityType<? extends RCRavagerEntity> p_i50250_1_, Level p_i50250_2_) {
		super(p_i50250_1_, p_i50250_2_);
		this.maxUpStep = 1.0F;
		this.navigation = new Navigator(this, level);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(1, new FloatGoal(this));
		this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
		this.goalSelector.addGoal(3, new RCRavagerEntity.AttackGoal());
		this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.4D));
		this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
		this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Mob.class, 8.0F));
		this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
		this.targetSelector.addGoal(3, new HurtByTargetGoal(this, Raider.class, RCRavagerEntity.class).setAlertOthers());
		this.targetSelector.addGoal(4, new UntamedAttackGoal<>(this, Player.class, true));
		this.targetSelector.addGoal(5, new UntamedAttackGoal<>(this, AbstractVillager.class, true));
		this.targetSelector.addGoal(5, new UntamedAttackGoal<>(this, IronGolem.class, true));
	}

	public boolean isSaddled() {
		return this.entityData.get(SADDLED);
	}

	private void setSaddled(boolean isSaddled) {
		this.entityData.set(SADDLED, isSaddled);
	}

	public boolean hasHornArmor() {
		ItemStack itemStackHeadSlot = this.getItemBySlot(EquipmentSlot.HEAD);
		return (itemStackHeadSlot != null && !itemStackHeadSlot.isEmpty() && itemStackHeadSlot.getItem() instanceof IRavagerHornArmorItem);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(BOOST_TIME, 0);
		this.entityData.define(SADDLED, false);
	}

	@Override
	protected void dropEquipment() {
		super.dropEquipment();
		if (this.isSaddled()) {
			this.spawnAtLocation(Items.SADDLE);
		}
		if (this.hasHornArmor()) {
			ItemStack hornArmor = this.getItemBySlot(EquipmentSlot.HEAD);
			this.spawnAtLocation(hornArmor);
		}
	}

	@Override
	public Vec3 getDismountLocationForPassenger(LivingEntity livingEntity) {
		Vec3 vector3d = getCollisionHorizontalEscapeVector(this.getBbWidth(), livingEntity.getBbWidth(), this.yRot + (livingEntity.getMainArm() == HumanoidArm.RIGHT ? 90.0F : -90.0F));
		Vec3 vector3d1 = this.getDismountLocationInDirection(vector3d, livingEntity);
		if (vector3d1 != null) {
			return vector3d1;
		} else {
			Vec3 vector3d2 = getCollisionHorizontalEscapeVector(this.getBbWidth(), livingEntity.getBbWidth(), this.yRot + (livingEntity.getMainArm() == HumanoidArm.LEFT ? 90.0F : -90.0F));
			Vec3 vector3d3 = this.getDismountLocationInDirection(vector3d2, livingEntity);
			return vector3d3 != null ? vector3d3 : this.position();
		}
	}

	@Nullable
	private Vec3 getDismountLocationInDirection(Vec3 p_234236_1_, LivingEntity p_234236_2_) {
		double d0 = this.getX() + p_234236_1_.x;
		double d1 = this.getBoundingBox().minY;
		double d2 = this.getZ() + p_234236_1_.z;
		BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

		for(Pose pose : p_234236_2_.getDismountPoses()) {
			mutablePos.set(d0, d1, d2);
			double d3 = this.getBoundingBox().maxY + 0.75D;

			while(true) {
				double d4 = this.level.getBlockFloorHeight(mutablePos);
				if ((double)mutablePos.getY() + d4 > d3) {
					break;
				}

				if (DismountHelper.isBlockFloorValid(d4)) {
					AABB axisalignedbb = p_234236_2_.getLocalBoundsForPose(pose);
					Vec3 vector3d = new Vec3(d0, (double)mutablePos.getY() + d4, d2);
					if (DismountHelper.canDismountTo(this.level, p_234236_2_, axisalignedbb.move(vector3d))) {
						p_234236_2_.setPose(pose);
						return vector3d;
					}
				}

				mutablePos.move(Direction.UP);
				if (!((double)mutablePos.getY() < d3)) {
					break;
				}
			}
		}

		return null;
	}

	@Override
	public boolean canBeControlledByRider() {
		return this.getControllingPassenger() instanceof LivingEntity;
	}

	@Override
	protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
		return isBaby() ? 0.7F : 1.3F;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("AttackTick", this.attackTick);
		compound.putInt("StunTick", this.stunTick);
		compound.putInt("RoarTick", this.roarTick);
		compound.putBoolean("IsSaddled", this.isSaddled());


		ItemStack itemStackHead = this.getItemBySlot(EquipmentSlot.HEAD);
		if(!itemStackHead.isEmpty()) {
			CompoundTag headCompoundNBT = new CompoundTag();
			itemStackHead.save(headCompoundNBT);
			compound.put("HeadSlot", headCompoundNBT);
		}

	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		this.attackTick = compound.getInt("AttackTick");
		this.stunTick = compound.getInt("StunTick");
		this.roarTick = compound.getInt("RoarTick");
		this.setSaddled(compound.getBoolean("IsSaddled"));

		CompoundTag compoundNBT = compound.getCompound("HeadSlot");
		boolean hasHornArmor = !compoundNBT.isEmpty();
		if(hasHornArmor) {
			this.setItemSlot(EquipmentSlot.HEAD, ItemStack.of(compoundNBT));
		}
	}

	@Override
	public void setTame(boolean tamed) {
		super.setTame(tamed);
		this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(160);
		this.setHealth(160);
	}

	@Override
	public void positionRider(Entity passenger) {
		if (this.hasPassenger(passenger)) {
			float f;
			int i = this.getPassengers().indexOf(passenger);
			if (i == 0) f = 0.2F;
			else f = -0.6F;
			Vec3 vec3d = (new Vec3(f, 0.0D, 0.0D)).yRot(-this.yRot * 0.017453292F - ((float) Math.PI / 2F));
			passenger.setPos(this.getX() + vec3d.x, this.getY() + this.getPassengersRidingOffset() + passenger.getMyRidingOffset(), this.getZ() + vec3d.z);
		}
	}

	@Override
	protected boolean canAddPassenger(Entity passenger) {
		return this.getPassengers().size() < 2;
	}

	@Nullable
	public Entity getControllingPassenger() {
		List<Entity> list = this.getPassengers();
		return list.isEmpty() ? null : list.get(0);
	}

	@Override
	public double getPassengersRidingOffset() {
		return this.getBbHeight() - 0.2F;
	}

	@Override
	public boolean isFood(ItemStack stack) {
		return stack.getItem() == RCItems.CABBAGE.get();
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.RAVAGER_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.RAVAGER_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.RAVAGER_DEATH;
	}

	@Override
	public float getEyeHeight(Pose pose) {
		return this.isBaby() ? this.getBbHeight() : 2.0F;
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 100.0D).add(Attributes.MOVEMENT_SPEED, 0.3D).add(Attributes.KNOCKBACK_RESISTANCE, 0.75D).add(Attributes.ATTACK_DAMAGE, 6.0D).add(Attributes.ATTACK_KNOCKBACK, 1.5D).add(Attributes.FOLLOW_RANGE, 32.0D).add(Attributes.ARMOR, 0D);
	}

	@Override
	protected float getSoundVolume() {
		return 0.4F;
	}

	@Override
	public boolean doHurtTarget(Entity entityIn) {
		this.level.broadcastEntityEvent(this, (byte)4);
		return super.doHurtTarget(entityIn);
	}

	@Override
	public void aiStep() {
		super.aiStep();
		if (this.isAlive()) {
			if (this.isImmobile()) {
				this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0D);
			} else {
				double d0 = this.getTarget() != null ? 0.35D : 0.3D;
				double d1 = this.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue();
				this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(Mth.lerp(0.1D, d1, d0));
			}

			if (!level.isClientSide && getMobGriefingEvent(this.level, this)) {
				boolean flag = false;
				AABB axisalignedbb = this.getBoundingBox().inflate(0.2D);

				for(BlockPos blockpos : BlockPos.betweenClosed(Mth.floor(axisalignedbb.minX), Mth.floor(axisalignedbb.minY), Mth.floor(axisalignedbb.minZ), Mth.floor(axisalignedbb.maxX), Mth.floor(axisalignedbb.maxY), Mth.floor(axisalignedbb.maxZ))) {
					BlockState blockstate = this.level.getBlockState(blockpos);
					Block block = blockstate.getBlock();
					if (block instanceof LeavesBlock) {
						flag = this.level.destroyBlock(blockpos, true, this) || flag;
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
				this.stunEffect();
				if (this.stunTick == 0) {
					this.playSound(SoundEvents.RAVAGER_ROAR, 1.0F, 1.0F);
					this.roarTick = 20;
				}
			}
		}
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else {
			Entity entity = source.getEntity();
			this.setOrderedToSit(false);
			if (entity != null && !(entity instanceof Player) && !(entity instanceof AbstractArrow)) {
				amount = (amount + 1.0F) / 2.0F;
			}

			return super.hurt(source, amount);
		}
	}

	protected boolean isImmobile() {
		return super.isImmobile() || this.attackTick > 0 || this.stunTick > 0 || this.roarTick > 0;
	}

	@Override
	public boolean hasLineOfSight(Entity entityIn) {
		return this.stunTick <= 0 && this.roarTick <= 0 && super.hasLineOfSight(entityIn);
	}

	@Override
	protected void blockedByShield(LivingEntity entityIn) {
		if (this.roarTick == 0) {
			if (this.random.nextDouble() < 0.5D) {
				this.stunTick = 40;
				this.playSound(SoundEvents.RAVAGER_STUNNED, 1.0F, 1.0F);
				this.level.broadcastEntityEvent(this, (byte)39);
				entityIn.push(this);
			} else {
				this.strongKnockback(entityIn);
			}

			entityIn.hurtMarked = true;
		}

	}

	public void roar() {
		if (this.isAlive()) {
			for(Entity entity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(4.0D), NO_RAVAGER_AND_ALIVE)) {
				if (!(entity instanceof AbstractIllager)) {
					entity.hurt(DamageSource.mobAttack(this), 6.0F);
				}

				this.strongKnockback(entity);
			}

			Vec3 vec3d = this.getBoundingBox().getCenter();

			for(int i = 0; i < 40; ++i) {
				double d0 = this.random.nextGaussian() * 0.2D;
				double d1 = this.random.nextGaussian() * 0.2D;
				double d2 = this.random.nextGaussian() * 0.2D;
				this.level.addParticle(ParticleTypes.POOF, vec3d.x, vec3d.y, vec3d.z, d0, d1, d2);
			}
		}
	}

	private void stunEffect() {
		if (this.random.nextInt(6) == 0) {
			double d0 = this.getX() - (double)this.getBbWidth() * Math.sin((this.yBodyRot * ((float)Math.PI / 180F))) + (this.random.nextDouble() * 0.6D - 0.3D);
			double d1 = this.getY() + (double)this.getBbHeight() - 0.3D;
			double d2 = this.getZ() + (double)this.getBbWidth() * Math.cos((this.yBodyRot * ((float)Math.PI / 180F))) + (this.random.nextDouble() * 0.6D - 0.3D);
			this.level.addParticle(ParticleTypes.ENTITY_EFFECT, d0, d1, d2, 0.4980392156862745D, 0.5137254901960784D, 0.5725490196078431D);
		}

	}

	private void strongKnockback(Entity p_33340_) {
		double d0 = p_33340_.getX() - this.getX();
		double d1 = p_33340_.getZ() - this.getZ();
		double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
		p_33340_.push(d0 / d2 * 4.0D, 0.2D, d1 / d2 * 4.0D);
	}

	@Override
	public RCRavagerEntity getBreedOffspring(ServerLevel p_241840_1_, AgeableMob p_241840_2_) {
		RCRavagerEntity ravagerentity = RCEntities.RAVAGER.get().create(p_241840_1_);
		UUID uuid = this.getOwnerUUID();
		if (uuid != null) {
			ravagerentity.setOwnerUUID(uuid);
			ravagerentity.setTame(true);
		}
		return ravagerentity;
	}

	@OnlyIn(Dist.CLIENT)
	public void handleEntityEvent(byte id) {
		if (id == 4) {
			this.attackTick = 10;
			this.playSound(SoundEvents.RAVAGER_ATTACK, 1.0F, 1.0F);
		} else if (id == 39) {
			this.stunTick = 40;
		}

		super.handleEntityEvent(id);
	}

	@OnlyIn(Dist.CLIENT)
	public int getAttackTick() {
		return this.attackTick;
	}

	@OnlyIn(Dist.CLIENT)
	public int getStunnedTick() {
		return this.stunTick;
	}

	@OnlyIn(Dist.CLIENT)
	public int getRoarTick() {
		return this.roarTick;
	}

	@Override
	public void travel(Vec3 travelVector) {
		if (this.isAlive()) {
			if (this.isVehicle() && this.canBeControlledByRider() && this.isSaddled()) {
				LivingEntity livingentity = (LivingEntity)this.getControllingPassenger();
				this.yRot = livingentity.yRot;
				this.yRotO = this.yRot;
				this.xRot = livingentity.xRot * 0.5F;
				this.setRot(this.yRot, this.xRot);
				this.yBodyRot = this.yRot;
				this.yHeadRot = this.yBodyRot;
				float f = livingentity.xxa * 0.5F;
				float f1 = livingentity.zza;
				if (f1 <= 0.0F) {
					f1 *= 0.5F;
				}

				if (this.isControlledByLocalInstance()) {
					this.setSpeed((float)this.getAttributeValue(Attributes.MOVEMENT_SPEED) / 2);
					super.travel(new Vec3(f, travelVector.y, f1));
				} else if (livingentity instanceof Player) {
					this.setDeltaMovement(Vec3.ZERO);
				}

				this.calculateEntityAnimation(this, false);
			} else {
				super.travel(travelVector);
			}
		}
	}


	public void setItemSlot(EquipmentSlot slotIn, ItemStack itemStack) {
		super.setItemSlot(slotIn, itemStack);

		if(slotIn == EquipmentSlot.HEAD && !itemStack.isEmpty()) {
			Item item = itemStack.getItem();
			if(item instanceof IRavagerHornArmorItem hornArmor) {
				this.getAttribute(Attributes.ARMOR).setBaseValue(hornArmor.getArmorValue());
				// Update this Entity's Atttributes.ARMOR base
			}
		}
	}

	@Override
	public ItemStack getPickedResult(HitResult target) {
		return new ItemStack(RCItems.RAVAGER_SPAWN_EGG.get());
	}

	@Override
	public InteractionResult mobInteract(Player player, InteractionHand hand) {
		ItemStack itemstack = player.getItemInHand(hand);
		Item item = itemstack.getItem();
		if (item == RCItems.RAVAGER_MILK.get() && !this.isTame()) {
			if (!player.isCreative()) {
				itemstack.shrink(1);
				ItemStack itemstack1 = ItemUtils.createFilledResult(itemstack, player, Items.BUCKET.getDefaultInstance());
				player.setItemInHand(hand, itemstack1);
			}
			if (this.random.nextInt(3) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, player)) {
				this.tame(player);
				this.navigation.stop();
				this.setTarget(null);
				this.setOrderedToSit(true);
				this.level.broadcastEntityEvent(this, (byte)7);
			} else {
				this.level.broadcastEntityEvent(this, (byte)6);
			}
			return InteractionResult.SUCCESS;
		} else if (this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
			if (!player.isCreative()) {
				itemstack.shrink(1);
			}
			this.heal(3F);
			return InteractionResult.SUCCESS;
		} else if (!this.isSaddled() && this.isTame() && !this.isBaby() && item == Items.SADDLE) {
			if (!player.isCreative()) {
				itemstack.shrink(1);
			}
			this.setSaddled(true);
			level.playSound(player, this, SoundEvents.PIG_SADDLE, SoundSource.AMBIENT, 0.5F, 1.0F);
			return InteractionResult.CONSUME;


		} else if (!this.hasHornArmor() && this.isTame() && !this.isBaby() && item instanceof IRavagerHornArmorItem) {

			this.setItemSlot(EquipmentSlot.HEAD, itemstack.copy());
			if (!player.isCreative()) {
				itemstack.shrink(1);
			}

			IRavagerHornArmorItem hornArmorItem = (IRavagerHornArmorItem)item;
			level.playSound(player, this, hornArmorItem.getArmorMaterial().getEquipSound(), SoundSource.AMBIENT, 0.5F, 1.0F);

			return InteractionResult.CONSUME;
		} else if (player.isSecondaryUseActive() && this.hasHornArmor() && this.isTame() && !this.isBaby() && itemstack.isEmpty()) {

			// Drop active HornArmorItem when Player is sneaking & has empty hand
			// Later we can remove this when this Entity has its own INamedContainerProvider like a Horse
			// so HornArmorItems can be set via drag'n drop
			ItemStack itemStack = this.getItemBySlot(EquipmentSlot.HEAD);
			if(!itemStack.isEmpty()) {
				this.spawnAtLocation(itemStack.copy());
				this.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
			}
			return InteractionResult.CONSUME;

		} else if (this.isSaddled() && item == Items.AIR) {
			player.startRiding(this);
			return InteractionResult.SUCCESS;
		} else if (this.isTame() && item == Items.BUCKET && !this.isBaby()) {
			player.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);
			ItemStack itemstack1 = ItemUtils.createFilledResult(itemstack, player, RCItems.RAVAGER_MILK.get().getDefaultInstance());
			player.setItemInHand(hand, itemstack1);
			return InteractionResult.sidedSuccess(this.level.isClientSide);
		}
		return super.mobInteract(player, hand);
	}

	@Override
	public boolean isSaddleable() {
		return this.isAlive() && !this.isBaby() && this.isTame();
	}

	@Override
	public void equipSaddle(SoundSource source) {
		this.steering.setSaddle(true);
		if (source != null) {
			this.level.playSound((Player)null, this, SoundEvents.STRIDER_SADDLE, source, 0.5F, 1.0F);
		}
	}

	@Override
	public boolean wantsToAttack(LivingEntity target, LivingEntity owner) {
		if (!(target instanceof Creeper) && !(target instanceof Ghast)) {
			if (target instanceof RCRavagerEntity) {
				RCRavagerEntity ravager = (RCRavagerEntity)target;
				return !ravager.isTame() || ravager.getOwner() != owner;
			} else if (target instanceof Player && owner instanceof Player && !((Player)owner).canHarmPlayer((Player)target)) {
				return false;
			} else if (target instanceof AbstractHorse && ((AbstractHorse)target).isTamed()) {
				return false;
			} else {
				return !(target instanceof TamableAnimal) || !((TamableAnimal)target).isTame();
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
			float f = RCRavagerEntity.this.getBbWidth() - 0.1F;
			return (f * 2.0F * f * 2.0F + attackTarget.getBbWidth());
		}
	}

	static class Navigator extends GroundPathNavigation {
		public Navigator(Mob p_i50754_1_, Level p_i50754_2_) {
			super(p_i50754_1_, p_i50754_2_);
		}

		protected PathFinder createPathFinder(int p_179679_1_) {
			this.nodeEvaluator = new RCRavagerEntity.Processor();
			return new PathFinder(this.nodeEvaluator, p_179679_1_);
		}
	}

	static class Processor extends WalkNodeEvaluator {
		private Processor() {
		}

		protected BlockPathTypes evaluateBlockPathType(BlockGetter p_215744_1_, boolean p_215744_2_, boolean p_215744_3_, BlockPos p_215744_4_, BlockPathTypes p_215744_5_) {
			return p_215744_5_ == BlockPathTypes.LEAVES ? BlockPathTypes.OPEN : super.evaluateBlockPathType(p_215744_1_, p_215744_2_, p_215744_3_, p_215744_4_, p_215744_5_);
		}
	}

	static class UntamedAttackGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {

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

		public boolean canUse() {
			if (super.canUse() && !goalOwner.isBaby() && !goalOwner.isTame()) {
				return true;
			} else {
				return false;
			}
		}

		public void stop() {
			super.stop();
		}

		public boolean canContinueToUse() {
			if (super.canContinueToUse()) {
				return true;
			} else {
				return false;
			}
		}

	}
}