package coda.ravagecabbage.util;

import coda.ravagecabbage.RavageCabbage;
import coda.ravagecabbage.client.renderer.entity.CabbageRavagerRenderer;
import coda.ravagecabbage.entity.CabbageRavagerEntity;
import coda.ravagecabbage.init.BlockInit;
import coda.ravagecabbage.init.EntityInit;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.entity.monster.RavagerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = RavageCabbage.MOD_ID, bus = Bus.MOD, value = Dist.CLIENT)
public class ClientEventBusSubscriber {
	
	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent event) {
		RenderTypeLookup.setRenderLayer(BlockInit.CABBAGE_CROP, RenderType.getCutout());
		RenderingRegistry.registerEntityRenderingHandler(EntityInit.RAVAGER, CabbageRavagerRenderer::new);
	}
}
