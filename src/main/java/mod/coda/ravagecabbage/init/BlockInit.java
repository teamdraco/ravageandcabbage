package mod.coda.ravagecabbage.init;

import mod.coda.ravagecabbage.Main;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockInit {

    public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, Main.MODID);

    public static final RegistryObject<Block> CABBAGE_CROP = BLOCKS.register("cabbage_crop", () -> new Block(Block.Properties.from(Blocks.POTATOES)));

}
