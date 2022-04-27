package teamdraco.ravagecabbage.common.entities.item;

import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.network.NetworkHooks;
import teamdraco.ravagecabbage.registry.RCEntities;
import teamdraco.ravagecabbage.registry.RCItems;

import javax.annotation.Nonnull;

public class CabbageItemEntity extends ThrowableItemProjectile {
   public CabbageItemEntity(EntityType<? extends CabbageItemEntity> p_i50159_1_, Level p_i50159_2_) {
      super(p_i50159_1_, p_i50159_2_);
   }

   public CabbageItemEntity(Level worldIn, LivingEntity throwerIn) {
      super(RCEntities.CABBAGE.get(), throwerIn, worldIn);
   }

   public CabbageItemEntity(Level worldIn, double x, double y, double z) {
      super(RCEntities.CABBAGE.get(), x, y, z, worldIn);
   }

   protected Item getDefaultItem() {
      return RCItems.CABBAGE_THROWABLE.get();
   }

   @Nonnull
   @Override
   public Packet<?> getAddEntityPacket() {
     return NetworkHooks.getEntitySpawningPacket(this);
   }

   protected void onHitEntity(EntityHitResult p_213868_1_) {
      super.onHitEntity(p_213868_1_);
      Entity entity = p_213868_1_.getEntity();
      entity.hurt(DamageSource.thrown(this, this.getOwner()), 2);
   }

   protected void onHitBlock(BlockHitResult result) {
      super.onHitBlock(result);
      if (!this.level.isClientSide) {
         this.level.broadcastEntityEvent(this, (byte)3);
         this.discard();
      }
   }
}
