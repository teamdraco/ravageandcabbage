package superlord.ravagecabbage.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import superlord.ravagecabbage.RavageAndCabbage;
import superlord.ravagecabbage.init.RCKeybinds;
import superlord.ravagecabbage.network.RCNetwork;
import superlord.ravagecabbage.network.message.InputMessage;

@Mod.EventBusSubscriber(modid = RavageAndCabbage.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class InputEvents {

    @SubscribeEvent
    public static void onKeyPress(InputEvent.KeyInputEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.world == null) return;
        onInput(mc, event.getKey(), event.getAction());
    }

    @SubscribeEvent
    public static void onMouseClick(InputEvent.MouseInputEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.world == null) return;
        onInput(mc, event.getButton(), event.getAction());
    }

    private static void onInput(Minecraft mc, int key, int action) {
        if (mc.currentScreen == null && RCKeybinds.roarKey.isPressed()) {
            RCNetwork.CHANNEL.sendToServer(new InputMessage(key));
        }
    }
}
