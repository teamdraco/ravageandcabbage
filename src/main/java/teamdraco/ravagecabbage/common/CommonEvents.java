package teamdraco.ravagecabbage.common;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import teamdraco.ravagecabbage.RavageAndCabbage;
import teamdraco.ravagecabbage.common.entities.CabbagerEntity;
import teamdraco.ravagecabbage.registry.RCItems;

@Mod.EventBusSubscriber(modid = RavageAndCabbage.MOD_ID, bus = Bus.FORGE)
public class CommonEvents {
	
	@SubscribeEvent
    public static void processInteract(PlayerInteractEvent.EntityInteract event) {
        ItemStack itemstack = event.getPlayer().getItemInHand(event.getHand());

        if (!event.isCanceled() && itemstack.getItem() == Items.BUCKET && event.getTarget() instanceof Ravager ravager) {
            if (ravager.stunnedTick > 0) {

                event.getPlayer().playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);
                if (!event.getPlayer().isCreative()) {
                    itemstack.shrink(1);
                }
                if (itemstack.isEmpty()) {
                    event.getPlayer().setItemInHand(event.getHand(), new ItemStack(RCItems.RAVAGER_MILK.get()));
                } else if (!event.getPlayer().getInventory().add(new ItemStack(RCItems.RAVAGER_MILK.get()))) {
                    event.getPlayer().drop(new ItemStack(RCItems.RAVAGER_MILK.get()), false);
                }
                event.setCanceled(true);
            }
        }
    }
	
	@SubscribeEvent
    public static void onEntityJoin(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof AbstractVillager villager) {
            villager.goalSelector.addGoal(1, new AvoidEntityGoal<>(villager, CabbagerEntity.class, 16.0F, 0.8D, 0.8D));
        }
    }

}
