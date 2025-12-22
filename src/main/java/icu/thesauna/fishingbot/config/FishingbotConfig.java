package icu.thesauna.fishingbot.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FishingbotConfig {
    private static final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("fishingbot.json")
            .toFile();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static FishingbotConfig instance;

    public boolean enabled = false;
    public boolean rodSwap = false;
    public int lureSlot = -1;
    public int reelSlot = -1;

    public static FishingbotConfig get() {
        if (instance == null) {
            instance = load();
        }
        return instance;
    }

    public static FishingbotConfig load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                return GSON.fromJson(reader, FishingbotConfig.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new FishingbotConfig();
    }

    public void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
