package codyhuh.ravagecabbage.registry;

import codyhuh.ravagecabbage.RavageAndCabbage;
import codyhuh.ravagecabbage.common.blocks.CabbageCropBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RCBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, RavageAndCabbage.MOD_ID);

	public static final RegistryObject<Block> CABBAGE_CROP = BLOCKS.register("cabbage_crop", () -> new CabbageCropBlock(BlockBehaviour.Properties.of().sound(SoundType.CROP).strength(0f).noCollission().randomTicks()));

}
