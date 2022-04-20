package teamdraco.ravagecabbage.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import teamdraco.ravagecabbage.RavageAndCabbage;
import teamdraco.ravagecabbage.client.render.CabbagerRenderer;
import teamdraco.ravagecabbage.client.render.RCRavagerRenderer;
import teamdraco.ravagecabbage.registry.RCBlocks;
import teamdraco.ravagecabbage.registry.RCEntities;
import teamdraco.ravagecabbage.registry.RCItems;
import teamdraco.ravagecabbage.registry.RCKeybinds;
import teamdraco.ravagecabbage.common.items.DyeableRavagerHornArmorItem;
import teamdraco.ravagecabbage.network.RCNetwork;
import teamdraco.ravagecabbage.network.KeyInputMessage;

@Mod.EventBusSubscriber(modid = RavageAndCabbage.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        RCKeybinds.register(event);

        EntityRenderers.register(RCEntities.CABBAGE.get(), ThrownItemRenderer::new);

        ItemBlockRenderTypes.setRenderLayer(RCBlocks.CABBAGE_CROP.get(), RenderType.cutout());
    }

    @SubscribeEvent
    public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(RCEntities.CABBAGER.get(), CabbagerRenderer::new);
        event.registerEntityRenderer(RCEntities.RAVAGER.get(), RCRavagerRenderer::new);
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void itemColors(ColorHandlerEvent.Item event) {
        ItemColors handler = event.getItemColors();
        ItemColor armorColor = (stack, tintIndex) -> ((DyeableRavagerHornArmorItem) stack.getItem()).getColor(stack);
        handler.register(armorColor, RCItems.LEATHER_HORN_ARMOR.get());
    }

    @Mod.EventBusSubscriber(modid = RavageAndCabbage.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ForgeBusEvents {

        @SubscribeEvent
        public static void onKeyPress(InputEvent.KeyInputEvent event) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null) return;
            onInput(mc, event.getKey(), event.getAction());
        }

        @SubscribeEvent
        public static void onMouseClick(InputEvent.MouseInputEvent event) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null) return;
            onInput(mc, event.getButton(), event.getAction());
        }

        private static void onInput(Minecraft mc, int key, int action) {
            if (mc.screen == null && RCKeybinds.roarKey.consumeClick()) {
                RCNetwork.CHANNEL.sendToServer(new KeyInputMessage(key));
            }
        }
    }
}