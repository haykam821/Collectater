package io.github.haykam821.collectater.command;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import io.github.haykam821.collectater.Main;
import io.github.haykam821.collectater.block.ModBlocks;
import io.github.haykam821.collectater.component.CollectatersComponent;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class CollectaterCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> baseBuilder = CommandManager.literal("collectater").requires(source -> {
			return source.hasPermissionLevel(2);
		});

		baseBuilder.then(CommandManager.literal("give")
			.then(CommandManager.argument("id", IdentifierArgumentType.identifier())
				.then(CommandManager.argument("targets", EntityArgumentType.players())
					.then(CommandManager.argument("count", IntegerArgumentType.integer(1))
					.executes(context -> {
						return CollectaterCommand.give(context, EntityArgumentType.getPlayers(context, "targets"), IntegerArgumentType.getInteger(context, "count"));
					}))
				.executes(context -> {
					return CollectaterCommand.give(context, EntityArgumentType.getPlayers(context, "targets"), 1);
				}))
			.executes(context -> {
				return CollectaterCommand.give(context, Collections.singleton(context.getSource().getPlayer()), 1);
			})));

		baseBuilder.then(CommandManager.literal("collect")
			.then(CommandManager.argument("id", IdentifierArgumentType.identifier())
				.then(CommandManager.argument("targets", EntityArgumentType.players())
				.executes(context -> {
					return CollectaterCommand.collect(context, EntityArgumentType.getPlayers(context, "targets"));
				}))
			.executes(context -> {
				return CollectaterCommand.collect(context, Collections.singleton(context.getSource().getPlayer()));
			})));

		baseBuilder.then(CommandManager.literal("uncollect")
			.then(CommandManager.argument("id", IdentifierArgumentType.identifier())
				.then(CommandManager.argument("targets", EntityArgumentType.players())
				.executes(context -> {
					return CollectaterCommand.uncollect(context, EntityArgumentType.getPlayers(context, "targets"));
				}))
			.executes(context -> {
				return CollectaterCommand.uncollect(context, Collections.singleton(context.getSource().getPlayer()));
			})));

		baseBuilder.then(CommandManager.literal("clear")
			.then(CommandManager.argument("targets", EntityArgumentType.players())
				.executes(context -> {
					return CollectaterCommand.clear(context, EntityArgumentType.getPlayers(context, "targets"));
				}))
			.executes(context -> {
				return CollectaterCommand.clear(context, Collections.singleton(context.getSource().getPlayer()));
			}));

		baseBuilder.then(CommandManager.literal("list")
			.then(CommandManager.argument("target", EntityArgumentType.player())
				.executes(context -> {
					return CollectaterCommand.list(context, EntityArgumentType.getPlayer(context, "target"));
				}))
			.executes(context -> {
				return CollectaterCommand.list(context, context.getSource().getPlayer());
			}));
		
		dispatcher.register(baseBuilder);
	}

	private static ItemStack getCollectaterStack(Identifier id) {
		ItemStack stack = new ItemStack(ModBlocks.COLLECTATER.getItem());

		// Set value for block entity
		CompoundTag blockEntityData = new CompoundTag();
		blockEntityData.putString("Id", id.toString());
		stack.putSubTag("BlockEntityTag", blockEntityData);

		return stack;
	}

	private static Text getTargetFeedback(ItemStack stack, Identifier id, Collection<ServerPlayerEntity> targets) {
		if (targets.size() == 1) {
			return new TranslatableText("commands.collectater.collectater.give.success.single", stack, id.toString(), targets.iterator().next().getDisplayName());
		}
		return new TranslatableText("commands.collectater.collectater.give.success.multiple", stack, id.toString(), targets.size());
	}

	public static int give(CommandContext<ServerCommandSource> context, Collection<ServerPlayerEntity> targets, int count) throws CommandSyntaxException {
		Identifier id = IdentifierArgumentType.getIdentifier(context, "id");

		ItemStack stack = CollectaterCommand.getCollectaterStack(id);
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
}