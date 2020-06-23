package coda.ravagecabbage.world.feature;

import coda.ravagecabbage.RavageCabbage;
import coda.ravagecabbage.entity.CabbageRavagerEntity;
import coda.ravagecabbage.init.EntityInit;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.RavagerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class BarnFeature extends Feature<NoFeatureConfig> {
    public static final ResourceLocation STRUCTURE1 = new ResourceLocation(RavageCabbage.MOD_ID, "barn1");
    public static final ResourceLocation STRUCTURE2 = new ResourceLocation(RavageCabbage.MOD_ID, "barn2");

    public BarnFeature() {
        super(NoFeatureConfig::deserialize);
        setRegistryName("barn");
    }

    @Override
    public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NoFeatureConfig config) {
        TemplateManager manager = ((ServerWorld) worldIn.getWorld()).getStructureTemplateManager();
        Template template1 = manager.getTemplateDefaulted(STRUCTURE1);
        Template template2 = manager.getTemplateDefaulted(STRUCTURE2);
        Rotation rotation = Rotation.values()[rand.nextInt(Rotation.values().length)];
        ChunkPos chunkpos = new ChunkPos(pos);
        MutableBoundingBox boundingBox = new MutableBoundingBox(chunkpos.getXStart() - 32, 0, chunkpos.getZStart() - 32, chunkpos.getXEnd() + 32, 256, chunkpos.getZEnd() + 32);
        PlacementSettings settings = new PlacementSettings().setRotation(rotation).setBoundingBox(boundingBox).setRandom(rand).addProcessor(BlockIgnoreStructureProcessor.AIR_AND_STRUCTURE_BLOCK);
        template1.addBlocksToWorld(worldIn, pos, settings);
        template2.addBlocksToWorld(worldIn, pos.offset(rotation.rotate(Direction.WEST), 8), settings);

        RavagerEntity ravager = EntityType.RAVAGER.create(worldIn.getWorld());
        if (ravager != null) {
            BlockPos p = pos.offset(rotation.rotate(Direction.SOUTH), 12).offset(rotation.rotate(Direction.EAST), 4);
            ravager.setPosition(p.getX(), p.getY() + 1, p.getZ());
            ravager.onInitialSpawn(worldIn, worldIn.getDifficultyForLocation(ravager.getPosition()), SpawnReason.STRUCTURE, null, null);
            worldIn.addEntity(ravager);
            for (int i = 0; i < rand.nextInt(3) + 3; i++) {
                p = pos.offset(rotation.rotate(Direction.SOUTH), 3 + i);
                CabbageRavagerEntity baby = EntityInit.RAVAGER.create(worldIn.getWorld());
                if (baby != null) {
                    baby.setPosition(p.getX(), p.getY() + 1, p.getZ());
                    baby.onInitialSpawn(worldIn, worldIn.getDifficultyForLocation(ravager.getPosition()), SpawnReason.STRUCTURE, null, null);
                    worldIn.addEntity(baby);
                }
            }
        }
        return true;
    }
}
