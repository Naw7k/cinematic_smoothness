package net.naw.cinematic_smoothness;

import net.fabricmc.api.ModInitializer;

public class Cinematic_smoothness implements ModInitializer {
    // // Instead of a temporary class, we load from our new ModConfig file
    public static ModConfig config;

    @Override
    public void onInitialize() {
        // // This loads your saved settings (or creates the file if it's missing)
        config = ModConfig.load();
    }
}