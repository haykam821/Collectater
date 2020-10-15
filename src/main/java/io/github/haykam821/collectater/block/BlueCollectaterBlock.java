package io.github.haykam821.collectater.block;

import io.github.haykam821.collectater.block.entity.BlueCollectaterBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.world.BlockView;

public class BlueCollectaterBlock extends CollectaterBlock {
	public static final BooleanProperty TANGIBLE = BooleanProperty.of("tangible");

	public BlueCollectaterBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState().with(TANGIBLE, false));
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