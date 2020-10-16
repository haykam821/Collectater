package io.github.haykam821.collectater;

import io.github.haykam821.collectater.block.ModBlocks;
import io.github.haykam821.collectater.block.entity.BlueCollectaterBlockEntity;
import io.github.haykam821.collectater.block.entity.CollectaterBlockEntity;
import io.github.haykam821.collectater.command.CollectaterCommand;
import io.github.haykam821.collectater.component.CollectaterGlobalStateComponent;
import io.github.haykam821.collectater.component.CollectatersComponent;
import nerdhub.cardinal.components.api.ComponentRegistry;
import nerdhub.cardinal.components.api.ComponentType;
import nerdhub.cardinal.components.api.event.EntityComponentCallback;
import nerdhub.cardinal.components.api.event.LevelComponentCallback;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Main implements ModInitializer {
	public static final String MOD_ID = "collectater";

	// Item group
	private static final Identifier COLLECTATERS_ID = new Identifier(MOD_ID, "collectaters");
	public static final ItemGroup COLLECTATERS_ITEM_GROUP = FabricItemGroupBuilder.build(COLLECTATERS_ID, () -> {
		return new ItemStack(ModBlocks.COLLECTATER.getItem());
	});

	// Block entities
	private static final Identifier COLLECTATER_BLOCK_ENTITY_TYPE_ID = new Identifier(MOD_ID, "collectater");
	public static final BlockEntityType<CollectaterBlockEntity> COLLECTATER_BLOCK_ENTITY_TYPE = BlockEntityType.Builder
		.create(CollectaterBlockEntity::new, ModBlocks.COLLECTATER.getBlock())
		.build(null);

	private static final Identifier BLUE_COLLECTATER_BLOCK_ENTITY_TYPE_ID = new Identifier(MOD_ID, "blue_collectater");
	public static final BlockEntityType<BlueCollectaterBlockEntity> BLUE_COLLECTATER_BLOCK_ENTITY_TYPE = BlockEntityType.Builder
		.create(BlueCollectaterBlockEntity::new, ModBlocks.BLUE_COLLECTATER.getBlock())
		.build(null);

	// Components
	public static final ComponentType<CollectatersComponent> COLLECTATERS = ComponentRegistry.INSTANCE
			.registerIfAbsent(COLLECTATERS_ID, CollectatersComponent.class)
			.attach(EntityComponentCallback.event(PlayerEntity.class), CollectatersComponent::new);

	private static final Identifier COLLECTATER_GLOBAL_STATE_ID = new Identifier(MOD_ID, "collectater_global_state");
	public static final ComponentType<CollectaterGlobalStateComponent> COLLECTATER_GLOBAL_STATE = ComponentRegistry.INSTANCE
			.registerIfAbsent(COLLECTATER_GLOBAL_STATE_ID, CollectaterGlobalStateComponent.class)
			.attach(LevelComponentCallback.EVENT, CollectaterGlobalStateComponent::new);

	@Override
	public void onInitialize() {
		// Blocks
		ModBlocks.register();

		// Block entity
		Registry.register(Registry.BLOCK_ENTITY_TYPE, COLLECTATER_BLOCK_ENTITY_TYPE_ID, COLLECTATER_BLOCK_ENTITY_TYPE);
		Registry.register(Registry.BLOCK_ENTITY_TYPE, BLUE_COLLECTATER_BLOCK_ENTITY_TYPE_ID, BLUE_COLLECTATER_BLOCK_ENTITY_TYPE);

		// Component
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			CollectaterGlobalStateComponent component = Main.COLLECTATER_GLOBAL_STATE.get(server.getOverworld().getLevelProperties());
			component.tick(server);
		});
		
		// Command
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			CollectaterCommand.register(dispatcher);
		});
	}
}