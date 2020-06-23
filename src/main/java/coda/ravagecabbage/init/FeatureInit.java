package coda.ravagecabbage.init;

import coda.ravagecabbage.RavageCabbage;
import coda.ravagecabbage.world.feature.BarnFeature;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RavageCabbage.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FeatureInit {
    public static final Feature<NoFeatureConfig> BARN = new BarnFeature();

    @SubscribeEvent
    public static void registerFatures(final RegistryEvent.Register<Feature<?>> event) {
        event.getRegistry().register(BARN);
        Biomes.PLAINS.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, BARN.withConfiguration(NoFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.CHANCE_HEIGHTMAP.configure(new ChanceConfig(100))));
    }
}
