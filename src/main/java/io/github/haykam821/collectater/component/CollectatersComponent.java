package io.github.haykam821.collectater.component;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import nerdhub.cardinal.components.api.util.sync.EntitySyncedComponent;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.util.Identifier;

public class CollectatersComponent implements EntitySyncedComponent {
	private final PlayerEntity player;
	private final Set<Identifier> collectaters = new HashSet<>();

	public CollectatersComponent(PlayerEntity player) {
		this.player = player;
	}

	public int getCollectedCount() {
		return this.collectaters.size();
	}

	public boolean hasCollected(Identifier id) {
		return this.collectaters.contains(id);
	}

	public void collect(Identifier id) {
		this.collectaters.add(id);
		this.sync();
	}

	public void uncollect(Identifier id) {
		this.collectaters.remove(id);
		this.sync();
	}

	public void clear() {
		this.collectaters.clear();
		this.sync();
	}

	public Iterator<Identifier> getIterator() {
		return this.collectaters.iterator();
	}

	@Override
	public Entity getEntity() {
		return this.player;
	}

	@Override
	public void fromTag(CompoundTag tag) {
		this.collectaters.clear();

		ListTag collectatersTag = tag.getList("Collectaters", NbtType.STRING);
		for (int index = 0; index < collectatersTag.size(); index++) {
			Identifier id = Identifier.tryParse(collectatersTag.getString(index));
			if (id != null) {
				this.collectaters.add(id);
			}
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		ListTag collectatersTag = new ListTag();
		for (Identifier id : this.collectaters) {
			collectatersTag.add(StringTag.of(id.toString()));
		}

		tag.put("Collectaters", collectatersTag);
		return tag;
	}
}