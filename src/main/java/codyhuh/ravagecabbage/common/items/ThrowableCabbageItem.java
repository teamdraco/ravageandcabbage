package codyhuh.ravagecabbage.common.items;

import codyhuh.ravagecabbage.common.entities.item.CabbageItemEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ThrowableCabbageItem extends Item {

	public ThrowableCabbageItem(Properties properties) {
		super(properties);
	}

	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
	      ItemStack itemstack = playerIn.getItemInHand(handIn);
	      worldIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (playerIn.getRandom().nextFloat() * 0.4F + 0.8F));
	      if (!worldIn.isClientSide) {
	         CabbageItemEntity cabbageEntity = new CabbageItemEntity(worldIn, playerIn);
	         cabbageEntity.getItem();
	         cabbageEntity.shootFromRotation(playerIn, playerIn.xRotO, playerIn.yRotO, 0.0F, 1.5F, 1.0F);
	         worldIn.addFreshEntity(cabbageEntity);
	      }

	      playerIn.awardStat(Stats.ITEM_USED.get(this));
	      if (!playerIn.isCreative()) {
	         itemstack.shrink(1);
	      }

	      return InteractionResultHolder.sidedSuccess(itemstack, worldIn.isClientSide());
	   }

	public CabbageItemEntity createCabbage(Level worldIn, ItemStack stack, LivingEntity shooter) {
		CabbageItemEntity cabbageEntity = new CabbageItemEntity(worldIn, shooter);
		return cabbageEntity;
	}
}