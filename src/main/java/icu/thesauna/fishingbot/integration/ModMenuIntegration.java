package icu.thesauna.fishingbot.integration;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import icu.thesauna.fishingbot.config.FishingbotConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new FishingbotConfigScreen(parent);
    }

    private static class FishingbotConfigScreen extends Screen {
        private final Screen parent;

        protected FishingbotConfigScreen(Screen parent) {
            super(Text.of("Fishingbot Settings"));
            this.parent = parent;
        }

        @Override
        protected void init() {
            FishingbotConfig config = FishingbotConfig.get();
            
            this.addDrawableChild(ButtonWidget.builder(
                Text.of("Fishingbot: " + (config.enabled ? "ON" : "OFF")),
                button -> {
                    config.enabled = !config.enabled;
                    config.save();
                    button.setMessage(Text.of("Fishingbot: " + (config.enabled ? "ON" : "OFF")));
                }
            )
            .dimensions(this.width / 2 - 100, this.height / 2 - 10, 200, 20)
            .build());

            this.addDrawableChild(ButtonWidget.builder(
                Text.of("Done"),
                button -> this.close()
            )
            .dimensions(this.width / 2 - 100, this.height / 2 + 20, 200, 20)
            .build());
        }

        @Override
        public void close() {
            this.client.setScreen(parent);
        }
    }
}
