package io.github.haykam821.collectater;

import io.github.haykam821.collectater.overlay.CollectaterOverlay;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;

public class ClientMain implements ClientModInitializer {
	private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

	@Override
	public void onInitializeClient() {
		CollectaterOverlay renderer = new CollectaterOverlay(CLIENT);
		HudRenderCallback.EVENT.register((matrices, delta) -> {
			renderer.render(matrices, (int) CLIENT.mouse.getX(), (int) CLIENT.mouse.getY(), delta);
		});
	}
}