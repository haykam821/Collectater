package io.github.haykam821.collectater.block;

import io.github.haykam821.collectater.Main;
import io.github.haykam821.collectater.component.CollectaterGlobalStateComponent;
import net.minecraft.block.BlockState;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TaterPlateBlock extends PressurePlateBlock {
	public TaterPlateBlock(ActivationRule type, Settings settings) {
		super(type, settings);
	}

	@Override
	public void updatePlateState(World world, BlockPos pos, BlockState state, int power) {
		if (this.getRedstoneOutput(world, pos) > 0 && power <= 0) {
			CollectaterGlobalStateComponent component = Main.COLLECTATER_GLOBAL_STATE.get(world.getLevelProperties());
			component.startTimer();
		} 
		super.updatePlateState(world, pos, state, power);
	}
}