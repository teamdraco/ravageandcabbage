package teamdraco.ravagecabbage.common.entities.item;

import javax.annotation.Nonnull;

import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import teamdraco.ravagecabbage.common.entities.CorruptedVillager;
import teamdraco.ravagecabbage.registry.RCEntities;
import teamdraco.ravagecabbage.registry.RCItems;

public class CorruptedCabbageItemEntity extends ThrowableItemProjectile {
	public CorruptedCabbageItemEntity(EntityType<? extends CorruptedCabbageItemEntity> p_i50159_1_, Level p_i50159_2_) {
		super(p_i50159_1_, p_i50159_2_);
	}

	public CorruptedCabbageItemEntity(Level worldIn, LivingEntity throwerIn) {
		super(RCEntities.CORRUPTED_CABBAGE.get(), throwerIn, worldIn);
	}

	public CorruptedCabbageItemEntity(Level worldIn, double x, double y, double z) {
		super(RCEntities.CORRUPTED_CABBAGE.get(), x, y, z, worldIn);
	}

	protected Item getDefaultItem() {
		return RCItems.CORRUPTED_CABBAGE.get();
	}

	@Nonnull
	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	protected void onHitEntity(EntityHitResult p_213868_1_) {
		if (p_213868_1_.getEntity().getType() == EntityType.VILLAGER) {
			Villager villager = (Villager) p_213868_1_.getEntity();
			CorruptedVillager corrupted = new CorruptedVillager(RCEntities.CORRUPTED_VILLAGER.get(), this.getLevel());
			Vec3 pos = new Vec3(villager.getX(), villager.getY(), villager.getZ());
			corrupted.setPos(pos);
			corrupted.setVillagerData(villager.getVillagerData());
			level.addFreshEntity(corrupted);
			villager.remove(RemovalReason.DISCARDED);
		} else {
			p_213868_1_.getEntity().hurt(DamageSource.thrown(this, this.getOwner()), 2);
		}
		super.onHitEntity(p_213868_1_);
	}

	protected void onHitBlock(BlockHitResult result) {
		super.onHitBlock(result);
		if (!this.level.isClientSide) {
			this.level.broadcastEntityEvent(this, (byte)3);
			this.discard();
		}
	}
}
