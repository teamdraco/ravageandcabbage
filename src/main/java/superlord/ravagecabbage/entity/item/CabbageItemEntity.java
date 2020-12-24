package superlord.ravagecabbage.entity.item;

import javax.annotation.Nonnull;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import superlord.ravagecabbage.init.EntityInit;
import superlord.ravagecabbage.init.ItemInit;

public class CabbageItemEntity extends ProjectileItemEntity {
   public CabbageItemEntity(EntityType<? extends CabbageItemEntity> p_i50159_1_, World p_i50159_2_) {
      super(p_i50159_1_, p_i50159_2_);
   }

   public CabbageItemEntity(World worldIn, LivingEntity throwerIn) {
      super(EntityInit.CABBAGE.get(), throwerIn, worldIn);
   }

   public CabbageItemEntity(World worldIn, double x, double y, double z) {
      super(EntityInit.CABBAGE.get(), x, y, z, worldIn);
   }

   protected Item getDefaultItem() {
      return ItemInit.CABBAGE_THROWABLE.get();
   }

   @Nonnull
   @Override
   public IPacket<?> createSpawnPacket() {
     return NetworkHooks.getEntitySpawningPacket(this);
   }

   protected void onEntityHit(EntityRayTraceResult p_213868_1_) {
      super.onEntityHit(p_213868_1_);
      Entity entity = p_213868_1_.getEntity();
      entity.attackEntityFrom(DamageSource.causeThrownDamage(this, this.func_234616_v_()), 2);
   }

   protected void onImpact(RayTraceResult result) {
      super.onImpact(result);
      if (!this.world.isRemote) {
         this.world.setEntityState(this, (byte)3);
         this.remove();
      }

   }
}
