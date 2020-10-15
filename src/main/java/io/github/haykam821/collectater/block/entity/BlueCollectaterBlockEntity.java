package io.github.haykam821.collectater.block.entity;

import io.github.haykam821.collectater.Main;
import io.github.haykam821.collectater.block.BlueCollectaterBlock;
import io.github.haykam821.collectater.component.CollectaterGlobalStateComponent;
import net.minecraft.util.Tickable;

public class BlueCollectaterBlockEntity extends CollectaterBlockEntity implements Tickable {
	public BlueCollectaterBlockEntity() {
		super(Main.BLUE_COLLECTATER_BLOCK_ENTITY_TYPE);
	}

	@Override
	public boolean canCollect() {
		CollectaterGlobalStateComponent component = Main.COLLECTATER_GLOBAL_STATE.get(this.getWorld().getLevelProperties());
		return component.isTimerRunning();
	}

	@Override
	public void tick() {
		if (!this.getWorld().isClient()) {
			CollectaterGlobalStateComponent component = Main.COLLECTATER_GLOBAL_STATE.get(this.getWorld().getLevelProperties());
			if (component.isTimerRunning() != this.getCachedState().get(BlueCollectaterBlock.TANGIBLE)) {
				this.getWorld().setBlockState(this.getPos(), this.getCachedState().with(BlueCollectaterBlock.TANGIBLE, component.isTimerRunning()), 3);
			}
		}
	}
}