package coda.ravagecabbage.init;

import coda.ravagecabbage.RavageCabbage;
import coda.ravagecabbage.block.CabbageCrop;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = RavageCabbage.MOD_ID, bus = Bus.MOD)
public class BlockInit {
	
	public static final Block CABBAGE_CROP = new CabbageCrop(Block.Properties.from(Blocks.POTATOES)).setRegistryName("cabbage_crop");

	@SubscribeEvent
	public static void registerItems(final RegistryEvent.Register<Block> event) {
		event.getRegistry().register(CABBAGE_CROP);
	}
	
}
