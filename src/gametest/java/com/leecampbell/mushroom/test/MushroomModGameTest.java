package com.leecampbell.mushroom.test;

import com.leecampbell.mushroom.ModItems;
import com.leecampbell.mushroom.MushroomMod;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class MushroomModGameTest implements FabricGameTest {

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    public void smokeTest(GameTestHelper helper) {
        helper.succeed();
    }

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    public void powerMushroomIsRegistered(GameTestHelper helper) {
        ResourceKey<net.minecraft.world.item.Item> key = ResourceKey.create(
                Registries.ITEM,
                ResourceLocation.fromNamespaceAndPath(MushroomMod.MOD_ID, "power_mushroom"));
        if (!BuiltInRegistries.ITEM.containsKey(key)) {
            helper.fail("ModItems.POWER_MUSHROOM is not registered in the item registry under key: "
                    + MushroomMod.MOD_ID + ":power_mushroom");
        }
        helper.succeed();
    }
}
