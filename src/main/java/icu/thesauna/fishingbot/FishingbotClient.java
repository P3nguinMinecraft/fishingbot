package icu.thesauna.fishingbot;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import icu.thesauna.fishingbot.config.FishingbotConfig;
import icu.thesauna.fishingbot.mixin.FishingBobberEntityAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class FishingbotClient implements ClientModInitializer {
    private boolean wasCasting = false;
    private int recastTimer = 0;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || !FishingbotConfig.get().enabled)
                return;

            boolean mainHand = client.player.getMainHandStack().isOf(Items.FISHING_ROD);
            boolean offHand = client.player.getOffHandStack().isOf(Items.FISHING_ROD);

            if (!mainHand && !offHand) {
                wasCasting = false;
                recastTimer = 0;
                return;
            }

            Hand activeHand = mainHand ? Hand.MAIN_HAND : Hand.OFF_HAND;
            var rodStack = mainHand ? client.player.getMainHandStack() : client.player.getOffHandStack();

            if (rodStack.getMaxDamage() - rodStack.getDamage() <= 1) {
                wasCasting = false;
                recastTimer = 0;
                return;
            }

            if (recastTimer > 0) {
                recastTimer--;
                if (recastTimer == 0) {
                    if (client.currentScreen != null) {
                        recastTimer++;
                        return;
                    }
                    doRightClick(client, activeHand);
                    wasCasting = true;
                }
                return;
            }

            FishingBobberEntity bobber = client.player.fishHook;

            if (bobber != null && !bobber.isRemoved()) {
                wasCasting = true;

                if (((FishingBobberEntityAccessor) bobber).getCaughtFish() && client.currentScreen == null) {
                    doRightClick(client, activeHand);
                    recastTimer = 20;
                }
            } else {
                wasCasting = false;
            }
        });

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(literal("fishingbot")
                .then(literal("toggle")
                    .executes(context -> {
                        FishingbotConfig config = FishingbotConfig.get();
                        config.enabled = !config.enabled;
                        config.save();
                        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("Fishingbot: " + (config.enabled ? "ON" : "OFF")));
                        return 1;
                    })
                )
            );
        });
    }

    private void doRightClick(MinecraftClient client, Hand hand) {
        if (client.interactionManager == null) return;
        if (client.currentScreen != null) return;
        client.interactionManager.interactItem(client.player, hand);
        client.player.swingHand(hand);
    }
}
