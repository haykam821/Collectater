package io.github.haykam821.collectater.command;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import io.github.haykam821.collectater.Main;
import io.github.haykam821.collectater.block.CollectaterBlock;
import io.github.haykam821.collectater.component.CollectaterGlobalStateComponent;
import io.github.haykam821.collectater.component.CollectatersComponent;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public class CollectaterCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> baseBuilder = CommandManager.literal("collectater").requires(source -> {
			return source.hasPermissionLevel(2);
		});

		// Give a collectater stack
		registerSubcommand("give", baseBuilder, builder -> {
			builder
				.then(CommandManager.argument("item", ItemStackArgumentType.itemStack())
				.suggests((source, suggestionsBuilder) -> {
					Iterator<Map.Entry<RegistryKey<Item>, Item>> iterator = Registry.ITEM.getEntries().iterator();
					while (iterator.hasNext()) {
						Map.Entry<RegistryKey<Item>, Item> entry = iterator.next();
						Item item = entry.getValue();
		
						if (CollectaterCommand.isValidCollectaterItem(item)) {
							suggestionsBuilder.suggest(entry.getKey().getValue().toString(), item.getName());
						}
					}

					return suggestionsBuilder.buildFuture();
				})
					.then(CommandManager.argument("id", IdentifierArgumentType.identifier())
					.executes(context -> {
						return CollectaterCommand.give(context, Collections.singleton(context.getSource().getPlayer()), 1);
					})
						.then(CommandManager.argument("targets", EntityArgumentType.players())
						.executes(context -> {
							return CollectaterCommand.give(context, EntityArgumentType.getPlayers(context, "targets"), 1);
						})
							.then(CommandManager.argument("count", IntegerArgumentType.integer(1))
							.executes(context -> {
								return CollectaterCommand.give(context, EntityArgumentType.getPlayers(context, "targets"), IntegerArgumentType.getInteger(context, "count"));
							})))));
		});

		// Collect a collectater
		registerSubcommand("collect", baseBuilder, builder -> {
			builder
				.then(CommandManager.argument("id", IdentifierArgumentType.identifier())
				.executes(context -> {
					return CollectaterCommand.collect(context, Collections.singleton(context.getSource().getPlayer()));
				})
					.then(CommandManager.argument("targets", EntityArgumentType.players())
					.executes(context -> {
						return CollectaterCommand.collect(context, EntityArgumentType.getPlayers(context, "targets"));
					})));
		});

		// Uncollect a collectater
		registerSubcommand("uncollect", baseBuilder, builder -> {
			builder
				.then(CommandManager.argument("id", IdentifierArgumentType.identifier())
				.executes(context -> {
					return CollectaterCommand.uncollect(context, Collections.singleton(context.getSource().getPlayer()));
				})
					.then(CommandManager.argument("targets", EntityArgumentType.players())
					.executes(context -> {
						return CollectaterCommand.uncollect(context, EntityArgumentType.getPlayers(context, "targets"));
					})));
		});

		// Clear collectater
		registerSubcommand("clear", baseBuilder, builder -> {
			builder
				.then(CommandManager.argument("targets", EntityArgumentType.players())
				.executes(context -> {
					return CollectaterCommand.clear(context, Collections.singleton(context.getSource().getPlayer()));
				}));
			
			builder.executes(context -> {
				return CollectaterCommand.clear(context, EntityArgumentType.getPlayers(context, "targets"));
			});
		});

		// List collectaters
		registerSubcommand("list", baseBuilder, builder -> {
			builder
				.then(CommandManager.argument("target", EntityArgumentType.player())
				.executes(context -> {
					return CollectaterCommand.list(context, EntityArgumentType.getPlayer(context, "target"));
				}));

			builder.executes(context -> {
				return CollectaterCommand.list(context, context.getSource().getPlayer());
			});
		});

		// Start timer
		registerSubcommand("timer", baseBuilder, builder -> {
			builder
				.then(CommandManager.literal("start")
				.executes(CollectaterCommand::startTimer));

			builder
				.then(CommandManager.literal("set")
					.then(CommandManager.argument("ticks", IntegerArgumentType.integer(0))
					.executes(CollectaterCommand::setTimer)));
		});

		// Set/get maximum
		registerSubcommand("maximum", baseBuilder, builder -> {
			builder
				.then(CommandManager.argument("maximum", IntegerArgumentType.integer(0))
				.executes(CollectaterCommand::setMaximum));

			builder.executes(CollectaterCommand::getMaximum);
		});
		
		dispatcher.register(baseBuilder);
	}

	private static void registerSubcommand(String literal, LiteralArgumentBuilder<ServerCommandSource> baseBuilder, Consumer<LiteralArgumentBuilder<ServerCommandSource>> consumer) {
		LiteralArgumentBuilder<ServerCommandSource> subBuilder = CommandManager.literal(literal);
		consumer.accept(subBuilder);
		baseBuilder.then(subBuilder);
	}

	private static ItemStack getCollectaterStack(Item item, int count, Identifier id) {
		ItemStack stack = new ItemStack(item, count);

		// Set value for block entity
		CompoundTag blockEntityData = new CompoundTag();
		blockEntityData.putString("Id", id.toString());
		stack.putSubTag("BlockEntityTag", blockEntityData);

		return stack;
	}

	private static boolean isValidCollectaterItem(Item item) {
		if (!(item instanceof BlockItem)) return false;
		return ((BlockItem) item).getBlock() instanceof CollectaterBlock;
	} 

	private static Text getTargetFeedback(ItemStack stack, Identifier id, Collection<ServerPlayerEntity> targets) {
		if (targets.size() == 1) {
			return new TranslatableText("commands.collectater.collectater.give.success.single", stack.getCount(), stack.toHoverableText(), id.toString(), targets.iterator().next().getDisplayName());
		}
		return new TranslatableText("commands.collectater.collectater.give.success.multiple", stack.getCount(), stack.toHoverableText(), id.toString(), targets.size());
	}

	public static int give(CommandContext<ServerCommandSource> context, Collection<ServerPlayerEntity> targets, int count) throws CommandSyntaxException {
		ItemStack item = ItemStackArgumentType.getItemStackArgument(context, "item").createStack(count, true);
		if (!CollectaterCommand.isValidCollectaterItem(item.getItem())) {
			context.getSource().sendError(new TranslatableText("commands.collectater.collectater.give.failure.not_collectater", Registry.ITEM.getId(item.getItem()).toString()));
			return 0;
		}

		Identifier id = IdentifierArgumentType.getIdentifier(context, "id");

		ItemStack stack = CollectaterCommand.getCollectaterStack(item.getItem(), count, id);
		for (ServerPlayerEntity target : targets) {
			target.giveItemStack(stack.copy());
		}

		context.getSource().sendFeedback(CollectaterCommand.getTargetFeedback(stack, id, targets), true);
		return 1;
	}

	public static int collect(CommandContext<ServerCommandSource> context, Collection<ServerPlayerEntity> targets) throws CommandSyntaxException {
		Identifier id = IdentifierArgumentType.getIdentifier(context, "id");

		for (ServerPlayerEntity target : targets) {
			CollectatersComponent component = Main.COLLECTATERS.get(target);
	
			if (component.hasCollected(id) && targets.size() == 1) {
				context.getSource().sendError(new TranslatableText("commands.collectater.collectater.collect.failure", id.toString(), targets.iterator().next().getDisplayName()));
				return 0;
			}
			component.collect(id);
		}

		if (targets.size() == 1) {
			context.getSource().sendFeedback(new TranslatableText("commands.collectater.collectater.collect.success.single", id.toString(), targets.iterator().next().getDisplayName()), true);
		} else {
			context.getSource().sendFeedback(new TranslatableText("commands.collectater.collectater.collect.success.multiple", id.toString(), targets.size()), true);
		}
		return 1;
	}

	public static int uncollect(CommandContext<ServerCommandSource> context, Collection<ServerPlayerEntity> targets) throws CommandSyntaxException {
		Identifier id = IdentifierArgumentType.getIdentifier(context, "id");

		for (ServerPlayerEntity target : targets) {
			CollectatersComponent component = Main.COLLECTATERS.get(target);

			if (!component.hasCollected(id) && targets.size() == 1) {
				context.getSource().sendError(new TranslatableText("commands.collectater.collectater.uncollect.failure", id.toString(), targets.iterator().next().getDisplayName()));
				return 0;
			}
			component.uncollect(id);
		}

		if (targets.size() == 1) {
			context.getSource().sendFeedback(new TranslatableText("commands.collectater.collectater.uncollect.success.single", id.toString(), targets.iterator().next().getDisplayName()), true);
		} else {
			context.getSource().sendFeedback(new TranslatableText("commands.collectater.collectater.uncollect.success.multiple", id.toString(), targets.size()), true);
		}
		return 1;
	}

	public static int clear(CommandContext<ServerCommandSource> context, Collection<ServerPlayerEntity> targets) throws CommandSyntaxException {
		for (ServerPlayerEntity target : targets) {
			CollectatersComponent component = Main.COLLECTATERS.get(target);

			if (component.getCollectedCount() == 0 && targets.size() == 1) {
				context.getSource().sendError(new TranslatableText("commands.collectater.collectater.clear.failure", targets.iterator().next().getDisplayName()));
				return 0;
			}
			component.clear();
		}

		if (targets.size() == 1) {
			context.getSource().sendFeedback(new TranslatableText("commands.collectater.collectater.clear.success.single", targets.iterator().next().getDisplayName()), true);
		} else {
			context.getSource().sendFeedback(new TranslatableText("commands.collectater.collectater.clear.success.multiple", targets.size()), true);
		}
		return 1;
	}

	public static int list(CommandContext<ServerCommandSource> context, ServerPlayerEntity target) throws CommandSyntaxException {
		CollectatersComponent component = Main.COLLECTATERS.get(target);
		MutableText text = new TranslatableText("commands.collectater.collectater.list.header", target.getDisplayName(), component.getCollectedCount());
		
		Iterator<Identifier> iterator = component.getIterator();
		while (iterator.hasNext()) {
			Identifier id = iterator.next();

			text.append(id.toString());
			if (iterator.hasNext()) {
				text.append(", ");
			}
		}

		context.getSource().sendFeedback(text, false);
		return 1;
	}

	public static int getMaximum(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		CollectaterGlobalStateComponent component = Main.COLLECTATER_GLOBAL_STATE.get(context.getSource().getWorld().getLevelProperties());
		int maximum = component.getMaximumCollectaters();

		context.getSource().sendFeedback(new TranslatableText("commands.collectater.collectater.maximum.query", maximum), false);
		return 1;
	}

	public static int setMaximum(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		CollectaterGlobalStateComponent component = Main.COLLECTATER_GLOBAL_STATE.get(context.getSource().getWorld().getLevelProperties());

		int maximum = IntegerArgumentType.getInteger(context, "maximum");
		component.setMaximumCollectaters(maximum);
		component.syncWithAll(context.getSource().getMinecraftServer());

		context.getSource().sendFeedback(new TranslatableText("commands.collectater.collectater.maximum.set", maximum), true);
		return 1;
	}

	public static int setTimer(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		CollectaterGlobalStateComponent component = Main.COLLECTATER_GLOBAL_STATE.get(context.getSource().getWorld().getLevelProperties());
		
		int ticks = IntegerArgumentType.getInteger(context, "ticks");
		component.setTimer(ticks);

		context.getSource().sendFeedback(new TranslatableText("commands.collectater.collectater.timer.set.success", ticks), true);
		return 1;
	}

	public static int startTimer(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		CollectaterGlobalStateComponent component = Main.COLLECTATER_GLOBAL_STATE.get(context.getSource().getWorld().getLevelProperties());
		component.startTimer();

		context.getSource().sendFeedback(new TranslatableText("commands.collectater.collectater.timer.start.success"), true);
		return 1;
	}
}