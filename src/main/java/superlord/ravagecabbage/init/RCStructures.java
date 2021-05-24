package superlord.ravagecabbage.init;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import superlord.ravagecabbage.RavageAndCabbage;
import superlord.ravagecabbage.world.structures.StableStructure;

@Mod.EventBusSubscriber(modid = RavageAndCabbage.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RCStructures {
	public static IStructurePieceType STABLE_PIECE_TYPE = StableStructure.Piece::new;

	public static final DeferredRegister<Structure<?>> REGISTER = DeferredRegister.create(ForgeRegistries.STRUCTURE_FEATURES, RavageAndCabbage.MOD_ID);

	public static final RegistryObject<Structure<NoFeatureConfig>> STABLE = REGISTER.register("stable", () -> (new StableStructure(NoFeatureConfig.field_236558_a_)));

	public static void setupStructures() {
		setupMapSpacingAndLand(STABLE.get(), new StructureSeparationSettings(100, 50, 2538959), true);
	}

	public static <F extends Structure<?>> void setupMapSpacingAndLand(F structure, StructureSeparationSettings structureSeparationSettings, boolean transformSurroundingLand) {
		Structure.NAME_STRUCTURE_BIMAP.put(structure.getRegistryName().toString(), structure);

		if (transformSurroundingLand) {
			Structure.field_236384_t_ = ImmutableList.<Structure<?>>builder().addAll(Structure.field_236384_t_).add(structure).build();
		}

		DimensionStructuresSettings.field_236191_b_ = ImmutableMap.<Structure<?>, StructureSeparationSettings>builder().putAll(DimensionStructuresSettings.field_236191_b_).put(structure, structureSeparationSettings).build();
	}

	public static void registerStructurePieces() {
		Registry.register(Registry.STRUCTURE_PIECE, new ResourceLocation(RavageAndCabbage.MOD_ID, "stable"), STABLE_PIECE_TYPE);

	}
}