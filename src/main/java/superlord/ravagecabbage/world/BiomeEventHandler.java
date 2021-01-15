package superlord.ravagecabbage.world;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import superlord.ravagecabbage.RavageAndCabbage;

@Mod.EventBusSubscriber(modid = RavageAndCabbage.MOD_ID)
public class BiomeEventHandler {
	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void addSpawn(BiomeLoadingEvent event) {
		BiomeGenerationSettingsBuilder generation = event.getGeneration();
		if (event.getName().getNamespace().contains("minecraft") || event.getName().getNamespace().contains("biomesoplenty")) {
			if (event.getCategory() == Biome.Category.PLAINS || event.getCategory() == Biome.Category.TAIGA || event.getCategory() == Biome.Category.SAVANNA || event.getCategory() == Biome.Category.DESERT) {
				generation.withStructure(RCStructures.STABLE);
			}
		}
	}

}
