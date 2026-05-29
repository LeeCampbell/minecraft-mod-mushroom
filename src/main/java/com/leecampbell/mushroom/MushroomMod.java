package com.leecampbell.mushroom;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MushroomMod implements ModInitializer {

    public static final String MOD_ID = "mushroom-mod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModItems.initialize();

        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamageTaken, damageTaken, blocked) -> {
            if (entity instanceof ServerPlayer serverPlayer && baseDamageTaken > 0 && MushroomPowerManager.hasPower(serverPlayer)) {
                MushroomPowerManager.removePower(serverPlayer);
                serverPlayer.sendSystemMessage(Component.literal("Your mushroom power fades!"));
            }
        });

        LOGGER.info("Power Mushroom Mod initialized!");
    }
}
