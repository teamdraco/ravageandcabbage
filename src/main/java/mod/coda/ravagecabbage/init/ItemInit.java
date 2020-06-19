package mod.coda.ravagecabbage.init;

import mod.coda.ravagecabbage.Main;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemInit {

    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, Main.MODID);

    public static final RegistryObject<Item> CABBAGE = ITEMS.register("cabbage", () -> new Item(new Item.Properties().food(new Food.Builder().saturation(0.1f).hunger(2).build()).group(Main.CabbageItemGroup.instance)));
    public static final RegistryObject<BlockItem> CABBAGE_SEEDS = ITEMS.register("cabbage_seeds", () -> new BlockItem(BlockInit.CABBAGE_CROP.get(), new Item.Properties().group(Main.CabbageItemGroup.instance)));

}
