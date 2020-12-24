package superlord.ravagecabbage.blocks;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.loading.FMLEnvironment;
import superlord.ravagecabbage.RavageAndCabbage;
import superlord.ravagecabbage.init.BlockInit;

@Mod.EventBusSubscriber(modid = RavageAndCabbage.MOD_ID, bus = Bus.MOD)
public class RavageAndCabbageRenderTypes {
	
	@SubscribeEvent
	public static void registerBlocks(final RegistryEvent.Register<Block> event) {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			RenderType cutoutRenderType = RenderType.getCutout();
			
			RenderTypeLookup.setRenderLayer(BlockInit.CABBAGE_CROP.get(), cutoutRenderType);
		}
	}

}
