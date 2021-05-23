package superlord.ravagecabbage.blocks;

import java.util.Random;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import superlord.ravagecabbage.init.RCItems;

public class CabbageCropBlock extends CropsBlock {
	public static final IntegerProperty AGE = BlockStateProperties.AGE_0_5;
	private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)};

	public CabbageCropBlock(AbstractBlock.Properties properties) {
		super(properties);
	}

	public IntegerProperty getAgeProperty() {
		return AGE;
	}

	public int getMaxAge() {
		return 5;
	}

	protected IItemProvider getSeedsItem() {
		return RCItems.CABBAGE_SEEDS.get();
	}

	public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
		if (random.nextInt(3) != 0) {
			super.randomTick(state, worldIn, pos, random);
		}

	}

	protected int getBonemealAgeIncrease(World worldIn) {
		return MathHelper.nextInt(worldIn.rand, 2, 5);
	}

	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(AGE);
	}

	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return SHAPE_BY_AGE[state.get(this.getAgeProperty())];
	}

}
