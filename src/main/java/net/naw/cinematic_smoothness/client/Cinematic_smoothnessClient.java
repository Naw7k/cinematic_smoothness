package net.naw.cinematic_smoothness.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.naw.cinematic_smoothness.gui.SmoothnessScreen;
import org.lwjgl.glfw.GLFW;

public class Cinematic_smoothnessClient implements ClientModInitializer {
    public static KeyMapping openSettingsKey;

    @Override
    public void onInitializeClient() {
        openSettingsKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.cinematic_smoothness.settings",
                GLFW.GLFW_KEY_F4, // // Changed from GLFW_KEY_O to GLFW_KEY_F4
                KeyMapping.Category.MISC
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openSettingsKey.consumeClick()) {
                client.setScreen(new SmoothnessScreen(null));
            }
        });
    }
}