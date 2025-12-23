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
    public int reelDelay = 10; // 0-30
    public int castDelay = 20; // 0-40
    public boolean rodSwap = false;
    public int castSlot = 1; // 1-9
    public int reelSlot = 2; // 1-9
    public int swapDelay = 5; // 0-20 <= swapDelay

    public static FishingbotConfig get() {
        if (instance == null) {
            instance = load();
            validate();
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
    
    public static void validate() {
        if (instance == null) {
            instance = load();
        }
        instance.reelDelay = clamp(instance.reelDelay, 0, 30);
        instance.castDelay = clamp(instance.castDelay, 0, 40);
        instance.castSlot = clamp(instance.castSlot, 1, 9);
        instance.reelSlot = clamp(instance.reelSlot, 1, 9);
        instance.swapDelay = clamp(instance.swapDelay, 0, 20);
    }
    
    private static int clamp(int val, int min, int max) {
        return Math.min(Math.max(val, min), max);
    }

    public void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
