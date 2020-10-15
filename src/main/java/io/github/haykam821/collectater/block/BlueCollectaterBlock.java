package io.github.haykam821.collectater.block;

import io.github.haykam821.collectater.block.entity.BlueCollectaterBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BlueCollectaterBlock extends CollectaterBlock {
	public static final BooleanProperty TANGIBLE = BooleanProperty.of("tangible");

	public BlueCollectaterBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState().with(TANGIBLE, false));
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (!world.getBlockState(pos).get(TANGIBLE)) {
			return ActionResult.PASS;
		}
		return super.onUse(state, world, pos, player, hand, hit);
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(TANGIBLE);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new BlueCollectaterBlockEntity();
	}
}