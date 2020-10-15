package io.github.haykam821.collectater.block;

import io.github.haykam821.collectater.block.entity.CollectaterBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class CollectaterBlock extends HorizontalFacingBlock implements BlockEntityProvider {
	private static final VoxelShape SHAPE = Block.createCuboidShape(5, 0, 5, 11, 10, 11);

	public CollectaterBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient()) {
			return ActionResult.SUCCESS;
		}

		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof CollectaterBlockEntity) {
			return ((CollectaterBlockEntity) blockEntity).onUse(state, world, pos, player, hand, hit);
		}

		return ActionResult.PASS;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		return this.getDefaultState().with(HorizontalFacingBlock.FACING, context.getPlayerFacing().getOpposite());
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(HorizontalFacingBlock.FACING);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new CollectaterBlockEntity();
	}
}