package superlord.ravagecabbage.entity;

import javax.annotation.Nullable;

import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.RangedAttackGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.AbstractIllagerEntity;
import net.minecraft.entity.monster.AbstractRaiderEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import superlord.ravagecabbage.entity.item.CabbageItemEntity;
import superlord.ravagecabbage.init.ItemInit;

public class CabbagerEntity extends AbstractIllagerEntity implements IRangedAttackMob {
	private final Inventory inventory = new Inventory(5);


	public CabbagerEntity(EntityType<? extends CabbagerEntity> type, World worldIn) {
		super(type, worldIn);
	}

	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(0, new SwimGoal(this));
		this.goalSelector.addGoal(2, new AbstractRaiderEntity.FindTargetGoal(this, 10.0F));
		this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.25D, 20, 10.0F));
		this.goalSelector.addGoal(8, new RandomWalkingGoal(this, 0.6D));
		this.goalSelector.addGoal(9, new LookAtGoal(this, PlayerEntity.class, 15.0F, 1.0F));
		this.goalSelector.addGoal(10, new LookAtGoal(this, MobEntity.class, 15.0F));
		this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, AbstractRaiderEntity.class)).setCallsForHelp());
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillagerEntity.class, false));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, true));
	}

	public static AttributeModifierMap.MutableAttribute createAttributes() {
		return MobEntity.func_233666_p_().createMutableAttribute(Attributes.MAX_HEALTH, 20.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, (double)0.2F);
	}

	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		ListNBT listnbt = new ListNBT();

		for(int i = 0; i < this.inventory.getSizeInventory(); ++i) {
			ItemStack itemstack = this.inventory.getStackInSlot(i);
			if (!itemstack.isEmpty()) {
				listnbt.add(itemstack.write(new CompoundNBT()));
			}
		}
	}

	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		ListNBT listnbt = compound.getList("Inventory", 10);

		for(int i = 0; i < listnbt.size(); ++i) {
			ItemStack itemstack = ItemStack.read(listnbt.getCompound(i));
			if (!itemstack.isEmpty()) {
				this.inventory.addItem(itemstack);
			}
		}
	}

	public void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor) {
		CabbageItemEntity cabbageEntity = new CabbageItemEntity(this.world, this);
		double d0 = target.getPosYEye() - (double)1.1F;
		double d1 = target.getPosX() - this.getPosX();
		double d2 = d0 - cabbageEntity.getPosY();
		double d3 = target.getPosZ() - this.getPosZ();
		float f = MathHelper.sqrt(d1 * d1 + d3 * d3) * 0.2F;
		cabbageEntity.shoot(d1, d2 + (double)f, d3, 1.6F, 12.0F);
		this.playSound(SoundEvents.ENTITY_SNOW_GOLEM_SHOOT, 1.0F, 0.4F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
		this.world.addEntity(cabbageEntity);
	}

	protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
		return 1.7F;
	}

	@Nullable
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_PILLAGER_AMBIENT;
	}

	@Nullable
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.ENTITY_PILLAGER_DEATH;
	}

	@Nullable
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_PILLAGER_HURT;
	}

	@OnlyIn(Dist.CLIENT)
	public Vector3d func_241205_ce_() {
		return new Vector3d(0.0D, (double)(0.75F * this.getEyeHeight()), (double)(this.getWidth() * 0.4F));
	}

	@Override
	public void applyWaveBonus(int wave, boolean p_213660_2_) {

	}

	public boolean isOnSameTeam(Entity entityIn) {
		if (super.isOnSameTeam(entityIn)) {
			return true;
		} else if (entityIn instanceof LivingEntity && ((LivingEntity)entityIn).getCreatureAttribute() == CreatureAttribute.ILLAGER) {
			return this.getTeam() == null && entityIn.getTeam() == null;
		} else {
			return false;
		}
	}

	private boolean func_213672_b(Item p_213672_1_) {
		return this.isRaidActive() && p_213672_1_ == Items.WHITE_BANNER;
	}

	protected void updateEquipmentIfNeeded(ItemEntity itemEntity) {
		ItemStack itemstack = itemEntity.getItem();
		if (itemstack.getItem() instanceof BannerItem) {
			super.updateEquipmentIfNeeded(itemEntity);
		} else {
			Item item = itemstack.getItem();
			if (this.func_213672_b(item)) {
				this.triggerItemPickupTrigger(itemEntity);
				ItemStack itemstack1 = this.inventory.addItem(itemstack);
				if (itemstack1.isEmpty()) {
					itemEntity.remove();
				} else {
					itemstack.setCount(itemstack1.getCount());
				}
			}
		}

	}

	@Override
	public SoundEvent getRaidLossSound() {
		return SoundEvents.ENTITY_PILLAGER_CELEBRATE;
	}
	
	@Override
    public ItemStack getPickedResult(RayTraceResult target) {
        return new ItemStack(ItemInit.CABBAGER_SPAWN_EGG.get());
    }
}
