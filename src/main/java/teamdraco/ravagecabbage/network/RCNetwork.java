package teamdraco.ravagecabbage.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import teamdraco.ravagecabbage.RavageAndCabbage;

public class RCNetwork {
    public static final String NETWORK_VERSION = "0.1.0";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(RavageAndCabbage.MOD_ID, "network"), () -> NETWORK_VERSION, version -> version.equals(NETWORK_VERSION), version -> version.equals(NETWORK_VERSION));

    public static void init() {
        CHANNEL.registerMessage(0, KeyInputMessage.class, KeyInputMessage::encode, KeyInputMessage::decode, KeyInputMessage::handle);
    }
}
