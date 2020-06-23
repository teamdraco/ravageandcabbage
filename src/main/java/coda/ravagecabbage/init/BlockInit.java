package coda.ravagecabbage.init;

import coda.ravagecabbage.RavageCabbage;
import coda.ravagecabbage.RavageCabbage.RCItemGroup;
import coda.ravagecabbage.block.CabbageCrop;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = RavageCabbage.MOD_ID, bus = Bus.MOD)
public class BlockInit {
	
	public static final Block CABBAGE_CROP = new CabbageCrop(Block.Properties.from(Blocks.POTATOES)).setRegistryName("cabbage_crop");
	public static final Block CABBAGE_CRATE = new Block(Block.Properties.create(Material.WOOD).hardnessAndResistance(2.5F).sound(SoundType.WOOD)).setRegistryName("cabbage_crate");

	@SubscribeEvent
	public static void registerItems(final RegistryEvent.Register<Block> event) {
		event.getRegistry().register(CABBAGE_CROP);
		event.getRegistry().register(CABBAGE_CRATE);
	}
	
	@SubscribeEvent
	public static void registerBlockItems(final RegistryEvent.Register<Item> event) {
		event.getRegistry().register(new BlockItem(CABBAGE_CRATE, new Item.Properties().group(RCItemGroup.instance)).setRegistryName("cabbage_crate"));
	}
	
}
