package coda.ravagecabbage.init;

import coda.ravagecabbage.RavageCabbage;
import coda.ravagecabbage.RavageCabbage.RCItemGroup;
import coda.ravagecabbage.item.RCFood;
import coda.ravagecabbage.item.RavagerMilk;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = RavageCabbage.MOD_ID, bus = Bus.MOD)
public class ItemInit {

	public static final Item CABBAGE = new RCFood(2, 0.1F).setRegistryName("cabbage");
	public static final Item CABBAGE_SEEDS = new BlockItem(BlockInit.CABBAGE_CROP, new Item.Properties().group(RCItemGroup.INSTANCE)).setRegistryName("cabbage_seeds");
	public static final Item RAVAGER_MILK = new RavagerMilk().setRegistryName("ravager_milk");
	
	@SubscribeEvent
	public static void registerItems(final RegistryEvent.Register<Item> event) {
		event.getRegistry().registerAll(CABBAGE, CABBAGE_SEEDS, RAVAGER_MILK);
	}
	
}
