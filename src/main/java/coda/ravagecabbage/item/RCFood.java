package coda.ravagecabbage.item;

import coda.ravagecabbage.RavageCabbage.RCItemGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class RCFood extends Item {
	
	public RCFood(Item.Properties properties) {
		super(properties);
	}
	
	public RCFood(int amount, float saturation) {
		this(new Item.Properties().group(RCItemGroup.instance).food(new Food.Builder().hunger(amount).saturation(saturation).build()));
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World world, LivingEntity entity) {
		if(entity instanceof PlayerEntity) onFoodEaten(stack, world, (PlayerEntity) entity);
		return super.onItemUseFinish(stack, world, entity);
	}
	
	protected void onFoodEaten(ItemStack stack, World world, PlayerEntity entity) {
		
	}

}
