package io.github.haykam821.collectater.overlay;

import io.github.haykam821.collectater.Main;
import io.github.haykam821.collectater.block.ModBlocks;
import io.github.haykam821.collectater.component.CollectatersComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class CollectaterOverlay extends Overlay {
	private static final ItemStack COLLECTATER_ICON = new ItemStack(ModBlocks.COLLECTATER.getBlock().asItem());
	private static final int PADDING = 4;
	private final MinecraftClient client;

	public CollectaterOverlay(MinecraftClient client) {
		this.client = client;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		CollectatersComponent component = Main.COLLECTATERS.get(client.player);

		Text text = new TranslatableText("text.collectater.overlay", component.getCollectedCount());
		int width = this.client.textRenderer.getWidth(text);

		DrawableHelper.fill(matrices, 0, 0, width + 16 + PADDING * 3, 16 + PADDING * 2, -1072689136);
		client.textRenderer.draw(matrices, text, 16 + PADDING * 2, client.textRenderer.fontHeight / (float) 2 + PADDING, 0xFFFFFF);
		this.client.getItemRenderer().renderInGuiWithOverrides(this.client.player, COLLECTATER_ICON, PADDING, PADDING);
	}

	@Override
	public boolean pausesGame() {
		return false;
	}
}