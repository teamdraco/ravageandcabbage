package superlord.ravagecabbage.world;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import superlord.ravagecabbage.RavageAndCabbage;
import superlord.ravagecabbage.world.structures.StablePieces;
import superlord.ravagecabbage.world.structures.StableStructure;

@Mod.EventBusSubscriber(modid = RavageAndCabbage.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RCStructures {
	
	public static final Structure<NoFeatureConfig> STABLE_STRUCTURE = new StableStructure(NoFeatureConfig.field_236558_a_);
	
	public static final StructureFeature<NoFeatureConfig, ? extends Structure<NoFeatureConfig>> STABLE = func_244162_a(prefix("stable"), STABLE_STRUCTURE.withConfiguration(NoFeatureConfig.field_236559_b_));

	public static final IStructurePieceType STABLE_PIECE = registerStructurePiece(new ResourceLocation(RavageAndCabbage.MOD_ID, "stable"), StablePieces.Piece::new);

	public static <C extends IFeatureConfig> IStructurePieceType registerStructurePiece(ResourceLocation key, IStructurePieceType pieceType) {
		return Registry.register(Registry.STRUCTURE_PIECE, key, pieceType);
	}
	
	@SubscribeEvent
	public static void registerStructure(RegistryEvent.Register<Structure<?>> registry) {
		DimensionSettings.func_242746_i().getStructures().func_236195_a_().put(STABLE_STRUCTURE, new StructureSeparationSettings(20, 5, 247872515));
		registry.getRegistry().register(STABLE_STRUCTURE.setRegistryName(RavageAndCabbage.MOD_ID, "stable"));
		Structure.NAME_STRUCTURE_BIMAP.put(prefix("stable"), STABLE_STRUCTURE);
	}
	
	private static <FC extends IFeatureConfig, F extends Structure<FC>> StructureFeature<FC, F> func_244162_a(String p_244162_0_, StructureFeature<FC, F> p_244162_1_) {
		return WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, p_244162_0_, p_244162_1_);
	}

	private static String prefix(String path) {
		return RavageAndCabbage.MOD_ID + ":" + path;
	}
	
}
