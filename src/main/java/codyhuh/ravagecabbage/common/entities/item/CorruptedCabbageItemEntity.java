package codyhuh.ravagecabbage.common.entities.item;

import codyhuh.ravagecabbage.common.entities.CorruptedVillager;
import codyhuh.ravagecabbage.registry.RCEntities;
import codyhuh.ravagecabbage.registry.RCItems;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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

import javax.annotation.Nonnull;

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

	public Item getDefaultItem() {
		return RCItems.CORRUPTED_CABBAGE.get();
	}

	@Nonnull
	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public void onHitEntity(EntityHitResult result) {
		if (result.getEntity() instanceof Villager villager && !villager.isBaby() && !(villager instanceof CorruptedVillager)) {
			CorruptedVillager corrupted = RCEntities.CORRUPTED_VILLAGER.get().create(level());

			if (corrupted != null) {
				Vec3 villagerPos = villager.position();
				Vec3 corruptedPos = corrupted.position();

				corrupted.moveTo(villagerPos.x, villagerPos.y, villagerPos.z, villager.yBodyRot, villager.xRot);
				corrupted.setVillagerData(villager.getVillagerData());

				level().playSound(null, corrupted.blockPosition(), SoundEvents.RAVAGER_AMBIENT, SoundSource.HOSTILE, 2.0F, 1.0F);
				level().addFreshEntity(corrupted);

				villager.discard();
			}
		}
		else {
			result.getEntity().hurt(result.getEntity().damageSources().thrown(this, getOwner()), 1.0F);
		}
		discard();
	}

	protected void onHitBlock(BlockHitResult result) {
		super.onHitBlock(result);
		if (!this.level().isClientSide) {
			this.level().broadcastEntityEvent(this, (byte)3);
			this.discard();
		}
	}
}
