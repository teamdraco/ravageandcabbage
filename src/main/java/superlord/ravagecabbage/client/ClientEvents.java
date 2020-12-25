package superlord.ravagecabbage.client;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import superlord.ravagecabbage.RavageAndCabbage;
import superlord.ravagecabbage.client.render.CabbageRenderer;
import superlord.ravagecabbage.client.render.CabbagerRenderer;
import superlord.ravagecabbage.client.render.RavagerRenderer;
import superlord.ravagecabbage.init.EntityInit;
import superlord.ravagecabbage.items.RavageAndCabbageSpawnEggItem;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = RavageAndCabbage.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {
	
	@SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EntityInit.CABBAGE.get(), CabbageRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityInit.CABBAGER.get(), CabbagerRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityInit.RAVAGER.get(), manager -> new RavagerRenderer()); 
	}
	
	@SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void itemColors(ColorHandlerEvent.Item event) {
        ItemColors handler = event.getItemColors();
        IItemColor eggColor = (stack, tintIndex) -> ((RavageAndCabbageSpawnEggItem) stack.getItem()).getColor(tintIndex);
        for (RavageAndCabbageSpawnEggItem e : RavageAndCabbageSpawnEggItem.UNADDED_EGGS) handler.register(eggColor, e);
    }

}
