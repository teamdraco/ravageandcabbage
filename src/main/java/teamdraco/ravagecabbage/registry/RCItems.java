package teamdraco.ravagecabbage.registry;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import teamdraco.ravagecabbage.RavageAndCabbage;
import teamdraco.ravagecabbage.common.items.CorruptedCabbageItem;
import teamdraco.ravagecabbage.common.items.DyeableRavagerHornArmorItem;
import teamdraco.ravagecabbage.common.items.RavagerHornArmorItem;
import teamdraco.ravagecabbage.common.items.RavagerMilkItem;
import teamdraco.ravagecabbage.common.items.ThrowableCabbageItem;

public class RCItems {
	
    public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, RavageAndCabbage.MOD_ID);

    public static final RegistryObject<Item> CABBAGE = REGISTER.register("cabbage", () -> new Item(new Item.Properties().tab(RavageAndCabbage.GROUP).food(new FoodProperties.Builder().nutrition(3).saturationMod(0.3F).build())));
    public static final RegistryObject<Item> CABBAGE_SEEDS = REGISTER.register("cabbage_seeds", () -> new ItemNameBlockItem(RCBlocks.CABBAGE_CROP.get(), new Item.Properties().tab(RavageAndCabbage.GROUP)));
    public static final RegistryObject<Item> CORRUPTED_CABBAGE = REGISTER.register("corrupted_cabbage", () -> new CorruptedCabbageItem(new Item.Properties().tab(RavageAndCabbage.GROUP)));
    public static final RegistryObject<Item> CORRUPTED_CABBAGE_SEEDS = REGISTER.register("corrupted_cabbage_seeds", () -> new ItemNameBlockItem(RCBlocks.CORRUPTED_CABBAGE_CROP.get(), new Item.Properties().tab(RavageAndCabbage.GROUP)));
    public static final RegistryObject<Item> RAVAGER_MILK = REGISTER.register("ravager_milk", () -> new RavagerMilkItem(new Item.Properties().tab(RavageAndCabbage.GROUP).stacksTo(1)));
    public static final RegistryObject<Item> CABBAGE_THROWABLE = REGISTER.register("throwable_cabbage", () -> new ThrowableCabbageItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> LEATHER_HORN_ARMOR = REGISTER.register("leather_horn_armor", () -> new DyeableRavagerHornArmorItem(1, "leather", new Item.Properties().tab(RavageAndCabbage.GROUP).durability(45)));
    public static final RegistryObject<Item> GOLDEN_HORN_ARMOR = REGISTER.register("golden_horn_armor", () -> new RavagerHornArmorItem(3, "golden", new Item.Properties().tab(RavageAndCabbage.GROUP).durability(50), ArmorMaterials.GOLD));
    public static final RegistryObject<Item> IRON_HORN_ARMOR = REGISTER.register("iron_horn_armor", () -> new RavagerHornArmorItem(2, "iron", new Item.Properties().tab(RavageAndCabbage.GROUP).durability(75), ArmorMaterials.IRON));
    public static final RegistryObject<Item> DIAMOND_HORN_ARMOR = REGISTER.register("diamond_horn_armor", () -> new RavagerHornArmorItem(5, "diamond", new Item.Properties().tab(RavageAndCabbage.GROUP).durability(100), ArmorMaterials.DIAMOND));
    public static final RegistryObject<Item> NETHERITE_HORN_ARMOR = REGISTER.register("netherite_horn_armor", () -> new RavagerHornArmorItem(8, "netherite", new Item.Properties().tab(RavageAndCabbage.GROUP).durability(150).fireResistant(), ArmorMaterials.NETHERITE));

    // Spawn Eggs
    public static final RegistryObject<Item> CABBAGER_SPAWN_EGG = REGISTER.register("cabbager_spawn_egg", () -> new ForgeSpawnEggItem(RCEntities.CABBAGER, 0x959B9B, 0x708438, new Item.Properties().tab(RavageAndCabbage.GROUP)));
    public static final RegistryObject<Item> RAVAGER_SPAWN_EGG = REGISTER.register("ravager_spawn_egg", () -> new ForgeSpawnEggItem(RCEntities.RAVAGER, 0x454040, 0x6a6965, new Item.Properties().tab(RavageAndCabbage.GROUP)));

}
