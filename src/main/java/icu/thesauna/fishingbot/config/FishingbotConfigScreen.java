package icu.thesauna.fishingbot.config;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;

public class FishingbotConfigScreen extends Screen {
    private final Screen parent;

    public FishingbotConfigScreen(Screen parent) {
        super(Text.of("Fishingbot Settings"));
        this.parent = parent;
    }

    @Override
    public void init() {
        FishingbotConfig config = FishingbotConfig.get();

        int centerX = this.width / 2 - 100;
        int y = this.height / 2 - 150;

        // Fishingbot
        this.addDrawableChild(ButtonWidget.builder(
                Text.of("Fishingbot: " + (config.enabled ? "ON" : "OFF")),
                button -> {
                    config.enabled = !config.enabled;
                    config.save();
                    button.setMessage(Text.of("Fishingbot: " + (config.enabled ? "ON" : "OFF")));
                }
        ).dimensions(centerX, y, 200, 20).build());

        y += 35;

        // Reel Delay Label
        TextWidget reelDelayLabel = new TextWidget(centerX + 65, y + 5, 70, 20, Text.of("Reel Delay: " + config.reelDelay + " ticks"), this.textRenderer);

        // Reel Delay Slider
        SliderWidget reelDelaySlider = new SliderWidget(centerX, y, 200, 20, Text.of(""), (config.reelDelay - 1) / 19.0) {
            @Override
            protected void updateMessage() {}

            @Override
            protected void applyValue() {
                config.reelDelay = (int) Math.round(this.value * 19) + 1;
                config.save();
                reelDelayLabel.setMessage(Text.of("Reel Delay: " + config.reelDelay + " ticks"));
            }
        };
        this.addDrawableChild(reelDelaySlider);
        this.addDrawableChild(reelDelayLabel);

        y += 35;

        // Rod Swap
        this.addDrawableChild(ButtonWidget.builder(
                Text.of("Rod Swap: " + (config.rodSwap ? "ON" : "OFF")),
                button -> {
                    config.rodSwap = !config.rodSwap;
                    config.save();
                    button.setMessage(Text.of("Rod Swap: " + (config.rodSwap ? "ON" : "OFF")));
                }
        ).dimensions(centerX, y, 200, 20).build());

        y += 35;

        // Lure Label
        TextWidget lureLabel = new TextWidget(centerX + 65, y + 5, 70, 20, Text.of("Lure Slot: " + config.lureSlot), this.textRenderer);

        // Lure Slider
        SliderWidget lureSlider = new SliderWidget(centerX, y, 200, 20, Text.of(""), (config.lureSlot - 1) / 8.0) {
            @Override
            protected void updateMessage() {}

            @Override
            protected void applyValue() {
                int slot = (int) Math.round(this.value * 8) + 1;
                config.lureSlot = slot;
                config.save();
                lureLabel.setMessage(Text.of("Lure Slot: " + slot));
            }
        };
        this.addDrawableChild(lureSlider);
        this.addDrawableChild(lureLabel);

        y += 25;

        // Reel Label
        TextWidget reelLabel = new TextWidget(centerX + 65, y + 5, 70, 20, Text.of("Reel Slot: " + config.reelSlot), this.textRenderer);

        // Reel Slider
        SliderWidget reelSlider = new SliderWidget(centerX, y, 200, 20, Text.of(""), (config.reelSlot - 1) / 8.0) {
            @Override
            protected void updateMessage() {}

            @Override
            protected void applyValue() {
                int slot = (int) Math.round(this.value * 8) + 1;
                config.reelSlot = slot;
                config.save();
                reelLabel.setMessage(Text.of("Reel Slot: " + slot));
            }
        };
        this.addDrawableChild(reelSlider);
        this.addDrawableChild(reelLabel);

        y += 35;

        // Reel Delay Label
        TextWidget swapDelayLabel = new TextWidget(centerX + 65, y + 5, 70, 20, Text.of("Swap Delay: " + config.swapDelay + " ticks"), this.textRenderer);

        // Reel Delay Slider
        SliderWidget swapDelaySlider = new SliderWidget(centerX, y, 200, 20, Text.of(""), (config.swapDelay - 1) / 19.0) {
            @Override
            protected void updateMessage() {}

            @Override
            protected void applyValue() {
                config.swapDelay = Math.min((int) Math.round(this.value * 19) + 1, config.reelDelay);
                config.save();
                swapDelayLabel.setMessage(Text.of("Swap Delay: " + config.swapDelay + " ticks"));
            }
        };
        this.addDrawableChild(swapDelaySlider);
        this.addDrawableChild(swapDelayLabel);


        y += 45;

        // Done button
        this.addDrawableChild(ButtonWidget.builder(
                Text.of("Done"),
                button -> this.close()
        ).dimensions(centerX, y, 200, 20).build());
    }

    @Override
    public void close() {
        this.client.setScreen(parent);
    }
}
