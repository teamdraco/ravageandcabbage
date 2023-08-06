package codyhuh.ravagecabbage.common.entities;

import java.util.Random;

import javax.annotation.Nullable;

import codyhuh.ravagecabbage.common.entities.item.CabbageItemEntity;
import codyhuh.ravagecabbage.common.entities.item.CorruptedCabbageItemEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PathfindToRaidGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import codyhuh.ravagecabbage.registry.RCItems;

public class CabbagerEntity extends AbstractIllager implements RangedAttackMob {
	private final SimpleContainer inventory = new SimpleContainer(5);

	public CabbagerEntity(EntityType<? extends CabbagerEntity> type, Level worldIn) {
		super(type, worldIn);
	}

	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.25D, 0, 40, 10.0F));
		this.goalSelector.addGoal(2, new PathfindToRaidGoal<>(this));
		this.goalSelector.addGoal(3, new RandomStrollGoal(this, 0.6D));
		this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 15.0F, 1.0F));
		this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Mob.class, 15.0F));
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Raider.class).setAlertOthers());
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false, e -> !(e instanceof CorruptedVillager)));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.MOVEMENT_SPEED, 0.2D);
	}

	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		ListTag listnbt = new ListTag();

		for(int i = 0; i < this.inventory.getContainerSize(); ++i) {
			ItemStack itemstack = this.inventory.getItem(i);
			if (!itemstack.isEmpty()) {
				listnbt.add(itemstack.save(new CompoundTag()));
			}
		}
	}

	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		ListTag listnbt = compound.getList("Inventory", 10);

		for(int i = 0; i < listnbt.size(); ++i) {
			ItemStack itemstack = ItemStack.of(listnbt.getCompound(i));
			if (!itemstack.isEmpty()) {
				this.inventory.addItem(itemstack);
			}
		}
	}

	public void performRangedAttack(LivingEntity target, float distanceFactor) {
		Random random = new Random();
		int corruptionChance = random.nextInt(3);
		if (target.getType() == EntityType.VILLAGER && corruptionChance == 0) {
			CorruptedCabbageItemEntity cabbageEntity = new CorruptedCabbageItemEntity(this.level(), this);
			double d0 = target.getEyeY() - (double)1.1F;
			double d1 = target.getX() - this.getX();
			double d2 = d0 - cabbageEntity.getY();
			double d3 = target.getZ() - this.getZ();
			float f = Mth.sqrt((float) (d1 * d1 + d3 * d3)) * 0.2F;
			cabbageEntity.shoot(d1, d2 + (double)f, d3, 1.6F, 12.0F);
			this.playSound(SoundEvents.SNOW_GOLEM_SHOOT, 1.0F, 0.4F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
			this.level().addFreshEntity(cabbageEntity);
		} else {
			CabbageItemEntity cabbageEntity = new CabbageItemEntity(this.level(), this);
			double d0 = target.getEyeY() - (double)1.1F;
			double d1 = target.getX() - this.getX();
			double d2 = d0 - cabbageEntity.getY();
			double d3 = target.getZ() - this.getZ();
			float f = Mth.sqrt((float) (d1 * d1 + d3 * d3)) * 0.2F;
			cabbageEntity.shoot(d1, d2 + (double)f, d3, 1.6F, 12.0F);
			this.playSound(SoundEvents.SNOW_GOLEM_SHOOT, 1.0F, 0.4F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
			this.level().addFreshEntity(cabbageEntity);
		}
	}

	protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
		return 1.7F;
	}

	@Nullable
	protected SoundEvent getAmbientSound() {
		return SoundEvents.PILLAGER_AMBIENT;
	}

	@Nullable
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.PILLAGER_HURT;
	}

	@Nullable
	protected SoundEvent getDeathSound() {
		return SoundEvents.PILLAGER_DEATH;
	}

	@OnlyIn(Dist.CLIENT)
	public Vec3 getLeashOffset() {
		return new Vec3(0.0D, (double)(0.75F * this.getEyeHeight()), (double)(this.getBbWidth() * 0.4F));
	}

	@Override
	public void applyRaidBuffs(int wave, boolean p_213660_2_) {

	}

	public boolean isAlliedTo(Entity entityIn) {
		if (super.isAlliedTo(entityIn)) {
			return true;
		} else if (entityIn instanceof LivingEntity && ((LivingEntity)entityIn).getMobType() == MobType.ILLAGER) {
			return this.getTeam() == null && entityIn.getTeam() == null;
		} else {
			return false;
		}
	}

	private boolean wantsItem(Item p_213672_1_) {
		return this.hasActiveRaid() && p_213672_1_ == Items.WHITE_BANNER;
	}

	protected void pickUpItem(ItemEntity itemEntity) {
		ItemStack itemstack = itemEntity.getItem();
		if (itemstack.getItem() instanceof BannerItem) {
			super.pickUpItem(itemEntity);
		} else {
			Item item = itemstack.getItem();
			if (this.wantsItem(item)) {
				this.onItemPickup(itemEntity);
				ItemStack itemstack1 = this.inventory.addItem(itemstack);
				if (itemstack1.isEmpty()) {
					itemEntity.discard();
				} else {
					itemstack.setCount(itemstack1.getCount());
				}
			}
		}

	}

	@Override
	public SoundEvent getCelebrateSound() {
		return SoundEvents.PILLAGER_CELEBRATE;
	}

	@Override
	public ItemStack getPickedResult(HitResult target) {
		return new ItemStack(RCItems.CABBAGER_SPAWN_EGG.get());
	}

}
