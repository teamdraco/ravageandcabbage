package superlord.ravagecabbage.world.structures;

import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.ImmutableMap;

import net.minecraft.block.Blocks;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.TemplateStructurePiece;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import superlord.ravagecabbage.RavageAndCabbage;
import superlord.ravagecabbage.entity.CabbagerEntity;
import superlord.ravagecabbage.entity.RavageAndCabbageRavagerEntity;
import superlord.ravagecabbage.init.EntityInit;
import superlord.ravagecabbage.world.RCStructures;

public class StablePieces {

	private static final ResourceLocation BARN = new ResourceLocation(RavageAndCabbage.MOD_ID, "barn");
	private static final Map<ResourceLocation, BlockPos> OFFSET = ImmutableMap.of(BARN, new BlockPos(0, 0, 0));


	public static void addStructure(TemplateManager p_207617_0_, BlockPos p_207617_1_, Rotation p_207617_2_, List<StructurePiece> p_207617_3_, Random p_207617_4_, Biome biome) {
		p_207617_3_.add(new StablePieces.Piece(p_207617_0_, BARN, p_207617_1_, p_207617_2_, 0));
	}

	public static class Piece extends TemplateStructurePiece {
		private final ResourceLocation field_207615_d;
		private final Rotation field_207616_e;

		public Piece(TemplateManager p_i49313_1_, ResourceLocation p_i49313_2_, BlockPos p_i49313_3_, Rotation p_i49313_4_, int p_i49313_5_) {
			super(RCStructures.STABLE_PIECE, 0);
			this.field_207615_d = p_i49313_2_;
			BlockPos blockpos = StablePieces.OFFSET.get(p_i49313_2_);
			this.templatePosition = p_i49313_3_.add(blockpos.getX(), blockpos.getY() - p_i49313_5_, blockpos.getZ());
			this.field_207616_e = p_i49313_4_;
			this.func_207614_a(p_i49313_1_);
		}

		public Piece(TemplateManager p_i50566_1_, CompoundNBT p_i50566_2_) {
			super(RCStructures.STABLE_PIECE, p_i50566_2_);
			this.field_207615_d = new ResourceLocation(p_i50566_2_.getString("Template"));
			this.field_207616_e = Rotation.valueOf(p_i50566_2_.getString("Rot"));
			this.func_207614_a(p_i50566_1_);
		}

		private void func_207614_a(TemplateManager p_207614_1_) {
			Template template = p_207614_1_.getTemplateDefaulted(this.field_207615_d);
			PlacementSettings placementsettings = (new PlacementSettings()).setRotation(this.field_207616_e).setMirror(Mirror.NONE).addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
			this.setup(template, this.templatePosition, placementsettings);
		}

		protected void readAdditional(CompoundNBT tagCompound) {
			super.readAdditional(tagCompound);
			tagCompound.putString("Template", this.field_207615_d.toString());
			tagCompound.putString("Rot", this.field_207616_e.name());
		}

		@Override
		public boolean func_230383_a_(ISeedReader worldIn, StructureManager manager, ChunkGenerator chunkGenerator, Random rand, MutableBoundingBox mutableBoundingBox, ChunkPos chunkPos, BlockPos pos) {
			BlockPos blockpos1 = this.templatePosition;
			int i = worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, blockpos1.getX(), blockpos1.getZ());
			BlockPos blockpos2 = this.templatePosition;
			this.templatePosition = this.templatePosition.add(0, i - 90 - 1, 0);
			boolean flag = super.func_230383_a_(worldIn, manager, chunkGenerator, rand, mutableBoundingBox, chunkPos, pos);

			this.templatePosition = blockpos2;

			CabbagerEntity cabbagerEntity = EntityInit.CABBAGER.get().create(worldIn.getWorld());
			cabbagerEntity.enablePersistence();
			cabbagerEntity.setLocationAndAngles((double) pos.getX() + 1.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 1.5D, 0f, 0f);
			cabbagerEntity.onInitialSpawn(worldIn, worldIn.getDifficultyForLocation(pos), SpawnReason.STRUCTURE, (ILivingEntityData) null, (CompoundNBT) null);
			worldIn.addEntity(cabbagerEntity);
			RavageAndCabbageRavagerEntity ravagerEntity = EntityInit.RAVAGER.get().create(worldIn.getWorld());
			ravagerEntity.enablePersistence();
			ravagerEntity.setLocationAndAngles((double) pos.getX() + 2.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 2.5D, 0f, 0f);
			ravagerEntity.onInitialSpawn(worldIn, worldIn.getDifficultyForLocation(pos), SpawnReason.STRUCTURE, (ILivingEntityData) null, (CompoundNBT) null);worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
			worldIn.addEntity(ravagerEntity);

			return flag;
		}
		@Override
		protected void handleDataMarker(String function, BlockPos pos, IServerWorld worldIn, Random rand, MutableBoundingBox sbb) {

		}

	}

}
