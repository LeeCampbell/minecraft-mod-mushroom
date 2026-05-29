package com.leecampbell.mushroom.test;

import com.leecampbell.mushroom.ModItems;
import com.leecampbell.mushroom.MushroomMod;
import com.leecampbell.mushroom.MushroomPowerManager;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;

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

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    public void applyPowerAddsScaleModifier(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        MushroomPowerManager.applyPower(player);
        AttributeInstance scaleAttr = player.getAttribute(Attributes.SCALE);
        if (!scaleAttr.hasModifier(ResourceLocation.fromNamespaceAndPath("mushroom-mod", "power_mushroom_scale"))) {
            helper.fail("Expected scale modifier 'mushroom-mod:power_mushroom_scale' to be present after applyPower");
        }
        helper.succeed();
    }

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    public void removePowerClearsModifiers(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        MushroomPowerManager.applyPower(player);
        MushroomPowerManager.removePower(player);
        AttributeInstance scaleAttr = player.getAttribute(Attributes.SCALE);
        if (scaleAttr.hasModifier(ResourceLocation.fromNamespaceAndPath("mushroom-mod", "power_mushroom_scale"))) {
            helper.fail("Expected scale modifier 'mushroom-mod:power_mushroom_scale' to be absent after removePower");
        }
        helper.succeed();
    }

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    public void hasPowerReturnsCorrectState(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        if (MushroomPowerManager.hasPower(player)) {
            helper.fail("Expected hasPower to return false before applyPower");
        }
        MushroomPowerManager.applyPower(player);
        if (!MushroomPowerManager.hasPower(player)) {
            helper.fail("Expected hasPower to return true after applyPower");
        }
        helper.succeed();
    }

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    public void applyPowerDoesNotStack(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        MushroomPowerManager.applyPower(player);
        MushroomPowerManager.applyPower(player);
        AttributeInstance scaleAttr = player.getAttribute(Attributes.SCALE);
        double value = scaleAttr.getValue();
        if (value > 2.1) {
            helper.fail("Scale modifier is stacking: expected ~2.0 (base 1.0 * (1 + 1.0)) but got " + value);
        }
        helper.succeed();
    }

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    public void damagingPoweredPlayerRemovesPower(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        MushroomPowerManager.applyPower(player);
        if (!MushroomPowerManager.hasPower(player)) {
            helper.fail("Expected hasPower to be true before damage");
        }
        ServerLivingEntityEvents.AFTER_DAMAGE.invoker().afterDamage(player, helper.getLevel().damageSources().generic(), 5.0f, 5.0f, false);
        if (MushroomPowerManager.hasPower(player)) {
            helper.fail("Expected power to be removed after AFTER_DAMAGE event fires with damage > 0");
        }
        helper.succeed();
    }

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    public void damagingNonPoweredPlayerDoesNothing(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        if (MushroomPowerManager.hasPower(player)) {
            helper.fail("Expected hasPower to be false before damage");
        }
        ServerLivingEntityEvents.AFTER_DAMAGE.invoker().afterDamage(player, helper.getLevel().damageSources().generic(), 5.0f, 5.0f, false);
        if (MushroomPowerManager.hasPower(player)) {
            helper.fail("Expected hasPower to remain false after AFTER_DAMAGE event on non-powered player");
        }
        helper.succeed();
    }
}
