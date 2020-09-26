package coda.ravagecabbage.init;

import coda.ravagecabbage.RavageCabbage;
import coda.ravagecabbage.RavageCabbage.RCItemGroup;
import coda.ravagecabbage.item.RavagerMilk;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = RavageCabbage.MOD_ID, bus = Bus.MOD)
public class ItemInit {

	public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, RavageCabbage.MOD_ID);

	public static final RegistryObject<Item> CABBAGE = ITEMS.register("cabbage", () -> new Item(new Item.Properties().group(RCItemGroup.INSTANCE).food(new Food.Builder().saturation(0.1f).hunger(3).build())));
	public static final RegistryObject<Item> CABBAGE_SEEDS = ITEMS.register("cabbage_seeds", () -> new BlockItem(BlockInit.CABBAGE_CROP, new Item.Properties().group(RCItemGroup.INSTANCE)));
	public static final RegistryObject<Item> RAVAGER_MILK = ITEMS.register("ravager_milk", () -> new RavagerMilk());
	
}
