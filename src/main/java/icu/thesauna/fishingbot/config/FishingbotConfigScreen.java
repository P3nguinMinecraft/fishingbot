package icu.thesauna.fishingbot.config;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
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
        int y = this.height / 2 - 90;

        // Fishingbot toggle
        this.addDrawableChild(ButtonWidget.builder(
                Text.of("Fishingbot: " + (config.enabled ? "ON" : "OFF")),
                button -> {
                    config.enabled = !config.enabled;
                    config.save();
                    button.setMessage(Text.of("Fishingbot: " + (config.enabled ? "ON" : "OFF")));
                }
        ).dimensions(centerX, y, 200, 20).build());

        y += 25;

        // Rod Swap toggle
        this.addDrawableChild(ButtonWidget.builder(
                Text.of("Rod Swap: " + (config.rodSwap ? "ON" : "OFF")),
                button -> {
                    config.rodSwap = !config.rodSwap;
                    config.save();
                    button.setMessage(Text.of("Rod Swap: " + (config.rodSwap ? "ON" : "OFF")));
                }
        ).dimensions(centerX, y, 200, 20).build());

        y += 30;

        // Lure Slot
        TextFieldWidget lureSlotField = new TextFieldWidget(
                this.textRenderer,
                centerX,
                y,
                200,
                20,
                Text.of("Lure Slot (1–9)")
        );
        lureSlotField.setText(String.valueOf(config.lureSlot));
        lureSlotField.setChangedListener(text -> {
            int slot = parseUiSlot(text);
            if (slot != -1) {
                config.lureSlot = slot;
                config.save();
            }
            else
                lureSlotField.setText(String.valueOf(config.lureSlot));
        });
        this.addSelectableChild(lureSlotField);
        this.addDrawableChild(lureSlotField);

        y += 25;

        // Reel Slot
        TextFieldWidget reelSlotField = new TextFieldWidget(
                this.textRenderer,
                centerX,
                y,
                200,
                20,
                Text.of("Reel Slot (1–9)")
        );
        reelSlotField.setText(String.valueOf(config.reelSlot));
        reelSlotField.setChangedListener(text -> {
            int slot = parseUiSlot(text);
            if (slot != -1) {
                config.reelSlot = slot;
                config.save();
            }
            else
                reelSlotField.setText(String.valueOf(config.reelSlot));
        });
        this.addSelectableChild(reelSlotField);
        this.addDrawableChild(reelSlotField);

        y += 35;

        // Done
        this.addDrawableChild(ButtonWidget.builder(
                Text.of("Done"),
                button -> this.close()
        ).dimensions(centerX, y, 200, 20).build());
    }

    private int parseUiSlot(String text) {
        try {
            int ui = Integer.parseInt(text);
            if (ui >= 1 && ui <= 9) {
                return ui;
            }
        } catch (NumberFormatException ignored) {}

        return -1;
    }
    @Override
    public void close() {
        this.client.setScreen(parent);
    }
}