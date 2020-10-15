package io.github.haykam821.collectater.block.entity;

import io.github.haykam821.collectater.Main;
import io.github.haykam821.collectater.component.CollectatersComponent;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CollectaterBlockEntity extends BlockEntity {
	private Identifier id;

	public CollectaterBlockEntity(BlockEntityType<?> type) {
		super(type);
	}

	public CollectaterBlockEntity() {
		super(Main.COLLECTATER_BLOCK_ENTITY_TYPE);
	}

	private void spawnReactionParticle(ParticleEffect effect, double offset) {
		if (!this.world.isClient()) {
			Vec3d spawnPos = Vec3d.of(this.getPos()).add(0.5, offset, 0.5);
			((ServerWorld) world).spawnParticles(effect, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), 1, 0, 0, 0, 0.2);
		}
	}

	private void spawnReactionParticle(ParticleEffect effect) {
		this.spawnReactionParticle(effect, 0.65);
	}

	public boolean canCollect() {
		return true;
	}

	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (this.id == null) return ActionResult.FAIL;
		if (!this.canCollect()) return ActionResult.PASS;

		CollectatersComponent component = Main.COLLECTATERS.get(player);
		if (component.hasCollected(this.id)) {
			player.sendMessage(new TranslatableText("block.collectater.collectater.already_collected"), true);

			this.spawnReactionParticle(ParticleTypes.ANGRY_VILLAGER);
			player.playSound(SoundEvents.ENTITY_VILLAGER_NO, SoundCategory.BLOCKS, 1, 1);
		} else {
			component.collect(this.id);

			this.spawnReactionParticle(ParticleTypes.HAPPY_VILLAGER);
			player.playSound(SoundEvents.ENTITY_VILLAGER_YES, SoundCategory.BLOCKS, 1, 1);
		}
		
		return ActionResult.SUCCESS;
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		if (this.id != null) {
			tag.putString("Id", this.id.toString());
		}
		return super.toTag(tag);
	}

	@Override
	public void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);
		
		Identifier id = Identifier.tryParse(tag.getString("Id"));
		if (id != null) {
			this.id = id;
		}
	}
}