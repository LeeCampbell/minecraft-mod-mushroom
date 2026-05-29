package com.leecampbell.mushroom;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MushroomMod implements ModInitializer {

    public static final String MOD_ID = "mushroom-mod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModItems.initialize();
        LOGGER.info("Power Mushroom Mod initialized!");
    }
}
