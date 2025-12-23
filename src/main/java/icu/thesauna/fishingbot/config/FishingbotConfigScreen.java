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
                    FishingbotConfigScreen.this.clearAndInit();
                }
        ).dimensions(centerX, y, 200, 20).build());

        // Predefine Sway Delay Value Label, y changed later
        TextWidget swapDelayValueLabel = new TextWidget(centerX + 10, y + 2, 70, 20, Text.of(config.swapDelay + " ticks"), this.textRenderer);

        if (config.enabled) {
            y += 35;

            // Reel Delay Label
            TextWidget reelDelayLabel = new TextWidget(centerX + 10, y, 70, 20, Text.of("Reel Delay"), this.textRenderer);
            this.addDrawableChild(reelDelayLabel);

            y += 20;

            // Reel Delay Value Label
            TextWidget reelDelay = new TextWidget(centerX + 10, y + 2, 70, 20, Text.of(config.reelDelay + " ticks"), this.textRenderer);

            // Reel Delay Slider
            SliderWidget reelDelaySlider = new SliderWidget(centerX, y, 200, 20, Text.of(""), (config.reelDelay) / 30.0) {
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

            y += 25;

            // Cast Delay Label
            TextWidget castDelayLabel = new TextWidget(centerX + 10, y, 70, 20, Text.of("Cast Delay"), this.textRenderer);
            this.addDrawableChild(castDelayLabel);

            y += 20;

            // Cast Delay Value Label
            TextWidget castDelayValueLabel = new TextWidget(centerX + 10, y + 2, 70, 20, Text.of(config.castDelay + " ticks"), this.textRenderer);

            // Cast Delay Slider
            SliderWidget castDelaySlider = new SliderWidget(centerX, y, 200, 20, Text.of(""), (config.castDelay) / 40.0) {
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
                    // TODO: use FishingbotConfigScreen.this.clearAndInit(); or another method to refresh swapDelay slider
                }
            };
            this.addDrawableChild(castDelaySlider);
            this.addDrawableChild(castDelayValueLabel);

            y += 35;

            // Rod Swap
            this.addDrawableChild(ButtonWidget.builder(
                    Text.of("Rod Swap: " + (config.rodSwap ? "ON" : "OFF")),
                    button -> {
                        config.rodSwap = !config.rodSwap;
                        config.save();
                        button.setMessage(Text.of("Rod Swap: " + (config.rodSwap ? "ON" : "OFF")));
                        FishingbotConfigScreen.this.clearAndInit();
                    }
            ).dimensions(centerX, y, 200, 20).build());
        }

        if (config.enabled && config.rodSwap) {
            y += 35;

            // Cast Label
            TextWidget castLabel = new TextWidget(centerX + 10, y, 70, 20, Text.of("Cast Slot"), this.textRenderer);
            this.addDrawableChild(castLabel);

            y += 20;

            // Cast Value Label
            TextWidget castValueLabel = new TextWidget(centerX + 10, y + 2, 70, 20, Text.of(String.valueOf(config.castSlot)), this.textRenderer);

            // Cast Slider
            SliderWidget castSlider = new SliderWidget(centerX, y, 200, 20, Text.of(""), (config.castSlot - 1) / 8.0) {
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

            y += 25;

            // Reel Label
            TextWidget reelLabel = new TextWidget(centerX + 10, y, 70, 20, Text.of("Reel Slot"), this.textRenderer);
            this.addDrawableChild(reelLabel);

            y += 20;

            // Reel Value Label
            TextWidget reelValueLabel = new TextWidget(centerX + 10, y + 2, 70, 20, Text.of(String.valueOf(config.reelSlot)), this.textRenderer);

            // Reel Slider
            SliderWidget reelSlider = new SliderWidget(centerX, y, 200, 20, Text.of(""), (config.reelSlot - 1) / 8.0) {
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

            y += 35;

            // Swap Delay Label
            TextWidget swapDelayLabel = new TextWidget(centerX + 10, y, 70, 20, Text.of("Swap Delay"), this.textRenderer);
            this.addDrawableChild(swapDelayLabel);

            y += 20;

            // Swap Delay Value Label
            // already defined, just fixing y value
            swapDelayValueLabel.setY(y + 2);

            // Swap Delay Slider
            SliderWidget swapDelaySlider = new SliderWidget(centerX, y, 200, 20, Text.of(""), (config.swapDelay) / 20.0) {
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
