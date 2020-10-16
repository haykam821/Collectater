package io.github.haykam821.collectater.component;

import io.github.haykam821.collectater.Main;
import nerdhub.cardinal.components.api.ComponentType;
import nerdhub.cardinal.components.api.util.sync.LevelSyncedComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.WorldProperties;

public class CollectaterGlobalStateComponent implements LevelSyncedComponent {
	private int timerTicks = 0;
	private int maximumCollectaters = 0;

	public CollectaterGlobalStateComponent(WorldProperties worldProperties) {
		return;
	}

	public boolean isTimerRunning() {
		return this.timerTicks > 0;
	}

	public void startTimer() {
		this.timerTicks = 20 * 16;
	}

	public void setTimer(int ticks) {
		this.timerTicks = ticks;
	}

	public int getMaximumCollectaters() {
		return this.maximumCollectaters;
	}

	public void setMaximumCollectaters(int maximumCollectaters) {
		this.maximumCollectaters = maximumCollectaters;
	}

	@Override
	public void fromTag(CompoundTag tag) {
		this.timerTicks = tag.getInt("TimerTicks");
		this.maximumCollectaters = tag.getInt("MaximumCollectaters");
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag.putInt("TimerTicks", this.timerTicks);
		tag.putInt("MaximumCollectaters", this.maximumCollectaters);
		return tag;
	}

	@Override
	public ComponentType<?> getComponentType() {
		return Main.COLLECTATER_GLOBAL_STATE;
	}

	public void tick(MinecraftServer server) {
		if (this.timerTicks > 0) {
			if (this.timerTicks % 15 == 0 || (this.timerTicks <= 40 && this.timerTicks % 5 == 0)) {
				for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
					player.playSound(SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.RECORDS, 1, 1.2f);
				}
			}
			this.timerTicks -= 1;
		}
	}
}