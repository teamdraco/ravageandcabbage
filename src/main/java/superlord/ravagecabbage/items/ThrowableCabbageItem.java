package superlord.ravagecabbage.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import superlord.ravagecabbage.entity.item.CabbageItemEntity;

public class ThrowableCabbageItem extends Item {

	public ThrowableCabbageItem(Properties properties) {
		super(properties);
		// TODO Auto-generated constructor stub
	}

	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
	      ItemStack itemstack = playerIn.getHeldItem(handIn);
	      worldIn.playSound((PlayerEntity)null, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
	      if (!worldIn.isRemote) {
	         CabbageItemEntity cabbageEntity = new CabbageItemEntity(worldIn, playerIn);
	         cabbageEntity.getItem();
	         cabbageEntity.func_234612_a_(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F, 1.0F);
	         worldIn.addEntity(cabbageEntity);
	      }

	      playerIn.addStat(Stats.ITEM_USED.get(this));
	      if (!playerIn.abilities.isCreativeMode) {
	         itemstack.shrink(1);
	      }

	      return ActionResult.func_233538_a_(itemstack, worldIn.isRemote());
	   }

	public CabbageItemEntity createCabbage(World worldIn, ItemStack stack, LivingEntity shooter) {
		CabbageItemEntity cabbageEntity = new CabbageItemEntity(worldIn, shooter);
		return cabbageEntity;
	}
}