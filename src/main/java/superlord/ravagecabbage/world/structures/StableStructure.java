package superlord.ravagecabbage.world.structures;

import java.util.Random;

import com.mojang.serialization.Codec;

import net.minecraft.block.Blocks;
import net.minecraft.util.Rotation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;
import superlord.ravagecabbage.RavageAndCabbage;

public class StableStructure extends Structure<NoFeatureConfig> {

	public StableStructure(Codec<NoFeatureConfig> p_i51440_1_) {
        super(p_i51440_1_);
    }

    public String getStructureName() {
        return RavageAndCabbage.MOD_ID + ":stable";
    }

    protected boolean func_230363_a_(ChunkGenerator p_230363_1_, BiomeProvider p_230363_2_, long p_230363_3_, SharedSeedRandom p_230363_5_, int p_230363_6_, int p_230363_7_, Biome p_230363_8_, ChunkPos p_230363_9_, NoFeatureConfig p_230363_10_) {
        int i = p_230363_6_ >> 4;
        int j = p_230363_7_ >> 4;
        p_230363_5_.setSeed((long) (i ^ j << 4) ^ p_230363_3_);
        p_230363_5_.nextInt();
        for (int k = p_230363_6_ - 10; k <= p_230363_6_ + 10; ++k) {
            for (int l = p_230363_7_ - 10; l <= p_230363_7_ + 10; ++l) {
                ChunkPos chunkpos = Structure.VILLAGE.getChunkPosForStructure(p_230363_1_.func_235957_b_().func_236197_a_(Structure.VILLAGE), p_230363_3_, p_230363_5_, k, l);
                if (k == chunkpos.x && l == chunkpos.z) {
                    return false;
                }
            }
        }

        return true;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public Structure.IStartFactory getStartFactory() {
        return StableStructure.Start::new;
    }

    @Override
    public GenerationStage.Decoration getDecorationStage() {
        return GenerationStage.Decoration.SURFACE_STRUCTURES;
    }

    public static class Start extends StructureStart<NoFeatureConfig> {
        public Start(Structure<NoFeatureConfig> p_i225806_1_, int p_i225806_2_, int p_i225806_3_, MutableBoundingBox p_i225806_4_, int p_i225806_5_, long p_i225806_6_) {
            super(p_i225806_1_, p_i225806_2_, p_i225806_3_, p_i225806_4_, p_i225806_5_, p_i225806_6_);
        }

        @Override
        public void func_230364_a_(DynamicRegistries p_230364_1_, ChunkGenerator p_230364_2_, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biome, NoFeatureConfig p_230364_7_) {
            BlockPos blockpos = new BlockPos(chunkX * 16, 90, chunkZ * 16);

            Rotation rotation = Rotation.values()[this.rand.nextInt(Rotation.values().length)];
            StablePieces.addStructure(templateManagerIn, blockpos, rotation, this.components, this.rand, biome);
            this.recalculateStructureSize();
        }

        public void func_230366_a_(ISeedReader p_230366_1_, StructureManager p_230366_2_, ChunkGenerator p_230366_3_, Random p_230366_4_, MutableBoundingBox p_230366_5_, ChunkPos p_230366_6_) {
            super.func_230366_a_(p_230366_1_, p_230366_2_, p_230366_3_, p_230366_4_, p_230366_5_, p_230366_6_);
            int i = this.bounds.minY;

            for (int j = p_230366_5_.minX; j <= p_230366_5_.maxX; ++j) {
                for (int k = p_230366_5_.minZ; k <= p_230366_5_.maxZ; ++k) {
                    BlockPos blockpos = new BlockPos(j, i, k);
                    if (!p_230366_1_.isAirBlock(blockpos) && this.bounds.isVecInside(blockpos)) {
                        boolean flag = false;

                        for (StructurePiece structurepiece : this.components) {
                            if (structurepiece.getBoundingBox().isVecInside(blockpos)) {
                                flag = true;
                                break;
                            }
                        }

                        if (flag) {
                            for (int l = i - 1; l > 1; --l) {
                                BlockPos blockpos1 = new BlockPos(j, l, k);
                                if (!p_230366_1_.isAirBlock(blockpos1) && !p_230366_1_.getBlockState(blockpos1).getMaterial().isLiquid()) {
                                    break;
                                }

                                p_230366_1_.setBlockState(blockpos1, Blocks.DIRT.getDefaultState(), 2);
                            }
                        }
                    }
                }
            }

        }
    }
}
