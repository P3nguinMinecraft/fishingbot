package icu.thesauna.fishingbot;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import icu.thesauna.fishingbot.config.FishingbotConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

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
                    doRightClick(client, activeHand);
                    wasCasting = true;
                }
                return;
            }

            FishingBobberEntity bobber = client.player.fishHook;

            if (bobber != null && !bobber.isRemoved()) {
                wasCasting = true;

                if (bobber.fishTravelCountdown > 0) {
                    doRightClick(client, activeHand);
                    recastTimer = 20;
                }
            } else {
                wasCasting = false;
            }
        });
    }

    private void doRightClick(MinecraftClient client, Hand hand) {
        if (client.interactionManager != null) {
            client.interactionManager.interactItem(client.player, hand);
            client.player.swingHand(hand);
        }
    }
}
