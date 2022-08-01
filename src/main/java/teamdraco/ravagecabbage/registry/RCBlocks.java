package teamdraco.ravagecabbage.registry;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import teamdraco.ravagecabbage.RavageAndCabbage;
import teamdraco.ravagecabbage.common.blocks.CabbageCropBlock;

public class RCBlocks {
    public static final DeferredRegister<Block> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, RavageAndCabbage.MOD_ID);

	public static final RegistryObject<Block> CABBAGE_CROP = REGISTER.register("cabbage_crop", () -> new CabbageCropBlock(BlockBehaviour.Properties.of(Material.PLANT).sound(SoundType.CROP).strength(0f).noCollission().randomTicks()));

}
