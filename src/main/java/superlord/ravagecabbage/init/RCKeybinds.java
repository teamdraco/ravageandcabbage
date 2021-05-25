package superlord.ravagecabbage.init;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import superlord.ravagecabbage.RavageAndCabbage;

import java.awt.event.KeyEvent;

@OnlyIn(Dist.CLIENT)
public class RCKeybinds {
    public static KeyBinding roarKey;

    public static void register(final FMLClientSetupEvent event) {
        roarKey = create("attack_key", KeyEvent.VK_G);

        ClientRegistry.registerKeyBinding(roarKey);
    }

    private static KeyBinding create(String name, int key) {
        return new KeyBinding("key." + RavageAndCabbage.MOD_ID + "." + name, key, "key.category." + RavageAndCabbage.MOD_ID);
    }
}
