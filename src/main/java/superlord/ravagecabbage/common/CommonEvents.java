package superlord.ravagecabbage.common;

import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.RavagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import superlord.ravagecabbage.RavageAndCabbage;
import superlord.ravagecabbage.entity.CabbagerEntity;
import superlord.ravagecabbage.init.ItemInit;

@Mod.EventBusSubscriber(modid = RavageAndCabbage.MOD_ID, bus = Bus.FORGE)
public class CommonEvents {
	
	@SubscribeEvent
    public static void processInteract(PlayerInteractEvent.EntityInteract event) {
        ItemStack itemstack = event.getPlayer().getHeldItem(event.getHand());

        if (!event.isCanceled() && itemstack.getItem() == Items.BUCKET && event.getTarget() instanceof RavagerEntity) {
            Integer stunTicks = ObfuscationReflectionHelper.getPrivateValue(RavagerEntity.class, (RavagerEntity) event.getTarget(), "field_213692_bA");

            if (stunTicks != null && stunTicks > 0) {

                event.getPlayer().playSound(SoundEvents.ENTITY_COW_MILK, 1.0F, 1.0F);
                if (!event.getPlayer().abilities.isCreativeMode) {
                    itemstack.shrink(1);
                }
                if (itemstack.isEmpty()) {
                    event.getPlayer().setHeldItem(event.getHand(), new ItemStack(ItemInit.RAVAGER_MILK.get()));
                } else if (!event.getPlayer().inventory.addItemStackToInventory(new ItemStack(ItemInit.RAVAGER_MILK.get()))) {
                    event.getPlayer().dropItem(new ItemStack(ItemInit.RAVAGER_MILK.get()), false);
                }
                event.setCanceled(true);
            }
        }
    }
	
	@SubscribeEvent
    public static void onEntityJoin(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof VillagerEntity) {
            VillagerEntity villager = (VillagerEntity) event.getEntity();
            villager.goalSelector.addGoal(1, new AvoidEntityGoal<>(villager, CabbagerEntity.class, 16.0F, 0.8D, 0.8D));
        }
    }

}
