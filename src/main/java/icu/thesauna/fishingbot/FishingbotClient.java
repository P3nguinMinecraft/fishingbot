package icu.thesauna.fishingbot;

import com.google.common.primitives.Shorts;
import com.google.common.primitives.SignedBytes;
import icu.thesauna.fishingbot.config.FishingbotConfigScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import icu.thesauna.fishingbot.config.FishingbotConfig;
import icu.thesauna.fishingbot.mixin.FishingBobberEntityAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class FishingbotClient implements ClientModInitializer {
    private boolean openGui = false;
    private int recastTimer = 0;
    private int swapTimer = 0;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            FishingbotConfig config = FishingbotConfig.get();
            if (openGui){
                client.setScreen(new FishingbotConfigScreen(client.currentScreen));
                openGui = false;
            }

            if (client.player == null || !config.enabled)
                return;

            boolean mainHand = client.player.getMainHandStack().isOf(Items.FISHING_ROD);
            boolean offHand = client.player.getOffHandStack().isOf(Items.FISHING_ROD);

            if (!mainHand && !offHand) {
                recastTimer = 0;
                return;
            }

            Hand activeHand = mainHand ? Hand.MAIN_HAND : Hand.OFF_HAND;
            var rodStack = mainHand ? client.player.getMainHandStack() : client.player.getOffHandStack();

            if (rodStack.getMaxDamage() - rodStack.getDamage() <= 1) {
                recastTimer = 0;
                return;
            }

            FishingBobberEntity bobber = client.player.fishHook;

            if (swapTimer > 0) {
                swapTimer--;
                if (swapTimer == 0) {
                    swapSlots(client, config.lureSlot - 1, config.reelSlot - 1);
                }
            }

            if (recastTimer > 0) {
                if (bobber != null && !bobber.isRemoved()){
                    recastTimer = 0;
                    return;
                }
                recastTimer--;
                if (recastTimer == 0) {
                    if (inGui(client)) {
                        recastTimer++;
                        return;
                    }
                    if (config.rodSwap){
                        client.player.getInventory().setSelectedSlot(config.reelSlot - 1);
                        swapSlots(client, config.lureSlot - 1, config.reelSlot - 1);
                        doRightClick(client, activeHand);
                        swapTimer = 5;
                    }
                    else
                        doRightClick(client, activeHand);
                }
                return;
            }

            if (bobber != null && !bobber.isRemoved()) {

                if (((FishingBobberEntityAccessor) bobber).getCaughtFish() && client.currentScreen == null) {
                    doRightClick(client, activeHand);
                    recastTimer = 20;
                }
            }
        });

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(literal("fishingbot")
                .then(literal("config")
                    .executes(context -> {
                        openGui = true;
                        return 1;
                    })
                )
            );
        });
    }

    private void swapSlots(MinecraftClient client, int slotA, int slotB) {
        int syncId = client.player.playerScreenHandler.syncId;
        client.interactionManager.clickSlot(syncId, slotB+35, 0, SlotActionType.PICKUP, client.player);
        client.interactionManager.clickSlot(syncId, slotA+35, 0, SlotActionType.PICKUP, client.player);
        client.interactionManager.clickSlot(syncId, slotB+35, 0, SlotActionType.PICKUP, client.player);
    }

    private void doRightClick(MinecraftClient client, Hand hand) {
        if (client.interactionManager == null || client.player == null) return;
        if (inGui(client)) return;
        client.interactionManager.interactItem(client.player, hand);
        client.player.swingHand(hand);
    }

    private boolean inGui(MinecraftClient client){
        return client.currentScreen != null && !(client.currentScreen instanceof ChatScreen);
    }
}
