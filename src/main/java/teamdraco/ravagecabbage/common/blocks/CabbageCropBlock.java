package teamdraco.ravagecabbage.common.blocks;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import teamdraco.ravagecabbage.registry.RCItems;

public class CabbageCropBlock extends CropBlock {
	public static final IntegerProperty AGE = BlockStateProperties.AGE_5;
	public static final BooleanProperty CORRUPTED = BooleanProperty.create("corrupted");
	private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)};

	public CabbageCropBlock(BlockBehaviour.Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0).setValue(CORRUPTED, false));
	}

	public IntegerProperty getAgeProperty() {
		return AGE;
	}

	public int getMaxAge() {
		return 5;
	}

	protected ItemLike getBaseSeedId() {
		return RCItems.CABBAGE_SEEDS.get();
	}

	@SuppressWarnings("deprecation")
	public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, Random random) {
		if (random.nextInt(3) != 0) {
			if (!worldIn.isAreaLoaded(pos, 1)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light
			if (worldIn.getRawBrightness(pos, 0) >= 9) {
				int i = this.getAge(state);
				if (i < this.getMaxAge()) {
					float f = getGrowthSpeed(this, worldIn, pos);
					if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos, state, random	.nextInt((int)(25.0F / f) + 1) == 0)) {
						worldIn.setBlock(pos, this.defaultBlockState().setValue(CORRUPTED, this.getCorruption(state)).setValue(AGE, i + 1), 2);
						net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state);
					}
				}
			}
		}
		if (random.nextInt(1000) < 1 && this.getAge(state) < this.getMaxAge()) {
			worldIn.setBlock(pos, this.defaultBlockState().setValue(CORRUPTED, true).setValue(AGE, this.getAge(state)), 2);
		}

	}

	protected int getBonemealAgeIncrease(Level worldIn) {
		return Mth.nextInt(worldIn.random, 2, 5);
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(AGE, CORRUPTED);
	}

	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return SHAPE_BY_AGE[state.getValue(this.getAgeProperty())];
	}

	public BlockState getStateForCorruption(boolean corrupted) {
		return this.defaultBlockState().setValue(this.getCorruptionProperty(), Boolean.valueOf(corrupted));
	}

	public BooleanProperty getCorruptionProperty() {
		return CORRUPTED;
	}

	protected boolean getCorruption(BlockState p_52306_) {
		return p_52306_.getValue(this.getCorruptionProperty());
	}

}
