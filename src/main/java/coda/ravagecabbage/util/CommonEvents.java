package coda.ravagecabbage.util;

import coda.ravagecabbage.RavageCabbage;
import coda.ravagecabbage.init.ItemInit;
import net.minecraft.entity.monster.RavagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = RavageCabbage.modid, bus = Bus.FORGE)
public class CommonEvents {

    @SubscribeEvent
    public static void processInteract(PlayerInteractEvent.EntityInteract event) {
        ItemStack itemstack = event.getPlayer().getHeldItem(event.getHand());
        if (!event.isCanceled() && itemstack.getItem() == Items.BUCKET && event.getTarget() instanceof RavagerEntity) {
            event.getPlayer().playSound(SoundEvents.ENTITY_COW_MILK, 1.0F, 1.0F);
            if (!event.getPlayer().abilities.isCreativeMode) {
                itemstack.shrink(1);
            }
            if (itemstack.isEmpty()) {
                event.getPlayer().setHeldItem(event.getHand(), new ItemStack(ItemInit.RAVAGER_MILK));
            } else if (!event.getPlayer().inventory.addItemStackToInventory(new ItemStack(ItemInit.RAVAGER_MILK))) {
                event.getPlayer().dropItem(new ItemStack(ItemInit.RAVAGER_MILK), false);
            }
            event.setCanceled(true);
        }
    }
}