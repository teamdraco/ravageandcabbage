package codyhuh.ravagecabbage.common;

import codyhuh.ravagecabbage.RavageAndCabbage;
import codyhuh.ravagecabbage.common.entities.CabbagerEntity;
import codyhuh.ravagecabbage.registry.RCItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = RavageAndCabbage.MOD_ID, bus = Bus.FORGE)
public class CommonEvents {
	
	@SubscribeEvent
    public static void processInteract(PlayerInteractEvent.EntityInteract event) {
        ItemStack itemstack = event.getEntity().getItemInHand(event.getHand());

        if (!event.isCanceled() && itemstack.getItem() == Items.BUCKET && event.getTarget() instanceof Ravager ravager) {
            if (ravager.stunnedTick > 0) {

                event.getEntity().playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);
                if (!event.getEntity().isCreative()) {
                    itemstack.shrink(1);
                }
                if (itemstack.isEmpty()) {
                    event.getEntity().setItemInHand(event.getHand(), new ItemStack(RCItems.RAVAGER_MILK.get()));
                } else if (!event.getEntity().getInventory().add(new ItemStack(RCItems.RAVAGER_MILK.get()))) {
                    event.getEntity().drop(new ItemStack(RCItems.RAVAGER_MILK.get()), false);
                }
                event.setCanceled(true);
            }
        }
    }
	
	@SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof AbstractVillager villager) {
            villager.goalSelector.addGoal(1, new AvoidEntityGoal<>(villager, CabbagerEntity.class, 16.0F, 0.8D, 0.8D));
        }
    }

}