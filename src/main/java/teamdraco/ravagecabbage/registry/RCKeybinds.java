package teamdraco.ravagecabbage.registry;

import java.awt.event.KeyEvent;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import teamdraco.ravagecabbage.RavageAndCabbage;

@OnlyIn(Dist.CLIENT)
public class RCKeybinds {
	public static KeyMapping roarKey;

    public static void register(final FMLClientSetupEvent event) {
        roarKey = create("attack_key", KeyEvent.VK_G);

        ClientRegistry.registerKeyBinding(roarKey);
    }

    private static KeyMapping create(String name, int key) {
        return new KeyMapping("key." + RavageAndCabbage.MOD_ID + "." + name, key, "key.category." + RavageAndCabbage.MOD_ID);
    }
}
