package coda.ravagecabbage.item;

import coda.ravagecabbage.RavageCabbage.RCItemGroup;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class RavagerMilk extends Item {
	public RavagerMilk() {
		super(new Item.Properties().group(RCItemGroup.INSTANCE).maxStackSize(1));
	}
	
	@Override
	public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

	@Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        playerIn.setActiveHand(handIn);
        return new ActionResult<>(ActionResultType.SUCCESS, playerIn.getHeldItem(handIn));
    }

	@Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, LivingEntity entity) {
        if (!world.isRemote) entity.curePotionEffects(stack);

        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity)entity;
            CriteriaTriggers.CONSUME_ITEM.trigger(player, stack);
            player.addStat(Stats.ITEM_USED.get(this));
        }

        if (entity instanceof PlayerEntity && !((PlayerEntity)entity).abilities.isCreativeMode) {
            stack.shrink(1);
        }

        if (!world.isRemote) {
            entity.addPotionEffect(new EffectInstance(Effects.STRENGTH, 1200, 0));
        }

        return stack.isEmpty() ? new ItemStack(Items.BUCKET) : stack;
	}
}
