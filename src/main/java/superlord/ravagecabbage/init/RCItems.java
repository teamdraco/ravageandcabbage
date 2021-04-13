package superlord.ravagecabbage.init;

import net.minecraft.item.BlockNamedItem;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import superlord.ravagecabbage.RavageAndCabbage;
import superlord.ravagecabbage.items.RavageAndCabbageSpawnEggItem;
import superlord.ravagecabbage.items.RavagerMilkItem;
import superlord.ravagecabbage.items.ThrowableCabbageItem;

public class RCItems {
	
    public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, RavageAndCabbage.MOD_ID);

    public static final RegistryObject<Item> CABBAGE = REGISTER.register("cabbage", () -> new Item(new Item.Properties().group(RavageAndCabbage.GROUP).food(new Food.Builder().hunger(3).saturation(0.3F).meat().build())));
    public static final RegistryObject<Item> CABBAGE_SEEDS = REGISTER.register("cabbage_seeds", () -> new BlockNamedItem(RCBlocks.CABBAGE_CROP.get(), new Item.Properties().group(RavageAndCabbage.GROUP)));
    public static final RegistryObject<Item> RAVAGER_MILK = REGISTER.register("ravager_milk", () -> new RavagerMilkItem(new Item.Properties().group(RavageAndCabbage.GROUP).maxStackSize(1)));
    public static final RegistryObject<Item> CABBAGE_THROWABLE = REGISTER.register("throwable_cabbage", () -> new ThrowableCabbageItem(new Item.Properties().maxStackSize(1)));
    public static final RegistryObject<Item> CABBAGER_SPAWN_EGG = REGISTER.register("cabbager_spawn_egg", () -> new RavageAndCabbageSpawnEggItem(RCEntities.CABBAGER, 0x959B9B, 0x708438, new Item.Properties().group(RavageAndCabbage.GROUP)));
    public static final RegistryObject<Item> RAVAGER_SPAWN_EGG = REGISTER.register("ravager_spawn_egg", () -> new RavageAndCabbageSpawnEggItem(RCEntities.RAVAGER, 0x454040, 0x6a6965, new Item.Properties().group(RavageAndCabbage.GROUP)));

}
