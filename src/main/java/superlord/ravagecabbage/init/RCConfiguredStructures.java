package superlord.ravagecabbage.init;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import superlord.ravagecabbage.RavageAndCabbage;

public class RCConfiguredStructures {
    public static StructureFeature<?, ?> CONFIGURED_STABLE = RCStructures.STABLE.get().withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG);

    public static void registerConfiguredStructures() {
        Registry<StructureFeature<?, ?>> registry = WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE;
        Registry.register(registry, new ResourceLocation(RavageAndCabbage.MOD_ID, "configured_stable"), CONFIGURED_STABLE);

        FlatGenerationSettings.STRUCTURES.put(RCStructures.STABLE.get(), CONFIGURED_STABLE);
    }
}
