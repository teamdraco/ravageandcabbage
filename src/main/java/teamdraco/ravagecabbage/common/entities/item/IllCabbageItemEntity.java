package teamdraco.ravagecabbage.common.entities.item;

import net.minecraft.network.protocol.Packet;
import net.minecraft.sounds.SoundEvents;
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

public class IllCabbageItemEntity extends ThrowableItemProjectile {
   public IllCabbageItemEntity(EntityType<? extends IllCabbageItemEntity> p_i50159_1_, Level p_i50159_2_) {
      super(p_i50159_1_, p_i50159_2_);
   }

   public IllCabbageItemEntity(Level worldIn, LivingEntity throwerIn) {
      super(RCEntities.ILL_CABBAGE.get(), throwerIn, worldIn);
   }

   public IllCabbageItemEntity(Level worldIn, double x, double y, double z) {
      super(RCEntities.ILL_CABBAGE.get(), x, y, z, worldIn);
   }

   protected Item getDefaultItem() {
      return RCItems.ILL_CABBAGE.get();
   }

   @Nonnull
   @Override
   public Packet<?> getAddEntityPacket() {
     return NetworkHooks.getEntitySpawningPacket(this);
   }

   protected void onHitEntity(EntityHitResult p_213868_1_) {
      super.onHitEntity(p_213868_1_);
      Entity entity = p_213868_1_.getEntity();
      entity.hurt(DamageSource.thrown(this, this.getOwner()), 8);

      if (!entity.isAlive() && getOwner() != null && !entity.is(getOwner())) {
         entity.playSound(SoundEvents.RAVAGER_ROAR, 1.0F, 1.0F);
      }
   }

   protected void onHitBlock(BlockHitResult result) {
      super.onHitBlock(result);
      if (!this.level.isClientSide) {
         this.level.broadcastEntityEvent(this, (byte)3);
         this.discard();
      }
   }
}
