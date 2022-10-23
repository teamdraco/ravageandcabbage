package teamdraco.ravagecabbage.common.items;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

public class RavagerMilkItem extends Item {
   public RavagerMilkItem(Item.Properties builder) {
      super(builder);
   }

   public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entity) {

      for (MobEffectInstance instance : entity.getActiveEffects()) {
         if (!worldIn.isClientSide && !instance.getEffect().isBeneficial()) {
            entity.removeEffect(instance.getEffect());
         }
      }

      if (!worldIn.isClientSide) entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 600, 0));

      if (entity instanceof ServerPlayer player) {
         CriteriaTriggers.CONSUME_ITEM.trigger(player, stack);
         player.awardStat(Stats.ITEM_USED.get(this));
      }

      if (entity instanceof Player && !((Player)entity).isCreative()) {
         stack.shrink(1);
      }

      return stack.isEmpty() ? new ItemStack(Items.BUCKET) : stack;
   }

   public int getUseDuration(ItemStack stack) {
      return 32;
   }

   public UseAnim getUseAnimation(ItemStack stack) {
      return UseAnim.DRINK;
   }

   public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
      return ItemUtils.startUsingInstantly(worldIn, playerIn, handIn);
   }

   @Override
   public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
      return new net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper(stack);
   }
}
