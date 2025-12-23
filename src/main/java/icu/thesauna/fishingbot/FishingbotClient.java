package icu.thesauna.fishingbot;

import icu.thesauna.fishingbot.config.FishingbotConfigScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import icu.thesauna.fishingbot.config.FishingbotConfig;
import icu.thesauna.fishingbot.mixin.FishingBobberEntityAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class FishingbotClient implements ClientModInitializer {
    public static Logger LOGGER = LoggerFactory.getLogger("Fishingbot");
    private boolean openGui = false;
    private int reelTimer = 0;
    private int recastTimer = -1;
    private int swapTimer = -1;
    private int useDelay = -1;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            FishingbotConfig config = FishingbotConfig.get();

            swapTimer = Math.max(swapTimer - 1, -1);
            recastTimer = Math.max(recastTimer - 1, -1);
            useDelay = Math.max(useDelay - 1, -1);

            if (openGui){
                client.setScreen(new FishingbotConfigScreen(client.currentScreen));
                openGui = false;
            }

            if (client.player == null || !config.enabled) return;

            if (swapTimer == 0 && config.rodSwap) {
                client.player.getInventory().setSelectedSlot(config.reelSlot - 1);
            }

            if (recastTimer == (config.castDelay - config.swapDelay) && config.rodSwap) {
                client.player.getInventory().setSelectedSlot(config.castSlot - 1);
            }

            boolean mainHand = client.player.getMainHandStack().isOf(Items.FISHING_ROD);
            boolean offHand = client.player.getOffHandStack().isOf(Items.FISHING_ROD);

            if (!mainHand && !offHand) {
                recastTimer = -1;
                return;
            }

            Hand activeHand = mainHand ? Hand.MAIN_HAND : Hand.OFF_HAND;
            ItemStack rodStack = mainHand ? client.player.getMainHandStack() : client.player.getOffHandStack();

            if (rodStack.getMaxDamage() - rodStack.getDamage() <= 1) {
                recastTimer = -1;
                return;
            }

            if (recastTimer == 0) {
                if (inGui(client)) return;
                if (config.rodSwap) swapTimer = config.swapDelay;;
                doRightClick(client, activeHand);
                reelTimer = 0;
            }

            FishingBobberEntity bobber = client.player.fishHook;
            if (bobber != null && !bobber.isRemoved() && useDelay < 0) {
                recastTimer = -1;
                if (((FishingBobberEntityAccessor) bobber).getCaughtFish()) {
                    if (reelTimer < config.reelDelay) {
                        reelTimer++;
                        return;
                    }
                    if (inGui(client)) return;
                    doRightClick(client, activeHand);
                    recastTimer = config.castDelay;
                    useDelay = 5;
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

//    Can add a toggle / feature to use hotbar slot method or item method for attribute swap
//    private void swapSlots(MinecraftClient client, int slotA, int slotB) {
//        int syncId = client.player.playerScreenHandler.syncId;
//        client.interactionManager.clickSlot(syncId, slotB+35, 0, SlotActionType.PICKUP, client.player);
//        client.interactionManager.clickSlot(syncId, slotA+35, 0, SlotActionType.PICKUP, client.player);
//        client.interactionManager.clickSlot(syncId, slotB+35, 0, SlotActionType.PICKUP, client.player);
//    }

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
