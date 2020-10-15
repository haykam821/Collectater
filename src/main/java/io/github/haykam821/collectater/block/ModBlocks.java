package io.github.haykam821.collectater.block;

import java.util.function.Function;

import io.github.haykam821.collectater.Main;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public enum ModBlocks {
	COLLECTATER("collectater", new CollectaterBlock(FabricBlockSettings.copyOf(Blocks.WHITE_WOOL).materialColor(MaterialColor.WOOD)));

	private final Block block;
	private final BlockItem item;

	private ModBlocks(String path, Block block, BlockItem item) {
		Identifier id = new Identifier(Main.MOD_ID, path);

		this.block = block;
		Registry.register(Registry.BLOCK, id, this.block);

		this.item = item;
		Registry.register(Registry.ITEM, id, this.item);
	}

	private ModBlocks(String path, Block block, Function<Block, BlockItem> itemFunction) {
		this(path, block, itemFunction.apply(block));
	}

	private ModBlocks(String path, Block block, ItemGroup group) {
		this(path, block, new BlockItem(block, new Item.Settings().group(group)));
	}

	private ModBlocks(String path, Block block) {
		this(path, block, new BlockItem(block, new Item.Settings()));
	}

	public Block getBlock() {
		return this.block;
	}

	public BlockItem getItem() {
		return this.item;
	}

	public static void register() {
		return;
	}
}