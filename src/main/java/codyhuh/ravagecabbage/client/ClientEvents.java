package codyhuh.ravagecabbage.client;

import java.awt.event.KeyEvent;

import codyhuh.ravagecabbage.client.model.CabbagerModel;
import codyhuh.ravagecabbage.client.model.RCRavagerModel;
import codyhuh.ravagecabbage.client.render.CabbagerRenderer;
import codyhuh.ravagecabbage.client.render.CorruptedVillagerRenderer;
import codyhuh.ravagecabbage.client.render.RCRavagerRenderer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import codyhuh.ravagecabbage.RavageAndCabbage;
import codyhuh.ravagecabbage.common.items.DyeableRavagerHornArmorItem;
import codyhuh.ravagecabbage.network.KeyInputMessage;
import codyhuh.ravagecabbage.network.RCNetwork;
import codyhuh.ravagecabbage.registry.RCBlocks;
import codyhuh.ravagecabbage.registry.RCEntities;
import codyhuh.ravagecabbage.registry.RCItems;

@Mod.EventBusSubscriber(modid = RavageAndCabbage.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {
	public static ModelLayerLocation CABBAGER = new ModelLayerLocation(new ResourceLocation(RavageAndCabbage.MOD_ID, "cabbager"), "cabbager");
	public static ModelLayerLocation RAVAGER = new ModelLayerLocation(new ResourceLocation(RavageAndCabbage.MOD_ID, "ravager"), "ravager");
	public static ModelLayerLocation RAVAGER_SADDLE = new ModelLayerLocation(new ResourceLocation(RavageAndCabbage.MOD_ID, "ravager_saddle"), "ravager_saddle");
	public static ModelLayerLocation RAVAGER_HORNS = new ModelLayerLocation(new ResourceLocation(RavageAndCabbage.MOD_ID, "ravager_horns"), "ravager_horns");

    @SuppressWarnings("removal")
	@SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(RCEntities.CABBAGE.get(), ThrownItemRenderer::new);
        EntityRenderers.register(RCEntities.CORRUPTED_CABBAGE.get(), ThrownItemRenderer::new);

        ItemBlockRenderTypes.setRenderLayer(RCBlocks.CABBAGE_CROP.get(), RenderType.cutout());
    }

    @SubscribeEvent
    public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(RCEntities.CABBAGER.get(), CabbagerRenderer::new);
        event.registerEntityRenderer(RCEntities.RAVAGER.get(), RCRavagerRenderer::new);
        event.registerEntityRenderer(RCEntities.CORRUPTED_VILLAGER.get(), CorruptedVillagerRenderer::new);
    }
    
    @SubscribeEvent
	public static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event) {
    	event.registerLayerDefinition(CABBAGER, CabbagerModel::createBodyLayer);
    	event.registerLayerDefinition(RAVAGER, RCRavagerModel::createBodyLayer);
    	event.registerLayerDefinition(RAVAGER_SADDLE, RCRavagerModel::createBodyLayer);
    	event.registerLayerDefinition(RAVAGER_HORNS, RCRavagerModel::createBodyLayer);
    }

    @SuppressWarnings("deprecation")
	@SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void itemColors(RegisterColorHandlersEvent.Item event) {
        ItemColors handler = event.getItemColors();
        ItemColor armorColor = (stack, tintIndex) -> ((DyeableRavagerHornArmorItem) stack.getItem()).getColor(stack);
        handler.register(armorColor, RCItems.LEATHER_HORN_ARMOR.get());
    }

    @Mod.EventBusSubscriber(modid = RavageAndCabbage.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ForgeBusEvents {

        @SubscribeEvent
        public static void onKeyPress(InputEvent.Key event) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null) return;
            onInput(mc, event.getKey(), event.getAction());
        }

        @SubscribeEvent
        public static void onMouseClick(InputEvent.MouseButton event) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null) return;
            onInput(mc, event.getButton(), event.getAction());
        }

        private static void onInput(Minecraft mc, int key, int action) {
            if (mc.screen == null && roarKey.consumeClick()) {
                RCNetwork.CHANNEL.sendToServer(new KeyInputMessage(key));
            }
        }
    }
    
    public static KeyMapping roarKey;

    @SubscribeEvent
    public static void register(final RegisterKeyMappingsEvent event) {
        roarKey = create("attack_key", KeyEvent.VK_G);

        event.register(roarKey);
    }

    private static KeyMapping create(String name, int key) {
        return new KeyMapping("key." + RavageAndCabbage.MOD_ID + "." + name, key, "key.category." + RavageAndCabbage.MOD_ID);
    }
}