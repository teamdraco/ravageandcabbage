package superlord.ravagecabbage.init;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import superlord.ravagecabbage.RavageAndCabbage;
import superlord.ravagecabbage.blocks.CabbageCropBlock;

public class BlockInit {

    public static final DeferredRegister<Block> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, RavageAndCabbage.MOD_ID);

	public static final RegistryObject<Block> CABBAGE_CROP = REGISTER.register("cabbage_crop", () -> new CabbageCropBlock(AbstractBlock.Properties.create(Material.PLANTS).sound(SoundType.CROP).hardnessAndResistance(0f).doesNotBlockMovement().tickRandomly()));
    
}
