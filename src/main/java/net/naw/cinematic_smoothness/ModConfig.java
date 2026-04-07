package net.naw.cinematic_smoothness;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ModConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger("cinematic_smoothness");

    // // Core Settings
    public double smoothness = 0.5;
    public boolean showBlackBars = true;
    public boolean useCinematicZoom = true;

    // // Fine Control Settings
    public boolean fineControl = false;
    public boolean showHudWithBars = false;

    // // NEW: Cinematic Visuals
    public boolean hideCrosshair = false;
    public boolean hideBlockOutline = false;

    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "cinematic_smoothness.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            LOGGER.error("Failed to save config", e);
        }
    }

    public static ModConfig load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                ModConfig config = GSON.fromJson(reader, ModConfig.class);
                if (config == null) return new ModConfig();
                return config;
            } catch (IOException e) {
                LOGGER.error("Failed to load config", e);
            }
        }
        ModConfig config = new ModConfig();
        config.save();
        return config;
    }
}
