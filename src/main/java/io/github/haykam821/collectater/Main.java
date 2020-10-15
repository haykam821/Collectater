package io.github.haykam821.collectater;

import io.github.haykam821.collectater.block.ModBlocks;
import io.github.haykam821.collectater.block.entity.CollectaterBlockEntity;
import io.github.haykam821.collectater.command.CollectaterCommand;
import io.github.haykam821.collectater.component.CollectatersComponent;
import nerdhub.cardinal.components.api.ComponentRegistry;
import nerdhub.cardinal.components.api.ComponentType;
import nerdhub.cardinal.components.api.event.EntityComponentCallback;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Main implements ModInitializer {
	public static final String MOD_ID = "collectater";

	// Block entity
	private static final Identifier COLLECTATER_BLOCK_ENTITY_TYPE_ID = new Identifier(MOD_ID, "collectater");
	public static final BlockEntityType<CollectaterBlockEntity> COLLECTATER_BLOCK_ENTITY_TYPE = BlockEntityType.Builder
		.create(CollectaterBlockEntity::new, ModBlocks.COLLECTATER.getBlock())
		.build(null);

	// Component
	private static final Identifier COLLECTATERS_ID = new Identifier(MOD_ID, "collectaters");
	public static final ComponentType<CollectatersComponent> COLLECTATERS = ComponentRegistry.INSTANCE
			.registerIfAbsent(COLLECTATERS_ID, CollectatersComponent.class)
			.attach(EntityComponentCallback.event(PlayerEntity.class), CollectatersComponent::new);

	@Override
	public void onInitialize() {
		// Blocks
		ModBlocks.register();

		// Block entity
		Registry.register(Registry.BLOCK_ENTITY_TYPE, COLLECTATER_BLOCK_ENTITY_TYPE_ID, COLLECTATER_BLOCK_ENTITY_TYPE);
		
		// Command
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			CollectaterCommand.register(dispatcher);
		});
	}
}