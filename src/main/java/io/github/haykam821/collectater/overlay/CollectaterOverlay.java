package io.github.haykam821.collectater.overlay;

import io.github.haykam821.collectater.Main;
import io.github.haykam821.collectater.block.ModBlocks;
import io.github.haykam821.collectater.component.CollectaterGlobalStateComponent;
import io.github.haykam821.collectater.component.CollectatersComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class CollectaterOverlay extends Overlay {
	private static final ItemStack COMPLETED_ICON = new ItemStack(ModBlocks.BLUE_COLLECTATER.getBlock().asItem());
	private static final ItemStack ICON = new ItemStack(ModBlocks.COLLECTATER.getBlock().asItem());

	private static final int PADDING = 4;
	private final MinecraftClient client;

	public CollectaterOverlay(MinecraftClient client) {
		this.client = client;
	}

	private Text getRenderText(CollectatersComponent component, CollectaterGlobalStateComponent globalComponent) {
		if (globalComponent.getMaximumCollectaters() == 0) {
			if (component.getCollectedCount() == 1) {
				return new TranslatableText("text.collectater.overlay.singular", component.getCollectedCount());
			}
			return new TranslatableText("text.collectater.overlay", component.getCollectedCount());
		}
		return new TranslatableText("text.collectater.overlay.maximum", component.getCollectedCount(), globalComponent.getMaximumCollectaters());
	}

	private ItemStack getRenderStack(CollectatersComponent component, CollectaterGlobalStateComponent globalComponent) {
		return component.getCollectedCount() >= globalComponent.getMaximumCollectaters() ? COMPLETED_ICON : ICON;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		CollectatersComponent component = Main.COLLECTATERS.get(client.player);
		CollectaterGlobalStateComponent globalComponent = Main.COLLECTATER_GLOBAL_STATE.get(client.world.getLevelProperties());

		Text text = this.getRenderText(component, globalComponent);
		int width = this.client.textRenderer.getWidth(text);

		DrawableHelper.fill(matrices, 0, 0, width + 16 + PADDING * 3, 16 + PADDING * 2, -1072689136);
		client.textRenderer.draw(matrices, text, 16 + PADDING * 2, client.textRenderer.fontHeight / (float) 2 + PADDING, 0xFFFFFF);

		this.client.getItemRenderer().renderInGuiWithOverrides(this.client.player, this.getRenderStack(component, globalComponent), PADDING, PADDING);
	}

	@Override
	public boolean pausesGame() {
		return false;
	}
}