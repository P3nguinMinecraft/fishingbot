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

        int buttonWidth = Math.min(200, Math.max(80, this.width - 40));
        int centerX = (this.width - buttonWidth) / 2;
        int labelWidth = Math.max(50, buttonWidth - 30);
        int topSafe = 20;
        int bottomSafe = 20;
        int maxAvailable = Math.max(50, this.height - topSafe - bottomSafe);

        int estimate = 0;
        estimate += 20;
        if (config.enabled) {
            estimate += 35;
            estimate += 20;
            estimate += 20;
            estimate += 25;
            estimate += 20;
            estimate += 20;
            estimate += 35;
        }
        if (config.enabled && config.rodSwap) {
            estimate += 35;
            estimate += 20;
            estimate += 20;
            estimate += 25;
            estimate += 20;
            estimate += 20;
            estimate += 35;
        }
        estimate += 45;
        estimate += 20;

        double scale = 1.0;
        if (estimate > maxAvailable)
            scale = (double) maxAvailable / (double) estimate;

        int y = topSafe + Math.max(0, (int) Math.round((this.height - topSafe - bottomSafe - estimate * scale) / 2.0));

        this.addDrawableChild(ButtonWidget.builder(
                Text.of("Fishingbot: " + (config.enabled ? "ON" : "OFF")),
                button -> {
                    config.enabled = !config.enabled;
                    config.save();
                    button.setMessage(Text.of("Fishingbot: " + (config.enabled ? "ON" : "OFF")));
                    FishingbotConfigScreen.this.clearAndInit();
                }).dimensions(centerX, y, buttonWidth, 20).build());

        TextWidget swapDelayValueLabel = new TextWidget(centerX + 10, y + 2, labelWidth, 20,
                Text.of(config.swapDelay + " ticks"), this.textRenderer);

        if (config.enabled) {
            y += (int) Math.round(35 * scale);

            TextWidget reelDelayLabel = new TextWidget(centerX + 10, y, labelWidth, 20, Text.of("Reel Delay"),
                    this.textRenderer);
            this.addDrawableChild(reelDelayLabel);

            y += (int) Math.round(20 * scale);

            TextWidget reelDelay = new TextWidget(centerX + 10, y + 2, labelWidth, 20,
                    Text.of(config.reelDelay + " ticks"),
                    this.textRenderer);

            SliderWidget reelDelaySlider = new SliderWidget(centerX, y, buttonWidth, 20, Text.of(""),
                    (config.reelDelay) / 30.0) {
                @Override
                protected void updateMessage() {
                }

                @Override
                protected void applyValue() {
                    config.reelDelay = (int) Math.round(this.value * 30);
                    config.save();
                    reelDelay.setMessage(Text.of(config.reelDelay + " ticks"));
                }
            };
            this.addDrawableChild(reelDelaySlider);
            this.addDrawableChild(reelDelay);

            y += (int) Math.round(25 * scale);

            TextWidget castDelayLabel = new TextWidget(centerX + 10, y, labelWidth, 20, Text.of("Cast Delay"),
                    this.textRenderer);
            this.addDrawableChild(castDelayLabel);

            y += (int) Math.round(20 * scale);

            TextWidget castDelayValueLabel = new TextWidget(centerX + 10, y + 2, labelWidth, 20,
                    Text.of(config.castDelay + " ticks"), this.textRenderer);

            SliderWidget castDelaySlider = new SliderWidget(centerX, y, buttonWidth, 20, Text.of(""),
                    (config.castDelay) / 40.0) {
                @Override
                protected void updateMessage() {
                }

                @Override
                protected void applyValue() {
                    config.castDelay = (int) Math.round(this.value * 40);
                    config.swapDelay = Math.min(config.swapDelay, config.castDelay);
                    config.save();
                    castDelayValueLabel.setMessage(Text.of(config.castDelay + " ticks"));
                    swapDelayValueLabel.setMessage(Text.of(config.swapDelay + " ticks"));
                }
            };
            this.addDrawableChild(castDelaySlider);
            this.addDrawableChild(castDelayValueLabel);

            y += (int) Math.round(35 * scale);

            this.addDrawableChild(ButtonWidget.builder(
                    Text.of("Rod Swap: " + (config.rodSwap ? "ON" : "OFF")),
                    button -> {
                        config.rodSwap = !config.rodSwap;
                        config.save();
                        button.setMessage(Text.of("Rod Swap: " + (config.rodSwap ? "ON" : "OFF")));
                        FishingbotConfigScreen.this.clearAndInit();
                    }).dimensions(centerX, y, buttonWidth, 20).build());
        }

        if (config.enabled && config.rodSwap) {
            y += (int) Math.round(35 * scale);

            TextWidget castLabel = new TextWidget(centerX + 10, y, labelWidth, 20, Text.of("Cast Slot"),
                    this.textRenderer);
            this.addDrawableChild(castLabel);

            y += (int) Math.round(20 * scale);

            TextWidget castValueLabel = new TextWidget(centerX + 10, y + 2, labelWidth, 20,
                    Text.of(String.valueOf(config.castSlot)), this.textRenderer);

            SliderWidget castSlider = new SliderWidget(centerX, y, buttonWidth, 20, Text.of(""),
                    (config.castSlot - 1) / 8.0) {
                @Override
                protected void updateMessage() {
                }

                @Override
                protected void applyValue() {
                    config.castSlot = (int) Math.round(this.value * 8) + 1;
                    config.save();
                    castValueLabel.setMessage(Text.of(String.valueOf(config.castSlot)));
                }
            };
            this.addDrawableChild(castSlider);
            this.addDrawableChild(castValueLabel);

            y += (int) Math.round(25 * scale);

            TextWidget reelLabel = new TextWidget(centerX + 10, y, labelWidth, 20, Text.of("Reel Slot"),
                    this.textRenderer);
            this.addDrawableChild(reelLabel);

            y += (int) Math.round(20 * scale);

            TextWidget reelValueLabel = new TextWidget(centerX + 10, y + 2, labelWidth, 20,
                    Text.of(String.valueOf(config.reelSlot)), this.textRenderer);

            SliderWidget reelSlider = new SliderWidget(centerX, y, buttonWidth, 20, Text.of(""),
                    (config.reelSlot - 1) / 8.0) {
                @Override
                protected void updateMessage() {
                }

                @Override
                protected void applyValue() {
                    config.reelSlot = (int) Math.round(this.value * 8) + 1;
                    config.save();
                    reelValueLabel.setMessage(Text.of(String.valueOf(config.reelSlot)));
                }
            };
            this.addDrawableChild(reelSlider);
            this.addDrawableChild(reelValueLabel);

            y += (int) Math.round(35 * scale);

            TextWidget swapDelayLabel = new TextWidget(centerX + 10, y, labelWidth, 20, Text.of("Swap Delay"),
                    this.textRenderer);
            this.addDrawableChild(swapDelayLabel);

            y += (int) Math.round(20 * scale);

            swapDelayValueLabel.setY(y + 2);

            SliderWidget swapDelaySlider = new SliderWidget(centerX, y, buttonWidth, 20, Text.of(""),
                    (config.swapDelay) / 20.0) {
                @Override
                protected void updateMessage() {
                }

                @Override
                protected void applyValue() {
                    config.swapDelay = Math.min((int) Math.round(this.value * 20) + 1, config.castDelay);
                    config.save();
                    swapDelayValueLabel.setMessage(Text.of(config.swapDelay + " ticks"));
                }
            };
            this.addDrawableChild(swapDelaySlider);
            this.addDrawableChild(swapDelayValueLabel);
        }

        y += (int) Math.round(45 * scale);

        this.addDrawableChild(ButtonWidget.builder(
                Text.of("Done"),
                button -> this.close()).dimensions(centerX, y, buttonWidth, 20).build());
    }

    @Override
    public void close() {
        this.client.setScreen(parent);
    }
}
