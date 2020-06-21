package coda.ravagecabbage.item;

import coda.ravagecabbage.RavageCabbage.RCItemGroup;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class RavagerMilk extends Item {
	
	public RavagerMilk(Item.Properties properties) {
		super(properties);
	}
	
	public RavagerMilk(int amount, float saturation) {
		this(new Item.Properties().group(RCItemGroup.instance).maxStackSize(1).food(new Food.Builder().hunger(amount).saturation(saturation).build()));
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
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        if (!worldIn.isRemote) entityLiving.curePotionEffects(stack);

        if (entityLiving instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)entityLiving;
            CriteriaTriggers.CONSUME_ITEM.trigger(serverplayerentity, stack);
            serverplayerentity.addStat(Stats.ITEM_USED.get(this));
        }

        if (entityLiving instanceof PlayerEntity && !((PlayerEntity)entityLiving).abilities.isCreativeMode) {
            stack.shrink(1);
        }

        if (!worldIn.isRemote) {
            entityLiving.addPotionEffect(new EffectInstance(Effects.STRENGTH, 1200, 0));
        }

        return stack.isEmpty() ? new ItemStack(Items.BUCKET) : stack;
	};

}
